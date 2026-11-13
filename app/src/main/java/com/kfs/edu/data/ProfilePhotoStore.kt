package com.kfs.edu.data

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Keeps the signed-in student's photo on the device: it is downloaded once per login and every screen reads the file.
 * The file is removed on logout.
 */
object ProfilePhotoStore {
    private const val TAG = "ProfilePhotoStore"
    private val lock = Mutex()

    private fun file(context: Context, nationalNumber: String): File =
        File(File(context.filesDir, "student_photo").apply { mkdirs() }, "${nationalNumber.filter { it.isDigit() }}.jpg")

    /** Returns the stored photo, downloading it first when this login has none yet. Null when it cannot be fetched. */
    suspend fun ensure(context: Context, nationalNumber: String): File? = withContext(Dispatchers.IO) {
        if (nationalNumber.isBlank()) return@withContext null
        lock.withLock {
            val target = file(context, nationalNumber)
            if (target.length() > 0) return@withLock target
            val raw = KfuNetworkClient.fetchStudentPhoto(nationalNumber) ?: return@withLock null
            val bytes = decodable(raw) ?: run { Log.w(TAG, "photo response is not an image (${raw.size} bytes)"); return@withLock null }
            target.writeBytes(bytes)
            target
        }
    }

    /** Accepts the image as is, or as base64 text (optionally a data: URI), which some endpoints return. */
    private fun decodable(raw: ByteArray): ByteArray? {
        fun isImage(b: ByteArray): Boolean {
            val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(b, 0, b.size, o)
            return o.outWidth > 0 && o.outHeight > 0
        }
        if (isImage(raw)) return raw
        return try {
            val text = String(raw, Charsets.UTF_8).trim().trim('"').substringAfter("base64,")
            val decoded = Base64.decode(text, Base64.DEFAULT)
            if (isImage(decoded)) decoded else null
        } catch (_: Exception) { null }
    }

    fun delete(context: Context) {
        try { File(context.filesDir, "student_photo").deleteRecursively() } catch (_: Exception) { }
    }
}
