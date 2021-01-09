package com.example.main.simplemp3_2.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.main.simplemp3_2.Dialog.SelectPlayListDialog;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.MusicConstants;
import com.example.main.simplemp3_2.Utils.MusicController;


public class MusicPlayerFragment extends Fragment implements MusicConstants {
    private final String TAG = "MusicPlayerFragment";
    private MusicController musicController;
    private AudioManager audioManager;
    private Handler musicHandler = new Handler();
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
                musicHandler.postDelayed(this,1000);
                if (musicController.isPlaying()) {
                    btnPlay.setImageResource(R.drawable.main_btn_pause);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        musicController = MusicController.getInstance(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_PAUSE);
        context.registerReceiver(updateMusicPlayerUIreceiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_play, null, false);
        btnPlay = view.findViewById(R.id.imgbtn_play);
        btnPlayNext = view.findViewById(R.id.imgbtn_next);
        btnPlayPrev = view.findViewById(R.id.imgbtn_prev);
        btnRepeatMode = view.findViewById(R.id.imgbtn_repeat);
        btnFavorite = view.findViewById(R.id.imgbtn_favorite);
        btnPlay.setOnClickListener(controlBarListener);
        btnPlayNext.setOnClickListener(controlBarListener);
        btnPlayPrev.setOnClickListener(controlBarListener);
        btnRepeatMode.setOnClickListener(controlBarListener);
        btnFavorite.setOnClickListener(controlBarListener);

        txvSongTitle = view.findViewById(R.id.txv_song_title);
        txvSongArtist = view.findViewById(R.id.txv_song_artist);
        txvPlayingTime = view.findViewById(R.id.txv_song_time);
        txvSongDuration = view.findViewById(R.id.txv_song_duration);
        txvSongNum = view.findViewById(R.id.text_view_song_total);
        txvSongTitle.setSelected(true);

        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarValueControl = view.findViewById(R.id.seekbar_value_control);
        seekBarValueControl.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBarValueControl.setProgress(currentVolume);
        seekBarValueControl.setOnSeekBarChangeListener(seekBarSongVolumeChangeListener);

        seekBarSongProgress = view.findViewById(R.id.seekbar_song_progress);
        seekBarSongProgress.setOnSeekBarChangeListener(seekBarSongProgressChangeListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (musicController.getSongList().size() > 0) {
            txvSongTitle.setText(musicController.getCurrentSong().getTitle());
            txvSongArtist.setText(musicController.getCurrentSong().getArtist());
            txvSongNum.setText(String.format("%d / %d", musicController.getSongIndex() + 1, musicController.getSongList().size()));
            seekBarSongProgress.setMax(musicController.getSongDuration());
            seekBarSongProgress.setProgress(musicController.getSongPlayingPosition());
            txvPlayingTime.setText(String.format("%d:%02d", musicController.getSongPlayingPosition() / 60000, musicController.getSongPlayingPosition() / 1000 % 60));
            txvSongDuration.setText(String.format("%2d:%02d", musicController.getSongDuration() / 60000, musicController.getSongDuration() / 1000 % 60 ));
        }
        musicController.updateRepeatImgButtonView(btnRepeatMode);
        if (musicController.isPlaying()) {
            mp3StartRunable.run();
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
                    selectPlayListDialog.show(getFragmentManager(), null);
                    break;
            }
        }
    };

    SeekBar.OnSeekBarChangeListener seekBarSongVolumeChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    SeekBar.OnSeekBarChangeListener seekBarSongProgressChangeListener = new SeekBar.OnSeekBarChangeListener() {
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
    };
}
