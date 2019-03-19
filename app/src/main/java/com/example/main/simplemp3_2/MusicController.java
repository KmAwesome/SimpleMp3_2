package com.example.main.simplemp3_2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import com.example.main.simplemp3_2.Service.MusicService;
import java.io.Serializable;
import java.util.ArrayList;
import static com.example.main.simplemp3_2.Service.MusicService.REPEAT;
import static com.example.main.simplemp3_2.Service.MusicService.REPEATONE;
import static com.example.main.simplemp3_2.Service.MusicService.SHUFFLE;

public class MusicController implements View.OnClickListener {
    private final String TAG = "MusicController";
    private Context context;
    private MusicService musicService;
    private MainActivity mainActivity;
    public BindMusicService bindMusicService;

    public MusicController(Context context) {
        this.context = context;
        this.mainActivity = (MainActivity)context;
        bindMusicService = new BindMusicService();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgbtn_play:
                if (musicService.getPosn() == 0) {
                    playSong();
                }else if (musicService.isPlaying()) {
                    pauseSong();
                }else {
                    goSong();
                }
                break;
            case R.id.imgbtn_next:
                playNext();
                break;
            case R.id.imgbtn_repeat:
                setRepeatMode(view);
                break;
        }
    }

    public void playSong(){
        musicService.playSong();
        mainActivity.startSeekBar();
    }

    public void playPrev(){
        musicService.playPrev();
        mainActivity.startSeekBar();
    }

    public void playNext(){
        musicService.playNext();
        mainActivity.startSeekBar();
    }

    public void setRepeatMode(View view) {
        if (view.getTag().equals(REPEAT)) {
            musicService.setRepeatMode(REPEAT);
            mainActivity.imgbtn_repeat.setImageResource(R.drawable.repeat);
            view.setTag(REPEATONE);
        }else if (view.getTag().equals(REPEATONE)) {
            musicService.setRepeatMode(REPEATONE);
            mainActivity.imgbtn_repeat.setImageResource(R.drawable.repeat_one);
            view.setTag(SHUFFLE);
        }else if (view.getTag().equals(SHUFFLE)) {
            musicService.setRepeatMode(SHUFFLE);
            mainActivity.imgbtn_repeat.setImageResource(R.drawable.shffle);
            view.setTag(REPEAT);
        }
    }

    public void goSong(){
        musicService.go();
        mainActivity.startSeekBar();
    }

    public void pauseSong(){
        musicService.pausePlayer();
    }

    public boolean isPlaying() {
        return musicService.isPlaying();
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

    public void setSongList(ArrayList<Song> songs) {
        musicService.setSongList(songs);
    }

    public int getSongDuration() {
        return musicService.getDur();
    }

    class BindMusicService implements ServiceConnection {

        public BindMusicService() {
            Intent intent = new Intent(context, MusicService.class);
            context.bindService(intent, this, Context.BIND_AUTO_CREATE);
            context.startService(intent);
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) iBinder;
            musicService = musicBinder.getService();
            mainActivity.startSeekBar();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }

    }

}

