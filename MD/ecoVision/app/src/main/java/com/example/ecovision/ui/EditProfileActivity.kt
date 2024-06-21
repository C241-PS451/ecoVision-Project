package com.example.ecovision.ui

import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Calendar
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.example.ecovision.R
import com.example.ecovision.databinding.ActivityEditProfileBinding
import com.example.ecovision.util.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var selectedPhotoUri: Uri? = null

    private var isPhotoChanged = false

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = uri
            Glide.with(this).load(uri).into(binding.profilePictureEdit)
            isPhotoChanged = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Edit Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        val isFirebaseUser = currentUser?.providerData?.find { it.providerId == "password" } != null
        val displayName = currentUser?.displayName ?: "not set"
        val email = currentUser?.email
        val photoUrl = intent.getStringExtra("photoUrl")
        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).into(binding.profilePictureEdit)
        } else {
            binding.profilePictureEdit.setImageResource(R.drawable.ic_profile)
        }

        val fullName = intent.getStringExtra("fullName") ?: "not set"
        val birthday = intent.getStringExtra("birthday") ?: "not set"
        val location = intent.getStringExtra("location") ?: "not set"

        // Set initial data
        binding.fullNameEdit.setText(fullName)
        binding.emailEdit.setText(email)
        binding.birthdayEdit.setText(birthday)
        binding.locationEdit.setText(location)

        if (isFirebaseUser) {
            binding.changePictureButton.visibility = View.VISIBLE
            binding.profilePictureEdit.setImageResource(R.drawable.ic_profile) // Default profile picture
        } else {
            binding.changePictureButton.visibility = View.GONE
            Glide.with(this).load(photoUrl).into(binding.profilePictureEdit)
        }

        binding.changePictureButton.setOnClickListener {
            checkPermission()
        }

        binding.birthdayEdit.setOnClickListener {
            showDatePickerDialog()
        }

        binding.saveButton.setOnClickListener {
            saveProfile()
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

    private fun openGallery() {
        pickImage.launch("image/*")
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            binding.birthdayEdit.setText("${selectedDay}/${selectedMonth + 1}/${selectedYear}")
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13 and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_CODE_STORAGE_PERMISSION
                )
            } else {
                openGallery()
            }
        } else {  // Below Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE_PERMISSION
                )
            } else {
                openGallery()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProfile() {
        val fullName = binding.fullNameEdit.text.toString()
        val birthday = binding.birthdayEdit.text.toString()
        val location = binding.locationEdit.text.toString()

        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val userProfile = hashMapOf(
                "fullName" to fullName,
                "birthday" to birthday,
                "location" to location
            )

            if (isPhotoChanged && selectedPhotoUri != null) {
                val bitmap = ImageUtils.getBitmapFromUri(this, selectedPhotoUri!!)
                val resizedBitmap = ImageUtils.resizeBitmap(bitmap, 500)
                val compressedData = ImageUtils.compressBitmap(resizedBitmap, 80)

                val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$userId.jpg")
                val uploadTask = storageRef.putBytes(compressedData)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        userProfile["photoUrl"] = uri.toString()
                        saveUserProfile(userId, userProfile)
                        Glide.with(this).load(uri).into(binding.profilePictureEdit)
                    }
                }.addOnFailureListener { e ->
                    Log.e("EditProfileActivity", "Failed to upload profile picture: ${e.message}")
                    Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                saveUserProfile(userId, userProfile)
            }
        } else {
            Log.e("EditProfileActivity", "User ID is null")
            Toast.makeText(this, "Failed to update profile: User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserProfile(userId: String, userProfile: HashMap<String, String>) {
        db.collection("users").document(userId)
            .set(userProfile, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("EditProfileActivity", "Profile updated successfully")
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent().apply {
                    putExtra("photoUrl", userProfile["photoUrl"])
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("EditProfileActivity", "Failed to update profile: ${e.message}")
                Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1
    }
}