package com.stopstone.whathelook.view.custom.gallery

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.stopstone.whathelook.databinding.ActivityCustomGalleryBinding
import com.stopstone.whathelook.view.custom.gallery.adapter.CustomGalleryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomGalleryActivity : AppCompatActivity() {
    private val binding: ActivityCustomGalleryBinding by lazy {
        ActivityCustomGalleryBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: CustomGalleryViewModel by viewModels()
    private val adapter: CustomGalleryAdapter by lazy { CustomGalleryAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        checkAndRequestPermission()

    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "갤러리"
    }

    private fun setupRecyclerView() {
        binding.rvGallery.adapter = adapter
        binding.rvGallery.layoutManager = GridLayoutManager(this, 3)

        adapter.setOnImageClickListener { uri ->
            viewModel.toggleImageSelection(uri)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.images.collect { images ->
                adapter.submitList(images)
            }
        }

        lifecycleScope.launch {
            viewModel.selectedImages.collect { selectedImages ->
                adapter.updateSelectedImages(selectedImages)
                updateUI(selectedImages)
            }
        }


        binding.btnDone.setOnClickListener {
            setResult(RESULT_OK, Intent().putExtra(SELECTED_IMAGES, ArrayList(viewModel.selectedImages.value)))
            finish()
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateUI(selectedImages: Set<Uri>) {
        binding.btnDone.isEnabled = selectedImages.isNotEmpty()
        binding.tvSelectedCount.text = "${selectedImages.size}/$MAX_IMAGES"

        if (selectedImages.size >= MAX_IMAGES) {
            showMaxImagesReachedMessage()
        }
    }

    private fun showMaxImagesReachedMessage() {
        Snackbar.make(binding.root, "최대 ${MAX_IMAGES}장까지만 선택할 수 있습니다.", Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.btnDone)
            .show()
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun shouldShowRationale(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun checkAndRequestPermission() {
        when {
            hasPermission() -> {
                viewModel.loadImages(contentResolver)
            }
            shouldShowRationale() -> {
                showPermissionRationaleDialog()
            }
            else -> {
                requestPermission()
            }
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.loadImages(contentResolver)
            } else {
                if (!shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                    showPermissionDeniedDialog()
                } else {
                    showPermissionRationaleDialog()
                }
            }
        }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setMessage("갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("확인") { _, _ ->
                requestPermissionLauncher.launch(
                    READ_EXTERNAL_STORAGE
                )
            }
            .setNegativeButton("취소") { _, _ -> finish() }
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setMessage("갤러리 접근 권한이 거부되었습니다. 설정에서 권한을 허용해주세요.")
            .setPositiveButton("설정으로 이동") { _, _ ->
                startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
                finish()
            }
            .setNegativeButton("취소") { _, _ -> finish() }
            .show()
    }

    companion object {
        const val MAX_IMAGES = 5
        const val SELECTED_IMAGES = "selected_images"
    }
}