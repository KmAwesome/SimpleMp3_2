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
import com.example.main.simplemp3_2.Adapter.AlbumAdapter;
import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.Song;
import java.util.ArrayList;

public class AlbumFragment extends Fragment implements AdapterView.OnItemClickListener {
    private final String TAG = "AlbumFragment";
    private InitSongList initSongList;
    private Context context;
    private ListView listView;
    private ArrayList<Song> songList;
    private ArrayList<String> albumNames;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        initSongList = new InitSongList(context);
        songList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_song, null,false);
        listView = view.findViewById(R.id.song_list);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("playSongList", getSongsInAlbumName(i));
        SongFragment songFragment = new SongFragment();
        songFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.relativLayout, songFragment).addToBackStack(null).commit();
    }

    private ArrayList<String> getAlbumList() {
        albumNames = new ArrayList<>();
        for (int i=0; i<songList.size(); i++) {
            String albumName = songList.get(i).getAlbum();
            if (!albumNames.contains(albumName)) {
                albumNames.add(albumName);
            }
        }
        return albumNames;
    }

    private ArrayList<Song> getSongsInAlbumName(int position) {
        ArrayList<Song> songs = new ArrayList<>();
        for (int i=0; i<songList.size(); i++) {
            if (songList.get(i).getAlbum().equals(albumNames.get(position))) {
                songs.add(songList.get(i));
            }
        }
        return songs;
    }

    @Override
    public void onStart() {
        super.onStart();
        songList = initSongList.getSongList();
        AlbumAdapter albumAdapter = new AlbumAdapter(context, getAlbumList(), songList);
        listView.setAdapter(albumAdapter);
    }
}
