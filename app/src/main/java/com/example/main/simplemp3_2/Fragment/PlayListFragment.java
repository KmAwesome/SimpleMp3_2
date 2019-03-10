package com.example.main.simplemp3_2.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.example.main.simplemp3_2.Adapter.PlayListAdapter;
import com.example.main.simplemp3_2.InitSongList;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.MusicController;
import com.example.main.simplemp3_2.SongListInFile;
import com.example.main.simplemp3_2.Song;
import com.example.main.simplemp3_2.R;

import java.util.ArrayList;

public class PlayListFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener {
    private String TAG = "PlayListFragment";
    private Context context;
    private ListView playListView;
    private PlayListAdapter playListAdapter;
    private ImageButton imgbtnAdd;
    private Bundle bundle;
    private ArrayList<Song> songlist;
    private ArrayList<String> songTitleList;
    private SongListInFile songListInFile;
    private InitSongList initSongList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        initSongList = ((MainActivity)context).getInitSongList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = new Bundle();
        songlist = new ArrayList<>();
        songlist = initSongList.getSongList();
        songListInFile = new SongListInFile(this.getContext());
        songTitleList = songListInFile.readSongTitleInFile();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist,null);
        imgbtnAdd = view.findViewById(R.id.imgbtn_add);
        playListView = view.findViewById(R.id.playListView);
        imgbtnAdd.setOnClickListener(this);
        playListView.setOnItemClickListener(this);
        playListAdapter = new PlayListAdapter(context, songTitleList,this);
        playListView.setAdapter(playListAdapter);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        bundle.putSerializable("playSongList", songListInFile.getSongListInFile(songTitleList.get(i)));
        SongFragment songFragment = new SongFragment();
        songFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.relativLayout, songFragment).addToBackStack(null).commit();
    }

    @Override
    public void onClick(View view) {
        showAlertDialog();
    }

    private void showAlertDialog() {
        final EditText editText = new EditText(context);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(editText);
        alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                songListInFile.writeTitleListToFile(editText.getText().toString());
                playListAdapter.notifyDataSetChanged();
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