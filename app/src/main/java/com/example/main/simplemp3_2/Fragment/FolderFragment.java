package com.example.main.simplemp3_2.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.main.simplemp3_2.Adapter.FolderAdapter;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Model.Song;

import java.io.*;
import java.util.ArrayList;

public class FolderFragment extends Fragment implements AdapterView.OnItemClickListener {
    private String TAG = "FolderFragment",folderName;
    private ListView listView;
    public FolderAdapter folderAdapter;
    private Context context;
    private ArrayList<File> fileArrayList;
    private ArrayList<Song> songlist,folderSonglist;
    private Bundle bundle;
    private SongFragment songFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = new Bundle();
        songlist = new ArrayList<>();
        folderSonglist = new ArrayList<>();
        songlist = ((MainActivity)getActivity()).getSonglist();
        folderAdapter = new FolderAdapter(context,getFolderName());
        songFragment = new SongFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.m_listview,container,false);
        listView = view.findViewById(R.id.song_list);
        listView.setAdapter(folderAdapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        folderName = ((TextView)view.findViewById(R.id.txv_folderName)).getText().toString();
        bundle.putSerializable("playSongList",getSongsInFolder());;
        songFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.relativLayout,songFragment).addToBackStack(null).commit();
    }
    
    public ArrayList<String> getFolderName() {
        String str[];
        File file;
        ArrayList<String> fileStr = new ArrayList<>();
        fileArrayList = new ArrayList<>();

        if (songlist == null) songlist = ((MainActivity)getActivity()).getSonglist();
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
    
    public ArrayList<Song> getSongsInFolder(){

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && songlist != null) {
            Log.i(TAG, "setUserVisibleHint: true");
            getFolderName();
            folderAdapter.notifyDataSetChanged();
        } else {
            Log.i(TAG, "setUserVisibleHint: false");
        }
    }




}
