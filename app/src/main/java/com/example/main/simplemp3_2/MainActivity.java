package com.example.main.simplemp3_2;

import android.Manifest;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.main.simplemp3_2.Adapter.PagerAdapter;
import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.Song.MusicController;
import com.example.main.simplemp3_2.Dialog.CountDownDialog;
import com.example.main.simplemp3_2.Dialog.SongFilterDialog;
import com.example.main.simplemp3_2.Widget.AppWidgetProviderController;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.main.simplemp3_2.Service.MusicService.ACTION_START;
import static com.example.main.simplemp3_2.Song.InitSongList.musicFilter;
import static com.example.main.simplemp3_2.Service.MusicService.ACTION_PAUSE;
import static com.example.main.simplemp3_2.Service.MusicService.ACTION_PLAY;

public class MainActivity extends AppCompatActivity {
    private final String TAG  = "MainActivity";
    public MusicController musicController;
    private InitSongList initSongList;
    private Toolbar toolbar;
    private final static int PERMISSION_ALL = 1;
    private Handler musicHandler = new Handler();
    //private static Handler musicScanHanlder = new Handler();
    private PagerAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private RelativeLayout relativeLayout;
    private LinearLayout controlLayout ;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View view;
    private ProgressBar mp3ProgressBar;
    public TextView txvSongTitle,txvSongArtist;
    public ImageButton btnPlaySong, btnPlayNext,btnRepeat;
    private String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE};
    private CountDownDialog countDownDialog;
    private static boolean isBackground, isRunning;

    private void initUpdateUiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_START);
        registerReceiver(UIbroadcastReceiver, intentFilter);
    }

    BroadcastReceiver UIbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!isRunning) {
                mp3Start.run();
            }

            if (intent.getAction().equals(ACTION_PAUSE)) {
                btnPlaySong.setImageResource(R.drawable.main_btn_play);
            }else if (intent.getAction().equals(ACTION_PLAY)) {
                btnPlaySong.setImageResource(R.drawable.main_btn_pause);
            }

            mp3ProgressBar.setMax(musicController.getSongDuration());
            mp3ProgressBar.setProgress(musicController.getSongPlayingPosition());
            txvSongTitle.setText(intent.getStringExtra("songTitle"));
            txvSongArtist.setText(intent.getStringExtra("songArtist"));
        }
    };

    private Runnable mp3Start = new Runnable() {
        @Override
        public void run() {
            if (musicController.isPlaying()) {
                mp3ProgressBar.setMax(musicController.getSongDuration());
                mp3ProgressBar.setProgress(musicController.getSongPlayingPosition());
                btnPlaySong.setImageResource(R.drawable.main_btn_pause);
            }
            isRunning = true;
            musicController.updateRepeatImgButtonView(btnRepeat);
            musicHandler.postDelayed(this, 50);
        }
    };

    /*
    Runnable musicScanRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (!isBackground) {
                    num++;
                    Log.i(TAG, "run: " + musicScanHanlder + " , " + musicController.getSongList().size() + ", " + num);
                }
                musicScanHanlder.postDelayed(this, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        requestPermission(PERMISSIONS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isBackground = false;
        isRunning = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isBackground = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (UIbroadcastReceiver != null){
                unregisterReceiver(UIbroadcastReceiver);
            }

            if (musicController != null) {
                musicHandler.removeCallbacks(mp3Start);
                musicController.unbindMusicService();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void requestPermission(String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
            } else {
                Log.i(TAG, "PERMISSION_GRANTED");
                musicController = new MusicController(this);
                initSongList = new InitSongList(this);
                initToolBar();
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
            initToolBar();
            initDrawer();
            initTabLayout();
            initView();
            initUpdateUiReceiver();
        }else {
            finish();
        }
    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_main_menu);

        SharedPreferences sharedPreferences = getSharedPreferences(musicFilter, Context.MODE_PRIVATE);
        String sortString = sharedPreferences.getString("SORTSTRING", "排列");
        toolbar.getMenu().findItem(R.id.menu_sort).setTitle(sortString);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_sort:
                        final String srot[] = {"默認", "日期"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("依方法排序");
                        builder.setItems(srot, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    item.setTitle(srot[i]);
                                    initSongList.setSortBy(InitSongList.sortBy.sortByDefault);
                                }else if (i == 1) {
                                    item.setTitle(srot[i]);
                                    initSongList.setSortBy(InitSongList.sortBy.sortByDate);
                                }
                                initSongList.saveData();
                                initSongList.initSongList();
                                pagerAdapter.refreshAllFragment();
                            }
                        }).show();
                        break;
                    case R.id.menu_set :
                        View view = findViewById(R.id.menu_set);
                        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                        popupMenu.getMenuInflater().inflate(R.menu.toolbar_menu_set, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.time_set :
                                        countDownDialog.show(getSupportFragmentManager(), null);
                                        break;
                                    case R.id.song_scan :
                                        SongFilterDialog songFilterDialog = new SongFilterDialog(MainActivity.this);
                                        songFilterDialog.show();
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                        break;
                }
                return false;
            }
        });
    }

    private void initDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_set, R.string.drawer_set);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.timer);
        countDownDialog = new CountDownDialog();
        countDownDialog.setMenuItem(menuItem);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.set) {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                }else if (id == R.id.timer) {
                    countDownDialog.show(getSupportFragmentManager(), "countDownDialog");
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    public void initTabLayout() {
        relativeLayout = findViewById(R.id.relativLayout);
        view = LayoutInflater.from(this).inflate(R.layout.fragment_container,null);
        relativeLayout.addView(view);

        tabLayout = view.findViewById(R.id.tablayout);
        viewPager = view.findViewById(R.id.viewpager);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        tabLayout.setTabTextColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent) );
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void initView() {
        controlLayout = findViewById(R.id.music_control_layout);
        btnPlaySong = findViewById(R.id.imgbtn_play);
        btnPlayNext = findViewById(R.id.imgbtn_next);
        btnRepeat = findViewById(R.id.btnRepeat);
        txvSongTitle = findViewById(R.id.txv_title);
        txvSongArtist = findViewById(R.id.txv_artist);
        controlLayout .setOnClickListener(controlListener);
        btnPlaySong.setOnClickListener(controlListener);
        btnPlayNext.setOnClickListener(controlListener);
        btnRepeat.setOnClickListener(controlListener);

        mp3ProgressBar = findViewById(R.id.mp3_seekbar);

        txvSongTitle.setSelected(true);

    }

    View.OnClickListener controlListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgbtn_play:
                    if (musicController.getSongPlayingPosition() == 0) {
                        musicController.playSong();
                    }else if (musicController.isPlaying()) {
                        musicController.pauseSong();
                        return;
                    }else {
                        musicController.continueSong();
                    }
                    break;
                case R.id.imgbtn_next:
                    musicController.nextSong();
                    break;
                case R.id.btnRepeat:
                    musicController.setRepeatMode(view);
                    break;
                case R.id.music_control_layout:
                    Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    public MusicController getMusicController() {
        return musicController;
    }

    public void refreshAllFragment() {
        pagerAdapter.refreshAllFragment();;
    }
}


