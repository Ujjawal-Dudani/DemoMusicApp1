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
import com.notification.demomusicapp.databinding.PlaylistViewBinding

class PlayListAdapter(private val context: Context, private var playListlist :ArrayList<Playlist>):
    RecyclerView.Adapter<PlayListAdapter.MyHolder>() {
    class MyHolder(binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.playListImg
        val name = binding.playListName
        val root =binding.root
        val delete = binding.playListDeleteBtn

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListAdapter.MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text =playListlist[position].name
        holder.name.isSelected =true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playListlist[position].name)
                .setMessage("Do You want To Delete")
                .setPositiveButton("Yes"){ dialog , _  ->
                    PlaylistActivity.musicPlayList.ref.removeAt(position)
                    refreshPlayList()
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
        holder.root.setOnClickListener {
            val intent =Intent(context,PlaylistDetails::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
        }

        if(PlaylistActivity.musicPlayList.ref[position].playlist.size > 0){

            //for image loading
            Glide.with(context)
                .load(PlaylistActivity.musicPlayList.ref[position].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(holder.image)

        }
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