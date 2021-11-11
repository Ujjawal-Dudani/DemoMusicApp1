package com.notification.demomusicapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.notification.demomusicapp.databinding.PlaylistViewBinding

class PlayListAdapter(private val context: Context, private var playListlist :ArrayList<Playlist>):
    RecyclerView.Adapter<PlayListAdapter.MyHolder>() {
    class MyHolder(binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.playListImg
        val name = binding.playListName
        val root =binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListAdapter.MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text =playListlist[position].name
        holder.name.isSelected =true
    }

    override fun getItemCount(): Int {
        return playListlist.size
    }

    fun refreshPlayList(){
        playListlist = ArrayList()
        playListlist.addAll(PlaylistActivity.musicPlayList.ref)
        notifyDataSetChanged()
    }

}