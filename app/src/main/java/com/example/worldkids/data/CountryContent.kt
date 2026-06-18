package com.example.worldkids.data

/**
 * Couche de contenu éditorial (le « DemoData » de l'app) : titres CDM, symboles de
 * monnaie, faits « À retenir » et galeries d'images emblématiques.
 *
 * POUR MODIFIER :
 *  - Nombre d'étoiles Coupe du monde -> [WORLD_CUP_TITLES]
 *  - Symboles de monnaie -> [currencySymbol]
 *  - Phrases « À retenir » -> [MEMORABLE_FACTS] (ou country_details.json)
 *  - Images de galerie -> [GALLERIES] (URLs Wikimedia Commons, faciles à remplacer)
 */
object CountryContent {

    /** Affiche le bandeau CDM sous le nom du pays. Mettre à false hors période CDM. */
    const val showWorldCupBadge: Boolean = true

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

    /**
     * Nombre de titres (= nombre d'étoiles) pour un pays.
     * Priorité au champ [Country.worldCupTitles] si renseigné, sinon table de référence.
     */
    fun worldCupTitles(country: Country): Int =
        country.worldCupTitles.takeIf { it > 0 } ?: WORLD_CUP_TITLES[country.id] ?: 0

    /**
     * Symbole typographique (Unicode) de la monnaie, ou null si aucun rendu propre.
     * On évite volontairement les symboles ambigus ou laids.
     */
    fun currencySymbol(country: Country): String? {
        country.currencySymbol?.let { if (it.isNotBlank()) return it }
        val c = country.currency.lowercase()
        return when {
            "euro" in c -> "€"
            "livre" in c || "sterling" in c -> "£"
            "yen" in c -> "¥"
            "won" in c -> "₩"
            "real" in c || "réal" in c -> "R$"
            "yuan" in c || "renminbi" in c -> "¥"
            "roupie" in c -> "₹"
            "dirham" in c -> "DH"
            "dollar" in c -> "$"
            "peso" in c -> "$"
            else -> null
        }
    }

    /** Libellé monnaie complet : "Euro €" si un symbole propre existe, sinon "Euro". */
    fun currencyLabel(country: Country): String {
        val symbol = currencySymbol(country)
        return if (symbol.isNullOrBlank()) country.currency else "${country.currency} $symbol"
    }

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

    /** 3 phrases courtes, mémorables et adaptées aux enfants. */
    fun memorableFacts(country: Country): List<String> {
        val curated = country.memorableFacts.ifEmpty { MEMORABLE_FACTS[country.id].orEmpty() }
        return curated.ifEmpty { displayFacts(country) }.take(3)
    }

    /** Galerie d'images emblématiques : curatée si dispo, sinon URLs brutes du pays. */
    fun gallery(country: Country): List<CountryImage> {
        GALLERIES[country.id]?.let { if (it.isNotEmpty()) return it }
        return country.images.map { url -> CountryImage(title = country.nameFr, imageUrl = url) }
    }

    fun displayMemoryHook(country: Country): String =
        country.memoryHook.ifBlank {
            buildString {
                append(country.continent)
                if (country.capital != "—") append(", ${country.capital}")
            }
        }

    // ── « À retenir » curatés (3 phrases max, kid-friendly) ──────────────────────
    private val MEMORABLE_FACTS: Map<String, List<String>> = mapOf(
        "france" to listOf(
            "La tour Eiffel est l'un des monuments les plus connus du monde.",
            "Les Alpes sont de grandes montagnes où l'on peut skier.",
            "La France est connue pour son pain, ses fromages et ses pâtisseries."
        ),
        "japan" to listOf(
            "Le mont Fuji ressemble à un volcan dessiné dans un manga.",
            "Les trains Shinkansen sont parmi les plus rapides du monde.",
            "Tokyo mélange temples anciens, jeux vidéo et immeubles géants."
        ),
        "brazil" to listOf(
            "L'Amazonie est une immense forêt pleine d'animaux incroyables.",
            "Le football est une vraie passion dans tout le pays.",
            "Le carnaval de Rio est une grande fête pleine de musique et de couleurs."
        ),
        "morocco" to listOf(
            "Le désert du Sahara est couvert de dunes de sable doré.",
            "Les souks sont des marchés colorés pleins d'épices et de tapis.",
            "Les montagnes de l'Atlas sont enneigées en hiver."
        ),
        "usa" to listOf(
            "La Statue de la Liberté accueille les bateaux à New York.",
            "Le Grand Canyon est une gorge gigantesque creusée par une rivière.",
            "La NASA y envoie des fusées dans l'espace."
        ),
        "argentina" to listOf(
            "Le football est adoré dans tout le pays.",
            "La Patagonie a des glaciers géants et des montagnes.",
            "Le tango est une danse née à Buenos Aires."
        )
    )

