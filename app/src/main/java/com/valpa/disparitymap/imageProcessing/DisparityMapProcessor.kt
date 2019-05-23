package com.valpa.disparitymap.imageProcessing

import org.opencv.calib3d.StereoBM
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class DisparityMapProcessor(private val imageStorage: ImageStorage) {

    fun calculateDisparityMap(img1: Mat, img2: Mat) {

        val img1Gray = Mat()
        val img2Gray = Mat()

        Imgproc.cvtColor(img1, img1Gray, Imgproc.COLOR_RGB2GRAY)
        Imgproc.cvtColor(img2, img2Gray, Imgproc.COLOR_RGB2GRAY)

        val stereoBM = StereoBM.create(NUM_DISPARITIES, BLOCK_SIZE)
        stereoBM.preFilterSize = 5
        stereoBM.preFilterCap = 61
        stereoBM.minDisparity = -39
        stereoBM.textureThreshold = 507
        stereoBM.uniquenessRatio = 0
        stereoBM.speckleWindowSize = 0
        stereoBM.speckleRange = 8
        stereoBM.disp12MaxDiff = 1

        val disparityMap = Mat()
        stereoBM.compute(img1Gray, img2Gray, disparityMap)

        imageStorage.savePhoto(disparityMap, "disparityMap")
    }

    companion object {

        private const val NUM_DISPARITIES = 112
        private const val BLOCK_SIZE = 9

    }

}