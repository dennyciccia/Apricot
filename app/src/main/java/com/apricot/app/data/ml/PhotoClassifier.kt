package com.apricot.app.data.ml

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifierResult
import java.lang.AutoCloseable

class PhotoClassifier(
    private val context: Context,
    private val useFoodSpecificModel: Boolean
) : AutoCloseable {
    val defaultModel = "efficientnet_lite0.tflite"
    val foodSpecificModel = "aiy_vision_classifier_food_v1.tflite"

    // lazy makes the classifier created only at its first usage
    private val classifier: ImageClassifier by lazy {
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(
                BaseOptions.builder()
                    .setModelAssetPath(if (useFoodSpecificModel) foodSpecificModel else defaultModel)
                    .build()
            )
            .setRunningMode(RunningMode.IMAGE)
            .setMaxResults(1)
            .setDisplayNamesLocale("en")
            .build()

        ImageClassifier.createFromOptions(context, options)
    }

    fun classify(bitmap: Bitmap): String? {
        val result = runInference(bitmap)

        // Get first result
        val topResult = result.classificationResult()
            .classifications()
            .firstOrNull()
            ?.categories()
            ?.firstOrNull()

        return if (useFoodSpecificModel) topResult?.displayName() else topResult?.categoryName()
    }

    private fun runInference(bitmap: Bitmap): ImageClassifierResult {
        val mpImage = BitmapImageBuilder(bitmap).build()
        return classifier.classify(mpImage)
    }

    override fun close() {
        classifier.close()
    }
}