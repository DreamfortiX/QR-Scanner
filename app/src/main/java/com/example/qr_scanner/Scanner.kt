package com.example.qr_scanner

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.TorchState
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Scanner : Fragment() {
    private lateinit var previewView: PreviewView
    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var viewfinder: View
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeAnalyzer: ImageAnalyzer
    private lateinit var cameraSelector: CameraSelector
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null
    private lateinit var topIconLayout:LinearLayout

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 200
        private const val REQUEST_GALLERY = 201
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)
        previewView = view.findViewById(R.id.previewView)
        viewfinder = view.findViewById(R.id.viewfinder)
        // Find buttons
        val galleryIcon = view.findViewById<View>(R.id.galleryIcon)
        val flashIcon = view.findViewById<View>(R.id.flashIcon)
        val cameraSwitchIcon = view.findViewById<View>(R.id.cameraSwitchIcon)
        topIconLayout = view.findViewById(R.id.topIconsLayout)

        cameraExecutor = Executors.newSingleThreadExecutor()
        checkCameraPermission()

        // Set touch listener for focusing
        previewView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val centerX = event.x
                val centerY = event.y
                focusOnPoint(centerX, centerY)
            }
            true
        }
        // Gallery button click listener
        galleryIcon.setOnClickListener {
            openGallery()
        }

        // Flash button click listener
        flashIcon.setOnClickListener {
            toggleFlash(flashIcon)
        }

        // Camera switch button click listener
        cameraSwitchIcon.setOnClickListener {
            switchCamera()
        }

        return view

    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    // Toggle the flash on and off
    private fun toggleFlash(flashIcon: View) {
        val flashEnabled = cameraInfo?.torchState?.value == TorchState.ON
        cameraControl?.enableTorch(!flashEnabled)
        if (flashEnabled) {
            flashIcon.setBackgroundResource(R.drawable.baseline_flash_off_24)
        } else {
            flashIcon.setBackgroundResource(R.drawable.baseline_flash_on_24)
        }
    }
    // Switch between front and back cameras
    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera() // Restart the camera with the new camera selector
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            previewView.visibility = View.VISIBLE
            viewfinder.visibility = View.VISIBLE
            startCamera()

        }
    }

    private fun startCamera() {
        Log.d("CameraX", "Starting camera")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            Log.d("CameraX", "Camera provider available")
            bindCameraUseCases(cameraProvider)
            topIconLayout.visibility = View.VISIBLE
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        // Create and assign the ImageAnalyzer to the class property
        barcodeAnalyzer = ImageAnalyzer()

        imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, barcodeAnalyzer)
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
        } catch (exc: Exception) {
            Log.e("CameraX", "Use case binding failed", exc)
        }
    }

    private fun focusOnPoint(centerX: Float, centerY: Float) {
        val point = previewView.meteringPointFactory.createPoint(centerX / previewView.width, centerY / previewView.height)

        val focusAction = FocusMeteringAction.Builder(point).build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val camera = cameraProvider.bindToLifecycle(this@Scanner, CameraSelector.DEFAULT_BACK_CAMERA)
            camera.cameraControl.startFocusAndMetering(focusAction).addListener({
                // Start scanning after 10 seconds if the focus is successful
                Handler().postDelayed({
                    // Enable scanning
                    barcodeAnalyzer.setScanningEnabled(true)
                }, 5000) // 5 seconds delay
            }, ContextCompat.getMainExecutor(requireContext()))
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private inner class ImageAnalyzer : ImageAnalysis.Analyzer {
        private var lastScannedValue: String? = null
        private var scanningEnabled = false

        fun setScanningEnabled(enabled: Boolean) {
            scanningEnabled = enabled
        }

        override fun analyze(image: ImageProxy) {
            if (!scanningEnabled) {
                image.close() // Close the image immediately if scanning is not enabled
                return
            }

            @OptIn(ExperimentalGetImage::class)
            val mediaImage = image.image
            if (mediaImage != null) {
                val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                val scanner = BarcodeScanning.getClient()

                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            val barcode = barcodes.first()
                            val rawValue = barcode.rawValue
                            if (rawValue != null && rawValue != lastScannedValue) {
                                lastScannedValue = rawValue
                                Toast.makeText(requireContext(), "Scanned: $rawValue", Toast.LENGTH_SHORT).show()
                                handleScannedData(rawValue)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("BarcodeScanner", "Barcode scanning failed", e)
                    }
                    .addOnCompleteListener {
                        image.close() // Close the image proxy to prevent memory leaks
                    }
            }
        }

        private fun handleScannedData(rawValue: String) {
            val resultFragment = Result()
            val bundle = Bundle().apply {
                putString("scannedData", rawValue)
            }
            resultFragment.arguments = bundle
            if (parentFragmentManager.findFragmentById(R.id.fragment_container) !is Result) {
                previewView.visibility = View.INVISIBLE
                viewfinder.visibility = View.INVISIBLE
                topIconLayout.visibility = View.INVISIBLE

                parentFragmentManager.beginTransaction()
                    .replace(R.id.Scanner_Container, resultFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown() // Shutdown the executor when the fragment is destroyed
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera() // Start the camera if permission granted
            } else {
                Log.e("CameraPermission", "Camera permission denied")
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            val selectedImage: Uri? = data?.data
            if (selectedImage != null) {
                // Handle selected image from the gallery
                Toast.makeText(requireContext(), "Image selected: $selectedImage", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
