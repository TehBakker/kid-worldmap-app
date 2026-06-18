package com.example.worldkids.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Charge tous les pays du monde + poules CDM 2026 + fiches enrichies optionnelles.
 * Régénérer les JSON : python tools/generate_world_data.py
 */
class WorldDataRepository(context: Context) {

    val countries: List<Country> = loadCountries(context)
    val matches: List<Match> = loadMatches(context)
    val worldCupGroups: WorldCupGroupsData = loadWorldCupGroups(context)

    /** Frontières réelles des pays (clé = id du pays). */
    val borders: Map<String, CountryShape> = loadBorders(context)

    fun countryById(id: String): Country? = countries.find { it.id == id }

    fun countriesByIds(ids: List<String>): List<Country> =
        ids.mapNotNull { countryById(it) }

    fun searchCountries(query: String, limit: Int = 12): List<Country> {
        if (query.isBlank()) return emptyList()
        val q = query.trim()
        return countries
            .filter {
                it.nameFr.contains(q, ignoreCase = true) ||
                    it.nameEn.contains(q, ignoreCase = true) ||
                    it.capital.contains(q, ignoreCase = true)
            }
            .sortedWith(
                compareBy<Country> { !it.nameFr.startsWith(q, ignoreCase = true) }
                    .thenBy { it.nameFr }
            )
            .take(limit)
    }

    fun worldCupCountries(): List<Country> =
        countries.filter { it.isWorldCup2026 }

    fun filterMatches(filter: MatchFilter): List<Match> = when (filter) {
        MatchFilter.ALL -> matches
        MatchFilter.GROUP -> matches.filter { it.phase == "group" }
        MatchFilter.KNOCKOUT -> matches.filter { it.phase == "knockout" }
        MatchFilter.FEATURED -> matches.filter { it.featured }
    }

