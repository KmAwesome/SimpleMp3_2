package com.example.main.simplemp3_2.Models;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by main on 2018/2/16.
 */

public class Song implements Serializable {
    private long id;
    private String title, artist, path, album, style, date;
    private long duration;

    public Song(long songID, String songTtile, String songArtist, long songDuration, String songPath, String songAlbum, String songStyle, String songDate){
        id = songID;
        title = songTtile;
        artist = songArtist;
        duration = songDuration;
        path = songPath;
        album = songAlbum;
        style = songStyle;
        date = songDate;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public long getDuration(){
        return duration;
    }

    public String getPath(){return path;}

    public String getAlbum(){return album;}

    public String getStyle(){return style;}

    public String getDate() {
        return date;
    }
}
