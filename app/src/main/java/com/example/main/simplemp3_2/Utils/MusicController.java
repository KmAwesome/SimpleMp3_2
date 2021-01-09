package com.example.main.simplemp3_2.Utils;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.Service.MusicService;
import java.util.ArrayList;

public class MusicController {
    private final static String TAG = "MusicController";
    private static MusicController instance;
    private static MusicService musicService;
    private static Context context;
    public static boolean isBind = false;

    private MusicController() {};

    public static MusicController getInstance(Context mContext) {
        if (instance == null) {
            Log.i(TAG, "getInstance: ");
            context = mContext;
            instance = new MusicController();
            instance.bindMusicService();
        }
        return instance;
    }

    public void bindMusicService() {
        Intent intent = new Intent(context, MusicService.class);
        context.bindService(intent, musicServiceConnection, context.BIND_AUTO_CREATE);
        context.startService(intent);
    }

    ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) iBinder;
            musicService = musicBinder.getService();
            if (context instanceof MainActivity) {
                ((MainActivity) context).initUserLayout();
            }
            isBind = true;
            Log.i(TAG, "onServiceConnected: " + isBind);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBind = false;
            Log.i(TAG, "onServiceDisconnected: " + isBind);
        }
    };

    public void playSong() {
        musicService.playSong();
    }

    public void pauseSong() {
        musicService.pauseSong();
    }

    public void stopSong() {
        musicService.stopSong();
    }

    public void continueSong() {
        musicService.continueSong();
    }

    public void prevSong() {
        musicService.prevSong();
    }

    public void nextSong() {
        musicService.nextSong();
    }

    public boolean isPlaying() {
        return musicService.isPlaying();
    }

    public void setSongIndex(int songIndex) {
        musicService.setSongIndex(songIndex);
    }

    public int getSongIndex() {
        return musicService.getSongIndex();
    }

    public void setSongPlayingPosition(int posn) {
        musicService.setSongPlayingPosition(posn);
    }

    public int getSongPlayingPosition() {
        return musicService.getSongPlayingPosition();
    }

    public Song getCurrentSong() {
        return musicService.getCurrentSong();
    }

    public void setSongList(ArrayList<Song> songs) {
        musicService.setSongList(songs);
    }

    public ArrayList<Song> getSongList() {
        return musicService.getSongList();
    }

    public int getSongDuration() {
        return musicService.getSongDuration();
    }

    public void setRepeatMode(View view) {
        musicService.setRepeatMode(view);
    }

    public void updateRepeatImgButtonView(View view) {
        musicService.updateRepeatImgButtonView(view);
    }

    public void setSongListShuffle() {
        musicService.setSongListShuffle();
    }

    public void updateWidget(String action) {
        musicService.updateWidget(action);
    }

    public void releaseMediaPlayer() {
        musicService.releaseMediaPlayer();
    }

    public void startForeground(Notification notification) {
        musicService.startForeground(1, notification);
    }

    public void stopForeground() {
        musicService.stopForeground(true);
    }

    public void unbindMusicService() {
        if (isBind) {
            context.unbindService(musicServiceConnection);
        }
    }
}

