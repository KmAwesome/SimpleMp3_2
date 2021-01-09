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

import com.example.main.simplemp3_2.Adapters.StyleRecycleAdapter;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.Utils.MusicUtils;
import java.util.ArrayList;
import static com.example.main.simplemp3_2.Utils.MusicConstants.ARGUMENTS_TOOLBAR_TITLE;

public class StyleRecycleFragment extends Fragment implements StyleRecycleAdapter.OnItemClickListener {
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> styleStringList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_list_container, container, false);
        DividerItemDecoration mDivider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.recycleview_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(mDivider);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        styleStringList = MusicUtils.getStringListByType(context, MusicUtils.TYPE_STYLE);
        StyleRecycleAdapter styleRecycleAdapter = new StyleRecycleAdapter(context, styleStringList);
        styleRecycleAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(styleRecycleAdapter);
    }

    @Override
    public void onItemClick(int position) {
        styleStringList = MusicUtils.getStringListByType(context, MusicUtils.TYPE_STYLE);
        String styleTitle = styleStringList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENTS_TOOLBAR_TITLE, styleTitle);
        ArrayList<Song> songArrayList = MusicUtils.getSongListByTitle(context, styleTitle);
        MusicUtils.setDisplaySongList(songArrayList);
        SongListFragmentWithBar songListFragmentWithBar = new SongListFragmentWithBar();
        songListFragmentWithBar.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frameLayout, songListFragmentWithBar).addToBackStack(null).commit();
    }
}
