package com.example.ui.ds

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val arEg = Locale("ar", "EG")

/** Arabic-Indic digits, like Number.toLocaleString('ar-EG') in the design. */
fun nf(n: Int): String = NumberFormat.getInstance(arEg).format(n)

private val parseIso = SimpleDateFormat("yyyy-MM-dd", Locale.US)
private val fullFmt = SimpleDateFormat("d MMMM yyyy", arEg)
private val monthFmt = SimpleDateFormat("MMMM yyyy", arEg)

/** "2026-05-21" -> Arabic long date. Falls back to the raw text when it cannot be parsed. */
fun fmtDate(iso: String): String = try {
    synchronized(parseIso) { fullFmt.format(parseIso.parse(iso.take(10))!!) }
} catch (_: Exception) { iso }

fun fmtMonth(iso: String): String = try {
    synchronized(parseIso) { monthFmt.format(parseIso.parse(iso.take(10))!!) }
} catch (_: Exception) { iso.take(7) }

fun monthKey(iso: String): String = iso.take(7)

fun greeting(): String {
    val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        h < 5 -> "مساء الخير"
        h < 12 -> "صباح الخير"
        h < 18 -> "نهارك سعيد"
        else -> "مساء الخير"
    }
}

/** Search normalisation from the design: unify alef/yaa/taa-marbuta forms and drop diacritics. */
fun normalizeAr(s: String): String = s
    .replace(Regex("[أإآ]"), "ا").replace('ى', 'ي').replace('ة', 'ه')
    .replace(Regex("[ً-ْ]"), "").lowercase()

data class CollegeUi(
    val facId: Int,
    val title: String,
    val logo: String,
    val urlName: String,
    val type: Int,
    val infoId: Int,
)

fun topicUrl(id: Int) = "https://kfs.edu.eg/news_mob.aspx?topic=$id"
