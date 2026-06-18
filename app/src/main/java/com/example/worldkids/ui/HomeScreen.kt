package com.example.worldkids.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.worldkids.data.MainTab
import com.example.worldkids.theme.Cream
import com.example.worldkids.theme.DividerGray
import com.example.worldkids.theme.NavyBlue
import com.example.worldkids.theme.SkyTeal
import com.example.worldkids.theme.SunYellow
import com.example.worldkids.theme.SurfaceWhite
import com.example.worldkids.theme.TextMain
import com.example.worldkids.theme.TextSub

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    tvMode: Boolean,
    onTvModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isWide = LocalConfiguration.current.screenWidthDp >= 680 ||
        (tvMode && LocalConfiguration.current.screenWidthDp > LocalConfiguration.current.screenHeightDp)
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val mapHeight = if (tvMode) screenHeight * 0.42f else screenHeight * 0.34f

    LaunchedEffect(viewModel.showConfetti) {
        if (viewModel.showConfetti) {
            kotlinx.coroutines.delay(1200)
            viewModel.clearConfetti()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Cream)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // ── Top Bar ─────────────────────────────────────────────────────────────
        TopBar(tvMode = tvMode, onTvModeChange = onTvModeChange)

        // ── Contenu scrollable ───────────────────────────────────────────────────
        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Colonne gauche : recherche/matchs (repliable) au-dessus de la carte
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExplorerSection(viewModel = viewModel, tvMode = tvMode, isWide = isWide)
                    WorldMapSection(viewModel = viewModel, mapHeight = mapHeight, tvMode = tvMode)
                }
                // Colonne droite : fiche pays
                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    CountryDetailPanel(
                        country = viewModel.selectedCountry,
                        selectedMatch = if (viewModel.selectedCountry == null) viewModel.selectedMatch else null,
                        onCountryFromMatchClick = viewModel::selectCountryById,
                        countryById = viewModel::countryById,
                        tvMode = tvMode,
                        borders = viewModel.borders
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExplorerSection(viewModel = viewModel, tvMode = tvMode, isWide = isWide)
                WorldMapSection(viewModel = viewModel, mapHeight = mapHeight, tvMode = tvMode)
                AnimatedVisibility(
                    visible = viewModel.selectedCountry != null || viewModel.selectedMatch != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CountryDetailPanel(
                        country = viewModel.selectedCountry,
                        selectedMatch = if (viewModel.selectedCountry == null) viewModel.selectedMatch else null,
                        onCountryFromMatchClick = viewModel::selectCountryById,
                        countryById = viewModel::countryById,
                        tvMode = tvMode,
                        borders = viewModel.borders
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Top Bar ──────────────────────────────────────────────────────────────────
@Composable
private fun TopBar(tvMode: Boolean, onTvModeChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "World Kids Explorer",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
            )
            Text(
                text = "Atlas ludique · Coupe du monde 2026",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSub)
            )
        }
        CastButton(tvMode = tvMode)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "TV",
                style = MaterialTheme.typography.labelMedium.copy(color = TextSub)
            )
            Switch(
                checked = tvMode,
                onCheckedChange = onTvModeChange,
                modifier = Modifier.size(width = 44.dp, height = 24.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SurfaceWhite,
                    checkedTrackColor = NavyBlue,
                    uncheckedThumbColor = TextSub,
                    uncheckedTrackColor = DividerGray
                )
            )
        }
    }
    if (tvMode) {
        TvModeBanner(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}

// ── Section carte ─────────────────────────────────────────────────────────────
@Composable
private fun WorldMapSection(viewModel: HomeViewModel, mapHeight: androidx.compose.ui.unit.Dp, tvMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        WorldMapScreen(
            countries = viewModel.countries,
            borders = viewModel.borders,
            highlightedCountryIds = viewModel.highlightedCountryIds,
            selectedMatch = viewModel.selectedMatch,
            focusCountryId = viewModel.focusCountryId,
            focusedContinent = viewModel.focusedContinent,
            countryExtruded = viewModel.countryExtruded,
            onCountryTapped = viewModel::selectCountry,
            onBackToContinent = viewModel::backToContinent,
            onBackToWorld = viewModel::backToWorld,
            showConfetti = viewModel.showConfetti,
            modifier = Modifier
                .fillMaxWidth()
                .height(mapHeight),
            tvMode = tvMode,
            countryById = viewModel::countryById
        )
    }
}

// ── Section recherche / Coupe du monde repliable (au-dessus de la carte) ──────
@Composable
private fun ExplorerSection(viewModel: HomeViewModel, tvMode: Boolean, isWide: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                ModeSegmentedControl(viewModel = viewModel, tvMode = tvMode, isWide = isWide)
            }
            CollapseToggle(
                expanded = viewModel.explorerExpanded,
                onClick = viewModel::toggleExplorer
            )
        }
        AnimatedVisibility(
            visible = viewModel.explorerExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            TabContent(viewModel = viewModel, tvMode = tvMode)
        }
    }
}

@Composable
private fun CollapseToggle(expanded: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Icon(
            imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
            contentDescription = if (expanded) "Replier la recherche" else "Déplier la recherche",
            tint = NavyBlue,
            modifier = Modifier.padding(10.dp).size(22.dp)
        )
    }
}

// ── Segmented control Explorer / Coupe du monde ──────────────────────────────
@Composable
private fun ModeSegmentedControl(viewModel: HomeViewModel, tvMode: Boolean, isWide: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        MainTab.entries.forEach { tab ->
            val selected = viewModel.mainTab == tab
            val icon = if (tab == MainTab.EXPLORE) Icons.Rounded.Public else Icons.Rounded.SportsSoccer
            // Libellé court "CDM" sur mobile pour éviter le retour à la ligne.
            val label = if (tab == MainTab.WORLD_CUP && !isWide) "CDM" else tab.label
            Card(
                onClick = { viewModel.onMainTabChange(tab) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) NavyBlue else SurfaceWhite
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) SurfaceWhite else TextSub,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = label,
                        maxLines = 1,
                        softWrap = false,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (selected) SurfaceWhite else TextSub,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

// ── Contenu de l'onglet ───────────────────────────────────────────────────────
@Composable
private fun TabContent(viewModel: HomeViewModel, tvMode: Boolean) {
    when (viewModel.mainTab) {
        MainTab.EXPLORE -> {
            CountryAutocompleteSearch(
                onCountrySelected = viewModel::selectCountry,
                searchCountries = viewModel::searchCountries,
                tvMode = tvMode
            )
        }
        MainTab.WORLD_CUP -> {
            WorldCupTab(
                competition = viewModel.worldCupCompetition,
                groups = viewModel.worldCupGroups,
                matches = viewModel.filteredMatches,
                selectedGroupId = viewModel.selectedGroupId,
                selectedMatchId = viewModel.selectedMatch?.id,
                matchFilter = viewModel.matchFilter,
                onFilterChange = viewModel::onMatchFilterChange,
                onGroupSelected = viewModel::selectGroup,
                onCountrySelected = viewModel::selectCountry,
                onMatchSelected = viewModel::selectMatch,
                countryById = viewModel::countryById,
                tvMode = tvMode
            )
        }
    }
}
