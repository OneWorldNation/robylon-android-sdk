package app.own.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.webkit.WebView
import org.json.JSONObject
import java.io.InputStream

internal fun WebView.setDefaultSettings() {
    scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
    isScrollbarFadingEnabled = false
    isVerticalScrollBarEnabled = false
    settings.allowFileAccess = true
    settings.allowContentAccess = true
    settings.domStorageEnabled = true
    settings.displayZoomControls = false
    enableJavaScript()
    settings.loadWithOverviewMode = true
    settings.useWideViewPort = true
}

@SuppressLint("SetJavaScriptEnabled")
internal fun WebView.enableJavaScript() {
    settings.javaScriptEnabled = true
}

internal fun WebView.postJsMessage(data: Map<String, Any?>, targetOrigin: String = "*") {
    val json = JSONObject(data).toString()
    val script = """window.postMessage($json, "$targetOrigin");"""
    this.evaluateJavascript(script, null)
}