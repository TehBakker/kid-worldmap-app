package com.example.worldkids.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.worldkids.data.Country
import com.example.worldkids.data.CountryContent
import com.example.worldkids.data.CountryImage

@Composable
fun ImageGallery(
    country: Country,
    modifier: Modifier = Modifier,
    imageHeight: Int = 120
) {
    val images = CountryContent.gallery(country)
    val fallbackColor = parseColor(country.colorHex)
    var fullscreen by remember(country.id) { mutableStateOf<CountryImage?>(null) }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        if (images.isEmpty()) {
            item {
                PlaceholderImage(country.nameFr, fallbackColor, imageHeight)
            }
        } else {
            items(images) { image ->
                GalleryThumbnail(
                    image = image,
                    fallbackColor = fallbackColor,
                    height = imageHeight,
                    onClick = { fullscreen = image }
                )
            }
        }
    }

    fullscreen?.let { image ->
        FullscreenImageViewer(
            image = image,
            fallbackColor = fallbackColor,
            onClose = { fullscreen = null }
        )
    }
}

@Composable
private fun GalleryThumbnail(
    image: CountryImage,
    fallbackColor: Color,
    height: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width((height * 1.4f).dp)
            .height(height.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        SubcomposeAsyncImage(
            model = image.imageUrl,
            contentDescription = image.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = { PlaceholderImage(image.title, fallbackColor, height) },
            error = { PlaceholderImage(image.title, fallbackColor, height) }
        )
        // Bandeau titre lisible en bas de la vignette
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                    )
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = image.title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

/** Visionneuse plein écran : fond sombre, légende en bas, bouton fermer + retour Android. */
@Composable
private fun FullscreenImageViewer(
    image: CountryImage,
    fallbackColor: Color,
    onClose: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BackHandler(enabled = true, onBack = onClose)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = image.imageUrl,
                contentDescription = image.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentScale = ContentScale.Fit,
                loading = { FullscreenFallback(image.title, fallbackColor) },
                error = { FullscreenFallback(image.title, fallbackColor) }
            )

            // Bouton fermer clair
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
                    .clickable(onClick = onClose)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Fermer",
                    tint = Color.White
                )
            }

            // Légende en bas
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = image.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                image.caption?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                image.sourceLabel?.let {
                    Text(
                        text = "Source : $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FullscreenFallback(name: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(color, color.copy(alpha = 0.6f)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.BrokenImage,
                contentDescription = null,
                tint = Color.White
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun parseColor(hex: String): Color {
    val cleaned = hex.removePrefix("#")
    return Color(android.graphics.Color.parseColor("#$cleaned"))
}
