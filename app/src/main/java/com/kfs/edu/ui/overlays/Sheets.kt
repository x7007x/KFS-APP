package com.kfs.edu.ui.overlays

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.shape.CircleShape
import com.kfs.edu.data.NewsArticle
import com.kfs.edu.ui.ds.dsGlass
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.kfs.edu.data.KfuNetworkClient
import com.kfs.edu.data.RealStudentProfile
import com.kfs.edu.ui.ds.BtnStyle
import com.kfs.edu.ui.ds.DsButton
import com.kfs.edu.ui.ds.DsDivider
import com.kfs.edu.ui.ds.DsIcon
import com.kfs.edu.ui.ds.DsList
import com.kfs.edu.ui.ds.LocalToast
import com.kfs.edu.ui.ds.Motion
import com.kfs.edu.ui.ds.T
import com.kfs.edu.ui.ds.dsClick
import com.kfs.edu.ui.theme.Ds
import com.kfs.edu.ui.theme.MiSansArabicFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Reports the open fraction (0..1) of the visible sheet so the app shell can scale back behind it. */
val LocalSheetProgress = androidx.compose.runtime.staticCompositionLocalOf<(Float) -> Unit> { {} }

/**
 * Bottom sheet from the design: dimmed scrim, 600ms spring slide-up, grab handle drag-to-dismiss.
 * [onProgress] reports 0..1 so the app shell can scale back behind the sheet.
 */
@Composable
fun DsSheet(
    onDismiss: () -> Unit,
    tall: Boolean = false,
    action: Boolean = false,
    full: Boolean = false,
    content: @Composable (close: () -> Unit) -> Unit,
) {
    val c = Ds.c
    val scope = rememberCoroutineScope()
    val hidden = remember { Animatable(1f) }          // 1 = off screen, 0 = shown
    var drag by remember { mutableFloatStateOf(0f) }
    var closing by remember { mutableStateOf(false) }
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val imeDp = WindowInsets.ime.asPaddingValues().calculateBottomPadding()

    val report = LocalSheetProgress.current
    LaunchedEffect(Unit) { snapshotFlow { 1f - hidden.value }.collect { report(it) } }
    DisposableEffect(Unit) { onDispose { report(0f) } }
    LaunchedEffect(Unit) { hidden.animateTo(0f, tween(600, easing = Motion.Spring)) }
    fun close() {
        if (closing) return
        closing = true
        scope.launch { hidden.animateTo(1f, tween(600, easing = Motion.Spring)); onDismiss() }
    }
    BackHandler(onBack = ::close)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val maxH = maxHeight
        Box(
            Modifier.fillMaxSize().graphicsLayer { alpha = (1f - hidden.value) * (1f - (drag / 1200f).coerceIn(0f, 1f)) }.background(c.dim)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = ::close),
        )
        val shape = if (action) RoundedCornerShape(30.dp) else RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        // The sheet rides above the keyboard and shrinks so its fields are never covered.
        val room = (maxH - imeDp - top - 8.dp).coerceAtLeast(160.dp)
        val sheetMax = minOf(maxH * .94f, room)
        Column(
            Modifier.align(Alignment.BottomCenter).imePadding()
                .then(if (action) Modifier.padding(start = 10.dp, end = 10.dp, bottom = 12.dp + bottom) else Modifier)
                .fillMaxWidth()
                .then(if (full) Modifier.height((maxH - imeDp).coerceAtLeast(160.dp)) else if (tall) Modifier.height(minOf(maxH * .92f, room)) else Modifier.heightIn(max = sheetMax))
                .graphicsLayer { translationY = hidden.value * size.height + drag }
                .clip(shape).then(if (action) Modifier.dsGlass(shape, blurDp = 22f) else Modifier.background(c.bg)),
        ) {
            if (full) Spacer(Modifier.fillMaxWidth().height(top))
            Box(
                Modifier.fillMaxWidth().height(26.dp).pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = { if (drag > 110.dp.toPx()) close() else scope.launch { androidx.compose.animation.core.animate(drag, 0f, animationSpec = tween(500, easing = Motion.Spring)) { v, _ -> drag = v } } },
                        onDragCancel = { drag = 0f },
                    ) { _, dy -> drag = (drag + dy).coerceAtLeast(0f) }
                },
                contentAlignment = Alignment.Center,
            ) { Box(Modifier.width(38.dp).height(5.dp).background(c.fill2, RoundedCornerShape(3.dp))) }
            Box(Modifier.then(if (tall || full) Modifier.weight(1f) else Modifier.weight(1f, fill = false)).padding(bottom = if (action) 0.dp else if (imeDp > 0.dp) 0.dp else bottom)) { content(::close) }
        }
    }
}

