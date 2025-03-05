package com.example.a4kitsw10com527

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.MapLibre

@Composable
fun MapComposable(modifier: Modifier) {
    var latitude by remember { mutableDoubleStateOf(LocationModel.latLng.latitude) }
    var longitude by remember { mutableDoubleStateOf(LocationModel.latLng.longitude) }
    var zoom by remember { mutableDoubleStateOf(14.0) }

    LocationModel.latLngLiveData.observeForever {
        latitude = it.latitude
        longitude = it.longitude
    }

    Surface(modifier) {
        Column {
            MapLibre(
                modifier = Modifier.width(250.dp).height(250.dp),
                styleBuilder = Style.Builder().fromUri("https://tiles.openfreemap.org/styles/bright"),
                cameraPosition = CameraPosition(target = LatLng(latitude, longitude), zoom = zoom)
            )

            Row {
                Button(onClick = {
                    zoom--
                }) {
                    Text("-")
                }

                Text(zoom.toString())

                Button(onClick = {
                    zoom++
                }) {
                    Text("+")
                }
            }

            Text("latitude ${LocationModel.latLng.latitude}, longitude ${LocationModel.latLng.longitude}")
        }
    }
}