package com.notification.demomusicapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.notification.demomusicapp.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetails : AppCompatActivity() {

    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var adapter: MusicAdapter

    companion object{
        var currentPlayListPos:Int =-1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setTheme(R.style.splash_screen)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlayListPos =intent.extras?.get("index") as Int

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        PlaylistActivity.musicPlayList.ref[currentPlayListPos].playlist.addAll(MainActivity.MusicListMA)
        adapter = MusicAdapter(this,PlaylistActivity.musicPlayList.ref[currentPlayListPos].playlist ,playListDetails = true)
        binding.playlistDetailsRV.adapter = adapter
        binding.backBtnPD.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playListNamePD.text = PlaylistActivity.musicPlayList.ref[currentPlayListPos].name
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On :\n${PlaylistActivity.musicPlayList.ref[currentPlayListPos].createdOn}\n\n" +
                "  -- ${PlaylistActivity.musicPlayList.ref[currentPlayListPos].createdBy}"

        if(adapter.itemCount>0)
        {
            //for image loading
            Glide.with(this)
                .load(PlaylistActivity.musicPlayList.ref[currentPlayListPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.playListImgPD)

            binding.shuffleBtnPD.visibility = View.VISIBLE
        }

    }
}