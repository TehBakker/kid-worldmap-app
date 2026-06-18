package com.example.worldkids.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

/**
 * Carte du monde détaillée pour le planisphère.
 *
 * Les polygones sont définis en degrés (longitude, latitude) puis convertis
 * en coordonnées normalisées [0..1] via une projection équirectangulaire :
 *   nx = (lon + 180) / 360
 *   ny = (90 - lat) / 180
 *
 * Cette projection est cohérente avec mapX/mapY des objets [com.example.worldkids.data.Country],
 * ce qui garantit l'alignement parfait des pastilles pays sur les continents.
 */
object WorldContinents {

    // Palette « atlas enfant »
    private val LAND       = Color(0xFFCDE7A6) // vert tendre
    private val LAND_DARK  = Color(0xFFA8D27A) // vert foncé pour îles/Greenland
    private val LAND_ICE   = Color(0xFFE8EEF2) // glace pour Antarctique
    private val LAND_DESERT = Color(0xFFE7D89F) // sable (péninsule arabique)
    val LAND_OUTLINE: Color = Color(0xFF5D8A3A)

    /** Toutes les masses continentales avec leurs couleurs (ordre d'empilage). */
    fun allPaths(width: Float, height: Float): List<Pair<Path, Color>> = listOf(
        buildPath(width, height, NORTH_AMERICA) to LAND,
        buildPath(width, height, SOUTH_AMERICA) to LAND,
        buildPath(width, height, EURASIA)       to LAND,
        buildPath(width, height, AFRICA)        to LAND,
        buildPath(width, height, ARABIA)        to LAND_DESERT,
        buildPath(width, height, INDIA_TRIM)    to LAND, // raffine forme Inde
        buildPath(width, height, AUSTRALIA)     to LAND,
        buildPath(width, height, ANTARCTICA)    to LAND_ICE,
        buildPath(width, height, GREENLAND)     to LAND_DARK,
        buildPath(width, height, BRITISH_ISLES) to LAND_DARK,
        buildPath(width, height, ICELAND)       to LAND_DARK,
        buildPath(width, height, JAPAN_HONSHU)  to LAND_DARK,
        buildPath(width, height, JAPAN_HOKKAIDO) to LAND_DARK,
        buildPath(width, height, MADAGASCAR)    to LAND_DARK,
        buildPath(width, height, NEW_ZEALAND_N) to LAND_DARK,
        buildPath(width, height, NEW_ZEALAND_S) to LAND_DARK,
        buildPath(width, height, SUMATRA)       to LAND_DARK,
        buildPath(width, height, JAVA)          to LAND_DARK,
        buildPath(width, height, BORNEO)        to LAND_DARK,
        buildPath(width, height, SULAWESI)      to LAND_DARK,
        buildPath(width, height, NEW_GUINEA)    to LAND_DARK,
        buildPath(width, height, PHILIPPINES)   to LAND_DARK,
        buildPath(width, height, SRI_LANKA)     to LAND_DARK,
        buildPath(width, height, CUBA)          to LAND_DARK,
        buildPath(width, height, HISPANIOLA)    to LAND_DARK,
        buildPath(width, height, TAIWAN)        to LAND_DARK
    )

