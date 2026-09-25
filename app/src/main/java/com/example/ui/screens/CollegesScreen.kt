package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseInOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private data class CollegeGroupData(
    val title: String,
    val items: List<CollegeItemData>,
    val textColor: Color,
    val borderColor: Color,
    val bgColor: Color
)

private data class CollegeItemData(
    val name: String,
    val facId: String,
    val logo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegesScreen(
    onShowWebView: (title: String, url: String) -> Unit = { _, _ -> },
    onRefresh: () -> Unit = {},
    isRefreshing: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var medicalColleges by remember { mutableStateOf(DataFetchCache.medicalColleges) }
    var scientificColleges by remember { mutableStateOf(DataFetchCache.scientificColleges) }
    var humanitiesColleges by remember { mutableStateOf(DataFetchCache.humanitiesColleges) }
    var isLoading by remember { mutableStateOf(!DataFetchCache.collegesLoaded) }

    fun refreshData() {
        isLoading = true
        coroutineScope.launch {
            try {
                val medDef = async { KfuNetworkClient.fetchColleges("2") }
                val sciDef = async { KfuNetworkClient.fetchColleges("1") }
                val humDef = async { KfuNetworkClient.fetchColleges("3") }
                val med = medDef.await()
                val sci = sciDef.await()
                val hum = humDef.await()
                DataFetchCache.medicalColleges = med
                DataFetchCache.scientificColleges = sci
                DataFetchCache.humanitiesColleges = hum
                DataFetchCache.collegesLoaded = true
                medicalColleges = med
                scientificColleges = sci
                humanitiesColleges = hum
                CacheManager.saveCollegesToCache(context, med, "2")
                CacheManager.saveCollegesToCache(context, sci, "1")
                CacheManager.saveCollegesToCache(context, hum, "3")
            } catch (_: Exception) {
                if (!DataFetchCache.collegesLoaded) {
                    val medDef = async { CacheManager.loadCollegesFromCache(context, "2") }
                    val sciDef = async { CacheManager.loadCollegesFromCache(context, "1") }
                    val humDef = async { CacheManager.loadCollegesFromCache(context, "3") }
                    val med = medDef.await()
                    val sci = sciDef.await()
                    val hum = humDef.await()
                    if (med.isNotEmpty()) {
                        DataFetchCache.medicalColleges = med
                        DataFetchCache.scientificColleges = sci
                        DataFetchCache.humanitiesColleges = hum
                        DataFetchCache.collegesLoaded = true
                        medicalColleges = med
                        scientificColleges = sci
                        humanitiesColleges = hum
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!DataFetchCache.collegesLoaded) {
            refreshData()
        } else {
            isLoading = false
        }
    }

    val groups = remember(medicalColleges, scientificColleges, humanitiesColleges) {
        buildList {
            if (medicalColleges.isNotEmpty()) {
                add(CollegeGroupData(
                    "الكليات الطبية والرعاية الصحية",
                    medicalColleges.map { CollegeItemData(it.title, it.facId.toString(), it.logo) },
                    CollegesMedicalText, CollegesMedicalBorder, CollegesMedicalBg
                ))
            }
            if (scientificColleges.isNotEmpty()) {
                add(CollegeGroupData(
                    "الكليات العملية والهندسية",
                    scientificColleges.map { CollegeItemData(it.title, it.facId.toString(), it.logo) },
                    CollegesMedicalText, CollegesMedicalBorder, CollegesMedicalBg
                ))
            }
            if (humanitiesColleges.isNotEmpty()) {
                add(CollegeGroupData(
                    "الكليات الإنسانية والتربوية والإدارية",
                    humanitiesColleges.map { CollegeItemData(it.title, it.facId.toString(), it.logo) },
                    CollegesMedicalText, CollegesMedicalBorder, CollegesMedicalBg
                ))
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize().background(KfuBackground)
    ) {
        if (isLoading && groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KfuBlue)
            }
        } else if (groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(text = "تعذر تحميل بيانات الكليات. اضغط على زر التحديث.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item(key = "header") {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                groups.forEach { group ->
                    item(key = "section_${group.title}") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = group.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = group.textColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    items(group.items, key = { "${group.title}_${it.facId}" }) { item ->
                        CollegeExpandableCard(
                            name = item.name,
                            facId = item.facId,
                            logoUrl = if (item.logo.isNotBlank()) "https://kfs.edu.eg/kfsapi/flogo/${item.logo}" else "",
                            textColor = group.textColor,
                            bgColor = group.bgColor,
                            onShowWebView = onShowWebView
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CollegeExpandableCard(
    name: String,
    facId: String,
    logoUrl: String,
    textColor: Color,
    bgColor: Color,
    onShowWebView: (title: String, url: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, textColor, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
                .background(if (expanded) bgColor.copy(alpha = 0.5f) else Color.Transparent)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.weight(1f)
                ) {
                    if (logoUrl.isNotBlank()) {
                        AsyncImage(
                            model = logoUrl,
                            contentDescription = name,
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (name.startsWith("كلية")) name else "كلية $name",
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KfuTextPrimary, textAlign = TextAlign.Start
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null, tint = textColor
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(500, easing = EaseInOut)) +
                    fadeIn(tween(500)),
                exit = shrinkVertically(animationSpec = tween(500, easing = EaseInOut)) +
                    fadeOut(tween(500))
            ) {
                ExpandedCollegeDetails(
                    facId = facId, collegeName = name,
                    textColor = textColor, bgColor = bgColor, onShowWebView = onShowWebView
                )
            }
        }
    }
}

@Composable
private fun ExpandedCollegeDetails(
    facId: String, collegeName: String, textColor: Color, bgColor: Color,
    onShowWebView: (title: String, url: String) -> Unit
) {
    val cached = remember(facId) {
        mutableStateOf<Pair<List<RealCollegeTopic>, List<RealNewsItem>>?>(null)
    }
    var isLoadingDetails by remember { mutableStateOf(cached.value == null) }

    LaunchedEffect(facId) {
        if (cached.value != null) return@LaunchedEffect
        try {
            val topics = KfuNetworkClient.fetchCollegeTopics(facId)
            val news = KfuNetworkClient.fetchCollegeNews(facId)
            cached.value = topics to news
        } catch (_: Exception) { } finally {
            isLoadingDetails = false
        }
    }

    val topics = cached.value?.first ?: emptyList()
    val newsList = cached.value?.second ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor.copy(alpha = 0.08f))
            .border(1.dp, bgColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.End
    ) {
        if (isLoadingDetails) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = textColor, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
            }
        } else {
            Text(
                text = "جداول ولوائح المقررات والامتحانات",
                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            if (topics.isEmpty()) {
                Text(text = "لا توجد جداول رسمية منشورة حالياً لهذه الكلية.",
                    fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp))
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    topics.take(3).forEach { topic ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(textColor)
                                .clickable {
                                    onShowWebView("${topic.title} - $collegeName",
                                        "https://kfs.edu.eg/news_mob.aspx?topic=${topic.topicId}")
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = topic.title, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                color = Color.White, textAlign = TextAlign.Right)
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null,
                                tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            HorizontalDivider(color = bgColor.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "أحدث الأخبار الأكاديمية والميدانية",
                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            if (newsList.isEmpty()) {
                Text(text = "لا توجد أخبار حديثة منشورة حالياً بالملف الإخباري للكلية.",
                    fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    newsList.take(3).forEach { newsItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                    onShowWebView(newsItem.title,
                                        "https://kfs.edu.eg/news_mob.aspx?topic=${newsItem.topicId}")
                                }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = newsItem.title, fontSize = 12.sp, color = KfuTextPrimary,
                                textAlign = TextAlign.Right, modifier = Modifier.weight(1f), lineHeight = 15.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(textColor))
                        }
                        HorizontalDivider(color = KfuBackground, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}
