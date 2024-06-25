package com.example.spellscanapp.util

import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import coil.imageLoader
import coil.request.ImageRequest

inline fun View.afterMeasured(crossinline block: () -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        block()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }
}

fun ImageView.loadFromUrl(url: String) {
    val imageLoader = this.context.imageLoader
    val request = ImageRequest.Builder(this.context)
        .data(url)
        .target(this)
        .build()
    imageLoader.enqueue(request)
}