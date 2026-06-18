package com.example.worldkids.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.worldkids.data.Country
import com.example.worldkids.data.Match
import com.example.worldkids.data.MatchFilter
import com.example.worldkids.data.WorldCupGroup

@Composable
fun WorldCupTab(
    competition: String,
    groups: List<WorldCupGroup>,
    matches: List<Match>,
    selectedGroupId: String?,
    selectedMatchId: String?,
    matchFilter: MatchFilter,
    onFilterChange: (MatchFilter) -> Unit,
    onGroupSelected: (String) -> Unit,
    onCountrySelected: (Country) -> Unit,
    onMatchSelected: (Match) -> Unit,
    countryById: (String) -> Country?,
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = competition,
            style = if (tvMode) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Choisis une poule ou un match — alternative à la recherche.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        Text(
            text = "Poules",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        WorldCupGroupsPanel(
            groups = groups,
            selectedGroupId = selectedGroupId,
            countryById = countryById,
            onGroupSelected = onGroupSelected,
            onCountrySelected = onCountrySelected,
            tvMode = tvMode
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Matchs",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        MatchSelector(
            matches = matches,
            selectedMatchId = selectedMatchId,
            matchFilter = matchFilter,
            onFilterChange = onFilterChange,
            onMatchSelected = onMatchSelected,
            countryById = countryById,
            showTitle = false,
            tvMode = tvMode
        )
    }
}
