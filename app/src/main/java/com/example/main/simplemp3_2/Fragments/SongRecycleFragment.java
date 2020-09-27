package com.example.main.simplemp3_2.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Adapters.SongRecycleAdapter;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.MusicUtils;
import java.util.ArrayList;

public class SongRecycleFragment extends Fragment implements SongRecycleAdapter.OnItemClickListener {
    private final String TAG = "SongRecycleFragment";
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SongRecycleAdapter songRecycleAdapter;
    private ArrayList<Song> songArrayList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        songArrayList = MusicUtils.getSongList(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycleview_container, container, false);
        DividerItemDecoration mDivider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView = view.findViewById(R.id.recycleview_main);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(mDivider);
        songRecycleAdapter = new SongRecycleAdapter(context, songArrayList);
        songRecycleAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(songRecycleAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        songRecycleAdapter.refreshAdapterView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(int postion) {
        Log.i(TAG, "onItemClick: " + postion);
    }
}
