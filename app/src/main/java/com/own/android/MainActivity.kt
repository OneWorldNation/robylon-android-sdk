package com.own.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.own.Robylon
import com.own.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        binding
            .forceRefreshBtn
            .setOnClickListener {
                Robylon.forceRefresh()
            }

        binding
            .destroySession
            .setOnClickListener {
                Robylon.destroy()
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.chatBotButton.destroy()
    }
}