package com.valpa.disparitymap.imageProcessing

import org.opencv.calib3d.StereoBM
import org.opencv.calib3d.StereoSGBM
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class DisparityMapProcessor(private val imageStorage: ImageStorage) {

    fun calculateDisparityMap(img1: Mat, img2: Mat) {

        val img1Gray = Mat()
        val img2Gray = Mat()

        Imgproc.cvtColor(img1, img1Gray, Imgproc.COLOR_RGB2GRAY)
        Imgproc.cvtColor(img2, img2Gray, Imgproc.COLOR_RGB2GRAY)

        /*val stereoBM = StereoBM.create(NUM_DISPARITIES, BLOCK_SIZE)
        stereoBM.preFilterSize = 5
        stereoBM.preFilterCap = 61
        stereoBM.minDisparity = -39
        stereoBM.textureThreshold = 507
        stereoBM.uniquenessRatio = 0
        stereoBM.speckleWindowSize = 0
        stereoBM.speckleRange = 8
        stereoBM.disp12MaxDiff = 1

        val disparityMap = Mat()
        stereoBM.compute(img1Gray, img2Gray, disparityMap)*/

        val stereoSGBM = StereoSGBM.create(-64, 192, 5)
        stereoSGBM.preFilterCap = 4
        stereoSGBM.uniquenessRatio = 1
        stereoSGBM.speckleWindowSize = 150
        stereoSGBM.speckleRange = 2
        stereoSGBM.disp12MaxDiff = 10
        stereoSGBM.p1 = 600
        stereoSGBM.p2 = 2400

        val disparityMap = Mat()
        stereoSGBM.compute(img1Gray, img2Gray, disparityMap)

        /*val disp8 = Mat()
        Core.normalize(disparityMap, disp8, 0.0, 255.0, Core.NORM_MINMAX, CvType.CV_8U)*/

        imageStorage.savePhoto(disparityMap, "disparityMap")
    }

    companion object {

        private const val NUM_DISPARITIES = 112
        private const val BLOCK_SIZE = 9

    }

}