package com.example.worldkids.utils

import com.example.worldkids.data.Country
import kotlin.math.max
import kotlin.math.min

data class MapCamera(
    val scale: Float = 1f,
    val centerX: Float = 0.5f,
    val centerY: Float = 0.5f
)

object MapCameraUtils {

    private const val MIN_SCALE = 1f
    const val MAX_SCALE = 7f
    private const val PADDING = 0.12f

    fun computeCamera(
        countries: List<Country>,
        focusIds: Set<String>,
        selectedMatchIds: Set<String>
    ): MapCamera {
        val ids = focusIds + selectedMatchIds
        if (ids.isEmpty()) return MapCamera()

        val points = countries.filter { it.id in ids }
        if (points.isEmpty()) return MapCamera()

        val minX = points.minOf { it.mapX }
        val maxX = points.maxOf { it.mapX }
        val minY = points.minOf { it.mapY }
        val maxY = points.maxOf { it.mapY }

        return cameraForBounds(minX, minY, maxX, maxY, maxScale = 3.2f)
    }

    /**
     * Caméra cadrant une boîte englobante normalisée, avec marge.
     * [maxScale] borne le zoom (un petit pays ne doit pas être démesurément agrandi).
     */
    fun cameraForBounds(
        minX: Float,
        minY: Float,
        maxX: Float,
        maxY: Float,
        padding: Float = PADDING,
        maxScale: Float = MAX_SCALE
    ): MapCamera {
        val spanX = max(maxX - minX, 0.02f) + padding
        val spanY = max(maxY - minY, 0.02f) + padding
        val scale = min(maxScale, max(MIN_SCALE, 1f / max(spanX, spanY) * 0.85f))
        return MapCamera(
            scale = scale,
            centerX = (minX + maxX) / 2f,
            centerY = (minY + maxY) / 2f
        )
    }

    /** Convertit un tap écran en coordonnées carte normalisées 0..1 */
    fun screenToMap(
        tapX: Float,
        tapY: Float,
        width: Float,
        height: Float,
        camera: MapCamera
    ): Pair<Float, Float> {
        val cx = width / 2f
        val cy = height / 2f
        val mapX = ((tapX - cx) / camera.scale + camera.centerX * width) / width
        val mapY = ((tapY - cy) / camera.scale + camera.centerY * height) / height
        return mapX to mapY
    }

    fun findCountryAt(
        mapX: Float,
        mapY: Float,
        countries: List<Country>,
        tapRadiusNormalized: Float = 0.045f
    ): Country? {
        return countries
            .map { country ->
                val dx = country.mapX - mapX
                val dy = country.mapY - mapY
                country to (dx * dx + dy * dy)
            }
            .filter { it.second <= tapRadiusNormalized * tapRadiusNormalized }
            .minByOrNull { it.second }
            ?.first
    }
}
