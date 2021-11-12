package com.notification.demomusicapp

import android.media.MediaMetadataRetriever
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

// it is like normal class but does not have methods only variables used for storing.
data class Music(val id:String, val title:String, val album:String, val artist:String, val duration:Long =0, val path:String,val artUri:String)

// it will represent a single playlist
class Playlist{
    lateinit var name : String
    lateinit var playlist : ArrayList<Music>
    lateinit var createdBy : String
    lateinit var createdOn : String
}

//this Will refer to playlist class
class MusicPlaylist{
    var ref : ArrayList<Playlist> = ArrayList() // for handling json type conversions
}
// This function is used for time Duration
fun format(duration: Long):String{
    val minutes =TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val seconds =(TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)-
            // the minutes which are already accessed will be converted
             minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))
    //02 represents only two digits
    //d represents int value
    return String.format("%02d :%02d",minutes,seconds)
}
fun getImageArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}
 fun setSongPosition(increment: Boolean){
    if(!PlayerActivity.repeat){
        if(increment)
        {
            if(PlayerActivity.musiclistPA.size-1 == PlayerActivity.songPosition)
                PlayerActivity.songPosition =0
            else ++PlayerActivity.songPosition

        }
        else
        {
            if(PlayerActivity.songPosition ==0)
                PlayerActivity.musiclistPA.size-1
            else --PlayerActivity.songPosition
        }
    }

}
fun exitApplication(){
    if(PlayerActivity.musicService!= null)
    {
        PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release() //releases occupied resources
        PlayerActivity.musicService = null
    }
    exitProcess(1)
}

fun favouriteChecker(id:String) : Int{
    FavouriteActivity.favouriteSongs.forEachIndexed { index, music ->
        PlayerActivity.isFavourite = false
        if(id ==music.id)
        {
            PlayerActivity.isFavourite =true
            return index
        }
    }
    return -1
}
fun checkPlaylist(playlist: ArrayList<Music>): ArrayList<Music>{
    playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if(!file.exists())
            playlist.removeAt(index)
    }
    return playlist
}
