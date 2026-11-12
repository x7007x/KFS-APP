package com.example.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.data.HarDataCache
import com.example.data.NewsArticle
import com.example.data.RealStudentProfile
import com.example.ui.ds.BtnStyle
import com.example.ui.ds.CollegeUi
import com.example.ui.ds.DsButton
import com.example.ui.ds.DsIcon
import com.example.ui.ds.DsPic
import com.example.ui.ds.DsSeg
import com.example.ui.ds.GlassCircle
import com.example.ui.ds.Motion
import com.example.ui.ds.QuickLinks
import com.example.ui.ds.Svc
import com.example.ui.ds.SvcStaff
import com.example.ui.ds.SvcStudents
import com.example.ui.ds.T
import com.example.ui.ds.TabScaffold
import com.example.ui.ds.TileGradients
import com.example.ui.ds.dsClick
import com.example.ui.ds.fmtDate
import com.example.ui.ds.greeting
import com.example.ui.ds.nf
import com.example.ui.ds.pop
import com.example.ui.ds.rise
import com.example.ui.theme.Ds

private const val HERO_URL = "https://www.arabcont.com/Images/ProjectImage/ConferenceHall_KafrElSheikhUniversity-01.jpg"

@Composable
fun AccountAvatar(profile: RealStudentProfile, size: androidx.compose.ui.unit.Dp, radius: androidx.compose.ui.unit.Dp, textSize: Float) {
    val c = Ds.c
    val ctx = LocalContext.current
    val photo by produceState<java.io.File?>(null, profile.nationalNumber) {
        value = com.example.data.ProfilePhotoStore.ensure(ctx, profile.nationalNumber)
    }
    Box(
        Modifier.size(size).clip(RoundedCornerShape(radius))
            .background(Brush.linearGradient(listOf(Color(0xFF2F86D9), Color(0xFF1A5CAA)))),
        contentAlignment = Alignment.Center,
    ) {
        T(profile.fullName.take(1), textSize, FontWeight.Bold, Color.White)
        val f = photo
        if (f != null) {
            AsyncImage(
                model = ImageRequest.Builder(ctx).data(f).crossfade(300).build(),
                contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun HomeScreen(
    scroll: ScrollState,
    enterKey: Int,
    news: List<NewsArticle>,
    colleges: List<CollegeUi>,
    profile: RealStudentProfile?,
    isDark: Boolean,
    unread: Int,
    refreshing: Boolean,
    serviceTab: Int,
    onServiceTab: (Int) -> Unit,
    onRefresh: () -> Unit,
    onToggleTheme: () -> Unit,
    onBell: () -> Unit,
    onLogin: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit,
    onNewsAll: () -> Unit,
    onNews: (NewsArticle, Rect) -> Unit,
    onUrl: (String, String) -> Unit,
) {
    val c = Ds.c
    var swaps by remember { mutableIntStateOf(0) }
    TabScaffold(
        scroll = scroll, title = "جامعة كفر الشيخ", showLogo = true,
        refreshing = refreshing, onRefresh = onRefresh,
        trailing = {
            GlassCircle(if (isDark) "sun" else "moon", "تبديل المظهر", onToggleTheme)
            GlassCircle("bell", "الإشعارات", onBell, badge = if (unread > 0) nf(minOf(unread, 9)) else null)
            if (profile != null) {
                Box(Modifier.dsClick(.88f, onClick = onProfile)) { AccountAvatar(profile, 38.dp, 19.dp, 15f) }
            } else {
                GlassCircle("user", "تسجيل الدخول", onLogin)
            }
        },
    ) {
        // large title
        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 14.dp).rise(0, enterKey)) {
            T(if (profile != null) "مرحباً ${profile.fullName.split(" ").first()}" else greeting(), 13f, FontWeight.SemiBold, c.green)
            T("جامعة كفر الشيخ", 27f, FontWeight.Bold, c.label, lh = 1.25f)
        }

        // hero with parallax
        val collegeCount = colleges.count { it.type < 4 }
        val serviceCount = SvcStudents.size + SvcStaff.size
        Box(
            Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(208.dp).rise(1, enterKey)
                .clip(RoundedCornerShape(28.dp)).background(c.tintD),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(HERO_URL).crossfade(500).build(),
                contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().graphicsLayer {
                    val s = scroll.value * .22f
                    translationY = minOf(60.dp.toPx(), s)
                    scaleX = 1.2f; scaleY = 1.2f
                },
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        0f to Color(0x1A0D3D75), .45f to Color(0x590D3D75), 1f to Color(0xE0071C3A),
                    ),
                ),
            )
            Column(Modifier.align(Alignment.BottomStart).padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                T("كل خدمات الجامعة في مكان واحد", 21f, FontWeight.Bold, Color.White, lh = 1.35f, modifier = Modifier.widthIn(max = 315.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HeroChip("building", "${nf(collegeCount)} كلية ومعهد")
                    HeroChip("grid", "${nf(serviceCount)} خدمة")
                }
            }
        }

        // sign-in card (signed-in students use the avatar in the bar to reach their account)
        if (profile == null) {
            Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp).rise(2, enterKey)) { LoggedOutCard(onLogin) }
        }

        // news
        SectionHeader("أهم الأخبار", "عرض الكل", onNewsAll, Modifier.rise(3, enterKey))
        val rail = rememberLazyListState()
        val shown = news.take(8)
        if (shown.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) { T("لا توجد أخبار متاحة حالياً", 14f, color = c.label2) }
        } else {
            LazyRow(
                state = rail, flingBehavior = rememberSnapFlingBehavior(rail),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.rise(4, enterKey),
            ) {
                itemsIndexed(shown, key = { _, a -> a.id }) { i, a ->
                    NewsCard(a, i, rail, onNews)
                }
            }
        }

        // quick links
        SectionHeader("روابط سريعة", null, {}, Modifier.rise(5, enterKey))
        Row(Modifier.padding(horizontal = 16.dp).rise(6, enterKey), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickLinks.forEach { q ->
                Column(
                    Modifier.weight(1f).heightIn(min = 94.dp).clip(RoundedCornerShape(20.dp)).background(c.surface)
                        .border(.5.dp, c.sep, RoundedCornerShape(20.dp)).dsClick { onUrl(q.url, q.title) }
                        .padding(start = 4.dp, end = 4.dp, top = 14.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DsIcon(q.icon, c.tint, 26.dp, 1.7f)
                    T(q.label, 11.5f, FontWeight.SemiBold, c.label, lh = 1.4f, align = TextAlign.Center)
                }
            }
        }

        // services
        SectionHeader("الخدمات", null, {}, Modifier.rise(7, enterKey))
        Column(Modifier.rise(8, enterKey)) {
            DsSeg(listOf("الطلاب", "أعضاء هيئة التدريس"), serviceTab, { swaps++; onServiceTab(it) }, Modifier.padding(horizontal = 16.dp))
            ServiceGrid(if (serviceTab == 0) SvcStudents else SvcStaff, swapKey = swaps, onUrl = onUrl)
        }
    }
}

