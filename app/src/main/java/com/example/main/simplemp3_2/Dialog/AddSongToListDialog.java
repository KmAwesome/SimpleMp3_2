package com.example.main.simplemp3_2.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.Song.Song;
import com.example.main.simplemp3_2.Song.SongListInFile;

import java.util.ArrayList;

public class AddSongToListDialog extends DialogFragment {
    private final String TAG = "AddSongToListDialog";
    private InitSongList initSongList;
    private ArrayList<Song> songList;
    private ArrayList<String> addedTitles;
    private String playListTitle;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        initSongList = new InitSongList(getActivity());
        songList = initSongList.getSongList();
        addedTitles = new ArrayList<>();
        ArrayList<String> songTitles = new ArrayList<>();
        for (int i=0; i<songList.size(); i++) {
            String title = songList.get(i).getTitle();
            songTitles.add(title);
        }
        final String[] titles = songTitles.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("請選擇要加入的歌曲");
        builder.setMultiChoiceItems(titles, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    addedTitles.add(titles[which]);
                }else if (addedTitles.contains(titles[which])) {
                    addedTitles.remove(titles[which]);
                }
            }
        });

        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SongListInFile songListInFile = new SongListInFile(getActivity());
                songListInFile.writeSongListToFile(playListTitle, addedTitles);
                if (context instanceof MainActivity) {
                    ((MainActivity)context).refreshAllFragment();
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    public void setPlayListTitle(String playListTitle) {
        this.playListTitle = playListTitle;
    }
}
