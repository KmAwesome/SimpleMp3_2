package com.example.main.simplemp3_2.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.main.simplemp3_2.Adapters.FolderRecycleAdapter;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.MusicUtils;
import java.util.ArrayList;
import static com.example.main.simplemp3_2.Utils.MusicConstants.ARGUMENTS_TOOLBAR_TITLE;

public class FolderRecycleFragment extends Fragment implements FolderRecycleAdapter.OnItemClickListener {
    private static final String TAG = "FolderRecycleFragment";
    private Context context;
    private RecyclerView folderRecycleView;
    private RecyclerView.LayoutManager layoutManager;
    private FolderRecycleAdapter folderRecycleAdapter;
    private ArrayList<String> folderStringList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        DividerItemDecoration mDivider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        View view = inflater.inflate(R.layout.song_list_container, null,false);
        folderRecycleView = view.findViewById(R.id.recycleview_main);
        folderRecycleView.addItemDecoration(mDivider);
        folderRecycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        folderRecycleView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        folderStringList  = MusicUtils.getStringListByType(context, MusicUtils.TYPE_PATH);
        folderRecycleAdapter = new FolderRecycleAdapter(context, folderStringList);
        folderRecycleView.setAdapter(folderRecycleAdapter);
        folderRecycleAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        String folderTitle = folderStringList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENTS_TOOLBAR_TITLE, folderTitle);
        ArrayList<Song> songArrayList = MusicUtils.getSongListByTitleContainTypes(context, folderTitle, MusicUtils.TYPE_PATH);
        MusicUtils.setDisplaySongList(songArrayList);
        SongListFragmentWithBar songListFragmentWithBar = new SongListFragmentWithBar();
        songListFragmentWithBar.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frameLayout, songListFragmentWithBar).addToBackStack(null).commit();
    }
}
