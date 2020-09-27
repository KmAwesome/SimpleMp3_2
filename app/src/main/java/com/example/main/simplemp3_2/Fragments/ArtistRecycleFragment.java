package com.example.main.simplemp3_2.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.main.simplemp3_2.Adapters.ArtistRecycleAdapter;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.MusicUtils;
import java.util.ArrayList;
import static com.example.main.simplemp3_2.Utils.MusicConstants.ARGUMENTS_TOOLBAR_TITLE;

public class ArtistRecycleFragment extends Fragment implements ArtistRecycleAdapter.onItemClickListener{
    private final static String TAG = "ArtistRecycleFragment";
    private Context context;
    private RecyclerView artistRecycleView;
    private ArtistRecycleAdapter artistRecycleAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> artistStringList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        artistStringList = MusicUtils.getStringListByType(context, MusicUtils.TYPE_ARTIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DividerItemDecoration mDivider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        View view = inflater.inflate(R.layout.recycleview_container,container,false);
        artistRecycleView = view.findViewById(R.id.recycleview_main);
        artistRecycleView.addItemDecoration(mDivider);
        artistRecycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        artistRecycleView.setLayoutManager(layoutManager);
        artistRecycleAdapter = new ArtistRecycleAdapter(context, artistStringList);
        artistRecycleAdapter.setOnItemClickListener(this);
        artistRecycleView.setAdapter(artistRecycleAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onItemClick(int position) {
        artistStringList = MusicUtils.getStringListByType(context, MusicUtils.TYPE_ARTIST);
        String artsitTitle = artistStringList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENTS_TOOLBAR_TITLE, artsitTitle);
        ArrayList<Song> songArrayList = MusicUtils.getSongListByTitle(context, artsitTitle);
        MusicUtils.setDisplaySongList(songArrayList);
        SongFragmentWithBar songFragmentWithBar = new SongFragmentWithBar();
        songFragmentWithBar.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frameLayout, songFragmentWithBar).addToBackStack(null).commit();
    }

}
