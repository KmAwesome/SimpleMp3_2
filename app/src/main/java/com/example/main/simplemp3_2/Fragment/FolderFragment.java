package com.example.main.simplemp3_2.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.main.simplemp3_2.Adapter.FolderAdapter;
import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.Song;

import java.io.*;
import java.util.ArrayList;

public class FolderFragment extends Fragment implements AdapterView.OnItemClickListener {
    private String TAG = "FolderFragment",folderName;
    private Context context;
    private InitSongList initSongList;
    private ListView listView;
    public FolderAdapter folderAdapter;
    private ArrayList<File> fileArrayList;
    private ArrayList<Song> songlist;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        initSongList = new InitSongList(context);
        songlist = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_song,container,false);
        listView = view.findViewById(R.id.song_list);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        folderName = ((TextView)view.findViewById(R.id.txv_folderName)).getText().toString();
        Bundle bundle = new Bundle();
        bundle.putSerializable("playSongList", getSongsInFolder());;
        SongFragment songFragment = new SongFragment();
        songFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.relativLayout,songFragment).addToBackStack(null).commit();
    }

    public ArrayList<Song> getSongsInFolder(){
        ArrayList<Song> folderSonglist = new ArrayList<>();
        if (folderSonglist != null){
            folderSonglist.clear();
        }
        for (int i=0; i<fileArrayList.size(); i++){
            if (fileArrayList.get(i).getParentFile().getName().contains(folderName)){
                folderSonglist.add(songlist.get(i));
            }
        }
        return folderSonglist;
    }

    @Override
    public void onStart() {
        super.onStart();
        songlist = initSongList.getSongList();
        folderAdapter = new FolderAdapter(context, getFolderName(), songlist);
        listView.setAdapter(folderAdapter);
    }

    public ArrayList<String> getFolderName() {
        String str[];
        File file;
        ArrayList<String> fileStr = new ArrayList<>();
        fileArrayList = new ArrayList<>();
        str = new String[songlist.size()];

        for(int i = 0; i< songlist.size(); i++){
            file = new File(songlist.get(i).getPath());
            str[i] = file.getParentFile().getName();
            fileArrayList.add(file);
        }

        for (int i=0; i<str.length; i++){
            if(!fileStr.contains(str[i])){
                fileStr.add(str[i]);
            }
        }
        return fileStr;
    }
    


}
