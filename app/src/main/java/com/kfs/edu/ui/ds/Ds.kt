package com.kfs.edu.ui.ds

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.kfs.edu.R
import com.kfs.edu.ui.theme.Ds
import com.kfs.edu.ui.theme.MiSansArabicFont
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/** Easing curves copied from the design artifact's CSS custom properties. */
object Motion {
    val Spring = CubicBezierEasing(.22f, 1f, .36f, 1f)
    val Bounce = CubicBezierEasing(.34f, 1.5f, .64f, 1f)
    val SplashClose = CubicBezierEasing(.76f, 0f, .18f, 1f)
    val TabIndicator = CubicBezierEasing(.34f, 1.35f, .64f, 1f)
}

/** Shows a toast: (message, icon name). */
val LocalToast = staticCompositionLocalOf<(String, String) -> Unit> { { _, _ -> } }

// ---------------------------------------------------------------- text

@Composable
fun T(
    text: String,
    size: Float = 15f,
    weight: FontWeight = FontWeight.Normal,
    color: Color = Ds.c.label,
    lh: Float = 1.5f,
    align: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        textAlign = align,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            fontFamily = MiSansArabicFont,
            fontSize = size.sp,
            lineHeight = (size * lh).sp,
            fontWeight = weight,
            color = color,
        ),
    )
}

// ---------------------------------------------------------------- icons

