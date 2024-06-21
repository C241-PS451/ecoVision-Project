package com.example.ecovision.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ecovision.data.PlasticType
import com.example.ecovision.databinding.ActivityDetailPlasticBinding

class DetailPlasticActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPlasticBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPlasticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Jenis Plastik"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val plasticType: PlasticType? = intent.getParcelableExtra("plastic_type")

        plasticType?.let {
            binding.imageViewPlasticDetail.setImageResource(it.imageResId)
            binding.textViewPlasticNameDetail.text = it.name
            binding.imageViewPlasticSymbol.setImageResource(it.imageIcon)
            binding.textViewDescriptionDetail.text = it.description
            binding.textViewRecyclingProcessDescription.text = it.recyclingProcess
            binding.textViewEnvironmentalImpactDescription.text = it.environmentalImpact

            val imageList = ArrayList<SlideModel>()

            it.examples.forEach { imageResId ->
                imageList.add(SlideModel(imageResId, ScaleTypes.CENTER_CROP))
            }

            binding.imageSlideshow.setImageList(imageList, ScaleTypes.CENTER_CROP)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}