package com.example.main.simplemp3_2.ListRecycleView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.Song.Song;

import java.util.ArrayList;

public class ListRecycleFragment extends Fragment {
    private final String TAG = "ListRecycleFragment";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SongListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycleview_container, container, false);
        DividerItemDecoration mDivider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView = view.findViewById(R.id.recycleview_song);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(mDivider);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter = new SongListAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
        updateSongAdapter();
    }

    public void updateSongAdapter() {
        mAdapter.notifyDataSetChanged();
    }

}
