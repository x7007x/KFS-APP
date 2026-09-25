package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.http.SslError
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.KfuBlue
import com.example.ui.theme.KfuBlueDark

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    title: String,
    onClose: () -> Unit
) {
    var canGoBack by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var pageTitle by remember { mutableStateOf("—") }
    val webView = remember { mutableStateOf<WebView?>(null) }
    val currentUrl = remember { mutableStateOf(url) }

    val client = remember {
        object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                hasError = false
                isLoading = true
                pageTitle = "—"
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                isLoading = false
                canGoBack = view?.canGoBack() ?: false
                val t = view?.title
                if (!t.isNullOrBlank() && !t.startsWith("http") && t != "—") {
                    pageTitle = t
                } else {
                    view?.evaluateJavascript(
                        """
                        (function() {
                            var t = document.title;
                            if (t && t.trim() && !t.startsWith('http')) return t.trim();
                            var h1 = document.querySelector('h1');
                            if (h1 && h1.innerText.trim()) return h1.innerText.trim();
                            var h2 = document.querySelector('h2');
                            if (h2 && h2.innerText.trim()) return h2.innerText.trim();
                            var meta = document.querySelector('meta[property="og:title"]');
                            if (meta && meta.getAttribute('content') && meta.getAttribute('content').trim())
                                return meta.getAttribute('content').trim();
                            var a = document.querySelector('a.title, .title, [class*="title"]');
                            if (a && a.innerText.trim()) return a.innerText.trim();
                            var p = document.querySelector('p');
                            if (p && p.innerText.trim()) return p.innerText.trim().substring(0, 60);
                            return '';
                        })();
                        """.trimIndent()
                    ) { result ->
                        val cleaned = result?.trim('"')?.trim()
                        pageTitle = if (!cleaned.isNullOrBlank()) cleaned else "—"
                    }
                }
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                canGoBack = view?.canGoBack() ?: false
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: android.webkit.WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    hasError = true
                    isLoading = false
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.url?.toString()?.let { currentUrl.value = it }
                return false
            }
        }
    }

    LaunchedEffect(url) {
        currentUrl.value = url
        webView.value?.loadUrl(url)
    }

    BackHandler {
        if (canGoBack) webView.value?.goBack() else onClose()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = KfuBlueDark,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White
            ),
            title = {
                Text(
                    text = pageTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { if (canGoBack) webView.value?.goBack() else onClose() }) {
                    Icon(
                        imageVector = if (canGoBack) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Close,
                        contentDescription = if (canGoBack) "رجوع" else "إغلاق"
                    )
                }
            },
            actions = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = { webView.value?.reload() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "إعادة تحميل",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        )

        Box(modifier = Modifier.weight(1f)) {
            if (isLoading && !hasError) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = KfuBlue,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(android.graphics.Color.WHITE)
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            supportZoom()
                            builtInZoomControls = true
                            displayZoomControls = false
                            setSupportMultipleWindows(true)
                            cacheMode = android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK
                            layoutAlgorithm = android.webkit.WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            defaultTextEncodingName = "utf-8"
                            userAgentString = settings.userAgentString.replace("; wv", "").replace("Version/\\d+", "Chrome/120.0")
                        }
                        overScrollMode = android.webkit.WebView.OVER_SCROLL_NEVER
                        isVerticalScrollBarEnabled = true
                        isHorizontalScrollBarEnabled = false
                        isNestedScrollingEnabled = false
                        setOnTouchListener { v, event ->
                            when (event.actionMasked) {
                                android.view.MotionEvent.ACTION_DOWN,
                                android.view.MotionEvent.ACTION_MOVE ->
                                    v.parent?.requestDisallowInterceptTouchEvent(true)
                                android.view.MotionEvent.ACTION_UP,
                                android.view.MotionEvent.ACTION_CANCEL ->
                                    v.parent?.requestDisallowInterceptTouchEvent(false)
                            }
                            v.performClick()
                            false
                        }
                        webViewClient = client
                        loadUrl(currentUrl.value)
                        webView.value = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (hasError) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "تعذر تحميل الصفحة", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView.value?.destroy()
            webView.value = null
        }
    }
}