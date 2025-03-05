package com.example.a4kitsw10com527

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.maplibre.android.geometry.LatLng

object LocationModel : ViewModel() {
    var latLng = LatLng(51.05, -0.72)
        set(newValue) {
            field = newValue
            latLngLiveData.value = newValue
        }

    var latLngLiveData = MutableLiveData<LatLng>()
}