package com.valpa.disparitymap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.valpa.disparitymap.imageCache.AutoLoadingBitmap

class MainViewModel: ViewModel() {

    val leftImage: MutableLiveData<AutoLoadingBitmap> by lazy { MutableLiveData<AutoLoadingBitmap>() }
    val rightImage: MutableLiveData<AutoLoadingBitmap> by lazy { MutableLiveData<AutoLoadingBitmap>() }

    val matchesImage: MutableLiveData<AutoLoadingBitmap> by lazy { MutableLiveData<AutoLoadingBitmap>() }
    val correctedImage: MutableLiveData<AutoLoadingBitmap> by lazy { MutableLiveData<AutoLoadingBitmap>() }

    val rawDisparityMap: MutableLiveData<AutoLoadingBitmap> by lazy { MutableLiveData<AutoLoadingBitmap>() }
    val filteredDisparityMap: MutableLiveData<AutoLoadingBitmap> by lazy { MutableLiveData<AutoLoadingBitmap>() }

    val useHomographyCorrection: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

}