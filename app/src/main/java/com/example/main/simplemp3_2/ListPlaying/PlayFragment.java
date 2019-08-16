package com.example.main.simplemp3_2.ListPlaying;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.Song.MusicController;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.Song;

import java.util.ArrayList;

public class PlayFragment extends Fragment implements AdapterView.OnItemClickListener {
    final static String TAG = "PlayFragment";
    private ListView listView;
    private ArrayList<Song> songlist;
    private InitSongList initSongList;
    private MusicController musicController;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSongList = ((MainActivity)getContext()).getInitSongList();
        musicController = ((MainActivity)getContext()).getMusicController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_container, container, false);
        listView = view.findViewById(R.id.song_list);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        songlist = initSongList.getSongList();
        SongAdapter songAdapter = new SongAdapter(getContext(), songlist);
        listView.setAdapter(songAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        musicController.setSongList(songlist);
        musicController.setSongIndex(i);
        musicController.playSong();
    }

}
