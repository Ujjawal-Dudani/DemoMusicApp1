package com.notification.demomusicapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        try{PlaylistActivity.musicPlayList.ref[currentPlayListPos].playlist =
            checkPlaylist(playlist = PlaylistActivity.musicPlayList.ref[currentPlayListPos].playlist)}
        catch(e: Exception){}

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        adapter = MusicAdapter(this,PlaylistActivity.musicPlayList.ref[currentPlayListPos].playlist ,playListDetails = true)
        binding.playlistDetailsRV.adapter = adapter
        binding.backBtnPD.setOnClickListener {
            finish()
        }
        binding.shuffleBtnPD.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlayListDetailsShuffle")
            startActivity(intent)
        }

        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this,SelectionActivity::class.java))
        }
        binding.removeAllPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("REMOVE")
                .setMessage("Do You want To Remove  ALL Songs From Playlist")
                .setPositiveButton("Yes"){ dialog , _  ->
                    PlaylistActivity.musicPlayList.ref[currentPlayListPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog,_ ->
                    dialog.dismiss()
                }
            val customDialog  = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playListNamePD.text = PlaylistActivity.musicPlayList.ref[currentPlayListPos].name
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On :\n${PlaylistActivity.musicPlayList.ref[currentPlayListPos].createdOn}\n\n" +
                "  -- ${PlaylistActivity.musicPlayList.ref[currentPlayListPos].createdBy}"

        if (adapter.itemCount > 0) {
            //for image loading
            Glide.with(this)
                .load(PlaylistActivity.musicPlayList.ref[currentPlayListPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.playListImgPD)

            binding.shuffleBtnPD.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()

//        //for storing favourites data using shared preferences
//        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
//        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlayList)
//        editor.putString("MusicPLayList",jsonStringPlaylist)
//        editor.apply()
//
//        }
    }
}
