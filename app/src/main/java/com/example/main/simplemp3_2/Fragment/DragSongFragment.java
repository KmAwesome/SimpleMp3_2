package com.example.main.simplemp3_2.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.main.simplemp3_2.Adapter.DragListAdapter;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.MusicController;
import com.example.main.simplemp3_2.Song.Song;

import java.util.ArrayList;

public class DragSongFragment extends Fragment implements AdapterView.OnItemClickListener {
    private final String TAG = "SongFragment";
    private Context context;
    private ListView songView;
    private ArrayList<Song> songlist;
    private String playListTitle;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        songlist = new ArrayList<>();
        songlist = (ArrayList<Song>) getArguments().get("playSongList");
        playListTitle = (String) getArguments().get("playListTitle");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_drag, container, false);
        songView = view.findViewById(R.id.drag_list);
        songView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DragListAdapter dragListAdapter = new DragListAdapter(context, songlist, playListTitle);
        songView.setAdapter(dragListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MusicController musicController = ((MainActivity)context).getMusicController();
        musicController.setSongList(songlist);
        musicController.setSongIndex(i);
        musicController.playSong();
    }
}
