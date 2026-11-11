package com.example.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.NewsArticle
import com.example.ui.ds.BtnStyle
import com.example.ui.ds.Chevron
import com.example.ui.ds.CollegeUi
import com.example.ui.ds.DsButton
import com.example.ui.ds.DsDivider
import com.example.ui.ds.DsList
import com.example.ui.ds.DsMono
import com.example.ui.ds.DsPic
import com.example.ui.ds.DsRow
import com.example.ui.ds.DsSeg
import com.example.ui.ds.EmptyState
import com.example.ui.ds.GroupHeader
import com.example.ui.ds.T
import com.example.ui.ds.TabScaffold
import com.example.ui.ds.dsClick
import com.example.ui.ds.fmtDate
import com.example.ui.ds.fmtMonth
import com.example.ui.ds.monthKey
import com.example.ui.ds.nf
import com.example.ui.ds.normalizeAr
import com.example.ui.ds.rise
import com.example.ui.theme.Ds
import com.example.ui.theme.DsColors

@Composable
fun NewsScreen(
    scroll: ScrollState,
    enterKey: Int,
    news: List<NewsArticle>,
    query: String,
    onQuery: (String) -> Unit,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onNews: (NewsArticle, Rect) -> Unit,
) {
    val c = Ds.c
    TabScaffold(scroll, "الأخبار", refreshing = refreshing, onRefresh = onRefresh) {
        Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 14.dp).rise(0, enterKey)) { T("الأخبار", 33f, FontWeight.Bold, c.label, lh = 1.25f) }
        Box(Modifier.rise(1, enterKey)) { DsSearch(query, onQuery, "ابحث في الأخبار") }
        Column(Modifier.rise(2, enterKey)) {
            val q = normalizeAr(query.trim())
            val items = news.filter { q.isEmpty() || normalizeAr(it.title).contains(q) }.sortedByDescending { it.date }
            if (items.isEmpty()) {
                EmptyState("search", "لا توجد نتائج", if (news.isEmpty()) "لا توجد أخبار متاحة حالياً." else "لم نجد خبراً يطابق «$query». جرّب كلمة أخرى.",
                    if (news.isEmpty()) null else "مسح البحث") { onQuery("") }
            } else {
                var rest = items
                if (q.isEmpty()) {
                    FeaturedNews(items.first(), onNews)
                    rest = items.drop(1)
                }
                rest.groupBy { monthKey(it.date) }.toSortedMap(compareByDescending<String> { it }).forEach { (m, list) ->
                    T(fmtMonth("$m-01"), 13f, FontWeight.SemiBold, c.label2, modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 8.dp))
                    DsList(Modifier.padding(horizontal = 16.dp)) {
                        list.forEachIndexed { i, a ->
                            if (i > 0) DsDivider()
                            NewsRow(a, onClick = { onNews(a, it) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedNews(a: NewsArticle, onNews: (NewsArticle, Rect) -> Unit) {
    var bounds by remember { mutableStateOf(Rect.Zero) }
    Box(
        Modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp).fillMaxWidth().height(250.dp)
            .shadow(14.dp, RoundedCornerShape(26.dp)).clip(RoundedCornerShape(26.dp))
            .onGloballyPositioned { bounds = it.boundsInRoot() }
            .dsClick(.97f) { onNews(a, bounds) },
    ) {
        DsPic(a.imageUrl, a.topicId, Modifier.fillMaxWidth().height(250.dp))
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(0f to Color.Transparent, .4f to Color(0x33051430), 1f to Color(0xE6051430))))
        Column(Modifier.align(Alignment.BottomStart).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.height(24.dp).clip(RoundedCornerShape(12.dp)).background(Color(0x33FFFFFF)).padding(horizontal = 10.dp), contentAlignment = Alignment.Center) {
                T("آخر الأخبار · ${fmtDate(a.date)}", 12f, FontWeight.SemiBold, Color.White, lh = 1.2f)
            }
            T(a.title, 18f, FontWeight.Bold, Color.White, lh = 1.5f)
        }
    }
}

private fun groupColors(c: DsColors, type: Int): Pair<Color, Color> = when (type) {
    1 -> c.gSci to c.gSciS
    3 -> c.gHum to c.gHumS
    else -> c.gMed to c.gMedS
}

val CollegeGroups = listOf(
    2 to "الكليات الطبية والرعاية الصحية",
    1 to "الكليات العملية والهندسية",
    3 to "الكليات الإنسانية والتربوية والإدارية",
    4 to "التربية العسكرية",
)

@Composable
fun CollegesScreen(
    scroll: ScrollState,
    enterKey: Int,
    colleges: List<CollegeUi>,
    query: String,
    onQuery: (String) -> Unit,
    onCollege: (CollegeUi) -> Unit,
) {
    val c = Ds.c
    TabScaffold(scroll, "الكليات") {
        Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 14.dp).rise(0, enterKey)) { T("الكليات", 33f, FontWeight.Bold, c.label, lh = 1.25f) }
        Box(Modifier.rise(1, enterKey)) { DsSearch(query, onQuery, "ابحث عن كلية أو معهد") }
        Column(Modifier.rise(2, enterKey)) {
            val q = normalizeAr(query.trim())
            var any = false
            CollegeGroups.forEach { (type, title) ->
                val items = colleges.filter { it.type == type && (q.isEmpty() || normalizeAr(it.title).contains(q)) }
                if (items.isEmpty()) return@forEach
                any = true
                val (fg, bg) = groupColors(c, type)
                Row(
                    Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, top = 20.dp, bottom = 7.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(Modifier.size(7.dp).background(fg, CircleShape))
                    T(title, 13f, FontWeight.SemiBold, fg, lh = 1.3f, modifier = Modifier.weight(1f))
                    T(nf(items.size), 13f, FontWeight.Medium, c.label2, lh = 1.3f)
                }
                DsList(Modifier.padding(horizontal = 16.dp)) {
                    items.forEachIndexed { i, f ->
                        if (i > 0) DsDivider()
                        DsRow(onClick = { onCollege(f) }) {
                            DsMono(f.title, "https://kfs.edu.eg/kfsapi/flogo/${f.logo}", fg, bg)
                            T(f.title, 15f, FontWeight.Medium, c.label, lh = 1.45f, modifier = Modifier.weight(1f))
                            Chevron()
                        }
                    }
                }
            }
            if (!any) {
                EmptyState("search", "لا توجد نتائج", if (colleges.isEmpty()) "تعذر تحميل بيانات الكليات. اسحب للتحديث." else "لم نجد كلية تطابق «$query».",
                    if (colleges.isEmpty()) null else "مسح البحث") { onQuery("") }
            }
        }
    }
}

