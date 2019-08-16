package com.example.main.simplemp3_2.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Activity.SettingActivity;
import com.example.main.simplemp3_2.Song.InitSongList;

public class SongFilterDialog extends AlertDialog.Builder {
    private final String TAG = "SongFilterDialog";
    private InitSongList initSongList;
    private final int [] filterTime = {0, 15, 30, 60, 90, 120};
    private final String[] time = {"不過濾", "15秒", "30秒", "1分鐘", "1分30秒", "2分鐘"};

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
                Toast.makeText(getContext().getApplicationContext(), "新增" + initSongList.getSongList().size() + "首歌曲至音樂庫", Toast.LENGTH_SHORT).show();
            }
        });
        return super.show();
    }

}
