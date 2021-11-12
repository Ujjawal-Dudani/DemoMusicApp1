package com.notification.demomusicapp

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

/*we have created a class of service.
* services are used to run process in background and foreground
*  There is a on bind method which is used to bind service to any activity
* We have created a inner class (MyBinder) which help us to return object of MainClass */
class MusicService : Service() ,AudioManager.OnAudioFocusChangeListener {
    private var myBinder =MyBinder()
    var mediaPlayer : MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable // runnable is a thing which is used to run a code multiple times
    lateinit var audioManager: AudioManager


    override fun onBind(intent: Intent?): IBinder {
       mediaSession = MediaSessionCompat(baseContext,"My Music")
        return  myBinder
    }
    inner class MyBinder :Binder(){
        fun currentService (): MusicService {
            return this@MusicService
        }
    }
    fun showNotification(playPauseBtn:Int){

        val intent = Intent(baseContext,MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this,0,intent,0)

        val prevIntent =Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent =Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent =Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent =Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)


        val imgArt = getImageArt(PlayerActivity.musiclistPA[PlayerActivity.songPosition].path)
        val image = if(imgArt != null ) {
            BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
        }
        else{
            BitmapFactory.decodeResource(resources,R.drawable.splash_screen)
        }


        val notification = NotificationCompat.Builder(baseContext,ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerActivity.musiclistPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musiclistPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_icon,"Previous",prevPendingIntent)
            .addAction(playPauseBtn,"Play",playPendingIntent)
            .addAction(R.drawable.next_icon,"Next",nextPendingIntent)
            .addAction(R.drawable.exit_icon,"Exit",exitPendingIntent)
            .build()
        startForeground(13,notification)

    }

     fun createMediaPlayer(){
        try{
            // to play any audio or media file android has a built in class media player
            if(PlayerActivity.musicService!!.mediaPlayer==null) PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()

            PlayerActivity.musicService!!.mediaPlayer!!.reset() //This Two !! marks denotes object is not null for this time

            //for setting path
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musiclistPA[PlayerActivity.songPosition].path)

            //for preparing player
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()

            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)

            //for notification icon
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)

            //for seekbar text_views setting
            PlayerActivity.binding.tvSeekBarStart.text = format(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text = format(mediaPlayer!!.duration.toLong())

            //when seek bar starts initializing and ending
            PlayerActivity.binding.seekBarPA.progress = 0
            PlayerActivity.binding.seekBarPA.max = mediaPlayer!!.duration

            PlayerActivity.nowPlayingId = PlayerActivity.musiclistPA[PlayerActivity.songPosition].id

        }
        catch (e:Exception){ return }
    }

    fun seekBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.tvSeekBarStart.text = format(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200) //Handler describes us after how much time the code should run

        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0) // it ensures the starting of inside handler
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange <=0 )
        {
            //pause music
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
            NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
            showNotification(R.drawable.play_icon)
            PlayerActivity.isPlaying = false
            PlayerActivity.musicService!!.mediaPlayer!!.pause()
        }
        else{
            //play music
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
            showNotification(R.drawable.pause_icon)
            PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
        }
    }
}