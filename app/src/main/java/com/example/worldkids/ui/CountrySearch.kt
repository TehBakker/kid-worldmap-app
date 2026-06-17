package com.example.worldkids.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.worldkids.data.Country
import com.example.worldkids.data.DemoData

@Composable
fun CountrySearch(
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) {
        if (query.isBlank()) emptyList()
        else DemoData.countries.filter {
            it.nameFr.contains(query, ignoreCase = true) ||
                it.nameEn.contains(query, ignoreCase = true)
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Chercher un pays",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cherche un pays…") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        if (results.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                items(results) { country ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                onCountrySelected(country)
                                query = ""
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Text(
                            text = "${country.flagEmoji}  ${country.nameFr}",
                            style = if (tvMode) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}
