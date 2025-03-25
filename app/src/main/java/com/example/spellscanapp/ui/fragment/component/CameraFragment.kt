package com.example.spellscanapp.ui.fragment.component

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
import androidx.fragment.app.activityViewModels
import com.example.spellscanapp.analyzer.CardImageAnalyzer
import com.example.spellscanapp.databinding.FragmentCameraBinding
import com.example.spellscanapp.model.CardReference
import com.example.spellscanapp.service.CardImageAnalyzerService
import com.example.spellscanapp.ui.viewmodel.CardViewModel
import com.example.spellscanapp.util.afterMeasured
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private val cardViewModel: CardViewModel by activityViewModels()

    private lateinit var binding: FragmentCameraBinding

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cardImageAnalyzerService: CardImageAnalyzerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardImageAnalyzerService = CardImageAnalyzerService()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCameraBinding.inflate(layoutInflater, container, false)
        startCamera()
        return binding.root
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
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
                throw e
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun showCard(card: CardReference) {
        if(isAdded) {
            cardViewModel.cardLiveData.value = card
        }
    }

    private fun addOnTouchFocus(camera: Camera) {
        binding.previewView.afterMeasured {
            binding.previewView.setOnTouchListener { v, event ->
                v.performClick()
                return@setOnTouchListener when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                            binding.previewView.width.toFloat(),
                            binding.previewView.height.toFloat()
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
                            Log.e(TAG, "cannot access camera", e)
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

    companion object {
        private const val TAG = "CameraFragment"
    }
}