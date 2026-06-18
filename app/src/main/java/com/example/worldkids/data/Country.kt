package com.example.worldkids.data

data class Country(
    val id: String,
    val nameFr: String,
    val nameEn: String,
    val flagEmoji: String,
    val capital: String,
    val population: String,
    val continent: String,
    val mainLanguage: String,
    val currency: String,
    val mapX: Float,
    val mapY: Float,
    val colorHex: String,
    val kidFactTitle: String = "",
    val kidFacts: List<String> = emptyList(),
    val memoryHook: String = "",
    val images: List<String> = emptyList(),
    val worldCup2026Group: String? = null,
    val isWorldCup2026: Boolean = false
) {
    val hasRichContent: Boolean
        get() = kidFacts.isNotEmpty()
}

data class WorldCupGroup(
    val id: String,
    val label: String,
    val teamIds: List<String>
)

data class WorldCupGroupsData(
    val competition: String,
    val hostCountries: List<String>,
    val groups: List<WorldCupGroup>
)
