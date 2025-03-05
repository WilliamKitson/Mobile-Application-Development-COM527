package com.example.a4kitsw10com527

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a4kitsw10com527.ui.theme._4kitsw10COM527Theme
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.MapLibre

class MainActivity : ComponentActivity(), LocationListener {
    private val locationModel : LocationModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher()
        startGPS()

        enableEdgeToEdge()
        setContent {
            _4kitsw10COM527Theme {
                NavigationComposable(Modifier
                    .border(BorderStroke(2.dp, Color.Red))
                    .padding(16.dp)
                    .fillMaxWidth()
                )
            }
        }
    }

    private fun permissionLauncher() {
        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Toast.makeText(this, "permission: ${it.key}, granted: ${it.value}", Toast.LENGTH_LONG).show()
            }
        }

        permissionLauncher.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ))
    }

    private fun startGPS() {
        val permission = android.Manifest.permission.ACCESS_FINE_LOCATION

        if(checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            val mgr = getSystemService(LOCATION_SERVICE) as LocationManager
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }
    }

    override fun onLocationChanged(location: Location) {
        locationModel.latLng.apply { LatLng(
            location.latitude,
            location.longitude
        ) }

        Toast.makeText(this, "lat ${location.latitude} lon ${location.longitude}", Toast.LENGTH_LONG).show()
    }

    @Composable
    fun NavigationComposable(modifier: Modifier) {
        val navController = rememberNavController()

        NavHost(navController=navController, startDestination="map") {
            composable("map") {
                MapComposable(modifier)
            }
        }
    }

    @Composable
    fun MapComposable(modifier: Modifier) {
        var latitude by remember { mutableDoubleStateOf(locationModel.latLng.latitude) }
        var longitude by remember { mutableDoubleStateOf(locationModel.latLng.longitude) }
        var zoom by remember { mutableDoubleStateOf(14.0) }

        locationModel.latLngLiveData.observe(this) {
            latitude = locationModel.latLng.latitude
            longitude = locationModel.latLng.longitude
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

                Text("latitude ${locationModel.latLng.latitude}, longitude ${locationModel.latLng.longitude}")
            }
        }
    }
}