package com.example.ui.ds

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.graphics.RectangleShape
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import com.example.R
import com.example.ui.theme.Ds

/** Scroll progress of the large title collapsing into the glass bar: (scrollDp - 14) / 40, clamped. */
@Composable
fun navProgress(scroll: ScrollState): Float {
    val d = LocalDensity.current.density
    return (((scroll.value / d) - 14f) / 40f).coerceIn(0f, 1f)
}

@Composable
fun DsNavBar(
    title: String,
    progress: Float,
    showLogo: Boolean,
    trailing: @Composable RowScope.() -> Unit,
) {
    val c = Ds.c
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box(Modifier.fillMaxWidth().height(top + 58.dp)) {
        Box(Modifier.matchParentSize().graphicsLayer { alpha = progress }.dsGlass(RectangleShape, border = false, blurDp = 20f))
        Box(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(.5.dp)
                .graphicsLayer { alpha = progress }.background(c.sep),
        )
        Row(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp).height(38.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (showLogo) {
                    Image(painterResource(R.drawable.logo_small), contentDescription = null, modifier = Modifier.width(26.dp))
                }
                val px = with(LocalDensity.current) { 7.dp.toPx() }
                T(
                    title, 16f, FontWeight.SemiBold, c.label,
                    modifier = Modifier.graphicsLayer { alpha = progress; translationY = (1f - progress) * px },
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), content = trailing)
        }
    }
}

/** Pull indicator: 34dp surface circle with a refresh icon, scaled and rotated by the pull distance. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxScope.PullIndicator(fraction: Float, refreshing: Boolean) {
    val c = Ds.c
    val spin = rememberInfiniteTransition(label = "ptr")
    val rot = spin.animateFloat(0f, 360f, infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Restart), label = "rot")
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val f = if (refreshing) 1f else fraction.coerceIn(0f, 1f)
    Box(
        Modifier.align(Alignment.TopCenter).padding(top = top + 60.dp).size(34.dp)
            .graphicsLayer {
                alpha = f; scaleX = .6f + .4f * f; scaleY = .6f + .4f * f
                translationY = (f * 48.dp.toPx()) - 20.dp.toPx()
                rotationZ = if (refreshing) rot.value else fraction * 240f
            }
            .shadow(8.dp, CircleShape).background(c.surface, CircleShape),
        contentAlignment = Alignment.Center,
    ) { DsIcon("refresh", c.tint, 18.dp, 2.2f) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabScaffold(
    scroll: ScrollState,
    title: String,
    showLogo: Boolean = false,
    trailing: @Composable RowScope.() -> Unit = {},
    refreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val p = navProgress(scroll)
    val backdrop = rememberLayerBackdrop()
    Box(Modifier.fillMaxSize()) {
        // Pages with a logo and actions keep the bar visible, so their content starts below it.
        val topPad = if (showLogo) top + 62.dp else top + 6.dp
        val body: @Composable () -> Unit = {
            Column(
                Modifier.fillMaxSize().layerBackdrop(backdrop).imePadding().verticalScroll(scroll)
                    .padding(top = topPad, bottom = bottom + 118.dp),
                content = content,
            )
        }
        if (onRefresh != null) {
            val state = rememberPullToRefreshState()
            PullToRefreshBox(
                isRefreshing = refreshing, onRefresh = onRefresh, state = state, modifier = Modifier.fillMaxSize(),
                indicator = { PullIndicator(state.distanceFraction, refreshing) },
            ) { body() }
        } else body()
        CompositionLocalProvider(LocalBackdrop provides backdrop) { DsNavBar(title, p, showLogo, trailing) }
    }
}
