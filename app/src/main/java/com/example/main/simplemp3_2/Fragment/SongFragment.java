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

public class SongFragment extends Fragment implements AdapterView.OnItemClickListener{
    private final String TAG = "SongFragment";
    private Context context;
    private ListView songView;
    public SongAdapter songAdapter;
    private ArrayList<Song> songlist;
    private ArrayList<Song> mSongs;
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
        View view = inflater.inflate(R.layout.listview_song, container, false);
        songView = view.findViewById(R.id.song_list);
        songView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (playListTitle != null) {
            songAdapter = new SongAdapter(context, getSongsInArtist(), playListTitle);
            songView.setAdapter(songAdapter);
        }else {
            songAdapter = new SongAdapter(context, getSongsInArtist());
            songView.setAdapter(songAdapter);
        }
    }

    private ArrayList<Song> getSongsInArtist() {
        boolean haveSong = false;
        mSongs = new ArrayList<>();
        ArrayList<Song> songs = new InitSongList(context).getSongList();
        for (int i=0; i<songs.size(); i++) {
            for (int j=0; j<songlist.size(); j++) {
                if (songs.get(i).getPath().contains(songlist.get(j).getPath())) {
                    haveSong = true;
                    break;
                }else {
                    haveSong = false;
                }
            }
            if (haveSong) {
                mSongs.add(songs.get(i));
            }
        }
        return mSongs;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MusicController musicController = ((MainActivity)context).getMusicController();
        musicController.setSongList(mSongs);
        musicController.setSongPos(i);
        musicController.playSong();
    }

}
