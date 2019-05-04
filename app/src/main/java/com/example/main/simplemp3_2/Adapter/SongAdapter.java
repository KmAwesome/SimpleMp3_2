package com.example.main.simplemp3_2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.Song.MusicController;
import com.example.main.simplemp3_2.Dialog.SelectPlayListFragmentDialog;
import com.example.main.simplemp3_2.Song.Song;
import com.example.main.simplemp3_2.MusicInfoActivity;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.SongListInFile;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.main.simplemp3_2.Service.MusicService.ACTION_PAUSE;

/**
 * Created by main on 2018/2/17.
 */

public class SongAdapter extends BaseAdapter {
    private static String TAG = "SongAdapter";
    private LayoutInflater songInf;
    private Context context;
    private ArrayList<Song> songlist;
    private int m_position;
    private InitSongList initSongList;
    private MusicController musicController;

    public SongAdapter(Context context, ArrayList<Song> songlist) {
        this.context = context;
        songInf = LayoutInflater.from(context);
        this.songlist = songlist;
        initSongList = new InitSongList(context);
        if (context instanceof MainActivity)
            musicController = ((MainActivity)context).getMusicController();
    }

    @Override
    public int getCount() {
        return songlist.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder  {
        public ImageButton songSetting;
        public TextView songView, artistView, durationView;

        public ViewHolder(TextView m_songView, TextView m_artistView,
                          TextView m_durationView, ImageButton m_songSetting) {
            songView = m_songView;
            artistView = m_artistView;
            durationView = m_durationView;
            songSetting = m_songSetting;
            songSetting.setOnClickListener(onClickListener);
        }
    }

    @Override
    public View getView(final int position, View converView, ViewGroup parent) {
        final ViewHolder holder;
        if (converView == null) {
            converView = songInf.inflate(R.layout.item_song, null);
            TextView songView = converView.findViewById(R.id.song_titile);
            TextView artistView = converView.findViewById(R.id.song_artist);
            TextView durationView = converView.findViewById(R.id.song_duration);
            ImageButton songSetting = converView.findViewById(R.id.song_setting);
            holder = new ViewHolder(songView, artistView, durationView, songSetting);
            converView.setTag(holder);
        } else {
            holder = (ViewHolder) converView.getTag();
        }
        holder.songSetting.setTag(position);
        Song currItemSong = songlist.get(position);
        holder.songView.setText(currItemSong.getTitle());
        holder.artistView.setText(currItemSong.getArtist());
        String sDuration = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(currItemSong.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(currItemSong.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currItemSong.getDuration()))
        );
        holder.durationView.setText(sDuration);
        return converView;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            m_position = (Integer) view.getTag();
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_song, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.musicInfo:
                            musicInfo(m_position);
                            break;
                        case R.id.addToList:
                            SelectPlayListFragmentDialog selectPlayListFragmentDialog = new SelectPlayListFragmentDialog();
                            selectPlayListFragmentDialog.addSongToFile(songlist.get(m_position).getTitle());
                            selectPlayListFragmentDialog.show(((MainActivity)context).getSupportFragmentManager(),null);
                            break;
                        case R.id.delete:
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setTitle("確定刪除此歌曲?");
                            alertDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteSong(songlist.get(m_position).getPath(), m_position);
                                    notifyDataSetChanged();
                                    ((MainActivity)context).refreshAllFragment();
                                }
                            });
                            alertDialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            alertDialog.show();
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    };

    private void musicInfo(int pos) {
        Song mysong = songlist.get(pos);
        Intent intent = new Intent();
        intent.setClass(context, MusicInfoActivity.class);
        intent.putExtra("Title", mysong.getTitle());
        intent.putExtra("Artist", mysong.getArtist());
        intent.putExtra("Album", mysong.getAlbum());
        intent.putExtra("Id", mysong.getId());
        intent.putExtra("Duration", mysong.getDuration());
        intent.putExtra("Path", mysong.getPath());
        intent.putExtra("Pos", pos);
        intent.putExtra("Style", mysong.getStyle());
        context.startActivity(intent);
    }

    private void deleteSong(String musicPath, int i) {
        File file = new File(musicPath);
        
        if (file.exists()) {
            file.delete();

            if (musicController.getSongIndex() == i) {
                if (musicController.isPlaying()) {
                    musicController.pauseSong();
                }
            }

            songlist.remove(i);

            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "='" + musicPath + "'", null);

            songlist = initSongList.getSongList();

            musicController.setSongList(songlist);
            if (musicController.getSongList().size() == 0) {
                musicController.updateWidget(ACTION_PAUSE);
            }
        }
    }

}
