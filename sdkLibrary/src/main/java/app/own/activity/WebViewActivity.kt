package app.own.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import app.own.event.ChatbotEventType
import app.own.internal.OwnInternal
import app.own.internal.OwnUserInternal
import app.own.utils.merge
import app.own.utils.postJsMessage
import app.own.utils.setBrandColor
import app.own.view.webview.AdvancedWebView
import app.own.view.webview.WebViewManager
import com.own.BuildConfig
import com.own.databinding.ActivityWebviewBinding
import org.json.JSONObject

class WebViewActivity : Activity() {

    private lateinit var binding: ActivityWebviewBinding

    private val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            WebViewManager.currentUrl = url?:""
            WebViewManager.onPageFinished = true
            val safeWebView = WebViewManager.webView ?: return
            postFirstMessage(safeWebView)
            postSecondMessage(safeWebView)
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        binding.progressBar.setBrandColor()

        val webView: WebView = WebViewManager.createOrGetWebView(this)

        if (binding.webViewFL.childCount == 0) {
            webView.webViewClient = webViewClient
            WebViewManager.detachFromParent()
            binding
                .webViewFL
                .addView(
                    webView,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )
        }
        loadIfRequired()
        WebViewManager.payloadListener { event ->
            if (event == ChatbotEventType.CHATBOT_CLOSED) {
                finish()
            }
        }
    }


    private fun loadIfRequired() {
        if (WebViewManager.onPageFinished) {
            binding.progressBar.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.VISIBLE
            OwnInternal.chatIframeUrl?.let {
                WebViewManager.webView?.loadUrl(it)
            }
        }
    }

    private fun postSecondMessage(webView: AdvancedWebView) {
        val finalUserProfile = OwnUserInternal.getSystemInfo()
        finalUserProfile.put("browser", webView.settings.userAgentString)
        OwnUserInternal.getUserProfile()?.let { userProfileJo ->
            finalUserProfile.merge(userProfileJo)
        }

        val dataJo = JSONObject()
        dataJo.put("userId", OwnUserInternal.getUserId())
        OwnUserInternal.getUserToken()?.let { userToken ->
            dataJo.put("token", userToken)
        }
        dataJo.put("userProfile", finalUserProfile)

        val messageData = mapOf(
            "name" to "registerUserId",
            "action" to "registerUserId",
            "data" to dataJo
        )
//        Log.i(OwnInternal.TAG, "postSecondMessage:$messageData")
        webView.postJsMessage(messageData)
    }

    private fun postFirstMessage(webView: AdvancedWebView) {
        webView.postJsMessage(
            mapOf(
                "name" to "openFrame",
                "domain" to "app-domain.com"
            )
        )
    }


    companion object {

        fun openChats(
            context: Context,
        ) {
            context.startActivity(Intent(context, WebViewActivity::class.java))
        }
    }
}