package com.notification.demomusicapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.notification.demomusicapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DemoMusicApp)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title ="Settings"

    }
}