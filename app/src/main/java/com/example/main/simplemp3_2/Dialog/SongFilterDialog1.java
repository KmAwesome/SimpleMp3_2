package com.example.main.simplemp3_2.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.Song.SongListInFile;

import java.io.File;
import java.util.ArrayList;

public class SongFilterDialog1 extends DialogFragment {
    private final String TAG = "SongFilterDialog";

    private Context context;

    private InitSongList initSongList;
    private TextView txvFilterTime;

    private final int [] filterTime = {0, 15, 30, 60, 90, 120};
    private final String[] time = {"不過濾", "15秒", "30秒", "1分鐘", "1分30秒", "2分鐘"};
    private String path = Environment.getExternalStorageDirectory().toString();

    private ArrayList<File> files;
    private String filePath;
    private int position;

    private Handler mHandler;
    private HandlerThread mHandlerThread;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initSongList = new InitSongList(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("略過時間小於");
        builder.setItems(time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                position = i;
                mHandlerThread = new HandlerThread("handler thread");
                mHandlerThread.start();
                mHandler = new Handler(mHandlerThread.getLooper());
                mHandler.post(runnable);
            }
        });
        return builder.create();
    }

    public void setUpdateView(TextView txvFilterTime) {
        this.txvFilterTime = txvFilterTime;
    }

    public void getMusicFile() {
        File directory = new File(path);
        if (directory.exists()) {
            if (directory.listFiles() != null) {
                for (File file : directory.listFiles()) {
                    if (file.isDirectory()) {
                        path = file.getPath();
                        getMusicFile();
                    }else {
                        if (file.getName().endsWith(".mp3")) {
                            files.add(file);
                        }
                    }
                }
            }
        }
    }

    private void scanFile(String path) {
        String[] mimeType = new String[]{MimeTypeMap.getSingleton().getMimeTypeFromExtension(".mp3")};
        MediaScannerConnection.scanFile(context.getApplicationContext(), new String[] { path }, mimeType, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                if (path.equals(filePath)) {
                    initSongList.setFilterTime(filterTime[position]);
                    initSongList.saveData();
                    initSongList.initSongList();

                    if (txvFilterTime != null) {
                        txvFilterTime.setText(time[position]);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "新增" + initSongList.getSongList().size() + "首歌曲至音樂庫", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            getMusicFile();

            filePath = files.get(files.size()-1).getPath();

            for (File file : files) {
                scanFile(file.getPath());
            }
        }
    };



}
