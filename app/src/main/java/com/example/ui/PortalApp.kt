package com.example.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.foundation.layout.ime
import androidx.compose.ui.draw.drawBehind
import com.example.ui.ds.LocalBackdrop
import com.example.ui.ds.dsGlass
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.CacheManager
import com.example.data.DataFetchCache
import com.example.data.HarDataCache
import com.example.data.KfuNetworkClient
import com.example.data.NewsArticle
import com.example.data.RealStudentProfile
import com.example.data.SessionManager
import com.example.data.toArticle
import com.example.data.toNewsArticle
import com.example.ui.ds.CollegeUi
import com.example.ui.ds.DsIcon
import com.example.ui.ds.LocalToast
import com.example.ui.ds.Motion
import com.example.ui.ds.T
import com.example.ui.ds.dsClick
import com.example.ui.overlays.BrowserSheet
import com.example.ui.overlays.LocalSheetProgress
import com.example.ui.overlays.LoginSheet
import com.example.ui.overlays.LogoutSheet
import com.example.ui.overlays.MessageSheet
import com.example.ui.overlays.NotificationsSheet
import com.example.ui.screens.CollegePage
import com.example.ui.screens.CollegesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.NewsDetail
import com.example.ui.screens.NewsScreen
import com.example.ui.screens.ProfilePage
import com.example.ui.screens.SplashOverlay
import com.example.ui.theme.Ds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private sealed interface PageKind {
    data class College(val college: CollegeUi) : PageKind
    data object Profile : PageKind
}

private var pageSeq = 0

private class PageEntry(val kind: PageKind) {
    val id = pageSeq++
    var shown by mutableStateOf(false)
}

private class NewsDetailState(val article: NewsArticle, val source: Rect)

private sealed interface SheetKind {
    data object Login : SheetKind
    data object Notifs : SheetKind
    data object Message : SheetKind
    data object Logout : SheetKind
    data class Browser(val url: String, val title: String) : SheetKind
}

private class ToastData(val message: String, val icon: String)

private val TabLabels = listOf("الرئيسية" to "home", "الأخبار" to "news", "الكليات" to "cap", "المزيد" to "grid")

private fun profileToJson(p: RealStudentProfile): String = org.json.JSONObject().apply {
    put("NATIONAL_NUMBER", p.nationalNumber); put("FULL_NAME_AR", p.fullName)
    put("METHOD_DESCR", p.email); put("METHOD_NOTES", p.notes); put("STUD_CODE", p.studentCode)
    put("PASSWORD_ITEC", p.passwordItec); put("phase_parent", p.phaseParent); put("GENDER_DESCR_AR", p.gender)
    put("CASE_DESCR_AR", p.caseDescr); put("RELIGION_DESCR_AR", p.religion); put("METHOD_DESCR_AR", p.methodDescr)
    put("NATURE_DESCR_AR", p.natureDescr); put("ED_CODE_STUD_ENROLL_AR", p.enrollStatus)
    put("Program_Name", p.programName); put("AS_FACULTY_INFO_ID", p.asFacultyInfoId); put("STUD_PHOTO", p.photo)
}.toString()

private fun buildColleges(): List<CollegeUi> {
    val live = if (DataFetchCache.collegesLoaded) {
        (DataFetchCache.medicalColleges + DataFetchCache.scientificColleges + DataFetchCache.humanitiesColleges)
            .map { CollegeUi(it.facId, it.title, it.logo, it.urlName, it.typeId, it.asFacultyInfoId) }
    } else {
        HarDataCache.allFaculties.filter { it.factype in 1..3 }
            .map { CollegeUi(it.facId, it.factitel, it.facLogo, it.facUrl, it.factype, it.asFacultyInfoId) }
    }
    val military = HarDataCache.allFaculties.filter { it.factype == 4 }
        .map { CollegeUi(it.facId, it.factitel, it.facLogo, it.facUrl, it.factype, it.asFacultyInfoId) }
    return live + military
}

