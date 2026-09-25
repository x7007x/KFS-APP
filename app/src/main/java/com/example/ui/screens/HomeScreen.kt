package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.HarDataCache
import com.example.data.NewsArticle
import com.example.data.RealStudentProfile
import com.example.ui.components.ServiceIcon
import com.example.ui.theme.*

@Immutable data class ServiceCard(val label: String, val icon: String, val id: String, val webTitle: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    newsArticles: List<com.example.data.NewsArticle> = emptyList(),
    loggedInProfile: com.example.data.RealStudentProfile? = null,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onNavigateToTab: (Int) -> Unit,
    onSelectNews: (com.example.data.NewsArticle) -> Unit,
    onShowLogin: (isStaff: Boolean) -> Unit,
    onTriggerService: (id: String, title: String) -> Unit,
    onShowAboutDetail: (title: String) -> Unit,
    onShowStudentProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val onStaffLogin = remember { { onShowLogin(true) } }
    val onStudentLogin = remember { { onShowLogin(false) } }

    val quickLinks = remember {
        listOf(
            Triple("مجلس جامعة\nكفر الشيخ", "group", "مجلس الجامعة"),
            Triple("لماذا جامعة\nكفر الشيخ", "diamond", "لماذا الجامعة"),
            Triple("عن الجامعة", "building", "عن الجامعة"),
            Triple("رؤية ورسالة\nالجامعة", "eye", "رؤية ورسالة")
        )
    }

    val studentServices = remember {
        listOf(
            ServiceCard("نتائج الطلاب", "clipboard_results", "stu_results_full", "نتائج الطلاب"),
            ServiceCard("تسجيل المقررات", "edit_note", "stu_reg_courses", "تسجيل مقررات الساعات المعتمدة"),
            ServiceCard("التنسيق الإلكتروني", "globe", "stu_coordination", "التنسيق الالكتروني"),
            ServiceCard("الكشف الطبي", "medical_services", "stu_medical", "الكشف الطبي"),
            ServiceCard("الدفع الإلكتروني", "wallet", "stu_payment", "منظومة الدفع الالكتروني"),
            ServiceCard("البريد الإلكتروني", "envelope", "stu_mail_full", "البريد الإلكتروني"),
            ServiceCard("ميثاق الطالب", "description", "stu_charter", "ميثاق الطالب"),
            ServiceCard("منصة المقررات", "graduation_cap", "stu_thinqi", "منصة المقررات الالكترونيه"),
            ServiceCard("التربية العسكرية", "military", "stu_military_full", "التربية العسكرية"),
            ServiceCard("المكتبة الرقمية", "book", "stu_digital_lib", "المكتبة الرقمية"),
            ServiceCard("سكن الطلاب", "home", "stu_housing", "سكن الطلاب"),
            ServiceCard("الاستمارة النهائية", "description", "stu_final_form", "الاستماره الخاصه بالفرقه النهائية"),
        )
    }

    val staffServices = remember {
        listOf(
            ServiceCard("دليل أعضاء التدريس", "group", "staff_directory", "دليل أعضاء هيئة التدريس"),
            ServiceCard("البحث عن عضو تدريس", "search", "staff_search", "البحث عن عضو هيئة تدريس"),
            ServiceCard("البريد الإلكتروني", "envelope", "staff_mail", "البريد الإلكتروني"),
            ServiceCard("المكتبة الرقمية", "book", "staff_digital_lib", "المكتبة الرقمية"),
            ServiceCard("دورات تنمية القدرات", "graduation_cap", "staff_courses", "دورات تنمية قدرات أعضاء هيئة التدريس"),
            ServiceCard("منصة المقررات thinqi", "globe", "staff_thinqi", "منصة المقررات الالكترونيه"),
            ServiceCard("اختبارات القدرات", "clipboard_results", "staff_moodle", "اختبارات تنمية قدرات أعضاء هيئة التدريس"),
            ServiceCard("دورات التحول الرقمي", "calendar_today", "staff_ictt", "حجز دورات التحول الرقمى"),
            ServiceCard("الهوية الرقمية RDI", "badge", "staff_rdi", "الهوية الرقمية للباحث"),
            ServiceCard("تسجيل الأبحاث", "edit_note", "staff_papers", "تسجيل الابحاث"),
            ServiceCard("صمم موقعك بنفسك", "globe", "staff_cv", "صمم موقعك بنفسك"),
            ServiceCard("المجلات العلمية", "book", "staff_journals", "المجلات العلمية المعتمدة"),
            ServiceCard("المعامل البحثية", "medical_services", "staff_labs", "المعامل البحثية"),
            ServiceCard("المراكز البحثية", "building", "staff_centers", "المراكز البحثية"),
        )
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize().background(KfuBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (loggedInProfile != null) {
                item(key = "profile") {
                    val faculty = remember(loggedInProfile) { HarDataCache.findFacultyByInfoId(loggedInProfile.asFacultyInfoId.toInt()) }
                    val collegeName = faculty?.factitel ?: ""
                    val firstName = loggedInProfile.fullName.split(" ").firstOrNull() ?: loggedInProfile.fullName
                    val photoUrl = faculty?.let { "https://kfs.edu.eg/${it.facUrl}/photo/${loggedInProfile.photo}" } ?: ""

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .border(1.dp, KfuBorder, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = KfuSurface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = "مرحباً $firstName",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KfuBlue,
                                        textAlign = TextAlign.Start
                                    )
                                    if (collegeName.isNotBlank()) {
                                        Text(
                                            text = collegeName,
                                            fontSize = 12.sp,
                                            color = KfuGreen,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                    Text(
                                        text = loggedInProfile.programName,
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Start
                                    )
                                }
                                if (loggedInProfile.photo.isNotBlank()) {
                                    val fallback = "https://ahmed-negm.pages.dev/images/5e1f1d9d-58df-4e68-b5b6-94e7ec7375db.jpg"
                                    SubcomposeAsyncImage(
                                        model = photoUrl,
                                        contentDescription = "صورة الطالب",
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop,
                                        error = {
                                            AsyncImage(
                                                model = fallback,
                                                contentDescription = "صورة الطالب",
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(RoundedCornerShape(12.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo_university),
                                        contentDescription = "صورة الطالب",
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = onShowStudentProfile,
                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = KfuGreen, contentColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("حسابي", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                FilledTonalButton(
                                    onClick = onLogout,
                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color.Red),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "تسجيل خروج", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تسجيل خروج", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                item(key = "login_buttons") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = onStudentLogin,
                            colors = ButtonDefaults.buttonColors(containerColor = KfuBlue),
                            modifier = Modifier
                                .testTag("student_login_button"),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                ServiceIcon(name = "graduation_cap", color = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "تسجيل الدخول", color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            item(key = "banner") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, KfuBorder, RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = "https://www.arabcont.com/Images/ProjectImage/ConferenceHall_KafrElSheikhUniversity-01.jpg",
                        contentDescription = "جامعة كفر الشيخ",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            item(key = "news_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "أهم الأخبار", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = KfuBlue, textAlign = TextAlign.Right)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToTab(1) }.testTag("view_all_news_link")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = KfuBlue, modifier = Modifier.size(16.dp))
                        Text(text = "عرض الكل", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = KfuBlue)
                    }
                }
            }

            if (newsArticles.isEmpty()) {
                item(key = "news_empty") {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(160.dp).padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "لا توجد أخبار متاحة حالياً", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            } else {
                item(key = "news_cards") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        newsArticles.take(8).forEach { article ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = KfuSurface),
                                modifier = Modifier
                                    .width(260.dp)
                                    .clickable { onSelectNews(article) }
                                    .border(1.dp, KfuBorder, RoundedCornerShape(12.dp))
                                    .testTag("news_card_${article.id}"),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column {
                                    if (article.imageUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = article.imageUrl,
                                            contentDescription = article.title,
                                            modifier = Modifier.fillMaxWidth().height(120.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = article.imageRes),
                                            contentDescription = article.title,
                                            modifier = Modifier.fillMaxWidth().height(120.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = article.date, fontSize = 11.sp, color = Color.Gray,
                                            fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = article.title, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                            color = KfuTextPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis,
                                            lineHeight = 18.sp, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item(key = "quick_links_header") {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    Text(text = "روابط سريعة", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = KfuBlue,
                        modifier = Modifier.padding(horizontal = 12.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            item(key = "quick_links") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    quickLinks.forEach { (label, iconName, id) ->
                        Card(
                            modifier = Modifier
                                .weight(1f).height(100.dp)
                                .clickable { onShowAboutDetail(id) }
                                .border(1.dp, KfuBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = KfuSurface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(6.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ServiceIcon(name = iconName, color = KfuBlue, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                    color = KfuTextPrimary, textAlign = TextAlign.Center, lineHeight = 13.sp)
                            }
                        }
                    }
                }
            }

            item(key = "student_services_header") {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    Text(text = "خدمات الطلاب", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = KfuBlue,
                        modifier = Modifier.padding(horizontal = 12.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item(key = "student_services") {
                ServiceGrid(
                    services = studentServices,
                    onTriggerService = onTriggerService
                )
            }

            item(key = "staff_services_header") {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    Text(text = "خدمات أعضاء هيئة التدريس", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = KfuBlue,
                        modifier = Modifier.padding(horizontal = 12.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item(key = "staff_services") {
                ServiceGrid(
                    services = staffServices,
                    onTriggerService = onTriggerService
                )
            }
        }
    }
}

@Composable
fun ServiceGrid(
    services: List<ServiceCard>,
    onTriggerService: (id: String, title: String) -> Unit
) {
    val rows = services.chunked(4)
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { card ->
                    Card(
                        modifier = Modifier
                            .weight(1f).height(85.dp)
                            .clickable { onTriggerService(card.id, card.webTitle) }
                            .border(1.dp, KfuBorder, RoundedCornerShape(10.dp))
                            .testTag("service_card_${card.id}"),
                        colors = CardDefaults.cardColors(containerColor = KfuSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ServiceIcon(name = card.icon, color = KfuBlue, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = card.label, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                color = KfuTextPrimary, textAlign = TextAlign.Center, lineHeight = 13.sp)
                        }
                    }
                }
                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
