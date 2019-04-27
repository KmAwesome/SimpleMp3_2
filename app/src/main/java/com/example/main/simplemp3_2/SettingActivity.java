package com.example.main.simplemp3_2;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.Dialog.SongFilterDialog;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "SettingActivity";
    private InitSongList initSongList;
    private LinearLayout musicFilterDescription;
    private TextView txvMusicFilterTime, txvVersion;

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
        txvMusicFilterTime = findViewById(R.id.txv_music_filter);
        txvMusicFilterTime.setText(initSongList.getFilterTime());
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
                SongFilterDialog songFilterDialog = new SongFilterDialog(SettingActivity.this);
                songFilterDialog.setUpdateView(txvMusicFilterTime);
                songFilterDialog.show();
                break;
        }
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
