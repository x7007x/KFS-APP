package com.kfs.edu.data

import com.kfs.edu.R
import androidx.compose.runtime.Immutable

@Immutable
data class NewsArticle(
    val id: String,
    val title: String,
    val date: String,
    val imageRes: Int = R.drawable.img_kfu_banner,
    val imageUrl: String = "",
    val summary: String,
    val content: String,
    val isReal: Boolean = false,
    val topicId: Int = 0
)

private const val UNIVERSITY_PHOTO_BASE = "https://kfs.edu.eg/university/photo/"

fun HarDataCache.HarNewsItem.toArticle(): NewsArticle {
    val imgRes = R.drawable.img_kfu_banner
    val imgUrl = UNIVERSITY_PHOTO_BASE + picture
    return NewsArticle(
        id = "real_$topicId",
        title = titleArabic,
        date = date.ifBlank { "" },
        imageRes = imgRes,
        imageUrl = imgUrl,
        summary = "اضغط لقراءة التفاصيل الكاملة",
        content = titleArabic,
        isReal = true,
        topicId = topicId
    )
}

fun com.kfs.edu.data.RealNewsItem.toNewsArticle(): NewsArticle {
    val imgRes = R.drawable.img_kfu_banner
    return NewsArticle(
        id = "real_$topicId",
        title = title,
        date = date.ifBlank { "" },
        imageRes = imgRes,
        imageUrl = imageUrl,
        summary = "اضغط لقراءة التفاصيل الكاملة",
        content = title,
        isReal = true,
        topicId = topicId
    )
}
