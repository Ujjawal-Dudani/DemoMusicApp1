package com.notification.demomusicapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.notification.demomusicapp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle // navigation drawer
    private lateinit var musicAdapter: MusicAdapter

    /* companion object is a static object.
     it is used for making the static objects the objects are created only once and can be accessed from any class */
    companion object{
        lateinit var MusicListMA : ArrayList<Music>
        var themeIndex:Int =0
        val currentTheme = arrayOf(R.style.coolPink,R.style.coolBlue,R.style.coolPurple,R.style.coolGreen,R.style.coolBlack)
        val currentThemeNav = arrayOf(R.style.coolPinkNav,R.style.coolBlueNav,R.style.coolPurpleNav,R.style.coolGreenNav
            ,R.style.coolBlackNav)
        val currentGradient = arrayOf(R.drawable.gradient_pink,R.drawable.gradient_blue,R.drawable.gradient_purple,R.drawable.gradient_green
        ,R.drawable.gradient_black)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex", 0)
        setTheme(currentThemeNav[themeIndex]) // changing the theme of the activity by default theme provided by the android studio.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //for nav drawer
        toggle = ActionBarDrawerToggle(this, binding.root,R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestRuntimePermission())
        {
            initializeLayout()

            //for retrieving favourites data using shared preferences
            FavouriteActivity.favouriteSongs = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSongs",null)
            val typeToken = object :TypeToken<ArrayList<Music>>(){}.type // for conversion of json to our required type
            if(jsonString!= null){
                val data : ArrayList<Music> = GsonBuilder().create().fromJson(jsonString,typeToken)
                FavouriteActivity.favouriteSongs.addAll(data)
            }

//           //for retrieving playlist data using shared preferences
//           PlaylistActivity.musicPlayList = MusicPlaylist()
//           val jsonStringPLayList = editor.getString("MusicPlayList",null)
//            if(jsonStringPLayList!= null){
//              val dataPLayList : MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPLayList,MusicPlaylist::class.java)
//              PlaylistActivity.musicPlayList = dataPLayList
//            }
        }
        Log.e("data retrieved ","data retrieved for playlist ")

        binding.shuffleBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }
        binding.favBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavouriteActivity::class.java))
        }
        binding.playlistbtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlaylistActivity::class.java))
        }
        binding.navdrawer.setNavigationItemSelectedListener{
            when(it.itemId)
            {
                R.id.navFeedback -> startActivity(Intent(this,FeedBackActivity::class.java))
                R.id.navSettings -> startActivity(Intent(this,SettingsActivity::class.java))
                R.id.navAbout -> startActivity(Intent(this,AboutActivity::class.java))
                R.id.navExit -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("EXIT")
                        .setMessage("Do You want To Exit Application")
                        .setPositiveButton("Yes"){ _ , _  ->
                            exitApplication()
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
            true
        }
    }
    //For requesting permission
    private fun requestRuntimePermission() :Boolean{
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 13){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted",Toast.LENGTH_SHORT).show()
                initializeLayout()
            }
            else
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
        }

    }

    //for navigation drawer item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initializeLayout(){
        MusicListMA = getAllAudio()
        // will create only that much object which are required will save the memory efficiently (L106)
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
        binding.musicRV.adapter = musicAdapter
        binding.totalsongs.text  = "Total Songs : "+musicAdapter.itemCount // counting total songs from adapter
    }
    @SuppressLint("Recycle", "Range")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio(): ArrayList<Music>{
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC +  " != 0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)

        //cursor is used to access any type of file from the storage
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,selection,null,
            MediaStore.Audio.Media.DATE_ADDED +  " DESC", null)
        if(cursor != null){
            if(cursor.moveToFirst())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                    val music = Music(id = idC, title = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC,
                        artUri = artUriC)
                    // checking whether the file exists or not
                    val file = File(music.path)
                    if(file.exists())
                        tempList.add(music)
                }while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!PlayerActivity.isPlaying && PlayerActivity.musicService!= null){
            exitApplication()
        }

    }

    override fun onResume() {
        super.onResume()
        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favouriteSongs)
        editor.putString("FavouriteSongs",jsonString)
//        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlayList)
//        editor.putString("MusicPLayList",jsonStringPlaylist)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // for setting theme in nav header
        findViewById<LinearLayout>(R.id.linearLayoutNVH)?.setBackgroundResource(currentGradient[themeIndex])
        return super.onCreateOptionsMenu(menu)
    }

}