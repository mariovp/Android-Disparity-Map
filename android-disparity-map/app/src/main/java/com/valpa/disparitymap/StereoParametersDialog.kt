package com.valpa.disparitymap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.valpa.disparitymap.databinding.LayoutStereoParametersBinding
import com.valpa.disparitymap.imageProcessing.DisparityParams
import com.valpa.disparitymap.ui.RoundedBottomSheetDialogFragment

class StereoParametersDialog: RoundedBottomSheetDialogFragment() {

    private var _binding: LayoutStereoParametersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutStereoParametersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textInputEditTextMinDisparities.setText(DisparityParams.minDisparity.toString())
        binding.textInputEditTextNumDisparities.setText(DisparityParams.numDisparities.toString())
        binding.textInputEditTextBlockSize.setText(DisparityParams.blockSize.toString())
        binding.textInputEditTextP1.setText(DisparityParams.P1.toString())
        binding.textInputEditTextP2.setText(DisparityParams.P2.toString())
        binding.textInputEditTextDisp12MaxDiff.setText(DisparityParams.disp12MaxDiff.toString())
        binding.textInputEditTextPreFilterCap.setText(DisparityParams.preFilterCap.toString())
        binding.textInputEditTextUniquenessRatio.setText(DisparityParams.uniquenessRatio.toString())
        binding.textInputEditTextSpeckleWindowSize.setText(DisparityParams.speckleWindowSize.toString())
        binding.textInputEditTextSpeckleRange.setText(DisparityParams.speckleRange.toString())

        binding.buttonAccept.setOnClickListener { saveParams() }
    }

    private fun saveParams() {

        with(DisparityParams) {
            minDisparity = binding.textInputEditTextMinDisparities.text.toString().toInt()
            numDisparities = binding.textInputEditTextNumDisparities.text.toString().toInt()
            blockSize = binding.textInputEditTextBlockSize.text.toString().toInt()
            P1 = binding.textInputEditTextP1.text.toString().toInt()
            P2 = binding.textInputEditTextP2.text.toString().toInt()
            disp12MaxDiff = binding.textInputEditTextDisp12MaxDiff.text.toString().toInt()
            preFilterCap = binding.textInputEditTextPreFilterCap.text.toString().toInt()
            uniquenessRatio = binding.textInputEditTextUniquenessRatio.text.toString().toInt()
            speckleWindowSize = binding.textInputEditTextSpeckleWindowSize.text.toString().toInt()
            speckleRange = binding.textInputEditTextSpeckleRange.text.toString().toInt()
        }

        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}