package com.example.main.simplemp3_2.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Utils.FileUtils;
import com.example.main.simplemp3_2.Utils.MusicUtils;

import java.io.File;
import java.util.ArrayList;

public class SongFilterDialog extends AlertDialog.Builder {
    private final String TAG = "SongFilterDialog";
    private Context context;
    private final int [] filterTime = {0, 15, 30, 60, 90, 120};
    private final String[] time = {"不過濾", "15秒", "30秒", "1分鐘", "1分30秒", "2分鐘"};
    private ArrayList<String> pathStringList;
    private final static String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath();

    public SongFilterDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public AlertDialog show() {
        setTitle("略過時間小於");
        setItems(time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                seachAllSongs();
                MusicUtils.setSongDurationInSeconds(filterTime[i]);
            }
        });
        return super.show();
    }

    public void seachAllSongs() {
        pathStringList = new ArrayList<>();
        if (MEDIA_PATH != null) {
            File home = new File(MEDIA_PATH);
            File[] listFiles = home.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    }
                }
            }
        }
        for (String path : pathStringList) {
            scanFile(path);
        }
    }

    private void scanDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        if (file.getName().endsWith(".mp3")) {
                            pathStringList.add(file.getPath());
                        }
                    }
                }
            }
        }
    }

    private void scanFile(String path) {
        final String[] mimeType = new String[]{MimeTypeMap.getSingleton().getMimeTypeFromExtension(".mp3")};
        MediaScannerConnection.scanFile(context, new String[] {path}, mimeType,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(TAG, "onScanCompleted: " + path);
                        if (path.equals(pathStringList.get(pathStringList.size() - 1))) {
                            if (context instanceof MainActivity) {
                                ((MainActivity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MusicUtils.updateSongList(context);
                                        ((MainActivity) context).refreshAllFragment();
                                        int songs = MusicUtils.getSongList(context).size();
                                        Toast.makeText(context.getApplicationContext(), "新增" + songs +
                                                "首歌曲至音樂庫", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                throw new RuntimeException(context.toString() + "Use Only MainActivity");
                            }
                        }

                    }
                });
    }

}
