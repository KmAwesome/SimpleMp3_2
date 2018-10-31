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
import android.widget.TextView;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.Model.InitSongList;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Model.Song;
import com.example.main.simplemp3_2.Adapter.SongAdapter;
import java.util.ArrayList;

public class PlayFragment extends Fragment implements AdapterView.OnItemClickListener {
    final static String TAG = "PlayFragment";
    private ListView songView;
    private SongAdapter songAdt;
    private ArrayList<Song> songlist;
    private Context context;
    public static int songPosn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songlist = new ArrayList<>();
        songlist = ((MainActivity)getActivity()).getSonglist();
        songAdt = new SongAdapter(context,songlist,this.getActivity());
        if (songlist.size() > 0){
            ((MainActivity) getActivity()).txv_showTitle.setText(songlist.get(0).getTitle());
            ((MainActivity) getActivity()).txv_showArtist.setText(songlist.get(0).getArtist());
        }
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
        songPosn = i;
        ((MainActivity)getActivity()).getSonglist();
        ((MainActivity)getActivity()).setSonglist(songlist);
        ((MainActivity)getActivity()).playSong();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && songlist != null) {
            Log.i(TAG, "setUserVisibleHint: true");
            ((MainActivity)getActivity()).refreshSongList();
            songAdt.notifyDataSetChanged();
        } else {
            Log.i(TAG, "setUserVisibleHint: false");
        }
    }

}
