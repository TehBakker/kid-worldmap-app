package com.example.worldkids.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.worldkids.theme.Cream

@Composable
fun WorldKidsApp(
    tvMode: Boolean,
    onTvModeChange: (Boolean) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Cream) {
        HomeScreen(
            viewModel = viewModel,
            tvMode = tvMode,
            onTvModeChange = onTvModeChange
        )
    }
}
