package com.example.main.simplemp3_2.Activity;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.main.simplemp3_2.Dialog.CountDownDialog;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.MusicConstants;
import com.example.main.simplemp3_2.Utils.MusicController;
import com.example.main.simplemp3_2.Dialog.SelectPlayListDialog;

public class MusicPlayerActivity extends AppCompatActivity implements MusicConstants {
    private final String TAG = "MusicPlayerActivity";
    private MusicController musicController;
    private Handler musicHandler = new Handler();
    private IntentFilter intentFilter;
    private AudioManager audioManager;
    private ImageButton btnPlay, btnPlayNext, btnPlayPrev, btnRepeatMode, btnFavorite;
    private TextView txvSongTitle, txvSongArtist, txvPlayingTime, txvSongDuration, txvSongNum;
    private SeekBar seekBarSongProgress, seekBarValueControl;
    private int currentVolume;

    BroadcastReceiver updateMusicPlayerUIreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    txvSongTitle.setText(musicController.getCurrentSong().getTitle());
                    txvSongArtist.setText(musicController.getCurrentSong().getArtist());
                    btnPlay.setImageResource(R.drawable.main_btn_pause);
                    mp3StartRunable.run();
                    break;
                case ACTION_PAUSE:
                    btnPlay.setImageResource(R.drawable.main_btn_play);
                    musicHandler.removeCallbacks(mp3StartRunable);
                    break;
                case ACTION_CONTINUE:
                    btnPlay.setImageResource(R.drawable.main_btn_pause);
                    mp3StartRunable.run();
                    break;
            }
            txvSongNum.setText(String.format("%d / %d", musicController.getSongIndex() + 1, musicController.getSongList().size()));
        }
    };

    private Runnable mp3StartRunable = new Runnable() {
        @Override
        public void run() {
            try {
                seekBarSongProgress.setMax(musicController.getSongDuration());
                seekBarSongProgress.setProgress(musicController.getSongPlayingPosition());
                txvPlayingTime.setText(String.format("%d:%02d", musicController.getSongPlayingPosition() / 60000, musicController.getSongPlayingPosition() / 1000 % 60));
                txvSongDuration.setText(String.format("%2d:%02d", musicController.getSongDuration() / 60000, musicController.getSongDuration() / 1000 % 60 ));
                seekBarValueControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                musicHandler.postDelayed(this,50);
                if (musicController.isPlaying()) {
                    btnPlay.setImageResource(R.drawable.main_btn_pause);
                }
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
                    PopupMenu popupMenu = new PopupMenu(MusicPlayerActivity.this, view);
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

        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_PAUSE);

        musicController = MusicController.getInstance(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        btnPlay = findViewById(R.id.imgbtn_play);
        btnPlayNext = findViewById(R.id.imgbtn_next);
        btnPlayPrev = findViewById(R.id.imgbtn_prev);
        btnRepeatMode = findViewById(R.id.imgbtn_repeat);
        btnFavorite = findViewById(R.id.imgbtn_favorite);

        btnPlay.setOnClickListener(controlBarListener);
        btnPlayNext.setOnClickListener(controlBarListener);
        btnPlayPrev.setOnClickListener(controlBarListener);
        btnRepeatMode.setOnClickListener(controlBarListener);
        btnFavorite.setOnClickListener(controlBarListener);

        txvSongTitle = findViewById(R.id.txv_song_title);
        txvSongArtist = findViewById(R.id.txv_song_artist);
        txvPlayingTime = findViewById(R.id.txv_song_time);
        txvSongDuration = findViewById(R.id.txv_song_duration);
        txvSongNum = findViewById(R.id.text_view_song_total);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(updateMusicPlayerUIreceiver, intentFilter);
        if (musicController.getSongList().size() > 0) {
            txvSongTitle.setText(musicController.getCurrentSong().getTitle());
            txvSongArtist.setText(musicController.getCurrentSong().getArtist());
            txvSongNum.setText(String.format("%d / %d", musicController.getSongIndex() + 1, musicController.getSongList().size()));
        }
        musicController.updateRepeatImgButtonView(btnRepeatMode);
        mp3StartRunable.run();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendBroadcast(new Intent().setAction(NOTIFICATION_CLOSE_NOTIFICATION_ONLY));
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (musicController.isPlaying()) {
            sendBroadcast(new Intent().setAction(NOTIFICATION_CREATE_NOTIFICATION));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(updateMusicPlayerUIreceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            musicHandler.removeCallbacks(mp3StartRunable);
            musicController.unbindMusicService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener controlBarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imgbtn_play:
                    if (musicController.getSongPlayingPosition() == 0) {
                        musicController.playSong();
                        mp3StartRunable.run();
                    }else if (musicController.isPlaying()) {
                        musicController.pauseSong();
                        musicHandler.removeCallbacks(mp3StartRunable);
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
                    SelectPlayListDialog selectPlayListDialog = new SelectPlayListDialog();
                    selectPlayListDialog.addSongToFile(txvSongTitle.getText().toString());
                    selectPlayListDialog.show(getSupportFragmentManager(), null);
                    break;
            }
        }
    };

}
