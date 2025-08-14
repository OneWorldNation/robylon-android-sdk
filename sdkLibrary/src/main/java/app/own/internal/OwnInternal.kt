package app.own.internal

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import app.own.base.NetworkClient
import app.own.base.SdkCreator
import app.own.event.ChatbotEventListener
import app.own.event.ChatbotEventType
import app.own.utils.merge
import app.own.view.webview.WebViewManager
import org.json.JSONObject

@SuppressLint("StaticFieldLeak")
internal object OwnInternal {

    const val TAG = "own_sdk"

    lateinit var context: Context

    var chatIframeUrl: String? = null
    lateinit var configBaseUrl: String

    lateinit var apiKey: String

    var userChatbotEventListener: ChatbotEventListener? = null

    private var chatbotEventListener: ChatbotEventListener = object : ChatbotEventListener {
        override fun onEvent(chatbotEventType: ChatbotEventType) {
            SdkCreator
                .executorService
                .execute {
                    handleInternalEvent(chatbotEventType)
                }
        }
    }


    fun fetchAndStoreConfig() {
        SdkCreator
            .executorService
            .execute {
                val userId = OwnUserInternal.getUserId()
                val bodyJo = JSONObject()
                bodyJo.put("client_user_id", userId)
                bodyJo.put("org_id", apiKey)
                val userToken = OwnUserInternal.getUserToken()
                if (!userToken.isNullOrBlank()) bodyJo.put("token", userToken)
                bodyJo.put("extra_info", JSONObject())
                val response = NetworkClient.httpCall(
                    urL = "$configBaseUrl/chat/chatbot/get/",
                    requestJson = bodyJo.toString()
                )
                if (response.ok()) {
                    OwnUserInternal.storeBotConfig(response.response)
                }
            }
    }

    fun chatBotEvent(chatbotClosed: ChatbotEventType) {
        userChatbotEventListener?.onEvent(chatbotClosed)
        chatbotEventListener.onEvent(chatbotClosed)
    }

    fun handleInternalEvent(
        eventType: ChatbotEventType,
        additionalData: JSONObject? = null
    ) {
        try {
            val systemInfo = OwnUserInternal.getSystemInfo()

            val userProfile = OwnUserInternal.getUserProfile()
            var eventDataJo = JSONObject()
            eventDataJo.put("trigger_time", System.currentTimeMillis())
            eventDataJo.put("channel", "CHATBOT")
            eventDataJo.put("launch_url", WebViewManager.currentUrl)
            eventDataJo.put("org_id", apiKey)
            eventDataJo.put("client_user_id", OwnUserInternal.getUserIdOrNull() ?: "Anonymous")
            eventDataJo.put("event_type", eventType.name)
            if (userProfile != null)
                eventDataJo.put("user_profile", userProfile)

            if (additionalData != null)
                eventDataJo = eventDataJo.merge(additionalData)


            val metadataJo = JSONObject()
            metadataJo.put("timestamp", System.currentTimeMillis())
            metadataJo.merge(systemInfo)

            val payloadJO = JSONObject()
            payloadJO.put("org_id", apiKey)
            payloadJO.put("event_data", eventDataJo)
            payloadJO.put("metadata", metadataJo)
            payloadJO.put("event_type", "INFO")
            payloadJO.put("user_id", OwnUserInternal.getUserId())


            NetworkClient.httpCall(
                urL = "${configBaseUrl}/users/sdk/record-logs/",
                requestJson = payloadJO.toString(),
                requestMethod = "POST"
            )

        } catch (e: Exception) {
            Log.e(TAG, "Exception in handleInternalEvent:$eventType", e)
        }
    }


}