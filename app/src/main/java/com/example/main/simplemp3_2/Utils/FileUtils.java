package com.example.main.simplemp3_2.Utils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Activity.EditMusicInfoActivity;
import com.example.main.simplemp3_2.Models.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FileUtils {
    private final static String TAG = "FileUtil";
    private final static String KEY_PLAY_LIST = "keyPlayList";
    private static String KEY_ADD_SONGS_IN_PLAY_LIST;

    public static void showMusicFileInfo(Context context, Song song) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Song", song);
        intent.putExtras(bundle);
        intent.setClass(context, EditMusicInfoActivity.class);
        context.startActivity(intent);
    }

    public static void deleteSongFile(Context context, String musicPath) {
        File file = new File(musicPath);
        if (file.exists()) {
            Log.i(TAG, "deleteSongFile: " + musicPath);
            file.delete();
            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "='" + musicPath + "'", null);
            ((MainActivity)context).refreshAllFragment();
        }
    }

    public static void deletePlayListData(Context context, String playListTitle) {
        ArrayList<String> playListArrayList = readPlayListData(context);
        if (playListArrayList.contains(playListTitle)) {
            playListArrayList.remove(playListTitle);
        }
       savePlayListData(context, playListArrayList);
    }

    public static void savePlayListData(Context context, ArrayList<String> playListArrayList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String playList = gson.toJson(playListArrayList);
        editor.putString(KEY_PLAY_LIST, playList);
        editor.apply();
        Log.i(TAG, "savePlayListData: size = " + playList);
    }

    public static ArrayList<String> readPlayListData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared Preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String playList = sharedPreferences.getString(KEY_PLAY_LIST, null );
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> playListArrayList = gson.fromJson(playList, type);
        if (playList == null) {
            playListArrayList = new ArrayList<>();
        }
        Log.i(TAG, "readPlayListData: size = " + playListArrayList.size());
        return playListArrayList;
    }

    public static void writeSongsToPlayListData(Context context, String playListTitle, ArrayList<String> songStringArrayList) {
        KEY_ADD_SONGS_IN_PLAY_LIST = playListTitle;
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String songsInPlayList = gson.toJson(songStringArrayList);
        editor.putString(KEY_ADD_SONGS_IN_PLAY_LIST, songsInPlayList);
        editor.apply();
        Log.i(TAG, "writeSongsToPlayListData: " + songsInPlayList);
    }

    public static ArrayList<String> readSongsFromPlayListDataByTitle (Context context, String playListTitle) {
        KEY_ADD_SONGS_IN_PLAY_LIST = playListTitle;
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared Preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String songsInPlayList = sharedPreferences.getString(KEY_ADD_SONGS_IN_PLAY_LIST, null );
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> songsInPlayListArrayList = gson.fromJson(songsInPlayList, type);
        Log.i(TAG, "readSongsFromPlayListDataByTitle: " + songsInPlayListArrayList);
        return songsInPlayListArrayList;

    }


}

