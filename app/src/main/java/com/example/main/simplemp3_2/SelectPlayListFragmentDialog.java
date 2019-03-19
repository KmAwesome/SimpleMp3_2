package com.example.main.simplemp3_2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.example.main.simplemp3_2.Fragment.PlayListFragment;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class SelectPlayListFragmentDialog extends DialogFragment{
    private final static String TAG = "SelectPlayListFragmentDialog";
    private SongListInFile songListInFile;
    private ArrayList<String> songTitleList;
    private ArrayList<String> songStringList = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        songListInFile = new SongListInFile(this.getActivity());
        songTitleList = songListInFile.readSongTitleInFile();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("請選擇播放清單");
        builder.setItems(songTitleList.toArray(new String[songTitleList.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                songListInFile.writeSongListToFile(songTitleList.get(i), songStringList);
            }
        });
        return builder.create();
    }

    public void addListToFile(ArrayList<String> songStringList) {
        this.songStringList = songStringList;
    }

    public void addSongToFile(String songTitle) {
        this.songStringList.add(songTitle);
    }


}
