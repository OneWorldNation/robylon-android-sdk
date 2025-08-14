package app.own.internal

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import app.own.base.getBestFontColor
import app.own.base.mapToEnum
import app.own.config.BotConfigListener
import app.own.view.ChatBotButtonPosition
import app.own.view.ChatBotButtonType
import com.own.BuildConfig
import org.json.JSONObject
import java.util.UUID

@SuppressLint("UseKtx")
object OwnUserInternal {

    private val pref: SharedPreferences by lazy {
        OwnInternal.context.getSharedPreferences(OWN_PREF, Context.MODE_PRIVATE)
    }

    private const val OWN_PREF = "own"
    private const val PREF_KEY_ANONYMOUS_USER_ID = "anonymous_user_id"

    private const val PREF_KEY_USER_TOKEN = "user_token"
    private const val PREF_KEY_USER_ID = "user_id"
    private const val PREF_KEY_USER_PROFILE = "user_profile"
    private const val PREF_KEY_BOT_CONFIG = "bot_config"

    private val botConfigListeners = arrayListOf<BotConfigListener>()

    var brandColor: String? = null

    fun getUserId(): String {
        return getUserIdOrNull() ?: getAnonymousUserId()
    }

    fun getUserIdOrNull(): String? {
        return if (pref.contains(PREF_KEY_USER_ID)) {
            pref.getString(PREF_KEY_USER_ID, "") ?: ""
        } else {
            null
        }
    }

    fun getAnonymousUserId(): String {
        return pref.getString(PREF_KEY_ANONYMOUS_USER_ID, "") ?: ""
    }

    fun setUserId(userId: String) {
        pref.edit().putString(PREF_KEY_USER_ID, userId).apply()
    }

    fun resetUserId() {
        pref.edit().remove(PREF_KEY_USER_ID).apply()
    }

    fun setAnonymousUserIdIfRequired() {
        if (!pref.contains(PREF_KEY_ANONYMOUS_USER_ID)) {
            pref.edit().putString(PREF_KEY_ANONYMOUS_USER_ID, UUID.randomUUID().toString()).apply()
        }
    }

    fun setUserProfile(userProfile: JSONObject) {
        pref.edit().putString(PREF_KEY_USER_PROFILE, userProfile.toString()).apply()
    }

    fun getUserProfile(): JSONObject? {
        val userProfileString = pref.getString(PREF_KEY_USER_PROFILE, null) ?: return null
        return JSONObject(userProfileString)
    }

    fun resetUserProfile() {
        pref.edit().remove(PREF_KEY_USER_PROFILE).apply()
    }

    fun setUserToken(userToken: String) {
        pref.edit().putString(PREF_KEY_USER_TOKEN, userToken).apply()
    }

    fun getUserToken(): String? {
        return pref.getString(PREF_KEY_USER_TOKEN, null)
    }

    fun resetUserToken() {
        pref.edit().remove(PREF_KEY_USER_TOKEN).apply()
    }

    fun storeBotConfig(response: String?) {
        val newBotConfigString = response ?: "{}"
        pref.edit().putString(PREF_KEY_BOT_CONFIG, response ?: "{}").apply()
        notifyListener(newBotConfigString.parseBotConfig())
    }

    fun addBotConfigListener(botConfigListener: BotConfigListener) {
        if (!botConfigListeners.contains(botConfigListener))
            botConfigListeners.add(botConfigListener)
        val currentBotConfigString = pref.getString(PREF_KEY_BOT_CONFIG, null)
        currentBotConfigString?.let {
            notifyListener(currentBotConfigString.parseBotConfig())
        }

    }

    fun removeBotConfigListener(botConfigListener: BotConfigListener) {
        botConfigListeners.remove(botConfigListener)
    }

    private fun notifyListener(parseBotConfig: ChatBotButtonType) {
        botConfigListeners.forEach { it.listen(parseBotConfig) }
    }

