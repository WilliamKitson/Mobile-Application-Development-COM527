package com.example.a4kitsw10com527

import android.os.Handler
import android.os.Looper
import org.junit.Test

class LocationModelTest {
    @Test
    fun zoomDefault() {
        Handler(Looper.getMainLooper()).post { LocationModel.getZoomLive().observeForever {
            assert(it == 14.0)
        }}
    }
}