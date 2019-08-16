package com.example.main.simplemp3_2.ListPlaying;

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
import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Song.MusicControl;
import com.example.main.simplemp3_2.Song.MusicController;
import com.example.main.simplemp3_2.Dialog.SelectPlayListFragmentDialog;
import com.example.main.simplemp3_2.Song.Song;
import com.example.main.simplemp3_2.Activity.MusicInfoActivity;
import com.example.main.simplemp3_2.R;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.main.simplemp3_2.Service.MusicService.ACTION_PAUSE;

public class SongAdapter extends BaseAdapter {
    private static String TAG = "SongAdapter";
    private InitSongList initSongList;
    private MusicController musicController;
    private Context context;
    private ArrayList<Song> songlist;
    private int m_position;

    public SongAdapter(Context context, ArrayList<Song> songlist) {
        this.context = context;
        this.songlist = songlist;
        initSongList = ((MainActivity)context).getInitSongList();
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

    static class ViewHolder  {
        ImageButton btnSetting;
        TextView songView, artistView, durationView;
    }

    @Override
    public View getView(final int position, View converView, ViewGroup parent) {
        final ViewHolder holder;
        if (converView == null) {
            holder = new ViewHolder();
            converView = LayoutInflater.from(context).inflate(R.layout.item_song, null);
            holder.songView = converView.findViewById(R.id.song_titile);
            holder.artistView = converView.findViewById(R.id.song_artist);
            holder.durationView = converView.findViewById(R.id.song_duration);
            holder.btnSetting = converView.findViewById(R.id.song_setting);
            converView.setTag(holder);
        } else {
            holder = (ViewHolder) converView.getTag();
        }
        Song currItemSong = songlist.get(position);
        holder.songView.setText(currItemSong.getTitle());
        holder.artistView.setText(currItemSong.getArtist());
        String sDuration = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(currItemSong.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(currItemSong.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currItemSong.getDuration()))
        );
        holder.durationView.setText(sDuration);
        holder.btnSetting.setTag(position);
        holder.btnSetting.setOnClickListener(onClickListener);
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
                        case R.id.play:
                            musicController.setSongIndex(m_position);
                            musicController.playSong();
                            break;
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
        Song songItem = songlist.get(pos);
        Intent intent = new Intent();
        intent.setClass(context, MusicInfoActivity.class);
        intent.putExtra("Title", songItem.getTitle());
        intent.putExtra("Artist", songItem.getArtist());
        intent.putExtra("Album", songItem.getAlbum());
        intent.putExtra("Id", songItem.getId());
        intent.putExtra("Duration", songItem.getDuration());
        intent.putExtra("Path", songItem.getPath());
        intent.putExtra("Pos", pos);
        intent.putExtra("Style", songItem.getStyle());
        context.startActivity(intent);
    }

    private void deleteSong(String musicPath, int i) {
        File file = new File(musicPath);
        if (file.exists()) {
            file.delete();
            songlist.remove(i);
            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "='" + musicPath + "'", null);
            musicController.setSongList(songlist);
        }
    }

}
