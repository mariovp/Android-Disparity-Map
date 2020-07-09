package com.valpa.disparitymap.imageProcessing

import org.opencv.calib3d.Calib3d
import org.opencv.core.*
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.Features2d
import org.opencv.features2d.ORB
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

class HomographyProcessor() {

    fun calculateHomography(img1: Mat, img2: Mat, matchesOutput: String, correctedOutput: String) {

        val img1Gray = Mat()
        val img2Gray = Mat()

        Imgproc.cvtColor(img1, img1Gray, Imgproc.COLOR_RGB2GRAY)
        Imgproc.cvtColor(img2, img2Gray, Imgproc.COLOR_RGB2GRAY)

        val keypoints1 = MatOfKeyPoint()
        val keypoints2 = MatOfKeyPoint()

        val descriptors1 = Mat()
        val descriptors2 = Mat()

        val orb = ORB.create(MAX_FEATURES,1.2f, 8, 31,0,2, ORB.HARRIS_SCORE,31, 20)
        orb.detectAndCompute(img1Gray, Mat(), keypoints1, descriptors1)
        orb.detectAndCompute(img2Gray, Mat(), keypoints2, descriptors2)

        var matches = MatOfDMatch()

        val matcher = DescriptorMatcher.create("BruteForce-Hamming")
        matcher.match(descriptors1, descriptors2, matches, Mat())

        // Sort matches by distance (score)
        val matchList: List<DMatch>  = matches.toList().sortedBy { dMatch -> dMatch.distance }

        val numGoodMatches: Int = (matches.size().area() * GOOD_MATCH_PERCENT).toInt()

        matches = MatOfDMatch()
        matches.fromList(matchList.subList(0, numGoodMatches))

        /*imageStorage.savePhoto(img1, "img1")
        imageStorage.savePhoto(img2, "img2")*/
        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2RGB)
        Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2RGB)
        // Draw best matches and save photo
        val imMatches = Mat()
        Features2d.drawMatches(img1, keypoints1, img2, keypoints2, matches, imMatches)

        Imgcodecs.imwrite(matchesOutput, imMatches)
        //imageStorage.savePhoto(imMatches, "matches")

        val points1List = ArrayList<Point>()
        val points2List = ArrayList<Point>()

        val keypoints1List = keypoints1.toList()
        val keypoints2List = keypoints2.toList()

        for (i in 0 until matchList.size) {
            points1List.add(keypoints1List[matchList[i].queryIdx].pt)
            points2List.add(keypoints2List[matchList[i].trainIdx].pt)
        }

        val points1 = MatOfPoint2f()
        points1.fromList(points1List)
        val points2 = MatOfPoint2f()
        points2.fromList(points2List)

        val h = Calib3d.findHomography(points2, points1, Calib3d.RANSAC, 0.995)

        val img2Reg = Mat()

        Imgproc.warpPerspective(img2, img2Reg, h, img2.size())

        val imgNorm = Mat()
        Core.normalize(img2Reg, imgNorm, 0.0, 255.0, Core.NORM_MINMAX, CvType.CV_8U)

        Imgcodecs.imwrite(correctedOutput, imgNorm)
        //imageStorage.savePhoto(img2Reg, "corrected")
        //disparityMapProcessor.calculateDisparityMap(img1, img2Reg)
    }

    companion object {
        private const val MAX_FEATURES = 500
        private const val GOOD_MATCH_PERCENT = 0.15f
    }

}