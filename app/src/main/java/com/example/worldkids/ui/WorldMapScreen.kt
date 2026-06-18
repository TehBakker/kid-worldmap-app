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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import com.example.worldkids.data.Country
import com.example.worldkids.data.CountryShape
import com.example.worldkids.data.GeoBounds
import com.example.worldkids.data.GeoUtils
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
    onCountryTapped: (Country) -> Unit,
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
            shape.maxByOrNull { ring -> GeoBounds.of(listOf(ring))?.let { (it.maxX - it.minX) * (it.maxY - it.minY) } ?: 0f }
                ?.let { ring -> GeoBounds.of(listOf(ring))?.let { id to it } }
        }.toMap()
    }

    // Caméra animée (centre + zoom) pilotée par la sélection ET par les gestes
    val scope = rememberCoroutineScope()
    val camScale = remember { Animatable(1f) }
    val camCx = remember { Animatable(0.5f) }
    val camCy = remember { Animatable(0.5f) }

    LaunchedEffect(focusCountryId, selectedMatch?.id, highlightedCountryIds) {
        suspend fun flyTo(target: MapCamera, durationMs: Int) = coroutineScope {
            val (cx, cy) = clampCenter(target.centerX, target.centerY, target.scale)
            launch { camScale.animateTo(target.scale, tween(durationMs, easing = FastOutSlowInEasing)) }
            launch { camCx.animateTo(cx, tween(durationMs, easing = FastOutSlowInEasing)) }
            launch { camCy.animateTo(cy, tween(durationMs, easing = FastOutSlowInEasing)) }
        }

        when {
            focusCountryId != null -> {
                val country = countries.find { it.id == focusCountryId }
                if (country != null) {
                    // Phase 1 : on plonge vers le continent
                    flyTo(continentCamera(country, countries), 550)
                    delay(120)
                    // Phase 2 : on centre la caméra sur le pays et on zoome
                    flyTo(countryCamera(country, focusBounds), 800)
                }
            }
            selectedMatch != null || highlightedCountryIds.isNotEmpty() -> {
                val ids = highlightedCountryIds.toMutableSet()
                selectedMatch?.let { ids.add(it.countryAId); ids.add(it.countryBId) }
                flyTo(MapCameraUtils.computeCamera(countries, ids, emptySet()), 700)
            }
            else -> flyTo(MapCamera(), 700)
        }
    }

    val camera = MapCamera(camScale.value, camCx.value, camCy.value)
    // Toujours lire la caméra courante dans les gestionnaires de gestes, sans relancer pointerInput
    val cameraState = rememberUpdatedState(camera)

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
                shape.map { ring ->
                    Path().apply {
                        ring.forEachIndexed { i, pt ->
                            if (i == 0) moveTo(pt.x * wPx, pt.y * hPx) else lineTo(pt.x * wPx, pt.y * hPx)
                        }
                        close()
                    }
                }
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
                .pointerInput(countries.size, borders.size) {
                    detectTapGestures { offset ->
                        val (mapX, mapY) = MapCameraUtils.screenToMap(
                            offset.x, offset.y, size.width.toFloat(), size.height.toFloat(), cameraState.value
                        )
                        pickCountry(mapX, mapY, countries, borders, bordersBounds, tvMode)
                            ?.let(onCountryTapped)
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

                // 2) Pays mis en avant : halo + contour épais + remplissage avivé
                val highlightedIds = buildSet {
                    addAll(highlightedCountryIds)
                    focusCountryId?.let { add(it) }
                }
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

                // 3) Ligne de match
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

                // 4) Pastilles pour les pays mis en avant SANS frontières (petits pays/îles)
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

                // 5) Étiquettes des pays mis en avant
                highlightedIds.forEach { id ->
                    val c = countryById(id) ?: return@forEach
                    val b = focusBounds[id]
                    val lx = (b?.centerX ?: c.mapX) * w
                    val ly = (b?.minY ?: c.mapY) * h - 10f / camera.scale
                    drawCountryLabel(c.nameFr, lx, ly, camera.scale, tvMode)
                }

                confetti.forEach { p ->
                    drawCircle(p.color.copy(alpha = 0.85f), radius = p.size / camera.scale, center = Offset(p.x * w, p.y * h))
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

// ── Caméra : continent puis pays ────────────────────────────────────────────────
private fun continentCamera(country: Country?, countries: List<Country>): MapCamera {
    if (country == null) return MapCamera()
    val ids = countries.filter { it.continent == country.continent }.map { it.id }.toSet()
    return if (ids.size <= 1) MapCamera() else MapCameraUtils.computeCamera(countries, ids, emptySet())
}

/**
 * Caméra centrée sur le pays. Le centre est le point représentatif du pays (mapX/mapY),
 * ce qui garantit qu'on atterrit toujours SUR le pays. Le zoom est déduit de la taille
 * de sa masse principale (un petit pays est davantage agrandi qu'un grand).
 */
private fun countryCamera(country: Country, focusBounds: Map<String, GeoBounds>): MapCamera {
    val b = focusBounds[country.id]
    val scale = if (b != null) {
        val span = maxOf(b.maxX - b.minX, b.maxY - b.minY) + 0.14f
        (1f / span * 0.85f).coerceIn(1.8f, 6f)
    } else {
        4.5f
    }
    return MapCamera(scale = scale, centerX = country.mapX, centerY = country.mapY)
}

/** Empêche la caméra de sortir des bords de la carte (0..1). */
private fun clampCenter(cx: Float, cy: Float, scale: Float): Pair<Float, Float> {
    val half = (0.5f / scale).coerceAtMost(0.5f)
    return cx.coerceIn(half, 1f - half) to cy.coerceIn(half, 1f - half)
}

// ── Hit-test : on choisit le plus petit pays contenant le point ─────────────────
private fun pickCountry(
    mapX: Float,
    mapY: Float,
    countries: List<Country>,
    borders: Map<String, CountryShape>,
    bordersBounds: Map<String, GeoBounds>,
    tvMode: Boolean
): Country? {
    var best: Country? = null
    var bestArea = Float.MAX_VALUE
    countries.forEach { c ->
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

    // Repli : pays sans frontières (petits) → pastille la plus proche
    val tapRadius = if (tvMode) 0.03f else 0.022f
    return countries
        .filter { it.id !in borders }
        .map { it to ((it.mapX - mapX) * (it.mapX - mapX) + (it.mapY - mapY) * (it.mapY - mapY)) }
        .filter { it.second <= tapRadius * tapRadius }
        .minByOrNull { it.second }
        ?.first
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
