package com.example.a4kitsw10com527

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.a4kitsw10com527.ui.theme._4kitsw10COM527Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher()

        enableEdgeToEdge()
        setContent {
            _4kitsw10COM527Theme {

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
}