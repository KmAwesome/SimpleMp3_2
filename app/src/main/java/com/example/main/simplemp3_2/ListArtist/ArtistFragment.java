package com.example.main.simplemp3_2.ListArtist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class ArtistFragment extends Fragment implements AdapterView.OnItemClickListener{
    final static String TAG = "ArtistFragment";
    private ArrayList<String> artistList;
    private ArrayList<Song> songlist;
    private ListView listView;
    private ArtistAdapter artistAdapter;
    private SongFragment songFragment;
    private InitSongList initSongList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_container,container,false);
        listView = view.findViewById(R.id.song_list);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSongList = ((MainActivity)getContext()).getInitSongList();
        songlist = new ArrayList<>();
        artistList = new ArrayList<>();
        songFragment = new SongFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        songlist = initSongList.getSongList();
        artistAdapter = new ArtistAdapter(getContext(), getAristList(), songlist);
        listView.setAdapter(artistAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("playSongList", getSongInArtistlist(i));
        songFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.relativLayout, songFragment).addToBackStack(null).commit();
    }

    public ArrayList<Song> getSongInArtistlist(int position) {
        ArrayList<Song> artistSongList = new ArrayList<>();
        for (int i=0; i<songlist.size(); i++){
            if (songlist.get(i).getArtist().equals(artistList.get(position))){
                artistSongList.add(songlist.get(i));
            }
        }
        return artistSongList;
    }

    public ArrayList<String> getAristList() {
        String[] stringArtist;
        stringArtist = new String[songlist.size()];
        if (artistList.size() > 0) {
            artistList.clear();
        }
        for(int i = 0; i< songlist.size(); i++){
            stringArtist[i] = songlist.get(i).getArtist();
        }
        for (String name : stringArtist){
            for (int i=0; i<stringArtist.length; i++){
                if (!artistList.contains(name)){
                    artistList.add(name);
                    break;
                }
            }
        }
        return artistList;
    }
}
