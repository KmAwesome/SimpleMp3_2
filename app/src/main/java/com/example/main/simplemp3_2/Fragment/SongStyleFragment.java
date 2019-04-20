package com.example.main.simplemp3_2.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.example.main.simplemp3_2.Adapter.SongStyleAdapter;
import com.example.main.simplemp3_2.InitSongList;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song;

import java.util.ArrayList;

public class SongStyleFragment extends Fragment {
    private InitSongList initSongList;
    private Context context;
    private ListView songListView;
    private ArrayList<String> songStyleList;
    private ArrayList<Song> songList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        initSongList = new InitSongList(context);
        songList = new ArrayList<>();
        songList = initSongList.getSongList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_song, null);
        songListView = view.findViewById(R.id.song_list);
        songListView.setOnItemClickListener(onItemClickListener);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initSongList.initSongList();
        songList = initSongList.getSongList();
        SongStyleAdapter songStyleAdapter = new SongStyleAdapter(context, getStyleNameList(), songList);
        songListView.setAdapter(songStyleAdapter);
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

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Bundle bundle = new Bundle();
            String styleName = getStyleNameList().get(i);
            bundle.putSerializable("playSongList", getSongListInStyleName(styleName));
            SongFragment songFragment = new SongFragment();
            songFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.relativLayout, songFragment).addToBackStack(null).commit();
        }
    };

    private ArrayList<Song> getSongListInStyleName(String styleName) {
        ArrayList<Song> songs = new ArrayList<>();
        for (int i=0; i<songList.size(); i++) {
            if (songList.get(i).getStyle().equals(styleName)) {
                songs.add(songList.get(i));
            }
        }
        return songs;
    }



}
