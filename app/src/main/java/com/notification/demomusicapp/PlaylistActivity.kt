package com.notification.demomusicapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.notification.demomusicapp.databinding.ActivityPlaylistBinding

class PlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DemoMusicApp) // changing the theme of the activity by default theme provided by the android studio.
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnPLA.setOnClickListener {
            finish()
        }
    }
}