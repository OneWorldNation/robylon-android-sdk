package app.own.view.webview

interface JavaScriptPayloadListener {
    fun onPayload(payload: String)
    fun logMessage(message: String)
}