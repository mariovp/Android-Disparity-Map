package com.valpa.disparitymap


import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_fullscreen.*
import org.opencv.android.*
import org.opencv.core.*
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.Features2d
import org.opencv.features2d.ORB
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        cameraView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    var mRgba: Mat? = null
    var mRgbaF: Mat? = null
    var mRgbaT: Mat? = null

    var isFirstImage: Boolean = true
    var img1: Mat? = null
    var img2: Mat? = null

    private lateinit var mOpenCvCameraView: JavaCameraView

    private val mLoaderCallback: BaseLoaderCallback = object: BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when(status) {
                LoaderCallbackInterface.SUCCESS -> mOpenCvCameraView.enableView()
                else -> super.onManagerConnected(status)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        // cameraView.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        mOpenCvCameraView = findViewById(R.id.cameraView)
        mOpenCvCameraView.setCameraIndex(0)
        mOpenCvCameraView.setCvCameraViewListener(this)
        fab_take_photo.setOnClickListener { takeImage() }
    }

    public override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            //Log.d(FragmentActivity.TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
        } else {
            //Log.d(FragmentActivity.TAG, "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        hide()
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        cameraView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        mRgba = Mat(height, width, CvType.CV_8UC4)
        mRgbaF = Mat(height, width, CvType.CV_8UC4)
        mRgbaT = Mat(width, width, CvType.CV_8UC4)
    }

    override fun onCameraViewStopped() {
        mRgba?.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        mRgba = inputFrame?.rgba()
        return mRgba!!
    }

    private fun takeImage() {
        if (isFirstImage) {
            img1 = Mat()
            mRgba?.copyTo(img1)
            isFirstImage = false
            Toast.makeText(this, "Took photo 1", Toast.LENGTH_SHORT).show()
        }

        else {
            img2 = Mat()
            mRgba?.copyTo(img2)
            isFirstImage = true
            Toast.makeText(this, "Took photo 2", Toast.LENGTH_SHORT).show()
            calculateHomography(img1!!, img2!!)
        }
    }

    private fun calculateHomography(img1: Mat, img2: Mat) {

        val MAX_FEATURES = 500
        val GOOD_MATCH_PERCENT = 0.15f

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

        var matches: MatOfDMatch = MatOfDMatch()

        val matcher = DescriptorMatcher.create("BruteForce-Hamming")
        matcher.match(descriptors1, descriptors2, matches, Mat())

        // Sort matches by distance (score)
        val matchList: List<DMatch>  = matches.toList().sortedBy { dMatch -> dMatch.distance }

        val matchesSize = matches.size().height * matches.size().width
        val numGoodMatches: Int = (matchesSize * GOOD_MATCH_PERCENT).toInt()

        matches = MatOfDMatch()
        matches.fromList(matchList.subList(0, numGoodMatches))

        val imMatches = Mat()
        Features2d.drawMatches(img1, keypoints1, img2, keypoints2, matches, imMatches)
        savePhoto(imMatches)

        val points1 = MatOfPoint2f()
        val points2 = MatOfPoint2f()
    }

    private fun savePhoto(img: Mat) {
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
        val currentDateandTime = sdf.format(Date())
        val fileName = Environment.getExternalStorageDirectory().path +
                "/sample_picture_" + currentDateandTime + ".jpg"
        Toast.makeText(this, "$fileName saved", Toast.LENGTH_SHORT).show()
        val filename = "/storage/emulated/0/DCIM/Camera/samplepass.jpg"
        Imgcodecs.imwrite(filename, img)
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
