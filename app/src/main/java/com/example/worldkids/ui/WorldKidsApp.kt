package com.example.worldkids.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.worldkids.theme.CreamBackground

@Composable
fun WorldKidsApp(
    tvMode: Boolean,
    onTvModeChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CreamBackground
    ) {
        HomeScreen(tvMode = tvMode, onTvModeChange = onTvModeChange)
    }
}
