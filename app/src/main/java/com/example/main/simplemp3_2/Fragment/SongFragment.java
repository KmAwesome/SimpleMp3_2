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

import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.Song.MusicController;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.Song;
import com.example.main.simplemp3_2.Adapter.SongAdapter;
import java.util.ArrayList;

public class SongFragment extends Fragment implements AdapterView.OnItemClickListener{
    private final String TAG = "SongFragment";
    private Context context;
    private ListView songView;
    public SongAdapter songAdapter;
    private ArrayList<Song> songlist;
    private ArrayList<Song> mSongs;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        songlist = new ArrayList<>();
        mSongs = new ArrayList<>();
        songlist = (ArrayList<Song>) getArguments().get("playSongList");
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
        songAdapter = new SongAdapter(context, getSongsInArtist());
        songView.setAdapter(songAdapter);
    }

    private ArrayList<Song> getSongsInArtist() {
        boolean haveSong = false;
        ArrayList<Song> songs = new InitSongList(context).getSongList();
        for (int i=0; i<songlist.size(); i++) {
            for (int j=0; j<songs.size(); j++) {
                if (songlist.get(i).getPath().contains(songs.get(j).getPath())) {
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
        musicController.setSongIndex(i);
        musicController.playSong();
    }

}
