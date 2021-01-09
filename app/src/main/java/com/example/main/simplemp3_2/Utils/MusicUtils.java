package com.example.main.simplemp3_2.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringDef;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Models.Song;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class MusicUtils {
    private static final String TAG = "MusicUtils";
    public final static String TYPE_ARTIST = "typeArtist";
    public final static String TYPE_ALBUM = "typeAlbum";
    public final static String TYPE_TITLE = "typeTitle";
    public final static String TYPE_PATH = "typePath";
    public final static String TYPE_STYLE = "typeStyle";
    @StringDef({TYPE_ALBUM, TYPE_ARTIST, TYPE_TITLE, TYPE_PATH, TYPE_STYLE})
    public @interface Type{}
    private static ArrayList<Song> songList;
    private static ArrayList<Song> displaySongList;
    private static int songDurationInSeconds = 60;

    public static void renameAllSongsInFolder(Context context, @Type String type, ArrayList<Song> songArrayList, String title) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        for (Song song : songArrayList) {
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.getId());
            values.put(MediaStore.Audio.Media.IS_PENDING, 1);
            switch (type) {
                case TYPE_ARTIST:
                    values.put(MediaStore.Audio.Media.ARTIST, title);
                    break;
                case TYPE_ALBUM:
                    values.put(MediaStore.Audio.Media.ALBUM, title);
                    break;
                case TYPE_STYLE:
                    values.put(MediaStore.Audio.Media.BOOKMARK, title);
                    break;
            }
            contentResolver.update(uri, values, null,null);
            values.put(MediaStore.Audio.Media.IS_PENDING, 0);
            contentResolver.update(uri, values, null,null);
            MusicUtils.updateSongList(context);
            Log.i(TAG, "renameAllSongsInFolder: " + song.getAlbum() + " " + title);
        }
    }

    public static void renameAllSongsInFolderBeforeQ(Context context, @Type String type, ArrayList<Song> songArrayList, String title) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        for (Song song : songArrayList) {
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.getId());
            switch (type) {
                case TYPE_ARTIST:
                    values.put(MediaStore.Audio.Media.ARTIST, title);
                    break;
                case TYPE_ALBUM:
                    values.put(MediaStore.Audio.Media.ALBUM, title);
                    break;
            }
            contentResolver.update(uri, values, null,null);
            MusicUtils.updateSongList(context);
        }
    }

    public static ArrayList<String> getStringListByType(Context context, @Type String type) {
        if (songList == null) {
            //songList = getSongList(context);
            songList = new ArrayList<>();
        }
        HashSet<String> stringHashSet = new HashSet<>();
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (Song song : songList) {
            switch (type) {
                case TYPE_ARTIST:
                    stringHashSet.add(song.getArtist());
                    break;
                case TYPE_ALBUM:
                    stringHashSet.add(song.getAlbum());
                    break;
                case TYPE_TITLE:
                    stringHashSet.add(song.getTitle());
                    break;
                case TYPE_PATH:
                    stringHashSet.add(song.getPath());
                    break;
                case TYPE_STYLE:
                    stringHashSet.add(song.getStyle());
                    break;
            }
        }
        stringArrayList.addAll(stringHashSet);
        Log.i(TAG, "getStringListByType: Type : " + type + " StringListSize : " + stringArrayList.size());
        return stringArrayList;
    }

    public static ArrayList<Song> getSongListByTitle(Context context, String title) {
        if (songList == null) {
            songList = getSongList(context);
        }
        ArrayList<Song> songArrayList = new ArrayList<>();
        for (Song song : songList){
            if (song.getArtist().equals(title)) {
                songArrayList.add(song);
            }else if (song.getAlbum().equals(title)) {
                songArrayList.add(song);
            }else if (song.getTitle().equals(title)) {
                songArrayList.add(song);
            }else if (song.getPath().equals(title)) {
                songArrayList.add(song);
            }else if (song.getStyle().equals(title)) {
                songArrayList.add(song);
            }
        }
        Log.i(TAG, "getSongListByTitle Size : " + songArrayList.size());
        return songArrayList;
    }

    public static ArrayList<Song> getSongListByTitleContainTypes(Context context, String title, @Type String titleType) {
        if (songList == null) {
            songList = getSongList(context);
        }
        ArrayList<Song> songArrayList = new ArrayList<>();
        for (Song song : songList) {
            switch (titleType) {
                case TYPE_ARTIST:
                    if (song.getArtist().equals(title))
                        songArrayList.add(song);
                    break;
                case TYPE_ALBUM:
                    if (song.getAlbum().equals(title))
                        songArrayList.add(song);
                    break;
                case TYPE_TITLE:
                    if (song.getTitle().equals(title))
                        songArrayList.add(song);
                    break;
                case TYPE_PATH:
                    if (song.getPath().equals(title))
                        songArrayList.add(song);
                    break;
                case TYPE_STYLE:
                    if (song.getStyle().equals(title))
                        songArrayList.add(song);
                    break;
            }
        }
        Log.i(TAG, "getSongListByTitleContainTypes Size : " + songArrayList.size());
        return songArrayList;
    }

    public static ArrayList<Song> getSongListContainTypeTitles(Context context, ArrayList<String> songTitles) {
        if (songList == null) {
            songList = getSongList(context);
        }
        ArrayList<Song> songArrayList = new ArrayList<>();
        for (Song song : songList) {
            if (songTitles.contains(song.getTitle())) {
                songArrayList.add(song);
            }else if (songTitles.contains(song.getArtist())) {
                songArrayList.add(song);
            }else if (songTitles.contains(song.getAlbum())) {
                songArrayList.add(song);
            }
        }
        Log.i(TAG, "getSongListContainTypeTitles: " + songArrayList.size());
        return songArrayList;
    }

    public static ArrayList<Song> getSongList(Context context) {
        updateSongList(context);
        return songList;
    }

    public static void setDisplaySongList(ArrayList<Song> songArrayList) {
        displaySongList = songArrayList;
    }

    public static ArrayList<Song> getDisplaySongList() {
        return displaySongList;
    }

    public static void setSongDurationInSeconds(int time) {
        songDurationInSeconds = time;
    }

    public static void updateSongList(Context context) {
        musicCursor(context);
    }

    private static void musicCursor(Context context) {
        if (songList == null) {
            songList = new ArrayList<>();
        } else {
            songList.clear();
        }
        ContentResolver musicResolver = context.getContentResolver();
        Cursor musicCursor = musicResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null , null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                try{
                    long thisID = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    long thisDuraion = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String thispath = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String thisStyle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK));
                    String thisDate = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
                    File file = new File(thispath);
                    thispath = file.getParentFile().getName();
                    if (thisTitle == null) { thisTitle = "<unknow>"; }
                    if (thisArtist == null) { thisArtist = "<unknow>"; }
                    if (thisAlbum == null) { thisAlbum = "<unknow>"; }
                    if (thisStyle == null || thisStyle.equals("")) { thisStyle = "<unknow>"; }
                    Song song = new Song(thisID, thisTitle, thisArtist, thisDuraion, thispath, thisAlbum, thisStyle, thisDate);
                    int timeInSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(thisDuraion);
                    if (timeInSeconds >= songDurationInSeconds) {
                        songList.add(song);
                    }
                }catch (Exception e){e.printStackTrace();}
            } while (musicCursor.moveToNext());
        }
        Log.i(TAG, "musicCursor: " + songList.size());
    }

}