    companion object {
        fun loadCountries(context: Context): List<Country> {
            val base = parseCountries(
                context.assets.open("world_countries.json").bufferedReader().readText()
            )
            val details = try {
                parseCountryDetails(
                    context.assets.open("country_details.json").bufferedReader().readText()
                )
            } catch (_: Exception) {
                emptyMap()
            }
            return base.map { c -> details[c.id]?.let { c.mergeDetails(it) } ?: c }
        }

        fun loadMatches(context: Context): List<Match> =
            parseMatches(context.assets.open("matches.json").bufferedReader().readText())

        fun loadBorders(context: Context): Map<String, CountryShape> = try {
            parseBorders(
                context.assets.open("country_borders.json").bufferedReader().readText()
            )
        } catch (_: Exception) {
            emptyMap()
        }

        fun parseBorders(json: String): Map<String, CountryShape> {
            val root = JSONObject(json)
            val map = HashMap<String, CountryShape>(root.length())
            root.keys().forEach { id ->
                val ringsArr = root.getJSONArray(id)
                val rings = ArrayList<List<GeoPoint>>(ringsArr.length())
                for (r in 0 until ringsArr.length()) {
                    val ptsArr = ringsArr.getJSONArray(r)
                    val pts = ArrayList<GeoPoint>(ptsArr.length())
                    for (p in 0 until ptsArr.length()) {
                        val pair = ptsArr.getJSONArray(p)
                        pts.add(
                            GeoPoint(
                                pair.getDouble(0).toFloat(),
                                pair.getDouble(1).toFloat()
                            )
                        )
                    }
                    rings.add(pts)
                }
                map[id] = rings
            }
            return map
        }

        fun loadWorldCupGroups(context: Context): WorldCupGroupsData {
            val root = JSONObject(
                context.assets.open("worldcup_2026_groups.json").bufferedReader().readText()
            )
            val groupsArr = root.getJSONArray("groups")
            val groups = buildList {
                for (i in 0 until groupsArr.length()) {
                    val g = groupsArr.getJSONObject(i)
                    add(
                        WorldCupGroup(
                            id = g.getString("id"),
                            label = g.getString("label"),
                            teamIds = g.getStringList("teamIds")
                        )
                    )
                }
            }
            return WorldCupGroupsData(
                competition = root.getString("competition"),
                hostCountries = root.getStringList("hostCountries"),
                groups = groups
            )
        }

        fun parseCountries(json: String): List<Country> {
            val array = JSONArray(json)
            return buildList {
                for (i in 0 until array.length()) {
                    val o = array.getJSONObject(i)
                    add(parseCountryObject(o))
                }
            }
        }

        fun parseCountryObject(o: JSONObject): Country = Country(
            id = o.getString("id"),
            nameFr = o.getString("nameFr"),
            nameEn = o.getString("nameEn"),
            flagEmoji = o.getString("flagEmoji"),
            capital = o.getString("capital"),
            population = o.getString("population"),
            continent = o.getString("continent"),
            mainLanguage = o.optString("mainLanguage", "—"),
            currency = o.optString("currency", "—"),
            mapX = o.getDouble("mapX").toFloat(),
            mapY = o.getDouble("mapY").toFloat(),
            colorHex = o.getString("colorHex"),
            kidFactTitle = o.optString("kidFactTitle", ""),
            kidFacts = o.optStringList("kidFacts"),
            memoryHook = o.optString("memoryHook", ""),
            images = o.optStringList("images"),
            worldCup2026Group = o.optString("worldCup2026Group").takeIf { it.isNotEmpty() },
            isWorldCup2026 = o.optBoolean("isWorldCup2026", false)
        )

        private fun parseCountryDetails(json: String): Map<String, CountryDetailOverlay> {
            val root = JSONObject(json)
            val map = mutableMapOf<String, CountryDetailOverlay>()
            root.keys().forEach { key ->
                val o = root.getJSONObject(key)
                map[key] = CountryDetailOverlay(
                    kidFactTitle = o.optString("kidFactTitle", ""),
                    kidFacts = o.optStringList("kidFacts"),
                    memoryHook = o.optString("memoryHook", ""),
                    images = o.optStringList("images")
                )
            }
            return map
        }

        fun parseMatches(json: String): List<Match> {
            val array = JSONArray(json)
            return buildList {
                for (i in 0 until array.length()) {
                    val o = array.getJSONObject(i)
                    add(
                        Match(
                            id = o.getString("id"),
                            label = o.getString("label"),
                            countryAId = o.getString("countryAId"),
                            countryBId = o.getString("countryBId"),
                            competition = o.getString("competition"),
                            dateLabel = o.getString("dateLabel"),
                            groupOrStage = o.getString("groupOrStage"),
                            phase = o.optString("phase", "group"),
                            featured = o.optBoolean("featured", false)
                        )
                    )
                }
            }
        }

        private fun JSONObject.getStringList(key: String): List<String> {
            val arr = getJSONArray(key)
            return buildList { for (i in 0 until arr.length()) add(arr.getString(i)) }
        }

        private fun JSONObject.optStringList(key: String): List<String> {
            if (!has(key)) return emptyList()
            return try {
                getStringList(key)
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}

private data class CountryDetailOverlay(
    val kidFactTitle: String,
    val kidFacts: List<String>,
    val memoryHook: String,
    val images: List<String>
)

private fun Country.mergeDetails(d: CountryDetailOverlay): Country = copy(
    kidFactTitle = d.kidFactTitle.ifBlank { kidFactTitle },
    kidFacts = d.kidFacts.ifEmpty { kidFacts },
    memoryHook = d.memoryHook.ifBlank { memoryHook },
    images = d.images.ifEmpty { images }
)

enum class MatchFilter(val label: String) {
    ALL("Tous"),
    GROUP("Poules"),
    KNOCKOUT("Finales"),
    FEATURED("Matchs du jour")
}

enum class MainTab(val label: String) {
    EXPLORE("Explorer"),
    WORLD_CUP("Coupe du monde")
}
