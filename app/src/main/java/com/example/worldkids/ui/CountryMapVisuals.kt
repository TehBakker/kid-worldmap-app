package com.example.worldkids.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.example.worldkids.data.CountryShape

/**
 * Outils visuels du planisphère : couleur distincte par pays (famille de teinte par
 * continent) et construction des tracés de frontières en coordonnées normalisées 0..1.
 */
object CountryMapVisuals {

    /** Contour des pays (vert atlas foncé, lisible sur toutes les teintes). */
    val BORDER: Color = Color(0xFF4E6B57)

    /** Pays sans données enrichies / fond neutre. */
    private val NEUTRAL = Color(0xFFB8C2BD)

    private data class Palette(
        val hueMin: Float,
        val hueMax: Float,
        val sat: Float,
        val light: Float
    )

    private fun paletteFor(continent: String): Palette = when (continent) {
        "Afrique" -> Palette(34f, 52f, 0.62f, 0.62f)          // jaunes / oranges chauds
        "Asie" -> Palette(0f, 20f, 0.58f, 0.64f)              // rouges / corail
        "Europe" -> Palette(205f, 235f, 0.52f, 0.62f)         // bleus
        "Amérique du Nord" -> Palette(95f, 140f, 0.45f, 0.60f) // verts
        "Amérique du Sud" -> Palette(150f, 178f, 0.48f, 0.58f) // turquoise / vert
        "Océanie" -> Palette(265f, 300f, 0.45f, 0.66f)        // violets / mauves
        "Antarctique" -> Palette(200f, 210f, 0.12f, 0.86f)    // glace
        else -> Palette(0f, 0f, 0f, 0.72f)
    }

    /** Couleur de remplissage stable et distincte pour un pays donné. */
    fun fillColor(continent: String, id: String): Color {
        val p = paletteFor(continent)
        if (p.sat == 0f && continent !in CONTINENTS) return NEUTRAL
        val h = id.hashCode()
        val r1 = (h and 0xFF) / 255f
        val r2 = ((h ushr 8) and 0xFF) / 255f
        val r3 = ((h ushr 16) and 0xFF) / 255f
        val hue = (p.hueMin + (p.hueMax - p.hueMin) * r1 + 360f) % 360f
        val sat = (p.sat - 0.10f + 0.20f * r2).coerceIn(0.10f, 0.80f)
        val light = (p.light - 0.08f + 0.16f * r3).coerceIn(0.45f, 0.88f)
        return Color.hsl(hue, sat, light)
    }

    private val CONTINENTS = setOf(
        "Afrique", "Asie", "Europe", "Amérique du Nord",
        "Amérique du Sud", "Océanie", "Antarctique"
    )

    /** Construit les tracés (coordonnées normalisées 0..1) pour chaque pays, une seule fois. */
    fun buildPaths(borders: Map<String, CountryShape>): Map<String, List<Path>> =
        borders.mapValues { (_, shape) ->
            shape.map { ring ->
                Path().apply {
                    ring.forEachIndexed { i, pt ->
                        if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                    }
                    close()
                }
            }
        }
}
