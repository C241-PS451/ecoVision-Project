package com.example.ecovision.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.ecovision.data.PlasticData
import com.example.ecovision.data.retrofit.PredictionResponse
import com.example.ecovision.data.retrofit.RetrofitClientInstance
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.example.ecovision.databinding.ActivityScanBinding
import com.example.ecovision.detection.BoundingBox
import com.example.ecovision.detection.Detector
import com.example.ecovision.util.Constants
import com.example.ecovision.util.UploadScanUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ScanActivity : AppCompatActivity(), Detector.DetectorListener {

    private lateinit var binding: ActivityScanBinding
    private val isFrontCamera = false

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var detector: Detector
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                processImageUri(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uploadButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        val decorView = window.decorView
        @Suppress("DEPRECATION") val windowInsetsController =
            ViewCompat.getWindowInsetsController(decorView)

        windowInsetsController?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        detector = Detector(baseContext, Constants.MODEL_PATH, Constants.LABELS_PATH, this)
        detector.setup()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    fun onBoundingBoxClicked(description: String, codeResult: Int) {
        binding.progressBar.visibility = View.VISIBLE
        binding.blurOverlay.visibility = View.VISIBLE
        captureImage(description, codeResult)
    }

    private fun captureImage(description: String?, codeResult: Int) {
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            File(
                externalMediaDirs.first(),
                "${System.currentTimeMillis()}.jpg"
            )
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri
                    if (savedUri != null) {
                        val plasticType = PlasticData.plasticTypes.find { it.name == description }
                        val currentDate =
                            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
                        val intent = Intent(this@ScanActivity, ResultActivity::class.java).apply {
                            putExtra(
                                ResultActivity.EXTRA_DESCRIPTION,
                                description ?: "limbah plastik"
                            )
                            putExtra(ResultActivity.CODE_RESULT, codeResult)
                            putExtra(ResultActivity.EXTRA_IMAGE_URI, savedUri.toString())
                            putExtra(ResultActivity.EXTRA_PLASTIC_TYPE, plasticType)
                            putExtra(ResultActivity.EXTRA_DATE, currentDate)
                        }
                        startActivity(intent)
                    }
                    binding.progressBar.visibility = View.GONE
                    binding.blurOverlay.visibility = View.GONE
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Image capture failed: ${exception.message}", exception)
                    Toast.makeText(
                        this@ScanActivity,
                        "Image capture failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visibility = View.GONE
                    binding.blurOverlay.visibility = View.GONE
                }
            }
        )
    }

    @Suppress("DEPRECATION")
    private fun bindCameraUseCases() {
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val rotation = binding.viewFinder.display.rotation

        val cameraSelector = CameraSelector
            .Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .build()

        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
            try {
                Log.d(TAG, "Image acquired: ${imageProxy.imageInfo.timestamp}")
                val bitmapBuffer = Bitmap.createBitmap(
                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888
                )
                bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)

                val matrix = Matrix().apply {
                    postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

                    if (isFrontCamera) {
                        postScale(-1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat())
                    }
                }

                val rotatedBitmap = Bitmap.createBitmap(
                    bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height, matrix, true
                )

                detector.detect(rotatedBitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing image: ${e.message}", e)
            } finally {
                Log.d(TAG, "Closing imageProxy: ${imageProxy.imageInfo.timestamp}")
                imageProxy.close()
            }
        }



        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalyzer
            )

            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
            Toast.makeText(
                this@ScanActivity,
                "Use case binding failed: ${exc.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun processImageUri(uri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        binding.blurOverlay.visibility = View.VISIBLE
        stopCamera()
        lifecycleScope.launch(Dispatchers.IO) {
            val compressedFile = UploadScanUtils.compressImage(this@ScanActivity, uri)

            val requestFile =
                compressedFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", compressedFile.name, requestFile)

            val service = RetrofitClientInstance.apiService
            val call = service.predict(body)

            call.enqueue(object : Callback<PredictionResponse> {
                override fun onResponse(
                    call: Call<PredictionResponse>,
                    response: Response<PredictionResponse>
                ) {
                    if (response.isSuccessful) {
                        val predictionResponse = response.body()
                        val imageUrl = predictionResponse?.imageUrl
                        val predictedClass = predictionResponse?.predictedClass
                        val currentDate =
                            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())

                        Log.d(TAG, "Prediction response: $predictionResponse")

                        val plasticType =
                            PlasticData.plasticTypes.find { it.name == predictedClass }

                        lifecycleScope.launch(Dispatchers.Main) {
                            val intent =
                                Intent(this@ScanActivity, ResultActivity::class.java).apply {
                                    putExtra(ResultActivity.EXTRA_DESCRIPTION, predictedClass)
                                    putExtra(ResultActivity.EXTRA_IMAGE_URI, imageUrl)
                                    putExtra(ResultActivity.EXTRA_PLASTIC_TYPE, plasticType)
                                    putExtra(ResultActivity.EXTRA_DATE, currentDate)
                                }
                            startActivity(intent)
                        }
                    } else {
                        Log.e(TAG, "Prediction request failed: ${response.errorBody()?.string()}")
                        Toast.makeText(
                            this@ScanActivity,
                            "Prediction request failed: ${response.errorBody()?.string()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.progressBar.visibility = View.GONE
                    binding.blurOverlay.visibility = View.GONE
                }

                override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                    Log.e(TAG, "Prediction request failed: ${t.message}")
                    Toast.makeText(
                        this@ScanActivity,
                        "Prediction request failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visibility = View.GONE
                    binding.blurOverlay.visibility = View.GONE
                }
            })
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.CAMERA] == true) {
                startCamera()
            }
        }

    private fun stopCamera() {
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
    }

    override fun onPause() {
        super.onPause()
        stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.clear()
        stopCamera()
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    override fun onEmptyDetect() {
        binding.overlay.invalidate()
    }

    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        runOnUiThread {
            binding.inferenceTime.text = "${inferenceTime}ms"
            binding.overlay.apply {
                setResults(boundingBoxes)
                invalidate()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressedDispatcher.onBackPressed()
        detector.clear()
        finish()
    }

    companion object {
        private const val TAG = "Camera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}