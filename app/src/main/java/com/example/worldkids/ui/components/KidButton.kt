package com.example.worldkids.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun KidButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = if (tvMode) 56.dp else 44.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text = text, style = if (tvMode) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelLarge)
    }
}
