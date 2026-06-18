package com.example.worldkids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.worldkids.data.Country
import com.example.worldkids.data.CountryContent
import com.example.worldkids.theme.CoralOrange
import com.example.worldkids.theme.TextSub

/**
 * Bandeau CDM à placer à droite du nom du pays.
 * Ligne 1 : ballon + poule (ou non qualifié). Ligne 2 : étoiles sous le ballon.
 */
@Composable
fun WorldCupBadge(
    country: Country,
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    if (!CountryContent.showWorldCupBadge) return

    val titles = CountryContent.worldCupTitles(country)
    val qualified = country.isWorldCup2026
    val starSize = if (tvMode) 12 else 10

    val description = when {
        qualified -> "Qualifié CDM 2026, poule ${country.worldCup2026Group ?: "?"}" +
            if (titles > 0) ", $titles titre(s)" else ""
        titles > 0 -> "Non qualifié CDM 2026, $titles titre(s) historique(s)"
        else -> "Non qualifié à la Coupe du monde 2026"
    }

    Column(
        modifier = modifier
            .semantics { contentDescription = description }
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (qualified) CoralOrange.copy(alpha = 0.1f)
                else TextSub.copy(alpha = 0.08f)
            )
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (qualified) {
                Text(text = "⚽", fontSize = if (tvMode) 14.sp else 12.sp)
                Text(
                    text = "Poule ${country.worldCup2026Group ?: "?"}",
                    maxLines = 1,
                    softWrap = false,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CoralOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            } else {
                BallNotQualifiedIcon(size = if (tvMode) 16 else 14)
                Text(
                    text = "Non qualifié",
                    maxLines = 1,
                    softWrap = false,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSub,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
        if (titles > 0) {
            WorldCupStars(titles = titles, starSize = starSize, spacing = 2)
        }
    }
}

@Composable
private fun BallNotQualifiedIcon(size: Int) {
    Box(
        modifier = Modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "⚽", fontSize = (size * 0.85f).sp)
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null,
            tint = Color(0xFFE53935),
            modifier = Modifier
                .size((size * 0.7f).dp)
                .align(Alignment.Center)
        )
    }
}
