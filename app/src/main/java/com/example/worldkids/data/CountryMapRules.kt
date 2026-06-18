package com.example.worldkids.data

import android.content.Context
import org.json.JSONObject

/**
 * Règles d'interaction carte : certains territoires trop petits ou enclavés
 * ne sont pas cliquables sur la carte et restent accessibles via la recherche texte.
 *
 * Liste éditable : [map_search_only_countries.json] dans assets.
 */
object CountryMapRules {

    private var searchOnlyIds: Set<String> = emptySet()
    private var minMainlandArea: Float = 0.00015f

    /** Charge la configuration depuis assets (appelé au démarrage par [WorldDataRepository]). */
    fun loadFromAssets(context: Context) {
        val config = loadConfig(context)
        searchOnlyIds = config.searchOnlyIds
        minMainlandArea = config.minMainlandArea
    }

    /** Pays sélectionnable par tap sur la carte (continent ou monde). */
    fun isClickableOnMap(countryId: String, borders: Map<String, CountryShape>): Boolean {
        if (countryId in searchOnlyIds) return false
        val shape = borders[countryId] ?: return false
        val mainland = ShapeUtils.mainlandShape(shape)
        val area = mainland.maxOfOrNull { ShapeUtils.ringArea(it) } ?: 0f
        return area >= minMainlandArea
    }

    /** Ids actuellement en recherche seule (debug / tests). */
    fun searchOnlyCountryIds(): Set<String> = searchOnlyIds

    private data class MapClickConfig(
        val searchOnlyIds: Set<String>,
        val minMainlandArea: Float
    )

    private fun loadConfig(context: Context): MapClickConfig = try {
        val root = JSONObject(
            context.assets.open("map_search_only_countries.json")
                .bufferedReader()
                .readText()
        )
        val ids = buildSet {
            val arr = root.getJSONArray("countryIds")
            for (i in 0 until arr.length()) add(arr.getString(i))
        }
        MapClickConfig(
            searchOnlyIds = ids,
            minMainlandArea = root.optDouble("minMainlandArea", 0.00015).toFloat()
        )
    } catch (_: Exception) {
        MapClickConfig(searchOnlyIds = DEFAULT_SEARCH_ONLY_IDS, minMainlandArea = 0.00015f)
    }

    /** Repli si le fichier JSON est absent ou invalide. */
    private val DEFAULT_SEARCH_ONLY_IDS = setOf(
        "vatican_city_state_(holy_see)",
        "san_marino",
        "monaco",
        "andorra",
        "liechtenstein",
        "luxembourg",
        "malta",
        "wales",
        "scotland",
        "gibraltar",
        "faroe_islands",
        "man_(isle_of)",
        "jersey",
        "guernsey"
    )
}
