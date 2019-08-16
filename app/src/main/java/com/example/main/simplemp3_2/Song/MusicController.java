package com.example.main.simplemp3_2.Song;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Service.MusicService;
import java.util.ArrayList;

public class MusicController implements MusicControl {
    private final String TAG = "MusicController";
    private MusicService musicService;
    private Context context;
    private static boolean isBind = false;

    public MusicController(Context context) {
        this.context = context;
        bindMusicService();
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
            musicService.updateActivityUi(MusicService.ACTION_START);
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBind = false;
        }
    };

    @Override
    public void playSong() {
        musicService.playSong();
        if (context instanceof MainActivity) {
            ((MainActivity) context).controlLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void pauseSong() {
        musicService.pauseSong();
    }

    @Override
    public void continueSong() {
        musicService.continueSong();
        if (context instanceof MainActivity) {
            ((MainActivity) context).controlLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void prevSong() {
        musicService.prevSong();
    }

    @Override
    public void nextSong() {
        musicService.nextSong();
    }

    @Override
    public boolean isPlaying() {
        return musicService.isPlaying();
    }

    @Override
    public void setSongIndex(int songIndex) {
        musicService.setSongIndex(songIndex);
    }

    @Override
    public int getSongIndex() {
        return musicService.getSongIndex();
    }

    @Override
    public void setSongPlayingPosition(int posn) {
        musicService.setSongPlayingPosition(posn);
    }

    @Override
    public int getSongPlayingPosition() {
        return musicService.getSongPlayingPosition();
    }

    @Override
    public void setSongList(ArrayList<Song> songs) {
        musicService.setSongList(songs);
    }

    @Override
    public ArrayList<Song> getSongList() {
        return musicService.getSongList();
    }

    @Override
    public int getSongDuration() {
        return musicService.getSongDuration();
    }

    @Override
    public void setRepeatMode(View view) {
        musicService.setRepeatMode(view);
    }

    @Override
    public void updateRepeatImgButtonView(View view) {
        musicService.updateRepeatImgButtonView(view);
    }

    @Override
    public void setSongListShuffle() {
        musicService.setSongListShuffle();
    }

    @Override
    public void updateWidget(String action) {
        musicService.updateWidget(action);
    }

    @Override
    public void releaseMediaPlayer() {
        musicService.releaseMediaPlayer();
    }

    public void unbindMusicService() {
        if (isBind) {
            context.unbindService(musicServiceConnection);
        }
    }
}

