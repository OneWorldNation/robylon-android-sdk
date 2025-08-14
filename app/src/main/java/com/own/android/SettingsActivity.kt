package com.own.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import app.own.Robylon
import app.own.event.ChatbotEventListener
import app.own.event.ChatbotEventType
import app.own.event.ChatbotEventType.*
import com.own.android.base.AppCreator
import com.own.android.databinding.ActivitySettingsBinding
import org.json.JSONObject

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val ORG_ID = "orgId"
        const val USER_ID = "userId"
        const val USER_TOKEN = "userToken"
        const val USER_PROFILE = "userProfile"
    }

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        binding.orgIdEt.setText(getValue(ORG_ID, BuildConfig.OWN_API_KEY))
        binding.userIdEt.setText(getValue(USER_ID))
        binding.userTokenEt.setText(getValue(USER_TOKEN,))
        binding.userProfileEt.setText(getValue(USER_PROFILE,getDefaultUserProfile()))

        binding.launchAIChat.setOnClickListener {
            storeValues()
            launchAIChat()
        }

    }

    private fun getDefaultUserProfile(): String {
        val jsonObject = JSONObject()
        jsonObject.put("isPremium",true)
        return jsonObject.toString()
    }

    private fun launchAIChat() {

        val orgId = binding.orgIdEt.text.toString()
        if (orgId.isBlank()) {
            Toast.makeText(this, "Please enter orgId", Toast.LENGTH_SHORT).show()
            return
        }


        val userId = binding.userIdEt.text.toString().ifBlank { null }
        Robylon.setUserId(userId)

        val userToken = binding.userTokenEt.text.toString().ifBlank { null }
        Robylon.setUserToken(userToken)

        val userProfileJo = binding.userProfileEt.text.toString().ifBlank { null }?.let { JSONObject(it) }
        Robylon.setUserProfile(userProfileJo)

        Robylon
            .initialize(context = this.applicationContext, apiKey = orgId,!binding.isProdCb.isChecked)

        Robylon.setChatbotEventListener(object : ChatbotEventListener {
            override fun onEvent(chatbotEventType: ChatbotEventType) {
                Log.i("ChatbotEventListener", "onEvent: $chatbotEventType")
            }
        })

        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun storeValues() {
        storeValue(ORG_ID, binding.orgIdEt.text.toString())
        storeValue(USER_ID, binding.userIdEt.text.toString())
        storeValue(USER_TOKEN, binding.userTokenEt.text.toString())
        storeValue(USER_PROFILE, binding.userProfileEt.text.toString())
    }

    private fun getValue(key: String, default: String = ""): String {
        return AppCreator.pref.getString(key, default) ?: default
    }

    private fun storeValue(key: String, value: String) {
        AppCreator.pref.edit(commit = true) {
            putString(key, value)
        }
    }
}