package com.example.ecovision.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecovision.R
import com.example.ecovision.data.PlasticType

class PlasticTypeAdapter(
    private val plasticTypes: List<PlasticType>,
    private val onClick: (PlasticType) -> Unit
) : RecyclerView.Adapter<PlasticTypeAdapter.PlasticTypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlasticTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plastic_type, parent, false)
        return PlasticTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlasticTypeViewHolder, position: Int) {
        holder.bind(plasticTypes[position])
    }

    override fun getItemCount() = plasticTypes.size

    inner class PlasticTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewPlastic)
        private val textView: TextView = itemView.findViewById(R.id.textViewPlasticName)
        private val imageViewSymbol: ImageView = itemView.findViewById(R.id.imageViewPlasticSymbol)

        fun bind(plasticType: PlasticType) {
            imageView.setImageResource(plasticType.imageResId)
            textView.text = plasticType.name
            imageViewSymbol.setImageResource(plasticType.imageIcon) // Set actual symbol resource
            itemView.setOnClickListener { onClick(plasticType) }
        }
    }
}
