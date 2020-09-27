package com.example.main.simplemp3_2.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.MusicConstants;
import com.example.main.simplemp3_2.Utils.MusicController;

public class MusicNotificationReceiver extends BroadcastReceiver implements MusicConstants {
    private final static String TAG = "mNotification";
    public RemoteViews mRemoteViews;
    public NotificationManager mNotificationManager;
    public Notification mBuilder;
    private Context context;
    private NotificationChannel notificationChannel;
    private PendingIntent pendingIntent;
    private MusicController musicController;

    public MusicNotificationReceiver(Context context) {
        this.context = context;

        musicController = MusicController.getInstance(context);

        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_mp3_controller);
        mNotificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(NOTIFICATION_PLAY);
        PendingIntent pendingPlay = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notePlay, pendingPlay);

        Intent prevIntent = new Intent(NOTIFICATION_PLAY_PREV);
        PendingIntent pendingPrev = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notePrev, pendingPrev);

        Intent nextIntent = new Intent(NOTIFICATION_PLAY_NEXT);
        PendingIntent pendingNext = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.noteNext, pendingNext);

        Intent closeIntent = new Intent(NOTIFICATION_CLOSE_NOTIFICATION);
        PendingIntent pendingClose = PendingIntent.getBroadcast(context, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.noteClose, pendingClose);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NOTIFICATION_PLAY);
        intentFilter.addAction(NOTIFICATION_PLAY_PREV);
        intentFilter.addAction(NOTIFICATION_PLAY_NEXT);
        intentFilter.addAction(NOTIFICATION_CLOSE_NOTIFICATION);

        context.registerReceiver(this, intentFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(NOTIFICATION_CLOSE_NOTIFICATION)) {
            musicController.pauseSong();
            musicController.stopForeground();
            mBuilder = null;
            return;
        } else if (intent.getAction().equals(NOTIFICATION_PLAY)) {
            if (musicController.getSongPlayingPosition() == 0) {
                musicController.playSong();
            }else if (musicController.isPlaying()) {
                musicController.pauseSong();
                intent.setAction(ACTION_PAUSE);
            }else {
                musicController.continueSong();
            }
        }else if (intent.getAction().equals(NOTIFICATION_PLAY_NEXT)) {
            musicController.nextSong();
        } else if (intent.getAction().equals(NOTIFICATION_PLAY_PREV)) {
            musicController.prevSong();
        }
        updateNotificationUI(intent.getAction());
    }

    public void updateNotificationUI(String tag) {
        if (mBuilder == null) {
            createNotification();
        }
        if (tag.equals(ACTION_PLAY)) {
            mRemoteViews.setImageViewResource(R.id.notePlay, R.drawable.note_btn_pause);
        }else if (tag.equals(ACTION_PAUSE)) {
            mRemoteViews.setImageViewResource(R.id.notePlay, R.drawable.note_btn_play);
        }
        mRemoteViews.setTextViewText(R.id.noteTitle, musicController.getCurrentSong().getTitle());
        mRemoteViews.setTextViewText(R.id.noteArtist, musicController.getCurrentSong().getArtist());
        mNotificationManager.notify(1, mBuilder);
    }

    public void createNotification() {
        Log.i(TAG, "createNotification: ");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "Channel01";
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, "Channel_1", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setSound(null, null);
                notificationChannel.enableVibration(false);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            mBuilder = new Notification.Builder(context, CHANNEL_ID)
                    .setChannelId(CHANNEL_ID)
                    .setSmallIcon(R.drawable.main_view_mp3_icon)
                    .setCustomContentView(mRemoteViews)
                    .setContentIntent(pendingIntent)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .build();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mBuilder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.main_view_mp3_icon)
                    .setContent(mRemoteViews)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .build();
        }
        mBuilder.flags = Notification.FLAG_NO_CLEAR;
        musicController.startForeground(mBuilder);
    }
}
