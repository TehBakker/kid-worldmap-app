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
    /** Nom de la monnaie (ex. "Euro"). */
    val currency: String,
    val mapX: Float,
    val mapY: Float,
    val colorHex: String,
    val capitalMapX: Float? = null,
    val capitalMapY: Float? = null,
    val kidFactTitle: String = "",
    val kidFacts: List<String> = emptyList(),
    val memoryHook: String = "",
    val images: List<String> = emptyList(),
    val worldCup2026Group: String? = null,
    val isWorldCup2026: Boolean = false,
    /**
     * Symbole typographique de la monnaie (ex. "€"). Optionnel : si null/blank,
     * il est déduit du nom via [CountryContent.currencySymbol]. Voir CountryContent
     * pour modifier la table des symboles.
     */
    val currencySymbol: String? = null,
    /**
     * Nombre de titres de Coupe du monde. Source de référence : la table dans
     * CountryContent. Ce champ permet une surcharge ponctuelle par pays.
     */
    val worldCupTitles: Int = 0,
    /**
     * Donnée de tracé d'une silhouette dédiée (ex. path SVG simplifié). Optionnel :
     * si null, on utilise les vraies frontières (country_borders.json) ou un fallback
     * avec les initiales. Prévu pour brancher facilement des SVG/VectorDrawable plus tard.
     */
    val shapePathData: String? = null,
    /**
     * Phrases « À retenir » adaptées aux enfants. Si vide, on retombe sur [kidFacts]
     * puis sur des faits générés. Modifiable dans country_details.json ou CountryContent.
     */
    val memorableFacts: List<String> = emptyList()
) {
    val hasRichContent: Boolean
        get() = kidFacts.isNotEmpty()

    /** Position de la capitale (repli sur le centre du pays si absente). */
    val capX: Float get() = capitalMapX ?: mapX
    val capY: Float get() = capitalMapY ?: mapY
}

/**
 * Image emblématique d'un pays affichée dans la galerie.
 * Pour modifier les images, voir [CountryContent.GALLERIES] et country_details.json.
 */
data class CountryImage(
    val title: String,
    val imageUrl: String,
    val caption: String? = null,
    val sourceLabel: String? = null,
    val sourceUrl: String? = null
)

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
