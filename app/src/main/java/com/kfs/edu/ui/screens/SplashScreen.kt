package com.kfs.edu.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kfs.edu.R
import com.kfs.edu.ui.ds.Motion
import com.kfs.edu.ui.ds.T
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot

/** circle(150% at 50% 44%) that collapses to nothing, like the artifact's splash exit. */
private class IrisShape(private val fraction: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val r = hypot(size.width, size.height) * fraction
        val path = Path().apply { addOval(Rect(Offset(size.width / 2f, size.height * .44f), r)) }
        return Outline.Generic(path)
    }
}

/**
 * Splash: the ring draws itself (1.2s), the logo pops with a bounce, the texts rise, then after 2.3s
 * (or a tap) the screen closes to a point over 950ms. [onCollapse] fires when the close begins.
 */
@Composable
fun SplashOverlay(onCollapse: () -> Unit, onFinished: () -> Unit) {
    val scope = rememberCoroutineScope()
    val ring = remember { Animatable(0f) }
    val logo = remember { Animatable(0f) }
    val t1 = remember { Animatable(0f) }
    val t2 = remember { Animatable(0f) }
    val close = remember { Animatable(1f) }
    var started by remember { mutableStateOf(false) }

    fun go() {
        if (started) return
        started = true
        onCollapse()
        scope.launch { close.animateTo(0f, tween(950, easing = Motion.SplashClose)); onFinished() }
    }

    LaunchedEffect(Unit) {
        launch { delay(150); ring.animateTo(1f, tween(1200, easing = Motion.Spring)) }
        launch { delay(100); logo.animateTo(1f, tween(800, easing = Motion.Bounce)) }
        launch { delay(550); t1.animateTo(1f, tween(800, easing = Motion.Spring)) }
        launch { delay(700); t2.animateTo(1f, tween(800, easing = Motion.Spring)) }
        delay(2300)
        go()
    }

    Box(
        Modifier.fillMaxSize()
            .graphicsLayer { shape = IrisShape(close.value); clip = true }
            .background(Brush.verticalGradient(0f to Color(0xFF0D3D75), 1f to Color(0xFF1A5CAA)))
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00924B), Color(0x0000924B)),
                        center = Offset(size.width / 2f, size.height * 1.08f), radius = size.height * .6f,
                    ),
                )
            }
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { go() },
        contentAlignment = Alignment.Center,
    ) {
        val fade = (close.value * 3.3f).coerceIn(0f, 1f)
        Column(Modifier.graphicsLayer { alpha = fade }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Box(Modifier.size(132.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(132.dp)) {
                    drawArc(
                        color = Color.White, startAngle = -90f, sweepAngle = 360f * ring.value, useCenter = false,
                        topLeft = Offset(1.2.dp.toPx(), 1.2.dp.toPx()), size = Size(size.width - 2.4.dp.toPx(), size.height - 2.4.dp.toPx()),
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
                Box(
                    Modifier.size(100.dp).graphicsLayer { val s = .4f + .6f * logo.value; scaleX = s; scaleY = s; alpha = logo.value.coerceIn(0f, 1f) }
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { Image(painterResource(R.drawable.logo_small), contentDescription = null, modifier = Modifier.width(70.dp)) }
            }
            Box(Modifier.graphicsLayer { alpha = t1.value; translationY = (1f - t1.value) * 16.dp.toPx() }) {
                T("جامعة كفر الشيخ", 24f, FontWeight.Bold, Color.White, lh = 1.3f)
            }
            Box(Modifier.graphicsLayer { alpha = t2.value * .8f; translationY = (1f - t2.value) * 16.dp.toPx() }) {
                T("البوابة الرسمية للطلاب", 13f, FontWeight.Normal, Color.White, lh = 1.3f)
            }
        }
    }
}
