package com.kfs.edu.ui.overlays

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AColor
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kfs.edu.data.KfuNetworkClient
import com.kfs.edu.ui.ds.EmptyState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File

/** Opened PDF whose pages are rendered natively (the file is downloaded once and kept in the cache folder). */
private class PdfDoc(val pfd: ParcelFileDescriptor, val renderer: PdfRenderer) {
    val lock = Mutex()
    fun close() { try { renderer.close(); pfd.close() } catch (_: Exception) { } }
}

private fun cachedFile(context: Context, url: String): File =
    File(File(context.filesDir, "doc_cache").apply { mkdirs() }, "${url.hashCode().toUInt()}.bin")

private suspend fun download(context: Context, url: String): File? = withContext(Dispatchers.IO) {
    val f = cachedFile(context, url)
    if (f.length() > 0) return@withContext f
    try {
        val req = Request.Builder().url(url).header("User-Agent", "Mozilla/5.0 (Linux; Android 13)").build()
        KfuNetworkClient.httpClient.newCall(req).execute().use { r ->
            if (!r.isSuccessful) return@use null
            val tmp = File(f.parentFile, f.name + ".part")
            r.body?.byteStream()?.use { input -> tmp.outputStream().use { input.copyTo(it) } }
            if (tmp.length() > 0 && tmp.renameTo(f)) f else null
        }
    } catch (_: Exception) { null }
}

@Composable
fun PdfPreview(url: String, onFailed: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var failed by remember(url) { mutableStateOf(false) }
    val doc by produceState<PdfDoc?>(null, url) {
        val f = download(context, url)
        value = withContext(Dispatchers.IO) {
            try {
                val pfd = ParcelFileDescriptor.open(f ?: return@withContext null, ParcelFileDescriptor.MODE_READ_ONLY)
                PdfDoc(pfd, PdfRenderer(pfd))
            } catch (_: Exception) { null }
        }
        if (value == null) failed = true
    }
    DisposableEffect(doc) { onDispose { doc?.close() } }

    Box(modifier.background(Color(0xFFE9EAEE))) {
        val d = doc
        if (failed) {
            EmptyState("globe", "تعذر عرض الملف", "تحقق من الاتصال ثم أعد المحاولة.", "فتح في المتصفح") { onFailed() }
        } else if (d != null) {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                items((0 until d.renderer.pageCount).toList()) { index ->
                    val bmp by produceState<Bitmap?>(null, d, index) {
                        value = withContext(Dispatchers.IO) {
                            d.lock.withLock {
                                try {
                                    d.renderer.openPage(index).use { page ->
                                        val w = 1200
                                        val h = (w.toFloat() * page.height / page.width).toInt()
                                        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                                        b.eraseColor(AColor.WHITE)
                                        page.render(b, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                        b
                                    }
                                } catch (_: Exception) { null }
                            }
                        }
                    }
                    val b = bmp
                    val ratio = remember(d, index) { d.renderer.let { r -> 0.707f } }
                    Box(Modifier.fillMaxWidth().background(Color.White)) {
                        if (b != null) {
                            Image(b.asImageBitmap(), null, Modifier.fillMaxWidth().aspectRatio(b.width.toFloat() / b.height), contentScale = ContentScale.FillWidth)
                        } else {
                            Box(Modifier.fillMaxWidth().aspectRatio(ratio))
                        }
                    }
                }
            }
        }
    }
}
