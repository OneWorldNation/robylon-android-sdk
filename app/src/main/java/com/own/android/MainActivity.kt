package com.own.android

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import app.own.Robylon
import com.own.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        enableEdgeToEdge()
        setupEdgeToEdgeInsets(binding.main)

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