    private fun buildPath(w: Float, h: Float, points: List<Pair<Float, Float>>): Path {
        val path = Path()
        points.forEachIndexed { i, (nx, ny) ->
            val x = nx * w
            val y = ny * h
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return path
    }

    /** Quadrillage équatorial / méridiens pour effet « globe ». */
    fun graticuleLines(width: Float, height: Float): List<Pair<Offset, Offset>> {
        val lines = mutableListOf<Pair<Offset, Offset>>()
        // Méridiens tous les 30°
        for (i in 1..11) {
            val x = width * i / 12f
            lines.add(Offset(x, 0f) to Offset(x, height))
        }
        // Parallèles tous les 30°
        for (j in 1..5) {
            val y = height * j / 6f
            lines.add(Offset(0f, y) to Offset(width, y))
        }
        return lines
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helper : conversion (lon, lat) → coords normalisées 0..1
    // ─────────────────────────────────────────────────────────────────────────
    private fun ll(lon: Float, lat: Float): Pair<Float, Float> =
        ((lon + 180f) / 360f) to ((90f - lat) / 180f)

    private fun poly(vararg p: Pair<Float, Float>): List<Pair<Float, Float>> = p.toList()

    // ═════════════════════════════════════════════════════════════════════════
    //  POLYGONES DES CONTINENTS (sens horaire, projection équirectangulaire)
    // ═════════════════════════════════════════════════════════════════════════

    // ── Amérique du Nord ────────────────────────────────────────────────────
    private val NORTH_AMERICA = poly(
        ll(-168f, 66f),   // Détroit de Béring
        ll(-163f, 70f),
        ll(-156f, 71f),   // Pointe Barrow
        ll(-141f, 70f),
        ll(-132f, 69f),
        ll(-122f, 70f),
        ll(-110f, 68f),
        ll(-95f,  69f),
        ll(-82f,  67f),
        ll(-78f,  62f),
        ll(-72f,  60f),   // Détroit d'Hudson
        ll(-78f,  58f),
        ll(-82f,  53f),   // Baie James
        ll(-79f,  50f),
        ll(-69f,  53f),   // Labrador
        ll(-57f,  53f),   // Terre-Neuve est
        ll(-52f,  47f),   // Cap Race
        ll(-65f,  44f),   // Nouvelle-Écosse
        ll(-70f,  41f),   // Cape Cod
        ll(-74f,  40f),   // NYC
        ll(-76f,  37f),   // Chesapeake
        ll(-76f,  35f),   // Cap Hatteras
        ll(-80f,  32f),   // Savannah
        ll(-80.5f, 25f),  // Floride sud-est
        ll(-81f,  25f),
        ll(-82f,  27f),
        ll(-83f,  29f),   // Tampa
        ll(-85f,  30f),   // Panhandle
        ll(-89f,  29f),   // Delta du Mississippi
        ll(-94f,  29f),   // Galveston
        ll(-97f,  26f),   // Brownsville
        ll(-97f,  22f),
        ll(-95f,  19f),   // Veracruz
        ll(-92f,  18f),   // Tehuantepec
        ll(-90f,  21f),
        ll(-87f,  21f),   // Yucatán
        ll(-87f,  17f),
        ll(-88f,  16f),   // Belize
        ll(-83f,  11f),   // Nicaragua est
        ll(-79f,  9f),    // Panama Caraïbes
        ll(-77f,  8f),    // Darien
        ll(-80f,  8f),    // Panama Pacifique
        ll(-84f,  10f),
        ll(-87f,  13f),   // Salvador
        ll(-92f,  14f),
        ll(-96f,  16f),
        ll(-101f, 17f),   // Acapulco
        ll(-105f, 19f),
        ll(-109f, 23f),   // Mazatlán
        ll(-110f, 27f),   // Sinaloa
        ll(-113f, 31f),   // Sonora
        ll(-114f, 31f),   // Tête du Golfe de Californie
        ll(-114f, 30f),
        ll(-111f, 26f),   // Baja est
        ll(-110f, 23f),   // Cabo
        ll(-115f, 26f),   // Baja ouest
        ll(-117f, 32f),   // Tijuana
        ll(-118f, 34f),   // Los Angeles
        ll(-122f, 37f),   // San Francisco
        ll(-124f, 41f),   // Cape Mendocino
        ll(-124f, 46f),   // Oregon
        ll(-124f, 48f),
        ll(-128f, 52f),
        ll(-131f, 55f),
        ll(-135f, 58f),   // Juneau
        ll(-141f, 60f),
        ll(-148f, 60f),   // Anchorage
        ll(-154f, 58f),
        ll(-158f, 57f),
        ll(-162f, 55f),   // Péninsule de l'Alaska
        ll(-167f, 53f),
        ll(-166f, 60f),
        ll(-168f, 66f)    // ferme
    )

    // ── Amérique du Sud ─────────────────────────────────────────────────────
    private val SOUTH_AMERICA = poly(
        ll(-77f,  8f),    // Panama / Colombie
        ll(-75f,  10f),   // Colombie nord
        ll(-71f,  12f),   // Venezuela
        ll(-62f,  11f),
        ll(-52f,  5f),    // Guyanes
        ll(-50f,  0f),
        ll(-45f, -2f),    // Amazone
        ll(-37f, -5f),    // Brésil nord-est (Fortaleza)
        ll(-35f, -7f),    // Recife (bulge)
        ll(-39f, -13f),   // Salvador
        ll(-41f, -22f),   // Rio
        ll(-48f, -25f),   // São Paulo
        ll(-53f, -33f),   // Uruguay
        ll(-58f, -35f),   // Buenos Aires
        ll(-62f, -39f),
        ll(-65f, -43f),
        ll(-66f, -50f),
        ll(-68f, -53f),
        ll(-71f, -55f),   // Terre de Feu
        ll(-74f, -52f),
        ll(-74f, -45f),   // Patagonie ouest
        ll(-73f, -38f),   // Chili central
        ll(-71f, -30f),
        ll(-71f, -18f),
        ll(-77f, -12f),   // Lima
        ll(-81f, -6f),
        ll(-81f,  0f),    // Équateur
        ll(-79f,  2f),
        ll(-79f,  8f),    // Panama
        ll(-77f,  8f)
    )

    // ── Eurasie (Europe + Asie, hors péninsule arabique et îles) ────────────
    private val EURASIA = poly(
        ll(-10f,  35f),   // Détroit de Gibraltar côté Europe
        ll(-9f,   37f),   // Portugal sud
        ll(-9f,   42f),   // Portugal nord
        ll(-9f,   43f),   // Galice
        ll(-2f,   43f),   // Pays basque
        ll(-1f,   46f),
        ll(-4f,   48f),   // Bretagne
        ll(-2f,   49f),
        ll(2f,    51f),   // Pas-de-Calais
        ll(4f,    52f),   // Pays-Bas
        ll(8f,    54f),   // Allemagne nord
        ll(8f,    57f),   // Danemark
        ll(11f,   58f),
        ll(5f,    59f),   // Norvège sud
        ll(5f,    62f),
        ll(8f,    63f),
        ll(11f,   65f),
        ll(15f,   68f),
        ll(20f,   70f),
        ll(28f,   71f),   // Cap Nord
        ll(35f,   69f),
        ll(40f,   67f),   // Mer Blanche
        ll(50f,   69f),
        ll(60f,   70f),
        ll(70f,   72f),
        ll(80f,   73f),
        ll(95f,   77f),
        ll(105f,  78f),
        ll(115f,  74f),
        ll(125f,  73f),
        ll(135f,  72f),
        ll(145f,  72f),
        ll(155f,  71f),
        ll(165f,  69f),
        ll(178f,  69f),   // Tchoukotka
        ll(180f,  66f),
        ll(170f,  60f),
        ll(163f,  60f),   // Kamtchatka nord
        ll(160f,  56f),
        ll(155f,  51f),   // Kamtchatka sud
        ll(143f,  46f),   // Sakhaline
        ll(141f,  43f),   // Hokkaido (continent)
        ll(132f,  43f),   // Vladivostok
        ll(128f,  39f),   // Corée du Nord
        ll(126f,  35f),   // Corée du Sud
        ll(122f,  39f),   // Mer Jaune
        ll(122f,  31f),   // Shanghai
        ll(118f,  24f),   // Fujian
        ll(110f,  21f),   // Hainan continent
        ll(108f,  10f),   // Sud-Vietnam
        ll(104f,  9f),    // Cambodge
        ll(101f,  3f),    // Singapour
        ll(99f,   8f),    // Thaïlande sud
        ll(99f,   16f),   // Birmanie ouest
        ll(94f,   16f),
        ll(93f,   20f),   // Birmanie sud-ouest
        ll(89f,   22f),   // Bangladesh
        ll(80f,   8f),    // Inde sud (point lat min)
        ll(72f,   20f),   // Mumbai
        ll(68f,   23f),   // Karachi
        ll(57f,   25f),   // Pakistan ouest / Golfe d'Oman
        ll(56f,   27f),
        ll(52f,   28f),   // Golfe Persique côté iranien
        ll(48f,   30f),
        ll(43f,   37f),   // Turquie est
        ll(36f,   36f),   // Turquie sud
        ll(34f,   36f),
        ll(30f,   36f),
        ll(26f,   37f),   // Égée
        ll(26f,   41f),   // Istanbul
        ll(28f,   41f),
        ll(28f,   43f),   // Bulgarie
        ll(23f,   45f),
        ll(20f,   46f),
        ll(18f,   43f),   // Adriatique est (Croatie)
        ll(15f,   42f),
        ll(13f,   45f),
        ll(12f,   45f),   // Venise
        ll(8f,    44f),   // Gênes
        ll(3f,    43f),   // Marseille
        ll(0f,    42f),   // Pyrénées est
        ll(-2f,   36f),   // Espagne sud (Almería)
        ll(-5f,   36f),   // Gibraltar
        ll(-10f,  35f)    // ferme
    )

    // ── Péninsule arabique (couleur sable, dessinée par-dessus) ─────────────
    private val ARABIA = poly(
        ll(34f,  29f),   // Sinaï
        ll(35f,  31f),
        ll(36f,  34f),
        ll(38f,  35f),
        ll(43f,  37f),
        ll(48f,  30f),
        ll(50f,  28f),
        ll(52f,  25f),
        ll(56f,  25f),   // Détroit d'Ormuz
        ll(59f,  22f),   // Oman
        ll(58f,  20f),
        ll(55f,  17f),
        ll(52f,  14f),   // Yémen est
        ll(45f,  12f),   // Aden
        ll(43f,  13f),   // Détroit Bab el-Mandeb
        ll(42f,  17f),
        ll(40f,  20f),
        ll(38f,  24f),   // Mer Rouge est
        ll(35f,  27f),
        ll(34f,  29f)    // ferme
    )

    // ── Sous-continent indien (pour bien marquer la pointe sud) ─────────────
    private val INDIA_TRIM = poly(
        ll(68f,  23f),
        ll(72f,  20f),
        ll(74f,  15f),   // Goa
        ll(76f,  10f),   // Kerala
        ll(77.5f, 8f),   // Cap Comorin
        ll(80f,   8f),
        ll(80f,  13f),   // Chennai
        ll(82f,  16f),
        ll(86f,  20f),
        ll(89f,  22f),
        ll(88f,  25f),
        ll(80f,  28f),
        ll(73f,  32f),
        ll(70f,  26f),
        ll(68f,  23f)
    )

    // ── Afrique ─────────────────────────────────────────────────────────────
    private val AFRICA = poly(
        ll(-10f,  35f),   // Maroc nord
        ll(-6f,   36f),
        ll(0f,    37f),
        ll(8f,    37f),   // Tunisie
        ll(11f,   33f),   // Libye nord-ouest
        ll(20f,   32f),
        ll(25f,   32f),
        ll(32f,   31f),   // Delta du Nil
        ll(34f,   31f),
        ll(34f,   29f),   // Sinaï côté Afrique
        ll(36f,   23f),   // Mer Rouge ouest
        ll(38f,   18f),
        ll(40f,   15f),
        ll(43f,   12f),   // Bab el-Mandeb
        ll(43f,   11f),
        ll(45f,   11f),   // Djibouti
        ll(48f,   11f),   // Somalie
        ll(51f,   12f),   // Cap Guardafui (Corne)
        ll(51f,   10f),
        ll(46f,   4f),
        ll(42f,   -2f),   // Côte kenyane
        ll(40f,   -5f),
        ll(40f,   -13f),  // Mozambique
        ll(36f,   -18f),
        ll(35f,   -24f),
        ll(32f,   -29f),
        ll(28f,   -33f),
        ll(22f,   -34f),  // Cap des Aiguilles
        ll(18f,   -34f),  // Cape Town
        ll(16f,   -29f),
        ll(13f,   -23f),  // Namibie
        ll(12f,   -16f),
        ll(13f,   -8f),
        ll(12f,   -5f),
        ll(9f,    2f),    // Golfe de Guinée est
        ll(6f,    4f),
        ll(3f,    6f),
        ll(-2f,   5f),
        ll(-8f,   4f),    // Ghana
        ll(-10f,  6f),
        ll(-13f,  8f),    // Sierra Leone
        ll(-15f,  12f),
        ll(-17f,  15f),
        ll(-17f,  21f),
        ll(-13f,  28f),
        ll(-9f,   30f),
        ll(-10f,  35f)
    )

    // ── Australie ───────────────────────────────────────────────────────────
    private val AUSTRALIA = poly(
        ll(143f, -11f),   // Cap York
        ll(146f, -19f),
        ll(150f, -22f),   // Côte est (Grande barrière)
        ll(153f, -28f),
        ll(151f, -34f),   // Sydney
        ll(149f, -37f),   // Melbourne (sud-est)
        ll(143f, -39f),
        ll(140f, -38f),
        ll(138f, -35f),   // Adélaïde
        ll(135f, -34f),
        ll(132f, -32f),
        ll(126f, -32f),
        ll(123f, -34f),
        ll(118f, -35f),
        ll(115f, -34f),   // Perth
        ll(114f, -27f),
        ll(114f, -22f),
        ll(118f, -20f),
        ll(122f, -17f),
        ll(126f, -14f),
        ll(130f, -12f),   // Darwin
        ll(135f, -12f),   // Golfe de Carpentarie ouest
        ll(137f, -16f),
        ll(141f, -16f),
        ll(141f, -12f),
        ll(143f, -11f)
    )

    // ── Antarctique (calotte sud) ───────────────────────────────────────────
    private val ANTARCTICA = poly(
        ll(-180f, -72f),
        ll(-160f, -75f),
        ll(-130f, -73f),
        ll(-100f, -74f),
        ll(-75f,  -72f),
        ll(-60f,  -64f),  // péninsule Antarctique
        ll(-55f,  -72f),
        ll(-40f,  -75f),
        ll(0f,    -71f),
        ll(40f,   -69f),
        ll(80f,   -67f),
        ll(120f,  -67f),
        ll(150f,  -68f),
        ll(170f,  -73f),
        ll(180f,  -73f),
        ll(180f,  -90f),
        ll(-180f, -90f),
        ll(-180f, -72f)
    )

    // ── Groenland ───────────────────────────────────────────────────────────
    private val GREENLAND = poly(
        ll(-45f, 83f),
        ll(-25f, 82f),
        ll(-18f, 76f),
        ll(-22f, 70f),
        ll(-32f, 66f),
        ll(-42f, 60f),   // Cap Farewell
        ll(-50f, 63f),
        ll(-55f, 67f),
        ll(-55f, 72f),
        ll(-65f, 76f),
        ll(-60f, 80f),
        ll(-50f, 83f),
        ll(-45f, 83f)
    )

    // ── Îles britanniques ──────────────────────────────────────────────────
    private val BRITISH_ISLES = poly(
        ll(-2f,  58f),   // Écosse nord
        ll(0f,   58f),
        ll(0f,   55f),
        ll(2f,   53f),
        ll(1f,   51f),
        ll(-1f,  50f),
        ll(-3f,  50f),
        ll(-6f,  50f),
        ll(-5f,  52f),
        ll(-4f,  54f),
        ll(-3f,  55f),
        ll(-5f,  56f),
        ll(-6f,  57f),
        ll(-5f,  58f),
        ll(-2f,  58f)
    )

    // ── Islande ─────────────────────────────────────────────────────────────
    private val ICELAND = poly(
        ll(-24f, 66f),
        ll(-20f, 67f),
        ll(-15f, 66f),
        ll(-14f, 65f),
        ll(-18f, 63.5f),
        ll(-23f, 64f),
        ll(-24f, 66f)
    )

    // ── Japon (Honshu) ──────────────────────────────────────────────────────
    private val JAPAN_HONSHU = poly(
        ll(141f, 41f),
        ll(141.5f, 39f),
        ll(140f, 35f),
        ll(139f, 35f),   // Tokyo
        ll(137f, 34f),
        ll(135f, 34f),   // Osaka
        ll(132f, 34f),
        ll(130f, 33f),   // Kyushu nord
        ll(131f, 31f),   // Kyushu sud
        ll(132f, 33f),
        ll(134f, 34f),
        ll(137f, 37f),
        ll(140f, 40f),
        ll(141f, 41f)
    )

    // ── Japon (Hokkaido) ────────────────────────────────────────────────────
    private val JAPAN_HOKKAIDO = poly(
        ll(140f, 41f),
        ll(143f, 41.5f),
        ll(145f, 43f),
        ll(145f, 45f),
        ll(142f, 45f),
        ll(140f, 43f),
        ll(140f, 41f)
    )

    // ── Madagascar ──────────────────────────────────────────────────────────
    private val MADAGASCAR = poly(
        ll(49f, -12f),
        ll(50f, -16f),
        ll(50f, -22f),
        ll(47f, -25f),
        ll(44f, -25f),
        ll(44f, -19f),
        ll(46f, -15f),
        ll(48f, -13f),
        ll(49f, -12f)
    )

    // ── Nouvelle-Zélande (île du Nord) ──────────────────────────────────────
    private val NEW_ZEALAND_N = poly(
        ll(173f, -34f),
        ll(176f, -37f),
        ll(178f, -38f),
        ll(177f, -41f),
        ll(175f, -41f),
        ll(173f, -39f),
        ll(173f, -34f)
    )

    // ── Nouvelle-Zélande (île du Sud) ──────────────────────────────────────
    private val NEW_ZEALAND_S = poly(
        ll(172f, -41f),
        ll(174f, -42f),
        ll(174f, -46f),
        ll(169f, -47f),
        ll(167f, -45f),
        ll(170f, -43f),
        ll(172f, -41f)
    )

    // ── Sumatra ─────────────────────────────────────────────────────────────
    private val SUMATRA = poly(
        ll(95f,   5f),
        ll(98f,   4f),
        ll(101f,  1f),
        ll(105f, -3f),
        ll(106f, -6f),
        ll(103f, -5f),
        ll(100f, -2f),
        ll(97f,   1f),
        ll(95f,   5f)
    )

    // ── Java ────────────────────────────────────────────────────────────────
    private val JAVA = poly(
        ll(105f, -6f),
        ll(110f, -7f),
        ll(114f, -8f),
        ll(114f, -9f),
        ll(110f, -8.5f),
        ll(105f, -7f),
        ll(105f, -6f)
    )

    // ── Bornéo ──────────────────────────────────────────────────────────────
    private val BORNEO = poly(
        ll(109f, 2f),
        ll(112f, 4f),
        ll(117f, 4f),
        ll(119f, 2f),
        ll(118f, -1f),
        ll(116f, -4f),
        ll(113f, -3f),
        ll(110f, -2f),
        ll(109f, 1f),
        ll(109f, 2f)
    )

    // ── Sulawesi (Célèbes) ──────────────────────────────────────────────────
    private val SULAWESI = poly(
        ll(119f, 1f),
        ll(121f, 1f),
        ll(122f, -2f),
        ll(123f, -5f),
        ll(121f, -5f),
        ll(120f, -3f),
        ll(119f, 1f)
    )

    // ── Nouvelle-Guinée ─────────────────────────────────────────────────────
    private val NEW_GUINEA = poly(
        ll(131f, -1f),
        ll(135f, -2f),
        ll(141f, -3f),
        ll(146f, -5f),
        ll(150f, -7f),
        ll(150f, -10f),
        ll(144f, -9f),
        ll(140f, -8f),
        ll(135f, -8f),
        ll(132f, -5f),
        ll(131f, -1f)
    )

    // ── Philippines (Luzon simplifié) ───────────────────────────────────────
    private val PHILIPPINES = poly(
        ll(120f, 18f),
        ll(122f, 18f),
        ll(122f, 13f),
        ll(125f, 10f),
        ll(126f, 7f),
        ll(124f, 6f),
        ll(122f, 9f),
        ll(120f, 14f),
        ll(120f, 18f)
    )

    // ── Sri Lanka ───────────────────────────────────────────────────────────
    private val SRI_LANKA = poly(
        ll(80f, 9.5f),
        ll(82f, 8f),
        ll(82f, 6f),
        ll(80f, 6f),
        ll(80f, 9.5f)
    )

    // ── Cuba ────────────────────────────────────────────────────────────────
    private val CUBA = poly(
        ll(-84f, 22f),
        ll(-80f, 23f),
        ll(-75f, 22f),
        ll(-74f, 20f),
        ll(-78f, 20f),
        ll(-82f, 21f),
        ll(-84f, 22f)
    )

    // ── Hispaniola (Haïti / Rép. Dominicaine) ───────────────────────────────
    private val HISPANIOLA = poly(
        ll(-74f, 20f),
        ll(-69f, 20f),
        ll(-68f, 18f),
        ll(-71f, 18f),
        ll(-74f, 19f),
        ll(-74f, 20f)
    )

    // ── Taïwan ──────────────────────────────────────────────────────────────
    private val TAIWAN = poly(
        ll(120f, 25f),
        ll(122f, 25f),
        ll(122f, 22f),
        ll(120f, 22f),
        ll(120f, 25f)
    )
}
