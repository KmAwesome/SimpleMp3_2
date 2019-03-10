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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MusicController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private final String TAG = "MusicController";
    private Context context;
    private MusicService musicService;
    private MainActivity mainActivity;
    private Handler musicPlayHandler;

    public MusicController(Context context) {
        this.context = context;
        musicPlayHandler = new Handler();
        this.mainActivity = (MainActivity)context;
        new BindMusicService();
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
                break;
        }
    }

    public void playSong(){
        musicService.playSong();
        musicPlayHandler.post(mp3Start);
    }

    public void goSong(){
        musicService.go();
    }

    public void pauseSong(){
        musicService.pausePlayer();
    }

    public void playPrev(){
        musicService.playPrev();
    }

    public void playNext(){
        musicService.playNext();
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

    public void setSongList(ArrayList<Song> songs) {
        musicService.setSongList(songs);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        musicService.seek(mainActivity.mp3SeekBar.getProgress());
    }

    private Runnable mp3Start = new Runnable() {
        @Override
        public void run() {
            if (musicService.isPlaying()) {
                mainActivity.mp3SeekBar.setMax(musicService.getDur() - 999);
                Message msg = musicPlayHandler.obtainMessage();
                msg.arg1 = musicService.getPosn();
                musicPlayHandler.sendMessage(msg);
                mainActivity.mp3SeekBar.setProgress(msg.arg1);
                mainActivity.imgbtn_playSong.setImageResource(R.drawable.notepause);
            }
            musicPlayHandler.postDelayed(mp3Start,50);
        }
    };

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
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    }

}

