package com.example.ecovision.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.Nullable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class ImageViewTargetWithProgressBar(
    private val imageView: ImageView,
    private val progressBar: ProgressBar
) : CustomTarget<Drawable>() {

    override fun onLoadStarted(@Nullable placeholder: Drawable?) {
        super.onLoadStarted(placeholder)
        progressBar.visibility = View.VISIBLE
    }

    override fun onLoadCleared(@Nullable placeholder: Drawable?) {
        progressBar.visibility = View.GONE
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        progressBar.visibility = View.GONE
        imageView.setImageDrawable(resource)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        progressBar.visibility = View.GONE
        super.onLoadFailed(errorDrawable)
    }
}