@Composable
private fun HeroChip(icon: String, text: String) {
    Row(
        Modifier.height(28.dp).clip(RoundedCornerShape(14.dp)).background(Color(0x2EFFFFFF)).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        DsIcon(icon, Color.White, 14.dp, 2f)
        T(text, 12.5f, FontWeight.Medium, Color.White, lh = 1.2f)
    }
}

@Composable
fun SectionHeader(title: String, action: String?, onAction: () -> Unit, modifier: Modifier = Modifier) {
    val c = Ds.c
    Row(
        modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom,
    ) {
        T(title, 20f, FontWeight.Bold, c.label, lh = 1.3f)
        if (action != null) {
            Row(Modifier.dsClick(.94f, onClick = onAction), verticalAlignment = Alignment.CenterVertically) {
                T(action, 14f, FontWeight.Medium, c.tint)
                DsIcon("chevL", c.tint, 16.dp, 2.2f)
            }
        }
    }
}

@Composable
private fun LoggedOutCard(onLogin: () -> Unit) {
    val c = Ds.c
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(c.surface).border(.5.dp, c.sep, RoundedCornerShape(22.dp)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(46.dp).background(c.tintSoft, RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
            DsIcon("cap", c.tint, 26.dp)
        }
        Column(Modifier.weight(1f)) {
            T("سجّل الدخول لحسابك", 15f, FontWeight.SemiBold, c.label)
            T("ادخل بالرقم القومي لعرض بياناتك وخدماتك.", 12.5f, color = c.label2, lh = 1.45f)
        }
        DsButton("دخول", onLogin)
    }
}

