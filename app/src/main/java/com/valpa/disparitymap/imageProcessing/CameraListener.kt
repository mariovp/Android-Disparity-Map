package com.valpa.disparitymap.imageProcessing

import android.content.Context
import android.widget.Toast
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.CvType
import org.opencv.core.Mat

class CameraListener(private val context: Context, private val homographyProcessor: HomographyProcessor, private val disparityMapProcessor: DisparityMapProcessor): CameraBridgeViewBase.CvCameraViewListener2 {

    var mRgba: Mat? = null
    var isFirstImage: Boolean = true
    var img1: Mat? = null
    var img2: Mat? = null

    override fun onCameraViewStarted(width: Int, height: Int) {
        mRgba = Mat(height, width, CvType.CV_8UC4)
    }

    override fun onCameraViewStopped() {
        mRgba?.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        mRgba = inputFrame?.rgba()
        return mRgba!!
    }

    fun takeImage() {
        if (isFirstImage) {
            img1 = Mat()
            mRgba?.copyTo(img1)
            isFirstImage = false
            Toast.makeText(context, "Took photo 1", Toast.LENGTH_SHORT).show()
        }

        else {
            img2 = Mat()
            mRgba?.copyTo(img2)
            isFirstImage = true
            Toast.makeText(context, "Took photo 2", Toast.LENGTH_SHORT).show()
            homographyProcessor.calculateHomography(img2!!, img1!!)
            //disparityMapProcessor.calculateDisparityMap(img1!!, img2!!)
        }
    }

}