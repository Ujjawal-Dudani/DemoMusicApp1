package com.notification.demomusicapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.notification.demomusicapp.databinding.FavouriteViewBinding

class FavouriteAdapter(private val context: Context, private val musiclist :ArrayList<Music>):
    RecyclerView.Adapter<FavouriteAdapter.MyHolder>() {
    class MyHolder(binding: FavouriteViewBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.songIMgFV
        val name = binding.songnameFV
        val root =binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteAdapter.MyHolder {
        return MyHolder(FavouriteViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: FavouriteAdapter.MyHolder, position: Int) {
        holder.name.text =musiclist[position].title
        //for image loading
        Glide.with(context)
            .load(musiclist[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)

        holder.root.setOnClickListener {
            val intent = Intent(context,PlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","FavouriteAdapter")
            //here directly start activity is not allowed
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return musiclist.size
    }

}