package com.example.main.simplemp3_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.main.simplemp3_2.Dialog.CountDownDialog;
import com.example.main.simplemp3_2.Song.MusicController;
import com.example.main.simplemp3_2.Song.Song;
import com.example.main.simplemp3_2.Dialog.SelectPlayListFragmentDialog;

import static com.example.main.simplemp3_2.Service.MusicService.ACTION_PAUSE;
import static com.example.main.simplemp3_2.Service.MusicService.ACTION_PLAY;
import static com.example.main.simplemp3_2.Service.MusicService.ACTION_START;

public class PlayActivity extends AppCompatActivity {
    private final String TAG = "PlayActivity";
    private MusicController musicController;
    private AudioManager audioManager;
    private ImageButton btnPlay, btnPlayNext, btnPlayPrev, btnRepeatMode, btnFavorite;
    private TextView txvSongTitle, txvSongArtist, txvPlayingTime, txvSongDuration, txvSongNum;
    private SeekBar seekBarSongProgress, seekBarValueControl;
    private int currentVolume;
    private static Handler musicHandler = new Handler();
    private static boolean isRunning, isBackground;

    private void initUpdateUiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_START);
        registerReceiver(UIbroadcastReceiver, intentFilter);
    }

    BroadcastReceiver UIbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!isRunning) {
                mp3StartRunable.run();
                isRunning = true;
            }

            if (intent.getAction().equals(ACTION_PAUSE)) {
                btnPlay.setImageResource(R.drawable.main_btn_play);
            }else if (intent.getAction().equals(ACTION_PLAY)) {
                btnPlay.setImageResource(R.drawable.main_btn_pause);
            }

            musicController.updateRepeatImgButtonView(btnRepeatMode);

            String title = intent.getStringExtra("songTitle");
            if (!txvSongTitle.getText().equals(title)) {
                txvSongTitle.setText(intent.getStringExtra("songTitle"));
            }
            txvSongArtist.setText(intent.getStringExtra("songArtist"));
            seekBarSongProgress.setMax(musicController.getSongDuration());
            seekBarSongProgress.setProgress(musicController.getSongPlayingPosition());

            Song song = musicController.getSongList().get(musicController.getSongIndex());
            txvSongDuration.setText(String.format("%d:%02d", song.getDuration() / 60000, song.getDuration() / 1000 % 60));
            txvSongNum.setText(String.format("%d / %d", musicController.getSongIndex() + 1, musicController.getSongList().size()));
        }
    };

    private Runnable mp3StartRunable = new Runnable() {
        @Override
        public void run() {
            try {
                if (musicController.isPlaying()) {
                    seekBarSongProgress.setMax(musicController.getSongDuration());
                    seekBarSongProgress.setProgress(musicController.getSongPlayingPosition());
                    btnPlay.setImageResource(R.drawable.main_btn_pause);
                }
                txvPlayingTime.setText(String.format("%d:%02d", musicController.getSongPlayingPosition() / 60000, musicController.getSongPlayingPosition() / 1000 % 60));
                seekBarValueControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                musicHandler.postDelayed(this,50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.play_btn_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
        toolbar.inflateMenu(R.menu.activity_play_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_set) {
                    View view = findViewById(R.id.menu_set);
                    PopupMenu popupMenu = new PopupMenu(PlayActivity.this, view);
                    popupMenu.getMenuInflater().inflate(R.menu.activity_play_menu_set, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.time_set :
                                    CountDownDialog countDownDialog = new CountDownDialog();
                                    countDownDialog.show(getSupportFragmentManager(), null);
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
                return false;
            }
        });

        musicController = new MusicController(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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
        seekBarSongProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    musicController.setSongPlayingPosition(seekBar.getProgress());
                    musicHandler.post(mp3StartRunable);
                }
            }
        });

        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarValueControl = findViewById(R.id.seekbar_value_control);
        seekBarValueControl.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBarValueControl.setProgress(currentVolume);
        seekBarValueControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        });

        initUpdateUiReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = false;
        isBackground = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isBackground = true;
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
    
    View.OnClickListener controlListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imgbtn_play:
                    if (musicController.getSongPlayingPosition() == 0) {
                        musicController.playSong();
                    }else if (musicController.isPlaying()) {
                        musicController.pauseSong();
                    }else {
                        musicController.continueSong();
                    }
                    break;
                case R.id.imgbtn_next:
                    musicController.nextSong();
                    break;
                case R.id.imgbtn_prev:
                    musicController.prevSong();
                    break;
                case R.id.imgbtn_repeat:
                    musicController.setRepeatMode(view);
                    break;
                case R.id.imgbtn_favorite:
                    SelectPlayListFragmentDialog selectPlayListFragmentDialog = new SelectPlayListFragmentDialog();
                    selectPlayListFragmentDialog.addSongToFile(txvSongTitle.getText().toString());
                    selectPlayListFragmentDialog.show(getSupportFragmentManager(), null);
                    break;
            }
        }
    };

}
