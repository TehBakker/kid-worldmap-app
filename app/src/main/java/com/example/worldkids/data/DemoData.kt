package com.example.worldkids.data

/**
 * Données locales de démonstration.
 *
 * Pour mettre à jour les vrais matchs de Coupe du monde :
 * modifie la liste [demoMatches] ci-dessous (dates, phases, pays).
 */
object DemoData {

    val countries: List<Country> = listOf(
        Country(
            id = "france",
            nameFr = "France",
            nameEn = "France",
            flagEmoji = "🇫🇷",
            capital = "Paris",
            population = "68 millions",
            continent = "Europe",
            mainLanguage = "Français",
            currency = "Euro",
            kidFactTitle = "Tour Eiffel et fromage",
            kidFacts = listOf(
                "La capitale est Paris, connue pour la tour Eiffel.",
                "On y mange beaucoup de pain, de fromage et de pâtisseries.",
                "Les Alpes sont de grandes montagnes où l'on peut skier.",
                "La France a gagné la Coupe du monde de football en 2018."
            ),
            memoryHook = "Tour Eiffel, fromage, football",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Tour_Eiffel_Wikimedia_Commons.jpg/320px-Tour_Eiffel_Wikimedia_Commons.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/La_Mer_de_Glace.jpg/320px-La_Mer_de_Glace.jpg"
            ),
            mapX = 0.48f, mapY = 0.32f, colorHex = "#1565C0"
        ),
        Country(
            id = "brazil",
            nameFr = "Brésil",
            nameEn = "Brazil",
            flagEmoji = "🇧🇷",
            capital = "Brasília",
            population = "215 millions",
            continent = "Amérique du Sud",
            mainLanguage = "Portugais",
            currency = "Real",
            kidFactTitle = "Amazonie et carnaval",
            kidFacts = listOf(
                "Le Brésil est le plus grand pays d'Amérique du Sud.",
                "L'Amazonie abrite des milliers d'espèces d'animaux.",
                "Le carnaval de Rio est une fête colorée avec de la musique.",
                "Le football est une passion nationale !"
            ),
            memoryHook = "Football, Amazonie, carnaval",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Christ_the_Redeemer_-_Cristo_Redentor.jpg/320px-Christ_the_Redeemer_-_Cristo_Redentor.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/Amazon_river_near_Manaus.jpg/320px-Amazon_river_near_Manaus.jpg"
            ),
            mapX = 0.32f, mapY = 0.58f, colorHex = "#2E7D32"
        ),
        Country(
            id = "argentina",
            nameFr = "Argentine",
            nameEn = "Argentina",
            flagEmoji = "🇦🇷",
            capital = "Buenos Aires",
            population = "46 millions",
            continent = "Amérique du Sud",
            mainLanguage = "Espagnol",
            currency = "Peso",
            kidFactTitle = "Tango et pampa",
            kidFacts = listOf(
                "Buenos Aires est surnommée la « Paris de l'Amérique du Sud ».",
                "Le tango est une danse née dans ce pays.",
                "La Patagonie offre des glaciers impressionnants.",
                "Les Gauchos sont les cow-boys argentins."
            ),
            memoryHook = "Tango, football, pampa",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/Perito_Moreno_Glacier_Patagonia_Argentina_Luca_Galuzzi_2005.jpg/320px-Perito_Moreno_Glacier_Patagonia_Argentina_Luca_Galuzzi_2005.jpg"
            ),
            mapX = 0.28f, mapY = 0.75f, colorHex = "#42A5F5"
        ),
        Country(
            id = "japan",
            nameFr = "Japon",
            nameEn = "Japan",
            flagEmoji = "🇯🇵",
            capital = "Tokyo",
            population = "125 millions",
            continent = "Asie",
            mainLanguage = "Japonais",
            currency = "Yen",
            kidFactTitle = "Fuji et trains rapides",
            kidFacts = listOf(
                "Tokyo est une des plus grandes villes du monde.",
                "Le mont Fuji ressemble à un volcan dessiné dans un manga.",
                "Les trains Shinkansen sont très rapides.",
                "Les cerisiers en fleurs au printemps sont magnifiques."
            ),
            memoryHook = "Fuji, mangas, trains rapides",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/Mt._Fuji_from_Motohakone_1-21-26.jpg/320px-Mt._Fuji_from_Motohakone_1-21-26.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/Shinkansen_series_N700.jpg/320px-Shinkansen_series_N700.jpg"
            ),
            mapX = 0.82f, mapY = 0.38f, colorHex = "#E53935"
        ),
        Country(
            id = "morocco",
            nameFr = "Maroc",
            nameEn = "Morocco",
            flagEmoji = "🇲🇦",
            capital = "Rabat",
            population = "37 millions",
            continent = "Afrique",
            mainLanguage = "Arabe et amazigh",
            currency = "Dirham",
            kidFactTitle = "Désert et souks",
            kidFacts = listOf(
                "Le désert du Sahara couvre une grande partie du sud.",
                "Les souks sont des marchés colorés et animés.",
                "Les montagnes de l'Atlas sont très hautes.",
                "Le couscous est un plat traditionnel délicieux."
            ),
            memoryHook = "Désert, souks, Atlas",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Marrakech_souks.jpg/320px-Marrakech_souks.jpg"
            ),
            mapX = 0.46f, mapY = 0.42f, colorHex = "#C62828"
        ),
        Country(
            id = "usa",
            nameFr = "États-Unis",
            nameEn = "United States",
            flagEmoji = "🇺🇸",
            capital = "Washington D.C.",
            population = "335 millions",
            continent = "Amérique du Nord",
            mainLanguage = "Anglais",
            currency = "Dollar",
            kidFactTitle = "Grand Canyon et NASA",
            kidFacts = listOf(
                "Le Grand Canyon est une gorge immense en Arizona.",
                "La NASA envoie des fusées vers l'espace depuis la Floride.",
                "New York a une statue géante : la Statue de la Liberté.",
                "Le pays compte 50 États différents."
            ),
            memoryHook = "Statue Liberté, NASA, grands parcs",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Grand_Canyon_view_from_Pima_Point_2010.jpg/320px-Grand_Canyon_view_from_Pima_Point_2010.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/Statue_of_Liberty_7.jpg/320px-Statue_of_Liberty_7.jpg"
            ),
            mapX = 0.22f, mapY = 0.35f, colorHex = "#1565C0"
        ),
        Country(
            id = "mexico",
            nameFr = "Mexique",
            nameEn = "Mexico",
            flagEmoji = "🇲🇽",
            capital = "Mexico",
            population = "128 millions",
            continent = "Amérique du Nord",
            mainLanguage = "Espagnol",
            currency = "Peso",
            kidFactTitle = "Pyramides et tacos",
            kidFacts = listOf(
                "Les Mayas ont construit d'immenses pyramides.",
                "Les tacos et les tortillas viennent du Mexique.",
                "Le pays a des plages sur deux océans.",
                "Les monarches sont des papillons orange qui migrent ici."
            ),
            memoryHook = "Pyramides, tacos, plages",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/51/Chichen_Itza_3.jpg/320px-Chichen_Itza_3.jpg"
            ),
            mapX = 0.18f, mapY = 0.45f, colorHex = "#00897B"
        ),
        Country(
            id = "canada",
            nameFr = "Canada",
            nameEn = "Canada",
            flagEmoji = "🇨🇦",
            capital = "Ottawa",
            population = "40 millions",
            continent = "Amérique du Nord",
            mainLanguage = "Anglais et français",
            currency = "Dollar canadien",
            kidFactTitle = "Ours et grands lacs",
            kidFacts = listOf(
                "Le Canada est le deuxième plus grand pays du monde.",
                "On peut voir des ours polaires dans le Grand Nord.",
                "Les chutes du Niagara font un bruit énorme !",
                "Le sirop d'érable vient des arbres canadiens."
            ),
            memoryHook = "Ours, érable, grands lacs",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/Niagara_Falls%2C_Canada.jpg/320px-Niagara_Falls%2C_Canada.jpg"
            ),
            mapX = 0.20f, mapY = 0.25f, colorHex = "#D32F2F"
        ),
        Country(
            id = "germany",
            nameFr = "Allemagne",
            nameEn = "Germany",
            flagEmoji = "🇩🇪",
            capital = "Berlin",
            population = "84 millions",
            continent = "Europe",
            mainLanguage = "Allemand",
            currency = "Euro",
            kidFactTitle = "Châteaux et forêts",
            kidFacts = listOf(
                "Berlin est une ville avec beaucoup d'histoire.",
                "Le pays est célèbre pour ses châteaux de contes de fées.",
                "Les forêts allemandes sont parfaites pour se promener.",
                "Les voitures allemandes sont réputées dans le monde."
            ),
            memoryHook = "Châteaux, forêts, voitures",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f8/Neuschwanstein_Castle_LOC_Large.jpg/320px-Neuschwanstein_Castle_LOC_Large.jpg"
            ),
            mapX = 0.51f, mapY = 0.28f, colorHex = "#F9A825"
        ),
        Country(
            id = "spain",
            nameFr = "Espagne",
            nameEn = "Spain",
            flagEmoji = "🇪🇸",
            capital = "Madrid",
            population = "48 millions",
            continent = "Europe",
            mainLanguage = "Espagnol",
            currency = "Euro",
            kidFactTitle = "Flamenco et plages",
            kidFacts = listOf(
                "Le flamenco mêle danse, chant et guitare.",
                "La Sagrada Família à Barcelone est un édifice unique.",
                "L'Espagne a de longues plages ensoleillées.",
                "La paella est un plat coloré au riz."
            ),
            memoryHook = "Flamenco, paella, plages",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9d/Sagrada_Familia_01.jpg/320px-Sagrada_Familia_01.jpg"
            ),
            mapX = 0.45f, mapY = 0.36f, colorHex = "#E65100"
        ),
        Country(
            id = "england",
            nameFr = "Angleterre",
            nameEn = "England",
            flagEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
            capital = "Londres",
            population = "56 millions (R.-U.)",
            continent = "Europe",
            mainLanguage = "Anglais",
            currency = "Livre sterling",
            kidFactTitle = "Big Ben et Harry Potter",
            kidFacts = listOf(
                "Londres a un grand pont rouge : Tower Bridge.",
                "Big Ben est une célèbre horloge.",
                "Harry Potter a été écrit par une auteure anglaise.",
                "Le football est né ici il y a très longtemps."
            ),
            memoryHook = "Big Ben, football, thé",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/9/93/Clock_Tower_-_Palace_of_Westminster%2C_London_-_May_2007.jpg/320px-Clock_Tower_-_Palace_of_Westminster%2C_London_-_May_2007.jpg"
            ),
            mapX = 0.47f, mapY = 0.26f, colorHex = "#3949AB"
        ),
        Country(
            id = "italy",
            nameFr = "Italie",
            nameEn = "Italy",
            flagEmoji = "🇮🇹",
            capital = "Rome",
            population = "59 millions",
            continent = "Europe",
            mainLanguage = "Italien",
            currency = "Euro",
            kidFactTitle = "Pizza et Colisée",
            kidFacts = listOf(
                "Rome abrite le Colisée, un amphithéâtre antique.",
                "La pizza et les pâtes viennent d'Italie.",
                "Venise est une ville construite sur l'eau.",
                "Le pays a la forme d'une botte de football."
            ),
            memoryHook = "Pizza, Colisée, Venise",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/Colosseo_2020.jpg/320px-Colosseo_2020.jpg"
            ),
            mapX = 0.52f, mapY = 0.34f, colorHex = "#43A047"
        ),
        Country(
            id = "senegal",
            nameFr = "Sénégal",
            nameEn = "Senegal",
            flagEmoji = "🇸🇳",
            capital = "Dakar",
            population = "17 millions",
            continent = "Afrique",
            mainLanguage = "Français",
            currency = "Franc CFA",
            kidFactTitle = "Lions et île de Gorée",
            kidFacts = listOf(
                "Dakar est la ville la plus à l'ouest de l'Afrique.",
                "Le pays est surnommé le pays du « Teranga » : l'hospitalité.",
                "On y trouve des lions dans certains parcs.",
                "Le mbalax est une musique rythmée populaire."
            ),
            memoryHook = "Teranga, lions, musique",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Dakar_from_above.jpg/320px-Dakar_from_above.jpg"
            ),
            mapX = 0.44f, mapY = 0.50f, colorHex = "#FDD835"
        ),
        Country(
            id = "australia",
            nameFr = "Australie",
            nameEn = "Australia",
            flagEmoji = "🇦🇺",
            capital = "Canberra",
            population = "26 millions",
            continent = "Océanie",
            mainLanguage = "Anglais",
            currency = "Dollar australien",
            kidFactTitle = "Kangourous et récif",
            kidFacts = listOf(
                "Les kangourous sautent partout dans ce pays.",
                "La Grande Barrière de corail est visible depuis l'espace.",
                "L'Opéra de Sydney ressemble à des voiles blanches.",
                "C'est un immense pays-continent entouré d'océan."
            ),
            memoryHook = "Kangourous, opéra, récif",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Sydney_Opera_House_Sailed_Away Photography_Shutterstock.jpg/320px-Sydney_Opera_House_Sailed_Away_Photography_Shutterstock.jpg"
            ),
            mapX = 0.85f, mapY = 0.72f, colorHex = "#FF7043"
        ),
        Country(
            id = "south_korea",
            nameFr = "Corée du Sud",
            nameEn = "South Korea",
            flagEmoji = "🇰🇷",
            capital = "Séoul",
            population = "52 millions",
            continent = "Asie",
            mainLanguage = "Coréen",
            currency = "Won",
            kidFactTitle = "K-pop et technologie",
            kidFacts = listOf(
                "Séoul est une ville ultra-moderne et lumineuse.",
                "La K-pop est une musique dansée très populaire.",
                "Le kimchi est un plat épicé à base de chou.",
                "Le pays est célèbre pour ses jeux vidéo et sa tech."
            ),
            memoryHook = "K-pop, kimchi, technologie",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Seoul_%28South_Korea%29_at_night.jpg/320px-Seoul_%28South_Korea%29_at_night.jpg"
            ),
            mapX = 0.80f, mapY = 0.36f, colorHex = "#5C6BC0"
        ),
        Country(
            id = "portugal",
            nameFr = "Portugal",
            nameEn = "Portugal",
            flagEmoji = "🇵🇹",
            capital = "Lisbonne",
            population = "10 millions",
            continent = "Europe",
            mainLanguage = "Portugais",
            currency = "Euro",
            kidFactTitle = "Explorateurs et pastéis",
            kidFacts = listOf(
                "Les navigateurs portugais ont exploré les océans.",
                "Lisbonne est une ville aux collines et tramways jaunes.",
                "Les pastéis de nata sont de petites tartes crémeuses.",
                "Le football est très suivi, surtout à Porto et Lisbonne."
            ),
            memoryHook = "Tramways, océan, football",
            images = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Lisbon_tram_28.jpg/320px-Lisbon_tram_28.jpg"
            ),
            mapX = 0.44f, mapY = 0.38f, colorHex = "#26A69A"
        )
    )

    // ── Matchs démo : remplace cette liste par les vrais matchs de Coupe du monde ──
    val demoMatches: List<Match> = listOf(
        Match("m1", "France vs Brésil", "france", "brazil", "Coupe du monde", "Match démo", "Finale — À personnaliser"),
        Match("m2", "Japon vs Allemagne", "japan", "germany", "Coupe du monde", "Match démo", "Groupe — À personnaliser"),
        Match("m3", "Maroc vs Espagne", "morocco", "spain", "Coupe du monde", "Match démo", "Huitième — À personnaliser"),
        Match("m4", "Argentine vs Mexique", "argentina", "mexico", "Coupe du monde", "Match démo", "Groupe — À personnaliser"),
        Match("m5", "Angleterre vs Sénégal", "england", "senegal", "Coupe du monde", "Match démo", "Huitième — À personnaliser"),
        Match("m6", "États-Unis vs Portugal", "usa", "portugal", "Coupe du monde", "Match démo", "Groupe — À personnaliser"),
        Match("m7", "Italie vs Corée du Sud", "italy", "south_korea", "Coupe du monde", "Match démo", "Groupe — À personnaliser"),
        Match("m8", "Canada vs Australie", "canada", "australia", "Coupe du monde", "Match démo", "Groupe — À personnaliser")
    )

    fun countryById(id: String): Country? = countries.find { it.id == id }
}
