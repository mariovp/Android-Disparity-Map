package com.valpa.disparitymap.imageProcessing

import org.opencv.calib3d.Calib3d
import org.opencv.core.*
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.Features2d
import org.opencv.features2d.ORB
import org.opencv.imgproc.Imgproc

class HomographyProcessor(private val imageStorage: ImageStorage, private val disparityMapProcessor: DisparityMapProcessor) {

    fun calculateHomography(img1: Mat, img2: Mat) {

        val img1Gray = Mat()
        val img2Gray = Mat()

        Imgproc.cvtColor(img1, img1Gray, Imgproc.COLOR_RGB2GRAY)
        Imgproc.cvtColor(img2, img2Gray, Imgproc.COLOR_RGB2GRAY)

        val keypoints1 = MatOfKeyPoint()
        val keypoints2 = MatOfKeyPoint()

        val descriptors1 = Mat()
        val descriptors2 = Mat()

        val orb = ORB.create(MAX_FEATURES)
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

        // Draw best matches and save photo
        val imMatches = Mat()
        Features2d.drawMatches(img1, keypoints1, img2, keypoints2, matches, imMatches)
        imageStorage.savePhoto(imMatches, "matches")

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

        val h = Calib3d.findHomography(points1, points2, Calib3d.RANSAC)

        val img1Reg = Mat()

        Imgproc.warpPerspective(img1, img1Reg, h, img2.size())

        imageStorage.savePhoto(img1Reg, "corrected")

        disparityMapProcessor.calculateDisparityMap(img1Reg, img2)
    }

    companion object {
        private const val MAX_FEATURES = 500
        private const val GOOD_MATCH_PERCENT = 0.15f
    }

}