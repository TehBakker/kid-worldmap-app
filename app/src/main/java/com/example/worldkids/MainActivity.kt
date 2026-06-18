package com.example.worldkids

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.example.worldkids.theme.WorldKidsTheme
import com.example.worldkids.ui.WorldKidsApp
import com.example.worldkids.ui.setImmersiveMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            var tvMode by rememberSaveable { mutableStateOf(false) }

            androidx.compose.runtime.LaunchedEffect(tvMode) {
                setImmersiveMode(this@MainActivity, tvMode)
                requestedOrientation = if (tvMode) {
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }

            WorldKidsTheme(tvMode = tvMode) {
                WorldKidsApp(
                    tvMode = tvMode,
                    onTvModeChange = { tvMode = it }
                )
            }
        }
    }
}
