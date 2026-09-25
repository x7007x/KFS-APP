package com.example.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.activity.compose.BackHandler

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.data.HarDataCache
import com.example.data.NewsArticle
import com.example.data.SessionManager
import com.example.data.toArticle
import com.example.data.toNewsArticle
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.example.ui.screens.*
import com.example.ui.theme.*

data class BottomTabItem(
    val index: Int,
    val label: String,
    val iconName: String,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalApp() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val context = LocalContext.current
        val sessionManager = remember { SessionManager.getInstance(context) }

        var showSplash by remember { mutableStateOf(true) }
        var selectedTabIndex by remember { mutableStateOf(0) }

        var showDrawerMenu by remember { mutableStateOf(false) }
        var showNotificationCenter by remember { mutableStateOf(false) }
        var selectedNewsArticle by remember { mutableStateOf<NewsArticle?>(null) }
        var activeWebViewUrl by remember { mutableStateOf<String?>(null) }
        var activeWebViewTitle by remember { mutableStateOf("") }

        var liveNewsList by remember { mutableStateOf<List<NewsArticle>>(HarDataCache.universityNews.map { it.toArticle() }) }
        var isNewsLoading by remember { mutableStateOf(false) }

        var showLoginType by remember { mutableStateOf<String?>(null) }
        var loggedInProfile by remember { mutableStateOf<com.example.data.RealStudentProfile?>(null) }
        var isCheckingSession by remember { mutableStateOf(true) }

        var showStudentProfile by remember { mutableStateOf(false) }
        var cameFromProfile by remember { mutableStateOf(false) }

        var isHomeRefreshing by remember { mutableStateOf(false) }
        var isNewsRefreshing by remember { mutableStateOf(false) }
        var isCollegesRefreshing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        val isOnline by rememberConnectivityState(context)

        val onHomeRefresh = remember {
            {
                isHomeRefreshing = true
                scope.launch {
                    try {
                        val items = com.example.data.KfuNetworkClient.fetchLatestNews()
                        if (items.isNotEmpty()) {
                            com.example.data.DataFetchCache.latestNews = items
                            com.example.data.DataFetchCache.newsLoaded = true
                            liveNewsList = items.map { it.toNewsArticle() }
                            com.example.data.CacheManager.saveNewsToCache(context, items)
                        }
                    } catch (_: Exception) { }
                    isHomeRefreshing = false
                }
                Unit
            }
        }

        val onNewsRefresh = remember {
            {
                isNewsRefreshing = true
                scope.launch {
                    com.example.data.DataFetchCache.newsLoaded = false
                    try {
                        val items = com.example.data.KfuNetworkClient.fetchLatestNews()
                        if (items.isNotEmpty()) {
                            com.example.data.DataFetchCache.latestNews = items
                            com.example.data.DataFetchCache.newsLoaded = true
                            liveNewsList = items.map { it.toNewsArticle() }
                            com.example.data.CacheManager.saveNewsToCache(context, items)
                        }
                    } catch (_: Exception) { }
                    isNewsRefreshing = false
                }
                Unit
            }
        }

        val onCollegesRefresh = remember {
            {
                isCollegesRefreshing = true
                scope.launch {
                    try {
                        val med = com.example.data.KfuNetworkClient.fetchColleges("2")
                        val sci = com.example.data.KfuNetworkClient.fetchColleges("1")
                        val hum = com.example.data.KfuNetworkClient.fetchColleges("3")
                        com.example.data.DataFetchCache.medicalColleges = med
                        com.example.data.DataFetchCache.scientificColleges = sci
                        com.example.data.DataFetchCache.humanitiesColleges = hum
                        com.example.data.DataFetchCache.collegesLoaded = true
                        com.example.data.CacheManager.saveCollegesToCache(context, med, "2")
                        com.example.data.CacheManager.saveCollegesToCache(context, sci, "1")
                        com.example.data.CacheManager.saveCollegesToCache(context, hum, "3")
                    } catch (_: Exception) { }
                    isCollegesRefreshing = false
                }
                Unit
            }
        }

        LaunchedEffect(Unit) {
            launch {
                if (!com.example.data.DataFetchCache.newsLoaded) {
                    isNewsLoading = true
                    try {
                        val items = com.example.data.KfuNetworkClient.fetchLatestNews()
                        if (items.isNotEmpty()) {
                            com.example.data.DataFetchCache.latestNews = items
                            com.example.data.DataFetchCache.newsLoaded = true
                            liveNewsList = items.map { it.toNewsArticle() }
                            com.example.data.CacheManager.saveNewsToCache(context, items)
                        }
                    } catch (_: Exception) {
                        val cached = com.example.data.CacheManager.loadNewsFromCache(context)
                        if (cached.isNotEmpty()) {
                            com.example.data.DataFetchCache.latestNews = cached
                            com.example.data.DataFetchCache.newsLoaded = true
                            liveNewsList = cached.map { it.toNewsArticle() }
                        }
                    } finally {
                        isNewsLoading = false
                    }
                }
            }

            if (sessionManager.isLoggedIn()) {
                val profile = sessionManager.getSavedProfile()
                if (profile != null) {
                    loggedInProfile = profile
                }
            }
            isCheckingSession = false
        }

        if (showSplash) {
            SplashScreen(onSplashFinished = { showSplash = false })
            return@CompositionLocalProvider
        }

        val rightLogo = remember(loggedInProfile) {
            loggedInProfile?.let { profile ->
                val faculty = HarDataCache.findFacultyByInfoId(profile.asFacultyInfoId.toInt())
                faculty?.let { "https://kfs.edu.eg/kfsapi/flogo/${it.facLogo}" }
            }
        }

        val bottomTabs = remember {
            listOf(
                BottomTabItem(0, "الرئيسية", "home", "tab_home"),
                BottomTabItem(1, "الأخبار", "newspaper", "tab_news"),
                BottomTabItem(2, "الكليات", "colleges", "tab_colleges"),
                BottomTabItem(3, "المزيد", "more", "tab_more")
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column {
                    TopAppBar(
                        modifier = Modifier.testTag("app_top_bar"),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White,
                            scrolledContainerColor = Color.White
                        ),
                        scrollBehavior = null,
                        title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Spacer(modifier = Modifier.width(1.dp))
                            Text(
                                text = "جامعة كفر الشيخ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = KfuBlue,
                                lineHeight = 18.sp
                            )
                            if (rightLogo != null) {
                                AsyncImage(
                                    model = rightLogo,
                                    contentDescription = "شعار الكلية",
                                    modifier = Modifier
                                        .size(56.dp)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(4.dp))
                                        .padding(end = 12.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_university),
                                    contentDescription = "شعار جامعة كفر الشيخ",
                                    modifier = Modifier
                                        .size(56.dp)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(4.dp))
                                        .padding(end = 12.dp)
                                )
                            }
                        }
                    },
                        navigationIcon = {},
                        actions = {}
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFB4DED0).copy(alpha = 0.25f))
                }
            },
            bottomBar = {
                Column {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFB4DED0).copy(alpha = 0.25f))
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .height(96.dp)
                            .navigationBarsPadding()
                    ) {
                        bottomTabs.forEach { tab ->
                            val isSelected = selectedTabIndex == tab.index
                            NavigationBarItem(
                                selected = isSelected,
                                modifier = Modifier.testTag(tab.tag),
                                onClick = {
                                    selectedTabIndex = tab.index
                                },
                                icon = {
                                    val tintColor = if (isSelected) KfuBlue else Color(0xFF999999)
                                    Box(contentAlignment = Alignment.Center) {
                                        IconBuilder(tab.iconName, isSelected = isSelected, tintColor = tintColor)
                                    }
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) KfuBlue else Color(0xFF999999)
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = KfuBlue.copy(alpha = 0.12f)
                                )
                            )
                        }
                    }
                }
            }
            ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(KfuBackground)
            ) {
                if (!isOnline) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD32F2F))
                            .padding(vertical = 6.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WifiOff,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "أنت غير متصل بالإنترنت",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Crossfade(targetState = selectedTabIndex, animationSpec = tween(500), label = "tab") { tabIndex ->
                    when (tabIndex) {
                        0 -> HomeScreen(
                            newsArticles = liveNewsList,
                            loggedInProfile = loggedInProfile,
                            isRefreshing = isHomeRefreshing,
                            onRefresh = onHomeRefresh,
                            onNavigateToTab = { index -> selectedTabIndex = index },
                            onSelectNews = { article ->
                                if (article.isReal && article.topicId > 0) {
                                    activeWebViewUrl = "https://kfs.edu.eg/news_mob.aspx?topic=${article.topicId}"
                                    activeWebViewTitle = article.title
                                } else {
                                    selectedNewsArticle = article
                                }
                            },
                            onShowLogin = { isStaff -> showLoginType = if (isStaff) "staff" else "student" },
                            onTriggerService = { id, title ->
                                val url = when (id) {
                                    "stu_results" -> "https://mis.kfs.edu.eg/newresult"
                                    "stu_portal" -> "https://kfs.edu.eg/display.aspx?topic=89154"
                                    "staff_portal" -> "https://kfs.edu.eg/display.aspx?topic=89155"
                                    "staff_news" -> "https://kfs.edu.eg"
                                    "stu_mail" -> "https://kfs.edu.eg/display.aspx?topic=89154"
                                    "stu_milit" -> "https://kfs.edu.eg/display.aspx?topic=85485"
                                    "staff_site" -> "https://kfs.edu.eg"
                                    "more_services" -> "https://kfs.edu.eg/display.aspx?topic=89154"
                                    // Student services detailed
                                    "stu_results_full" -> "https://mis.kfs.edu.eg/newresult/"
                                    "stu_reg_courses" -> "https://mis.kfs.edu.eg/CreditHours_Reg/"
                                    "stu_coordination" -> "https://portal.kfs.edu.eg/arts"
                                    "stu_medical" -> "https://mis.kfs.edu.eg/misclinic/"
                                    "stu_payment" -> "https://payments.kfs.edu.eg/"
                                    "stu_mail_full" -> "https://kfs.edu.eg/displaytopic.aspx?topic=95559"
                                    "stu_charter" -> "https://scu.eg//News/News_img/15359661961298821989.pdf"
                                    "stu_thinqi" -> "https://thinqiacademy.ekb.eg/"
                                    "stu_military_full" -> "https://kfs.edu.eg/milatrysite/"
                                    "stu_digital_lib" -> "https://kfs.edu.eg/displaytopic.aspx?topic=95556"
                                    "stu_housing" -> "https://www.kfs.edu.eg/student_affair/display_dep.aspx?topic=68220&dep=1321"
                                    "stu_final_form" -> "https://mis.kfs.edu.eg/last_year"
                                    // Staff services detailed
                                    "staff_directory" -> "https://mis.kfs.edu.eg/stff/"
                                    "staff_search" -> "https://www.kfs.edu.eg/SEARCH_STAFF_UNI.ASPX"
                                    "staff_mail" -> "https://kfs.edu.eg/university/displaytopic.aspx?topic=95533"
                                    "staff_digital_lib" -> "https://kfs.edu.eg/displaytopic.aspx?topic=95556"
                                    "staff_courses" -> "https://forms.gle/qox2UhgYGyVYtL2KA"
                                    "staff_thinqi" -> "https://sso.eetest.online/login"
                                    "staff_moodle" -> "https://moodle.kfs.edu.eg/"
                                    "staff_ictt" -> "https://kfs-ictt.metatec.app/"
                                    "staff_rdi" -> "https://www.kfs.edu.eg/display_dep.aspx?topic=87805&dep=1504"
                                    "staff_papers" -> "https://mis.kfs.edu.eg/papers/Home/Index"
                                    "staff_cv" -> "https://www.kfs.edu.eg/control_cv/"
                                    "staff_journals" -> "https://kfs.edu.eg/display_dep.aspx?topic=76465&dep=1"
                                    "staff_labs" -> "https://kfs.edu.eg/display_dep.aspx?topic=59665&dep=1504"
                                    "staff_centers" -> "https://kfs.edu.eg/display_dep.aspx?topic=75534&dep=1504"
                                    else -> "https://kfs.edu.eg"
                                }
                                activeWebViewUrl = url
                                activeWebViewTitle = title
                            },
                            onShowAboutDetail = { id ->
                                val (url, title) = when (id) {
                                    "مجلس الجامعة" -> "https://kfs.edu.eg/news_mob.aspx?topic=85455" to "مجلس جامعة كفر الشيخ"
                                    "لماذا الجامعة" -> "https://kfs.edu.eg/display.aspx?topic=70705" to "لماذا جامعة كفر الشيخ؟"
                                    "عن الجامعة" -> "https://kfs.edu.eg/display.aspx?topic=74339" to "عن جامعة كفر الشيخ"
                                    "رؤية ورسالة" -> "https://kfs.edu.eg/display.aspx?topic=1054" to "رؤية ورسالة الجامعة"
                                    else -> "https://kfs.edu.eg" to id
                                }
                                activeWebViewUrl = url
                                activeWebViewTitle = title
                            },
                            onShowStudentProfile = { showStudentProfile = true },
                            onLogout = {
                                scope.launch {
                                    sessionManager.clearSession()
                                    loggedInProfile = null
                                }
                            }
                        )
                        1 -> NewsScreen(
                            newsArticles = liveNewsList,
                            isRefreshing = isNewsRefreshing,
                            onSelectNews = { article ->
                                if (article.isReal && article.topicId > 0) {
                                    activeWebViewUrl = "https://kfs.edu.eg/news_mob.aspx?topic=${article.topicId}"
                                    activeWebViewTitle = article.title
                                } else {
                                    selectedNewsArticle = article
                                }
                            },
                            onRefresh = onNewsRefresh
                        )
                        2 -> CollegesScreen(
                            isRefreshing = isCollegesRefreshing,
                            onRefresh = onCollegesRefresh,
                            onShowWebView = { title, url ->
                                activeWebViewUrl = url
                                activeWebViewTitle = title
                            }
                        )
                        3 -> MoreScreen()
                    }
                }
                }
            }
        }

        // Drawer Menu Dialog
        if (showDrawerMenu) {
            Dialog(onDismissRequest = { showDrawerMenu = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),

                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { showDrawerMenu = false }) {
                                Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = KfuBlue)
                            }
                            Text("خيارات البوابة السريعة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = KfuBlue)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        DrawerMenuItem(Icons.Default.Info, "لوحة أخبار كفر الشيخ") {
                            showDrawerMenu = false; selectedTabIndex = 1
                        }
                        DrawerMenuItem(Icons.Default.AccountBox, "دليل الكليات والشعب") {
                            showDrawerMenu = false; selectedTabIndex = 2
                        }
                        DrawerMenuItem(Icons.Default.Settings, "إعدادات الأمان والدعم") {
                            showDrawerMenu = false; selectedTabIndex = 3
                        }
                    }
                }
            }
        }

        // Notification Center Dialog
        if (showNotificationCenter) {
            Dialog(onDismissRequest = { showNotificationCenter = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(350.dp),
                    shape = RoundedCornerShape(16.dp),                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),

                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { showNotificationCenter = false }) {
                                Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = KfuBlue)
                            }
                            Text("مركز الإشعارات الهامة", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = KfuBlue)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                NotificationItem(
                                    title = "جداول امتحانات الفصل الدراسي الثاني",
                                    desc = "تم رفع جداول امتحانات العملي والنظري لمختلف الفرق الدراسية وشعب الكليات عدا الطب البشري.",
                                    time = "منذ ساعة"
                                )
                                NotificationItem(
                                    title = "فتح الاشتراك في صندوق الدعم الاجتماعي",
                                    desc = "تعلن رعاية الشباب بالجامعة عن بدء تلقي طلبات الدعم والتسجيل الإلكتروني بمظلة الصندوق التكافلي.",
                                    time = "منذ يوم"
                                )
                                NotificationItem(
                                    title = "الملتقى التوظيفي السنوي بالجامعة لعام 2026",
                                    desc = "يقام الملتقى بمشاركة 50 شركة مصرية ودولية في الصالة المغطاة بالقرية الرياضية.",
                                    time = "منذ يومين"
                                )
                            }
                        }
                    }
                }
            }
        }

        // News Detail Dialog
        selectedNewsArticle?.let { article ->
            Dialog(onDismissRequest = { selectedNewsArticle = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(500.dp),
                    shape = RoundedCornerShape(16.dp),                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),

                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            if (article.imageUrl.isNotBlank()) {
                                coil.compose.AsyncImage(
                                    model = article.imageUrl,
                                    contentDescription = article.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = article.imageRes),
                                    contentDescription = article.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.45f))
                            )
                            IconButton(
                                onClick = { selectedNewsArticle = null },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "قفل", tint = Color.White)
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = article.date,
                                fontSize = 11.sp, color = KfuBlue, fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = article.title,
                                fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KfuTextPrimary,
                                textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth(), lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = article.content,
                                fontSize = 13.sp, color = KfuTextSecondary, textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(), lineHeight = 20.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(KfuBackground)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { selectedNewsArticle = null },
                                colors = ButtonDefaults.buttonColors(containerColor = KfuBlue),
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("مشاركة الخبر الأكاديمي", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Login Dialog
        showLoginType?.let { type ->
            var nationalId by remember { mutableStateOf("") }
            var isLoginLoading by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf("") }
            val coroutineScope = rememberCoroutineScope()

            Dialog(onDismissRequest = { if (!isLoginLoading) showLoginType = null }) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.End) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (!isLoginLoading) showLoginType = null }
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "إغلاق",
                                        tint = Color.Gray
                                    )
                                }
                                Text(
                                    text = "تسجيل الدخول بالرقم القومي",
                                    fontWeight = FontWeight.Bold, fontSize = 15.sp, color = KfuBlue,
                                    textAlign = TextAlign.Right
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = nationalId,
                                onValueChange = { newVal ->
                                    if (newVal.length <= 14 && newVal.all { it.isDigit() }) {
                                        nationalId = newVal
                                    }
                                    errorMessage = ""
                                },
                                modifier = Modifier.fillMaxWidth().testTag("login2_nid_field"),
                                readOnly = isLoginLoading,
                                label = {
                                    Text(text = "الرقم القومي (14 خانة)", textAlign = TextAlign.Right,
                                        modifier = Modifier.fillMaxWidth())
                                },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = Color.Black),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = KfuBlue,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedLabelColor = KfuBlue,
                                    unfocusedLabelColor = Color.Gray,
                                    cursorColor = KfuBlue,
                                    disabledTextColor = Color.Black,
                                    disabledLabelColor = Color.Gray,
                                    disabledBorderColor = Color.LightGray,
                                    disabledPlaceholderColor = Color.Gray
                                )
                            )

                            if (errorMessage.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = errorMessage, color = Color.Red, fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (isLoginLoading) return@Button
                                    if (nationalId.length != 14) {
                                        errorMessage = "يجب أن يتكون الرقم القومي من 14 رقماً تماماً."
                                    } else {
                                        isLoginLoading = true
                                        errorMessage = ""
                                        coroutineScope.launch {
                                            try {
                                                val profile = com.example.data.KfuNetworkClient.loginWithNationalId(nationalId)
                                                if (profile != null) {
                                                    val profileJson = org.json.JSONObject().apply {
                                                        put("NATIONAL_NUMBER", profile.nationalNumber)
                                                        put("FULL_NAME_AR", profile.fullName)
                                                        put("METHOD_DESCR", profile.email)
                                                        put("METHOD_NOTES", profile.notes)
                                                        put("STUD_CODE", profile.studentCode)
                                                        put("PASSWORD_ITEC", profile.passwordItec)
                                                        put("phase_parent", profile.phaseParent)
                                                        put("GENDER_DESCR_AR", profile.gender)
                                                        put("CASE_DESCR_AR", profile.caseDescr)
                                                        put("RELIGION_DESCR_AR", profile.religion)
                                                        put("METHOD_DESCR_AR", profile.methodDescr)
                                                        put("NATURE_DESCR_AR", profile.natureDescr)
                                                        put("ED_CODE_STUD_ENROLL_AR", profile.enrollStatus)
                                                        put("Program_Name", profile.programName)
                                                        put("AS_FACULTY_INFO_ID", profile.asFacultyInfoId)
                                                        put("STUD_PHOTO", profile.photo)
                                                    }.toString()
                                                    sessionManager.saveSession(profileJson, nationalId)
                                                    loggedInProfile = profile
                                                    showLoginType = null
                                                } else {
                                                    errorMessage = "لم يتم العثور على سجل مطابق. يرجى تجربة رقم آخر."
                                                }
                                            } catch (e: Exception) {
                                                errorMessage = "فشل الاتصال بخادم الجامعة. تأكد من اتصال الإنترنت."
                                            } finally {
                                                isLoginLoading = false
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = KfuBlue),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login2_submit_btn"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                                    if (isLoginLoading) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(22.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("تسجيل دخول", color = Color.White)
                                    }
                                }
                        }
                    }
                }
            }
        }
        }

        // Student Profile Screen
        if (showStudentProfile && loggedInProfile != null) {
            BackHandler(enabled = showStudentProfile) {
                showStudentProfile = false
            }
            StudentProfileScreen(
                profile = loggedInProfile!!,
                onClose = { showStudentProfile = false },
                onOpenWebView = { url, title ->
                    cameFromProfile = true
                    activeWebViewUrl = url
                    activeWebViewTitle = title
                },
                onLogout = {
                    scope.launch {
                        sessionManager.clearSession()
                        loggedInProfile = null
                        showStudentProfile = false
                    }
                }
            )
        }

        // WebView Screen
        activeWebViewUrl?.let { webUrl ->
            WebViewScreen(
                url = webUrl,
                title = activeWebViewTitle,
                onClose = {
                    activeWebViewUrl = null
                    activeWebViewTitle = ""
                    if (cameFromProfile) {
                        cameFromProfile = false
                        showStudentProfile = true
                    }
                }
            )
        }
    }
}

