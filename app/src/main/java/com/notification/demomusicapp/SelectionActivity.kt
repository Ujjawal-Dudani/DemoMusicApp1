package com.notification.demomusicapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_selection.*

class SelectionActivity : AppCompatActivity() {
    private lateinit var adapter: MusicAdapter
    private var musicList: ArrayList<Music> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        setContentView(R.layout.activity_selection)

        //recycler view
        selectionRV.setItemViewCacheSize(10)
        selectionRV.setHasFixedSize(true)
        selectionRV.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this, MainActivity.MusicListMA, selectionActivity = true)
        selectionRV.adapter = adapter

        addSongsSA.setOnClickListener {
            addSongsSA.visibility = View.VISIBLE
            finish()
        }
        removeSongsSA.setOnClickListener {
            removeSongsSA.visibility = View.VISIBLE
            finish()
        }

//        addSongsSA.visibility = View.INVISIBLE
//        removeSongsSA.visibility = View.INVISIBLE
//        Log.e("song list1::",musicList.size.toString())
//
//        if(musicList.size>0)
//        {
//            Log.e("song list",musicList.toString())
//            addSongsSA.visibility = View.VISIBLE
//            removeSongsSA.visibility = View.VISIBLE
//        }
        /*else {
            Log.e("song list...",musicList.toString())
            addSongsSA.visibility = View.INVISIBLE
            removeSongsSA.visibility = View.INVISIBLE
        }*/


        //for search View
       searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if(newText != null){
                    val userInput = newText.lowercase()
                    for (song in MainActivity.MusicListMA)
                        if(song.title.lowercase().contains(userInput))
                            MainActivity.musicListSearch.add(song)
                    MainActivity.search = true
                    adapter.updateMusicList(searchList = MainActivity.musicListSearch)
                }
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        //for black theme checking
        if(MainActivity.themeIndex == 4)
        {
            searchViewSA.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
        }
    }
}