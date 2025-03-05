package com.example.a4kitsw10com527

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.maplibre.android.geometry.LatLng

object LocationModel : ViewModel() {
    private var latLng = LatLng(51.05, -0.72)
        set(newValue) {
            field = newValue
            latLngLiveData.value = newValue
        }

    private var latLngLiveData = MutableLiveData<LatLng>()

    private var zoom = 14.0
        set(newValue) {
            field = newValue
            zoomLive.value = newValue
        }

    private var zoomLive = MutableLiveData<Double>()

    fun setLocation(latitude: Double, longitude: Double) {
        latLng = LatLng(
            latitude,
            longitude
        )
    }

    fun zoomIn() {
        zoom++
    }

    fun zoomOut() {
        zoom--
    }

    fun getLatitude(): Double {
        return latLng.latitude
    }

    fun getLongitude(): Double {
        return latLng.longitude
    }

    fun getLocationLive(): MutableLiveData<LatLng> {
        return latLngLiveData
    }

    fun getZoomLive(): MutableLiveData<Double> {
        return zoomLive
    }
}