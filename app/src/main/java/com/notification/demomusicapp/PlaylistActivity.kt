package com.notification.demomusicapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.notification.demomusicapp.databinding.ActivityPlaylistBinding
import com.notification.demomusicapp.databinding.AddPlaylistDialogBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var  adapter: PlayListAdapter

    companion object{
         var musicPlayList : MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DemoMusicApp) // changing the theme of the activity by default theme provided by the android studio.
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // will create only that much object which are required will save the memory efficently (L106)
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this,2)
        adapter = PlayListAdapter(this, playListlist = musicPlayList.ref)
        binding.playlistRV.adapter = adapter
        binding.addPlaylistBtn.setOnClickListener {
            customAlertDialogBox()
        }

        binding.backBtnPLA.setOnClickListener {
            finish()
        }
    }
    private  fun customAlertDialogBox(){
        val customDialog = LayoutInflater.from(this).inflate( R.layout.add_playlist_dialog, binding.root, false )
        val binder =AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("Add"){ dialog , _  ->
                val playListNameA= binder.playListNameA.text
                val createdBy =binder.playListUserNameA.text
                if(playListNameA!=null &&  createdBy!=null )
                    if(playListNameA.isNotEmpty() &&  createdBy.isNotEmpty()){
                        addPlayList(playListNameA.toString(),createdBy.toString())
                    }

                dialog.dismiss()
            }.show()
    }

    private fun addPlayList(name:String,createdBy:String){
        var playListExists =false
        for (i in musicPlayList.ref)
        {
            if(name.equals(i.name))
            {
                playListExists =true
                break
            }
        }
        if(playListExists)
        {
            Toast.makeText(this, "Playlist Already Exists", Toast.LENGTH_SHORT).show()
        }
        else
        {
            val tempPlaylist = Playlist()
            tempPlaylist.name =name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy =createdBy
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calendar)

            musicPlayList.ref.add(tempPlaylist)
            adapter.refreshPlayList()
        }

    }
}