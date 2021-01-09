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

import com.example.main.simplemp3_2.Adapters.AlbumRecycleAdapter;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.MusicUtils;
import java.util.ArrayList;
import static com.example.main.simplemp3_2.Utils.MusicConstants.ARGUMENTS_TOOLBAR_TITLE;

public class AlbumRecycleFragment extends Fragment implements AlbumRecycleAdapter.OnItemClickListener {
    private static final String TAG = "AlbumRecycleFragment";
    private Context context;
    private RecyclerView albumRecycleView;
    private RecyclerView.LayoutManager layoutManager;
    private AlbumRecycleAdapter albumAdapter;
    private ArrayList<String> albumStringList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        albumStringList = MusicUtils.getStringListByType(context, MusicUtils.TYPE_ALBUM);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_list_container, container,false);
        DividerItemDecoration mDivider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        layoutManager = new LinearLayoutManager(context);
        albumRecycleView = view.findViewById(R.id.recycleview_main);
        albumRecycleView.setHasFixedSize(true);
        albumRecycleView.setLayoutManager(layoutManager);
        albumRecycleView.addItemDecoration(mDivider);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        albumStringList = MusicUtils.getStringListByType(context, MusicUtils.TYPE_ALBUM);
        albumAdapter = new AlbumRecycleAdapter(context, albumStringList);
        albumAdapter.setOnItemClickListener(this);
        albumRecycleView.setAdapter(albumAdapter);
    }

    @Override
    public void onItemClick(int position) {
        String albumTitle = albumStringList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENTS_TOOLBAR_TITLE, albumTitle);
        ArrayList<Song> songArrayList = MusicUtils.getSongListByTitle(getContext(), albumTitle);
        MusicUtils.setDisplaySongList(songArrayList);
        SongListFragmentWithBar songListFragmentWithBar = new SongListFragmentWithBar();
        songListFragmentWithBar.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frameLayout, songListFragmentWithBar).addToBackStack(null).commit();
    }
}
