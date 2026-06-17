package com.example.worldkids.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.worldkids.data.Country
import com.example.worldkids.data.DemoData
import com.example.worldkids.data.Match
import com.example.worldkids.ui.components.FactCard
import com.example.worldkids.ui.components.FlagBadge
import com.example.worldkids.ui.components.ImageGallery
import com.example.worldkids.theme.AccentOrange
import com.example.worldkids.theme.Lavender

@Composable
fun CountryDetailPanel(
    country: Country?,
    selectedMatch: Match?,
    detailCountryId: String?,
    onCountryFromMatchClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    val scrollState = rememberScrollState()
    val imageHeight = if (tvMode) 160 else 120

    AnimatedVisibility(
        visible = country != null || selectedMatch != null,
        enter = fadeIn() + slideInVertically { it / 3 },
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                when {
                    selectedMatch != null && country == null -> {
                        MatchDetailContent(
                            match = selectedMatch,
                            onCountryClick = onCountryFromMatchClick,
                            tvMode = tvMode
                        )
                    }
                    country != null -> {
                        CountryDetailContent(country = country, imageHeight = imageHeight, tvMode = tvMode)
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchDetailContent(
    match: Match,
    onCountryClick: (String) -> Unit,
    tvMode: Boolean
) {
    val countryA = DemoData.countryById(match.countryAId)
    val countryB = DemoData.countryById(match.countryBId)

    Text(
        text = "Aujourd'hui : ${match.label}",
        style = MaterialTheme.typography.headlineMedium,
        color = AccentOrange
    )
    Spacer(Modifier.height(4.dp))
    Text(text = match.groupOrStage, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
    Spacer(Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        countryA?.let {
            MatchCountryChip(country = it, onClick = { onCountryClick(it.id) }, tvMode = tvMode)
        }
        Text(text = "vs", style = MaterialTheme.typography.headlineLarge)
        countryB?.let {
            MatchCountryChip(country = it, onClick = { onCountryClick(it.id) }, tvMode = tvMode)
        }
    }
    Spacer(Modifier.height(12.dp))
    Text(
        text = "Touche un drapeau pour découvrir le pays !",
        style = MaterialTheme.typography.bodyMedium,
        color = Lavender
    )
}

@Composable
private fun MatchCountryChip(country: Country, onClick: () -> Unit, tvMode: Boolean) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FlagBadge(emoji = country.flagEmoji, fontSize = if (tvMode) 40 else 32)
            Spacer(Modifier.height(4.dp))
            Text(text = country.nameFr, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun CountryDetailContent(country: Country, imageHeight: Int, tvMode: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FlagBadge(emoji = country.flagEmoji, fontSize = if (tvMode) 48 else 36)
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = country.nameFr, style = MaterialTheme.typography.headlineMedium)
            Text(text = country.nameEn, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        }
    }

    Spacer(Modifier.height(8.dp))
    Text(
        text = "🧠 À retenir : ${country.memoryHook}",
        modifier = Modifier
            .fillMaxWidth()
            .background(Lavender.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(Modifier.height(12.dp))
    InfoRow("🏛️ Capitale", country.capital)
    InfoRow("👥 Population", country.population)
    InfoRow("🌍 Continent", country.continent)
    InfoRow("🗣️ Langue", country.mainLanguage)
    InfoRow("💰 Monnaie", country.currency)

    Spacer(Modifier.height(12.dp))
    Text(text = country.kidFactTitle, style = MaterialTheme.typography.titleLarge, color = AccentOrange)
    Spacer(Modifier.height(8.dp))
    country.kidFacts.forEach { fact ->
        FactCard(fact = fact, modifier = Modifier.padding(bottom = 8.dp))
    }

    Spacer(Modifier.height(12.dp))
    Text(text = "Galerie", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    ImageGallery(country = country, imageHeight = imageHeight)
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(text = "$label : ", style = MaterialTheme.typography.labelLarge)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
