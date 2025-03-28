package com.example.spellscanapp.analyzer

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.spellscanapp.model.CardReference
import com.example.spellscanapp.service.CardImageAnalyzerService
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class CardImageAnalyzer(
    private val cardImageAnalyzerService: CardImageAnalyzerService,
    private val onCardRecognized: (CardReference) -> Unit
) : ImageAnalysis.Analyzer {
    private var isOnProcessing = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return

        if (isOnProcessing) {
            imageProxy.close()
            return
        }

        isOnProcessing = true

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
            .addOnSuccessListener { text ->
                cardImageAnalyzerService.analyze(text, onCardRecognized)
            }
            .addOnCanceledListener { Log.e(TAG, "process canceled") }
            .addOnFailureListener { e -> Log.e(TAG, "process failed", e) }
            .addOnCompleteListener {
                isOnProcessing = false
                imageProxy.close()
            }
    }

    companion object {
        private const val TAG = "CardImageAnalyzer"
    }
}