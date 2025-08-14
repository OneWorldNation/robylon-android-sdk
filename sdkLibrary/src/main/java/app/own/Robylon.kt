package app.own

import android.content.Context
import app.own.event.ChatbotEventListener
import app.own.internal.OwnInternal
import app.own.internal.OwnUserInternal
import app.own.view.webview.WebViewManager
import com.own.BuildConfig
import org.json.JSONObject

object Robylon {

    /**
     * Initialize Own SDK
     * @param context Application context
     * @param apiKey Own API key / Org ID
     * @param baseUrl Own base URL
     */
    fun initialize(
        context: Context,
        apiKey: String,
        isStaging:Boolean = false
    ) {

        //CONTEXT
        OwnInternal.context = context

        //API KEY
        if (apiKey.isBlank()) {
            throw IllegalStateException("Please add correct api key")
        }
        OwnInternal.apiKey = apiKey

        if (isStaging) {
            OwnInternal.configBaseUrl = BuildConfig.OWN_CONFIG_BASE_URL_STAGE
        } else {
            OwnInternal.configBaseUrl = BuildConfig.OWN_CONFIG_BASE_URL_PROD
        }

        //Anonymous userID
        OwnUserInternal.setAnonymousUserIdIfRequired()

        OwnInternal.fetchAndStoreConfig()

    }

    /**
     * Set user ID - Null if
     */
    fun setUserId(userId: String?) {
        if (userId == null) {
            OwnUserInternal.resetUserId()
        } else {
            OwnUserInternal.setUserId(userId)
        }
    }

    fun setUserProfile(userProfile: JSONObject?) {
        if (userProfile == null) {
            OwnUserInternal.resetUserProfile()
        } else {
            OwnUserInternal.setUserProfile(userProfile)
        }
    }

    fun setUserToken(userToken: String?) {
        if (userToken == null) {
            OwnUserInternal.resetUserToken()
        } else {
            OwnUserInternal.setUserToken(userToken)
        }
    }

    fun setChatbotEventListener(chatbotEventListener: ChatbotEventListener){
        OwnInternal.userChatbotEventListener = chatbotEventListener
    }

    fun forceRefresh() {
        WebViewManager.forceRefresh()
    }

    fun destroy() {
        WebViewManager.onDestroy()
    }
}