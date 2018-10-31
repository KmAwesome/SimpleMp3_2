package com.example.main.simplemp3_2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.example.daniel.myjar.Mp3File;


public class MusicInfo extends AppCompatActivity {
    private EditText edtTitle, edtArtist, edtAlbum, edtStyle;
    private String songTitle, songArtist, songAlbum, songStyle,songPath;
    private long songId,songDuration;
    private ImageButton imgbtnOK, imgbtnCancel;
    private final static String TAG = "MusicInfo";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_info);

        edtTitle = findViewById(R.id.edtTitle);
        edtArtist = findViewById(R.id.edtArtist);
        edtAlbum = findViewById(R.id.edtAlbum);
        edtStyle = findViewById(R.id.edtStyle);
        imgbtnOK = findViewById(R.id.imgbtnOK);
        imgbtnCancel = findViewById(R.id.imgbtnCancel);

        imgbtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TAG", "onClick: " + songPath);
                saveTag();
            }
        });

        imgbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Intent intent = this.getIntent();
        songTitle = intent.getStringExtra("Title");
        songArtist = intent.getStringExtra("Artist");
        songAlbum = intent.getStringExtra("Album");
        songStyle = intent.getStringExtra("Style");
        songId = intent.getLongExtra("Id",0);
        songDuration = intent.getLongExtra("Duration",0);
        songPath = intent.getStringExtra("Path");

        edtTitle.setText(songTitle);
        edtArtist.setText(songArtist);
        edtAlbum.setText(songAlbum);
        edtStyle.setText(songStyle);
    }

    private void saveTag() {
        try {
            Mp3File mp3file = new Mp3File(songPath);
            System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
            System.out.println("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
            System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
            System.out.println("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
            System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
            System.out.println("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));
        } catch (Exception e1) { e1.printStackTrace(); }
    }
}



