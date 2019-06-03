package com.valpa.disparitymap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.valpa.disparitymap.ui.RoundedBottomSheetDialogFragment

class StereoParametersDialog: RoundedBottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_stereo_parameters, container, false)
    }

}