package com.example.worldkids.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.worldkids.data.DemoData
import com.example.worldkids.data.Match
import com.example.worldkids.ui.components.FlagBadge
import com.example.worldkids.theme.OceanLight

@Composable
fun MatchSelector(
    selectedMatchId: String?,
    onMatchSelected: (Match) -> Unit,
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = "Choisir un match",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(DemoData.demoMatches) { match ->
                val selected = match.id == selectedMatchId
                val countryA = DemoData.countryById(match.countryAId)
                val countryB = DemoData.countryById(match.countryBId)

                Card(
                    onClick = { onMatchSelected(match) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) OceanLight.copy(alpha = 0.5f) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 8.dp else 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            countryA?.let { FlagBadge(it.flagEmoji, fontSize = if (tvMode) 32 else 24) }
                            Spacer(Modifier.width(6.dp))
                            Text("vs", style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.width(6.dp))
                            countryB?.let { FlagBadge(it.flagEmoji, fontSize = if (tvMode) 32 else 24) }
                        }
                        Text(
                            text = match.label,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        Text(
                            text = match.dateLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