@Composable
fun MoreScreen(
    scroll: ScrollState,
    enterKey: Int,
    loggedIn: Boolean,
    studentName: String,
    themeMode: Int,
    onThemeMode: (Int) -> Unit,
    onMessage: () -> Unit,
    onLogin: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit,
    onUrl: (String, String) -> Unit,
) {
    val c = Ds.c
    TabScaffold(scroll, "المزيد") {
        Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 14.dp).rise(0, enterKey)) { T("المزيد", 33f, FontWeight.Bold, c.label, lh = 1.25f) }
        Box(Modifier.padding(horizontal = 16.dp).rise(1, enterKey)) { DsButton("إرسال رسالة للجامعة", onMessage, Modifier.fillMaxWidth(), icon = "mail", big = true) }
        Column(Modifier.rise(2, enterKey)) {
            GroupHeader("حسابي")
            DsList(Modifier.padding(horizontal = 16.dp)) {
                if (loggedIn) {
                    LinkRow("user", Color(0xFF1A5CAA), studentName, "الملف الشخصي وخدمات الطالب", onProfile)
                    DsDivider()
                    DsRow(onClick = onLogout) {
                        RowIconBoxRed()
                        T("تسجيل الخروج", 15f, FontWeight.Medium, c.red, modifier = Modifier.weight(1f))
                    }
                } else {
                    LinkRow("cap", Color(0xFF1A5CAA), "تسجيل الدخول", "بالرقم القومي للطالب", onLogin)
                }
            }
        }
        Column(Modifier.rise(3, enterKey)) {
            GroupHeader("المظهر")
            DsSeg(listOf("تلقائي", "فاتح", "داكن"), themeMode, onThemeMode, Modifier.padding(horizontal = 16.dp))
        }
        Column(Modifier.rise(4, enterKey)) {
            GroupHeader("روابط")
            DsList(Modifier.padding(horizontal = 16.dp)) {
                LinkRow("globe", Color(0xFF008744), "الموقع الإلكتروني الرسمي للجامعة", "www.kfs.edu.eg") { onUrl("https://www.kfs.edu.eg", "الموقع الإلكتروني الرسمي") }
                DsDivider()
                LinkRow("link", Color(0xFF5B3FD6), "مركز تطوير نظم المعلومات والخدمات", "itc.kfs.edu.eg") { onUrl("https://itc.kfs.edu.eg", "مركز تطوير نظم المعلومات") }
            }
        }
        Column(Modifier.rise(5, enterKey)) {
            GroupHeader("تفاصيل الاتصال بالجامعة")
            DsList(Modifier.padding(horizontal = 16.dp)) {
                InfoWithIcon("building", Color(0xFF1A5CAA), "الاسم", "جامعة كفر الشيخ")
                DsDivider()
                InfoWithIcon("pin", Color(0xFFD93036), "العنوان", "مصر، كفر الشيخ")
                DsDivider()
                CopyRow("phone", Color(0xFF008744), "الهاتف", "002-047-3109590")
                DsDivider()
                CopyRow("mail", Color(0xFFE07B00), "البريد", "President@kfs.edu.eg")
                DsDivider()
                CopyRow("globe", Color(0xFF1A5CAA), "الموقع", "kfs.edu.eg")
            }
        }
    }
}

@Composable
private fun RowIconBoxRed() = com.example.ui.ds.RowIconBox("logout", Color(0xFFD93036))

@Composable
private fun InfoWithIcon(icon: String, color: Color, label: String, value: String) {
    val c = Ds.c
    DsRow {
        com.example.ui.ds.RowIconBox(icon, color)
        Column(Modifier.weight(1f)) {
            T(label, 12.5f, color = c.label2, lh = 1.4f)
            T(value, 14.5f, FontWeight.SemiBold, c.label, lh = 1.4f)
        }
    }
}
