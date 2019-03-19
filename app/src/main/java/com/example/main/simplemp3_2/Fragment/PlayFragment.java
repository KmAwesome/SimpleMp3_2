package com.example.main.simplemp3_2.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.main.simplemp3_2.InitSongList;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.MusicController;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song;
import com.example.main.simplemp3_2.Adapter.SongAdapter;
import java.util.ArrayList;

public class PlayFragment extends Fragment implements AdapterView.OnItemClickListener {
    final static String TAG = "PlayFragment";
    private ListView songView;
    private SongAdapter songAdt;
    private ArrayList<Song> songlist;
    private Context context;
    private InitSongList initSongList;
    private MusicController musicController;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSongList = ((MainActivity)context).getInitSongList();
        musicController = ((MainActivity)context).getMusicController();
        songlist = initSongList.getSongList();
        songAdt = new SongAdapter(context, songlist);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.m_listview, container, false);
        songView = view.findViewById(R.id.song_list);
        songView.setAdapter(songAdt);
        songView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        initSongList.setSongList(songlist);
        musicController.setSongPos(i);
        musicController.playSong();
    }

}
