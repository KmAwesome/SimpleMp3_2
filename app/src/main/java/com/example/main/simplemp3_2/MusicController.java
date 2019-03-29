package com.example.main.simplemp3_2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;

import com.example.main.simplemp3_2.Service.MusicService;

import java.util.ArrayList;
import static com.example.main.simplemp3_2.Service.MusicService.REPEAT;
import static com.example.main.simplemp3_2.Service.MusicService.REPEATONE;
import static com.example.main.simplemp3_2.Service.MusicService.SHUFFLE;

public class MusicController implements View.OnClickListener {
    private final String TAG = "MusicController";
    private Context context;
    public Handler musicPlayHandler = new Handler();
    private MusicService musicService;
    private MainActivity mainActivity;
    private boolean isBind;

    public MusicController(Context context) {
        this.context = context;
        this.mainActivity = (MainActivity)context;
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
            musicPlayHandler.post(mp3Start);
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

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

    private Runnable mp3Start = new Runnable() {
        @Override
        public void run() {
            if (isPlaying()) {
                mainActivity.mp3SeekBar.setMax(getSongDuration());
                mainActivity.mp3SeekBar.setProgress(getSongPlayingPos());
                mainActivity.imgbtn_playSong.setImageResource(R.drawable.btn_pause);
            }
            musicPlayHandler.postDelayed(mp3Start,50);
            updateRepeatImgButtonView();
        }
    };

    private void updateRepeatImgButtonView() {
        if (musicService.getRepeatMode().equals(REPEAT)) {
            mainActivity.imgbtn_repeat.setImageResource(R.drawable.btn_repeat_all);
        }else if (musicService.getRepeatMode().equals(REPEATONE)) {
            mainActivity.imgbtn_repeat.setImageResource(R.drawable.btn_repeat_one);
        }else if (musicService.getRepeatMode().equals(SHUFFLE)) {
            mainActivity.imgbtn_repeat.setImageResource(R.drawable.btn_repeat_shffle);
        }
    }

    public void setRepeatMode(View view) {
        if (view.getTag().equals(REPEAT)) {
            musicService.setRepeatMode(REPEAT);
            mainActivity.imgbtn_repeat.setImageResource(R.drawable.btn_repeat_all);
            view.setTag(REPEATONE);
        }else if (view.getTag().equals(REPEATONE)) {
            musicService.setRepeatMode(REPEATONE);
            mainActivity.imgbtn_repeat.setImageResource(R.drawable.btn_repeat_one);
            view.setTag(SHUFFLE);
        }else if (view.getTag().equals(SHUFFLE)) {
            musicService.setRepeatMode(SHUFFLE);
            mainActivity.imgbtn_repeat.setImageResource(R.drawable.btn_repeat_shffle);
            view.setTag(REPEAT);
        }
    }

    public void playSong(){
        musicService.playSong();
        musicPlayHandler.post(mp3Start);
    }

    public void playPrev(){
        musicService.playPrev();
        musicPlayHandler.post(mp3Start);
    }

    public void playNext(){
        musicService.playNext();
        musicPlayHandler.post(mp3Start);
    }

    public void goSong(){
        musicService.go();
        musicPlayHandler.post(mp3Start);
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

    public void removeHandlerCallback() {
        if (musicPlayHandler != null) {
            musicPlayHandler.removeCallbacks(mp3Start);
        }
    }

    public void unbindMusicService() {
        if (isBind) {
            context.unbindService(musicServiceConnection);
        }
    }

}

