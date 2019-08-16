package com.example.main.simplemp3_2.ListCustomSong;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Song.MusicControl;
import com.example.main.simplemp3_2.Song.MusicController;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.Song;
import com.example.main.simplemp3_2.ListPlaying.SongAdapter;
import java.util.ArrayList;

public class SongFragment extends ListFragment {
    private final String TAG = "SongFragment";
    private MusicController musicController;
    public SongAdapter songAdapter;
    private ArrayList<Song> songList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songList = new ArrayList<>();
        songList = (ArrayList<Song>) getArguments().get("playSongList");
        musicController = ((MainActivity)getContext()).getMusicController();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        songAdapter = new SongAdapter(getContext(), songList);
        setListAdapter(songAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        musicController.setSongList(songList);
        musicController.setSongIndex(position);
        musicController.playSong();
    }

}
