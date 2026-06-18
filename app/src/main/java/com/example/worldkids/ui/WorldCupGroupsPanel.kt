package com.example.worldkids.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.worldkids.data.Country
import com.example.worldkids.data.WorldCupGroup
import com.example.worldkids.theme.OceanLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorldCupGroupsPanel(
    groups: List<WorldCupGroup>,
    selectedGroupId: String?,
    countryById: (String) -> Country?,
    onGroupSelected: (String) -> Unit,
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    // Column simple (pas de LazyVerticalGrid) — évite le crash dans un parent scrollable
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        groups.forEach { group ->
            GroupCard(
                group = group,
                selected = group.id == selectedGroupId,
                countryById = countryById,
                onGroupClick = { onGroupSelected(group.id) },
                onCountryClick = onCountrySelected,
                tvMode = tvMode
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GroupCard(
    group: WorldCupGroup,
    selected: Boolean,
    countryById: (String) -> Country?,
    onGroupClick: () -> Unit,
    onCountryClick: (Country) -> Unit,
    tvMode: Boolean
) {
    Card(
        onClick = onGroupClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) OceanLight.copy(alpha = 0.45f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 6.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = group.label,
                style = if (tvMode) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                group.teamIds.forEach { teamId ->
                    val country = countryById(teamId)
                    if (country != null) {
                        FilterChip(
                            selected = false,
                            onClick = { onCountryClick(country) },
                            label = {
                                Text(
                                    text = "${country.flagEmoji} ${country.nameFr}",
                                    style = if (tvMode) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    } else {
                        Text(
                            text = "🏳️ $teamId",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}
