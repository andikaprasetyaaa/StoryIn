package com.example.storyin.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.storyin.R
import com.example.storyin.data.preferences.UserPreference
import com.example.storyin.data.remote.api.ApiConfig
import com.example.storyin.data.repository.UploadStoryRepository
import com.example.storyin.domain.result.UploadResult
import com.example.storyin.ui.viewmodel.UploadStoryViewModel
import com.example.storyin.ui.viewmodel.factory.UploadStoryViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@Suppress("SameParameterValue", "DEPRECATION")
class UploadFragment : Fragment(R.layout.fragment_upload) {

    private lateinit var progressBar: ProgressBar
    private lateinit var userPreference: UserPreference
    private lateinit var viewModel: UploadStoryViewModel
    private lateinit var previewImageStory: ImageView
    private lateinit var descriptionEditText: TextInputEditText
    private var currentImageUri: Uri? = null
    private var currentBitmap: Bitmap? = null
    private lateinit var uploadButton: MaterialButton

    companion object {
        private const val KEY_CURRENT_IMAGE_URI = "current_image_uri"
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraResultLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else {
            showToast(getString(R.string.camera_permission_required))
        }
    }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                val compressedBitmap = compressBitmapToMaxSize(imageBitmap, 1_000_000)
                previewImageStory.setImageBitmap(compressedBitmap)
                currentBitmap = compressedBitmap
                viewModel.setSelectedImage(compressedBitmap)
            }
        }

    private val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                currentImageUri = it
                Glide.with(requireContext())
                    .asBitmap()
                    .load(it)
                    .override(1000, 1000)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val compressedBitmap = compressBitmapToMaxSize(resource, 1_000_000)
                            previewImageStory.setImageBitmap(compressedBitmap)
                            currentBitmap = compressedBitmap
                            viewModel.setSelectedImage(compressedBitmap)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeView(view)
        initializeViewModel()
        setupObservers()
        setupButtonListeners(view)

        savedInstanceState?.let {
            currentBitmap = it.getParcelable("currentBitmap")
            currentBitmap?.let { bitmap ->
                previewImageStory.setImageBitmap(bitmap)
                viewModel.setSelectedImage(bitmap)
            }
            descriptionEditText.setText(it.getString("descriptionText"))

            currentImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(KEY_CURRENT_IMAGE_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable(KEY_CURRENT_IMAGE_URI)
            }

            currentImageUri?.let { uri ->
                previewImageStory.setImageURI(uri)
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedImage.observe(viewLifecycleOwner) { bitmap ->
            bitmap?.let {
                previewImageStory.setImageBitmap(it)
            }
        }
    }

    private fun setupButtonListeners(view: View) {
        view.findViewById<MaterialButton>(R.id.button_add).setOnClickListener {
            uploadStory()
        }

        view.findViewById<MaterialButton>(R.id.btnCamera).setOnClickListener {
            openCamera()
        }

        view.findViewById<MaterialButton>(R.id.btnGallery).setOnClickListener {
            openGallery()
        }
    }

    private fun initializeView(view: View) {
        progressBar = view.findViewById(R.id.progressBarLoading)
        previewImageStory = view.findViewById(R.id.preViewImageStory)
        descriptionEditText = view.findViewById(R.id.ed_add_description)
        uploadButton = view.findViewById(R.id.button_add)
        progressBar.visibility = View.GONE
        userPreference = UserPreference.getInstance(requireContext())
    }

    private fun initializeViewModel() {
        val repository = UploadStoryRepository.getInstance(ApiConfig.getApiService())
        val factory = UploadStoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[UploadStoryViewModel::class.java]

        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            handleUploadResult(result)
        }
    }

    private fun handleUploadResult(result: UploadResult) {
        when (result) {
            is UploadResult.Success -> {
                progressBar.visibility = View.GONE
                showToast(getString(R.string.upload_successful))
                resetUploadForm()
            }

            is UploadResult.Error -> {
                progressBar.visibility = View.GONE
                showToast(getString(R.string.upload_failed, result.exception.message))
            }

            is UploadResult.Loading -> {
                progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun uploadStory() {
        val description = descriptionEditText.text.toString().trim()
        if (description.isEmpty() || previewImageStory.drawable == null) {
            showToast(getString(R.string.description_and_image_required))
            return
        }

        lifecycleScope.launch {
            try {
                val token = userPreference.getToken().first()
                val descriptionBody = description.toRequestBody("text/plain".toMediaType())
                val imagePart = createImagePartFromImageView(previewImageStory)
                viewModel.uploadStory("Bearer $token", descriptionBody, imagePart)
            } catch (e: Exception) {
                showToast(getString(R.string.upload_failed, e.message))
            }
        }
    }

    private fun createImagePartFromImageView(imageView: ImageView): MultipartBody.Part {
        val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap
            ?: throw IllegalStateException(getString(R.string.no_image_selected))
        val compressedBitmap = compressBitmapToMaxSize(bitmap, 1_000_000)
        val file = File(requireContext().cacheDir, "uploaded_image.jpg")
        FileOutputStream(file).use { outputStream ->
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        }
        return MultipartBody.Part.createFormData(
            "photo",
            file.name,
            file.asRequestBody("image/jpeg".toMediaType())
        )
    }

    private fun compressBitmapToMaxSize(bitmap: Bitmap, maxSize: Int): Bitmap {
        var quality = 100
        var compressedBitmap = bitmap

        while (true) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            if (byteArrayOutputStream.size() <= maxSize || quality <= 5) break
            quality -= 5
            compressedBitmap = BitmapFactory.decodeByteArray(
                byteArrayOutputStream.toByteArray(),
                0,
                byteArrayOutputStream.size()
            )
        }
        return compressedBitmap
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            cameraResultLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openGallery() {
        galleryResultLauncher.launch("image/*")
    }

    private fun resetUploadForm() {
        descriptionEditText.text?.clear()
        previewImageStory.setImageDrawable(null)
        currentImageUri = null
        currentBitmap = null
        uploadButton.isEnabled = true
        viewModel.clearSelectedImage()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentBitmap?.let { outState.putParcelable("currentBitmap", it) }
        outState.putString("descriptionText", descriptionEditText.text.toString())
        outState.putParcelable(KEY_CURRENT_IMAGE_URI, currentImageUri)
    }
}