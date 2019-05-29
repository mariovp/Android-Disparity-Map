package com.valpa.disparitymap.imageProcessing

import org.opencv.calib3d.StereoBM
import org.opencv.calib3d.StereoSGBM
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.imgproc.Imgproc
import org.opencv.ximgproc.Ximgproc

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

        val minDisparity = -64
        val numDisparities = 256
        val blockSize = 11
        val P1 = 200
        val P2 = 400
        val disp12MaxDiff = 10
        val preFilterCap = 4
        val uniquenessRatio = 1
        val speckleWindowSize = 200
        val speckleRange = 16
        val mode = StereoSGBM.MODE_SGBM

        val leftStereoSGBM = StereoSGBM.create(
            minDisparity,
            numDisparities,
            blockSize,
            P1,
            P2,
            disp12MaxDiff,
            preFilterCap,
            uniquenessRatio,
            speckleWindowSize,
            speckleRange,
            mode)

        val disparityMapLeft = Mat()
        leftStereoSGBM.compute(img1Gray, img2Gray, disparityMapLeft)

        val rightStereoSGBM = Ximgproc.createRightMatcher(leftStereoSGBM)
        val disparityMapRight = Mat()
        rightStereoSGBM.compute(img2Gray, img1Gray, disparityMapRight)

        val disparityMatFiltered = Mat(disparityMapLeft.rows(), disparityMapLeft.cols(), CvType.CV_8UC1)
        val disparityWLSFilter = Ximgproc.createDisparityWLSFilter(leftStereoSGBM)
        disparityWLSFilter.lambda = 44000.0 //PrefHelper.getLambda(activity)
        disparityWLSFilter.sigmaColor = 2.5//PrefHelper.getSigma(activity)
        disparityWLSFilter.filter(disparityMapLeft, img1Gray, disparityMatFiltered, disparityMapRight, Rect(0, 0, disparityMapLeft.cols(), disparityMapLeft.rows()), img2Gray)


        /*val disp8 = Mat()
        Core.normalize(disparityMap, disp8, 0.0, 255.0, Core.NORM_MINMAX, CvType.CV_8U)*/

        imageStorage.savePhoto(disparityMatFiltered, "disparityMap", isColor=false)
    }

    companion object {

        private const val NUM_DISPARITIES = 112
        private const val BLOCK_SIZE = 9

    }

}