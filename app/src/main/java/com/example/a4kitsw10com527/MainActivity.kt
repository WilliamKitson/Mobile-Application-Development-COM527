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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.a4kitsw10com527.ui.theme._4kitsw10COM527Theme
import org.maplibre.android.geometry.LatLng

class MainActivity : ComponentActivity(), LocationListener {
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
        LocationModel.latLng = LatLng(
            location.latitude,
            location.longitude
        )
    }
}