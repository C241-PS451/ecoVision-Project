package com.example.ecovision.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecovision.data.PlasticData
import com.example.ecovision.adapter.PlasticTypeAdapter
import com.example.ecovision.R
import com.example.ecovision.ui.activity.DetailPlasticActivity

class ArticleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewPlasticTypes)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = PlasticTypeAdapter(PlasticData.plasticTypes) { plasticType ->
            val intent = Intent(context, DetailPlasticActivity::class.java)
            intent.putExtra("plastic_type", plasticType)
            startActivity(intent)
        }
    }
}
