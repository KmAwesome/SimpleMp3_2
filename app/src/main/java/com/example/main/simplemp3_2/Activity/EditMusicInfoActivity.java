package com.example.main.simplemp3_2.Activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.MusicUtils;


public class EditMusicInfoActivity extends AppCompatActivity {
    private final static String TAG = "MusicInfoActivity";
    private EditText edtTitle, edtArtist, edtAlbum, edtStyle;
    private String songTitle, songArtist, songAlbum, songStyle,songPath;
    private Button btnOK, btnCancel;
    private Song song;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_info);
        setTitle("編輯標籤");
        Bundle bundle = getIntent().getExtras();
        song = (Song) bundle.getSerializable("Song");
        songTitle = song.getTitle();
        songArtist = song.getArtist();
        songAlbum = song.getAlbum();
        songStyle = song.getStyle();
        songPath = song.getPath();
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
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.getId());
            ContentResolver contentResolver = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.IS_PENDING, 1);
            values.put(MediaStore.Audio.Media.TITLE, edtTitle.getText().toString());
            values.put(MediaStore.Audio.Media.ARTIST, edtArtist.getText().toString());
            values.put(MediaStore.Audio.Media.ALBUM, edtAlbum.getText().toString());
            values.put(MediaStore.Audio.Media.BOOKMARK,edtStyle.getText().toString());
            contentResolver.update(uri, values, null,null);
            values.clear();
            values.put(MediaStore.Audio.Media.IS_PENDING, 0);
            values.put(MediaStore.Audio.Media.TITLE, edtTitle.getText().toString());
            values.put(MediaStore.Audio.Media.ARTIST, edtArtist.getText().toString());
            values.put(MediaStore.Audio.Media.ALBUM, edtAlbum.getText().toString());
            values.put(MediaStore.Audio.Media.BOOKMARK,edtStyle.getText().toString());
            contentResolver.update(uri, values, null,null);
            MusicUtils.updateSongList(this);
        } catch (Exception e1) { e1.printStackTrace(); }
    }

}



