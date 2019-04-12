package com.example.main.simplemp3_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.main.simplemp3_2.Adapter.PagerAdapter;

import java.io.Serializable;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.main.simplemp3_2.Service.MusicService.PAUSE;
import static com.example.main.simplemp3_2.Service.MusicService.PLAY;
import static com.example.main.simplemp3_2.Service.MusicService.REPEAT;
import static com.example.main.simplemp3_2.Service.MusicService.REPEATONE;
import static com.example.main.simplemp3_2.Service.MusicService.SHUFFLE;
import static com.example.main.simplemp3_2.Service.MusicService.repeatMode;

public class MainActivity extends AppCompatActivity {
    public MusicController musicController;
    private InitSongList initSongList;
    private SongListInFile songListInFile;
    private final String TAG  = "MainActivity";
    private final static int PERMISSION_ALL = 1;
    private static Handler musicHandler = new Handler();
    private IntentFilter updateUIfilter;
    private PagerAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private RelativeLayout relativeLayout;
    private LinearLayout songViewLinearLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View view;
    public SeekBar mp3SeekBar;
    public TextView txv_showTitle,txv_showArtist;
    public ImageButton btnPlaySong, btnPlayNext,btnRepeat, btnPlayPrev;
    private String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE};

    private void initUpdateUiReceiver() {
        updateUIfilter = new IntentFilter();
        updateUIfilter.addAction(PLAY);
        updateUIfilter.addAction(PAUSE);
        registerReceiver(updateUI, updateUIfilter);
    }

    BroadcastReceiver updateUI = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PAUSE)) {
                btnPlaySong.setImageResource(R.drawable.btn_play);
            }else if (intent.getAction().equals(PLAY)) {
                txv_showTitle.setText(intent.getStringExtra("songTitle"));
                txv_showArtist.setText(intent.getStringExtra("songArtist"));
                mp3SeekBar.setMax(musicController.getSongDuration());
                mp3SeekBar.setProgress(musicController.getSongPlayingPos());
            }
            musicHandler.post(mp3Start);
        }
    };

    private Runnable mp3Start = new Runnable() {
        @Override
        public void run() {
            if (musicController.isPlaying()) {
                mp3SeekBar.setMax(musicController.getSongDuration());
                mp3SeekBar.setProgress(musicController.getSongPlayingPos());
                btnPlaySong.setImageResource(R.drawable.btn_pause);
            }
            updateRepeatImgButtonView();
            musicHandler.postDelayed(mp3Start,50);
        }
    };

    private void updateRepeatImgButtonView() {
        if (repeatMode.equals(REPEAT)) {
            btnRepeat.setImageResource(R.drawable.btn_repeat_all);
        }else if (repeatMode.equals(REPEATONE)) {
            btnRepeat.setImageResource(R.drawable.btn_repeat_one);
        }else if (repeatMode.equals(SHUFFLE)) {
            btnRepeat.setImageResource(R.drawable.btn_repeat_shffle);
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        requestPermission(PERMISSIONS);
    }

    private void requestPermission(String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
            } else {
                Log.i(TAG, "PERMISSION_GRANTED");
                musicController = new MusicController(this);
                initSongList = new InitSongList(this);
                songListInFile = new SongListInFile(this);
                initDrawer();
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
            musicController = new MusicController(this);
            initSongList = new InitSongList(this);
            songListInFile = new SongListInFile(this);
            initDrawer();
            initTabLayout();
            initView();
            initUpdateUiReceiver();
        }else {
            finish();
        }
    }

    private void initDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,drawerLayout, toolbar, R.string.drawer_set, R.string.drawer_set);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.set) {

                }else if (id == R.id.timer) {
                    CountDownDialog countDownDialog = new CountDownDialog();
                    countDownDialog.show(getFragmentManager(), "countDownDialog");
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    public void initTabLayout() {
        relativeLayout = findViewById(R.id.relativLayout);
        view = LayoutInflater.from(this).inflate(R.layout.fragment_play,null);
        relativeLayout.addView(view);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        tabLayout.setTabTextColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent) );

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void initView() {
        songViewLinearLayout = findViewById(R.id.songview_linearlayout);
        btnPlaySong = findViewById(R.id.imgbtn_play);
        btnPlayNext = findViewById(R.id.imgbtn_next);
        btnRepeat = findViewById(R.id.btnRepeat);
        txv_showTitle = findViewById(R.id.txv_title);
        txv_showArtist = findViewById(R.id.txv_artist);

        songViewLinearLayout.setOnClickListener(controlListener);
        btnPlaySong.setOnClickListener(controlListener);
        btnPlayNext.setOnClickListener(controlListener);
        btnRepeat.setOnClickListener(controlListener);

        mp3SeekBar = findViewById(R.id.mp3_seekbar);
        mp3SeekBar.setOnSeekBarChangeListener(mp3SeekBarChangeLIstener);

        txv_showTitle.setSelected(true);
        btnRepeat.setTag(REPEATONE);
    }

    View.OnClickListener controlListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgbtn_play:
                    if (musicController.getSongPlayingPos() == 0) {
                        musicController.playSong();
                    }else if (musicController.isPlaying()) {
                        musicController.pauseSong();
                    }else {
                        musicController.goSong();
                    }
                    break;
                case R.id.imgbtn_next:
                    musicController.playNext();
                    break;
                case R.id.btnRepeat:
                    musicController.setRepeatMode();
                    break;
                case R.id.songview_linearlayout:
                    Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                    startActivity(intent);
                    break;
            }
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        }else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            moveTaskToBack(true);
        }else{
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        if (updateUIfilter != null) {
            unregisterReceiver(updateUI);
        }
        if (musicController != null) {
            musicHandler.removeCallbacks(mp3Start);
            musicController.unbindMusicService();
        }

    }

}