@Composable
fun IconBuilder(name: String, isSelected: Boolean, tintColor: Color) {
    val sizeMod = Modifier.size(24.dp)
    when (name) {
        "home" -> Icon(imageVector = Icons.Default.Home, contentDescription = "الرئيسية", tint = tintColor, modifier = sizeMod)
        "newspaper" -> Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "الأخبار", tint = tintColor, modifier = sizeMod)
        "services" -> Icon(imageVector = Icons.Default.Menu, contentDescription = "الخدمات", tint = tintColor, modifier = sizeMod)
        "colleges" -> Icon(imageVector = Icons.Default.AccountBox, contentDescription = "الكليات", tint = tintColor, modifier = sizeMod)
        "more" -> Icon(imageVector = Icons.Default.Apps, contentDescription = "المزيد", tint = tintColor, modifier = sizeMod)
    }
}

@Composable
fun DrawerMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = KfuTextPrimary)
        Spacer(modifier = Modifier.width(10.dp))
        Icon(imageVector = icon, contentDescription = null, tint = KfuBlue, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun NotificationItem(title: String, desc: String, time: String) {
    Card(        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),

        colors = CardDefaults.cardColors(containerColor = KfuBackground),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.End) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(time, fontSize = 10.sp, color = KfuBlue, fontWeight = FontWeight.Bold)
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KfuTextPrimary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, fontSize = 11.sp, color = KfuTextSecondary, textAlign = TextAlign.Right, lineHeight = 14.sp)
        }
    }
}

@Composable
fun rememberConnectivityState(context: Context): State<Boolean> {
    val connectivityManager = remember {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun currentlyOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    val isOnline = remember { mutableStateOf(currentlyOnline()) }

    DisposableEffect(connectivityManager) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isOnline.value = currentlyOnline()
            }

            override fun onLost(network: Network) {
                isOnline.value = currentlyOnline()
            }

            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                isOnline.value = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        isOnline.value = currentlyOnline()
        onDispose { connectivityManager.unregisterNetworkCallback(callback) }
    }

    return isOnline
}
