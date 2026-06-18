package com.example.worldkids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.worldkids.theme.OceanLight

@Composable
fun TvModeBanner(modifier: Modifier = Modifier) {
    Text(
        text = "📺 Astuce : active le Cast puis pose le téléphone à l'horizontale.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxWidth()
            .background(OceanLight.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    )
}

@Composable
fun CastHelpBanner(modifier: Modifier = Modifier) {
    Text(
        text = "Active Smart View / Cast, choisis la TV LG, puis repasse l'app en plein écran.",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF37474F),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE3F2FD), RoundedCornerShape(10.dp))
            .padding(10.dp)
    )
}