@Composable
fun DsIcon(name: String, tint: Color, size: Dp = 22.dp, stroke: Float = 1.8f, modifier: Modifier = Modifier) {
    val path = remember(name) { PathParser().parsePathString(DsPaths[name] ?: "").toPath() }
    Canvas(modifier.size(size)) {
        val s = this.size.width / 24f
        withTransform({ scale(s, s, pivot = Offset.Zero) }) {
            drawPath(path, tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

// ---------------------------------------------------------------- motion modifiers

/**
 * Press feedback: the element swells while held, then settles back with a springy overshoot.
 * [scaleTo] keeps its old meaning as the press strength (0.88 is stronger than 0.98); the target is mirrored above 1.
 */
fun Modifier.dsClick(scaleTo: Float = .95f, enabled: Boolean = true, onClick: () -> Unit): Modifier = composed {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val grow = (1f + (1f - scaleTo) * 1.1f).coerceIn(1.015f, 1.14f)
    val sc by animateFloatAsState(
        if (pressed) grow else 1f,
        if (pressed) spring(dampingRatio = .72f, stiffness = 260f) else spring(dampingRatio = .38f, stiffness = 320f),
        label = "press",
    )
    this
        .graphicsLayer { scaleX = sc; scaleY = sc }
        .clickable(interactionSource = src, indication = null, enabled = enabled, onClick = onClick)
}

/** Entrance: fade in and rise 16dp, 700ms spring curve, staggered by 55ms per index. */
@Composable
fun Modifier.rise(index: Int, key: Any = Unit): Modifier {
    val a = remember(key) { Animatable(0f) }
    LaunchedEffect(key) {
        delay(index * 55L)
        a.animateTo(1f, tween(700, easing = Motion.Spring))
    }
    val px = with(LocalDensity.current) { 16.dp.toPx() }
    return this.graphicsLayer { alpha = a.value; translationY = (1f - a.value) * px }
}

/** Pop-in used for service tiles after the segmented control switches (550ms, 28ms stagger). */
@Composable
fun Modifier.pop(index: Int, key: Any): Modifier {
    val a = remember(key) { Animatable(0f) }
    LaunchedEffect(key) {
        delay(index * 28L)
        a.animateTo(1f, tween(550, easing = Motion.Bounce))
    }
    return this.graphicsLayer {
        val s = .4f + .6f * a.value
        scaleX = s; scaleY = s; alpha = a.value.coerceIn(0f, 1f)
    }
}

// ---------------------------------------------------------------- images

@Composable
fun DsPic(url: String?, seed: Int, modifier: Modifier = Modifier) {
    val g = CardGradients[seed.mod(CardGradients.size)]
    Box(modifier.background(Brush.linearGradient(listOf(Color(g.first), Color(g.second))))) {
        Image(
            painter = painterResource(R.drawable.logo_small), contentDescription = null,
            modifier = Modifier.align(Alignment.Center).fillMaxSize(.4f),
            alpha = .22f, colorFilter = ColorFilter.tint(Color.White),
        )
        if (!url.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(600).build(),
                contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun DsMono(title: String, logoUrl: String?, fg: Color, bg: Color, size: Dp = 42.dp, textSize: Float = 17f) {
    val letter = title.removePrefix("كلية").trim().take(1)
    Box(Modifier.size(size).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
        T(letter, textSize, FontWeight.Bold, fg)
        if (!logoUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(logoUrl).crossfade(400).build(),
                contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize(),
                loading = {}, error = {},
                success = {
                    val imageScope = this
                    Box(Modifier.fillMaxSize().background(Color.White, CircleShape).padding(2.dp)) {
                        imageScope.SubcomposeAsyncImageContent()
                    }
                },
            )
        }
    }
}

// ---------------------------------------------------------------- buttons

enum class BtnStyle { Solid, Soft, Gray, Red }

@Composable
fun DsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null,
    style: BtnStyle = BtnStyle.Solid,
    big: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val c = Ds.c
    val bg = when (style) {
        BtnStyle.Solid -> c.tint; BtnStyle.Soft -> c.tintSoft; BtnStyle.Gray -> c.fill; BtnStyle.Red -> c.redSoft
    }
    val fg = when (style) {
        BtnStyle.Solid -> c.onTint; BtnStyle.Soft -> c.tint; BtnStyle.Gray -> c.label; BtnStyle.Red -> c.red
    }
    val h = if (big) 52.dp else 42.dp
    Box(
        modifier
            .height(h)
            .graphicsLayer { alpha = if (enabled) 1f else .55f }
            .clip(RoundedCornerShape(h / 2))
            .background(bg)
            .dsClick(enabled = enabled && !loading, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(color = fg, strokeWidth = 2.5.dp, modifier = Modifier.size(20.dp))
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (icon != null) DsIcon(icon, fg, if (big) 20.dp else 18.dp, 1.8f)
                T(text, if (big) 16f else 14.5f, FontWeight.SemiBold, fg, lh = 1.3f)
            }
        }
    }
}

/** Round glass button used in navigation bars (38dp). */
@Composable
fun GlassCircle(icon: String, desc: String, onClick: () -> Unit, badge: String? = null, tint: Color = Ds.c.label) {
    val c = Ds.c
    Box(Modifier.size(38.dp)) {
        Box(
            Modifier.size(38.dp).dsClick(.88f, onClick = onClick)
                .clip(CircleShape).background(c.surface).border(.5.dp, c.sep, CircleShape),
            contentAlignment = Alignment.Center,
        ) { DsIcon(icon, tint, 20.dp) }
        if (badge != null) {
            val a = remember { Animatable(0f) }
            LaunchedEffect(Unit) { delay(900); a.animateTo(1f, tween(600, easing = Motion.Bounce)) }
            Box(
                Modifier.align(Alignment.TopEnd).offset(x = 3.dp, y = (-3).dp).height(18.dp).width(18.dp)
                    .graphicsLayer { scaleX = a.value; scaleY = a.value }
                    .background(c.red, CircleShape),
                contentAlignment = Alignment.Center,
            ) { T(badge, 11f, FontWeight.SemiBold, Color.White, lh = 1.1f) }
        }
    }
}

// ---------------------------------------------------------------- segmented control

@Composable
fun DsSeg(labels: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val c = Ds.c
    val pos by animateFloatAsState(selected.toFloat(), tween(500, easing = Motion.Bounce), label = "seg")
    BoxWithConstraints(modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(c.fill).padding(3.dp)) {
        val w = maxWidth / labels.size
        val wPx = with(LocalDensity.current) { w.toPx() }
        // Modifier.offset is layout-direction aware, so the thumb follows the RTL order.
        Box(
            Modifier.offset { IntOffset((pos * wPx).roundToInt(), 0) }.width(w).height(34.dp)
                .background(c.surface, RoundedCornerShape(9.dp)),
        )
        Row(Modifier.fillMaxWidth()) {
            labels.forEachIndexed { i, l ->
                Box(
                    Modifier.weight(1f).height(34.dp).clickable(
                        interactionSource = remember { MutableInteractionSource() }, indication = null,
                    ) { onSelect(i) },
                    contentAlignment = Alignment.Center,
                ) { T(l, 13.5f, FontWeight.SemiBold, if (i == selected) c.label else c.label2, lh = 1.3f) }
            }
        }
    }
}

// ---------------------------------------------------------------- lists

@Composable
fun DsList(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val c = Ds.c
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(c.surface)
            .border(.5.dp, c.sep, RoundedCornerShape(22.dp)),
        content = content,
    )
}

@Composable
fun DsDivider() {
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(.5.dp).background(Ds.c.sep))
}

@Composable
fun GroupHeader(text: String, color: Color = Ds.c.label2, weight: FontWeight = FontWeight.Medium) {
    T(text, 13f, weight, color, modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 20.dp, bottom = 7.dp))
}

@Composable
fun DsRow(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val c = Ds.c
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val bg by animateFloatAsState(if (pressed) 1f else 0f, tween(200), label = "rowbg")
    Row(
        modifier.fillMaxWidth().heightIn(min = 52.dp)
            .background(c.fill.copy(alpha = c.fill.alpha * bg))
            .let { if (onClick != null) it.clickable(interactionSource = src, indication = null, onClick = onClick) else it }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
fun RowIconBox(icon: String, color: Color) {
    Box(Modifier.size(32.dp).background(color, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
        DsIcon(icon, Color.White, 18.dp, 2f)
    }
}

@Composable
fun Chevron() {
    DsIcon("chevL", Ds.c.label3, 18.dp, 2.2f)
}

/** Empty state: round icon, title, message and an optional action. */
@Composable
fun EmptyState(icon: String, title: String, message: String, action: String? = null, onAction: () -> Unit = {}) {
    val c = Ds.c
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 46.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.size(56.dp).background(c.fill, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
            DsIcon(icon, c.label2, 26.dp)
        }
        T(title, 16f, FontWeight.SemiBold, c.label)
        T(message, 14f, color = c.label2, align = TextAlign.Center)
        if (action != null) DsButton(action, onAction, style = BtnStyle.Soft)
    }
}
