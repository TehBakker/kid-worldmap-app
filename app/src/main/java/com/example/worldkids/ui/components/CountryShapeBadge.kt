package com.example.worldkids.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.worldkids.data.Country
import com.example.worldkids.data.CountryShape
import com.example.worldkids.data.GeoBounds
import com.example.worldkids.data.ShapeUtils
import com.example.worldkids.theme.NavyBlue
import com.example.worldkids.theme.SurfaceCard
import kotlin.math.min

/**
 * Petit bloc arrondi affichant la silhouette d'un pays, conçu pour s'aligner
 * visuellement avec le drapeau dans l'en-tête de la fiche pays.
 *
 * Source des formes (par ordre de priorité) :
 *  1. [Country.shapePathData] (réservé pour de futurs tracés dédiés)
 *  2. [shape] = vraies frontières (country_borders.json)
 *  3. fallback : forme abstraite arrondie avec les initiales du pays.
 */
@Composable
fun CountryShapeBadge(
    country: Country,
    shape: CountryShape?,
    modifier: Modifier = Modifier,
    sizeDp: Int = 56,
    fillColor: Color = NavyBlue,
    backgroundColor: Color = SurfaceCard
) {
    val displayShape = ShapeUtils.shapeForBadge(shape)
    val hasShape = displayShape != null && displayShape.any { it.size >= 3 }
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(RoundedCornerShape(if (sizeDp <= 48) 10.dp else 14.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (hasShape) {
            ShapeCanvas(shape = displayShape!!, fillColor = fillColor, sizeDp = sizeDp)
        } else {
            InitialsFallback(country = country, color = fillColor, sizeDp = sizeDp)
        }
    }
}

@Composable
private fun ShapeCanvas(shape: CountryShape, fillColor: Color, sizeDp: Int) {
    val canvasDp = (sizeDp * 0.72f).dp
    val bounds = GeoBounds.of(shape) ?: return
    Canvas(modifier = Modifier.size(canvasDp)) {
        val padding = size.minDimension * 0.12f
        val availW = size.width - padding * 2f
        val availH = size.height - padding * 2f
        val bw = (bounds.maxX - bounds.minX).coerceAtLeast(1e-4f)
        val bh = (bounds.maxY - bounds.minY).coerceAtLeast(1e-4f)
        val scale = min(availW / bw, availH / bh)
        val drawW = bw * scale
        val drawH = bh * scale
        val offX = (size.width - drawW) / 2f
        val offY = (size.height - drawH) / 2f

        val path = Path()
        shape.forEach { ring ->
            if (ring.size < 2) return@forEach
            ring.forEachIndexed { i, p ->
                val x = offX + (p.x - bounds.minX) * scale
                val y = offY + (p.y - bounds.minY) * scale
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
        }
        drawPath(path = path, color = fillColor.copy(alpha = 0.85f))
        drawPath(path = path, color = fillColor, style = Stroke(width = 1.5f))
    }
}

@Composable
private fun InitialsFallback(country: Country, color: Color, sizeDp: Int) {
    Box(
        modifier = Modifier
            .size((sizeDp * 0.66f).dp)
            .clip(RoundedCornerShape(percent = 45))
            .background(color.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = country.initials(),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = (sizeDp * 0.28f).sp,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

private fun Country.initials(): String {
    val words = nameFr.split(' ', '-').filter { it.isNotBlank() }
    return when {
        words.size >= 2 -> (words[0].take(1) + words[1].take(1)).uppercase()
        nameFr.length >= 2 -> nameFr.take(2).uppercase()
        else -> nameFr.uppercase()
    }
}
