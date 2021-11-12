package com.notification.demomusicapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.notification.demomusicapp.databinding.ActivityPlayerBinding


class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var musiclistPA: ArrayList<Music>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
        var min1: Boolean = false
        var min5: Boolean = false
        var min10: Boolean = false
        var nowPlayingId:String =""
        var isFavourite:Boolean=false
        var fIndex:Int =-1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DemoMusicApp) // changing the theme of the activity by default theme provided by the android studio.
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initializeLayout()

        binding.backBtnPA.setOnClickListener {
            finish()
        }

        binding.playPauseBtnPA.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()
        }

        binding.previousBtnPA.setOnClickListener {
            prevNextSong(false)
        }

        binding.nextBtnPA.setOnClickListener {
            prevNextSong(true)
        }

        binding.seekBarPA.setOnSeekBarChangeListener(@SuppressLint("AppCompatCustomView")
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekbar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            //Unit is written when nothing should be happen on execution of function
            //L63 function is used when user clicks on this
            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            //L65 function is used when user releases clicks on this
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        }
        )
        binding.repeatBtnPA.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_green))
            } else {
                repeat = false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
            }
        }

        binding.equalizerBtnPA.setOnClickListener {
            try {
                val eqIntent =
                    Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL) // will generate built in control panel
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayer!!.audioSessionId
                )
                // package name indicates for which application the music should be changed
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 12)
            } catch (e: Exception) {
                Toast.makeText(this, "Equalizer not Supported", Toast.LENGTH_SHORT).show()
            }
        }
        binding.timerBtnPA.setOnClickListener {
            val timer = min1 || min5 || min10
            if (!timer) showBottomNavigationSheet()
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Do You want To Stop?")
                    .setPositiveButton("Yes") { _, _ ->
                        min1 = false
                        min5 = false
                        min10 = false
                        binding.timerBtnPA.setColorFilter(
                            ContextCompat.getColor(
                                this,
                                R.color.cool_pink
                            )
                        )
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }
        binding.shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"    //  /*  it means any type of file will be there but the type should be audio
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musiclistPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music  File"))
        }
        binding.favouriteBtnPA.setOnClickListener {
            if(isFavourite)
            {
                isFavourite = false
                binding.favouriteBtnPA.setImageResource(R.drawable.favorite_empty_icon)
                FavouriteActivity.favouriteSongs.removeAt(fIndex)
            }
            else{
                isFavourite = true
                binding.favouriteBtnPA.setImageResource(R.drawable.favorite_icon)
                FavouriteActivity.favouriteSongs.add(musiclistPA[songPosition])
            }
        }
    }


     private fun setLayout() {
         fIndex = favouriteChecker(musiclistPA[songPosition].id)
        //for image loading
        Glide.with(this)
            .load(musiclistPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(binding.songImgPA)

        binding.songNamePA.text = musiclistPA[songPosition].title

        if (repeat) binding.repeatBtnPA.setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.cool_green
            )
        )
        if (min1 || min5 || min10) binding.timerBtnPA.setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.purple_500
            )
        )
         if(isFavourite) binding.favouriteBtnPA.setImageResource(R.drawable.favorite_icon)
         else binding.favouriteBtnPA.setImageResource(R.drawable.favorite_empty_icon)
    }

    private fun createMediaPlayer() {
        try {
            // to play any audio or media file android has a built in class media player
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()

            musicService!!.mediaPlayer!!.reset() //This Two !! marks denotes object is not null for this time

            //for setting path
            musicService!!.mediaPlayer!!.setDataSource(musiclistPA[songPosition].path)

            //for preparing player
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true

            binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            //for notification icon
            musicService!!.showNotification(R.drawable.pause_icon)

            //for seekbar text_views setting
            binding.tvSeekBarStart.text =
                format(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text = format(musicService!!.mediaPlayer!!.duration.toLong())

            //when seek bar starts initializing and ending
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)

            nowPlayingId = musiclistPA[songPosition].id


        } catch (e: Exception) {
            return
        }
    }

    private fun initializeLayout() {
        //catching intent
        songPosition = intent.getIntExtra("index", 0)

        //for so many classes
        when (intent.getStringExtra("class")) {

            "FavouriteAdapter" ->{
                val intent = Intent(this@PlayerActivity, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA = ArrayList()
                musiclistPA.addAll(FavouriteActivity.favouriteSongs) // contents of musiclistma will load in musiclistpa
                setLayout()
            }

            "NowPlaying" -> {
                setLayout()
                binding.tvSeekBarStart.text = format(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvSeekBarEnd.text = format(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
                if(isPlaying) binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
                else binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
            }
            "MusicAdapter" -> {

                //for starting service
                val intent = Intent(this@PlayerActivity, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA = ArrayList()
                musiclistPA.addAll(MainActivity.MusicListMA) // contents of musiclistma will load in musiclistpa
                setLayout()
            }
            "MainActivity" -> {

                //for starting service
                val intent = Intent(this@PlayerActivity, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA = ArrayList()
                musiclistPA.addAll(MainActivity.MusicListMA)
                musiclistPA.shuffle()
                setLayout()
            }

            "FavouriteShuffle" ->{
                //for starting service
                val intent = Intent(this@PlayerActivity, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA = ArrayList()
                musiclistPA.addAll(FavouriteActivity.favouriteSongs)
                musiclistPA.shuffle()
                setLayout()

            }

            "PlayListDetailsAdapter" ->{
                //for starting service
                val intent = Intent(this@PlayerActivity, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA = ArrayList()
                musiclistPA.addAll(PlaylistActivity.musicPlayList.ref[PlaylistDetails.currentPlayListPos].playlist)
                setLayout()

            }
            "PlayListDetailsShuffle" ->{
                //for starting service
                val intent = Intent(this@PlayerActivity, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musiclistPA = ArrayList()
                musiclistPA.addAll(PlaylistActivity.musicPlayList.ref[PlaylistDetails.currentPlayListPos].playlist)
                musiclistPA.shuffle()
                setLayout()

            }
        }
    }

    private fun playMusic() {
        binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(false)
            setLayout()
            createMediaPlayer()
        }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 || resultCode == RESULT_OK)
            return
    }

    private fun showBottomNavigationSheet() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottom_sheet_layout)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_1)?.setOnClickListener {
            Toast.makeText(baseContext, "Music Will Stop After  1 Minute", Toast.LENGTH_SHORT)
                .show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min1 = true
            Thread {
                Thread.sleep((60000).toLong())
                if (min1) exitApplication()
            }.start()

            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_5)?.setOnClickListener {
            Toast.makeText(baseContext, "Music Will Stop After  5 Minutes", Toast.LENGTH_SHORT)
                .show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min5 = true
            Thread {
                Thread.sleep((5 * 60000).toLong())
                if (min5) exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_10)?.setOnClickListener {
            Toast.makeText(baseContext, "Music Will Stop After  10 Minutes", Toast.LENGTH_SHORT)
                .show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min10 = true
            Thread {
                Thread.sleep((10 * 60000).toLong())
                if (min10) exitApplication()
            }.start()
            dialog.dismiss()
        }
    }
}