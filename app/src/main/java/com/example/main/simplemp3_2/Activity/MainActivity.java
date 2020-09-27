package com.example.main.simplemp3_2.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
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
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.PagerAdapter.MyFragmentPagerAdapter;
import com.example.main.simplemp3_2.Utils.MusicConstants;
import com.example.main.simplemp3_2.Utils.MusicController;
import com.example.main.simplemp3_2.Dialog.CountDownDialog;
import com.example.main.simplemp3_2.Dialog.SongFilterDialog;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements MusicConstants {
    private final String TAG  = "MainActivity";
    private final static int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE};
    private Handler musicHandler = new Handler();
    private MusicController musicController;
    private IntentFilter intentFilter;
    private updateMainActivityUiReceiver updateMainActivityUiReceiver;
    private MyFragmentPagerAdapter MyFragmentPagerAdapter;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private RelativeLayout relativeLayout;
    private LinearLayout musicControlLayout ;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProgressBar mp3ProgressBar;
    private TextView txvSongTitle,txvSongArtist;
    private ImageButton btnPlaySong, btnPlayNext,btnRepeat;
    private View view;
    private CountDownDialog countDownDialog;

    public class updateMainActivityUiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    txvSongTitle.setText(musicController.getCurrentSong().getTitle());
                    txvSongArtist.setText(musicController.getCurrentSong().getArtist());
                    btnPlaySong.setImageResource(R.drawable.main_btn_pause);
                    mp3StartRunnable.run();
                    break;
                case ACTION_PAUSE:
                    btnPlaySong.setImageResource(R.drawable.main_btn_play);
                    musicHandler.removeCallbacks(mp3StartRunnable);
                    break;
                case ACTION_CONTINUE:
                    btnPlaySong.setImageResource(R.drawable.main_btn_pause);
                    mp3StartRunnable.run();
                    break;
            }
        }
    }

    private Runnable mp3StartRunnable = new Runnable() {
        @Override
        public void run() {
            if (musicController.isPlaying()) {
                mp3ProgressBar.setMax(musicController.getSongDuration());
                mp3ProgressBar.setProgress(musicController.getSongPlayingPosition());
                btnPlaySong.setImageResource(R.drawable.main_btn_pause);
            }
            musicController.updateRepeatImgButtonView(btnRepeat);
            musicHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission(PERMISSIONS);
        if (musicController.isBind) {
            initUserLayout();
        }
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_PAUSE);
        updateMainActivityUiReceiver = new updateMainActivityUiReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (musicController.isBind) {
            if (!musicController.isPlaying()) {
                musicController.updateRepeatImgButtonView(btnRepeat);
                mp3ProgressBar.setMax(musicController.getSongDuration());
                mp3ProgressBar.setProgress(musicController.getSongPlayingPosition());
                btnPlaySong.setImageResource(R.drawable.main_btn_play);
            }else {
                mp3StartRunnable.run();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(updateMainActivityUiReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(updateMainActivityUiReceiver);
        musicHandler.removeCallbacks(mp3StartRunnable);
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
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    private void requestPermission(String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
            } else {
                Log.i(TAG, "PERMISSION_GRANTED");
                musicController = MusicController.getInstance(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "REQUEST_PERMISSION");
            musicController = MusicController.getInstance(this);
        }else {
            finish();
        }
    }

    public void initUserLayout() {
        initToolBar();
        initDrawerLayout();
        initTabLayoutAndPagerAdapter();
        initMusicControllerToolBarView();
    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_main_menu);
        toolbar.getMenu().findItem(R.id.menu_sort).setTitle("123");

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
                                    //initSongList.sortByDefalut();
                                }else if (i == 1) {
                                    item.setTitle(srot[i]);
                                    //initSongList.sortByDate();
                                }
                                //initSongList.saveData();
                                //initSongList.initSongList();
                                refreshAllFragment();
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

    private void initDrawerLayout() {
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

    public void initTabLayoutAndPagerAdapter() {
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

        MyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(MyFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void initMusicControllerToolBarView() {
        musicControlLayout = findViewById(R.id.music_control_layout);
        btnPlaySong = findViewById(R.id.imgbtn_play);
        btnPlayNext = findViewById(R.id.imgbtn_next);
        btnRepeat = findViewById(R.id.btnRepeat);
        txvSongTitle = findViewById(R.id.txv_title);
        txvSongArtist = findViewById(R.id.txv_artist);

        musicControlLayout.setOnClickListener(musicControlListener);
        btnPlaySong.setOnClickListener(musicControlListener);
        btnPlayNext.setOnClickListener(musicControlListener);
        btnRepeat.setOnClickListener(musicControlListener);

        mp3ProgressBar = findViewById(R.id.mp3_seekbar);

        txvSongTitle.setSelected(true);

        if (musicController.getSongList().size() > 0) {
            txvSongTitle.setText(musicController.getCurrentSong().getTitle());
            txvSongArtist.setText(musicController.getCurrentSong().getArtist());
        }

    }

    View.OnClickListener musicControlListener = new View.OnClickListener() {
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
                    Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    public void refreshAllFragment() {
        MyFragmentPagerAdapter.refreshAllFragment();;
    }
}


