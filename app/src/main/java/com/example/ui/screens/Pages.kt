package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.HarDataCache
import com.example.data.KfuNetworkClient
import com.example.data.NewsArticle
import com.example.data.RealStudentProfile
import com.example.data.toNewsArticle
import com.example.ui.ds.BtnStyle
import com.example.ui.ds.CollegeUi
import com.example.ui.ds.DsButton
import com.example.ui.ds.DsDivider
import com.example.ui.ds.DsIcon
import com.example.ui.ds.DsList
import com.example.ui.ds.DsMono
import com.example.ui.ds.DsRow
import com.example.ui.ds.DsSeg
import com.example.ui.ds.EmptyState
import com.example.ui.ds.GroupHeader
import com.example.ui.ds.Motion
import com.example.ui.ds.RowIconBox
import com.example.ui.ds.SvcStudents
import com.example.ui.ds.T
import com.example.ui.ds.TileGradients
import com.example.ui.ds.dsClick
import com.example.ui.ds.dsGlass
import androidx.compose.foundation.layout.imePadding
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.example.ui.ds.navProgress
import com.example.ui.ds.rise
import com.example.ui.ds.topicUrl
import com.example.ui.theme.Ds

/** Pushed page chrome: glass bar with the "رجوع" pill and a centred title that fades in on scroll. */
@Composable
fun PageScaffold(title: String, onBack: () -> Unit, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    val c = Ds.c
    val scroll = rememberScrollState()
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val d = LocalDensity.current.density
    val p = (((scroll.value / d) - 30f) / 36f).coerceIn(0f, 1f)
    Box(Modifier.fillMaxSize().background(c.bg)) {
        val backdrop = rememberLayerBackdrop()
        Column(Modifier.fillMaxSize().layerBackdrop(backdrop).background(c.bg).imePadding().verticalScroll(scroll).padding(top = top + 58.dp, bottom = bottom + 40.dp), content = content)
        androidx.compose.runtime.CompositionLocalProvider(com.example.ui.ds.LocalBackdrop provides backdrop) {
        Box(Modifier.fillMaxWidth().height(top + 58.dp)) {
            if (scroll.value > 0) Box(Modifier.matchParentSize().graphicsLayer { alpha = ((scroll.value / d) / 6f).coerceIn(0f, 1f) }.dsGlass(androidx.compose.ui.graphics.RectangleShape, border = false, blurDp = 20f))
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(.5.dp).graphicsLayer { alpha = ((scroll.value / d) / 6f).coerceIn(0f, 1f) }.background(c.sep))
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 8.dp).height(38.dp)) {
                Row(
                    Modifier.align(Alignment.CenterStart).height(38.dp).dsClick(.92f, onClick = onBack)
                        .clip(RoundedCornerShape(19.dp)).background(c.surface).border(.5.dp, c.sep, RoundedCornerShape(19.dp)).padding(start = 12.dp, end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DsIcon("chevR", c.tint, 20.dp, 2.4f)
                    T("رجوع", 15f, FontWeight.Medium, c.tint, lh = 1.2f)
                }
                T(title, 16f, FontWeight.SemiBold, c.label, maxLines = 1, modifier = Modifier.align(Alignment.Center).graphicsLayer { alpha = p; translationY = (1f - p) * 7.dp.toPx() })
            }
        }
        }
    }
}

@Composable
private fun Skeleton(rows: Int = 3) {
    val c = Ds.c
    val t = rememberInfiniteTransition(label = "skel")
    val x by t.animateFloat(-1f, 2f, infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart), label = "x")
    Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        repeat(rows) {
            Box(
                Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp)).background(
                    Brush.horizontalGradient(
                        0f to c.fill, (x - .25f).coerceIn(0f, 1f) to c.fill, x.coerceIn(0f, 1f) to c.fill2,
                        (x + .25f).coerceIn(0f, 1f) to c.fill, 1f to c.fill,
                    ),
                ),
            )
        }
    }
}

private fun openExternal(ctx: android.content.Context, url: String) {
    try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (_: Exception) { }
}

