package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NewsArticle
import com.example.ui.ds.Chevron
import com.example.ui.ds.DsIcon
import com.example.ui.ds.DsPic
import com.example.ui.ds.DsRow
import com.example.ui.ds.LocalToast
import com.example.ui.ds.Motion
import com.example.ui.ds.RowIconBox
import com.example.ui.ds.T
import com.example.ui.ds.dsClick
import com.example.ui.ds.fmtDate
import com.example.ui.theme.Ds
import com.example.ui.theme.MiSansArabicFont
import kotlinx.coroutines.delay

/** Search field: 40dp, radius 12, fill background, 2dp tint ring while focused. */
@Composable
fun DsSearch(value: String, onChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    val c = Ds.c
    val src = remember { MutableInteractionSource() }
    val focused by src.collectIsFocusedAsState()
    val ring by animateFloatAsState(if (focused) 1f else 0f, tween(250), label = "ring")
    Row(
        modifier.padding(start = 16.dp, end = 16.dp, bottom = 6.dp).fillMaxWidth().height(40.dp)
            .clip(RoundedCornerShape(12.dp)).background(c.fill)
            .border(2.dp, c.tint.copy(alpha = ring), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DsIcon("search", c.label2, 18.dp)
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) T(placeholder, 15f, color = c.label2)
            BasicTextField(
                value = value, onValueChange = onChange, singleLine = true, interactionSource = src,
                textStyle = TextStyle(fontFamily = MiSansArabicFont, fontSize = 15.sp, color = c.label),
                cursorBrush = SolidColor(c.tint), modifier = Modifier.fillMaxWidth(),
            )
        }
        if (value.isNotEmpty()) {
            Box(Modifier.size(20.dp).dsClick(.85f) { onChange("") }, contentAlignment = Alignment.Center) {
                DsIcon("close", c.label2, 16.dp, 2.2f)
            }
        }
    }
}

/** News row used by lists: 86dp picture, date, three-line title. Reports its bounds for the shared-element open. */
@Composable
fun NewsRow(a: NewsArticle, onClick: (Rect) -> Unit, divider: Boolean = false) {
    val c = Ds.c
    var bounds by remember { mutableStateOf(Rect.Zero) }
    Box(Modifier.onGloballyPositioned { bounds = it.boundsInRoot() }) {
        DsRow(onClick = { onClick(bounds) }) {
            DsPic(a.imageUrl, a.topicId, Modifier.size(86.dp).clip(RoundedCornerShape(16.dp)))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                T(fmtDate(a.date), 12f, color = c.label2, lh = 1.3f)
                T(a.title, 14f, FontWeight.SemiBold, c.label, lh = 1.55f, maxLines = 3)
            }
        }
    }
}

/** Row that copies [value] to the clipboard; the copy icon swaps to a green check for ~1.8s. */
@Composable
fun CopyRow(icon: String, color: Color, label: String, value: String, display: String = value) {
    val c = Ds.c
    val clip = LocalClipboardManager.current
    val toast = LocalToast.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) { if (copied) { delay(1800); copied = false } }
    val k by animateFloatAsState(if (copied) 1f else 0f, tween(350, easing = Motion.Bounce), label = "copy")
    DsRow(onClick = {
        clip.setText(AnnotatedString(value)); copied = true; toast("تم النسخ", "copy")
    }) {
        RowIconBox(icon, color)
        Column(Modifier.weight(1f)) {
            T(label, 12.5f, color = c.label2, lh = 1.4f)
            T(display, 14.5f, FontWeight.SemiBold, c.label, lh = 1.4f)
        }
        Box(Modifier.size(22.dp)) {
            Box(Modifier.graphicsLayer { alpha = 1f - k; scaleX = 1f - .7f * k; scaleY = 1f - .7f * k }) { DsIcon("copy", c.label3, 22.dp) }
            Box(Modifier.graphicsLayer { alpha = k; scaleX = .3f + .7f * k; scaleY = .3f + .7f * k }) { DsIcon("check", c.green, 22.dp, 2.4f) }
        }
    }
}

@Composable
fun LinkRow(icon: String, color: Color, title: String, subtitle: String? = null, onClick: () -> Unit) {
    val c = Ds.c
    DsRow(onClick = onClick) {
        RowIconBox(icon, color)
        Column(Modifier.weight(1f)) {
            T(title, 15f, FontWeight.Medium, c.label, lh = 1.45f)
            if (subtitle != null) T(subtitle, 12.5f, color = c.label2, lh = 1.4f)
        }
        Chevron()
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    val c = Ds.c
    DsRow {
        Column(Modifier.weight(1f)) {
            T(label, 12.5f, color = c.label2, lh = 1.4f)
            T(value, 14.5f, FontWeight.SemiBold, c.label, lh = 1.4f)
        }
    }
}
