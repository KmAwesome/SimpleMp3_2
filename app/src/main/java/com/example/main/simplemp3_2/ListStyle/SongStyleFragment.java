package com.example.main.simplemp3_2.ListStyle;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.ListCustomSong.SongFragment;
import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.Song;

import java.util.ArrayList;

public class SongStyleFragment extends ListFragment {
    private InitSongList initSongList;
    private ArrayList<String> songStyleList;
    private ArrayList<Song> songList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSongList = ((MainActivity)getContext()).getInitSongList();
        songList = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        songList = initSongList.getSongList();
        SongStyleAdapter songStyleAdapter = new SongStyleAdapter(getContext(), getStyleNameList(), songList);
        setListAdapter(songStyleAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Bundle bundle = new Bundle();
        String styleName = getStyleNameList().get(position);
        bundle.putSerializable("playSongList", getSongListInStyleName(styleName));
        SongFragment songFragment = new SongFragment();
        songFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.relativLayout, songFragment).addToBackStack(null).commit();
    }

    private ArrayList<Song> getSongListInStyleName(String styleName) {
        ArrayList<Song> songs = new ArrayList<>();
        for (int i=0; i<songList.size(); i++) {
            if (songList.get(i).getStyle().equals(styleName)) {
                songs.add(songList.get(i));
            }
        }
        return songs;
    }

    private ArrayList<String> getStyleNameList() {
        songStyleList = new ArrayList<>();
        for (int i=0; i<songList.size(); i++) {
            String styleName = songList.get(i).getStyle();
            if (!songStyleList.contains(styleName)) {
                songStyleList.add(styleName);
            }
        }
        return songStyleList;
    }

}
