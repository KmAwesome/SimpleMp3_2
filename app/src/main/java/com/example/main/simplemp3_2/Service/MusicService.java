package com.example.main.simplemp3_2.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.Widget.AppWidgetProviderController;
import com.example.main.simplemp3_2.Song.MusicControl;
import com.example.main.simplemp3_2.Song.Song;
import com.example.main.simplemp3_2.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class MusicService extends Service implements MusicControl {
    private final String TAG = "MusicService";

    public final IBinder musicBind = new MusicBinder();

    private final String MODE_REPEAT_ALL = "MODE_REPEAT_ALL";
    private final String MODE_REPEAT_ONE = "MODE_REPEAT_ONE";
    private final String MODE_SHUFFLE = "MODE_SHUFFLE";
    private final String MODE_REPEAT = "MODE_REPEAT";

    public final static String ACTION_PLAY = "ACTION_PLAY";
    public final static String ACTION_PAUSE = "ACTION_PAUSE";
    public final static String ACTION_START = "ACTION_START";

    private static String repeatMode = "MODE_REPEAT_ALL";

    private static boolean isPause;

    private MusicControlNotification musicControlNotification;

    private InitSongList initSongList;
    private MediaPlayer player;
    private Intent updateActivityUiIntent;
    private ArrayList<Song> songList;
    private ArrayList<Integer> shuffledList;
    private Song song;
    private int songIndex;
    private int shuffledIndex;
    private Toast toast;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private MediaSessionCompat mediaSessionCompat;

    @StringDef({ACTION_PLAY, ACTION_PAUSE, ACTION_START})
    public @interface Action{}

    public class MusicBinder extends Binder {
        public MusicService getService() {
            Log.i(TAG, "MUSIC_BIND_SUCCESS");
            updateActivityUi(ACTION_START);
            initHeadsetControllReceiver();
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

    public void updateActivityUi(@Action String action) {
        updateActivityUiIntent.setAction(action);
        if (songList.size() > 0) {
            updateActivityUiIntent.putExtra("songTitle", song.getTitle());
            updateActivityUiIntent.putExtra("songArtist", song.getArtist());
        }
        sendBroadcast(updateActivityUiIntent);
    }

    public void initHeadsetControllReceiver() {
        ComponentName mbr = new ComponentName(getPackageName(), HeadSetReceiver.class.getName());
        mediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "mbr", mbr, null);
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setActive(true);

        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                KeyEvent keyEvent  = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                if (keyEvent.getAction() == keyEvent.ACTION_UP) {
                    if (Intent.ACTION_MEDIA_BUTTON.equals(mediaButtonEvent.getAction())) {
                        if (getSongPlayingPosition() == 0) {
                            playSong();
                        }else if (isPlaying()) {
                            pauseSong();
                        }else {
                            continueSong();
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initSongList = new InitSongList(this);
        songList = initSongList.getSongList();
        setSongListShuffle();
        updateActivityUiIntent = new Intent();
        toast = new Toast(this);

        musicControlNotification = new MusicControlNotification(this);

        IntentFilter headsetFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(noisyReceiver, headsetFilter);

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        player = new MediaPlayer();
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                if (repeatMode.equals(MODE_REPEAT) && songIndex == songList.size()-1) {
                    try {
                        songIndex = 0;
                        player.reset();
                        song = songList.get(songIndex);
                        long currSong = song.getId();
                        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
                        player.setDataSource(getApplicationContext(), trackUri);
                        updateActivityUi(ACTION_PAUSE);
                        updateWidget(ACTION_PAUSE);
                        musicControlNotification.updateNotificationUI("Pause");
                        isPause = false;
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                nextSong();
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayer.reset();
                mediaPlayer.start();
                return false;
            }
        });

        if (songList.size() > 0 ) {
            try {
                song = songList.get(songIndex);
                long currSong = song.getId();
                Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
                player.setDataSource(getApplicationContext(), trackUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sharedPreferences = getSharedPreferences("songInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("numOfSongs", songList.size());
        editor.apply();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopSelf();
        mediaSessionCompat.release();
    }

    BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isPlaying()) {
                pauseSong();
            }
        }
    };

    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.i(TAG, "AUDIOFOCUS_GAIN" + isPause);
                    if (getSongPlayingPosition() != 0 && !isPause) {
                        continueSong();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Log.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT" + isPause);
                    if (getSongPlayingPosition() != 0 && !isPause) {
                        pauseSong();
                        isPause = false;
                    }
                    break;
            }
        }
    };

    @Override
    public void playSong() {
        try {
            if (songList.size() > 0) {
                player.reset();
                song = songList.get(songIndex);
                long currSong = song.getId();
                Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
                player.setDataSource(getApplicationContext(), trackUri);
                player.prepareAsync();
                updateActivityUi(ACTION_PLAY);
                updateWidget(ACTION_PLAY);
                musicControlNotification.updateNotificationUI("Play");
                isPause = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseSong() {
        if (songList.size() > 0) {
            player.pause();
            updateActivityUi(ACTION_PAUSE);
            updateWidget(ACTION_PAUSE);
            musicControlNotification.updateNotificationUI("Pause");
            isPause = true;
        }
    }

    @Override
    public void continueSong() {
        if (songList.size() > 0) {
            player.start();
            updateWidget(ACTION_PLAY);
            musicControlNotification.updateNotificationUI("Play");
            isPause = false;
        }
    }

    @Override
    public void prevSong() {

        if (songList.size() == 0) {
            return;
        }

        if (songIndex == 0) {
            songIndex = songList.size() - 1;
        } else if (songIndex > 0) {
            songIndex--;
        }
        playSong();
    }

    @Override
    public void nextSong() {

        if (songList.size() == 0) {
            return;
        }

        switch (repeatMode) {
            case MODE_REPEAT_ALL:
                songIndex++;
                if (songIndex >= songList.size()) songIndex = 0;
                break;
            case MODE_REPEAT_ONE:
                break;
            case MODE_SHUFFLE:
                if (shuffledIndex <=  shuffledList.size()-1) {
                    songIndex = shuffledList.get(shuffledIndex);
                    shuffledIndex++;
                }else {
                    shuffledIndex = 0;
                    songIndex = shuffledList.get(shuffledIndex);
                    shuffledIndex++;
                }
                break;
            case MODE_REPEAT:
                songIndex++;
                if (songIndex >= songList.size()) {
                    songIndex = 0;
                }
                break;
        }
        playSong();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void setSongIndex(int songIndex) {
        this.songIndex = songIndex;
    }

    @Override
    public int getSongIndex() {
        return songIndex;
    }

    @Override
    public void setSongPlayingPosition(int posn) {
        player.seekTo(posn);
    }

    @Override
    public int getSongPlayingPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public void setSongList(ArrayList<Song> songs) {
        songList  = songs;
    }

    @Override
    public ArrayList<Song> getSongList() {
        return songList;
    }

    @Override
    public int getSongDuration() {
        if (player.getCurrentPosition() == 0) {
            return 0;
        }
        return player.getDuration();
    }

    @Override
    public void setRepeatMode(View view) {
        if (repeatMode.equals(MODE_REPEAT_ALL)) {
            repeatMode = MODE_REPEAT_ONE;
            toast.cancel();
            toast = Toast.makeText(this, "單曲循環", Toast.LENGTH_SHORT);
            toast.show();
        }else if (repeatMode.equals(MODE_REPEAT_ONE)) {
            repeatMode = MODE_SHUFFLE;
            toast.cancel();
            toast = Toast.makeText(this, "隨機播放", Toast.LENGTH_SHORT);
            toast.show();
        }else if (repeatMode.equals(MODE_SHUFFLE)) {
            repeatMode = MODE_REPEAT;
            toast.cancel();
            toast = Toast.makeText(this, "關閉循環播放", Toast.LENGTH_SHORT);
            toast.show();
        }else if (repeatMode.equals(MODE_REPEAT)) {
            repeatMode = MODE_REPEAT_ALL;
            toast.cancel();
            toast = Toast.makeText(this, "循環播放", Toast.LENGTH_SHORT);
            toast.show();

        }
        if (view.getId() == R.id.btnRepeat || view.getId() == R.id.imgbtn_repeat) {
            updateRepeatImgButtonView(view);
        }
    }

    public void updateRepeatImgButtonView(View view) {
        ImageButton imageButton = ((ImageButton)view);
        if (repeatMode.equals(MODE_REPEAT_ALL)) {
            imageButton.setImageResource(R.drawable.main_btn_repeat_all);
        }else if (repeatMode.equals(MODE_REPEAT_ONE)) {
            imageButton.setImageResource(R.drawable.main_btn_repeat_one);
        }else if (repeatMode.equals(MODE_SHUFFLE)) {
            imageButton.setImageResource(R.drawable.main_btn_repeat_shffle);
        }else if (repeatMode.equals(MODE_REPEAT)) {
            imageButton.setImageResource(R.drawable.main_btn_repeat);
        }
    }

    @Override
    public void setSongListShuffle() {
        shuffledIndex = 0;
        shuffledList = new ArrayList<>();
        for (int i=0; i<songList.size(); i++) {
            shuffledList.add(i);
        }
        Collections.shuffle(shuffledList);
        Log.i(TAG, "setSongListShuffle: " + shuffledList);
    }

    public void updateWidget(@Action String action) {

        if (songList.size() > 0) {
            Song song = songList.get(getSongIndex());
            editor.putInt("numOfSongs", songList.size());
            editor.putString("songTitle", song.getTitle());
            editor.putString("songArtist", song.getArtist());
        }else {
            editor.putInt("numOfSongs", songList.size());
            editor.putString("songTitle", "點此新增歌曲");
            editor.putString("songArtist", "");
        }

        if (isPlaying()) {
            editor.putBoolean("isPlay", true);
        }else {
            editor.putBoolean("isPlay", false);
        }
        editor.apply();

        Intent intent = new Intent(this, AppWidgetProviderController.class);
        intent.setAction(action);
        sendBroadcast(intent);
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

            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_mp3_controller);
            mNotificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

            Intent intent = new Intent(context, MainActivity.class);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent playIntent = new Intent("playSong");
            PendingIntent pendingPlay = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.notePlay, pendingPlay);

            Intent prevIntent = new Intent("playPrev");
            PendingIntent pendingPrev = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.notePrev, pendingPrev);

            Intent nextIntent = new Intent("playNext");
            PendingIntent pendingNext = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.noteNext, pendingNext);

            Intent closeIntent = new Intent("closeNotification");
            PendingIntent pendingClose = PendingIntent.getBroadcast(context, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.noteClose, pendingClose);

            notiticationFilter = new IntentFilter();
            notiticationFilter.addAction("playSong");
            notiticationFilter.addAction("playPrev");
            notiticationFilter.addAction("playNext");
            notiticationFilter.addAction("closeNotification");

            context.registerReceiver(this, notiticationFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("closeNotification")) {
                pauseSong();
                stopForeground(true);
                mBuilder = null;
                return;
            } else if (intent.getAction().equals("playSong")) {
                if (getSongPlayingPosition() == 0) {
                    playSong();
                }else if (isPlaying()) {
                    pauseSong();
                }else {
                    continueSong();
                }
            }else if (intent.getAction().equals("playNext")) {
                nextSong();
            } else if (intent.getAction().equals("playPrev")) {
                prevSong();
            }
        }

        public void updateNotificationUI(String tag) {
            createNotification();
            if (tag.equals("Play")) {
                mRemoteViews.setImageViewResource(R.id.notePlay, R.drawable.note_btn_pause);
            }else if (tag.equals("Pause")) {
                mRemoteViews.setImageViewResource(R.id.notePlay, R.drawable.note_btn_play);
            }
            mRemoteViews.setTextViewText(R.id.noteTitle, song.getTitle());
            mRemoteViews.setTextViewText(R.id.noteArtist, song.getArtist());
            mNotificationManager.notify(1, mBuilder);
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
                startForeground(1, mBuilder);
            }
        }
    }

}
