package com.example.main.simplemp3_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.main.simplemp3_2.Adapter.PagerAdapter;

import java.util.ArrayList;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.main.simplemp3_2.Service.MusicService.PAUSE;
import static com.example.main.simplemp3_2.Service.MusicService.PLAY;
import static com.example.main.simplemp3_2.Service.MusicService.REPEAT;
import static com.example.main.simplemp3_2.Service.MusicService.REPEATONE;

public class MainActivity extends AppCompatActivity {
    private final String TAG  = "MainActivity";
    private final static int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE};
    private RelativeLayout relativeLayout;
    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private InitSongList initSongList;
    private SongListInFile songListInFile;
    private Handler musicPlayHandler = new Handler();
    public SeekBar mp3SeekBar;
    public MusicController musicController;
    public TextView txv_showTitle,txv_showArtist;
    public ImageButton imgbtn_playSong, imgbtn_playNext,imgbtn_repeat, imgbtn_playPrev;

    BroadcastReceiver updateUI = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PAUSE)) {
                imgbtn_playSong.setImageResource(R.drawable.play);
            }else if (intent.getAction().equals(PLAY)) {
                txv_showTitle.setText(intent.getStringExtra("songTitle"));
                txv_showArtist.setText(intent.getStringExtra("songArtist"));
                mp3SeekBar.setMax(musicController.getSongDuration());
                mp3SeekBar.setProgress(musicController.getSongPlayingPos());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        musicController = new MusicController(this);
        initSongList = new InitSongList(this);
        songListInFile = new SongListInFile(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission(PERMISSIONS);
    }

    private void requestPermission(String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
            } else {
                Log.i(TAG, "PERMISSION_GRANTED");
                initTabLayout();
                initView();
                initUpdateUiReceiver();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "PERMISSION_GRANTED");
            initTabLayout();
            initView();
            initUpdateUiReceiver();
        }else {
            finish();
        }
    }

    public void initTabLayout() {
        relativeLayout = findViewById(R.id.relativLayout);
        view = LayoutInflater.from(this).inflate(R.layout.fragment_play,null);
        relativeLayout.addView(view);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void initView() {
        txv_showTitle = findViewById(R.id.txv_title);
        txv_showArtist = findViewById(R.id.txv_artist);

        imgbtn_playSong = findViewById(R.id.imgbtn_play);
        imgbtn_playNext = findViewById(R.id.imgbtn_next);
        imgbtn_repeat = findViewById(R.id.imgbtn_repeat);
        imgbtn_playSong.setOnClickListener(musicController);
        imgbtn_playNext.setOnClickListener(musicController);
        imgbtn_repeat.setOnClickListener(musicController);

        mp3SeekBar = findViewById(R.id.mp3_seekbar);
        mp3SeekBar.setOnSeekBarChangeListener(mp3SeekBarChangeLIstener);

        txv_showTitle.setSelected(true);
        imgbtn_repeat.setTag(REPEATONE);

    }

    private void initUpdateUiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY);
        intentFilter.addAction(PAUSE);
        registerReceiver(updateUI, intentFilter);
    }

    SeekBar.OnSeekBarChangeListener mp3SeekBarChangeLIstener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            musicController.setSongPlayingPos(seekBar.getProgress());
        }
    };

    public void startSeekBar() {
        musicPlayHandler.post(mp3Start);
    }

    private Runnable mp3Start = new Runnable() {
        @Override
        public void run() {
            if (musicController.isPlaying()) {
                mp3SeekBar.setMax(musicController.getSongDuration());
                mp3SeekBar.setProgress(musicController.getSongPlayingPos());
                imgbtn_playSong.setImageResource(R.drawable.notepause);
            }
            musicPlayHandler.postDelayed(mp3Start,50);
        }
    };

    public MusicController getMusicController() {
        return musicController;
    }

    public InitSongList getInitSongList() {
        return initSongList;
    }

    public SongListInFile getSongListInFile() {
        return songListInFile;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0){
            moveTaskToBack(true);
        }else{
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        unregisterReceiver(updateUI);
        musicPlayHandler.removeCallbacks(mp3Start);
    }

}


