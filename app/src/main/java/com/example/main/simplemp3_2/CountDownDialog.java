package com.example.main.simplemp3_2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class CountDownDialog extends DialogFragment {
    private final String TAG = "CountDownDialog";
    private static Handler timeHandler = new Handler();
    private static TextView timeView;
    private static int time;
    private static boolean timeIsCountDown = false;
    private SeekBar timeSetSeekbar;
    private AlertDialog.Builder builder;
    private View contentView;
    private Context context;
    private static MenuItem menuItem;
    private String timeString;
    private MusicController musicController;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        musicController = new MusicController(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_time_set, null);
        timeView = contentView.findViewById(R.id.time_view);

        timeSetSeekbar = contentView.findViewById(R.id.time_seekbar);
        timeSetSeekbar.setOnSeekBarChangeListener(timeSeekBar);

        if (timeIsCountDown) {
            timeView.setText("剩餘時間   " + timeString);
            timeSetSeekbar.setEnabled(false);
            builder = new AlertDialog.Builder(context)
                    .setTitle("時間設定")
                    .setView(contentView)
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            timeHandler.removeCallbacksAndMessages(null);
                            timeSetSeekbar.setEnabled(true);
                            timeIsCountDown = false;
                            menuItem.setTitle(context.getResources().getString(R.string.drawer_timer));
                        }
                    });
        }else {
            createCountDownView();
        }
        return builder.create();
    }

    private void createCountDownView() {
        builder = new AlertDialog.Builder(context)
                .setTitle("時間設定")
                .setView(contentView)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (timeSetSeekbar.getProgress() != 0) {
                            time = timeSetSeekbar.getProgress() * 60;
                            timeHandler.post(timeCountDownRunnable);
                            timeIsCountDown = true;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
    }

    private SeekBar.OnSeekBarChangeListener timeSeekBar = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            timeView.setText(seekBar.getProgress() + " : 分鐘 ");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    Runnable timeCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            time--;
            if (time == 0) {
                timeHandler.removeCallbacks(this);
                timeSetSeekbar.setEnabled(true);
                timeIsCountDown = false;
                if ( ((MainActivity)context).musicController.isPlaying()) {
                    ((MainActivity)context).musicController.pauseSong();
                }
                return;
            }
            timeString = String.format("%2d : %02d : %02d", time/3600, time/60%60, time % 60);
            timeView.setText("剩餘時間    " + timeString);
            menuItem.setTitle(context.getResources().getString(R.string.drawer_timer) + "     " + timeString);
            timeHandler.postDelayed(this, 1000);
        }
    };

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
}
