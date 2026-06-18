package com.example.worldkids.data

/** Point en coordonnées normalisées 0..1 (projection équirectangulaire, alignée sur mapX/mapY). */
data class GeoPoint(val x: Float, val y: Float)

/** Forme d'un pays : liste d'anneaux extérieurs (un pays peut avoir plusieurs morceaux/îles). */
typealias CountryShape = List<List<GeoPoint>>

/** Boîte englobante normalisée d'une forme de pays. */
data class GeoBounds(
    val minX: Float,
    val minY: Float,
    val maxX: Float,
    val maxY: Float
) {
    val centerX: Float get() = (minX + maxX) / 2f
    val centerY: Float get() = (minY + maxY) / 2f

    companion object {
        fun of(shape: CountryShape): GeoBounds? {
            var minX = Float.MAX_VALUE
            var minY = Float.MAX_VALUE
            var maxX = -Float.MAX_VALUE
            var maxY = -Float.MAX_VALUE
            var found = false
            shape.forEach { ring ->
                ring.forEach { p ->
                    found = true
                    if (p.x < minX) minX = p.x
                    if (p.y < minY) minY = p.y
                    if (p.x > maxX) maxX = p.x
                    if (p.y > maxY) maxY = p.y
                }
            }
            return if (found) GeoBounds(minX, minY, maxX, maxY) else null
        }
    }
}

object ShapeUtils {

    /** Surface normalisée d'un anneau (boîte englobante). */
    fun ringArea(ring: List<GeoPoint>): Float {
        val b = GeoBounds.of(listOf(ring)) ?: return 0f
        return (b.maxX - b.minX) * (b.maxY - b.minY)
    }

    /**
     * Retient uniquement la masse terrestre principale (ex. France métropolitaine,
     * sans Guyane / DOM-TOM). Même logique que le cadrage carte dans [WorldMapScreen].
     */
    fun mainlandShape(shape: CountryShape): CountryShape {
        if (shape.isEmpty()) return shape
        val main = shape.maxByOrNull { ringArea(it) } ?: return shape
        return listOf(main)
    }

    /** Forme adaptée au badge fiche pays : masse principale, ou forme complète si unique. */
    fun shapeForBadge(shape: CountryShape?): CountryShape? {
        if (shape == null || shape.isEmpty()) return null
        return if (shape.size == 1) shape else mainlandShape(shape)
    }
}

object GeoUtils {
    /** Test d'appartenance d'un point à une forme (ray casting sur les anneaux). */
    fun contains(shape: CountryShape, x: Float, y: Float): Boolean {
        var inside = false
        shape.forEach { ring ->
            if (ringContains(ring, x, y)) inside = !inside
        }
        return inside
    }

    private fun ringContains(ring: List<GeoPoint>, x: Float, y: Float): Boolean {
        var inside = false
        var j = ring.size - 1
        for (i in ring.indices) {
            val xi = ring[i].x
            val yi = ring[i].y
            val xj = ring[j].x
            val yj = ring[j].y
            val intersect = (yi > y) != (yj > y) &&
                x < (xj - xi) * (y - yi) / (yj - yi) + xi
            if (intersect) inside = !inside
            j = i
        }
        return inside
    }
}
