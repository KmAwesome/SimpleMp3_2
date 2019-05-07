package com.example.main.simplemp3_2.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.example.main.simplemp3_2.Adapter.PlayListAdapter;
import com.example.main.simplemp3_2.Dialog.AddSongToListDialog;
import com.example.main.simplemp3_2.Song.SongListInFile;
import com.example.main.simplemp3_2.R;
import java.util.ArrayList;

public class PlayListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private String TAG = "PlayListFragment";
    private Context context;
    private ListView playListView;
    private PlayListAdapter playListAdapter;
    private ImageButton imgbtnAdd;
    private ArrayList<String> songTitleList;
    private SongListInFile songListInFile;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        songListInFile = new SongListInFile(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist,null);
        imgbtnAdd = view.findViewById(R.id.imgbtn_add);
        playListView = view.findViewById(R.id.playListView);

        imgbtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(context);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setView(editText);
                alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        songListInFile.setTitleListFile(editText.getText().toString());
                        AddSongToListDialog addSongToListDialog = new AddSongToListDialog();
                        addSongToListDialog.setPlayListTitle(editText.getText().toString());
                        addSongToListDialog.show(getActivity().getSupportFragmentManager(), null);
                        onStart();
                    }
                });

                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.create().show();
            }
        });

        playListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        songTitleList = songListInFile.getTitleListFile();
        playListAdapter = new PlayListAdapter(context, songTitleList);
        playListView.setAdapter(playListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("playSongList", songListInFile.getSongListFile(songTitleList.get(i)));
        bundle.putString("playListTitle", songTitleList.get(i));
        DragSongFragment dragSongFragment = new DragSongFragment();
        dragSongFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.relativLayout, dragSongFragment).addToBackStack(null).commit();
    }

}