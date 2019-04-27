package com.example.main.simplemp3_2.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import com.example.main.simplemp3_2.Song.InitSongList;

public class SongFilterDialog extends AlertDialog.Builder {
    private final String TAG = "SongFilterDialog";
    final String[] time = {"不過濾", "15秒", "30秒", "1分鐘", "1分30秒", "2分鐘"};
    final int [] filterTime = {0, 15, 30, 60, 90, 120};
    private InitSongList initSongList;
    private TextView txvFilterTime;

    public SongFilterDialog(Context context) {
        super(context);
        initSongList = new InitSongList(context);
    }

    @Override
    public AlertDialog show() {
        setTitle("略過時間小於");
        setItems(time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                initSongList.setFilterTime(filterTime[i]);
                initSongList.saveData();
                initSongList.initSongList();
                if (txvFilterTime != null) {
                    txvFilterTime.setText(time[i]);
                }
            }
        });
        return super.show();
    }

    public void setUpdateView(TextView txvFilterTime) {
        this.txvFilterTime = txvFilterTime;
    }

}
