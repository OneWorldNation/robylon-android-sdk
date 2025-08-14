package app.own.view.webview

import android.webkit.JavascriptInterface

class JavaScriptInterfaceLinksHandler {

    var listener: JavaScriptPayloadListener? = null

    @JavascriptInterface
    fun handlePayload(payload: String) {
        if (payload.isNotBlank()) {
            listener?.onPayload(payload)
        }
    }

    @JavascriptInterface
    fun logMessage(message: String) {
        listener?.logMessage(message)
    }
}

