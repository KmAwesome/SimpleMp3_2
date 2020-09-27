package com.example.main.simplemp3_2.Service;

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
import android.widget.Toast;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Utils.MusicConstants;
import com.example.main.simplemp3_2.Utils.MusicUtils;
import com.example.main.simplemp3_2.Widget.AppWidgetProviderController;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MusicService extends Service implements MusicConstants {
    private final String TAG = "MusicService";
    public final IBinder musicBind = new MusicBinder();
    private MusicNotificationReceiver musicNotificationReceiver;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MediaSessionCompat mediaSessionCompat;
    private MediaPlayer mediaPlayer;
    private Toast toast;
    private ArrayList<Song> songList;
    private ArrayList<Integer> shuffledList;
    private static boolean isPause;
    private String repeatMode = "MODE_REPEAT_ALL";
    private Song song;
    private int songIndex;
    private int shuffledIndex;

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

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
        }
    };

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.reset();
            if (repeatMode.equals(MODE_REPEAT) && songIndex == songList.size()-1) {
                try {
                    songIndex = 0;
                    mediaPlayer.reset();
                    song = songList.get(songIndex);
                    long currSong = song.getId();
                    Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
                    mediaPlayer.setDataSource(getApplicationContext(), trackUri);
                    updateWidget(ACTION_PAUSE);
                    musicNotificationReceiver.updateNotificationUI("Pause");
                    isPause = false;
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            nextSong();
        }
    };

    MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            mediaPlayer.reset();
            mediaPlayer.start();
            return false;
        }
    };

    MediaSessionCompat.Callback mediaSessionCallBack = new MediaSessionCompat.Callback() {
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
    };

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter headsetFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(noisyReceiver, headsetFilter);

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.setOnErrorListener(onErrorListener);

        ComponentName mbr = new ComponentName(getPackageName(), HeadSetControlReceiver.class.getName());
        mediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "mbr", mbr, null);
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setActive(true);
        mediaSessionCompat.setCallback(mediaSessionCallBack);

        toast = new Toast(this);
        songList = MusicUtils.getSongList(getApplicationContext());
        musicNotificationReceiver = new MusicNotificationReceiver(this);
        if (songList.size() > 0 ) {
            try {
                song = songList.get(songIndex);
                long currSong = song.getId();
                Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
                mediaPlayer.setDataSource(getApplicationContext(), trackUri);
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

    public void playSong() {
        try {
            if (songList.size() > 0) {
                mediaPlayer.reset();
                song = songList.get(songIndex);
                long currSong = song.getId();
                Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
                mediaPlayer.setDataSource(getApplicationContext(), trackUri);
                mediaPlayer.prepareAsync();
                isPause = false;
                updateWidget(ACTION_PLAY);
                musicNotificationReceiver.updateNotificationUI(ACTION_PLAY);
                sendBroadcast(new Intent().setAction(ACTION_PLAY));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseSong() {
        if (songList.size() > 0) {
            mediaPlayer.pause();
            updateWidget(ACTION_PAUSE);
            musicNotificationReceiver.updateNotificationUI(ACTION_PAUSE);
            sendBroadcast(new Intent().setAction(ACTION_PAUSE));
            isPause = true;
        }
    }

    public void continueSong() {
        if (songList.size() > 0) {
            mediaPlayer.start();
            updateWidget(ACTION_PLAY);
            musicNotificationReceiver.updateNotificationUI(ACTION_PLAY);
            sendBroadcast(new Intent().setAction(ACTION_PLAY));
            isPause = false;
        }
    }

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

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void setSongIndex(int songIndex) {
        this.songIndex = songIndex;
    }

    public int getSongIndex() {
        return songIndex;
    }

    public void setSongPlayingPosition(int posn) {
        mediaPlayer.seekTo(posn);
    }

    public int getSongPlayingPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void setSongList(ArrayList<Song> songs) {
        songList  = songs;
    }

    public Song getCurrentSong() {
        return songList.get(getSongIndex());
    }

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public int getSongDuration() {
        if (mediaPlayer.getCurrentPosition() == 0) {
            return 0;
        }
        return mediaPlayer.getDuration();
    }

    public void setRepeatMode(View view) {
        if (repeatMode.equals(MODE_REPEAT_ALL)) {
            repeatMode = MODE_REPEAT_ONE;
            toast.cancel();
            toast = Toast.makeText(this, "單曲循環", Toast.LENGTH_SHORT);
            toast.show();
        }else if (repeatMode.equals(MODE_REPEAT_ONE)) {
            setSongListShuffle();
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

    public void setSongListShuffle() {
        shuffledIndex = 0;
        shuffledList = new ArrayList<>();
        for (int i=0; i<songList.size(); i++) {
            shuffledList.add(i);
        }
        Collections.shuffle(shuffledList);
        Log.i(TAG, "setSongListShuffle: " + shuffledList);
    }

    public void releaseMediaPlayer() {
        mediaPlayer.reset();
    }

    @StringDef({ACTION_PLAY, ACTION_PAUSE, ACTION_START})
    public @interface Action{}

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

}
