package com.kfs.edu

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.kfs.edu.data.KfuNetworkClient
import java.io.File

/**
 * App-wide image cache: every news / college / logo image is stored on disk once and then read from there
 * (cache headers from the university servers are ignored, they ask to re-download every time).
 */
class KfuApp : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .okHttpClient { KfuNetworkClient.httpClient }
            .memoryCache { MemoryCache.Builder(this).maxSizePercent(0.25).build() }
            .diskCache { DiskCache.Builder().directory(File(filesDir, "image_cache")).maxSizeBytes(300L * 1024 * 1024).build() }
            .respectCacheHeaders(false)
            .crossfade(false)
            .build()
}
