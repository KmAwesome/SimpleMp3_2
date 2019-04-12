package com.example.main.simplemp3_2;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class InitSongList {
    private final String TAG = "InitSongList";
    private Context context;
    private ArrayList<Song> songlist;

    public InitSongList(Context context) {
        this.context = context;
        songlist = new ArrayList<>();
        initSongList();
    }

    public void initSongList() {
        if (songlist != null){
            songlist.clear();
        }

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                try{
                    String thispath = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    long thisID = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String thisStyle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK));
                    long thisDuraion = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                    if (thisTitle == null){
                        thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    }

                    if (thisArtist == null){
                        thisArtist = "<unknow>";
                    }

                    if (thisAlbum == null){
                        thisAlbum = "<unknow>";
                    }

                    int time = (int) TimeUnit.MILLISECONDS.toSeconds(thisDuraion);

                    if (time > 60) {
                        songlist.add(new Song(thisID, thisTitle, thisArtist, thisDuraion, thispath, thisAlbum,thisStyle));
                    }

                }catch (Exception e){e.printStackTrace();}
            }
            while (musicCursor.moveToNext());
        }

        Collections.sort(songlist, new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }


    public void setSongList(ArrayList<Song> songs) {
        songlist = songs;
    }

    public ArrayList<Song> getSongList() {
        return songlist;
    }

}
