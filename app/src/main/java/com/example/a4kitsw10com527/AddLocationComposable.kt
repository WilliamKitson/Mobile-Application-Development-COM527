package com.example.a4kitsw10com527

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.navigation.NavController

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

            TextField(value = latitude.toString(), placeholder = {
                Text("Latitude")
            }, onValueChange = {
                latitude = it.toDouble()
            })

            TextField(value = longitude.toString(), placeholder = {
                Text("Longitude")
            }, onValueChange = {
                longitude = it.toDouble()
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

            Button(onClick = {
                navController.navigate("map")
            }) {
                Text("Submit")
            }
        }
    }
}