    // ── Galeries d'images emblématiques ──────────────────────────────────────────
    // URLs Wikimedia Commons (libres / réutilisables). Pour en ajouter, copie une
    // ligne CountryImage(...) et remplace l'URL par une vignette stable.
    private val GALLERIES: Map<String, List<CountryImage>> = mapOf(
        "france" to listOf(
            wiki("Tour Eiffel", "Le monument le plus célèbre de Paris.",
                "a/a8/Tour_Eiffel_Wikimedia_Commons.jpg"),
            wiki("Mont-Saint-Michel", "Une abbaye posée sur un îlot, en Normandie.",
                "1/1a/Mont_St_Michel_3%2C_Brittany%2C_France_-_July_2011.jpg"),
            wiki("Les Alpes", "De grandes montagnes parfaites pour le ski.",
                "8/87/Mont_Blanc_oct_2004.JPG")
        ),
        "japan" to listOf(
            wiki("Mont Fuji", "Le volcan emblématique du Japon.",
                "4/45/080103_hakkai_fuji.jpg"),
            wiki("Tokyo", "Une mégapole géante et lumineuse.",
                "8/85/Skyscrapers_of_Shinjuku_2009_January.jpg"),
            wiki("Cerisiers en fleurs", "Au printemps, les sakura fleurissent partout.",
                "6/62/Cherry_blossoms_in_Tokyo.jpg")
        ),
        "brazil" to listOf(
            wiki("Christ Rédempteur", "La grande statue au-dessus de Rio.",
                "5/5d/Christ_the_Redeemer_-_Cristo_Redentor.jpg"),
            wiki("Amazonie", "La plus grande forêt tropicale du monde.",
                "0/0a/Amazon_CIAT_%2810816643833%29.jpg"),
            wiki("Plage de Rio", "Les plages de sable bordées de montagnes.",
                "f/f8/1_rio_de_janeiro_copacabana_beach_panorama_2014.jpg")
        ),
        "morocco" to listOf(
            wiki("Désert du Sahara", "D'immenses dunes de sable doré.",
                "5/5e/Morocco_Africa_Flickr_Rosino_December_2005_84514010.jpg"),
            wiki("Médina", "Les ruelles colorées des vieilles villes.",
                "2/2a/Marrakech_-_Place_Jemaa_El_Fna.jpg"),
            wiki("Montagnes de l'Atlas", "Des sommets enneigés en hiver.",
                "1/16/Atlas_Mountains_in_Morocco.jpg")
        ),
        "usa" to listOf(
            wiki("Statue de la Liberté", "Le symbole de New York.",
                "a/a1/Statue_of_Liberty_7.jpg"),
            wiki("Grand Canyon", "Une gorge immense en Arizona.",
                "d/dd/Grand_Canyon_view_from_Pima_Point_2010.jpg"),
            wiki("New York", "La ville aux gratte-ciels célèbres.",
                "5/52/Above_Gotham.jpg")
        ),
        "argentina" to listOf(
            wiki("Buenos Aires", "La capitale animée et colorée.",
                "8/83/Caminito-2010.jpg"),
            wiki("Patagonie", "Glaciers et montagnes au sud du pays.",
                "1/19/Perito_Moreno_Glacier_Patagonia_Argentina_Luca_Galuzzi_2005.JPG")
        )
    )

    /** Construit une CountryImage à partir d'un chemin Wikimedia Commons (vignette 320px). */
    private fun wiki(title: String, caption: String, path: String): CountryImage {
        val file = path.substringAfterLast('/')
        return CountryImage(
            title = title,
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/$path/320px-$file",
            caption = caption,
            sourceLabel = "Wikimedia Commons",
            sourceUrl = "https://commons.wikimedia.org/wiki/File:$file"
        )
    }
}
