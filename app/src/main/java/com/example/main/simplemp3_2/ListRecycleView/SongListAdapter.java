package com.example.main.simplemp3_2.ListRecycleView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Activity.MusicInfoActivity;
import com.example.main.simplemp3_2.Dialog.SelectPlayListFragmentDialog;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.Song.MusicControl;
import com.example.main.simplemp3_2.Song.MusicController;
import com.example.main.simplemp3_2.Song.Song;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyViewHolder> {
    private final String TAG = "SongListAdapter";
    private ArrayList<Song> songList;
    private MusicControl musicController;
    private InitSongList initSongList;
    private Context context;

    public SongListAdapter(Context context) {
        this.context = context;
        musicController = ((MainActivity)context).getMusicController();
        initSongList = ((MainActivity)context).getInitSongList();
        songList = initSongList.getSongList();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView songView, artistView, durationView;
        public ImageView logoView;
        public ImageButton btnSetting;

        public MyViewHolder(View view) {
            super(view);
            songView = view.findViewById(R.id.song_titile);
            artistView = view.findViewById(R.id.song_artist);
            durationView = view.findViewById(R.id.song_duration);
            logoView = view.findViewById(R.id.imgv_logo);
            btnSetting = view.findViewById(R.id.song_setting);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    musicController.setSongList(songList);
                    musicController.setSongIndex(getAdapterPosition());
                    musicController.playSong();
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public SongListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final SongListAdapter.MyViewHolder viewHolder, int i) {
        viewHolder.songView.setText(songList.get(i).getTitle());
        viewHolder.artistView.setText(songList.get(i).getArtist());
        String sDuration = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(songList.get(i).getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(songList.get(i).getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(songList.get(i).getDuration()))
        );
        viewHolder.durationView.setText(sDuration);
        viewHolder.btnSetting.setTag(i);
        viewHolder.btnSetting.setOnClickListener(onClickListener);

        /*
        if (songList.get(musicController.getSongIndex()).getId() == songList.get(i).getId()) {
            viewHolder.logoView.setImageResource(R.drawable.main_btn_play);
        }else {
            viewHolder.logoView.setImageResource(R.drawable.main_view_mp3_icon);
        }
        */

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int m_position = (Integer) view.getTag();
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
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
                            selectPlayListFragmentDialog.addSongToFile(songList.get(m_position).getTitle());
                            selectPlayListFragmentDialog.show(((MainActivity)context).getSupportFragmentManager(),null);
                            break;
                        case R.id.delete:
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setTitle("確定刪除此歌曲?");
                            alertDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteSong(songList.get(m_position).getPath(), m_position);
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
        Song songItem = songList.get(pos);
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
            songList.remove(i);
            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "='" + musicPath + "'", null);
            musicController.setSongList(songList);
        }
    }


}
