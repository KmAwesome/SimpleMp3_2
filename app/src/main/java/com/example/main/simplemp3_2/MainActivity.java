package com.example.main.simplemp3_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.main.simplemp3_2.Service.MusicService.PAUSE;
import static com.example.main.simplemp3_2.Service.MusicService.PLAY;


public class MainActivity extends AppCompatActivity {
    private final String TAG  = "MainActivity";
    private final static int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE};
    private RelativeLayout relativeLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private InitSongList initSongList;
    private SongListInFile songListInFile;
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
                txv_showTitle.setText(initSongList.getSongList().get(musicController.getSongPos()).getTitle());
                txv_showArtist.setText(initSongList.getSongList().get(musicController.getSongPos()).getArtist());
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        initSongList = new InitSongList(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission(PERMISSIONS);
        initUpdateUiReceiver();
        initSongList = new InitSongList(this);
        songListInFile = new SongListInFile(this);
    }

    private void requestPermission(String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
            } else {
                Log.i(TAG, "PERMISSION_GRANTED");
                musicController = new MusicController(this);
                initTabLayout();
                initView();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "PERMISSION_GRANTED");
            musicController = new MusicController(this);
            initTabLayout();
            initView();
        }
    }

    private void initUpdateUiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY);
        intentFilter.addAction(PAUSE);
        registerReceiver(updateUI, intentFilter);
    }

    public void initTabLayout() {
        relativeLayout = findViewById(R.id.relativLayout);
        View fragment_play = LayoutInflater.from(this).inflate(R.layout.fragment_play,null);
        relativeLayout.addView(fragment_play);

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
        musicController = new MusicController(this);

        txv_showTitle = findViewById(R.id.txv_title);
        txv_showArtist = findViewById(R.id.txv_artist);

        imgbtn_playSong = findViewById(R.id.imgbtn_play);
        imgbtn_playNext = findViewById(R.id.imgbtn_next);
        imgbtn_repeat = findViewById(R.id.imgbtn_repeat);
        imgbtn_playSong.setOnClickListener(musicController);
        imgbtn_playNext.setOnClickListener(musicController);
        imgbtn_repeat.setOnClickListener(musicController);

        mp3SeekBar = findViewById(R.id.mp3_seekbar);
        mp3SeekBar.setOnSeekBarChangeListener(musicController);

        txv_showTitle.setSelected(true);
        imgbtn_playSong.setImageResource(R.drawable.play);
        imgbtn_repeat.setImageResource(R.drawable.repeat);;
    }

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
        super.onDestroy();
        unregisterReceiver(updateUI);
    }

}


