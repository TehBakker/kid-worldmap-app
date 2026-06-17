package com.example.worldkids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.worldkids.data.Country

@Composable
fun ImageGallery(
    country: Country,
    modifier: Modifier = Modifier,
    imageHeight: Int = 120
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(country.images) { url ->
            GalleryImage(
                url = url,
                countryName = country.nameFr,
                fallbackColor = parseColor(country.colorHex),
                height = imageHeight
            )
        }
        if (country.images.isEmpty()) {
            item {
                PlaceholderImage(country.nameFr, parseColor(country.colorHex), imageHeight)
            }
        }
    }
}

@Composable
private fun GalleryImage(
    url: String,
    countryName: String,
    fallbackColor: Color,
    height: Int
) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = countryName,
        modifier = Modifier
            .width((height * 1.4f).dp)
            .height(height.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
        loading = { PlaceholderImage(countryName, fallbackColor, height) },
        error = { PlaceholderImage(countryName, fallbackColor, height) }
    )
}

@Composable
private fun PlaceholderImage(name: String, color: Color, height: Int) {
    Box(
        modifier = Modifier
            .width((height * 1.4f).dp)
            .height(height.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(listOf(color, color.copy(alpha = 0.6f)))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

private fun parseColor(hex: String): Color {
    val cleaned = hex.removePrefix("#")
    return Color(android.graphics.Color.parseColor("#$cleaned"))
}
