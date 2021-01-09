package com.example.main.simplemp3_2.Song;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.main.simplemp3_2.Models.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class InitSongList {
    public final static String musicFilter = "MUSICFILTER";
    public final static String sortString = "SORTSTRING";
    private final String TAG = "InitSongList";
    private final static String time = "TIME";
    private final static String sort = "SORT";
    private String string;
    private static int filterTime, sortSelect;
    private Context context;
    private SharedPreferences sharedPreferences;
    private ArrayList<Song> songlist;

    public InitSongList(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(musicFilter, Context.MODE_PRIVATE);
        songlist = new ArrayList<>();
        initSongList();
    }

    public void initSongList() {

        readData();

        if (songlist != null){
            songlist.clear();
        }

        ContentResolver musicResolver = context.getContentResolver();
        Cursor musicCursor = musicResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                try{
                    long thisID = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    long thisDuraion = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String thispath = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String thisStyle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK));
                    String thisDate = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));

                    if (thisTitle == null) {
                        thisTitle = "<unknow>";
                    }

                    if (thisArtist == null) {
                        thisArtist = "<unknow>";
                    }

                    if (thisAlbum == null) {
                        thisAlbum = "<unknow>";
                    }

                    if (thisStyle == null || thisStyle.equals("")) {
                        thisStyle = "<unknow>";
                    }

                    int time = (int) TimeUnit.MILLISECONDS.toSeconds(thisDuraion);

                    if (time > filterTime) {
                        songlist.add(new Song(thisID, thisTitle, thisArtist, thisDuraion, thispath, thisAlbum, thisStyle, thisDate));
                    }

                }catch (Exception e){e.printStackTrace();}
            }
            while (musicCursor.moveToNext());
        }

        if (sortSelect == 0) {
            sortByDefalut();
        }else if (sortSelect == 1) {
            sortByDate();
        }
    }

    public void setSongList(ArrayList<Song> songs) {
        songlist = songs;
    }

    public ArrayList<Song> getSongList() {
        initSongList();
        return songlist;
    }

    public void sortByDefalut() {
        sortSelect = 0;
        string = "默認";
        Collections.sort(songlist, new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

    public void sortByDate() {
        sortSelect = 1;
        string = "日期";
        Collections.sort(songlist, new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return b.getDate().compareTo(a.getDate());
            }
        });
    }

    public void setFilterTime(int filterTime) {
        this.filterTime = filterTime;
    }

    public void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(time, filterTime);
        editor.putInt(sort, sortSelect);
        editor.putString(sortString, string);
        editor.apply();
    }

    public void readData() {
        filterTime = sharedPreferences.getInt(time, 60);
        sortSelect = sharedPreferences.getInt(sort, 0);
    }

    public String getSortName() {
        return sharedPreferences.getString(sortString, "排列");
    }

    public String getFilterTime() {
        String string = "";
        switch (filterTime) {
            case 0:
                string = "不過濾";
                break;
            case 15:
                string = "15秒";
                break;
            case 30:
                string = "30秒";
                break;
            case 60:
                string = "1分鐘";
                break;
            case 90:
                string = "1分30秒";
                break;
            case 120:
                string = "2分鐘";
                break;
        }
        return string;
    }


}
