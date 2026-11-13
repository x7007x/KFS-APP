package com.kfs.edu

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kfs.edu.ui.PortalApp
import com.kfs.edu.ui.theme.MyApplicationTheme

/** Appearance preference: 0 = follow the system, 1 = light, 2 = dark. */
private object ThemePrefs {
    private const val FILE = "kfu_ui"
    private const val KEY = "theme_mode"
    fun load(ctx: Context): Int = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt(KEY, 0).coerceIn(0, 2)
    fun save(ctx: Context, mode: Int) { ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putInt(KEY, mode).apply() }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var mode by remember { mutableIntStateOf(ThemePrefs.load(this@MainActivity)) }
            val system = isSystemInDarkTheme()
            val dark = when (mode) { 1 -> false; 2 -> true; else -> system }
            DisposableEffect(dark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                )
                onDispose { }
            }
            MyApplicationTheme(darkTheme = dark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PortalApp(
                        themeMode = mode, isDark = dark,
                        onThemeMode = { mode = it; ThemePrefs.save(this@MainActivity, it) },
                    )
                }
            }
        }
    }
}
