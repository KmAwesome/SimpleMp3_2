package com.example.main.simplemp3_2.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.main.simplemp3_2.Adapters.PlayListRecycleAdapter;
import com.example.main.simplemp3_2.Dialog.AddSongsToPlayListDialog;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.FileUtils;
import com.example.main.simplemp3_2.Utils.MusicUtils;

import java.util.ArrayList;
import static com.example.main.simplemp3_2.Utils.MusicConstants.ARGUMENTS_TOOLBAR_TITLE;

public class PlayListRecycleFragment extends Fragment implements PlayListRecycleAdapter.OnItemClickListener {
    private String TAG = "PlayListFragment";
    private Context context;
    private RecyclerView playListRecyclerView;
    private PlayListRecycleAdapter playListRecycleAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageButton imageButtonAddPlayList;
    private ArrayList<String> playListStringList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        playListStringList = FileUtils.readPlayListData(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        DividerItemDecoration mDivider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        View view = inflater.inflate(R.layout.fragment_play_list,null);
        imageButtonAddPlayList = view.findViewById(R.id.image_button_add_play_list);
        imageButtonAddPlayList.setOnClickListener(new AddPlayListListener());
        playListRecyclerView = view.findViewById(R.id.play_list_recycle_view);
        playListRecyclerView.addItemDecoration(mDivider);
        playListRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        playListRecyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        playListRecycleAdapter = new PlayListRecycleAdapter(context, playListStringList);
        playListRecycleAdapter.setOnItemClickListener(this);
        playListRecyclerView.setAdapter(playListRecycleAdapter);
    }

    private class AddPlayListListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final EditText editText = new EditText(context);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setView(editText);
            alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ArrayList<String> playListArrayList = FileUtils.readPlayListData(context);
                    String playlist = editText.getText().toString();
                    playListArrayList.add(playlist);
                    FileUtils.savePlayListData(context, playListArrayList);
                    AddSongsToPlayListDialog addSongsToPlayListDialog = new AddSongsToPlayListDialog();
                    addSongsToPlayListDialog.show(getActivity().getSupportFragmentManager(), null);
                }
            });

            alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialog.create().show();
        }
    }

    @Override
    public void onItemClick(int position) {
        playListStringList = FileUtils.readPlayListData(context);
        String playListTitle = playListStringList.get(position);
        ArrayList<String> songTitlesInPlayListData = FileUtils.readSongsFromPlayListDataByTitle(context, playListTitle);
        ArrayList<Song> songArrayList = MusicUtils.getSongListContainTypeTitles(context, songTitlesInPlayListData);
        MusicUtils.setDisplaySongList(songArrayList);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENTS_TOOLBAR_TITLE, playListTitle);
        SongListFragmentWithBar songListFragmentWithBar = new SongListFragmentWithBar();
        songListFragmentWithBar.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frameLayout, songListFragmentWithBar).addToBackStack(null).commit();
    }

}