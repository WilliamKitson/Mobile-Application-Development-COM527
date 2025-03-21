package com.example.a4kitsw10com527

import android.Manifest
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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.a4kitsw10com527.ui.theme._4kitsw10COM527Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity(), LocationListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher()
        startGPS()

        enableEdgeToEdge()
        setContent {
            _4kitsw10COM527Theme {
                Button(onClick = {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            val database = MyDatabase.getDatabase(application)

                            database.myDAO().insert(DataEntity(
                                id = 0,
                                name = "test",
                                type = "test",
                                latitude = 0.0,
                                longitude = 0.0,
                                rooms = 0,
                                meals = false
                            ))

                            for (i in database.myDAO().getAll()) {
                                print("id: ${i.id}")
                            }
                        }
                    }
                }) {
                    Text("database")
                }

                /*
                NavigationComposable(Modifier
                    .border(BorderStroke(2.dp, Color.Red))
                    .padding(16.dp)
                    .fillMaxWidth()
                )
                */
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
            Manifest.permission.ACCESS_FINE_LOCATION
        ))
    }

    private fun startGPS() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        if(checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            val mgr = getSystemService(LOCATION_SERVICE) as LocationManager
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }
    }

    override fun onLocationChanged(location: Location) {
        LocationModel.setLocation(
            location.latitude,
            location.longitude
        )
    }
}