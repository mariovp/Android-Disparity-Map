package com.valpa.disparitymap

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.valpa.disparitymap.imageCache.AutoLoadingBitmap
import com.valpa.disparitymap.imageCache.ImageCache
import com.valpa.disparitymap.imageProcessing.DisparityMapProcessor
import com.valpa.disparitymap.imageProcessing.HomographyProcessor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val homographyProcessor = HomographyProcessor()
    private val disparityMapProcessor = DisparityMapProcessor()

    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_take_left.setOnClickListener { takeLeftPhoto() }
        button_take_right.setOnClickListener { takeRightPhoto() }
        button_process.setOnClickListener { process() }
    }

    public override fun onResume() {
        super.onResume()
        OpenCVLoader.initDebug()
        restoreImageViews()
    }

    private fun restoreImageViews() {
        with(ImageCache) {
            leftImage?.setPic(imageView_leftPhoto)
            rightImage?.setPic(imageView_rightPhoto)
        }
    }

    private fun process() {
        clearProcessImages()

        val leftMat = ImageCache.leftImage?.asMat()!!
        val rightMat = ImageCache.rightImage?.asMat()!!

        val rawFile = createImageFile().absolutePath
        val filteredFile = createImageFile().absolutePath
        ImageCache.rawDisparityMap = AutoLoadingBitmap(rawFile)
        ImageCache.filteredDisparityMap = AutoLoadingBitmap(filteredFile)

        if (checkBox_homography.isChecked) {
            val matchesFile = createImageFile().absolutePath
            val correctedFile = createImageFile().absolutePath
            ImageCache.matchesImage = AutoLoadingBitmap(matchesFile)
            ImageCache.correctedImage = AutoLoadingBitmap(correctedFile)

            homographyProcessor.calculateHomography(leftMat, rightMat, matchesFile, correctedFile)
            val rightCorrected = ImageCache.correctedImage?.asMat(1)!!
            disparityMapProcessor.calculateDisparityMap(leftMat, rightCorrected, rawFile, filteredFile)
        } else {
            disparityMapProcessor.calculateDisparityMap(leftMat, rightMat, rawFile, filteredFile)
        }

        GlobalScope.launch(context = Dispatchers.Main) {
            delay(5000)
            ImageCache.matchesImage?.setPic(imageView_homography_points)
            ImageCache.correctedImage?.setPic(imageView_homography_corrected)
            ImageCache.rawDisparityMap?.setPic(imageView_depthMap_raw)
            ImageCache.filteredDisparityMap?.setPic(imageView_depthMap_filtered)
        }
    }

    private fun clearProcessImages() {

        with(ImageCache) {
            matchesImage = null
            correctedImage = null
            rawDisparityMap = null
            filteredDisparityMap = null
        }

        imageView_homography_points.setImageResource(R.color.gray)
        imageView_homography_corrected.setImageResource(R.color.gray)
        imageView_depthMap_raw.setImageResource(R.color.gray)
        imageView_depthMap_filtered.setImageResource(R.color.gray)
    }

    private fun takeLeftPhoto() {
        dispatchTakePictureIntent(REQUEST_TAKE_LEFT_PHOTO)
        ImageCache.leftImage = AutoLoadingBitmap(currentPhotoPath!!)
        currentPhotoPath = null
    }

    private fun takeRightPhoto() {
        dispatchTakePictureIntent(REQUEST_TAKE_RIGHT_PHOTO)
        ImageCache.rightImage = AutoLoadingBitmap(currentPhotoPath!!)
        currentPhotoPath = null
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent(photoToTake: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    //...
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, photoToTake)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when(requestCode) {
                REQUEST_TAKE_LEFT_PHOTO -> ImageCache.leftImage?.setPic(imageView_leftPhoto)
                REQUEST_TAKE_RIGHT_PHOTO -> ImageCache.rightImage?.setPic(imageView_rightPhoto)
            }
        }
    }

    companion object {
        const val REQUEST_TAKE_LEFT_PHOTO = 1
        const val REQUEST_TAKE_RIGHT_PHOTO = 2
    }

}
