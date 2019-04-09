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
import com.example.main.simplemp3_2.Song;
import com.example.main.simplemp3_2.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private String TAG = "MusicService";
    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public final static String REPEAT = "Repeat";
    public final static String REPEATONE = "RepeatOne";
    public final static String SHUFFLE = "Shuffle";
    public final IBinder musicBind = new MusicBinder();
    public MusicControlNotification musicControlNotification;
    private MediaPlayer player;
    private String repeatMode;
    private Random rand;
    private Intent updateUiIntent;
    public Song playItemSong;
    private ArrayList<Song> songlist;
    private InitSongList initSongList;
    private int songPosn;
    private AudioManager audioManager;

    public class MusicBinder extends Binder {
        public MusicService getService() {
            Log.i(TAG, "MUSIC_BIND_SUCCESS");
            updateUi();
            return MusicService.this;
        }
    }

    public void updateUi() {
        if (songlist.size() > 0) {
            updateUiIntent.setAction(PLAY);
            updateUiIntent.putExtra("songTitle", playItemSong.getTitle());
            updateUiIntent.putExtra("songArtist", playItemSong.getArtist());
            sendBroadcast(updateUiIntent);
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
        setRepeatMode(REPEAT);
        musicControlNotification = new MusicControlNotification(this);
        initSongList = new InitSongList(this);
        songlist = initSongList.getSongList();
        updateUiIntent = new Intent();
        rand = new Random();
        initMusicPlayer();
        getAudioFocus();
    }
    
    private void initMusicPlayer() {
        player = new MediaPlayer();
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        if (songlist.size() > 0 ) {
            try {
                playItemSong = songlist.get(songPosn);
                long currSong = playItemSong.getId();
                Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
                player.setDataSource(getApplicationContext(), trackUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getAudioFocus() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN :
                    Log.i(TAG, "AUDIOFOCUS_GAIN");
                    if (getPosn() != 0) {
                        go();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT :
                    Log.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                    if (getPosn() != 0) {
                        pausePlayer();
                    }
                    break;
            }
        }
    };

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
            if (songlist.size() > 0) {
                player.reset();
                playItemSong = songlist.get(songPosn);
                long currSong = playItemSong.getId();
                Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
                player.setDataSource(getApplicationContext(), trackUri);
                player.prepareAsync();
                updateUi();
                musicControlNotification.createNotification();
                musicControlNotification.updateNotificationUI("Play");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void go() {
        if (songlist.size() > 0) {
            player.start();
            musicControlNotification.createNotification();
            musicControlNotification.updateNotificationUI("Play");
        }
    }

    public void pausePlayer() {
        if (songlist.size() > 0) {
            player.pause();
            musicControlNotification.createNotification();
            musicControlNotification.updateNotificationUI("Pause");
            updateUiIntent.setAction(PAUSE);
            sendBroadcast(updateUiIntent);
        }
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
                if (songlist.size() >= 2) {
                    int newSong = songPosn;
                    while (newSong == songPosn) {
                        newSong = rand.nextInt(songlist.size());
                    }
                    songPosn = newSong;
                }
                break;
        }
        playSong();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public ArrayList<Song> getSonglist() {
        return songlist;
    }

    public void setPosn(int posn) {
        player.seekTo(posn);
    }

    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        if (player.getCurrentPosition() == 0) {
            return 0;
        }
        return player.getDuration();
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

    public String getRepeatMode() {
        return repeatMode;
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

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
    }

    public class MusicControlNotification extends BroadcastReceiver {
        private final String TAG = "mNotification";
        public RemoteViews mRemoteViews;
        public NotificationManager mNotificationManager;
        public Notification mBuilder;
        private IntentFilter notiticationFilter;
        private Context context;
        private NotificationChannel notificationChannel;
        private PendingIntent pendingIntent;

        public MusicControlNotification(Context context) {
            this.context = context.getApplicationContext();
            notiticationFilter = new IntentFilter();
            notiticationFilter.addAction("closeNotification");
            notiticationFilter.addAction("playNotification");
            notiticationFilter.addAction("nextNotification");
            notiticationFilter.addAction("prevNotification");
            notiticationFilter.addAction("pauseNotification");

            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_mp3_control);
            mRemoteViews.setImageViewResource(R.id.viewLogo, R.drawable.view_note_logo);
            mRemoteViews.setImageViewResource(R.id.notePlay, R.drawable.btn_note_play);
            mRemoteViews.setImageViewResource(R.id.noteNext, R.drawable.btn_note_next);
            mRemoteViews.setImageViewResource(R.id.notePrev, R.drawable.btn_note_prev);
            mRemoteViews.setImageViewResource(R.id.noteClose, R.drawable.btn_note_close);

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

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("closeNotification")) {
                pausePlayer();
                stopForeground(true);
                mBuilder = null;
                return;
            } else if (intent.getAction().equals("playNotification")) {
                if (isPlaying()) {
                    pausePlayer();
                }else {
                    go();
                }
            } else if (intent.getAction().equals("nextNotification")) {
                playNext();
            } else if (intent.getAction().equals("prevNotification")) {
                playPrev();
            }
        }

        public void createNotification() {
            if (mBuilder == null) {
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
                            .setSmallIcon(R.drawable.view_mp3_icon)
                            .setCustomContentView(mRemoteViews)
                            .setContentIntent(pendingIntent)
                            .build();
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    mBuilder = new Notification.Builder(context)
                            .setSmallIcon(R.drawable.view_mp3_icon)
                            .setContent(mRemoteViews)
                            .setContentIntent(pendingIntent)
                            .setPriority(Notification.PRIORITY_MAX)
                            .build();
                }
                mBuilder.flags = Notification.FLAG_NO_CLEAR;
                startForeground(1, mBuilder);
            }
        }

        public void updateNotificationUI(String tag) {
            if (tag.equals("Play")) {
                mRemoteViews.setImageViewResource(R.id.notePlay, R.drawable.btn_note_pause);
            }else if (tag.equals("Pause")) {
                mRemoteViews.setImageViewResource(R.id.notePlay, R.drawable.btn_note_play);
            }
            mRemoteViews.setTextViewText(R.id.noteTitle, playItemSong.getTitle());
            mRemoteViews.setTextViewText(R.id.noteArtist, playItemSong.getArtist());
            mNotificationManager.notify(1, mBuilder);
        }
    }

}
