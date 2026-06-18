package com.example.worldkids.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.worldkids.data.Country
import com.example.worldkids.data.CountryContent
import com.example.worldkids.data.Match
import com.example.worldkids.theme.CoralOrange
import com.example.worldkids.theme.DividerGray
import com.example.worldkids.theme.NavyBlue
import com.example.worldkids.theme.SkyTeal
import com.example.worldkids.theme.SunYellow
import com.example.worldkids.theme.SurfaceCard
import com.example.worldkids.theme.SurfaceWhite
import com.example.worldkids.theme.TextMain
import com.example.worldkids.theme.TextSub
import com.example.worldkids.ui.components.FlagBadge
import com.example.worldkids.ui.components.ImageGallery

@Composable
fun CountryDetailPanel(
    country: Country?,
    selectedMatch: Match?,
    onCountryFromMatchClick: (String) -> Unit,
    countryById: (String) -> Country?,
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    AnimatedVisibility(
        visible = country != null || selectedMatch != null,
        enter = fadeIn() + slideInVertically { it / 3 },
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                when {
                    selectedMatch != null && country == null -> {
                        MatchDetailContent(
                            match = selectedMatch,
                            countryById = countryById,
                            onCountryClick = onCountryFromMatchClick,
                            tvMode = tvMode
                        )
                    }
                    country != null -> {
                        CountryDetailContent(
                            country = country,
                            imageHeight = if (tvMode) 160 else 120,
                            tvMode = tvMode
                        )
                    }
                }
            }
        }
    }
}

// ── Fiche match ───────────────────────────────────────────────────────────────
@Composable
private fun MatchDetailContent(
    match: Match,
    countryById: (String) -> Country?,
    onCountryClick: (String) -> Unit,
    tvMode: Boolean
) {
    val countryA = countryById(match.countryAId)
    val countryB = countryById(match.countryBId)

    Text(
        text = "Match",
        style = MaterialTheme.typography.labelLarge.copy(color = TextSub)
    )
    Text(
        text = match.label,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = NavyBlue
        )
    )
    Text(
        text = "${match.competition} · ${match.groupOrStage} · ${match.dateLabel}",
        style = MaterialTheme.typography.bodySmall.copy(color = TextSub)
    )

    Spacer(Modifier.height(16.dp))
    HorizontalDivider(color = DividerGray)
    Spacer(Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        countryA?.let {
            MatchCountryCard(country = it, onClick = { onCountryClick(it.id) }, tvMode = tvMode)
        }
        Text(
            text = "VS",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = CoralOrange
            )
        )
        countryB?.let {
            MatchCountryCard(country = it, onClick = { onCountryClick(it.id) }, tvMode = tvMode)
        }
    }

    Spacer(Modifier.height(16.dp))
    Text(
        text = "Touche un pays pour en savoir plus !",
        style = MaterialTheme.typography.bodyMedium.copy(color = SkyTeal),
        modifier = Modifier
            .fillMaxWidth()
            .background(SkyTeal.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    )
}

@Composable
private fun MatchCountryCard(country: Country, onClick: () -> Unit, tvMode: Boolean) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FlagBadge(emoji = country.flagEmoji, fontSize = if (tvMode) 44 else 36)
            Text(
                text = country.nameFr,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextMain
                )
            )
        }
    }
}

// ── Fiche pays ────────────────────────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CountryDetailContent(country: Country, imageHeight: Int, tvMode: Boolean) {
    // En-tête : drapeau + nom
    Row(verticalAlignment = Alignment.CenterVertically) {
        FlagBadge(emoji = country.flagEmoji, fontSize = if (tvMode) 52 else 42)
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = country.nameFr,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
            )
            Text(
                text = country.nameEn,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSub)
            )
            if (country.isWorldCup2026) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "⚽ CDM 2026 · Poule ${country.worldCup2026Group ?: "?"}",
                    style = MaterialTheme.typography.labelLarge.copy(color = CoralOrange),
                    modifier = Modifier
                        .background(CoralOrange.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }

    Spacer(Modifier.height(14.dp))
    HorizontalDivider(color = DividerGray)
    Spacer(Modifier.height(14.dp))

    // Infos clés
    InfoGrid(country = country)

    Spacer(Modifier.height(14.dp))

    // À retenir
    Text(
        text = "🧠 À retenir",
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = TextMain
        )
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = CountryContent.displayMemoryHook(country),
        style = MaterialTheme.typography.bodyMedium.copy(color = TextMain),
        modifier = Modifier
            .fillMaxWidth()
            .background(SunYellow.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    )

    Spacer(Modifier.height(14.dp))

    // Faits
    Text(
        text = CountryContent.displayTitle(country),
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = CoralOrange
        )
    )
    Spacer(Modifier.height(8.dp))
    CountryContent.displayFacts(country).take(3).forEach { fact ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .background(SurfaceCard, RoundedCornerShape(10.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(text = "✦", style = MaterialTheme.typography.bodyMedium.copy(color = SkyTeal))
            Text(
                text = fact,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextMain)
            )
        }
    }

    // Galerie
    if (country.images.isNotEmpty()) {
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Galerie",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextMain
            )
        )
        Spacer(Modifier.height(8.dp))
        ImageGallery(country = country, imageHeight = imageHeight)
    }
}

@Composable
private fun InfoGrid(country: Country) {
    val titles = CountryContent.worldCupTitles(country)
    val items = buildList {
        add("🏛️" to Pair("Capitale", country.capital))
        add("👥" to Pair("Population", country.population))
        add("🌍" to Pair("Continent", country.continent))
        add("🗣️" to Pair("Langue", country.mainLanguage))
        add("💰" to Pair("Monnaie", country.currency))
        if (titles > 0) {
            add("🏆" to Pair("Coupes du monde", "$titles fois championne"))
        } else if (country.isWorldCup2026) {
            add("🏆" to Pair("Coupes du monde", "Pas encore gagnée"))
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { (icon, pair) ->
            val (label, value) = pair
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = icon, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextSub
                    ),
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMain)
                )
            }
        }
    }
}
