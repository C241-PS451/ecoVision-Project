package com.example.ecovision.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.ecovision.data.PlasticData
import com.example.ecovision.databinding.ActivityDetailHistoryBinding
import com.example.ecovision.databinding.BottomSheetResultBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File
import java.io.FileOutputStream

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    private lateinit var bottomSheetBinding: BottomSheetResultBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val plasticType = intent.getStringExtra(EXTRA_PLASTIC_TYPE)
        val date = intent.getStringExtra(EXTRA_DATE)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)

        supportActionBar?.title = description

        if (plasticType != null) {
            val plasticData = PlasticData.plasticTypes.find { it.name == plasticType }
            if (plasticData != null) {
                binding.plasticTypeValueTextView.text = plasticData.name
                binding.recyclingTipsValueTextView.text = plasticData.recyclingProcess
                binding.recyclingTipsValueTextView2.text = plasticData.recyclingProcessTwo
            }
        } else {
            binding.plasticTypeValueTextView.text = description
            binding.recyclingTipsValueTextView.text = ""
            binding.recyclingTipsValueTextView2.text = ""
        }

        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (imageUri != null) {
            Glide.with(this)
                .load(imageUri)
                .into(binding.resultImageView)
        }

        binding.tvDate.text = date

        bottomSheetBinding = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetBinding.root)

        bottomSheetBinding.learnMoreButton.setOnClickListener {
            if (plasticType != null) {
                val plasticData = PlasticData.plasticTypes.find { it.name == plasticType }
                if (plasticData != null) {
                    val intent = Intent(this, DetailPlasticActivity::class.java)
                    intent.putExtra("plastic_type", plasticData)
                    startActivity(intent)
                }
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

        binding.shareButton.setOnClickListener {
            shareScreenshot()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
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

    private fun shareScreenshot() {
        val rootView = window.decorView.findViewById<View>(android.R.id.content)
        val screenshot = getScreenshot(rootView)

        val screenshotUri = saveScreenshot(screenshot)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
        startActivity(Intent.createChooser(shareIntent, "Share Screenshot"))
    }

    private fun getScreenshot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun saveScreenshot(bitmap: Bitmap): Uri {
        val filename = "screenshot_${System.currentTimeMillis()}.jpg"
        val file = File(getExternalFilesDir(null), filename)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
    }

    companion object {
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_PLASTIC_TYPE = "extra_plastic_type"
        const val EXTRA_DATE = "extra_date"
    }
}