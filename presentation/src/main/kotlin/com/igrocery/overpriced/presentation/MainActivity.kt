package com.igrocery.overpriced.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresPermission
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.AndroidEntryPoint

@Suppress("unused")
private val log = Logger { }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}
