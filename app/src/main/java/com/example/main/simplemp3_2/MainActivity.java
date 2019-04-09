package com.example.main.simplemp3_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.main.simplemp3_2.Service.MusicService.PAUSE;
import static com.example.main.simplemp3_2.Service.MusicService.PLAY;
import static com.example.main.simplemp3_2.Service.MusicService.REPEATONE;

public class MainActivity extends AppCompatActivity {
    private final String TAG  = "MainActivity";
    private final static int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE};
    private RelativeLayout relativeLayout;
    private LinearLayout songViewLinearLayout;
    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private InitSongList initSongList;
    private SongListInFile songListInFile;
    public SeekBar mp3SeekBar;
    public MusicController musicController;
    public TextView txv_showTitle,txv_showArtist;
    public ImageButton imgbtn_playSong, imgbtn_playNext,imgbtn_repeat, imgbtn_playPrev;
    private IntentFilter updateUIfilter;
    private PagerAdapter pagerAdapter;
    private boolean permissionGranted;
    private DrawerLayout drawerLayout;

    BroadcastReceiver updateUI = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PAUSE)) {
                imgbtn_playSong.setImageResource(R.drawable.btn_play);
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
                permissionGranted = true;
                musicController = new MusicController(this);
                initSongList = new InitSongList(this);
                songListInFile = new SongListInFile(this);
                initTabLayout();
                initView();
                initUpdateUiReceiver();
                initDrawer();
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
        songViewLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });

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
        updateUIfilter = new IntentFilter();
        updateUIfilter.addAction(PLAY);
        updateUIfilter.addAction(PAUSE);
        registerReceiver(updateUI, updateUIfilter);
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
            musicController.removeHandlerCallback();
            musicController.unbindMusicService();
        }

    }

}


