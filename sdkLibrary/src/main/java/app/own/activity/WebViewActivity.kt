package app.own.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowInsetsCompat
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
import androidx.core.net.toUri

class WebViewActivity : ComponentActivity() {

    private lateinit var binding: ActivityWebviewBinding

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try{
            WebViewManager.webView?.handleFilePickerResult(result.resultCode, result.data)
        }catch (e:Exception){
            //Do nothing
        }
    }

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

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return handleUrlLoading(url)
        }

        @Suppress("DEPRECATION")
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                request?.url?.toString()?.let { handleUrlLoading(it) } ?: false
            } else {
                super.shouldOverrideUrlLoading(view, request)
            }
        }
    }

    private fun handleUrlLoading(url: String?): Boolean {
        try {
            if (url == null) return false

            val chatIframeUrl = OwnInternal.chatIframeUrl
            if (chatIframeUrl != null && url.startsWith(chatIframeUrl)) {
                return false
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            return false
        }
        return true
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

        val advancedWebView: AdvancedWebView = WebViewManager.createOrGetWebView(this)

        advancedWebView.setFilePickerLauncher { intent ->
            filePickerLauncher.launch(intent)
        }

        if (binding.webViewFL.childCount == 0) {
            advancedWebView.webViewClient = webViewClient
            WebViewManager.detachFromParent()
            binding
                .webViewFL
                .addView(
                    advancedWebView,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )
        }
        
        // Handle system insets for edge-to-edge
        setupEdgeToEdgeInsets(binding.parentFL)
        
        loadIfRequired()
        WebViewManager.payloadListener { event, jsonObject ->
            if (event == ChatbotEventType.CHATBOT_CLOSED) {
                finish()
            } else if (event == ChatbotEventType.DOWNLOAD_FILE) {
                val url = jsonObject?.getString("url")
                try {
                    val launchUri = url?.toUri() ?: return@payloadListener
                    val intent = Intent(Intent.ACTION_VIEW, launchUri)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } catch (e: Exception) {
                    // Do nothing
                }
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


    override fun onDestroy() {
        super.onDestroy()
        WebViewManager.webView?.clearActivityReference()
    }

    companion object {

        fun openChats(
            context: Context,
        ) {
            context.startActivity(Intent(context, WebViewActivity::class.java))
        }
    }
}