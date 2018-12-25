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
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.Model.Song;
import com.example.main.simplemp3_2.Model.SongPlayList;
import com.example.main.simplemp3_2.R;


import java.io.ObjectOutputStream;

import java.util.ArrayList;


import static com.example.main.simplemp3_2.MainActivity.playList;

public class PlayListFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener {
    private static final String TAG = "PlayListFragment";
    private Context context;
    private ListView playListView;
    private PlayListAdapter playListAdapter;
    private LayoutInflater layoutInflater;
    private ImageButton imgbtnAdd;
    private Bundle bundle;
    private ArrayList<Song> songlist;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = new Bundle();
        songlist = new ArrayList<>();
        songlist = ((MainActivity)getActivity()).getSonglist();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_playlist,null);
        imgbtnAdd = v.findViewById(R.id.imgbtn_add);
        imgbtnAdd.setOnClickListener(this);
        playListView = v.findViewById(R.id.playListView);
        playListAdapter = new PlayListAdapter(context, songlist,this);
        playListView.setAdapter(playListAdapter);
        playListView.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        showAlertDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        bundle.putSerializable("playSongList",getSongsInPlayList(playList.get(i).getPlayListSong()));
        SongFragment songFragment = new SongFragment();
        songFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.relativLayout, songFragment).addToBackStack(null).commit();
    }

    private void showAlertDialog() {
        final EditText editText = new EditText(context);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(editText);
        alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SongPlayList songPlayList = new SongPlayList();
                songPlayList.setTitle(editText.getText().toString());
                playList.add(songPlayList);
                writePlayListToFile();
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

    private ArrayList<Song> getSongsInPlayList(ArrayList<String> songTitleList) {
        ArrayList<Song> songs,playSongs;
        playSongs = new ArrayList<>();
        songs = ((MainActivity)getActivity()).getSonglist();
        for (int i=0; i<songs.size(); i++) {
            if (songTitleList.contains(songs.get(i).getTitle())){
                playSongs.add(songs.get(i));
            }
        }
        return playSongs;
    }

    public void writePlayListToFile() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput("PlayList.bin",Context.MODE_PRIVATE));
            objectOutputStream.writeObject(playList);
            objectOutputStream.close();
            Log.i(TAG, "writePlayListToFile : playList.size = " + playList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
