package com.example.main.simplemp3_2.Model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class SongPlayList implements Serializable{
    private static String TAG = "SongPlayList";
    private String title;
    private ArrayList<String> addToSongList;

    public SongPlayList() {
        addToSongList = new ArrayList<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return  title;
    }

    public void addToList(ArrayList<String> addsongList) {
        for(String s: addsongList) {
            if (!addToSongList.contains(s)) addToSongList.add(s);
        }
        Log.i(TAG, "addToList: " + addToSongList.size());
    }

    public ArrayList<String> getPlayListSong() {
        return addToSongList;
    }

}