@Composable
fun PortalApp(themeMode: Int, isDark: Boolean, onThemeMode: (Int) -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val c = Ds.c
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val sessionManager = remember { SessionManager.getInstance(context) }
        val isOnline by rememberConnectivityState(context)

        // ---- state
        var splashVisible by remember { mutableStateOf(true) }
        var splashClosing by remember { mutableStateOf(false) }
        var tab by remember { mutableIntStateOf(0) }
        var tabDir by remember { mutableIntStateOf(1) }
        val visits = remember { mutableStateListOf(0, 0, 0, 0) }
        val scrolls = listOf(rememberScrollState(), rememberScrollState(), rememberScrollState(), rememberScrollState())
        var newsQuery by remember { mutableStateOf("") }
        var collegeQuery by remember { mutableStateOf("") }
        var serviceTab by remember { mutableIntStateOf(0) }

        var news by remember { mutableStateOf(HarDataCache.universityNews.map { it.toArticle() }) }
        var collegesTick by remember { mutableIntStateOf(0) }
        val colleges = remember(collegesTick) { buildColleges() }
        var profile by remember { mutableStateOf<RealStudentProfile?>(null) }
        var homeRefreshing by remember { mutableStateOf(false) }
        var newsRefreshing by remember { mutableStateOf(false) }

        val pages = remember { mutableStateListOf<PageEntry>() }
        var detail by remember { mutableStateOf<NewsDetailState?>(null) }
        var sheet by remember { mutableStateOf<SheetKind?>(null) }
        var sheetP by remember { mutableFloatStateOf(0f) }
        var toast by remember { mutableStateOf<ToastData?>(null) }
        var toastShown by remember { mutableStateOf(false) }
        var toastSeq by remember { mutableIntStateOf(0) }

        // Notifications are the university's real news; an item is unread until the bell sheet has been opened after it appeared.
        val uiPrefs = remember { context.getSharedPreferences("kfu_ui", Context.MODE_PRIVATE) }
        var seenMax by remember { mutableIntStateOf(uiPrefs.getInt("notif_seen_max", -1)) }
        val newestId = remember(news) { news.maxOfOrNull { it.topicId } ?: 0 }
        LaunchedEffect(newestId) {
            if (seenMax < 0 && newestId > 0) { seenMax = newestId; uiPrefs.edit().putInt("notif_seen_max", newestId).apply() }
        }
        val unreadIds = remember(news, seenMax) { if (seenMax < 0) emptySet() else news.filter { it.topicId > seenMax }.map { it.id }.toSet() }
        fun markNotifsSeen() { if (newestId > seenMax) { seenMax = newestId; uiPrefs.edit().putInt("notif_seen_max", newestId).apply() } }
        val tabsBackdrop = rememberLayerBackdrop()
        val shellBackdrop = rememberLayerBackdrop()

        val showToast: (String, String) -> Unit = remember { { m, i -> toast = ToastData(m, i); toastShown = true; toastSeq++ } }
        LaunchedEffect(toastSeq) { if (toastShown) { delay(2200); toastShown = false } }

        // ---- data (same sources and caches as before the redesign)
        suspend fun loadNews(force: Boolean) {
            if (force) DataFetchCache.newsLoaded = false
            if (DataFetchCache.newsLoaded) return
            try {
                val items = KfuNetworkClient.fetchLatestNews()
                if (items.isNotEmpty()) {
                    DataFetchCache.latestNews = items; DataFetchCache.newsLoaded = true
                    news = items.map { it.toNewsArticle() }
                    CacheManager.saveNewsToCache(context, items)
                }
            } catch (_: Exception) {
                val cached = CacheManager.loadNewsFromCache(context)
                if (cached.isNotEmpty()) {
                    DataFetchCache.latestNews = cached; DataFetchCache.newsLoaded = true
                    news = cached.map { it.toNewsArticle() }
                }
            }
        }

        LaunchedEffect(Unit) {
            launch { loadNews(false) }
            launch {
                if (!DataFetchCache.collegesLoaded) {
                    try {
                        val med = KfuNetworkClient.fetchColleges("2")
                        val sci = KfuNetworkClient.fetchColleges("1")
                        val hum = KfuNetworkClient.fetchColleges("3")
                        if (med.isNotEmpty() || sci.isNotEmpty() || hum.isNotEmpty()) {
                            DataFetchCache.medicalColleges = med; DataFetchCache.scientificColleges = sci
                            DataFetchCache.humanitiesColleges = hum; DataFetchCache.collegesLoaded = true
                            CacheManager.saveCollegesToCache(context, med, "2")
                            CacheManager.saveCollegesToCache(context, sci, "1")
                            CacheManager.saveCollegesToCache(context, hum, "3")
                            collegesTick++
                        }
                    } catch (_: Exception) {
                        val med = CacheManager.loadCollegesFromCache(context, "2")
                        val sci = CacheManager.loadCollegesFromCache(context, "1")
                        val hum = CacheManager.loadCollegesFromCache(context, "3")
                        if (med.isNotEmpty() || sci.isNotEmpty() || hum.isNotEmpty()) {
                            DataFetchCache.medicalColleges = med; DataFetchCache.scientificColleges = sci
                            DataFetchCache.humanitiesColleges = hum; DataFetchCache.collegesLoaded = true
                            collegesTick++
                        }
                    }
                }
            }
            if (sessionManager.isLoggedIn()) profile = sessionManager.getSavedProfile()
        }

        // ---- actions
        fun selectTab(i: Int) {
            if (i == tab) { scope.launch { scrolls[i].animateScrollTo(0) }; return }
            tabDir = if (i > tab) 1 else -1
            tab = i
            visits[i] = visits[i] + 1
        }
        fun openUrl(url: String, title: String) { sheet = SheetKind.Browser(url, title) }
        fun pushPage(kind: PageKind) { if (pages.lastOrNull()?.kind != kind) pages.add(PageEntry(kind)) }
        fun popTop() {
            val e = pages.lastOrNull() ?: return
            e.shown = false
            scope.launch { delay(520); pages.remove(e) }
        }
        fun openNews(a: NewsArticle, r: Rect) { detail = NewsDetailState(a, r) }

        BackHandler(enabled = pages.isNotEmpty() && detail == null && sheet == null) { popTop() }

        // ---- shell transforms
        val covered = pages.isNotEmpty() || detail != null
        val under by animateFloatAsState(if (covered) 1f else 0f, tween(550, easing = Motion.Spring), label = "under")
        val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        CompositionLocalProvider(LocalToast provides showToast, LocalSheetProgress provides { sheetP = it }) {
            Box(Modifier.fillMaxSize().background(c.bg)) {
                // shell: base + pages + detail, scaled back while a sheet is open
                Box(
                    Modifier.fillMaxSize().then(if (sheet != null || toastShown) Modifier.layerBackdrop(shellBackdrop) else Modifier).graphicsLayer {
                        val s = 1f - .07f * sheetP
                        scaleX = s; scaleY = s
                        translationY = -6.dp.toPx() * sheetP
                        transformOrigin = TransformOrigin(.5f, 1f)
                        shape = RoundedCornerShape(30.dp.toPx() * sheetP)
                        clip = sheetP > 0f
                    },
                ) {
                    // base layer
                    val glassPaused by remember { androidx.compose.runtime.derivedStateOf { under > .001f } }
                    CompositionLocalProvider(com.example.ui.ds.LocalGlassPaused provides glassPaused) {
                    Box(Modifier.fillMaxSize().graphicsLayer { translationX = size.width * .26f * under }) {
                        Box(Modifier.fillMaxSize().layerBackdrop(tabsBackdrop).background(c.bg)) {
                        for (i in 0..3) {
                            key(i) {
                                TabLayer(on = tab == i, dir = tabDir) {
                                        val ek = visits[i] + if (splashClosing) 1 else 0
                                        when (i) {
                                            0 -> HomeScreen(
                                                scroll = scrolls[0], enterKey = ek, news = news, colleges = colleges, profile = profile, isDark = isDark, unread = unreadIds.size,
                                                refreshing = homeRefreshing, serviceTab = serviceTab, onServiceTab = { serviceTab = it },
                                                onRefresh = { homeRefreshing = true; scope.launch { loadNews(true); homeRefreshing = false; showToast("تم تحديث الرئيسية", "refresh") } },
                                                onToggleTheme = { onThemeMode(if (isDark) 1 else 2); showToast(if (isDark) "الوضع الفاتح" else "الوضع الداكن", if (isDark) "sun" else "moon") },
                                                onBell = { sheet = SheetKind.Notifs }, onLogin = { sheet = SheetKind.Login },
                                                onProfile = { pushPage(PageKind.Profile) }, onLogout = { sheet = SheetKind.Logout },
                                                onNewsAll = { selectTab(1) }, onNews = ::openNews, onUrl = ::openUrl,
                                            )
                                            1 -> NewsScreen(
                                                scroll = scrolls[1], enterKey = ek, news = news, query = newsQuery, onQuery = { newsQuery = it },
                                                refreshing = newsRefreshing,
                                                onRefresh = { newsRefreshing = true; scope.launch { loadNews(true); newsRefreshing = false; showToast("تم تحديث الأخبار", "refresh") } },
                                                onNews = ::openNews,
                                            )
                                            2 -> CollegesScreen(
                                                scroll = scrolls[2], enterKey = ek, colleges = colleges, query = collegeQuery, onQuery = { collegeQuery = it },
                                                onCollege = { pushPage(PageKind.College(it)) },
                                            )
                                            else -> MoreScreen(
                                                scroll = scrolls[3], enterKey = ek, profile = profile,
                                                themeMode = themeMode, onThemeMode = onThemeMode,
                                                onMessage = { sheet = SheetKind.Message }, onLogin = { sheet = SheetKind.Login },
                                                onProfile = { pushPage(PageKind.Profile) }, onLogout = { sheet = SheetKind.Logout }, onUrl = ::openUrl,
                                            )
                                        }
                                }
                            }
                        }
                        }
                        CompositionLocalProvider(LocalBackdrop provides tabsBackdrop) { TabBar(tab, ::selectTab, Modifier.align(Alignment.BottomCenter)) }
                        OfflineBanner(!isOnline, statusTop)
                    }
                    }
                    Box(Modifier.fillMaxSize().drawBehind { drawRect(Color.Black.copy(alpha = .1f * under)) })

                    // pushed pages (college, profile)
                    pages.forEachIndexed { idx, entry ->
                        key(entry.id) {
                            PageHost(entry, covered = idx < pages.lastIndex || detail != null, onRemoveNow = { pages.remove(entry) }) {
                                when (val k = entry.kind) {
                                    is PageKind.College -> CollegePage(k.college, onBack = ::popTop, onUrl = ::openUrl)
                                    PageKind.Profile -> {
                                        val p = profile
                                        if (p != null) ProfilePage(p, onBack = ::popTop, onLogout = { sheet = SheetKind.Logout }, onUrl = ::openUrl)
                                        else LaunchedEffect(Unit) { popTop() }
                                    }
                                }
                            }
                        }
                    }

                    // news detail with the shared-element reveal
                    detail?.let { d ->
                        key(d.article.id) {
                            NewsDetail(
                                article = d.article, source = d.source,
                                others = news.filter { it.id != d.article.id },
                                onOpenNews = ::openNews, onUrl = ::openUrl, onDismissed = { detail = null },
                            )
                        }
                    }
                    Box(Modifier.fillMaxSize().drawBehind { drawRect(Color.Black.copy(alpha = .18f * sheetP)) })
                }

                // sheets
                CompositionLocalProvider(LocalBackdrop provides shellBackdrop) {
                when (val s = sheet) {
                    null -> {}
                    SheetKind.Login -> LoginSheet(
                        onLoggedIn = { p, nid ->
                            sessionManager.saveSession(profileToJson(p), nid)
                            profile = p
                            scope.launch { com.example.data.ProfilePhotoStore.ensure(context, p.nationalNumber) }
                            showToast("تم تسجيل الدخول", "check")
                        },
                        onDismiss = { sheet = null },
                    )
                    SheetKind.Notifs -> NotificationsSheet(
                        items = news, unreadIds = unreadIds,
                        onOpen = { a, r -> openNews(a, r) },
                        onDismiss = { markNotifsSeen(); sheet = null },
                    )
                    SheetKind.Message -> MessageSheet { sheet = null }
                    SheetKind.Logout -> LogoutSheet(
                        onConfirm = {
                            scope.launch {
                                sessionManager.clearSession()
                                com.example.data.ProfilePhotoStore.delete(context)
                                profile = null
                                pages.clear()
                                showToast("تم تسجيل الخروج", "check")
                            }
                        },
                        onDismiss = { sheet = null },
                    )
                    is SheetKind.Browser -> key(s.url) { BrowserSheet(s.url, s.title) { sheet = null } }
                }
                }

                // toast
                val tyState = animateFloatAsState(if (toastShown) 0f else -1f, tween(550, easing = Motion.Bounce), label = "toast")
                val t = toast
                if (t != null && (toastShown || tyState.value > -.99f)) {
                  CompositionLocalProvider(LocalBackdrop provides shellBackdrop) {
                    Row(
                        Modifier.align(Alignment.TopCenter).padding(top = statusTop + 14.dp)
                            .graphicsLayer { translationY = tyState.value * 140.dp.toPx() }
                            .height(42.dp)
                            .dsGlass(RoundedCornerShape(21.dp), blurDp = 14f).padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        DsIcon(t.icon, c.green, 18.dp, 2.2f)
                        T(t.message, 14f, FontWeight.SemiBold, c.label, lh = 1.2f, maxLines = 1)
                    }
                  }
                }

                if (splashVisible) SplashOverlay(onCollapse = { splashClosing = true }, onFinished = { splashVisible = false })
            }
        }
    }
}

