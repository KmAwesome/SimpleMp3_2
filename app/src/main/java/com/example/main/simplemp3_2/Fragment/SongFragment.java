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
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Model.Song;
import com.example.main.simplemp3_2.Adapter.SongAdapter;
import static com.example.main.simplemp3_2.Fragment.PlayFragment.songPosn;
import java.util.ArrayList;

public class SongFragment extends Fragment implements AdapterView.OnItemClickListener{
    private Context context;
    private ListView songView;
    public SongAdapter songAdapter;
    private ArrayList<Song> songlist;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songlist = new ArrayList<>();
        songlist = (ArrayList<Song>) getArguments().get("playSongList");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.m_listview, container, false);
        songView = view.findViewById(R.id.song_list);
        songAdapter = new SongAdapter(context,songlist,this.getActivity());
        songView.setAdapter(songAdapter);
        songView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i("TAG", "onItemClick:  " + i);
        songPosn = i;
        ((MainActivity)getActivity()).setSonglist(songlist);
        ((MainActivity)getActivity()).playSong();
    }

}
