package com.example.worldkids.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.worldkids.theme.SunYellow

/**
 * Étoiles dorées représentant les titres de Coupe du monde (comme sur les maillots).
 * 1 titre = 1 étoile. N'affiche rien si [titles] <= 0.
 */
@Composable
fun WorldCupStars(
    titles: Int,
    modifier: Modifier = Modifier,
    starSize: Int = 16,
    spacing: Int = 2,
    color: Color = SunYellow
) {
    if (titles <= 0) return
    Row(
        modifier = modifier.semantics {
            contentDescription = "$titles étoile(s) de Coupe du monde"
        },
        horizontalArrangement = Arrangement.spacedBy(spacing.dp)
    ) {
        repeat(titles) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(starSize.dp)
            )
        }
    }
}
