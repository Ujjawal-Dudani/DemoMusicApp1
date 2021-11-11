package com.notification.demomusicapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.notification.demomusicapp.databinding.MusicViewBinding

class MusicAdapter(private val context: Context,private val musiclist :ArrayList<Music>):RecyclerView.Adapter<MusicAdapter.MyHolder>() {
    class MyHolder(binding: MusicViewBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.songnameMV
        val album = binding.songalbumMV
        val image = binding.imagemv
        val duration = binding.songDuration
        val root =binding.root // if some body clicks on root layout we will move to respective layout.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicAdapter.MyHolder, position: Int) {
        holder.title.text = musiclist[position].title
        holder.album.text = musiclist[position].album
        holder.duration.text = format(musiclist[position].duration)

        //for image loading
        Glide.with(context)
            .load(musiclist[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)

        //for moving to respective layout
        holder.root.setOnClickListener {

            val intent = Intent(context,PlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","MusicAdapter")
            //here directly start activity is not allowed
            ContextCompat.startActivity(context,intent,null)

            when
            {
                musiclist[position].id == PlayerActivity.nowPlayingId ->
                sendIntent("NowPlaying",pos = PlayerActivity.songPosition)
            }


        }
    }

    override fun getItemCount(): Int {
        return musiclist.size
    }

    private fun sendIntent(ref:String,pos: Int){
        val intent = Intent(context,PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        //here directly start activity is not allowed
        ContextCompat.startActivity(context,intent,null)
    }
}