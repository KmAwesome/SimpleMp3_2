package com.example.main.simplemp3_2.Song;

import android.view.View;

import com.example.main.simplemp3_2.Song.Song;

import java.util.ArrayList;

public interface MusicControl {

    void playSong();
    void pauseSong();
    void continueSong();
    void prevSong();
    void nextSong();

    boolean isPlaying();

    void setSongIndex(int songIndex);
    int getSongIndex();

    void setSongPlayingPosition(int posn);
    int getSongPlayingPosition();

    void setSongList(ArrayList<Song> songs);
    ArrayList<Song> getSongList();

    int getSongDuration();

    void setRepeatMode(View view);

    void updateRepeatImgButtonView(View view);

    void setSongListShuffle();

    void updateWidget(String action);
}
