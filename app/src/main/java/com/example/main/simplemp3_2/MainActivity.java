package com.example.main.simplemp3_2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.main.simplemp3_2.Fragment.ArtistFragment;
import com.example.main.simplemp3_2.Fragment.PlayListFragment;
import com.example.main.simplemp3_2.Model.InitSongList;
import com.example.main.simplemp3_2.Model.Song;
import com.example.main.simplemp3_2.Model.SongPlayList;
import com.example.main.simplemp3_2.Service.MusicService;
import com.example.main.simplemp3_2.Adapter.PagerAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.main.simplemp3_2.Fragment.PlayFragment.songPosn;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener,ServiceConnection{
    private String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE};
    private String TAG = "MainActivity";
    private MusicService musicSrv;
    private Intent playIntent;
    private RelativeLayout relativeLayout;
    private SeekBar mp3SeekBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private InitSongList initSongList;
    private boolean musicBond;
    public final static int PERMISSION_ALL = 1;
    public final static String REPEAT = "Repeat";
    public final static String REPEATONE = "RepeatOne";
    public final static String SHUFFLE = "Shuffle";
    public static ArrayList<SongPlayList> playList;
    public ArrayList<Song> songlist,tempSonglist;
    public TextView txv_showTitle,txv_showArtist;
    public ImageButton imgbtn_playSong, imgbtn_playNext,imgbtn_repeat, imgbtn_playPrev;
    private Handler musicHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission(this,PERMISSIONS);
    }

    public void getPermission(Context context,String...perssions){
        if(context != null && perssions != null){
            for(String permission : perssions){
                if(ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                }else{
                    Log.d(TAG, "getPermission:  Permission Granted");
                    initControllerSongList();
                    initTabLayout();
                    if(playIntent == null) {
                        playIntent = new Intent(this, MusicService.class);
                        bindService(playIntent, this, Context.BIND_AUTO_CREATE);
                        startService(playIntent);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_ALL:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initControllerSongList();
                    initTabLayout();
                    if(playIntent == null){
                        playIntent = new Intent(this, MusicService.class);
                        bindService(playIntent,this, Context.BIND_AUTO_CREATE);
                        startService(playIntent);
                    }
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("此權限僅讀取您手機中的音樂檔案，如要使用播放器請按是，並請選擇 ALLOW ")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{READ_EXTERNAL_STORAGE},
                                            PERMISSION_ALL);
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                }
                return;
            }
        }
    }

    public void initTabLayout(){
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

    private void initControllerSongList(){
        initSongList = new InitSongList(this);
        readPlayList(this);
        songlist = new ArrayList<>();
        tempSonglist = new ArrayList<>();
        songlist = initSongList.getSongList();
        txv_showTitle = findViewById(R.id.txv_title);
        txv_showArtist = findViewById(R.id.txv_artist);
        imgbtn_playSong = findViewById(R.id.imgbtn_play);
        imgbtn_playNext = findViewById(R.id.imgbtn_next);
        imgbtn_repeat = findViewById(R.id.imgbtn_repeat);
        imgbtn_playSong.setImageResource(R.drawable.play);
        imgbtn_repeat.setImageResource(R.drawable.repeat);
        imgbtn_playSong.setTag("Start");
        imgbtn_repeat.setTag(REPEAT);
        mp3SeekBar = findViewById(R.id.sb);
        txv_showTitle.setSelected(true);
        mp3SeekBar.setOnSeekBarChangeListener(this);
        imgbtn_playSong.setOnClickListener(this);
        imgbtn_playNext.setOnClickListener(this);
        imgbtn_repeat.setOnClickListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.SENDSONG_TEXT);
        registerReceiver(setSongTextReceiver,intentFilter);
    }

    public void readPlayList(Context context) {
        playList = new ArrayList<>();
        File file = context.getFileStreamPath("PlayList.bin");
        if (file.exists()){
            try {
                InputStream inputStream = context.openFileInput("PlayList.bin");
                if (inputStream != null) {
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    playList = (ArrayList<SongPlayList>)objectInputStream.readObject();
                    Log.i(TAG, "readPlayList : " + playList.size());
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void playSong(){
        if (songlist.size() == 0) return;
        musicSrv.playSong();
        imgbtn_playSong.setTag("Play");
        setViewTextAndmusicHandler();
    }

    public void goSong(){
        if (songlist.size() == 0) return;
        musicSrv.go();
        musicHandler.post(mp3Start);
        setViewImage();
    }

    public void pauseSong(){
        if (songlist.size() == 0) return;
        musicSrv.pausePlayer();
        musicHandler.removeCallbacks(mp3Start);
        setViewImage();
    }

    public void playPrev(){
        if (songlist.size() == 0) return;
       musicSrv.playPrev();
       setViewTextAndmusicHandler();
    }

    public void playNext(){
        if (songlist.size() == 0) return;
        musicSrv.playNext();
        setViewTextAndmusicHandler();
    }

    public void setSongPos(int pos){
        if (songlist.size() == 0) return;
        musicSrv.setSongPos(pos);
    }

    public boolean isPlaying() {
        return musicSrv.isPlaying();
    }

    public boolean tagIsPlaying(){
        return musicSrv.tagIsPlaying();
    }

    public void setViewTextAndmusicHandler(){
        musicHandler.removeCallbacks(mp3Start);
        musicHandler.post(mp3Start);
        txv_showTitle.setText(songlist.get(songPosn).getTitle());
        txv_showArtist.setText(songlist.get(songPosn).getArtist());
        setViewImage();
    }

    public void setViewImage() {
        if (tagIsPlaying()){
            imgbtn_playSong.setImageResource(R.drawable.notepause);
        }else if(!tagIsPlaying()){
            imgbtn_playSong.setImageResource(R.drawable.play);
        }
    }

    public void refreshSongList(){
        if (tempSonglist == null){ tempSonglist = new ArrayList<>(); }
        tempSonglist = initSongList.getSongList();
    }

    public void setSonglist(ArrayList<Song> songlist){
        if (this.songlist == null) {this.songlist = new ArrayList<>();}
        this.songlist = songlist;
        musicSrv.setSongList(songlist);
    }

    public ArrayList<Song> getSonglist() {
        if (songlist == null) {songlist = new ArrayList<>();}
        songlist = initSongList.getSongList();
        return songlist;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        unregisterReceiver(setSongTextReceiver);
        System.exit(0);
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
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        musicSrv.seek(mp3SeekBar.getProgress());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgbtn_play:
                if (isPlaying()) {
                    pauseSong();
                } else if (imgbtn_playSong.getTag().equals("Start")){
                    playSong();
                } else {
                    goSong();
                }
                break;
            case R.id.imgbtn_next:
                playNext();
                break;
            case R.id.imgbtn_repeat:
                switch (imgbtn_repeat.getTag().toString()){
                    case REPEAT:
                        musicSrv.setRepeatMode(REPEATONE);
                        imgbtn_repeat.setImageResource(R.drawable.repeat_one);
                        imgbtn_repeat.setTag(REPEATONE);
                        break;
                    case REPEATONE:
                        musicSrv.setRepeatMode(SHUFFLE);
                        imgbtn_repeat.setImageResource(R.drawable.shffle);
                        imgbtn_repeat.setTag(SHUFFLE);
                        break;
                    case SHUFFLE:
                        musicSrv.setRepeatMode(REPEAT);
                        imgbtn_repeat.setImageResource(R.drawable.repeat);
                        imgbtn_repeat.setTag(REPEAT);
                        break;
                }
                break;
        }
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(TAG, "ServiceConnected");
        MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
        musicSrv = binder.getService();
        musicBond = true;
        setSonglist(songlist);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicBond = false;
    }

    Runnable mp3Start = new Runnable() {
        @Override
        public void run() {
            if(musicSrv != null) {
                if (musicSrv.isPlaying()) {
                    mp3SeekBar.setMax(musicSrv.getDur() - 999);
                    Message msg = musicHandler.obtainMessage();
                    msg.arg1 = musicSrv.getPosn();
                    musicHandler.sendMessage(msg);
                    mp3SeekBar.setProgress(msg.arg1);
                }
            }
            musicHandler.postDelayed(mp3Start,50);
        }
    };

    BroadcastReceiver setSongTextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setViewTextAndmusicHandler();
        }
    };

}
