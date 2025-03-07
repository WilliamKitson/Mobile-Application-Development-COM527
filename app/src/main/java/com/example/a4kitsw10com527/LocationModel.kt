package com.example.a4kitsw10com527

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.maplibre.android.geometry.LatLng

object LocationModel : ViewModel() {
    private var latLng = LatLng(0.0, 0.0)
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
    private var pointsOfInterest = arrayOf<PointOfInterest>()

    fun setLocation(latitude: Double, longitude: Double) {
        latLng = LatLng(
            latitude,
            longitude
        )
    }

    fun zoomIn() {
        zoom++

        val zoomMaximum = 20.0

        if (zoom > zoomMaximum) {
            zoom = zoomMaximum
        }
    }

    fun zoomOut() {
        zoom--

        val zoomMinimum = 0.0

        if (zoom < zoomMinimum) {
            zoom = zoomMinimum
        }
    }

    fun addPointOfInterest(pointOfInterest: PointOfInterest) {
        pointsOfInterest += pointOfInterest
    }

    fun getLocationLive(): MutableLiveData<LatLng> {
        return latLngLiveData
    }

    fun getZoomLive(): MutableLiveData<Double> {
        return zoomLive
    }
}