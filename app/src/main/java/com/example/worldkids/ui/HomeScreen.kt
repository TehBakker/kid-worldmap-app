package com.example.worldkids.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.worldkids.data.Country
import com.example.worldkids.data.DemoData
import com.example.worldkids.data.Match

@Composable
fun HomeScreen(
    tvMode: Boolean,
    onTvModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMatch by rememberSaveable { mutableStateOf<Match?>(null) }
    var selectedCountry by rememberSaveable { mutableStateOf<Country?>(null) }
    var showConfetti by rememberSaveable { mutableStateOf(false) }

    val highlightedIds: Set<String> = buildSet {
        selectedMatch?.let {
            add(it.countryAId)
            add(it.countryBId)
        }
        selectedCountry?.let { add(it.id) }
    }

    val focusId = selectedCountry?.id

    LaunchedEffect(showConfetti) {
        if (showConfetti) {
            kotlinx.coroutines.delay(1200)
            showConfetti = false
        }
    }

    val isWide = LocalConfiguration.current.screenWidthDp >= 700
    val scrollState = rememberScrollState()
    val padding = if (tvMode) 20.dp else 12.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(padding)
    ) {
        HeaderRow(tvMode = tvMode, onTvModeChange = onTvModeChange)

        Spacer(Modifier.height(if (tvMode) 16.dp else 8.dp))
        if (tvMode) TvModeBanner() else CastHelpBanner()
        Spacer(Modifier.height(12.dp))

        MatchSelector(
            selectedMatchId = selectedMatch?.id,
            onMatchSelected = { match ->
                selectedMatch = match
                selectedCountry = null
                showConfetti = true
            },
            tvMode = tvMode
        )

        Spacer(Modifier.height(12.dp))
        CountrySearch(
            onCountrySelected = { country ->
                selectedCountry = country
                selectedMatch = null
            },
            tvMode = tvMode
        )

        Spacer(Modifier.height(16.dp))

        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WorldMapScreen(
                    highlightedCountryIds = highlightedIds,
                    selectedMatch = selectedMatch,
                    focusCountryId = focusId,
                    onCountryTapped = { country ->
                        selectedCountry = country
                        selectedMatch = null
                    },
                    showConfetti = showConfetti,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(if (tvMode) 420.dp else 320.dp),
                    tvMode = tvMode
                )
                CountryDetailPanel(
                    country = selectedCountry,
                    selectedMatch = if (selectedCountry == null) selectedMatch else null,
                    detailCountryId = selectedCountry?.id,
                    onCountryFromMatchClick = { id ->
                        selectedCountry = DemoData.countryById(id)
                    },
                    modifier = Modifier.weight(0.8f),
                    tvMode = tvMode
                )
            }
        } else {
            WorldMapScreen(
                highlightedCountryIds = highlightedIds,
                selectedMatch = selectedMatch,
                focusCountryId = focusId,
                onCountryTapped = { country ->
                    selectedCountry = country
                    selectedMatch = null
                },
                showConfetti = showConfetti,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (tvMode) 360.dp else 280.dp),
                tvMode = tvMode
            )
            Spacer(Modifier.height(12.dp))
            CountryDetailPanel(
                country = selectedCountry,
                selectedMatch = if (selectedCountry == null) selectedMatch else null,
                detailCountryId = selectedCountry?.id,
                onCountryFromMatchClick = { id ->
                    selectedCountry = DemoData.countryById(id)
                },
                tvMode = tvMode
            )
        }
    }
}

@Composable
private fun HeaderRow(tvMode: Boolean, onTvModeChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "World Kids Explorer", style = MaterialTheme.typography.headlineLarge)
            Text(
                text = "Explore les pays de la Coupe du monde",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        CastButton(tvMode = tvMode)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Mode TV", style = MaterialTheme.typography.labelLarge)
            Switch(checked = tvMode, onCheckedChange = onTvModeChange)
        }
    }
}
