package com.example.a4kitsw10com527

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                    .fillMaxHeight()
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun permissionLauncher() {
        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Toast.makeText(this, "permission: ${it.key}, granted: ${it.value}", Toast.LENGTH_LONG).show()
            }
        }

        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
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

        locationModel.calculateNotificationLandmark()
        dispatchNotification()
    }

    private fun dispatchNotification() {
        if (locationModel.getNotificationLandmark() == null) {
            return
        }

        val channelID = "LOCATIONS_CHANNEL"

        val channel = NotificationChannel(
            channelID,
            "Location Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val nMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nMgr.createNotificationChannel(channel)

        val landmarkIntent = Intent(this, MainActivity::class.java).let {
            it.action = "ACTION_SHOW_LANDMARK"
            it.putExtra("latitude", locationModel.getNotificationLandmark()!!.latitude)
            it.putExtra("longitude", locationModel.getNotificationLandmark()!!.longitude)
        }

        landmarkIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val landmarkPendingIntent = PendingIntent.getActivity(
            this,
            0,
            landmarkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(this, channelID)
            .setContentTitle("Location update")
            .setContentText("you are within 50 meters of ${locationModel.getNotificationLandmark()!!.name}")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(landmarkPendingIntent)
            .build()

        nMgr.notify(
            0,
            notification
        )
    }

    override fun onNewIntent(intent: Intent){
        super.onNewIntent(intent)
        if(intent.action == "ACTION_SHOW_LANDMARK") {
            intent.extras?.let {
                locationModel.setLocation(
                    it.getDouble("latitude"),
                    it.getDouble("longitude")
                )
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
                                if (drawerState.isOpen) {
                                    drawerState.close()
                                } else {
                                    drawerState.open()
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.Filled.Menu, "Menu")
                        }
                    },
                    title = { Text("Main Menu") }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, "Map") },
                        label = { Text("Map") },
                        onClick = { navController.navigate("map") },
                        selected = false
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Add, "Add Landmark") },
                        label = { Text("Add Landmark") },
                        onClick = { navController.navigate("addLocation") },
                        selected = false
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("addLocation") },
                    content = {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Location")
                    }
                )
            }
        ) { innerPadding ->
            ModalNavigationDrawer(
                gesturesEnabled = false,
                drawerState = drawerState,
                modifier = Modifier.padding(innerPadding),
                drawerContent = {
                    ModalDrawerSheet{
                        NavigationDrawerItem(
                            selected = false,
                            label = { Text("Map") },
                            onClick = {
                                navController.navigate("map")

                                coroutineScope.launch {
                                    drawerState.close()
                                }
                            }
                        )

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
                        MapComposable(modifier)
                    }

                    composable("addLocation") {
                        AddLocationComposable(modifier, navController)
                    }
                }
            }
        }
    }

    @Composable
    fun MapComposable(modifier: Modifier) {
        var search by remember { mutableStateOf("") }
        var location by remember { mutableStateOf(LatLng(0.0, 0.0)) }
        var zoom by remember { mutableDoubleStateOf(14.0) }
        var popup by remember { mutableStateOf<Landmark?>(Landmark(
            "t",
            "t",
            "t",
            0.0,
            0.0,
            0,
            false
        )) }

        if (popup != null) {
            AlertDialog(
                title = {
                    Text(popup!!.name)
                },
                text = {
                    Text("Type: \nLatitude: \nlongitude:")
                },
                onDismissRequest = {
                },
                confirmButton = {
                    Button(
                        onClick = {
                            popup = null
                        }
                    ) {
                        Text("Book")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            popup = null
                        }
                    ) {
                        Text("Back")
                    }
                }
            )
        }

        locationModel.getLocationLive().observe(this) {
            location = it
        }

        locationModel.getZoomLive().observe(this) {
            zoom = it
        }

        Column {
            TextField(value = search, placeholder = {
                Text("Search")
            }, onValueChange = {
                search = it
                locationModel.clearLandmarks()
                loadLandmarks(it)
                loadLandmarksFromWeb(it)
                locationModel.calculateNotificationLandmark()
                dispatchNotification()
            })

            Surface(modifier) {
                MapLibre(
                    modifier = Modifier,
                    styleBuilder = Style.Builder()
                        .fromUri("https://tiles.openfreemap.org/styles/bright"),
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
                        locationModel.zoomIn()
                    }) {
                        Text("+")
                    }
                }
            }
        }
    }

    private fun loadLandmarks(location: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val database = LandmarksDatabase.getDatabase(application)

                database.landmarksDataAccessObject().getAll(location).forEach {
                    locationModel.addLandmark(Landmark(
                        it.name,
                        it.type,
                        it.location,
                        it.latitude,
                        it.longitude,
                        it.rooms,
                        it.meals
                    ))
                }
            }
        }
    }

    private fun loadLandmarksFromWeb(location: String) {
        val url = "http://10.0.2.2:3000/accommodation/all"
        url.httpGet().responseJson { _, _, result ->
            when(result) {
                is com.github.kittinunf.result.Result.Success<*> -> {
                    val jsonArray = result.get().array()

                    for(i in 0 until jsonArray.length()) {
                        val curObj = jsonArray.getJSONObject(i)

                        if (curObj.getString("location") == location) {
                            locationModel.addLandmark(Landmark(
                                curObj.getString("name"),
                                curObj.getString("type"),
                                curObj.getString("location"),
                                curObj.getDouble("latitude"),
                                curObj.getDouble("longitude"),
                                curObj.getInt("rooms"),
                                true
                            ))
                        }
                    }
                }
                is com.github.kittinunf.result.Result.Failure<*> -> {
                    Toast.makeText(this, "ERROR ${result.error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @Composable
    fun AddLocationComposable(modifier: Modifier, navController: NavController) {
        var name by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("") }
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

                TextField(value = location, placeholder = {
                    Text("Location")
                }, onValueChange = {
                    location = it
                })

                TextField(value = rooms.toString(), placeholder = {
                    Text("Rooms")
                }, onValueChange = {
                    rooms = it.toIntOrNull() ?: 0
                })

                Row {
                    Switch(checked = meals, onCheckedChange = {
                        meals = it
                    })

                    Text("Meals Provided")
                }

                Row {
                    Button(onClick = {
                        writeLandmark(Landmark(
                            name,
                            type,
                            location,
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
                        location = landmark.location,
                        latitude = landmark.latitude,
                        longitude = landmark.longitude,
                        rooms = landmark.rooms,
                        meals = landmark.meals
                    )
                )

                locationModel.addLandmark(landmark)
            }
        }

        writeLandmarkToWeb(landmark)
    }

    private fun writeLandmarkToWeb(landmark: Landmark) {
        val url = "http://10.0.2.2:3000/accommodation/create"
        val postData = listOf(
            "name" to landmark.name,
            "type" to landmark.type,
            "location" to landmark.location,
            "rooms" to landmark.rooms,
            "meals" to landmark.meals,
            "longitude" to landmark.longitude.toFloat(),
            "latitude" to landmark.latitude.toFloat()
        )

        url.httpPost(postData).response { _, _, result ->
            when (result) {
                is com.github.kittinunf.result.Result.Success<*> -> {
                    Toast.makeText(this, result.get().decodeToString(), Toast.LENGTH_LONG).show()
                }
                is com.github.kittinunf.result.Result.Failure<*> -> {
                    Toast.makeText(this, "ERROR ${result.error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}