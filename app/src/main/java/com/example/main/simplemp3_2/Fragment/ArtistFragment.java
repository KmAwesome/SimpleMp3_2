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
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Model.Song;
import java.util.ArrayList;

public class ArtistFragment extends Fragment implements AdapterView.OnItemClickListener{
    final static String TAG = "ArtistFragment";
    private ArrayList<String> artistList;
    private ArrayList<Song> songlist,artistSonglist;
    private ListView listView;
    private Context context;
    private ArtistAdapter artistAdapter;
    private Bundle bundle;
    private SongFragment songFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = new Bundle();
        artistList = new ArrayList<>();
        songlist = new ArrayList<>();
        songlist = ((MainActivity)getActivity()).getSonglist();
        artistAdapter = new ArtistAdapter(context,getAristList(),songlist);
        songFragment = new SongFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.m_listview,container,false);
        listView = view.findViewById(R.id.song_list);
        listView.setOnItemClickListener(this);
        listView.setAdapter(artistAdapter);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        bundle.putSerializable("playSongList",getSongInArtistlist(i));
        songFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.relativLayout, songFragment).addToBackStack(null).commit();
    }

    public ArrayList<String> getAristList() {
        String[] stringArtist;
        if (songlist == null) songlist = ((MainActivity)getActivity()).getSonglist();
        stringArtist = new String[songlist.size()];
        if (artistList.size() > 0){
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && songlist != null) {
            Log.i(TAG, "setUserVisibleHint: true");
            getAristList();
            artistAdapter.notifyDataSetChanged();
        } else {
            Log.i(TAG, "setUserVisibleHint: false");
        }
    }

}
