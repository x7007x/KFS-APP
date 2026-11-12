package com.example.ui.ds

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Ds
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.vibrancy

/** The content that glass surfaces in the current subtree refract and blur. Null means nothing to sample. */
val LocalBackdrop = compositionLocalOf<Backdrop?> { null }

/** True while the base screen is sliding under or out from a pushed page: blur is skipped so the transition stays smooth. */
val LocalGlassPaused = compositionLocalOf { false }

/**
 * Real glass: the pixels behind the element are captured, blurred.
 * Below Android 12 the platform has no render-effect blur, so the surface falls back to the solid card colour
 * instead of faking see-through glass.
 */
@Composable
fun Modifier.dsGlass(shape: Shape, border: Boolean = true, blurDp: Float = 16f): Modifier {
    val c = Ds.c
    val backdrop = LocalBackdrop.current
    val hairline = if (border) Modifier.border(.5.dp, c.sep, shape) else Modifier
    if (backdrop == null || LocalGlassPaused.current || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return this.clip(shape).background(c.surface).then(hairline)
    }
    val tint = if (c.isDark) Color(0x33000000) else Color(0x47FFFFFF)
    return this.drawBackdrop(
        backdrop = backdrop,
        shape = { shape },
        effects = {
            vibrancy()
            blur(blurDp.dp.toPx())
        },
        onDrawSurface = { drawRect(tint) },
    ).then(hairline)
}
