package com.example.main.simplemp3_2.Adapter;

import android.app.Activity;
import android.content.Context;
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

import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.Model.SelectPlayListFragmentDialog;
import com.example.main.simplemp3_2.Model.Song;
import com.example.main.simplemp3_2.MusicInfo;
import com.example.main.simplemp3_2.R;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by main on 2018/2/17.
 */

public class SongAdapter extends BaseAdapter {
    private static String TAG = "SongAdapter";
    private LayoutInflater songInf;
    private Context context;
    private ArrayList<Song> songlist;
    private Activity fragmentActivity;
    private int m_position;

    public SongAdapter(Context c, ArrayList<Song> songlist, Activity fragmentActivity) {
        context = c;
        songInf = LayoutInflater.from(c);
        this.songlist = songlist;
        this.fragmentActivity = fragmentActivity;
    }

    private class ViewHolder implements View.OnClickListener {
        public ImageButton songSetting;
        public TextView songView, artistView, durationView;

        public ViewHolder(TextView m_songView, TextView m_artistView,
                          TextView m_durationView, ImageButton m_songSetting) {
            songView = m_songView;
            artistView = m_artistView;
            durationView = m_durationView;
            songSetting = m_songSetting;
            songSetting.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            m_position = (Integer) view.getTag();
            Log.i(TAG, "SettingOnClick: " + m_position);
            PopupMenu popupMenu = new PopupMenu(context, songSetting);
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
                            selectPlayListFragmentDialog.show(((MainActivity)context).getFragmentManager(),null);
                            break;
                        case R.id.delete:
                            delete(songlist.get(m_position).getPath(), m_position);
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
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

    private void musicInfo(int pos) {
        Song mysong = songlist.get(pos);
        Intent intent = new Intent();
        intent.setClass(context, MusicInfo.class);
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

    private void delete(String musicPath, int i) {
        File file = new File(musicPath);
        if (file.exists()) {
            Log.i(TAG, "delete: " + musicPath);
            file.delete();
            songlist.remove(i);
            ((MainActivity) fragmentActivity).setSonglist(songlist);
            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.MediaColumns.DATA + "='" + musicPath + "'", null);
            this.notifyDataSetChanged();
        }
    }

}
