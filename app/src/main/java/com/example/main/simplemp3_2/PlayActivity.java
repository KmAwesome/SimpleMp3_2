package com.example.main.simplemp3_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.example.main.simplemp3_2.Service.MusicService.PAUSE;
import static com.example.main.simplemp3_2.Service.MusicService.PLAY;
import static com.example.main.simplemp3_2.Service.MusicService.REPEAT;
import static com.example.main.simplemp3_2.Service.MusicService.REPEATONE;
import static com.example.main.simplemp3_2.Service.MusicService.SHUFFLE;
import static com.example.main.simplemp3_2.Service.MusicService.repeatMode;

public class PlayActivity extends AppCompatActivity {
    private final String TAG = "PlayActivity";
    private MusicController musicController;
    private AudioManager audioManager;
    private static Handler musicHandler = new Handler();
    private IntentFilter updateUIfilter;
    private ImageButton btnPlay, btnPlayNext, btnPlayPrev, btnRepeatMode, btnFavorite;
    private TextView txvSongTitle, txvSongArtist, txvPlayingTime, txvSongDuration, txvSongNum;
    private SeekBar seekBarSongProgress, seekBarValueControl;
    private int currentVolume;

    private void initUpdateUiReceiver() {
        updateUIfilter = new IntentFilter();
        updateUIfilter.addAction(PLAY);
        updateUIfilter.addAction(PAUSE);
        registerReceiver(UIbroadcastReceiver, updateUIfilter);
    }

    BroadcastReceiver UIbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PAUSE)) {
                btnPlay.setImageResource(R.drawable.main_btn_play);
            }
            String title = intent.getStringExtra("songTitle");
            if (!txvSongTitle.getText().equals(title)) {
                txvSongTitle.setText(intent.getStringExtra("songTitle"));
            }
            txvSongArtist.setText(intent.getStringExtra("songArtist"));
            seekBarSongProgress.setMax(musicController.getSongDuration());
            seekBarSongProgress.setProgress(musicController.getSongPlayingPos());
            Song song = musicController.getSongList().get(musicController.getSongPos());
            txvSongDuration.setText(String.format("%d:%02d", song.getDuration() / 60000, song.getDuration() / 1000 % 60));
            txvSongNum.setText(String.format("%d / %d", musicController.getSongPos() + 1, musicController.getSongList().size()));
        }
    };

    private Runnable mp3StartRunable = new Runnable() {
        @Override
        public void run() {
            if (musicController.isPlaying()) {
                seekBarSongProgress.setMax(musicController.getSongDuration());
                seekBarSongProgress.setProgress(musicController.getSongPlayingPos());
                btnPlay.setImageResource(R.drawable.main_btn_pause);
            }
            txvPlayingTime.setText(String.format("%d:%02d", musicController.getSongPlayingPos() / 60000, musicController.getSongPlayingPos() / 1000 % 60));
            seekBarValueControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            updateRepeatImgButtonView();
            musicHandler.postDelayed(this,50);
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            txvPlayingTime.setText(String.format("%d:%02d", seekBar.getProgress() / 60000, seekBar.getProgress() / 1000 % 60));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            musicHandler.removeCallbacks(mp3StartRunable);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (musicController.getSongList().size() > 0) {
                musicController.setSongPlayingPos(seekBar.getProgress());
                musicHandler.post(mp3StartRunable);
            }
        }
    };

    private void updateRepeatImgButtonView() {
        if (repeatMode.equals(REPEAT)) {
            btnRepeatMode.setImageResource(R.drawable.main_btn_repeat_all);
        }else if (repeatMode.equals(REPEATONE)) {
            btnRepeatMode.setImageResource(R.drawable.main_btn_repeat_one);
        }else if (repeatMode.equals(SHUFFLE)) {
            btnRepeatMode.setImageResource(R.drawable.main_btn_repeat_shffle);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        musicController = new MusicController(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        initUpdateUiReceiver();
        musicHandler.post(mp3StartRunable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnPlay = findViewById(R.id.imgbtn_play);
        btnPlayNext = findViewById(R.id.imgbtn_next);
        btnPlayPrev = findViewById(R.id.imgbtn_prev);
        btnRepeatMode = findViewById(R.id.imgbtn_repeat);
        btnFavorite = findViewById(R.id.imgbtn_favorite);

        btnPlay.setOnClickListener(controlListener);
        btnPlayNext.setOnClickListener(controlListener);
        btnPlayPrev.setOnClickListener(controlListener);
        btnRepeatMode.setOnClickListener(controlListener);
        btnFavorite.setOnClickListener(controlListener);

        txvSongTitle = findViewById(R.id.txv_song_title);
        txvSongArtist = findViewById(R.id.txv_song_artist);
        txvPlayingTime = findViewById(R.id.txv_song_time);
        txvSongDuration = findViewById(R.id.txv_song_duration);
        txvSongNum = findViewById(R.id.txv_song_num);
        txvSongTitle.setSelected(true);

        seekBarSongProgress = findViewById(R.id.seekbar_song_progress);
        seekBarSongProgress.setOnSeekBarChangeListener(seekBarChangeListener);

        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarValueControl = findViewById(R.id.seekbar_value_control);
        seekBarValueControl.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBarValueControl.setProgress(currentVolume);
        seekBarValueControl.setOnSeekBarChangeListener(valueControlListener);
    }

    View.OnClickListener controlListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imgbtn_play:
                    if (musicController.getSongPlayingPos() == 0) {
                        musicController.playSong();
                    }else if (musicController.isPlaying()) {
                        musicController.pauseSong();
                    }else {
                        musicController.goSong();
                    }
                    break;
                case R.id.imgbtn_next:
                    musicController.playNext();
                    break;
                case R.id.imgbtn_prev:
                    musicController.playPrev();
                    break;
                case R.id.imgbtn_repeat:
                    musicController.setRepeatMode();
                    updateRepeatImgButtonView();
                    break;
                case R.id.imgbtn_favorite:
                    SelectPlayListFragmentDialog selectPlayListFragmentDialog = new SelectPlayListFragmentDialog();
                    selectPlayListFragmentDialog.addSongToFile(txvSongTitle.getText().toString());
                    selectPlayListFragmentDialog.show(getSupportFragmentManager(), null);
                    break;
            }
        }
    };

    SeekBar.OnSeekBarChangeListener valueControlListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(UIbroadcastReceiver);
            musicHandler.removeCallbacks(mp3StartRunable);
            musicController.unbindMusicService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
