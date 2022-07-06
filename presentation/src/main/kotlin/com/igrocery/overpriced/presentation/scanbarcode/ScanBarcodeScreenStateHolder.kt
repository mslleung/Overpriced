package com.igrocery.overpriced.presentation.scanbarcode

import android.util.Size
import androidx.activity.ComponentActivity
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.*
import androidx.camera.core.AspectRatio.RATIO_16_9
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.igrocery.overpriced.shared.Logger
import java.util.concurrent.Executors

@Suppress("unused")
private val log = Logger { }

class ScanBarcodeScreenStateHolder {

    val preview = Preview.Builder()
        .setTargetAspectRatio(RATIO_16_9)
        .build()

    private val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
        .setTargetResolution(Size(100000, 100000))  // tells camerax to use the maximum resolution
        .build()

    init {
        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor())
        @ExperimentalGetImage
        { imageProxy ->
            log.error("${imageProxy.width},${imageProxy.height}")
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val scanner = BarcodeScanning.getClient(options)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            // only care about one
                            for (barcode in barcodes) {
                                val barcodeString = barcode.rawValue
                                if (barcodeString != null && barcodeString.isDigitsOnly()) {
                                    scannedBarcode = barcodeString
                                    break
                                }
                            }
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }

    private val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    private var camera: Camera? = null

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    var scannedBarcode by mutableStateOf<String?>(null)


    fun startCamera(activity: ComponentActivity) {
        log.debug("startCamera()")
        if (activity.isDestroyed) {
            log.warn("startCamera() called with destroyed activity, is the user trying to rotate the device?")
            return
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener(
            @ExperimentalCamera2Interop
            {
                val cameraProvider = cameraProviderFuture.get()

                cameraProvider.unbindAll()

                val useCaseGroup = UseCaseGroup.Builder().apply {
                    addUseCase(preview)
                    addUseCase(imageAnalysis)
                    // do not set viewport, it will lower the resolution
                }.build()

                camera = cameraProvider.bindToLifecycle(
                    activity as LifecycleOwner,
                    cameraSelector,
                    useCaseGroup
                )

            }, ContextCompat.getMainExecutor(activity)
        )
    }

    fun isCameraStarted() = camera != null
}

@Composable
fun rememberScanBarcodeScreenState() = remember {
    mutableStateOf(ScanBarcodeScreenStateHolder())
}
