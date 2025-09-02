package app.own.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import app.own.Robylon
import app.own.config.BotConfigListener
import app.own.event.ChatbotEventType
import app.own.internal.OwnInternal
import app.own.internal.OwnUserInternal
import app.own.utils.merge
import app.own.utils.postJsMessage
import app.own.utils.setBrandColor
import app.own.view.ChatBotButtonType
import app.own.view.webview.AdvancedWebView
import app.own.view.webview.WebViewManager
import com.own.BuildConfig
import com.own.databinding.ActivityWebviewBinding
import org.json.JSONObject

class WebViewActivity : ComponentActivity() {

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
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
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
        
        // Handle system insets for edge-to-edge
        setupEdgeToEdgeInsets(binding.parentFL)
        
        loadIfRequired()
        WebViewManager.payloadListener { event ->
            if (event == ChatbotEventType.CHATBOT_CLOSED) {
                finish()
            }
        }

        if (!OwnUserInternal.hasBotConfig()) {
            OwnUserInternal
                .addBotConfigListener(
                    object : BotConfigListener {
                        override fun listen(chatBotButtonType: ChatBotButtonType) {
                            loadIfRequired()
                            OwnUserInternal.removeBotConfigListener(this)
                        }
                    }
                )
        }
    }

    private fun setupEdgeToEdgeInsets(onView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            onView.setOnApplyWindowInsetsListener { view, insets ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    view.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                    )
                }

                insets
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