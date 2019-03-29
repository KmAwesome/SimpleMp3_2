package com.example.main.simplemp3_2.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.main.simplemp3_2.Adapter.ArtistAdapter;
import com.example.main.simplemp3_2.InitSongList;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.MusicController;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song;
import java.util.ArrayList;

public class ArtistFragment extends Fragment implements AdapterView.OnItemClickListener{
    final static String TAG = "ArtistFragment";
    private ArrayList<String> artistList;
    private ArrayList<Song> songlist,artistSonglist;
    private ListView listView;
    private Context context;
    private ArtistAdapter artistAdapter;
    private SongFragment songFragment;
    private InitSongList initSongList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        initSongList = new InitSongList(context);
        artistList = new ArrayList<>();
        songlist = new ArrayList<>();
        songFragment = new SongFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.m_listview,container,false);
        listView = view.findViewById(R.id.song_list);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initSongList.initSongList();
        songlist = initSongList.getSongList();
        artistAdapter = new ArtistAdapter(context,getAristList(),songlist);
        listView.setAdapter(artistAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("playSongList", getSongInArtistlist(i));
        songFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.relativLayout, songFragment).addToBackStack(null).commit();
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

    public ArrayList<Song> getSongInArtistlist(int position) {
        artistSonglist = new ArrayList<>();
        for (int i=0; i<songlist.size(); i++){
            if (songlist.get(i).getArtist().equals(artistList.get(position))){
                artistSonglist.add(songlist.get(i));
            }
        }
        return artistSonglist;
    }

}
