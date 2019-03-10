package com.example.main.simplemp3_2.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.main.simplemp3_2.InitSongList;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.MusicController;
import com.example.main.simplemp3_2.Song;
import com.example.main.simplemp3_2.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public static final String SENDSONG_TEXT = "sendSongText";;
    public final static String REPEAT = "Repeat";
    public final static String REPEATONE = "RepeatOne";
    public final static String SHUFFLE = "Shuffle";
    public final IBinder musicBind = new MusicBinder();
    private String TAG = "MusicService";
    private MediaPlayer player;
    private String songTitile = "", songArtist = "", repeatMode = "";
    private Random rand;
    private Intent intentSongText, updateUiIntent;
    private Song playItemSong;
    private ArrayList<Song> songlist;
    private InitSongList initSongList;
    public initNotification initNotification;
    private int songPosn;

    public class MusicBinder extends Binder {
        public MusicService getService() {
            Log.i(TAG, "MUSIC_BIND_SUCCESS");
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMusicPlayer();
        updateUiIntent = new Intent();
    }

    public void initMusicPlayer() {
        player = new MediaPlayer();
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        setSongPos(0);
        intentSongText = new Intent(SENDSONG_TEXT);
        songlist = new ArrayList<>();
        rand = new Random();
        initNotification = new initNotification(this, songlist);
        initSongList = new InitSongList(this);
        songlist = initSongList.getSongList();
        setRepeatMode(REPEAT);
        player.reset();
        try {
            playItemSong = songlist.get(songPosn);
            long currSong = playItemSong.getId();
            Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.reset();
        playNext();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        mediaPlayer.start();
        return false;
    }

    public void playSong() {
        try {
            player.reset();
            playItemSong = songlist.get(songPosn);
            long currSong = playItemSong.getId();
            Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
            player.setDataSource(getApplicationContext(), trackUri);
            player.prepareAsync();
            songTitile = playItemSong.getTitle();
            songArtist = playItemSong.getArtist();
            updateUiIntent.setAction(PLAY);
            sendBroadcast(updateUiIntent);
            intentSongText.putExtra("songTitle", songTitile);
            intentSongText.putExtra("songArtist", songArtist);
            sendBroadcast(intentSongText);
            initNotification.createNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void go() {
        player.start();
    }

    public void pausePlayer() {
        player.pause();
        initNotification.createNotification();
        updateUiIntent.setAction(PAUSE);
        sendBroadcast(updateUiIntent);
        sendBroadcast(intentSongText);
    }

    public void playPrev() {
        if (songPosn == 0) {
            songPosn = songlist.size() - 1;
        } else if (songPosn > 0) {
            songPosn--;
        }
        playSong();
    }

    public void playNext() {
        switch (repeatMode) {
            case REPEAT:
                songPosn++;
                if (songPosn >= songlist.size()) songPosn = 0;
                break;
            case REPEATONE:
                break;
            case SHUFFLE:
                int newSong = songPosn;
                while (newSong == songPosn) {
                    newSong = rand.nextInt(songlist.size());
                }
                songPosn = newSong;
                break;
        }
        playSong();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void setRepeatMode(String string) {
        switch (string) {
            case REPEAT:
                repeatMode = REPEAT;
                break;
            case REPEATONE:
                repeatMode = REPEATONE;
                break;
            case SHUFFLE:
                repeatMode = SHUFFLE;
                break;
        }
        Log.i(TAG, "setRepeatMode: " + repeatMode);
    }

    public void setSongList(ArrayList<Song> songs) {
        songlist = songs;
    }

    public void setSongPos(int songIndex) {
        songPosn = songIndex;
    }

    public int getSongPos() {
        return songPosn;
    }

    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
    }

    public class initNotification extends BroadcastReceiver {
        private String TAG = "mNotification";
        public RemoteViews mRemoteViews;
        public NotificationManager mNotificationManager;
        public Notification mBuilder, notification;
        private IntentFilter notiticationFilter;
        private ArrayList<Song> songlist;
        private Context context;
        private NotificationChannel notificationChannel;
        private PendingIntent pendingIntent;

        public initNotification(Context context, ArrayList<Song> songlist) {
            this.context = context.getApplicationContext();
            this.songlist = songlist;
            notiticationFilter = new IntentFilter();
            notiticationFilter.addAction("closeNotification");
            notiticationFilter.addAction("playNotification");
            notiticationFilter.addAction("nextNotification");
            notiticationFilter.addAction("prevNotification");
            notiticationFilter.addAction("pauseNotification");

            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_mp3_control);
            mRemoteViews.setImageViewResource(R.id.viewLogo, R.drawable.logo);
            mRemoteViews.setImageViewResource(R.id.notePlay, R.drawable.notepause);
            mNotificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

            Intent intent = new Intent(context, MainActivity.class);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent closeIntent = new Intent("closeNotification");
            PendingIntent pendingClose = PendingIntent.getBroadcast(context, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.noteClose, pendingClose);

            Intent prevIntent = new Intent("prevNotification");
            PendingIntent pendingPrev = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.notePrev, pendingPrev);

            Intent nextIntent = new Intent("nextNotification");
            PendingIntent pendingNext = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.noteNext, pendingNext);

            Intent mpauseIntent = new Intent("pauseNotification");
            PendingIntent pendingPause = PendingIntent.getBroadcast(context, 0, mpauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.notePlay, pendingPause);

            Intent mplayIntent = new Intent("playNotification");
            PendingIntent pendingPlay = PendingIntent.getBroadcast(context, 0, mplayIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.notePlay, pendingPlay);

            context.registerReceiver(this, notiticationFilter);
        }

        public void createNotification() {
            if (mBuilder == null) {
                Log.i(TAG, "createNotification: ");
                String CHANNEL_ID = "Channel01";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (notificationChannel == null) {
                        notificationChannel = new NotificationChannel(CHANNEL_ID, "Channel_1", NotificationManager.IMPORTANCE_LOW);
                        notificationChannel.setSound(null, null);
                        notificationChannel.enableVibration(false);
                        mNotificationManager.createNotificationChannel(notificationChannel);
                    }
                    mBuilder = new Notification.Builder(context, CHANNEL_ID)
                            .setChannelId(CHANNEL_ID)
                            .setSmallIcon(R.drawable.mp3)
                            .setCustomContentView(mRemoteViews)
                            .setContentIntent(pendingIntent)
                            .build();
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    mBuilder = new Notification.Builder(context)
                            .setSmallIcon(R.drawable.mp3)
                            .setContent(mRemoteViews)
                            .setContentIntent(pendingIntent)
                            .setPriority(Notification.PRIORITY_MAX)
                            .build();
                }
                mBuilder.flags = Notification.FLAG_NO_CLEAR;
                startForeground(1, mBuilder);
            }
            setmRemoteViews();
        }

        public void setNotificationSonglist(ArrayList<Song> songlist) {
            this.songlist = songlist;
        }

        public void setmRemoteViews() {
            if (mBuilder != null) {
                mNotificationManager.notify(1, mBuilder);
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("closeNotification")) {
                Log.i(TAG, "closeNotification");
                pausePlayer();
                stopForeground(true);
                mBuilder = null;
                return;
            } else if (intent.getAction().equals("playNotification")) {
                Log.i(TAG, "playNotification");
                if (isPlaying())
                    pausePlayer();
                else go();
            } else if (intent.getAction().equals("nextNotification")) {
                Log.i(TAG, "nextNotification");
                playNext();
            } else if (intent.getAction().equals("prevNotification")) {
                Log.i(TAG, "prevNotification");
                playPrev();
            }
            setmRemoteViews();
        }
    }

}
