package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** rgba(r, g, b, a) exactly as written in the design artifact's CSS. */
fun rgba(r: Int, g: Int, b: Int, a: Float): Color = Color(r / 255f, g / 255f, b / 255f, a)

/** Design tokens of the redesigned portal (light and dark), taken from the design artifact. */
@Immutable
class DsColors(
    val isDark: Boolean,
    val bg: Color,
    val surface: Color,
    val fill: Color,
    val fill2: Color,
    val label: Color,
    val label2: Color,
    val label3: Color,
    val sep: Color,
    val tint: Color,
    val tintD: Color,
    val tintSoft: Color,
    val onTint: Color,
    val green: Color,
    val greenSoft: Color,
    val red: Color,
    val redSoft: Color,
    val glass: Color,
    val dim: Color,
    val gMed: Color,
    val gSci: Color,
    val gHum: Color,
    val gMedS: Color,
    val gSciS: Color,
    val gHumS: Color,
)

val LightDs = DsColors(
    isDark = false,
    bg = Color(0xFFF2F4F8),
    surface = Color(0xFFFFFFFF),
    fill = rgba(118, 128, 150, .13f),
    fill2 = rgba(118, 128, 150, .22f),
    label = Color(0xFF0C1626),
    label2 = rgba(40, 56, 84, .66f),
    label3 = rgba(40, 56, 84, .36f),
    sep = rgba(40, 56, 84, .14f),
    tint = Color(0xFF1A5CAA),
    tintD = Color(0xFF0D3D75),
    tintSoft = rgba(26, 92, 170, .11f),
    onTint = Color(0xFFFFFFFF),
    green = Color(0xFF008744),
    greenSoft = rgba(0, 135, 68, .11f),
    red = Color(0xFFD93036),
    redSoft = rgba(217, 48, 54, .10f),
    // The design uses a blurred glass; Compose has no backdrop blur, so the alpha is raised.
    glass = rgba(248, 250, 253, .92f),
    dim = rgba(8, 16, 30, .38f),
    gMed = Color(0xFF1E5FA3),
    gSci = Color(0xFFA67A00),
    gHum = Color(0xFF007D48),
    gMedS = rgba(30, 95, 163, .11f),
    gSciS = rgba(214, 160, 0, .15f),
    gHumS = rgba(0, 125, 72, .11f),
)

val DarkDs = DsColors(
    isDark = true,
    bg = Color(0xFF000000),
    surface = Color(0xFF15191F),
    fill = rgba(130, 140, 165, .20f),
    fill2 = rgba(130, 140, 165, .32f),
    label = Color(0xFFF4F7FB),
    label2 = rgba(215, 225, 245, .66f),
    label3 = rgba(215, 225, 245, .34f),
    sep = rgba(150, 165, 195, .20f),
    tint = Color(0xFF5AA2F0),
    tintD = Color(0xFF0D3D75),
    tintSoft = rgba(90, 162, 240, .18f),
    onTint = Color(0xFF06121F),
    green = Color(0xFF3DD68C),
    greenSoft = rgba(61, 214, 140, .16f),
    red = Color(0xFFFF6B6F),
    redSoft = rgba(255, 107, 111, .16f),
    glass = rgba(24, 28, 34, .92f),
    dim = rgba(0, 0, 0, .55f),
    gMed = Color(0xFF6DB0F5),
    gSci = Color(0xFFF0C24A),
    gHum = Color(0xFF4CE09B),
    gMedS = rgba(109, 176, 245, .17f),
    gSciS = rgba(240, 194, 74, .17f),
    gHumS = rgba(76, 224, 155, .16f),
)

val LocalDs = staticCompositionLocalOf { LightDs }

object Ds {
    val c: DsColors
        @Composable @ReadOnlyComposable get() = LocalDs.current
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    @Suppress("UNUSED_PARAMETER") dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val ds = if (darkTheme) DarkDs else LightDs
    val scheme = if (darkTheme) {
        darkColorScheme(primary = ds.tint, onPrimary = ds.onTint, background = ds.bg, surface = ds.surface,
            onBackground = ds.label, onSurface = ds.label)
    } else {
        lightColorScheme(primary = ds.tint, onPrimary = ds.onTint, background = ds.bg, surface = ds.surface,
            onBackground = ds.label, onSurface = ds.label)
    }
    CompositionLocalProvider(LocalDs provides ds) {
        MaterialTheme(colorScheme = scheme, typography = Typography, content = content)
    }
}
