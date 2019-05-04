package com.example.main.simplemp3_2;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.io.File;

public class MyTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private String path = Environment.getExternalStorageDirectory().toString();

    public MyTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getMusicFile();
        return null;
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
                            scanFile(file.getPath());
                        }
                    }
                }
            }
        }
    }

    private void scanFile(String path) {
        final String[] mimeType = new String[]{MimeTypeMap.getSingleton().getMimeTypeFromExtension(".mp3")};
        MediaScannerConnection.scanFile(context.getApplicationContext(), new String[] { path }, mimeType, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
            }
        });
    }

}
