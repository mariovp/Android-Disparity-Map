package com.valpa.disparitymap.imageCache

object ImageCache {

    var leftImage: AutoLoadingBitmap? = null
    var rightImage: AutoLoadingBitmap? = null

    var matchesImage: AutoLoadingBitmap? = null
    var correctedImage: AutoLoadingBitmap? = null

    var rawDisparityMap: AutoLoadingBitmap? = null
    var filteredDisparityMap: AutoLoadingBitmap? = null

}