@Composable
private fun LoggedInCard(profile: RealStudentProfile, onProfile: () -> Unit, onLogout: () -> Unit) {
    val c = Ds.c
    val faculty = remember(profile) { HarDataCache.findFacultyByInfoId(profile.asFacultyInfoId.toInt()) }
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(c.surface).border(.5.dp, c.sep, RoundedCornerShape(22.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AccountAvatar(profile, 54.dp, 18.dp, 20f)
            Column(Modifier.weight(1f)) {
                T("مرحباً ${profile.fullName.split(" ").first()}", 16.5f, FontWeight.SemiBold, c.label)
                if (faculty != null) T(faculty.factitel, 12.5f, FontWeight.SemiBold, c.green)
                T(profile.programName, 12f, color = c.label2)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DsButton("حسابي", onProfile, Modifier.weight(1f), icon = "user")
            DsButton("تسجيل خروج", onLogout, Modifier.weight(1f), icon = "logout", style = BtnStyle.Red)
        }
    }
}

/** News card: scales to 92% and fades to 65% as it moves away from the centre of the rail. */
@Composable
private fun NewsCard(a: NewsArticle, index: Int, rail: androidx.compose.foundation.lazy.LazyListState, onNews: (NewsArticle, Rect) -> Unit) {
    val c = Ds.c
    var bounds by remember { mutableStateOf(Rect.Zero) }
    Column(
        Modifier.width(266.dp)
            .graphicsLayer {
                val info = rail.layoutInfo
                val item = info.visibleItemsInfo.firstOrNull { it.index == index }
                if (item != null) {
                    val vpW = (info.viewportEndOffset - info.viewportStartOffset).toFloat()
                    val center = info.viewportStartOffset + vpW / 2f
                    val d = (kotlin.math.abs(item.offset + item.size / 2f - center) / (vpW * .8f)).coerceIn(0f, 1f)
                    val s = 1f - d * .08f
                    scaleX = s; scaleY = s; alpha = 1f - d * .35f
                }
            }
            .clip(RoundedCornerShape(24.dp)).background(c.surface)
            .border(.5.dp, c.sep, RoundedCornerShape(24.dp))
            .onGloballyPositioned { bounds = it.boundsInRoot() }
            .dsClick(.97f) { onNews(a, bounds) },
    ) {
        DsPic(a.imageUrl, a.topicId, Modifier.fillMaxWidth().height(150.dp))
        Column(Modifier.padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            T(fmtDate(a.date), 12f, FontWeight.SemiBold, c.green, lh = 1.3f)
            T(a.title, 14.5f, FontWeight.SemiBold, c.label, lh = 1.55f, maxLines = 2)
        }
    }
}

@Composable
fun ServiceGrid(list: List<Svc>, swapKey: Int, onUrl: (String, String) -> Unit) {
    val c = Ds.c
    val rows = list.chunked(4)
    Column(Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 18.dp, bottom = 4.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        var k = 0
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { s ->
                    val idx = k++
                    val g = TileGradients[idx % TileGradients.size]
                    val tileMod = if (swapKey != 0) Modifier.pop(idx, swapKey) else Modifier
                    Column(
                        Modifier.weight(1f).then(tileMod).dsClick(.88f) { onUrl(s.url, s.title) },
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        Box(
                            Modifier.size(58.dp)
                                .clip(RoundedCornerShape(17.dp))
                                .background(Brush.linearGradient(listOf(Color(g.first), Color(g.second)))),
                            contentAlignment = Alignment.Center,
                        ) { DsIcon(s.icon, Color.White, 27.dp, 1.7f) }
                        T(s.label, 11.5f, FontWeight.Medium, c.label, lh = 1.35f, align = TextAlign.Center, maxLines = 2)
                    }
                }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
