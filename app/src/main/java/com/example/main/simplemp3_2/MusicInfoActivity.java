package com.example.main.simplemp3_2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MusicInfoActivity extends AppCompatActivity {
    private final static String TAG = "MusicInfoActivity";
    private EditText edtTitle, edtArtist, edtAlbum, edtStyle;
    private String songTitle, songArtist, songAlbum, songStyle,songPath;
    private Button btnOK, btnCancel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_info);
        setTitle("編輯標籤");

        Intent intent = this.getIntent();
        songTitle = intent.getStringExtra("Title");
        songArtist = intent.getStringExtra("Artist");
        songAlbum = intent.getStringExtra("Album");
        songStyle = intent.getStringExtra("Style");
        songPath = intent.getStringExtra("Path");

        edtTitle = findViewById(R.id.edtTitle);
        edtTitle.setText(songTitle);

        edtArtist = findViewById(R.id.edtArtist);
        edtArtist.setText(songArtist);

        edtAlbum = findViewById(R.id.edtAlbum);
        edtAlbum.setText(songAlbum);

        edtStyle = findViewById(R.id.edtStyle);
        edtStyle.setText(songStyle);

        btnOK = findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTag();
                finish();
            }
        });

        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void saveTag() {
        try {
            ContentResolver contentResolver = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.TITLE, edtTitle.getText().toString());
            values.put(MediaStore.Audio.Media.ARTIST, edtArtist.getText().toString());
            values.put(MediaStore.Audio.Media.ALBUM, edtAlbum.getText().toString());
            values.put(MediaStore.Audio.Media.BOOKMARK,edtStyle.getText().toString());
            String[] str = {songTitle};
            contentResolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, MediaStore.Audio.Media.TITLE + "=?", str);
        } catch (Exception e1) { e1.printStackTrace(); }
    }
}