    private fun String?.parseBotConfig(): ChatBotButtonType {
        this ?: return ChatBotButtonType.Text()
        val jo = JSONObject(this)
        val user = jo.optJSONObject("user")
        val orgInfo = user?.optJSONObject("org_info")
        val brandConfig = orgInfo?.optJSONObject("brand_config") ?: return ChatBotButtonType.Text()
        OwnInternal.chatIframeUrl = brandConfig.optString("chat_iframe_url", "") ?: ""
        brandConfig
            .optJSONObject("colors")
            ?.optString("brand_color")?.let { brandColor ->
                this@OwnUserInternal.brandColor = brandColor
            }

        return when (brandConfig.optString("launcher_type")) {
            "TEXT"->{
                getTextBotButtonType(brandConfig)
            }
            "TEXTUAL_IMAGE" -> {
                val textType = getTextBotButtonType(brandConfig)
                val imageType = getImagedBotButtonType(brandConfig)
                ChatBotButtonType.TextImage(
                    textType,
                    imageType
                )
            }
            else -> {
                getImagedBotButtonType(brandConfig)
            }
        }
    }

    private fun getImagedBotButtonType(brandConfig: JSONObject): ChatBotButtonType.Image {
        var botButton = ChatBotButtonType.Image()
        brandConfig
            .optJSONObject("images")
            ?.optJSONObject("launcher_image_url")
            ?.optString("url")
            ?.let { launcherImageUrl ->
                botButton = botButton.copy(url = launcherImageUrl)
            }
        if(botButton.url.isBlank()){
            botButton = botButton.copy(url = "https://chatbot.robylon.ai/chatbubble.png")
        }
        botButton = botButton.copy(sideSpacingDp = 40, bottomSpacingDp = 40)
        brandConfig
            .optJSONObject("interface_properties")
            ?.let { jo ->
//                jo.optInt("side_spacing").let { sideSpacing ->
//                    botButton = botButton.copy(sideSpacingDp = sideSpacing)
//                }


//                jo.optInt("bottom_spacing").let { bottomSpacing ->
//                    botButton = botButton.copy(bottomSpacingDp = bottomSpacing)
//                }

                jo.optString("position").mapToEnum<ChatBotButtonPosition>(ignoreCase = true)?.let { newPosition ->
                    botButton = botButton.copy(position = newPosition)
                }
            }

        return botButton
    }

    private fun getTextBotButtonType(brandConfig: JSONObject): ChatBotButtonType.Text {
        var textType = ChatBotButtonType.Text()

        brandConfig
            .optJSONObject("launcher_properties")
            ?.optString("text")
            ?.let { newText ->
                textType = textType.copy(text = newText)
            }

        brandConfig
            .optJSONObject("colors")
            ?.optString("brand_color")?.let { brandColor ->
                textType = textType.copy(
                    backgroundColor = brandColor,
                    textColor = getBestFontColor(brandColor)
                )
            }

        textType = textType.copy(sideSpacingDp = 40, bottomSpacingDp = 40)

        brandConfig
            .optJSONObject("interface_properties")
            ?.let { jo ->
//                jo.optInt("side_spacing").let { sideSpacing ->
//                    textType = textType.copy(sideSpacingDp = sideSpacing)
//                }


//                jo.optInt("bottom_spacing").let { bottomSpacing ->
//                    textType = textType.copy(bottomSpacingDp = bottomSpacing)
//                }

                jo.optString("position").mapToEnum<ChatBotButtonPosition>(ignoreCase = true)?.let { newPosition ->
                    textType = textType.copy(position = newPosition)
                }
            }
        return textType
    }

    fun getSystemInfo(): JSONObject {
        val resources = OwnInternal.context.resources
        val metrics = resources.displayMetrics
        return JSONObject().apply {
            put("platform", "android")
            put("os_version", Build.VERSION.RELEASE ?: "android")
            put("os_api_level", Build.VERSION.SDK_INT) // Ex - 34,35
            put("sdk_version", BuildConfig.OWN_SDK_VERSION_NAME)
            put("device", if (resources.configuration.smallestScreenWidthDp >= 600) "tablet" else "phone")
            put("screen_size", "${metrics.widthPixels}x${metrics.heightPixels}")
        }
    }
}