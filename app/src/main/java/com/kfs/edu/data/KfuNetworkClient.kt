package com.kfs.edu.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.regex.Pattern
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import androidx.compose.runtime.Immutable

@Immutable
data class RealNewsItem(
    val topicId: Int,
    val title: String,
    val date: String,
    val picture: String,
    val imageUrl: String = ""
)

@Immutable
data class RealCollegeItem(
    val id: Int,
    val facId: Int,
    val urlName: String,
    val logo: String,
    val title: String,
    val typeId: Int,
    val asFacultyInfoId: Int
)

@Immutable
data class RealCollegeTopic(
    val facId: Int,
    val topicId: Int,
    val title: String,
    val pic: String,
    val idi: Int,
    val facName: String,
    val id: Int
)

@Immutable
data class RealStudentProfile(
    val nationalNumber: String,
    val fullName: String,
    val email: String,
    val notes: String,
    val studentCode: String,
    val passwordItec: String,
    val phaseParent: String,
    val gender: String,
    val caseDescr: String,
    val religion: String,
    val methodDescr: String,
    val natureDescr: String,
    val enrollStatus: String,
    val programName: String,
    val asFacultyInfoId: Double,
    val photo: String
)

object KfuNetworkClient {
    private const val TAG = "KfuNetworkClient"

