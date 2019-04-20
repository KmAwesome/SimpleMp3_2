package com.example.main.simplemp3_2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.main.simplemp3_2.Service.MusicService;

import java.util.ArrayList;
import static com.example.main.simplemp3_2.Service.MusicService.REPEAT;
import static com.example.main.simplemp3_2.Service.MusicService.REPEATONE;
import static com.example.main.simplemp3_2.Service.MusicService.SHUFFLE;
import static com.example.main.simplemp3_2.Service.MusicService.repeatMode;

public class MusicController {
    private final String TAG = "MusicController";
    private Context context;
    private MusicService musicService;
    private boolean isBind = false;
    private Toast toast;

    public MusicController(Context context) {
        this.context = context;
        bindMusicService();
        toast = new Toast(context);
    }

    public void bindMusicService() {
        Log.i(TAG, "bindMusicService: " + isBind);
        Intent intent = new Intent(context, MusicService.class);
        context.bindService(intent, musicServiceConnection, context.BIND_AUTO_CREATE);
        context.startService(intent);
    }

    ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) iBinder;
            musicService = musicBinder.getService();
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void playSong(){
        musicService.playSong();
    }

    public void playPrev(){
        musicService.playPrev();
    }

    public void playNext(){
        musicService.playNext();
    }

    public void goSong(){
        musicService.go();
    }

    public void pauseSong(){
        musicService.pausePlayer();
    }

    public boolean isPlaying() {
        return musicService.isPlaying();
    }

    public void setSongList(ArrayList<Song> songs) {
        musicService.setSongList(songs);
    }

    public ArrayList<Song> getSongList() {
        return musicService.getSonglist();
    }

    public void setSongPos(int pos){
        musicService.setSongPos(pos);
    }

    public int getSongPos() {
        return musicService.getSongPos();
    }

    public void setSongPlayingPos(int posn) {
        musicService.setPosn(posn);
    }

    public int getSongPlayingPos() {
        return musicService.getPosn();
    }

    public int getSongDuration() {
        return musicService.getDur();
    }

    public void setRepeatMode() {
        if (repeatMode.equals(REPEAT)) {
            repeatMode = REPEATONE;
            toast.cancel();
            toast = Toast.makeText(context, "單曲循環", Toast.LENGTH_SHORT);
            toast.show();
        }else if (repeatMode.equals(REPEATONE)) {
            toast.cancel();
            toast = Toast.makeText(context, "隨機播放", Toast.LENGTH_SHORT);
            toast.show();
            repeatMode = SHUFFLE;
        }else if (repeatMode.equals(SHUFFLE)) {
            toast.cancel();
            toast = Toast.makeText(context, "循環播放", Toast.LENGTH_SHORT);
            toast.show();
            repeatMode = REPEAT;
        }
    }

    public void unbindMusicService() {
        if (isBind) {
            context.unbindService(musicServiceConnection);
        }
    }

}

