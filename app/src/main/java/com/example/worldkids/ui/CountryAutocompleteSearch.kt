package com.example.worldkids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.worldkids.data.Country
import com.example.worldkids.theme.DividerGray
import com.example.worldkids.theme.NavyBlue
import com.example.worldkids.theme.SkyTeal
import com.example.worldkids.theme.SunYellow
import com.example.worldkids.theme.SurfaceCard
import com.example.worldkids.theme.SurfaceWhite
import com.example.worldkids.theme.TextMain
import com.example.worldkids.theme.TextMuted
import com.example.worldkids.theme.TextSub

private val POPULAR_COUNTRIES = listOf(
    "france", "bresil", "japon", "maroc", "etats-unis", "argentine",
    "espagne", "allemagne", "portugal", "angleterre", "mexique", "canada"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CountryAutocompleteSearch(
    onCountrySelected: (Country) -> Unit,
    searchCountries: (String) -> List<Country>,
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val suggestions = remember(query) {
        if (query.isBlank()) emptyList() else searchCountries(query).take(8)
    }
    val popularSuggestions = remember {
        POPULAR_COUNTRIES.flatMap { searchCountries(it).take(1) }.take(6)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = null,
                    tint = NavyBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "Explorer un pays",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextMain
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Chercher un pays…",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyBlue,
                    unfocusedBorderColor = DividerGray,
                    focusedLabelColor = NavyBlue
                ),
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = TextSub)
                }
            )

            // Suggestions de recherche
            if (suggestions.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, DividerGray, RoundedCornerShape(12.dp))
                        .background(SurfaceWhite)
                ) {
                    suggestions.forEach { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    focusManager.clearFocus()
                                    onCountrySelected(country)
                                    query = ""
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = country.flagEmoji, style = MaterialTheme.typography.titleLarge)
                            Column {
                                Text(
                                    text = country.nameFr,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = TextMain
                                    )
                                )
                                Text(
                                    text = country.continent,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSub)
                                )
                            }
                        }
                    }
                }
            }

            // Aucun résultat
            if (query.isNotBlank() && suggestions.isEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Aucun pays trouvé pour « $query »",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSub),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Chips pays populaires (quand pas de recherche)
            if (query.isBlank() && popularSuggestions.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Pays populaires",
                    style = MaterialTheme.typography.labelLarge.copy(color = TextSub)
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    popularSuggestions.forEach { country ->
                        CountryChip(
                            country = country,
                            onClick = {
                                focusManager.clearFocus()
                                onCountrySelected(country)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryChip(country: Country, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = country.flagEmoji, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = country.nameFr,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextMain
                )
            )
        }
    }
}
