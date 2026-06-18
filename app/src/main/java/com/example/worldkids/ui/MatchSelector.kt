package com.example.worldkids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.worldkids.data.Country
import com.example.worldkids.data.Match
import com.example.worldkids.data.MatchFilter
import com.example.worldkids.theme.DividerGray
import com.example.worldkids.theme.NavyBlue
import com.example.worldkids.theme.SunYellow
import com.example.worldkids.theme.SurfaceCard
import com.example.worldkids.theme.SurfaceWhite
import com.example.worldkids.theme.TextMain
import com.example.worldkids.theme.TextSub
import com.example.worldkids.ui.components.FlagBadge

@Composable
fun MatchSelector(
    matches: List<Match>,
    selectedMatchId: String?,
    matchFilter: MatchFilter,
    onFilterChange: (MatchFilter) -> Unit,
    onMatchSelected: (Match) -> Unit,
    countryById: (String) -> Country?,
    modifier: Modifier = Modifier,
    showTitle: Boolean = true,
    tvMode: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (showTitle) {
                Text(
                    text = "Matchs à explorer",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextMain
                    )
                )
                Spacer(Modifier.height(10.dp))
            }

            // Filtres compacts
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(MatchFilter.entries.toList()) { filter ->
                    FilterChip(
                        selected = matchFilter == filter,
                        onClick = { onFilterChange(filter) },
                        label = {
                            Text(
                                filter.label,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyBlue,
                            selectedLabelColor = SurfaceWhite
                        )
                    )
                }
            }

            // Carrousel de matchs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(matches) { match ->
                    MatchCard(
                        match = match,
                        selected = match.id == selectedMatchId,
                        countryById = countryById,
                        tvMode = tvMode,
                        onClick = { onMatchSelected(match) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchCard(
    match: Match,
    selected: Boolean,
    countryById: (String) -> Country?,
    tvMode: Boolean,
    onClick: () -> Unit
) {
    val countryA = countryById(match.countryAId)
    val countryB = countryById(match.countryBId)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) NavyBlue else SurfaceCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 6.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .width(if (tvMode) 180.dp else 150.dp)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Drapeaux + VS
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                countryA?.let {
                    FlagBadge(it.flagEmoji, fontSize = if (tvMode) 32 else 26)
                } ?: Text("?", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (selected) SunYellow else TextSub
                    )
                )
                Spacer(Modifier.width(8.dp))
                countryB?.let {
                    FlagBadge(it.flagEmoji, fontSize = if (tvMode) 32 else 26)
                } ?: Text("?", style = MaterialTheme.typography.titleLarge)
            }
            // Noms courts
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = countryA?.nameFr?.take(7) ?: "?",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (selected) SurfaceWhite else TextMain
                    )
                )
                Text(
                    text = "·",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (selected) SurfaceWhite.copy(0.6f) else TextSub
                    )
                )
                Text(
                    text = countryB?.nameFr?.take(7) ?: "?",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (selected) SurfaceWhite else TextMain
                    )
                )
            }
            // Date / phase
            Text(
                text = match.dateLabel,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (selected) SurfaceWhite.copy(0.75f) else TextSub
                )
            )
        }
    }
}
