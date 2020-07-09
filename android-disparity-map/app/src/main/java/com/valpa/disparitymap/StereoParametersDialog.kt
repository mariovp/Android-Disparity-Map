package com.valpa.disparitymap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.valpa.disparitymap.imageProcessing.DisparityParams
import com.valpa.disparitymap.ui.RoundedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_stereo_parameters.*

class StereoParametersDialog: RoundedBottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_stereo_parameters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textInputEditText_minDisparities.setText(DisparityParams.minDisparity.toString())
        textInputEditText_numDisparities.setText(DisparityParams.numDisparities.toString())
        textInputEditText_blockSize.setText(DisparityParams.blockSize.toString())
        textInputEditText_p1.setText(DisparityParams.P1.toString())
        textInputEditText_p2.setText(DisparityParams.P2.toString())
        textInputEditText_disp12MaxDiff.setText(DisparityParams.disp12MaxDiff.toString())
        textInputEditText_preFilterCap.setText(DisparityParams.preFilterCap.toString())
        textInputEditText_uniquenessRatio.setText(DisparityParams.uniquenessRatio.toString())
        textInputEditText_speckleWindowSize.setText(DisparityParams.speckleWindowSize.toString())
        textInputEditText_speckleRange.setText(DisparityParams.speckleRange.toString())

        button_accept.setOnClickListener { saveParams() }
    }

    private fun saveParams() {

        with(DisparityParams) {
            minDisparity = textInputEditText_minDisparities.text.toString().toInt()
            numDisparities = textInputEditText_numDisparities.text.toString().toInt()
            blockSize = textInputEditText_blockSize.text.toString().toInt()
            P1 = textInputEditText_p1.text.toString().toInt()
            P2 = textInputEditText_p2.text.toString().toInt()
            disp12MaxDiff = textInputEditText_disp12MaxDiff.text.toString().toInt()
            preFilterCap = textInputEditText_preFilterCap.text.toString().toInt()
            uniquenessRatio = textInputEditText_uniquenessRatio.text.toString().toInt()
            speckleWindowSize = textInputEditText_speckleWindowSize.text.toString().toInt()
            speckleRange = textInputEditText_speckleRange.text.toString().toInt()
        }

        dismiss()
    }

}