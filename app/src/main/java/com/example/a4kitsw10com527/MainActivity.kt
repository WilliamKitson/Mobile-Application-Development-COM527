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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    private val locationModel = LocationModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher()
        startGPS()
        loadLandmarks()

        enableEdgeToEdge()
        setContent {
            _4kitsw10COM527Theme {
                NavigationComposable(Modifier
                    .border(BorderStroke(2.dp, Color.Red))
                    .padding(16.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
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
        locationModel.setLocation(
            location.latitude,
            location.longitude
        )
    }

    private fun loadLandmarks() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val database = LandmarksDatabase.getDatabase(application)

                database.landmarksDataAccessObject().getAll().forEach {
                    locationModel.addLandmark(Landmark(
                        it.name,
                        it.type,
                        it.latitude,
                        it.longitude,
                        it.rooms,
                        it.meals
                    ))
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NavigationComposable(modifier: Modifier) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()
        val navController = rememberNavController()

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    actions = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(imageVector = Icons.Filled.Menu, "Menu")
                        }
                    },
                    title = { Text("Main Menu") }
                )
            }
        ) { innerPadding ->
            ModalNavigationDrawer(
                drawerState = drawerState,
                modifier = Modifier.padding(innerPadding),
                drawerContent = {
                    ModalDrawerSheet{
                        NavigationDrawerItem(
                            selected = false,
                            label = { Text("Add Location") },
                            onClick = {
                                navController.navigate("addLocation")

                                coroutineScope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                    }
                }
            ) {
                NavHost(
                    navController=navController,
                    startDestination="map"
                ) {
                    composable("map") {
                        MapComposable(modifier, navController)
                    }

                    composable("addLocation") {
                        AddLocationComposable(modifier, navController)
                    }
                }
            }
        }
    }

    @Composable
    fun MapComposable(modifier: Modifier, navController: NavController) {
        var location by remember { mutableStateOf(LatLng(0.0, 0.0)) }
        var zoom by remember { mutableDoubleStateOf(14.0) }

        locationModel.getLocationLive().observe(this) {
            location = it
        }

        locationModel.getZoomLive().observe(this) {
            zoom = it
        }

        Surface(modifier) {
            MapLibre(
                modifier = Modifier,
                styleBuilder = Style.Builder().fromUri("https://tiles.openfreemap.org/styles/bright"),
                cameraPosition = CameraPosition(target = location, zoom = zoom)
            ) {
                locationModel.getLandmarks().forEach {
                    Circle(
                        LatLng(it.latitude, it.longitude),
                        25.0f
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    locationModel.zoomOut()
                }) {
                    Text("-")
                }

                Button(onClick = {
                    navController.navigate("addLocation")
                }) {
                    Text("Save Location")
                }

                Button(onClick = {
                    locationModel.zoomIn()
                }) {
                    Text("+")
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

        locationModel.getLocationLive().observeForever {
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
                        navController.navigate("map")
                    }) {
                        Text("Back")
                    }

                    Button(onClick = {
                        writeLandmark(Landmark(
                            name,
                            type,
                            latitude,
                            longitude,
                            rooms,
                            meals
                        ))

                        navController.navigate("map")
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }

    private fun writeLandmark(landmark: Landmark) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val database = LandmarksDatabase.getDatabase(application)

                database.landmarksDataAccessObject().insert(
                    LandmarksDataEntity(
                        id = 0,
                        name = landmark.name,
                        type = landmark.type,
                        latitude = landmark.latitude,
                        longitude = landmark.longitude,
                        rooms = landmark.rooms,
                        meals = landmark.meals
                    )
                )

                locationModel.addLandmark(landmark)
            }
        }
    }
}