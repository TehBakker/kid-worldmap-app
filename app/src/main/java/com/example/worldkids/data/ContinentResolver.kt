package com.example.worldkids.data

/**
 * Corrige le continent utilisé pour la vue « continent » sur la carte.
 * Le JSON source classe parfois toute l'Amérique latine en « Amérique du Nord » :
 * on sépare alors Nord et Sud par position géographique sur la carte.
 */
object ContinentResolver {

    private const val AMERICAS_MIN_X = 0.08f
    private const val AMERICAS_MAX_X = 0.42f
    private const val AMERICAS_MIN_Y = 0.26f
    private const val AMERICAS_MAX_Y = 0.72f
    /** Frontière approximative Amérique centrale / Amérique du Sud (coords normalisées). */
    private const val AMERICAS_SPLIT_Y = 0.455f

    private val EXPLICIT_OVERRIDES: Map<String, String> = mapOf(
        "russia" to "Asie"
    )

    /** Continent pour la vue carte et l'affichage (Nord/Sud séparés). */
    fun mapContinent(country: Country): String {
        EXPLICIT_OVERRIDES[country.id]?.let { return it }
        if (isInAmericas(country)) {
            return if (country.mapY >= AMERICAS_SPLIT_Y) "Amérique du Sud" else "Amérique du Nord"
        }
        return when (country.continent) {
            "Amérique" -> if (country.mapY >= AMERICAS_SPLIT_Y) "Amérique du Sud" else "Amérique du Nord"
            else -> country.continent
        }
    }

    private fun isInAmericas(country: Country): Boolean {
        val label = country.continent
        if (label == "Amérique du Nord" || label == "Amérique du Sud" || label == "Amérique") {
            return true
        }
        return country.mapX in AMERICAS_MIN_X..AMERICAS_MAX_X &&
            country.mapY in AMERICAS_MIN_Y..AMERICAS_MAX_Y
    }
}
