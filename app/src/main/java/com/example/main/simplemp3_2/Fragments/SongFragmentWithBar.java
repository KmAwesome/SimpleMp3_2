package com.example.main.simplemp3_2.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.main.simplemp3_2.Adapters.SongAdapterWithBar;
import com.example.main.simplemp3_2.R;

import static com.example.main.simplemp3_2.Utils.MusicConstants.ARGUMENTS_TOOLBAR_TITLE;

public class SongFragmentWithBar extends Fragment implements SongAdapterWithBar.OnItemClickListener {
    private final String TAG = "SongFragmentWithBar";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SongAdapterWithBar songAdapterWithBar;
    private String toolbarTitle;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            toolbarTitle = (String) getArguments().get(ARGUMENTS_TOOLBAR_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycleview_container_with_toolbar, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(toolbarTitle);
        toolbar.setNavigationIcon(R.drawable.play_btn_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        DividerItemDecoration mDivider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView = view.findViewById(R.id.recycleview_main);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(mDivider);
        songAdapterWithBar = new SongAdapterWithBar(getContext());
        songAdapterWithBar.setOnItemClickListener(this);
        recyclerView.setAdapter(songAdapterWithBar);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        songAdapterWithBar.refreshAdapterView();
    }

    @Override
    public void onItemClick(int postion) {

    }
}
