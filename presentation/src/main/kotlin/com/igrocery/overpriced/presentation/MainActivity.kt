package com.igrocery.overpriced.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresPermission
import androidx.core.view.WindowCompat
import com.google.android.gms.maps.MapsInitializer
import com.igrocery.overpriced.shared.Logger
import dagger.hilt.android.AndroidEntryPoint

@Suppress("unused")
private val log = Logger { }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // always draw fullscreen, allocate spaces for system bars manually
        // this allows better control over the system insets
        WindowCompat.setDecorFitsSystemWindows(window, false)

        MapsInitializer.initialize(
            applicationContext,
            MapsInitializer.Renderer.LATEST
        ) { renderer ->
            when (renderer) {
                MapsInitializer.Renderer.LATEST -> log.debug("The latest version of the renderer is used.")
                MapsInitializer.Renderer.LEGACY -> log.debug("The legacy version of the renderer is used.")
            }
        }

        setContent {
            App()
        }
    }
}