/** One tab of the base layer: cross-fades and slides in the direction of travel, leaving composition once hidden. */
@Composable
private fun TabLayer(on: Boolean, dir: Int, content: @Composable () -> Unit) {
    val p = remember { Animatable(if (on) 1f else 0f) }
    var shown by remember { mutableStateOf(on) }
    LaunchedEffect(on) {
        if (on) { shown = true; p.animateTo(1f, tween(550, easing = Motion.Spring)) }
        else { p.animateTo(0f, tween(550, easing = Motion.Spring)); shown = false }
    }
    if (shown) {
        Box(
            Modifier.fillMaxSize().graphicsLayer {
                val sign = if (on) -dir else dir
                translationX = (1f - p.value) * sign * 30.dp.toPx()
                val sc = .985f + .015f * p.value
                scaleX = sc; scaleY = sc; alpha = p.value
            },
        ) { content() }
    }
}

/** One pushed page: slides in from the leading edge, supports interactive edge-swipe back. */
@Composable
private fun PageHost(entry: PageEntry, covered: Boolean, onRemoveNow: () -> Unit, content: @Composable () -> Unit) {
    val c = Ds.c
    var dragPx by remember { mutableFloatStateOf(0f) }
    val under by animateFloatAsState(if (covered) 1f else 0f, tween(550, easing = Motion.Spring), label = "pageUnder")
    LaunchedEffect(Unit) { entry.shown = true }
    AnimatedVisibility(
        visible = entry.shown,
        enter = slideInHorizontally(tween(580, easing = Motion.Spring)) { -it },
        exit = slideOutHorizontally(tween(480, easing = Motion.Spring)) { -it },
    ) {
        Box(
            Modifier.fillMaxSize().graphicsLayer { translationX = -dragPx + size.width * .26f * under }
                .background(c.bg),
        ) {
            content()
            Box(Modifier.fillMaxSize().drawBehind { drawRect(Color.Black.copy(alpha = .1f * under)) })
            // interactive swipe back from the leading edge
            val scope = rememberCoroutineScope()
            Box(
                Modifier.align(Alignment.TopStart).padding(top = statusBarTopPad() + 64.dp).fillMaxHeight().width(18.dp)
                    .pointerInput(Unit) {
                        val w = size.width.toFloat()
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    if (dragPx > 120.dp.toPx()) {
                                        animate(dragPx, w.coerceAtLeast(1f) + 4000f, animationSpec = tween(220)) { v, _ -> dragPx = v }
                                        onRemoveNow()
                                    } else {
                                        animate(dragPx, 0f, animationSpec = tween(400, easing = Motion.Spring)) { v, _ -> dragPx = v }
                                    }
                                }
                            },
                            onDragCancel = { dragPx = 0f },
                        ) { _, dx -> dragPx = (dragPx - dx).coerceAtLeast(0f) }
                    },
            )
        }
    }
}