@Composable
fun CollegePage(college: CollegeUi, onBack: () -> Unit, onUrl: (String, String) -> Unit) {
    val c = Ds.c
    val ctx = LocalContext.current
    val (fg, bg) = when (college.type) {
        1 -> c.gSci to c.gSciS
        3 -> c.gHum to c.gHumS
        else -> c.gMed to c.gMedS
    }
    val groupTitle = CollegeGroups.firstOrNull { it.first == college.type }?.second ?: ""
    var tab by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    var topics by remember { mutableStateOf<List<Pair<Int, String>>>(emptyList()) }
    var news by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }

    LaunchedEffect(college.facId) {
        loading = true
        try {
            val t = KfuNetworkClient.fetchCollegeTopics(college.facId.toString())
            topics = if (t.isNotEmpty()) t.map { it.topicId to it.title }
            else HarDataCache.getTopicsForFaculty(college.facId).map { it.topicId to it.titleArabic }
            val n = KfuNetworkClient.fetchCollegeNews(college.facId.toString())
            news = n.map { it.toNewsArticle() }
        } catch (_: Exception) {
            topics = HarDataCache.getTopicsForFaculty(college.facId).map { it.topicId to it.titleArabic }
        } finally { loading = false }
    }

    BackHandler(onBack = onBack)
    PageScaffold(college.title, onBack) {
        Column(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 18.dp).rise(0), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier) {
                DsMono(college.title, "https://kfs.edu.eg/kfsapi/flogo/${college.logo}", fg, bg, 92.dp, 36f)
            }
            T(college.title, 20f, FontWeight.Bold, c.label, lh = 1.4f, align = TextAlign.Center)
            Box(Modifier.height(26.dp).clip(RoundedCornerShape(13.dp)).background(bg).padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                T(groupTitle, 12.5f, FontWeight.SemiBold, fg, lh = 1.2f)
            }
        }
        Box(Modifier.rise(1)) { DsSeg(listOf("الجداول", "الأخبار", "التواصل"), tab, { tab = it }, Modifier.padding(horizontal = 16.dp)) }
        AnimatedContent(
            targetState = tab,
            transitionSpec = {
                val dir = if (targetState > initialState) -1 else 1
                (fadeIn(tween(500, easing = Motion.Spring)) + slideInHorizontally(tween(500, easing = Motion.Spring)) { dir * 24 }) togetherWith fadeOut(tween(150))
            },
            modifier = Modifier.padding(top = 16.dp), label = "collegePane",
        ) { i ->
            Column {
                when (i) {
                    0 -> when {
                        loading -> Skeleton()
                        topics.isEmpty() -> EmptyState("cal", "لا توجد جداول منشورة", "لا توجد جداول رسمية منشورة حالياً لهذه الكلية.")
                        else -> DsList(Modifier.padding(horizontal = 16.dp)) {
                            topics.forEachIndexed { k, (id, title) ->
                                if (k > 0) DsDivider()
                                DsRow(onClick = { onUrl(topicUrl(id), "$title - ${college.title}") }) {
                                    RowIconBox("cal", fg)
                                    T(title, 15f, FontWeight.Medium, c.label, lh = 1.45f, modifier = Modifier.weight(1f))
                                    com.example.ui.ds.Chevron()
                                }
                            }
                        }
                    }
                    1 -> when {
                        loading -> Skeleton()
                        news.isEmpty() -> EmptyState("news", "لا توجد أخبار", "لا توجد أخبار حديثة منشورة حالياً بالملف الإخباري للكلية.")
                        else -> DsList(Modifier.padding(horizontal = 16.dp)) {
                            news.take(10).forEachIndexed { k, a ->
                                if (k > 0) DsDivider()
                                NewsRow(a, onClick = { onUrl(topicUrl(a.topicId), a.title) })
                            }
                        }
                    }
                    else -> {
                        val soc = HarDataCache.socialInfo[college.facId]
                        fun ok(s: String?) = !s.isNullOrBlank() && s != "لا يوجد"
                        DsList(Modifier.padding(horizontal = 16.dp)) {
                            DsRow(onClick = { onUrl("https://kfs.edu.eg/${college.urlName}", college.title) }) {
                                RowIconBox("globe", Color(0xFF1A5CAA))
                                Column(Modifier.weight(1f)) {
                                    T("الموقع الإلكتروني", 12.5f, color = c.label2, lh = 1.4f)
                                    T("kfs.edu.eg/${college.urlName}", 14.5f, FontWeight.SemiBold, c.label, lh = 1.4f)
                                }
                                com.example.ui.ds.Chevron()
                            }
                            if (soc != null && ok(soc.phone)) { DsDivider(); CopyRow("phone", Color(0xFF008744), "الهاتف", soc.phone) }
                            if (soc != null && ok(soc.location)) { DsDivider(); LinkOut("pin", Color(0xFFD93036), "الموقع على الخريطة") { openExternal(ctx, soc.location) } }
                            if (soc != null && ok(soc.youtube)) { DsDivider(); LinkOut("yt", Color(0xFFE0312F), "قناة اليوتيوب") { openExternal(ctx, soc.youtube) } }
                            if (soc != null && ok(soc.facebook)) {
                                DsDivider()
                                LinkOut("fb", Color(0xFF1877F2), "صفحة فيسبوك") { openExternal(ctx, "https://www.facebook.com/" + soc.facebook.substringAfterLast("/")) }
                            }
                        }
                        if (soc == null) T("لا تتوفر بيانات تواصل أخرى لهذه الجهة.", 12.5f, color = c.label2, modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkOut(icon: String, color: Color, title: String, onClick: () -> Unit) {
    val c = Ds.c
    DsRow(onClick = onClick) {
        RowIconBox(icon, color)
        T(title, 15f, FontWeight.Medium, c.label, lh = 1.45f, modifier = Modifier.weight(1f))
        DsIcon("ext", c.label3, 18.dp, 2f)
    }
}

private val ProfileServiceLabels = listOf("نتائج الطلاب", "تسجيل المقررات", "الكشف الطبي", "الدفع الإلكتروني", "البريد الإلكتروني", "المكتبة الرقمية", "منصة المقررات", "التربية العسكرية", "سكن الطلاب", "الاستمارة النهائية")

@Composable
fun ProfilePage(profile: RealStudentProfile, onBack: () -> Unit, onLogout: () -> Unit, onUrl: (String, String) -> Unit) {
    val c = Ds.c
    val faculty = remember(profile) { HarDataCache.findFacultyByInfoId(profile.asFacultyInfoId.toInt()) }
    val collegeName = faculty?.factitel ?: "غير معروف"
    val collegeUrl = faculty?.let { "https://kfs.edu.eg/${it.facUrl}" } ?: "https://kfs.edu.eg"
    var reveal by remember { mutableStateOf(false) }
    val clip = androidx.compose.ui.platform.LocalClipboardManager.current
    val toast = com.example.ui.ds.LocalToast.current
    val services = remember { ProfileServiceLabels.mapNotNull { l -> SvcStudents.firstOrNull { it.label == l } } }
    val domain = profile.email.substringAfter("@", "")
    val mailUrl = if (Regex("outlook|office|microsoft").containsMatchIn(domain)) "https://outlook.office.com/mail/" else "https://outlook.live.com/owa/"

    BackHandler(onBack = onBack)
    PageScaffold("الملف الشخصي", onBack) {
        Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)) { T("الملف الشخصي", 27f, FontWeight.Bold, c.label, lh = 1.25f) }
        Row(
            Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp).fillMaxWidth().rise(0).clip(RoundedCornerShape(22.dp)).background(c.surface)
                .border(.5.dp, c.sep, RoundedCornerShape(22.dp)).padding(14.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AccountAvatar(profile, 54.dp, 18.dp, 20f)
            Column(Modifier.weight(1f)) {
                T("مرحباً ${profile.fullName.split(" ").first()}", 16.5f, FontWeight.SemiBold, c.label)
                T(collegeName, 12.5f, FontWeight.SemiBold, c.green)
                T("شعبة: ${profile.programName}", 12f, color = c.label2)
            }
        }
        Column(Modifier.rise(1)) {
            GroupHeader("البيانات الشخصية")
            DsList(Modifier.padding(horizontal = 16.dp)) {
                CopyRow("user", Color(0xFF1A5CAA), "الاسم بالكامل", profile.fullName); DsDivider()
                CopyRow("badge", Color(0xFF1A5CAA), "الرقم القومي", profile.nationalNumber); DsDivider()
                CopyRow("badge", Color(0xFF008744), "كود الطالب", profile.studentCode); DsDivider()
                InfoRow("الكلية", collegeName); DsDivider()
                InfoRow("الشعبة", profile.programName); DsDivider()
                InfoRow("الفرقة/المستوى", profile.phaseParent); DsDivider()
                InfoRow("الحالة", profile.caseDescr); DsDivider()
                InfoRow("طبيعة الدراسة", profile.natureDescr); DsDivider()
                InfoRow("نظام الدراسة", profile.methodDescr); DsDivider()
                InfoRow("النوع", profile.gender)
            }
        }
        Column(Modifier.rise(2)) {
            GroupHeader("بيانات الحساب")
            DsList(Modifier.padding(horizontal = 16.dp)) {
                CopyRow("mail", Color(0xFFE07B00), "البريد الإلكتروني", profile.email)
                DsDivider()
                DsRow {
                    RowIconBox("lock", Color(0xFF5B3FD6))
                    Column(Modifier.weight(1f)) {
                        T("كلمة المرور الابتدائية", 12.5f, color = c.label2, lh = 1.4f)
                        AnimatedPassword(reveal, profile.notes)
                    }
                    SmallRound(if (reveal) "eyeoff" else "eye") { reveal = !reveal }
                    SmallRound("copy") { clip.setText(androidx.compose.ui.text.AnnotatedString(profile.notes)); toast("تم النسخ", "copy") }
                }
            }
            Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp)) {
                DsButton("فتح البريد الإلكتروني", { onUrl(mailUrl, "البريد الإلكتروني") }, Modifier.fillMaxWidth(), icon = "mail", big = true)
            }
        }
        Column(Modifier.rise(3)) {
            GroupHeader("خدمات الطلاب")
            Column(Modifier.padding(horizontal = 16.dp).fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(c.surface).border(.5.dp, c.sep, RoundedCornerShape(22.dp)).padding(bottom = 14.dp)) {
                ServiceGrid(services, swapKey = 0, onUrl = onUrl)
            }
        }
        Column(Modifier.rise(4)) {
            GroupHeader("خدمات الكلية")
            DsList(Modifier.padding(horizontal = 16.dp)) {
                LinkRow("globe", Color(0xFF008744), "موقع الكلية") { onUrl(collegeUrl, collegeName) }; DsDivider()
                LinkRow("cal", Color(0xFF008744), "جداول ولوائح") { onUrl(topicUrl(faculty?.facId ?: 1), "جداول ولوائح الكلية") }; DsDivider()
                LinkRow("news", Color(0xFF008744), "أخبار الكلية") { onUrl(collegeUrl, "أخبار الكلية") }
            }
        }
        Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 22.dp).rise(5)) {
            DsButton("تسجيل الخروج", onLogout, Modifier.fillMaxWidth(), icon = "logout", style = BtnStyle.Red, big = true)
        }
    }
}

@Composable
private fun AnimatedPassword(reveal: Boolean, pass: String) {
    val c = Ds.c
    AnimatedContent(reveal, transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(150)) }, label = "pw") { r ->
        T(if (r) pass else "••••••••", 14.5f, FontWeight.SemiBold, c.label, lh = 1.4f)
    }
}

@Composable
private fun SmallRound(icon: String, onClick: () -> Unit) {
    val c = Ds.c
    Box(Modifier.size(34.dp).clip(androidx.compose.foundation.shape.CircleShape).background(c.fill).dsClick(.86f, onClick = onClick), contentAlignment = Alignment.Center) {
        DsIcon(icon, c.label2, 18.dp)
    }
}
