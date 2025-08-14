package app.own.view.webview

import android.app.Activity
import app.own.base.mapToEnum
import app.own.event.ChatbotEventType
import app.own.internal.OwnInternal
import app.own.utils.detachFromParent
import app.own.utils.setDefaultSettings
import com.own.R
import org.json.JSONObject


internal object WebViewManager {

    private lateinit var payloadListener: (event: ChatbotEventType) -> Unit
    private var javaScriptInterfaceLinksHandler: JavaScriptInterfaceLinksHandler? = null
    var webView: AdvancedWebView? = null
    var currentUrl :String = ""
    var onPageFinished = false
    private var isError = false


    fun createOrGetWebView(activity: Activity): AdvancedWebView {
        return if (webView == null) {
            val safeWebView = activity.layoutInflater.inflate(R.layout.webview, null, false) as AdvancedWebView
            val safeJavaScriptInterfaceLinksHandler = JavaScriptInterfaceLinksHandler()
            safeWebView.addJavascriptInterface(safeJavaScriptInterfaceLinksHandler, "Android")
            safeWebView.setDefaultSettings()
            javaScriptInterfaceLinksHandler = safeJavaScriptInterfaceLinksHandler
            safeJavaScriptInterfaceLinksHandler.listener = object : JavaScriptPayloadListener {
                override fun onPayload(payload: String) {
                    handlePayload(payload)
                }

                override fun logMessage(message: String) {

                }

            }
            webView = safeWebView
            safeWebView
        } else {
            webView!!
        }
    }

    private fun handlePayload(payload: String) {
        if (payload.isNotBlank()) {
            val jsonObject = JSONObject(payload)
            val operation = jsonObject.getString("operation")
            if (operation == "chat.close") {
                payloadListener.invoke(ChatbotEventType.CHATBOT_CLOSED)
                OwnInternal.chatBotEvent(ChatbotEventType.CHATBOT_CLOSED)
            } else if (operation == "system.event") {
                val state = jsonObject.getString("event")
                state.mapToEnum<ChatbotEventType>(ignoreCase = true)?.let {
                    OwnInternal.chatBotEvent(it)
                }
            }
        }
    }

    fun detachFromParent() {
        webView?.detachFromParent()
    }

    fun payloadListener(listener: (event: ChatbotEventType) -> Unit) {
        payloadListener = listener
    }

    fun onDestroy() {
        onPageFinished = false
        isError = false
        javaScriptInterfaceLinksHandler = null
        webView?.destroy()
        webView = null
    }

    fun forceRefresh() {
        onPageFinished = false
    }
}