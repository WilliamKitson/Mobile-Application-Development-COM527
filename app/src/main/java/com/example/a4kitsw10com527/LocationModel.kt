package com.example.a4kitsw10com527

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.maplibre.android.geometry.LatLng

class LocationModel : ViewModel() {
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

    private var notificationLandmark: Landmark? = null
        set(newValue) {
            field = newValue
            notificationLandmarkLiveData.value = newValue
        }

    private var notificationLandmarkLiveData = MutableLiveData<Landmark?>()
    private var zoomLive = MutableLiveData<Double>()
    private var landmarks = arrayOf<Landmark>()

    fun setLocation(latitude: Double, longitude: Double) {
        latLng = LatLng(
            latitude,
            longitude
        )
    }

    fun calculateNotificationLandmark() {
        for (landmark in landmarks) {
            if (squareRoot(difference(landmark)) <= 50.0) {
                notificationLandmark = landmark
                return
            }
        }

        notificationLandmark = null
    }

    private fun squareRoot(input: Double): Double {
        var output: Double = input

        while ((output - input / output) > 0.000001f)
        {
            output = (output + input / output) / 2
        }

        return output
    }

    private fun difference(landmark: Landmark): Double {
        return difference(latLng.latitude, landmark.latitude) + difference(latLng.longitude, landmark.longitude)
    }

    private fun difference(a: Double, b: Double): Double {
        return square(a - b)
    }

    private fun square(input: Double): Double {
        return input * input
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

    fun addLandmark(landmark: Landmark) {
        landmarks += landmark
    }

    fun clearLandmarks() {
        landmarks = arrayOf()
    }

    fun getLocationLive(): MutableLiveData<LatLng> {
        return latLngLiveData
    }

    fun getZoomLive(): MutableLiveData<Double> {
        return zoomLive
    }

    fun getLandmarks(): Array<Landmark> {
        return landmarks
    }

    fun getNotificationLandmark(): Landmark? {
        return notificationLandmark
    }

    fun getNotificationLandmarkLive(): MutableLiveData<Landmark?> {
        return notificationLandmarkLiveData
    }
}