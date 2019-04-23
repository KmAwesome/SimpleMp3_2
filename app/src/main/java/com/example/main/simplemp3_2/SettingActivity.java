package com.example.main.simplemp3_2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "SettingActivity";
    private InitSongList initSongList;
    private LinearLayout musicFilterDescription;
    private TextView txvMusicFilter, txvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting); Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("功能設置");
        initSongList = new InitSongList(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        musicFilterDescription = findViewById(R.id.music_filter_description);
        musicFilterDescription.setOnClickListener(this);
        txvMusicFilter = findViewById(R.id.txv_music_filter);
        txvMusicFilter.setText(initSongList.getFilterTime());
        txvVersion = findViewById(R.id.txv_version);
        try {
            txvVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.music_filter_description:
                dialogTimeFilter();
                break;
        }
    }

    public void dialogTimeFilter() {
        final String[] time = {"不過濾", "15秒", "30秒", "1分鐘", "1分30秒", "2分鐘"};
        final int [] filterTime = {0, 15, 30, 60, 90, 120};

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("略過小於時間")
                .setItems(time, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            txvMusicFilter.setText(time[i]);
                            initSongList.setFilterTime(filterTime[i]);
                            initSongList.saveData();
                            initSongList.initSongList();
                            Toast.makeText(getApplicationContext(), "新增" + initSongList.getSongList().size() + "首歌曲至音樂庫中", Toast.LENGTH_LONG).show();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
