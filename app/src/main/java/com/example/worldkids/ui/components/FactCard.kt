package com.example.worldkids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FactCard(
    fact: String,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.secondary
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "✨ $fact",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