@Composable
private fun statusBarTopPad() = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

/** Floating glass tab bar with a spring-driven selection pill. */
@Composable
private fun TabBar(selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val c = Ds.c
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val pos by animateFloatAsState(selected.toFloat(), tween(600, easing = Motion.TabIndicator), label = "tabPos")
    // The keyboard owns the bottom of the screen, so the bar steps aside while it is open.
    val imeOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val hide by animateFloatAsState(if (imeOpen) 1f else 0f, tween(250), label = "tabHide")
    if (hide >= .99f) return
    BoxWithConstraints(
        modifier.graphicsLayer { alpha = 1f - hide; translationY = hide * 40.dp.toPx() }
            .padding(start = 14.dp, end = 14.dp, bottom = 14.dp + bottom).fillMaxWidth().height(66.dp)
            .dsGlass(RoundedCornerShape(33.dp), blurDp = 20f).padding(6.dp),
    ) {
        val w = maxWidth / 4
        val wPx = with(androidx.compose.ui.platform.LocalDensity.current) { w.toPx() }
        Box(Modifier.offset { IntOffset((pos * wPx).roundToInt(), 0) }.width(w).fillMaxHeight().background(c.tintSoft, RoundedCornerShape(27.dp)))
        Row(Modifier.fillMaxSize()) {
            TabLabels.forEachIndexed { i, (label, icon) ->
                val on = i == selected
                val pop = remember { Animatable(1f) }
                LaunchedEffect(on) { if (on) { pop.snapTo(.5f); pop.animateTo(1f, tween(550, easing = Motion.Bounce)) } }
                Column(
                    Modifier.weight(1f).fillMaxHeight().dsClick(.94f) { onSelect(i) },
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
                ) {
                    Box(Modifier.graphicsLayer { scaleX = pop.value; scaleY = pop.value }) { DsIcon(icon, if (on) c.tint else c.label2, 24.dp) }
                    T(label, 11f, if (on) FontWeight.SemiBold else FontWeight.Medium, if (on) c.tint else c.label2, lh = 1.2f)
                }
            }
        }
    }
}

@Composable
private fun OfflineBanner(offline: Boolean, statusTop: androidx.compose.ui.unit.Dp) {
    val c = Ds.c
    val y by animateFloatAsState(if (offline) 0f else -1f, tween(500, easing = Motion.Spring), label = "offline")
    if (y > -.99f) {
        Row(
            Modifier.fillMaxWidth().graphicsLayer { translationY = y * size.height }.background(c.red).padding(top = statusTop).height(34.dp + statusTop),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically,
        ) {
            DsIcon("wifioff", Color.White, 16.dp, 2f)
            T("أنت غير متصل بالإنترنت", 13f, FontWeight.SemiBold, Color.White, lh = 1.2f)
        }
    }
}

@Composable
fun rememberConnectivityState(context: Context): State<Boolean> {
    val cm = remember { context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    fun online(): Boolean {
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    val state = remember { mutableStateOf(online()) }
    DisposableEffect(cm) {
        val cb = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { state.value = online() }
            override fun onLost(network: Network) { state.value = online() }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) { state.value = online() }
        }
        try { cm.registerDefaultNetworkCallback(cb) } catch (_: Exception) { }
        onDispose { try { cm.unregisterNetworkCallback(cb) } catch (_: Exception) { } }
    }
    return state
}
