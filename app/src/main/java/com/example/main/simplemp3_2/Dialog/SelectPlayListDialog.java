package com.example.main.simplemp3_2.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.main.simplemp3_2.Song.SongListInFile;

import java.util.ArrayList;

public class SelectPlayListDialog extends DialogFragment {
    private final String TAG = "SelectPlayListDialog";
    private SongListInFile songListInFile;
    private ArrayList<String> songTitleList;
    private ArrayList<String> songStringList = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        songListInFile = new SongListInFile(this.getActivity());
        songTitleList = songListInFile.getTitleListFile();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("請選擇播放清單");
        builder.setItems(songTitleList.toArray(new String[songTitleList.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                songListInFile.setSongListFile(songTitleList.get(i), songStringList);
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