@Composable
fun SheetHeader(title: String, onClose: () -> Unit) {
    val c = Ds.c
    Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        T(title, 19f, FontWeight.Bold, c.label, lh = 1.3f)
        XButton("close", onClose)
    }
}

@Composable
fun XButton(icon: String, onClick: () -> Unit) {
    val c = Ds.c
    Box(Modifier.size(34.dp).clip(CircleShape).background(c.fill).dsClick(.86f, onClick = onClick), contentAlignment = Alignment.Center) {
        DsIcon(icon, c.label2, 18.dp, if (icon == "close") 2.2f else 1.8f)
    }
}

// ------------------------------------------------------------------ notifications

data class NotifItem(val icon: String, val title: String, val body: String, val key: String)

@Composable
fun NotificationsSheet(items: List<NotifItem>, unread: Boolean, onAction: (String) -> Unit, onDismiss: () -> Unit) = DsSheet(onDismiss) { close ->
    val c = Ds.c
    Column {
        SheetHeader("الإشعارات", close)
        if (items.isEmpty()) {
            com.kfs.edu.ui.ds.EmptyState("bell", "لا توجد إشعارات", "ستظهر هنا تنبيهات حسابك وحالة التطبيق.")
        } else {
            Column(Modifier.verticalScroll(rememberScrollState()).padding(start = 16.dp, end = 16.dp, bottom = 24.dp)) {
                DsList {
                    items.forEachIndexed { i, n ->
                        if (i > 0) DsDivider()
                        Row(
                            Modifier.fillMaxWidth().dsClick(.98f) { if (n.key == "login") { close(); onAction(n.key) } }.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(Modifier.size(38.dp).background(c.tintSoft, CircleShape), contentAlignment = Alignment.Center) { DsIcon(n.icon, c.tint, 20.dp) }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                T(n.title, 15f, FontWeight.SemiBold, c.label)
                                T(n.body, 13f, color = c.label2, lh = 1.5f)
                            }
                            if (unread && n.key != "sync") Box(Modifier.size(9.dp).background(c.tint, CircleShape))
                        }
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------ logout confirmation (action sheet)

@Composable
fun LogoutSheet(onConfirm: () -> Unit, onDismiss: () -> Unit) = DsSheet(onDismiss, action = true) { close ->
    val c = Ds.c
    val scope = rememberCoroutineScope()
    Column(Modifier.padding(start = 10.dp, end = 10.dp, top = 6.dp, bottom = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        T("هل تريد تسجيل الخروج من حسابك؟", 14f, FontWeight.Medium, c.label2, align = TextAlign.Center, modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 6.dp))
        ActionButton("تأكيد الخروج", c.red, FontWeight.SemiBold) { onConfirm(); close() }
        ActionButton("إلغاء", c.tint, FontWeight.Medium) { close() }
    }
}

@Composable
private fun ActionButton(text: String, color: Color, weight: FontWeight, onClick: () -> Unit) {
    val c = Ds.c
    Box(Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(18.dp)).background(c.surface).dsClick(.97f, onClick = onClick), contentAlignment = Alignment.Center) {
        T(text, 17f, weight, color, lh = 1.3f)
    }
}

// ------------------------------------------------------------------ login

private const val ARABIC_DIGITS = "٠١٢٣٤٥٦٧٨٩"

@Composable
fun LoginSheet(onLoggedIn: suspend (RealStudentProfile, String) -> Unit, onDismiss: () -> Unit) = DsSheet(onDismiss) { close ->
    val c = Ds.c
    val scope = rememberCoroutineScope()
    var nid by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val shake = remember { Animatable(0f) }
    val pop = remember { Animatable(.4f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, tween(700, easing = Motion.Bounce)) }

    fun submit() {
        if (loading) return
        if (nid.length != 14) {
            error = "يجب أن يتكون الرقم القومي من 14 رقماً تماماً."
            scope.launch {
                for (x in listOf(-8f, 7f, -5f, 3f, 0f)) shake.animateTo(x, tween(90))
            }
            return
        }
        loading = true; error = ""
        scope.launch {
            try {
                val profile = KfuNetworkClient.loginWithNationalId(nid)
                if (profile != null) { onLoggedIn(profile, nid); close() }
                else error = "لم يتم العثور على سجل مطابق. يرجى تجربة رقم آخر."
            } catch (e: Exception) {
                error = "فشل الاتصال بخادم الجامعة. تأكد من اتصال الإنترنت."
            } finally { loading = false }
        }
    }

    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(76.dp).graphicsLayer { scaleX = pop.value; scaleY = pop.value; alpha = pop.value.coerceIn(0f, 1f) }
                .clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(Color(0xFF2F86D9), c.tintD))),
            contentAlignment = Alignment.Center,
        ) { DsIcon("cap", Color.White, 38.dp, 1.6f) }
        T("تسجيل الدخول بالرقم القومي", 20f, FontWeight.Bold, c.label, align = TextAlign.Center)
        T("أدخل الرقم القومي المكوّن من 14 رقماً للوصول إلى ملفك وخدماتك.", 14f, color = c.label2, align = TextAlign.Center)
        Row(
            Modifier.fillMaxWidth().height(56.dp).graphicsLayer { translationX = shake.value.dp.toPx() }
                .clip(RoundedCornerShape(16.dp)).background(c.surface)
                .border(if (error.isNotEmpty()) 2.dp else .5.dp, if (error.isNotEmpty()) c.red else c.sep, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            DsIcon("badge", c.label2, 22.dp)
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (nid.isEmpty()) T("الرقم القومي (14 خانة)", 15f, color = c.label3)
                BasicTextField(
                    value = nid,
                    onValueChange = { v ->
                        nid = v.map { ch -> val i = ARABIC_DIGITS.indexOf(ch); if (i >= 0) ('0' + i) else ch }.filter { it.isDigit() }.joinToString("").take(14)
                        error = ""
                    },
                    singleLine = true, enabled = !loading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontFamily = MiSansArabicFont, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = c.label, textAlign = TextAlign.Center, textDirection = TextDirection.Ltr, letterSpacing = 2.5.sp),
                    cursorBrush = SolidColor(c.tint), modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(14) { i ->
                val f by animateFloatAsState(if (i < nid.length) 1f else 0f, tween(350, easing = Motion.Bounce), label = "dot")
                Box(Modifier.width(16.dp).height(5.dp).graphicsLayer { scaleY = 1f + .5f * f }.background(if (f > .5f) c.tint else c.fill2, RoundedCornerShape(3.dp)))
            }
        }
        T(error, 13f, FontWeight.Medium, c.red, align = TextAlign.Center, modifier = Modifier.heightIn(min = 20.dp))
        DsButton("تسجيل دخول", ::submit, Modifier.fillMaxWidth(), big = true, loading = loading)
    }
}

// ------------------------------------------------------------------ message to the university

@Composable
private fun CheckDraw(color: Color) {
    val p = remember { Animatable(0f) }
    LaunchedEffect(Unit) { delay(250); p.animateTo(1f, tween(600, easing = Motion.Spring)) }
    val path = remember { PathParser().parsePathString("M5 12.5l4.5 4.5L19 7").toPath() }
    Canvas(Modifier.size(42.dp)) {
        val s = size.width / 24f
        val scaled = Path().apply { addPath(path); transform(androidx.compose.ui.graphics.Matrix().apply { scale(s, s) }) }
        val m = PathMeasure().apply { setPath(scaled, false) }
        val seg = Path()
        m.getSegment(0f, m.length * p.value, seg, true)
        drawPath(seg, color, style = Stroke(width = 2.6f * s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun FormField(label: String, value: String, onChange: (String) -> Unit, enabled: Boolean, ltr: Boolean = false, lines: Int = 1) {
    val c = Ds.c
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(c.surface).border(.5.dp, c.sep, RoundedCornerShape(16.dp)).padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        T(label, 12f, FontWeight.Medium, c.label2, lh = 1.3f)
        BasicTextField(
            value = value, onValueChange = onChange, enabled = enabled, singleLine = lines == 1, minLines = lines,
            textStyle = TextStyle(fontFamily = MiSansArabicFont, fontSize = 15.sp, color = c.label, textDirection = if (ltr) TextDirection.Ltr else TextDirection.Content),
            cursorBrush = SolidColor(c.tint), modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun MessageSheet(onDismiss: () -> Unit) = DsSheet(onDismiss) { close ->
    val c = Ds.c
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    var sent by remember { mutableStateOf(false) }
    Column(Modifier.verticalScroll(rememberScrollState()).padding(bottom = 24.dp)) {
        SheetHeader("إرسال رسالة للجامعة", close)
        if (sent) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                val pop = remember { Animatable(.4f) }
                LaunchedEffect(Unit) { pop.animateTo(1f, tween(600, easing = Motion.Bounce)) }
                Box(Modifier.padding(top = 30.dp).size(84.dp).graphicsLayer { scaleX = pop.value; scaleY = pop.value }.background(c.greenSoft, CircleShape), contentAlignment = Alignment.Center) { CheckDraw(c.green) }
                T("تم إرسال رسالتك بنجاح", 20f, FontWeight.Bold, c.label, align = TextAlign.Center)
                DsButton("إغلاق", close, Modifier.fillMaxWidth(), big = true)
            }
        } else {
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FormField("الاسم", name, { name = it; error = "" }, !sending)
                FormField("البريد الإلكتروني", email, { email = it; error = "" }, !sending, ltr = true)
                FormField("الموضوع", subject, { subject = it; error = "" }, !sending)
                FormField("الرسالة", message, { message = it; error = "" }, !sending, lines = 4)
                T(error, 13f, FontWeight.Medium, c.red, modifier = Modifier.heightIn(min = 20.dp))
                DsButton("إرسال", {
                    if (listOf(name, email, subject, message).any { it.isBlank() }) { error = "يرجى ملء جميع الحقول" }
                    else if (!sending) {
                        sending = true
                        scope.launch {
                            val ok = KfuNetworkClient.sendUniversityMessage(name.trim(), email.trim(), subject.trim(), message.trim())
                            sending = false
                            if (ok) sent = true else error = "فشل الإرسال. حاول مرة أخرى."
                        }
                    }
                }, Modifier.fillMaxWidth(), icon = "send", big = true, loading = sending)
            }
        }
    }
}

// ------------------------------------------------------------------ in-app browser

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserSheet(url: String, title: String, profile: com.kfs.edu.data.RealStudentProfile?, onDismiss: () -> Unit) = DsSheet(onDismiss, full = true) { close ->
    val c = Ds.c
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val toast = LocalToast.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var failed by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var current by remember { mutableStateOf(url) }
    val host = remember(current) { try { Uri.parse(current).host?.removePrefix("www.") ?: "kfs.edu.eg" } catch (_: Exception) { "kfs.edu.eg" } }
    val barA by animateFloatAsState(if (loading) 1f else 0f, tween(400), label = "barA")
    val bar by animateFloatAsState(progress, tween(350, easing = Motion.Spring), label = "bar")

    var pdfUrl by remember { mutableStateOf<String?>(if (fileKind(url) == FileKind.Pdf) url else null) }
    fun openExternal(u: String) { try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(u)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (_: Exception) { } }
    /** Routes a file link to the right in-app preview; false when it is an ordinary page. */
    fun previewFile(u: String, mime: String? = null): Boolean {
        val k = if (mime?.contains("pdf") == true) FileKind.Pdf else fileKind(u)
        return when (k) {
            FileKind.Pdf -> { pdfUrl = u; current = u; true }
            FileKind.Office -> { webView?.loadUrl("https://docs.google.com/gview?embedded=true&url=" + Uri.encode(u)); true }
            else -> false
        }
    }

    val imeOpen = WindowInsets.ime.getBottom(androidx.compose.ui.platform.LocalDensity.current) > 0
    var hint by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(imeOpen) {
        if (!imeOpen) { hint = null; return@LaunchedEffect }
        kotlinx.coroutines.delay(150)
        webView?.evaluateJavascript("(function(){var e=document.activeElement;if(!e)return '';return [e.type,e.name,e.id,e.placeholder,e.autocomplete].join(' ').toLowerCase()})()") { r ->
            val a = r.orEmpty()
            hint = when {
                a.contains("password") || a.contains("pass") || a.contains("كلمة") -> "pass"
                a.contains("email") || a.contains("mail") || a.contains("بريد") -> "email"
                a.contains("national") || a.contains("nid") || a.contains("قومي") || a.contains("ssn") -> "nid"
                a.contains("code") || a.contains("كود") || a.contains("user") || a.contains("stud") -> "code"
                a.contains("name") || a.contains("اسم") -> "name"
                else -> null
            }
        }
    }
    /** Types [value] into the focused web input like a password manager would (fires input/change events). */
    fun fillField(value: String) {
        val v = org.json.JSONObject.quote(value)
        webView?.evaluateJavascript("(function(v){var e=document.activeElement;if(!e||e===document.body)return;var p=Object.getPrototypeOf(e);var d=Object.getOwnPropertyDescriptor(p,'value');if(d&&d.set)d.set.call(e,v);else e.value=v;e.dispatchEvent(new Event('input',{bubbles:true}));e.dispatchEvent(new Event('change',{bubbles:true}));})($v)", null)
    }

    BackHandler(enabled = pdfUrl != null) { pdfUrl = null }
    BackHandler(enabled = canGoBack && pdfUrl == null) { webView?.goBack() }
    DisposableEffect(Unit) { onDispose { webView?.destroy(); webView = null } }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            XButton("close", close)
            Row(
                Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(12.dp)).background(c.fill).padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
            ) {
                DsIcon("lock", c.label2, 13.dp, 2f)
                T(host, 13f, FontWeight.Medium, c.label, lh = 1.2f, maxLines = 1, modifier = Modifier.padding(start = 6.dp))
            }
            XButton("link") { clipboard.setText(AnnotatedString(current)); toast("تم النسخ", "copy") }
        }
        Box(Modifier.fillMaxWidth().height(2.5.dp).graphicsLayer { alpha = barA }) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(bar.coerceIn(0f, 1f)).align(Alignment.CenterStart).background(c.tint))
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        setBackgroundColor(android.graphics.Color.WHITE)
                        settings.apply {
                            javaScriptEnabled = true; domStorageEnabled = true; databaseEnabled = true
                            useWideViewPort = true; loadWithOverviewMode = true
                            setSupportZoom(true); builtInZoomControls = true; displayZoomControls = false
                            setSupportMultipleWindows(true)
                            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                            layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            defaultTextEncodingName = "utf-8"
                        }
                        overScrollMode = WebView.OVER_SCROLL_NEVER
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) { progress = newProgress / 100f }
                        }
                        setDownloadListener { dl, _, _, mime, _ -> if (!previewFile(dl, mime)) openExternal(dl) }
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val u = request?.url?.toString() ?: return false
                                return request.isForMainFrame && previewFile(u)
                            }
                            override fun onPageStarted(view: WebView?, u: String?, favicon: Bitmap?) { loading = true; failed = false; u?.let { current = it } }
                            override fun onPageFinished(view: WebView?, u: String?) { loading = false; canGoBack = view?.canGoBack() ?: false }
                            override fun doUpdateVisitedHistory(view: WebView?, u: String?, isReload: Boolean) { canGoBack = view?.canGoBack() ?: false; u?.let { current = it } }
                            // Kept from the previous WebViewScreen: the university's servers use certificates the platform rejects.
                            @SuppressLint("WebViewClientOnReceivedSslError")
                            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) { handler?.proceed() }
                            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: android.webkit.WebResourceError?) {
                                if (request?.isForMainFrame == true) { failed = true; loading = false }
                            }
                        }
                        val k = fileKind(url)
                        if (k == FileKind.Office) loadUrl("https://docs.google.com/gview?embedded=true&url=" + Uri.encode(url))
                        else if (k != FileKind.Pdf) loadUrl(url)
                        webView = this
                    }
                },
            )
            pdfUrl?.let { pu -> PdfPreview(pu, onFailed = { openExternal(pu) }, modifier = Modifier.fillMaxSize()) }
            if (failed && pdfUrl == null) {
                Box(Modifier.fillMaxSize().background(c.bg), contentAlignment = Alignment.Center) {
                    com.kfs.edu.ui.ds.EmptyState("globe", "تعذر تحميل الصفحة", "تحقق من اتصال الإنترنت ثم أعد المحاولة.", "إعادة المحاولة") { failed = false; webView?.reload() }
                }
            }
        }
        if (imeOpen && profile != null) {
            val items = remember(profile, hint) {
                val all = listOf(
                    "nid" to ("الرقم القومي" to profile.nationalNumber),
                    "code" to ("كود الطالب" to profile.studentCode),
                    "pass" to ("كلمة المرور" to profile.passwordItec),
                    "email" to ("البريد الإلكتروني" to profile.email),
                    "name" to ("الاسم" to profile.fullName),
                ).filter { it.second.second.isNotBlank() }
                all.sortedByDescending { it.first == hint }
            }
            Row(
                Modifier.fillMaxWidth().background(c.surface).horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items.forEach { (k, lv) ->
                    val sel = k == hint
                    Column(
                        Modifier.clip(RoundedCornerShape(12.dp)).background(if (sel) c.tint else c.fill)
                            .dsClick(.94f) { fillField(lv.second) }.padding(horizontal = 12.dp, vertical = 7.dp),
                    ) {
                        T(lv.first, 11f, FontWeight.Medium, if (sel) Color.White else c.label2, lh = 1.2f, maxLines = 1)
                        T(if (k == "pass") "•".repeat(lv.second.length.coerceAtMost(8)) else lv.second, 13.5f, FontWeight.SemiBold, if (sel) Color.White else c.label, lh = 1.3f, maxLines = 1)
                    }
                }
            }
        } else
        Row(
            Modifier.fillMaxWidth().background(c.surface).padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            ToolButton("refresh") { if (pdfUrl != null) { val u = pdfUrl; pdfUrl = null; pdfUrl = u } else webView?.reload() }
            ToolButton("ext") { try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(current)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (_: Exception) { } }
            ToolButton("share") {
                try {
                    val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, current)
                    ctx.startActivity(Intent.createChooser(send, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } catch (_: Exception) { clipboard.setText(AnnotatedString(current)); toast("تم النسخ", "copy") }
            }
        }
    }
}

@Composable
private fun ToolButton(icon: String, onClick: () -> Unit) {
    Box(Modifier.size(44.dp).clip(CircleShape).dsClick(.85f, onClick = onClick), contentAlignment = Alignment.Center) {
        DsIcon(icon, Ds.c.tint, 22.dp)
    }
}

private enum class FileKind { Pdf, Office, Image, Other }

private fun fileKind(url: String): FileKind {
    val path = try { Uri.parse(url).path.orEmpty().lowercase() } catch (_: Exception) { url.lowercase() }
    return when {
        path.endsWith(".pdf") -> FileKind.Pdf
        path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".xls") || path.endsWith(".xlsx") ||
            path.endsWith(".ppt") || path.endsWith(".pptx") -> FileKind.Office
        path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".webp") || path.endsWith(".gif") -> FileKind.Image
        else -> FileKind.Other
    }
}
