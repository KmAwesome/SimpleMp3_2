package com.example.main.simplemp3_2.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.main.simplemp3_2.Dialog.SelectPlayListDialog;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.MusicController;
import com.example.main.simplemp3_2.Utils.FileUtils;
import com.example.main.simplemp3_2.Utils.MusicUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SongAdapterWithBar extends RecyclerView.Adapter<SongAdapterWithBar.ViewHolder> {
    private final String TAG = "SongAdapterWithBar";
    private ArrayList<Song> songArrayList;
    private MusicController musicController;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private int position;

    public interface OnItemClickListener {
        void onItemClick(int postion);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SongAdapterWithBar(Context context) {
        this.context = context;
        musicController = MusicController.getInstance(context);
        songArrayList = MusicUtils.getDisplaySongList();
    }

    @Override
    public SongAdapterWithBar.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, viewGroup, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final SongAdapterWithBar.ViewHolder viewHolder, int i) {
        viewHolder.songView.setText(songArrayList.get(i).getTitle());
        viewHolder.artistView.setText(songArrayList.get(i).getArtist());
        String sDuration = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(songArrayList.get(i).getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(songArrayList.get(i).getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(songArrayList.get(i).getDuration()))
        );
        viewHolder.durationView.setText(sDuration);
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView songView, artistView, durationView;
        public ImageView logoView;
        public ImageButton btnSetting;

        public ViewHolder(View view) {
            super(view);
            songView = view.findViewById(R.id.song_titile);
            artistView = view.findViewById(R.id.song_artist);
            durationView = view.findViewById(R.id.texv_view_song_duration);
            logoView = view.findViewById(R.id.imgv_logo);
            btnSetting = view.findViewById(R.id.image_button_setting);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                    musicController.setSongList(songArrayList);
                    musicController.setSongIndex(getAdapterPosition());
                    musicController.playSong();
                    notifyDataSetChanged();
                }
            });

            btnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_song, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.play:
                                    musicController.setSongIndex(getAdapterPosition());
                                    musicController.playSong();
                                    break;
                                case R.id.musicInfo:
                                    FileUtils.showMusicFileInfo(context, songArrayList.get(getAdapterPosition()));
                                    break;
                                case R.id.addToList:
                                    SelectPlayListDialog selectPlayListDialog = new SelectPlayListDialog();
                                    selectPlayListDialog.addSongToFile(songArrayList.get(getAdapterPosition()).getTitle());
                                    selectPlayListDialog.show(((MainActivity)context).getSupportFragmentManager(),null);
                                    break;
                                case R.id.delete:
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                    alertDialog.setTitle("確定刪除此歌曲?");
                                    alertDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            FileUtils.deleteSongFile(context, songArrayList, getAdapterPosition(), musicController);
                                            refreshAdapterView();
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
            });
        }
    }

    public void refreshAdapterView() {
        ArrayList<Song> songs = MusicUtils.getSongList(context);
        ArrayList<Song> tempSongs = new ArrayList<>();
        for (Song song: songs) {
            for (int i=0; i<songArrayList.size(); i++) {
                if (songArrayList.get(i).getId() == song.getId()) {
                    tempSongs.add(song);
                }
            }
        }
        songArrayList = tempSongs;
        notifyDataSetChanged();
    }
}
