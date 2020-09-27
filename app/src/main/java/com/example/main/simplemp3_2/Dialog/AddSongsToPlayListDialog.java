package com.example.main.simplemp3_2.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Utils.FileUtils;
import com.example.main.simplemp3_2.Utils.MusicUtils;

import java.util.ArrayList;

public class AddSongsToPlayListDialog extends DialogFragment {
    private final String TAG = "AddPlayListToFileDialog";
    private ArrayList<String> songStringList;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        songStringList = MusicUtils.getStringListByType(context, MusicUtils.TYPE_TITLE);
        final String[] allSong = songStringList.toArray(new String[songStringList.size()]);
        final ArrayList<String> playLists = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("請選擇要加入的歌曲");
        builder.setMultiChoiceItems(allSong, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    playLists.add(allSong[which]);
                }
            }
        });

        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> playListTitles = FileUtils.readPlayListData(context);
                FileUtils.writeSongsToPlayListData(context, playListTitles.get(playListTitles.size() - 1), playLists);
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

}
