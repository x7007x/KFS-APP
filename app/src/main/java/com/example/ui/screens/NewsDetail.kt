package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.data.NewsArticle
import com.example.ui.ds.BtnStyle
import com.example.ui.ds.DsButton
import com.example.ui.ds.DsDivider
import com.example.ui.ds.DsIcon
import com.example.ui.ds.DsList
import com.example.ui.ds.DsPic
import com.example.ui.ds.LocalToast
import com.example.ui.ds.Motion
import com.example.ui.ds.T
import com.example.ui.ds.dsClick
import com.example.ui.ds.fmtDate
import com.example.ui.ds.rise
import com.example.ui.ds.topicUrl
import com.example.ui.theme.Ds
import kotlinx.coroutines.launch

/** Clip that grows from the tapped card's bounds to the full screen (shared-element open). */
private class RevealShape(private val from: Rect, private val p: Float, private val startRadius: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val rect = Rect(
            lerp(from.left, 0f, p), lerp(from.top, 0f, p),
            lerp(from.right, size.width, p), lerp(from.bottom, size.height, p),
        )
        return Outline.Rounded(RoundRect(rect, CornerRadius(startRadius * (1f - p))))
    }
}

@Composable
fun NewsDetail(
    article: NewsArticle,
    source: Rect,
    others: List<NewsArticle>,
    onOpenNews: (NewsArticle, Rect) -> Unit,
    onUrl: (String, String) -> Unit,
    onDismissed: () -> Unit,
) {
    val c = Ds.c
    val prog = remember(article.id) { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var closing by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val toast = LocalToast.current
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val url = topicUrl(article.topicId)
    val radius = with(androidx.compose.ui.platform.LocalDensity.current) { 24.dp.toPx() }

    LaunchedEffect(article.id) { prog.animateTo(1f, tween(640, easing = Motion.Spring)) }
    fun close() {
        if (closing) return
        closing = true
        scope.launch { prog.animateTo(0f, tween(480, easing = Motion.Spring)); onDismissed() }
    }
    BackHandler(onBack = ::close)

    Box(Modifier.fillMaxSize().graphicsLayer { clip = true; shape = RevealShape(source, prog.value, radius) }.background(c.bg)) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(Modifier.fillMaxWidth().height(340.dp)) {
                DsPic(article.imageUrl, article.topicId, Modifier.fillMaxSize().graphicsLayer {
                    val s = 1.25f - .25f * prog.value
                    scaleX = s; scaleY = s
                })
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0f to Color(0x59000000), .28f to Color.Transparent, .62f to Color.Transparent, 1f to c.bg)))
                Row(Modifier.fillMaxWidth().padding(top = top + 12.dp, start = 12.dp, end = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    DetailRound("close", "إغلاق", ::close)
                    DetailRound("share", "نسخ الرابط") { clipboard.setText(AnnotatedString(url)); toast("تم النسخ", "copy") }
                }
            }
            Column(Modifier.offset(y = (-52).dp).padding(start = 20.dp, end = 20.dp, bottom = bottom + 40.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(Modifier.rise(0, article.id), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill("cal", fmtDate(article.date), c.green, c.greenSoft)
                    Pill("news", "أخبار الجامعة", c.tint, c.tintSoft)
                }
                T(article.title, 24f, FontWeight.Bold, c.label, lh = 1.55f, modifier = Modifier.rise(1, article.id))
                T("يمكنك قراءة نص الخبر كاملاً مع الصور المرفقة على موقع الجامعة الرسمي.", 15f, color = c.label2, lh = 1.85f, modifier = Modifier.rise(2, article.id))
                Column(Modifier.rise(3, article.id), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DsButton("قراءة الخبر كاملاً", { onUrl(url, article.title) }, Modifier.fillMaxWidth(), icon = "ext", big = true)
                    DsButton("نسخ رابط الخبر", { clipboard.setText(AnnotatedString(url)); toast("تم النسخ", "copy") }, Modifier.fillMaxWidth(), icon = "link", style = BtnStyle.Gray, big = true)
                }
                if (others.isNotEmpty()) {
                    Column(Modifier.rise(4, article.id)) {
                        T("أخبار أخرى", 20f, FontWeight.Bold, c.label, lh = 1.3f, modifier = Modifier.padding(top = 18.dp, bottom = 10.dp))
                        DsList {
                            others.take(4).forEachIndexed { i, a ->
                                if (i > 0) DsDivider()
                                NewsRow(a, onClick = { onOpenNews(a, it) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRound(icon: String, desc: String, onClick: () -> Unit) {
    val c = Ds.c
    Box(
        Modifier.size(38.dp).clip(CircleShape).background(c.glass).border(.5.dp, c.sep, CircleShape).dsClick(.88f, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { DsIcon(icon, c.label, 20.dp, if (icon == "close") 2.2f else 1.8f) }
}

@Composable
private fun Pill(icon: String, text: String, fg: Color, bg: Color) {
    Row(
        Modifier.height(28.dp).clip(RoundedCornerShape(14.dp)).background(bg).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        DsIcon(icon, fg, 14.dp, 2f)
        T(text, 12.5f, FontWeight.SemiBold, fg, lh = 1.2f)
    }
}