    private val client: OkHttpClient by lazy {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    /**
     * Student photo. The real portal request is disabled (commented out below); a random portrait
     * (gender-matched, picked at random) is downloaded instead.
     */
    suspend fun fetchStudentPhoto(nationalNumber: String, female: Boolean = false): ByteArray? = withContext(Dispatchers.IO) {
        // Real request (disabled):
        // val url = "https://portal.kfs.edu.eg/KFSUnStudDataAPI/sphoto/$nationalNumber.jpg"
        // try {
        //     val request = Request.Builder().url(url)
        //         .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Mobile Safari/537.36")
        //         .header("Accept", "image/jpeg,image/*;q=0.9,*/*;q=0.5").build()
        //     client.newCall(request).execute().use { r ->
        //         if (!r.isSuccessful) { Log.w(TAG, "photo HTTP ${r.code}"); return@use null }
        //         val bytes = r.body?.bytes()
        //         if (bytes == null || bytes.isEmpty()) { Log.w(TAG, "photo empty body"); null } else bytes
        //     }
        // } catch (e: Exception) { Log.w(TAG, "photo failed: $e"); null }
        val url = "https://randomuser.me/api/portraits/${if (female) "women" else "men"}/${java.util.Random().nextInt(90)}.jpg"
        try {
            val request = Request.Builder().url(url).header("User-Agent", "Mozilla/5.0 (Linux; Android 13)").build()
            client.newCall(request).execute().use { r ->
                if (!r.isSuccessful) { Log.w(TAG, "photo HTTP ${r.code}"); return@use null }
                r.body?.bytes()?.takeIf { it.isNotEmpty() }
            }
        } catch (e: Exception) {
            Log.w(TAG, "photo failed: $e"); null
        }
    }

    private const val BASE_URL = "https://kfs.edu.eg"

    private fun getPhotoBase(facId: Int): String {
        if (facId == 1) return "$BASE_URL/university/photo/"
        val faculty = HarDataCache.findFacultyByFacId(facId)
        val folder = faculty?.facUrl ?: "university"
        return "$BASE_URL/$folder/photo/"
    }

    suspend fun fetchLatestNews(): List<RealNewsItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<RealNewsItem>()
        try {
            val formBody = FormBody.Builder()
                .add("facid", "1")
                .build()
            val request = Request.Builder()
                .url("$BASE_URL/kfsapi/news.php")
                .post(formBody)
                .addHeader("User-Agent", "Dart/3.9 (dart:io)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val jsonArray = JSONArray(bodyString)
                    val photoBase = getPhotoBase(1)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val topicId = obj.optInt("topic_id")
                        val title = obj.optString("title_arabic")
                        val picture = obj.optString("picture")
                        val dateObj = obj.optJSONObject("topic_date")
                        var dateStr = ""
                        if (dateObj != null) {
                            val fullDate = dateObj.optString("date")
                            if (fullDate.contains(" ")) {
                                dateStr = fullDate.split(" ")[0]
                            } else {
                                dateStr = fullDate
                            }
                        }
                        list.add(RealNewsItem(topicId, title, dateStr, picture, photoBase + picture))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchLatestNews error: ${e.message}")
        }
        return@withContext list
    }

    suspend fun fetchColleges(factype: String): List<RealCollegeItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<RealCollegeItem>()
        try {
            val formBody = FormBody.Builder()
                .add("factype", factype)
                .build()

            val request = Request.Builder()
                .url("$BASE_URL/kfsapi/mobdb/data.php")
                .post(formBody)
                .addHeader("User-Agent", "Dart/3.9 (dart:io)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val jsonArray = JSONArray(bodyString)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        list.add(RealCollegeItem(
                            obj.optInt("id"), obj.optInt("facid"), obj.optString("facurl"),
                            obj.optString("faclogo"), obj.optString("factitel"),
                            obj.optInt("factype"), obj.optInt("AS_FACULTY_INFO_ID")
                        ))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchColleges error: ${e.message}")
        }
        return@withContext list
    }

    suspend fun fetchCollegeNews(facId: String): List<RealNewsItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<RealNewsItem>()
        try {
            val formBody = FormBody.Builder()
                .add("facid", facId)
                .build()

            val request = Request.Builder()
                .url("$BASE_URL/kfsapi/news.php")
                .post(formBody)
                .addHeader("User-Agent", "Dart/3.9 (dart:io)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val jsonArray = JSONArray(bodyString)
                    val facIdInt = facId.toIntOrNull() ?: 1
                    val photoBase = getPhotoBase(facIdInt)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val topicId = obj.optInt("topic_id")
                        val title = obj.optString("title_arabic")
                        val picture = obj.optString("picture")
                        val dateObj = obj.optJSONObject("topic_date")
                        var dateStr = ""
                        if (dateObj != null) {
                            val fullDate = dateObj.optString("date")
                            if (fullDate.contains(" ")) dateStr = fullDate.split(" ")[0]
                            else dateStr = fullDate
                        }
                        list.add(RealNewsItem(topicId, title, dateStr, picture, photoBase + picture))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchCollegeNews error: ${e.message}")
        }
        return@withContext list
    }

    suspend fun fetchCollegeTopics(facId: String): List<RealCollegeTopic> = withContext(Dispatchers.IO) {
        val list = mutableListOf<RealCollegeTopic>()
        try {
            val formBody = FormBody.Builder()
                .add("facid", facId)
                .build()

            val request = Request.Builder()
                .url("$BASE_URL/kfsapi/mobdb/Factopic.php")
                .post(formBody)
                .addHeader("User-Agent", "Dart/3.9 (dart:io)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val jsonArray = JSONArray(bodyString)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        list.add(RealCollegeTopic(
                            obj.optInt("fac_id"), obj.optInt("topic_id"),
                            obj.optString("title_arabic"), obj.optString("topic_pic"),
                            obj.optInt("idi"), obj.optString("FacName"), obj.optInt("id")
                        ))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchCollegeTopics error: ${e.message}")
        }
        return@withContext list
    }

    /** Ready-made login response (same keys and values the server returns), used while the live request below is disabled. */
    /** Builds a login response with randomly generated values (same keys/format as the real API reply). */
    private fun randomLoginJson(nationalId: String): String {
        val r = java.util.Random()
        fun <T> pick(l: List<T>): T = l[r.nextInt(l.size)]
        fun digits(n: Int) = buildString { repeat(n) { append(r.nextInt(10)) } }
        val female = r.nextBoolean()
        val first = pick(if (female) listOf("سلمى", "مريم", "نور", "هبة", "ياسمين", "دعاء", "منة", "رنا", "إسراء", "آية") else listOf("أحمد", "محمد", "محمود", "عمر", "كريم", "يوسف", "مصطفى", "إبراهيم", "علي", "خالد"))
        val names = listOf("طه", "حسن", "السيد", "عبدالله", "إبراهيم", "فتحي", "جمال", "رضا", "سعيد", "نبيل")
        val last = listOf("راشد", "النجار", "الشافعي", "عيسى", "مصطفى", "سليمان", "البحيري", "عوض", "هلال", "منصور")
        val en = listOf("sara", "mona", "omar", "ali", "nour", "hana", "adam", "yara", "zein", "laila")
        val programs = listOf("الجيولوجيا", "الكيمياء", "الفيزياء", "علوم الحاسب", "الرياضيات", "علم الحيوان", "النبات", "الإحصاء")
        val levels = listOf("المستوى الأول", "المستوى الثانى", "المستوى الثالث", "المستوى الرابع")
        val faculty = 100 + r.nextInt(300)
        val o = JSONObject()
        o.put("NATIONAL_NUMBER", nationalId)
        o.put("FULL_NAME_AR", "$first ${pick(names)} ${pick(names)} ${pick(last)}")
        o.put("METHOD_DESCR", "${pick(en)}_${digits(4)}@sci.kfs.edu.eg")
        o.put("METHOD_NOTES", "${pick(listOf("K", "M", "Z", "T", "R"))}@${digits(12)}${pick(listOf("oc", "xp", "qa", "lm"))}")
        o.put("STUD_CODE", "16091" + digits(11))
        o.put("PASSWORD_ITEC", digits(6))
        o.put("phase_parent", pick(levels))
        o.put("GENDER_DESCR_AR", if (female) "أنثى" else "ذكر")
        o.put("CASE_DESCR_AR", pick(listOf("مستجد", "باق للإعادة", "منقول")))
        o.put("RELIGION_DESCR_AR", pick(listOf("مسلم", "مسلم", "مسيحي")))
        o.put("METHOD_DESCR_AR", pick(listOf("ساعات معتمدة", "نظام الفصلين")))
        o.put("NATURE_DESCR_AR", pick(listOf("انتظام", "انتساب")))
        o.put("ED_CODE_STUD_ENROLL_AR", pick(listOf("مقيد", "مقيد", "موقوف قيد")))
        o.put("Program_Name", pick(programs))
        o.put("AS_FACULTY_INFO_ID", faculty.toDouble())
        o.put("STUD_PHOTO", "~/userdocuments//2//$faculty//Staff//0/${digits(21)}.jpg")
        o.put("PAYMENT_STATUS", "000")
        return o.toString()
    }

    private fun profileFrom(bodyString: String): RealStudentProfile? = try {
        val obj = JSONObject(bodyString)
        if (obj.has("FULL_NAME_AR") && !obj.isNull("FULL_NAME_AR")) {
            RealStudentProfile(
                nationalNumber = obj.optString("NATIONAL_NUMBER"),
                fullName = obj.optString("FULL_NAME_AR"),
                email = obj.optString("METHOD_DESCR"),
                notes = obj.optString("METHOD_NOTES"),
                studentCode = obj.optString("STUD_CODE"),
                passwordItec = obj.optString("PASSWORD_ITEC"),
                phaseParent = obj.optString("phase_parent"),
                gender = obj.optString("GENDER_DESCR_AR"),
                caseDescr = obj.optString("CASE_DESCR_AR"),
                religion = obj.optString("RELIGION_DESCR_AR"),
                methodDescr = obj.optString("METHOD_DESCR_AR"),
                natureDescr = obj.optString("NATURE_DESCR_AR"),
                enrollStatus = obj.optString("ED_CODE_STUD_ENROLL_AR"),
                programName = obj.optString("Program_Name"),
                asFacultyInfoId = obj.optDouble("AS_FACULTY_INFO_ID"),
                photo = obj.optString("STUD_PHOTO"),
            )
        } else null
    } catch (_: Exception) { null }

    suspend fun loginWithNationalId(nationalId: String): RealStudentProfile? = withContext(Dispatchers.IO) {
        // Live request (disabled):
    //    try {
    //        val mediaType = "application/json; charset=utf-8".toMediaType()
    //        val jsonBody = "\"qkdd!VhSUSfUIhqN\""
    //        val requestBody = jsonBody.toRequestBody(mediaType)
    //
    //        val request = Request.Builder()
    //            .url("https://mis.kfs.edu.eg/sh_kfs/api/ShehabStud?nid=$nationalId")
    //            .post(requestBody)
    //            .addHeader("User-Agent", "Dart/3.9 (dart:io)")
    //            .addHeader("Accept-Encoding", "gzip")
    //            .addHeader("Content-Type", "application/json; charset=utf-8")
    //            .build()
    //
    //        client.newCall(request).execute().use { response ->
    //            val bodyString = response.body?.string() ?: ""
    //            Log.d(TAG, "login code=${response.code} body=${bodyString.take(200)}")
    //            if (response.code == 200 && bodyString.isNotBlank()) {
    //                try {
    //                    val obj = JSONObject(bodyString)
    //                    if (obj.has("FULL_NAME_AR") && !obj.isNull("FULL_NAME_AR")) {
    //                        return@withContext RealStudentProfile(
    //                            nationalNumber = obj.optString("NATIONAL_NUMBER"),
    //                            fullName = obj.optString("FULL_NAME_AR"),
    //                            email = obj.optString("METHOD_DESCR"),
    //                            notes = obj.optString("METHOD_NOTES"),
    //                            studentCode = obj.optString("STUD_CODE"),
    //                            passwordItec = obj.optString("PASSWORD_ITEC"),
    //                            phaseParent = obj.optString("phase_parent"),
    //                            gender = obj.optString("GENDER_DESCR_AR"),
    //                            caseDescr = obj.optString("CASE_DESCR_AR"),
    //                            religion = obj.optString("RELIGION_DESCR_AR"),
    //                            methodDescr = obj.optString("METHOD_DESCR_AR"),
    //                            natureDescr = obj.optString("NATURE_DESCR_AR"),
    //                            enrollStatus = obj.optString("ED_CODE_STUD_ENROLL_AR"),
    //                            programName = obj.optString("Program_Name"),
    //                            asFacultyInfoId = obj.optDouble("AS_FACULTY_INFO_ID"),
    //                            photo = obj.optString("STUD_PHOTO")
    //                        )
    //                    }
    //                } catch (_: Exception) { }
    //            }
    //            Log.w(TAG, "login failed: HTTP ${response.code}")
    //        }
    //    } catch (e: javax.net.ssl.SSLHandshakeException) {
    //        Log.e(TAG, "login SSL error: ${e.message}")
    //        throw e
    //    } catch (e: java.net.SocketTimeoutException) {
    //        Log.e(TAG, "login timeout: ${e.message}")
    //        throw e
    //    } catch (e: Exception) {
    //        Log.e(TAG, "login error: ${e.message}")
    //        throw e
    //    }
    //    return@withContext null
    //}

        profileFrom(randomLoginJson(nationalId))
    }

    suspend fun sendUniversityMessage(senderName: String, senderEmail: String, subject: String, message: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val formBody = FormBody.Builder()
                    .add("sendern", senderName)
                    .add("senderm", senderEmail)
                    .add("subject", subject)
                    .add("message", message)
                    .build()
                val request = Request.Builder()
                    .url("https://kfs.edu.eg/email/Default/SendEmail")
                    .post(formBody)
                    .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36")
                    .addHeader("Origin", "https://kfs.edu.eg")
                    .addHeader("Referer", "https://kfs.edu.eg/email/Default/Index")
                    .build()
                val response = client.newCall(request).execute()
                response.isSuccessful
            } catch (_: Exception) {
                false
            }
        }
    }

    suspend fun fetchSiteIcon(siteUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            val base = siteUrl.substringBefore("/news_mob").let { if (it.endsWith("/")) it else "$it/" }
            val root = runCatching {
                val u = java.net.URL(siteUrl)
                "${u.protocol}://${u.host}/"
            }.getOrDefault(base)

            val request = Request.Builder()
                .url(siteUrl)
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use null
                val html = response.body?.string() ?: return@use null
                val linkPat = Pattern.compile("""<link[^>]+rel\s*=\s*["']?(?:shortcut\s+icon|icon|apple-touch-icon)["']?[^>]*>""", Pattern.CASE_INSENSITIVE)
                val hrefPat = Pattern.compile("""href\s*=\s*["']([^"']+)["']""", Pattern.CASE_INSENSITIVE)
                val linkMatcher = linkPat.matcher(html)
                while (linkMatcher.find()) {
                    val tag = linkMatcher.group()
                    val hrefMatcher = hrefPat.matcher(tag)
                    if (!hrefMatcher.find()) continue
                    val href = hrefMatcher.group(1).trim()
                    if (href.isBlank() || href.startsWith("data:")) continue
                    val abs = when {
                        href.startsWith("http://") || href.startsWith("https://") -> href
                        href.startsWith("//") -> "https:$href"
                        href.startsWith("/") -> root.removeSuffix("/") + href
                        else -> root + href
                    }
                    return@use abs
                }
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
