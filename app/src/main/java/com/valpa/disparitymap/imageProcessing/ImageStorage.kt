package com.valpa.disparitymap.imageProcessing

import android.os.Environment
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import java.text.SimpleDateFormat
import java.util.*

class ImageStorage {

    fun savePhoto(img: Mat, name: String) {
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
        val currentDateAndTime = sdf.format(Date())
        val fileName = Environment.getExternalStorageDirectory().path +
                "/"+ name + "_" + currentDateAndTime + ".jpg"
        Imgcodecs.imwrite(fileName, img)
    }

}