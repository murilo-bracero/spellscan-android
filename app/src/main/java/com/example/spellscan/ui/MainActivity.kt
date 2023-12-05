package com.example.spellscan.ui

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.MeteringPointFactory
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.spellscan.analyzer.CardImageAnalyzer
import com.example.spellscan.databinding.ActivityMainBinding
import com.example.spellscan.logger.TAG
import com.example.spellscan.model.Card
import com.example.spellscan.provider.PermissionsProvider
import com.example.spellscan.service.CardImageAnalyzerService
import com.example.spellscan.ui.viewmodel.CardViewModel
import com.example.spellscan.util.afterMeasured
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private val cardViewModel: CardViewModel by viewModels()

    private lateinit var viewBinding: ActivityMainBinding

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cardImageAnalyzerService: CardImageAnalyzerService
    private lateinit var permissionsProvider: PermissionsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        cardImageAnalyzerService = CardImageAnalyzerService()
        permissionsProvider = PermissionsProvider(this)

        if (permissionsProvider.allPermissionsGranted()) {
            startCamera()
        } else {
            permissionsProvider.requestAppPermissions()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.previewView.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        CardImageAnalyzer(cardImageAnalyzerService) { card -> showCard(card) })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
                addOnTouchFocus(camera)
            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
                Toast.makeText(baseContext, "Use case binding failed", Toast.LENGTH_SHORT)
                    .show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun showCard(card: Card) {
        cardViewModel.cardLiveData.value = card
    }

    private fun addOnTouchFocus(camera: Camera) {
        viewBinding.previewView.afterMeasured {
            viewBinding.previewView.setOnTouchListener { v, event ->
                v.performClick()
                return@setOnTouchListener when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                            viewBinding.previewView.width.toFloat(),
                            viewBinding.previewView.height.toFloat()
                        )
                        val autoFocusPoint = factory.createPoint(event.x, event.y)
                        try {
                            camera.cameraControl.startFocusAndMetering(
                                FocusMeteringAction.Builder(
                                    autoFocusPoint,
                                    FocusMeteringAction.FLAG_AF
                                ).apply {
                                    //focus only when the user tap the preview
                                    disableAutoCancel()
                                }.build()
                            )
                        } catch (e: CameraInfoUnavailableException) {
                            Log.d("ERROR", "cannot access camera", e)
                        }
                        true
                    }

                    else -> false // Unhandled event.
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
