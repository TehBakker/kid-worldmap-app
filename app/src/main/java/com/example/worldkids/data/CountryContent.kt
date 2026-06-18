package com.example.worldkids.data

object CountryContent {

    /** Nombre de Coupes du monde masculines gagnées (au 1er janvier 2026). */
    private val WORLD_CUP_TITLES: Map<String, Int> = mapOf(
        "brazil" to 5,
        "germany" to 4,
        "italy" to 4,
        "argentina" to 3,
        "france" to 2,
        "uruguay" to 2,
        "england" to 1,
        "spain" to 1
    )

    fun worldCupTitles(country: Country): Int = WORLD_CUP_TITLES[country.id] ?: 0

    fun displayTitle(country: Country): String =
        country.kidFactTitle.ifBlank { "Découvre ${country.nameFr} !" }

    fun displayFacts(country: Country): List<String> {
        if (country.kidFacts.isNotEmpty()) return country.kidFacts
        return buildList {
            add("La capitale est ${country.capital}.")
            add("Ce pays se trouve en ${country.continent}.")
            if (country.mainLanguage != "—") {
                add("On y parle principalement ${country.mainLanguage}.")
            }
            if (country.isWorldCup2026) {
                add("Ce pays participe à la Coupe du monde 2026 !")
                country.worldCup2026Group?.let { add("Il est dans la poule $it.") }
            }
        }
    }

    fun displayMemoryHook(country: Country): String =
        country.memoryHook.ifBlank {
            buildString {
                append(country.continent)
                if (country.capital != "—") append(", ${country.capital}")
            }
        }
}
