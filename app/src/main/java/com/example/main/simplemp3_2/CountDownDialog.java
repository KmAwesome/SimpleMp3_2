package com.example.main.simplemp3_2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class CountDownDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener {
    private final String TAG = "CountDownDialog";
    private static Handler timeHandler = new Handler();
    private static TextView timeView;
    private static int time;
    private static boolean timeIsCountDown = false;
    private SeekBar timeSetSeekbar;
    private AlertDialog.Builder builder;
    private View contentView;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_time_set, null);
        timeView = contentView.findViewById(R.id.time_view);
        timeSetSeekbar = contentView.findViewById(R.id.time_seekbar);
        timeSetSeekbar.setOnSeekBarChangeListener(this);
        if (timeIsCountDown) {
            timeView.setText("剩餘時間 " + time + " 秒");
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
                            time = timeSetSeekbar.getProgress() * 5;
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        timeView.setText(seekBar.getProgress() + " : 分鐘 ");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

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
            timeView.setText("剩餘時間 " + time + " 秒");
            timeHandler.postDelayed(this, 1000);
        }

    };



}
