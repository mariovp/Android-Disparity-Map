package com.valpa.disparitymap.imageCache

import android.graphics.BitmapFactory
import android.widget.ImageView
import org.opencv.android.Utils
import org.opencv.core.Mat

class AutoLoadingBitmap(private val imagePath: String) {

    fun setPic(imageView: ImageView) {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(imagePath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

    fun asMat(scaleBy: Int = 4): Mat {
        val bmOptions = BitmapFactory.Options().apply { inSampleSize = scaleBy } // Scale original by 1/4
        val mat = Mat()
        BitmapFactory.decodeFile(imagePath, bmOptions)?.also { bitmap -> Utils.bitmapToMat(bitmap, mat) }
        return mat
    }
}