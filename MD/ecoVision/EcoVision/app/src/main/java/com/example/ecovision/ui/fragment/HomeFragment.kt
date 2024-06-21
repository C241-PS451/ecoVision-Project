package com.example.ecovision.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecovision.R
import com.example.ecovision.adapter.HistoryAdapter
import com.example.ecovision.data.TipsData
import com.example.ecovision.data.local.HistoryEntity
import com.example.ecovision.data.local.HistoryRepository
import com.example.ecovision.databinding.FragmentHomeBinding
import com.example.ecovision.ui.activity.DetailHistoryActivity
import com.example.ecovision.ui.activity.GuideActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var tipsData: TipsData
    private lateinit var auth: FirebaseAuth
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyAdapter: HistoryAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        val displayName = firebaseUser?.displayName ?: "User"

        binding.textViewTitle.text = getString(R.string.welcome_sign, displayName)

        binding.imageViewQuestionMark.setOnClickListener {
            val intent = Intent(requireContext(), GuideActivity::class.java)
            startActivity(intent)
        }

        tipsData = TipsData()
        val tipOfTheDay = tipsData.getTipForToday()
        binding.textViewTip.text = tipOfTheDay

        historyRepository = HistoryRepository(requireContext())

        val recyclerView = binding.rvRecentHistory
        recyclerView.layoutManager = LinearLayoutManager(context)
        historyAdapter = HistoryAdapter(
            emptyList(),
            ::onDeleteHistory,
            ::onChangeDescription,
            ::onHistoryItemClick
        )
        recyclerView.adapter = historyAdapter

        loadHistory()
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun loadHistory() {
        lifecycleScope.launch(Dispatchers.IO) {
            val historyList = historyRepository.getLimitedHistoryItems(3)
            val totalScans = historyRepository.getAllHistoryItems().size
            withContext(Dispatchers.Main) {
                historyAdapter.updateData(historyList)
                updateScanCount(totalScans)
                updateVisibility(totalScans)
            }
        }
    }

    private fun updateScanCount(totalScans: Int) {
        binding.textViewProgress.text = if (totalScans > 0) {
            getString(R.string.amount_scanned, totalScans)
        } else {
            getString(R.string.no_scans_yet)
        }
    }

    private fun updateVisibility(totalScans: Int) {
        if (totalScans > 0) {
            binding.textViewScannedTitle.visibility = View.VISIBLE
            binding.rvRecentHistory.visibility = View.VISIBLE
        } else {
            binding.textViewScannedTitle.visibility = View.GONE
            binding.rvRecentHistory.visibility = View.GONE
        }
    }

    private fun onDeleteHistory(historyItem: HistoryEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            historyRepository.deleteHistoryItem(historyItem)
            val updatedList = historyRepository.getLimitedHistoryItems(3)
            withContext(Dispatchers.Main) {
                historyAdapter.updateData(updatedList)
            }
        }
    }

    private fun onChangeDescription(historyItem: HistoryEntity, newDescription: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            historyRepository.updateHistoryDescription(historyItem, newDescription)
            val updatedList = historyRepository.getLimitedHistoryItems(3)
            withContext(Dispatchers.Main) {
                historyAdapter.updateData(updatedList)
            }
        }
    }

    private fun onHistoryItemClick(historyItem: HistoryEntity) {
        val intent = Intent(requireContext(), DetailHistoryActivity::class.java).apply {
            putExtra(DetailHistoryActivity.EXTRA_DESCRIPTION, historyItem.description)
            putExtra(DetailHistoryActivity.EXTRA_IMAGE_URI, historyItem.imageUri)
            putExtra(DetailHistoryActivity.EXTRA_PLASTIC_TYPE, historyItem.category)
            putExtra(DetailHistoryActivity.EXTRA_DATE, historyItem.date)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}