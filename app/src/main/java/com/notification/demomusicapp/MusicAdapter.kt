package com.notification.demomusicapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.notification.demomusicapp.MusicAdapter.MyHolder
import com.notification.demomusicapp.databinding.MusicViewBinding

class MusicAdapter(private val context: Context, private var musicList: ArrayList<Music>, private val playlistDetails: Boolean = false,
                   private val selectionActivity: Boolean = false)
    : RecyclerView.Adapter<MyHolder>() {

    class MyHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
            .into(holder.image)
        when{
            playlistDetails ->{
                holder.root.setOnClickListener {
                    sendIntent(ref = "PlaylistDetailsAdapter", pos = position)
                }
            }
            selectionActivity ->{
                holder.root.setOnClickListener {
                    if(addSong(musicList[position]))
                    {
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.cool_green))
                    }
                    else
                    {
                        //holder.root.visibility = View.INVISIBLE
                        val builder = MaterialAlertDialogBuilder(context)
                        builder.setTitle(musicList[position].title)
                            .setMessage("Song Already Exists Do you want to remove?")
                            .setPositiveButton("Yes"){ dialog, _ ->
                                holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){dialog, _ ->
                                if(addSong(musicList[position]))
                                {
                                    holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.cool_green))
                                }
                                dialog.dismiss()
                            }
                        val customDialog = builder.create()
                        customDialog.show()
                        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                        customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(musicList[position])
                    }
                }
            }


            else ->{
                holder.root.setOnClickListener {
                when{
                    MainActivity.search -> sendIntent(ref = "MusicAdapterSearch", pos = position)
                    musicList[position].id == PlayerActivity.nowPlayingId ->
                        sendIntent(ref = "NowPlaying", pos = PlayerActivity.songPosition)
                    else->sendIntent(ref="MusicAdapter", pos = position) } }
        }

         }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList : ArrayList<Music>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun sendIntent(ref: String, pos: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }
    private fun addSong(song: Music): Boolean{
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(song.id == music.id){
                PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        return true
    }
    fun refreshPlaylist(){
        musicList = ArrayList()
        musicList = PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }
}