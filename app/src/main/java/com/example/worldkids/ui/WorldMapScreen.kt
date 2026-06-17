package com.example.worldkids.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.worldkids.data.Country
import com.example.worldkids.data.DemoData
import com.example.worldkids.data.Match
import com.example.worldkids.theme.GrassGreen
import com.example.worldkids.theme.MatchLine
import com.example.worldkids.theme.OceanDeep
import com.example.worldkids.theme.OceanLight
import kotlinx.coroutines.delay
import kotlin.math.hypot
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
    highlightedCountryIds: Set<String>,
    selectedMatch: Match?,
    focusCountryId: String?,
    onCountryTapped: (Country) -> Unit,
    showConfetti: Boolean,
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )

    val zoom by animateFloatAsState(
        targetValue = if (focusCountryId != null || selectedMatch != null) 1.05f else 1f,
        animationSpec = tween(600),
        label = "zoom"
    )

    val lineProgress by animateFloatAsState(
        targetValue = if (selectedMatch != null) 1f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "line"
    )

    val confetti = remember { mutableStateListOf<ConfettiParticle>() }
    var confettiTick by remember { mutableStateOf(0f) }

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
                confettiTick += 1f
                confetti.replaceAll { p ->
                    p.copy(x = p.x + p.vx, y = p.y + p.vy, vy = p.vy + 0.0003f)
                }
                delay(16)
            }
            confetti.clear()
        }
    }

    val tapRadius = if (tvMode) 48f else 36f

    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(listOf(OceanLight.copy(0.3f), OceanDeep.copy(0.15f))),
                RoundedCornerShape(20.dp)
            )
            .padding(8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(highlightedCountryIds, selectedMatch) {
                    detectTapGestures { offset ->
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        DemoData.countries.minByOrNull { country ->
                            val cx = country.mapX * w
                            val cy = country.mapY * h
                            hypot(offset.x - cx, offset.y - cy)
                        }?.let { nearest ->
                            val cx = nearest.mapX * w
                            val cy = nearest.mapY * h
                            if (hypot(offset.x - cx, offset.y - cy) < tapRadius * 2) {
                                onCountryTapped(nearest)
                            }
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val cx = w / 2
            val cy = h / 2

            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF4FC3F7), Color(0xFF0288D1), Color(0xFF01579B))
                ),
                size = size
            )

            // Continents stylisés
            drawContinentBlob(Color(0xFF81C784), cx * 0.35f, cy * 0.55f, w * 0.22f, h * 0.28f)
            drawContinentBlob(Color(0xFF66BB6A), cx * 0.28f, cy * 0.85f, w * 0.14f, h * 0.18f)
            drawContinentBlob(Color(0xFFA5D6A7), cx * 0.48f, cy * 0.38f, w * 0.16f, h * 0.22f)
            drawContinentBlob(Color(0xFF8BC34A), cx * 0.55f, cy * 0.55f, w * 0.18f, h * 0.25f)
            drawContinentBlob(Color(0xFF9CCC65), cx * 0.82f, cy * 0.42f, w * 0.14f, h * 0.16f)
            drawContinentBlob(Color(0xFF7CB342), cx * 0.85f, cy * 0.75f, w * 0.16f, h * 0.14f)

            // Ligne match
            selectedMatch?.let { match ->
                val a = DemoData.countryById(match.countryAId)
                val b = DemoData.countryById(match.countryBId)
                if (a != null && b != null) {
                    val start = Offset(a.mapX * w, a.mapY * h)
                    val end = Offset(b.mapX * w, b.mapY * h)
                    val mid = Offset((start.x + end.x) / 2, (start.y + end.y) / 2 - h * 0.12f)
                    val path = Path().apply {
                        moveTo(start.x, start.y)
                        quadraticTo(mid.x, mid.y, end.x, end.y)
                    }
                    drawPath(
                        path = path,
                        color = MatchLine.copy(alpha = 0.9f * lineProgress),
                        style = Stroke(width = 4f * zoom, cap = StrokeCap.Round)
                    )
                }
            }

            // Marqueurs pays
            DemoData.countries.forEach { country ->
                val px = country.mapX * w
                val py = country.mapY * h
                val highlighted = country.id in highlightedCountryIds || country.id == focusCountryId
                val countryColor = parseHex(country.colorHex)

                if (highlighted) {
                    val radius = (18f + 10f * pulse) * zoom
                    drawCircle(color = countryColor.copy(alpha = 0.25f), radius = radius * 1.8f, center = Offset(px, py))
                    drawCircle(color = countryColor.copy(alpha = 0.45f), radius = radius * 1.2f, center = Offset(px, py))
                }

                drawCircle(color = if (highlighted) countryColor else GrassGreen, radius = 8f * zoom, center = Offset(px, py))
                drawCircle(color = Color.White, radius = 3f, center = Offset(px, py))
            }

            // Confettis
            confetti.forEach { p ->
                drawCircle(
                    color = p.color.copy(alpha = 0.85f),
                    radius = p.size,
                    center = Offset(p.x * w, p.y * h)
                )
            }
        }

        Text(
            text = "Planisphère",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawContinentBlob(
    color: Color,
    cx: Float,
    cy: Float,
    rx: Float,
    ry: Float
) {
    drawOval(color = color.copy(alpha = 0.85f), topLeft = Offset(cx - rx, cy - ry), size = androidx.compose.ui.geometry.Size(rx * 2, ry * 2))
}

private fun parseHex(hex: String): Color {
    return Color(android.graphics.Color.parseColor(hex))
}
