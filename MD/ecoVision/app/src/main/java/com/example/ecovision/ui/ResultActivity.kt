package com.example.ecovision.ui

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ecovision.data.PlasticType
import com.example.ecovision.data.local.HistoryEntity
import com.example.ecovision.data.local.HistoryRepository
import com.example.ecovision.databinding.ActivityResultBinding
import com.example.ecovision.databinding.BottomSheetResultBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var bottomSheetBinding: BottomSheetResultBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var historyRepository: HistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Hasil Scan"

        historyRepository = HistoryRepository(this)

        val plasticType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_PLASTIC_TYPE, PlasticType::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_PLASTIC_TYPE)
        }

        val date = intent.getStringExtra(EXTRA_DATE)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: "limbah plastik"

        if (plasticType != null) {
            binding.plasticTypeValueTextView.text = plasticType.name
            binding.recyclingTipsValueTextView.text = plasticType.recyclingProcess
            binding.recyclingTipsValueTextView2.text = plasticType.recyclingProcessTwo
        }

        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (imageUri != null) {
            binding.resultImageView.setImageURI(Uri.parse(imageUri))
        }

        if (date != null) {
            binding.tvDate.text = date
        }

        bottomSheetBinding = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetBinding.root)

        bottomSheetBinding.learnMoreButton.setOnClickListener {
            val plasticTypeDetail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_PLASTIC_TYPE, PlasticType::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_PLASTIC_TYPE)
            }
            if (plasticTypeDetail != null) {
                val intent = Intent(this, DetailPlasticActivity::class.java)
                intent.putExtra("plastic_type", plasticTypeDetail)
                startActivity(intent)
            }
        }

        bottomSheetBinding.mapButton.setOnClickListener {
            if (isLocationEnabled()) {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            } else {
                showLocationEnableDialog()
            }
        }

        bottomSheetBinding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        binding.fabScanAgain.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
            finish()
        }

        saveToHistory(date, imageUri, description, plasticType?.name ?: "Other")
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showLocationEnableDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable Location")
            .setMessage("Please enable location to use the map feature.")
            .setPositiveButton("Enable") { dialog, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun saveToHistory(date: String?, imageUri: String?, description: String, category: String) {
        val historyItem = HistoryEntity(date = date ?: "", imageUri = imageUri ?: "", description = description, category = category)
        lifecycleScope.launch(Dispatchers.IO) {
            historyRepository.addHistoryItem(historyItem)
        }
    }

    companion object {
        const val EXTRA_DESCRIPTION = "extra_description"
        const val CODE_RESULT = "code_result"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_PLASTIC_TYPE = "extra_plastic_type"
        const val EXTRA_DATE = "extra_date"
    }
}