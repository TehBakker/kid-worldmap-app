package com.example.worldkids.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.worldkids.data.ContinentResolver
import com.example.worldkids.data.Country
import com.example.worldkids.data.CountryMapRules
import com.example.worldkids.data.CountryShape
import com.example.worldkids.data.GeoBounds
import com.example.worldkids.data.GeoUtils
import com.example.worldkids.data.ShapeUtils
import com.example.worldkids.data.Match
import com.example.worldkids.theme.MatchLine
import com.example.worldkids.theme.OceanDeep
import com.example.worldkids.theme.OceanLight
import com.example.worldkids.utils.MapCamera
import com.example.worldkids.utils.MapCameraUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float
)

@Composable
fun WorldMapScreen(
    countries: List<Country>,
    borders: Map<String, CountryShape>,
    highlightedCountryIds: Set<String>,
    selectedMatch: Match?,
    focusCountryId: String?,
    focusedContinent: String? = null,
    countryExtruded: Boolean = false,
    onCountryTapped: (Country) -> Unit,
    onBackToContinent: () -> Unit = {},
    onBackToWorld: () -> Unit = {},
    showConfetti: Boolean,
    countryById: (String) -> Country?,
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    // Boîtes englobantes complètes (territoires inclus) — pour le tri du hit-test
    val bordersBounds = remember(borders) {
        borders.mapNotNull { (id, shape) -> GeoBounds.of(shape)?.let { id to it } }.toMap()
    }
    // Boîte de la masse principale (plus grand anneau) — pour cadrer/étiqueter le pays
    // sans être faussé par les territoires lointains (Guyane, Alaska, Hawaï…).
    val focusBounds = remember(borders) {
        borders.mapNotNull { (id, shape) ->
            ShapeUtils.mainlandShape(shape).firstOrNull()
                ?.let { ring -> GeoBounds.of(listOf(ring))?.let { id to it } }
        }.toMap()
    }

    // Caméra animée (centre + zoom) pilotée par la sélection ET par les gestes
    val scope = rememberCoroutineScope()
    val camScale = remember { Animatable(1f) }
    val camCx = remember { Animatable(0.5f) }
    val camCy = remember { Animatable(0.5f) }

    // Deux niveaux animés : 1) le continent sort de la carte (0→1) et reste cliquable,
    // 2) le pays s'extrait du continent (0→1). Pilotés par focusedContinent + focusCountryId.
    val continentLift = remember { Animatable(0f) }
    val countryLift = remember { Animatable(0f) }
    // Pays/continent actuellement « présentés » (conservés pendant les animations de retour)
    var liftedId by remember { mutableStateOf<String?>(null) }
    var shownContinent by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(focusedContinent, focusCountryId, selectedMatch?.id, highlightedCountryIds) {
        suspend fun flyTo(target: MapCamera, durationMs: Int) = coroutineScope {
            val (cx, cy) = clampCenter(target.centerX, target.centerY, target.scale)
            launch { camScale.animateTo(target.scale, tween(durationMs, easing = FastOutSlowInEasing)) }
            launch { camCx.animateTo(cx, tween(durationMs, easing = FastOutSlowInEasing)) }
            launch { camCy.animateTo(cy, tween(durationMs, easing = FastOutSlowInEasing)) }
        }

        val validCountry = focusCountryId?.takeIf { id -> countries.any { it.id == id } }
        when {
            focusedContinent != null -> {
                val contChanged = shownContinent != focusedContinent
                shownContinent = focusedContinent
                if (contChanged) {
                    // Nouveau continent : on repart de zéro et on le fait sortir
                    countryLift.snapTo(0f)
                    continentLift.snapTo(0f)
                    liftedId = validCountry
                    flyTo(MapCamera(), 360)
                    continentLift.animateTo(1f, tween(620, easing = FastOutSlowInEasing))
                    if (validCountry != null) {
                        delay(130)
                        countryLift.animateTo(1f, tween(640, easing = FastOutSlowInEasing))
                    }
                } else {
                    // Même continent déjà présenté
                    if (continentLift.value < 1f) continentLift.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
                    when {
                        validCountry == null -> {
                            // Retour niveau 1 : le pays redescend, le continent reste
                            if (countryLift.value > 0f) countryLift.animateTo(0f, tween(360, easing = FastOutSlowInEasing))
                            liftedId = null
                        }
                        liftedId != validCountry -> {
                            // Échange : l'ancien redescend, le nouveau monte
                            if (countryLift.value > 0f) countryLift.animateTo(0f, tween(240, easing = FastOutSlowInEasing))
                            liftedId = validCountry
                            countryLift.snapTo(0f)
                            countryLift.animateTo(1f, tween(560, easing = FastOutSlowInEasing))
                        }
                        else -> {
                            if (countryLift.value < 1f) countryLift.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
                        }
                    }
                }
            }
            selectedMatch != null || highlightedCountryIds.isNotEmpty() -> {
                if (countryLift.value > 0f) countryLift.animateTo(0f, tween(240))
                continentLift.snapTo(0f)
                liftedId = null
                shownContinent = null
                val ids = highlightedCountryIds.toMutableSet()
                selectedMatch?.let { ids.add(it.countryAId); ids.add(it.countryBId) }
                flyTo(MapCameraUtils.computeCamera(countries, ids, emptySet()), 700)
            }
            else -> {
                coroutineScope {
                    launch { if (countryLift.value > 0f) countryLift.animateTo(0f, tween(320, easing = FastOutSlowInEasing)) }
                }
                coroutineScope {
                    launch { if (continentLift.value > 0f) continentLift.animateTo(0f, tween(420, easing = FastOutSlowInEasing)) }
                    launch { flyTo(MapCamera(), 600) }
                }
                liftedId = null
                shownContinent = null
            }
        }
    }

    // Ids des pays du continent présenté
    val continentIds = remember(shownContinent, countries) {
        if (shownContinent == null) emptyList()
        else countries
            .filter { ContinentResolver.mapContinent(it) == shownContinent }
            .map { it.id }
    }

    // Boîte englobante du continent (union des masses principales), pour le cadrer.
    val continentBounds = remember(continentIds) {
        val boxes = continentIds.mapNotNull { focusBounds[it] }
        if (boxes.isEmpty()) null
        else GeoBounds(
            minX = boxes.minOf { it.minX },
            minY = boxes.minOf { it.minY },
            maxX = boxes.maxOf { it.maxX },
            maxY = boxes.maxOf { it.maxY }
        )
    }

    val camera = MapCamera(camScale.value, camCx.value, camCy.value)
    // Toujours lire l'état courant dans les gestionnaires de gestes, sans relancer pointerInput
    val shownContinentState = rememberUpdatedState(shownContinent)
    val countryExtrudedState = rememberUpdatedState(countryExtruded)

    // Gestion des taps :
    // - pays extrait : clic hors silhouette métropolitaine → vue continent (pas d'autre pays)
    // - vue continent : clic sur un pays → zoom sur ce pays
    // - hors du continent → retour carte monde
    val handleTap = rememberUpdatedState<(Float, Float, Float, Float) -> Unit> { tapX, tapY, vw, vh ->
        val cont = shownContinent
        val cb = continentBounds
        if (cont != null && cb != null && continentLift.value > 0.4f) {
            val camS = camera.scale
            val camBx = vw / 2f - camera.centerX * vw * camera.scale
            val camBy = vh / 2f - camera.centerY * vh * camera.scale
            val (contS, contTx, contTy) = fitAffine(cb, vw, vh, vw / 2f, vh * 0.52f, vw * 0.90f, vh * 0.88f)
            val t1 = continentLift.value
            val cs = (1f - t1) * camS + t1 * contS
            val ctx = (1f - t1) * camBx + t1 * contTx
            val cty = (1f - t1) * camBy + t1 * contTy

            val lid = liftedId
            val t2 = countryLift.value
            var handled = false

            // 1) Pays extrait : clic sur la masse métropolitaine = rien ; hors masse = vue continent
            if (countryExtrudedState.value && lid != null && t2 > 0.15f) {
                val bb = focusBounds[lid]
                val mainland = borders[lid]?.let { ShapeUtils.mainlandShape(it) }
                if (bb != null && mainland != null) {
                    val (mx, my) = liftedCountryMapXY(tapX, tapY, vw, vh, bb, t2, contS, contTx, contTy)
                    if (GeoUtils.contains(mainland, mx, my)) {
                        handled = true
                    } else {
                        onBackToContinent()
                        handled = true
                    }
                }
            }

            // 2) Vue continent (pays non extrait) : sélection d'un pays
            if (!handled && !countryExtrudedState.value) {
                val mxC = ((tapX - ctx) / cs) / vw
                val myC = ((tapY - cty) / cs) / vh
                val contCountries = countries.filter { it.id in continentIds }
                val picked = pickCountry(mxC, myC, contCountries, borders, bordersBounds, tvMode, mapOnly = true)
                if (picked != null) {
                    onCountryTapped(picked)
                    handled = true
                }
            }

            // 3) Tap hors du bloc continent → retour carte monde
            if (!handled) {
                val left = cs * cb.minX * vw + ctx
                val right = cs * cb.maxX * vw + ctx
                val top = cs * cb.minY * vh + cty
                val bottom = cs * cb.maxY * vh + cty
                val m = 24f
                val outside = tapX < left - m || tapX > right + m || tapY < top - m || tapY > bottom + m
                if (outside) {
                    if (countryExtrudedState.value) onBackToContinent() else onBackToWorld()
                }
            }
        } else {
            val (mapX, mapY) = MapCameraUtils.screenToMap(tapX, tapY, vw, vh, camera)
            pickCountry(mapX, mapY, countries, borders, bordersBounds, tvMode, mapOnly = true)?.let(onCountryTapped)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )

    val lineProgress by animateFloatAsState(
        targetValue = if (selectedMatch != null) 1f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "line"
    )

    val confetti = remember { mutableStateListOf<ConfettiParticle>() }
    LaunchedEffect(showConfetti) {
        if (showConfetti) {
            confetti.clear()
            repeat(40) {
                confetti.add(
                    ConfettiParticle(
                        x = Random.nextFloat(),
                        y = Random.nextFloat() * 0.3f,
                        vx = (Random.nextFloat() - 0.5f) * 0.004f,
                        vy = Random.nextFloat() * 0.006f + 0.002f,
                        color = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta).random(),
                        size = Random.nextFloat() * 6f + 4f
                    )
                )
            }
            repeat(60) {
                confetti.replaceAll { p -> p.copy(x = p.x + p.vx, y = p.y + p.vy, vy = p.vy + 0.0003f) }
                delay(16)
            }
            confetti.clear()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .background(
                Brush.verticalGradient(listOf(OceanLight.copy(0.3f), OceanDeep.copy(0.15f))),
                RoundedCornerShape(20.dp)
            )
            .padding(8.dp)
    ) {
        val density = LocalDensity.current
        val wPx = with(density) { maxWidth.toPx() }
        val hPx = with(density) { maxHeight.toPx() }

        // Tracés des frontières en pixels (reconstruits seulement si la taille change)
        val countryPaths = remember(borders, wPx, hPx) {
            borders.mapValues { (_, shape) ->
                shapeToPaths(shape, wPx, hPx)
            }
        }
        // Tracés métropolitains (masse principale) pour l'extrusion pays
        val mainlandPaths = remember(borders, wPx, hPx) {
            borders.mapValues { (_, shape) ->
                shapeToPaths(ShapeUtils.mainlandShape(shape), wPx, hPx)
            }
        }

        // Couleur distincte par pays
        val countryColors = remember(countries) {
            countries.associate { it.id to CountryMapVisuals.fillColor(it.continent, it.id) }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                // Déplacement (pan) + zoom (pincement) à un ou deux doigts
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Pas de pan/zoom manuel en vue continent (évite les conflits)
                        if (shownContinentState.value != null) return@detectTransformGestures
                        scope.launch {
                            val w = size.width.toFloat()
                            val h = size.height.toFloat()
                            val newScale = (camScale.value * zoom).coerceIn(1f, MapCameraUtils.MAX_SCALE)
                            val ncx = camCx.value - pan.x / (newScale * w)
                            val ncy = camCy.value - pan.y / (newScale * h)
                            val (cx, cy) = clampCenter(ncx, ncy, newScale)
                            camScale.snapTo(newScale)
                            camCx.snapTo(cx)
                            camCy.snapTo(cy)
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        handleTap.value(offset.x, offset.y, size.width.toFloat(), size.height.toFloat())
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val borderStroke = (if (tvMode) 1.6f else 1.2f) / camera.scale

            withTransform({
                translate(w / 2f, h / 2f)
                scale(camera.scale, camera.scale)
                translate(-camera.centerX * w, -camera.centerY * h)
            }) {
                // Océan (large, pour éviter le fond visible quand on zoome au bord)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFB8E0EC), Color(0xFF7FB6D1), Color(0xFF4A7FA0)),
                        startY = 0f,
                        endY = h
                    ),
                    topLeft = Offset(-w, -h),
                    size = androidx.compose.ui.geometry.Size(w * 3f, h * 3f)
                )

                // Graticule discret
                WorldContinents.graticuleLines(w, h).forEach { (start, end) ->
                    drawLine(Color.White.copy(alpha = 0.16f), start, end, strokeWidth = 1f / camera.scale)
                }
                drawLine(Color.White.copy(alpha = 0.28f), Offset(0f, h / 2f), Offset(w, h / 2f), strokeWidth = 1.4f / camera.scale)
                drawLine(Color.White.copy(alpha = 0.28f), Offset(w / 2f, 0f), Offset(w / 2f, h), strokeWidth = 1.4f / camera.scale)

                // 1) Tous les pays : remplissage + contour
                countryPaths.forEach { (id, paths) ->
                    val fill = countryColors[id] ?: CountryMapVisuals.fillColor("", id)
                    paths.forEach { p ->
                        drawPath(p, color = fill)
                        drawPath(p, color = CountryMapVisuals.BORDER, style = Stroke(width = borderStroke))
                    }
                }

                if (liftedId == null) {
                    // ── Mode « match / groupe » : halo classique sur les pays ─────────
                    val highlightedIds = highlightedCountryIds
                    highlightedIds.forEach { id ->
                        val paths = countryPaths[id] ?: return@forEach
                        val accent = countryColors[id] ?: Color(0xFFFFC107)
                        paths.forEach { p ->
                            drawPath(p, color = Color.White.copy(alpha = 0.22f + 0.18f * pulse))
                            drawPath(
                                p,
                                color = Color(0xFFFFD54F),
                                style = Stroke(width = (if (tvMode) 4.5f else 3.2f) / camera.scale)
                            )
                            drawPath(
                                p,
                                color = accent.copy(alpha = 0.9f),
                                style = Stroke(width = (if (tvMode) 2f else 1.4f) / camera.scale)
                            )
                        }
                    }

                    // Ligne de match
                    selectedMatch?.let { match ->
                        val a = countryById(match.countryAId)
                        val b = countryById(match.countryBId)
                        if (a != null && b != null) {
                            val start = Offset(a.mapX * w, a.mapY * h)
                            val end = Offset(b.mapX * w, b.mapY * h)
                            val mid = Offset((start.x + end.x) / 2, (start.y + end.y) / 2 - h * 0.1f)
                            val path = Path().apply {
                                moveTo(start.x, start.y)
                                quadraticTo(mid.x, mid.y, end.x, end.y)
                            }
                            drawPath(
                                path = path,
                                color = MatchLine.copy(alpha = 0.9f * lineProgress),
                                style = Stroke(width = 4f / camera.scale, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Pastilles pour les pays mis en avant SANS frontières (petits pays/îles)
                    highlightedIds.forEach { id ->
                        if (countryPaths.containsKey(id)) return@forEach
                        val c = countryById(id) ?: return@forEach
                        val px = c.mapX * w
                        val py = c.mapY * h
                        val r = 9f / camera.scale
                        drawCircle(Color(0xFFFFD54F), radius = r * 1.7f * (0.7f + 0.3f * pulse), center = Offset(px, py))
                        drawCircle(countryColors[id] ?: Color(0xFFE53935), radius = r, center = Offset(px, py))
                        drawCircle(Color.White, radius = r * 0.4f, center = Offset(px, py))
                    }

                    // Étiquettes des pays mis en avant
                    highlightedIds.forEach { id ->
                        val c = countryById(id) ?: return@forEach
                        val b = focusBounds[id]
                        val lx = (b?.centerX ?: c.mapX) * w
                        val ly = (b?.minY ?: c.mapY) * h - 10f / camera.scale
                        drawCountryLabel(c.nameFr, lx, ly, camera.scale, tvMode)
                    }
                }

                confetti.forEach { p ->
                    drawCircle(p.color.copy(alpha = 0.85f), radius = p.size / camera.scale, center = Offset(p.x * w, p.y * h))
                }
            }

            // ── Deux niveaux : continent qui sort (cliquable), puis pays qui s'en extrait ─
            val lid = liftedId
            val t1 = continentLift.value
            val t2 = countryLift.value
            val cb = continentBounds
            if (shownContinent != null && cb != null && t1 > 0.001f) {
                // Pénombre sur la carte pour faire ressortir le continent
                drawRect(Color.Black.copy(alpha = 0.34f * t1), size = size)

                // Transformation caméra courante : screen = a * P + camB
                val camS = camera.scale
                val camBx = w / 2f - camera.centerX * w * camera.scale
                val camBy = h / 2f - camera.centerY * h * camera.scale

                // Transformation « continent présenté » (cadré, centré)
                val (contS, contTx, contTy) = fitAffine(cb, w, h, w / 2f, h * 0.52f, w * 0.90f, h * 0.88f)

                // 1) Continent : interpolation caméra → présentation
                val cs = (1f - t1) * camS + t1 * contS
                val ctx = (1f - t1) * camBx + t1 * contTx
                val cty = (1f - t1) * camBy + t1 * contTy

                // Ombre portée du bloc continent
                if (t1 > 0.05f) {
                    withTransform({
                        translate(ctx + 6f * t1, cty + 22f * t1)
                        scale(cs, cs, pivot = Offset.Zero)
                    }) {
                        continentIds.forEach { id ->
                            countryPaths[id]?.forEach { p -> drawPath(p, color = Color.Black.copy(alpha = 0.10f * t1)) }
                        }
                    }
                }

                // Pays du continent, détourés
                withTransform({
                    translate(ctx, cty)
                    scale(cs, cs, pivot = Offset.Zero)
                }) {
                    continentIds.forEach { id ->
                        val paths = countryPaths[id] ?: return@forEach
                        val fill = countryColors[id] ?: CountryMapVisuals.fillColor("", id)
                        // Le pays sélectionné s'efface à mesure qu'il s'extrait (empreinte)
                        val a = if (id == lid) (1f - 0.82f * t2) else 1f
                        paths.forEach { p ->
                            drawPath(p, color = fill.copy(alpha = a))
                            drawPath(p, color = Color.White.copy(alpha = 0.9f), style = Stroke(width = 2.2f / cs))
                        }
                    }
                }

                // 2) Pays extrait : interpolation présentation continent → pays levé
                val c = lid?.let { countryById(it) }
                val bb = lid?.let { focusBounds[it] }
                val paths = lid?.let { mainlandPaths[it] }
                if (c != null && bb != null && paths != null && t2 > 0.001f) {
                    val (cntS, cntTx, cntTy) = fitAffine(bb, w, h, w / 2f, h * 0.40f, w * 0.50f, h * 0.58f)
                    val ps = (1f - t2) * contS + t2 * cntS
                    val ptx = (1f - t2) * contTx + t2 * cntTx
                    val pty = (1f - t2) * contTy + t2 * cntTy
                    val accent = countryColors[c.id] ?: Color(0xFFFFC107)

                        // Ombre portée du pays
                        for (k in 3 downTo 1) {
                            val off = 28f * t2 * k / 3f
                            withTransform({
                                translate(ptx + off * 0.25f, pty + off)
                                scale(ps, ps, pivot = Offset.Zero)
                            }) {
                                paths.forEach { p -> drawPath(p, color = Color.Black.copy(alpha = 0.10f * t2)) }
                            }
                        }

                        // Le pays lui-même
                        withTransform({
                            translate(ptx, pty)
                            scale(ps, ps, pivot = Offset.Zero)
                        }) {
                            paths.forEach { p ->
                                drawPath(p, color = accent)
                                drawPath(p, color = Color.White, style = Stroke(width = 3f / ps))
                                drawPath(p, color = Color(0xFFFFC107), style = Stroke(width = 1.4f / ps))
                            }
                        }

                        // Capitale pointée (vraie position de la capitale) en espace écran
                        val capX = ps * c.capX * w + ptx
                        val capY = ps * c.capY * h + pty
                        val markerAlpha = ((t2 - 0.45f) / 0.55f).coerceIn(0f, 1f)
                        if (markerAlpha > 0f) {
                            val r = (if (tvMode) 9f else 7f)
                            drawCircle(Color.White.copy(alpha = markerAlpha), radius = r * (1.4f + 0.4f * pulse), center = Offset(capX, capY))
                            drawCircle(Color(0xFFE53935).copy(alpha = markerAlpha), radius = r, center = Offset(capX, capY))
                            drawCircle(Color.White.copy(alpha = markerAlpha), radius = r * 0.38f, center = Offset(capX, capY))
                            if (c.capital.isNotBlank() && c.capital != "—") {
                                drawCapitalLabel(c.capital, capX, capY - r - 6f, markerAlpha, tvMode)
                            }
                        }

                    // Nom du pays (titre) au-dessus du pays extrait
                    if (t2 > 0.4f) {
                        val titleY = h * 0.40f - (bb.maxY - bb.minY) * h * cntS / 2f - 18f
                        drawCountryLabel(c.nameFr, w / 2f, titleY.coerceAtLeast(28f), 1f, tvMode)
                    }
                }
            }
        }

        Text(
            text = "Planisphère du monde",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
        )
    }
}

private fun liftedCountryMapXY(
    tapX: Float,
    tapY: Float,
    vw: Float,
    vh: Float,
    bb: GeoBounds,
    t2: Float,
    contS: Float,
    contTx: Float,
    contTy: Float
): Pair<Float, Float> {
    val (cntS, cntTx, cntTy) = fitAffine(bb, vw, vh, vw / 2f, vh * 0.40f, vw * 0.50f, vh * 0.58f)
    val ps = (1f - t2) * contS + t2 * cntS
    val ptx = (1f - t2) * contTx + t2 * cntTx
    val pty = (1f - t2) * contTy + t2 * cntTy
    val mx = ((tapX - ptx) / ps) / vw
    val my = ((tapY - pty) / ps) / vh
    return mx to my
}

/** Empêche la caméra de sortir des bords de la carte (0..1). */
private fun clampCenter(cx: Float, cy: Float, scale: Float): Pair<Float, Float> {
    val half = (0.5f / scale).coerceAtMost(0.5f)
    return cx.coerceIn(half, 1f - half) to cy.coerceIn(half, 1f - half)
}

private fun shapeToPaths(shape: CountryShape, wPx: Float, hPx: Float): List<Path> =
    shape.map { ring ->
        Path().apply {
            ring.forEachIndexed { i, pt ->
                if (i == 0) moveTo(pt.x * wPx, pt.y * hPx) else lineTo(pt.x * wPx, pt.y * hPx)
            }
            close()
        }
    }

// ── Hit-test : on choisit le plus petit pays cliquable contenant le point ─────
private fun pickCountry(
    mapX: Float,
    mapY: Float,
    countries: List<Country>,
    borders: Map<String, CountryShape>,
    bordersBounds: Map<String, GeoBounds>,
    tvMode: Boolean,
    mapOnly: Boolean = false
): Country? {
    var best: Country? = null
    var bestArea = Float.MAX_VALUE
    countries.forEach { c ->
        if (mapOnly && !CountryMapRules.isClickableOnMap(c.id, borders)) return@forEach
        val shape = borders[c.id] ?: return@forEach
        val b = bordersBounds[c.id]
        if (b != null && (mapX < b.minX || mapX > b.maxX || mapY < b.minY || mapY > b.maxY)) return@forEach
        if (GeoUtils.contains(shape, mapX, mapY)) {
            val area = b?.let { (it.maxX - it.minX) * (it.maxY - it.minY) } ?: Float.MAX_VALUE
            if (area < bestArea) {
                bestArea = area
                best = c
            }
        }
    }
    if (best != null) return best

    if (mapOnly) return null

    // Repli : pays sans frontières (petits) → pastille la plus proche (recherche conseillée)
    val tapRadius = if (tvMode) 0.03f else 0.022f
    return countries
        .filter { it.id !in borders }
        .map { it to ((it.mapX - mapX) * (it.mapX - mapX) + (it.mapY - mapY) * (it.mapY - mapY)) }
        .filter { it.second <= tapRadius * tapRadius }
        .minByOrNull { it.second }
        ?.first
}

/**
 * Transformation affine (échelle uniforme + translation) cadrant une boîte normalisée
 * dans une zone écran centrée sur (cx, cy) et tenant dans (fillW × fillH).
 * Retourne (scale, tx, ty) tel que screen = scale * pointPx + (tx, ty).
 */
private fun fitAffine(
    b: GeoBounds,
    w: Float,
    h: Float,
    cx: Float,
    cy: Float,
    fillW: Float,
    fillH: Float
): Triple<Float, Float, Float> {
    val bw = ((b.maxX - b.minX) * w).coerceAtLeast(1f)
    val bh = ((b.maxY - b.minY) * h).coerceAtLeast(1f)
    val s = minOf(fillW / bw, fillH / bh)
    return Triple(s, cx - b.centerX * w * s, cy - b.centerY * h * s)
}

private fun DrawScope.drawCapitalLabel(text: String, x: Float, y: Float, alpha: Float, tvMode: Boolean) {
    val a = (alpha.coerceIn(0f, 1f) * 255).toInt()
    drawContext.canvas.nativeCanvas.apply {
        val size = if (tvMode) 26f else 22f
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.argb(a, 255, 255, 255)
            textSize = size
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
            setShadowLayer(5f, 0f, 0f, android.graphics.Color.argb(a, 0, 0, 0))
        }
        val bg = android.graphics.Paint().apply {
            color = android.graphics.Color.argb((a * 0.85f).toInt(), 198, 40, 40)
        }
        val tw = paint.measureText(text)
        val pad = 7f
        drawRoundRect(
            x - tw / 2 - pad,
            y - size - pad,
            x + tw / 2 + pad,
            y + pad * 0.6f,
            6f, 6f, bg
        )
        drawText(text, x, y, paint)
    }
}

private fun DrawScope.drawCountryLabel(text: String, x: Float, y: Float, camScale: Float, tvMode: Boolean) {
    drawContext.canvas.nativeCanvas.apply {
        val size = (if (tvMode) 30f else 26f) / camScale
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = size
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
            setShadowLayer(6f / camScale, 0f, 0f, android.graphics.Color.BLACK)
        }
        val bg = android.graphics.Paint().apply {
            color = android.graphics.Color.argb(210, 33, 33, 33)
        }
        val tw = paint.measureText(text)
        val pad = 8f / camScale
        drawRoundRect(
            x - tw / 2 - pad,
            y - size - pad,
            x + tw / 2 + pad,
            y + pad * 0.6f,
            6f / camScale, 6f / camScale, bg
        )
        drawText(text, x, y, paint)
    }
}
