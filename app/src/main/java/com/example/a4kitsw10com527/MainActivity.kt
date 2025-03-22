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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a4kitsw10com527.ui.theme._4kitsw10COM527Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.Circle
import org.ramani.compose.MapLibre

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

    @Composable
    fun NavigationComposable(modifier: Modifier) {
        val navController = rememberNavController()

        NavHost(navController=navController, startDestination="map") {
            composable("map") {
                MapComposable(modifier, navController)
            }

            composable("addLocation") {
                AddLocationComposable(modifier, navController)
            }
        }
    }

    @Composable
    fun MapComposable(modifier: Modifier, navController: NavController) {
        var location by remember { mutableStateOf(LatLng(0.0, 0.0)) }
        var zoom by remember { mutableDoubleStateOf(14.0) }

        LocationModel.getLocationLive().observeForever {
            location = it
        }

        LocationModel.getZoomLive().observeForever {
            zoom = it
        }

        Surface(modifier) {
            Column {
                MapLibre(
                    modifier = Modifier.width(250.dp).height(250.dp),
                    styleBuilder = Style.Builder().fromUri("https://tiles.openfreemap.org/styles/bright"),
                    cameraPosition = CameraPosition(target = location, zoom = zoom)
                ) {
                    //TODO loop through database and draw
                }

                Row {
                    Button(onClick = {
                        LocationModel.zoomOut()
                    }) {
                        Text("-")
                    }

                    Text(zoom.toString())

                    Button(onClick = {
                        LocationModel.zoomIn()
                    }) {
                        Text("+")
                    }
                }

                Button(onClick = {
                    navController.navigate("addLocation")
                }) {
                    Text("Save Location")
                }
            }
        }
    }

    @Composable
    fun AddLocationComposable(modifier: Modifier, navController: NavController) {
        var name by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("") }
        var latitude by remember { mutableDoubleStateOf(0.0) }
        var longitude by remember { mutableDoubleStateOf(0.0) }
        var rooms by remember { mutableIntStateOf(0) }
        var meals by remember { mutableStateOf(false) }

        LocationModel.getLocationLive().observeForever {
            latitude = it.latitude
            longitude = it.longitude
        }

        Surface(modifier) {
            Column {
                TextField(value = name, placeholder = {
                    Text("Name")
                }, onValueChange = {
                    name = it
                })

                TextField(value = type, placeholder = {
                    Text("Type")
                }, onValueChange = {
                    type = it
                })

                TextField(value = rooms.toString(), placeholder = {
                    Text("Rooms")
                }, onValueChange = {
                    rooms = it.toInt()
                })

                Row {
                    Switch(checked = meals, onCheckedChange = {
                        meals = it
                    })

                    Text("Meals Provided")
                }

                Row {
                    Button(onClick = {
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                val database = MyDatabase.getDatabase(application)

                                database.myDAO().insert(MyDataEntity(
                                    id = 0,
                                    name = name,
                                    type = type,
                                    latitude = latitude,
                                    longitude = longitude,
                                    rooms = rooms,
                                    meals = meals
                                ))
                            }
                        }

                        navController.navigate("map")
                    }) {
                        Text("Save")
                    }

                    Button(onClick = {
                        navController.navigate("map")
                    }) {
                        Text("Back")
                    }
                }
            }
        }
    }
}