package com.example.main.simplemp3_2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

public class Mp3AppWidgetProvider extends AppWidgetProvider {
    private final String TAG = "Mp3AppWidgetProvider";
    private RemoteViews remoteViews;
    private Intent playIntent;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_mp3_controller);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        playIntent = new Intent("playSong");
        PendingIntent pendingPlay = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_play, pendingPlay);

        Intent prevIntent = new Intent("playPrev");
        PendingIntent pendingPrev = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_playPrev, pendingPrev);

        Intent nextIntent = new Intent("playNext");
        PendingIntent pendingNext = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_playNext, pendingNext);

        SharedPreferences sharedPreferences = context.getSharedPreferences("songInfo", Context.MODE_PRIVATE);
        Boolean isPlay = sharedPreferences.getBoolean("isPlay", false);

        if (isPlay) {
            remoteViews.setImageViewResource(R.id.btn_play, R.drawable.widget_btn_pause);
        }else {
            remoteViews.setImageViewResource(R.id.btn_play, R.drawable.widget_btn_play);
        }
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_mp3_controller);
        ComponentName thisWidget = new ComponentName(context, Mp3AppWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        SharedPreferences sharedPreferences = context.getSharedPreferences("songInfo", Context.MODE_PRIVATE);
        int numOfSongs = sharedPreferences.getInt("numOfSongs", 0);
        String songTitle = sharedPreferences.getString("songTitle", "songTitle");
        String songArtist = sharedPreferences.getString("songArtist", "songArtist");

        if (numOfSongs == 0) {
            remoteViews.setTextViewText(R.id.txv_songTitle, "點此新增歌曲");
            remoteViews.setTextViewText(R.id.txv_songArtist, "");
            remoteViews.setImageViewResource(R.id.btn_play, R.drawable.widget_btn_play);
            for (int appWidgetId : appWidgetIds) {
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
            return;
        }
        
        String action = intent.getAction();
        switch (action) {
            case "playSong" :
                Log.i(TAG, "playSong");
                remoteViews.setImageViewResource(R.id.btn_play, R.drawable.widget_btn_pause);
                break;
            case "pauseSong" :
                Log.i(TAG, "playSong");
                remoteViews.setImageViewResource(R.id.btn_play, R.drawable.widget_btn_play);
            case "playNext" :
                Log.i(TAG, "playNext");
                break;
            case "playPrev" :
                Log.i(TAG, "playPrev");
                break;
            case "playList" :
                Log.i(TAG, "playList");
                break;
        }

        remoteViews.setTextViewText(R.id.txv_songTitle, songTitle);
        remoteViews.setTextViewText(R.id.txv_songArtist, songArtist);

        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
