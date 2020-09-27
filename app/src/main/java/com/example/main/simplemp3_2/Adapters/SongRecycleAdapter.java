package com.example.main.simplemp3_2.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
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
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.Utils.MusicController;
import com.example.main.simplemp3_2.Utils.FileUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SongRecycleAdapter extends RecyclerView.Adapter<SongRecycleAdapter.ViewHolder> {
    private final String TAG = "SongRecycleAdapter";
    private ArrayList<Song> songArrayList;
    private MusicController musicController;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int postion);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SongRecycleAdapter(Context context, ArrayList<Song> songArrayList) {
        this.context = context;
        musicController = MusicController.getInstance(context);
        this.songArrayList = songArrayList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewSongTitle, textViewArtistName, textViewDuration;
        public ImageView logoView;
        public ImageButton btnSetting;

        public ViewHolder(View view) {
            super(view);
            textViewSongTitle = view.findViewById(R.id.song_titile);
            textViewArtistName = view.findViewById(R.id.song_artist);
            textViewDuration = view.findViewById(R.id.texv_view_song_duration);
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
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
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
                                            FileUtils.deleteSongFile(context, songArrayList.get(getAdapterPosition()).getPath());
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

    @Override
    public SongRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, viewGroup, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final SongRecycleAdapter.ViewHolder viewHolder, int i) {
        viewHolder.textViewSongTitle.setText(songArrayList.get(i).getTitle());
        viewHolder.textViewArtistName.setText(songArrayList.get(i).getArtist());
        String sDuration = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(songArrayList.get(i).getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(songArrayList.get(i).getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(songArrayList.get(i).getDuration()))
        );
        viewHolder.textViewDuration.setText(sDuration);
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public void refreshAdapterView() {
        notifyDataSetChanged();
    }

}
