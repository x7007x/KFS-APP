package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object CacheManager {
    private const val NEWS_CACHE = "news_cache.json"
    private const val COLLEGES_CACHE = "colleges_cache.json"

    private fun getCacheDir(context: Context): File {
        val dir = File(context.cacheDir, "kfu_data")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun saveNewsToCache(context: Context, news: List<RealNewsItem>) {
        try {
            val jsonArray = JSONArray()
            news.forEach { item ->
                jsonArray.put(JSONObject().apply {
                    put("topicId", item.topicId)
                    put("title", item.title)
                    put("date", item.date)
                    put("picture", item.picture)
                    put("imageUrl", item.imageUrl)
                })
            }
            File(getCacheDir(context), NEWS_CACHE).writeText(jsonArray.toString())
        } catch (_: Exception) { }
    }

    fun loadNewsFromCache(context: Context): List<RealNewsItem> {
        try {
            val file = File(getCacheDir(context), NEWS_CACHE)
            if (!file.exists()) return emptyList()
            val jsonArray = JSONArray(file.readText())
            val list = mutableListOf<RealNewsItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(RealNewsItem(
                    topicId = obj.optInt("topicId"),
                    title = obj.optString("title"),
                    date = obj.optString("date"),
                    picture = obj.optString("picture"),
                    imageUrl = obj.optString("imageUrl")
                ))
            }
            return list
        } catch (_: Exception) {
            return emptyList()
        }
    }

    fun saveCollegesToCache(context: Context, colleges: List<RealCollegeItem>, type: String) {
        try {
            val file = File(getCacheDir(context), "colleges_${type}.json")
            val jsonArray = JSONArray()
            colleges.forEach { item ->
                jsonArray.put(JSONObject().apply {
                    put("id", item.id)
                    put("facId", item.facId)
                    put("urlName", item.urlName)
                    put("logo", item.logo)
                    put("title", item.title)
                    put("typeId", item.typeId)
                    put("asFacultyInfoId", item.asFacultyInfoId)
                })
            }
            file.writeText(jsonArray.toString())
        } catch (_: Exception) { }
    }

    fun loadCollegesFromCache(context: Context, type: String): List<RealCollegeItem> {
        try {
            val file = File(getCacheDir(context), "colleges_${type}.json")
            if (!file.exists()) return emptyList()
            val jsonArray = JSONArray(file.readText())
            val list = mutableListOf<RealCollegeItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(RealCollegeItem(
                    id = obj.optInt("id"),
                    facId = obj.optInt("facId"),
                    urlName = obj.optString("urlName"),
                    logo = obj.optString("logo"),
                    title = obj.optString("title"),
                    typeId = obj.optInt("typeId"),
                    asFacultyInfoId = obj.optInt("asFacultyInfoId")
                ))
            }
            return list
        } catch (_: Exception) {
            return emptyList()
        }
    }
}
