package com.example.main.simplemp3_2.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Dialog.SelectPlayListDialog;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.MusicController;
import com.example.main.simplemp3_2.Utils.MusicUtils;

import java.util.ArrayList;

public class AlbumRecycleAdapter extends RecyclerView.Adapter<AlbumRecycleAdapter.ViewHolder> {
    private static final String TAG = "AlbumRecycleAdapter";
    private Context context;
    private ArrayList<String> albumTitleStringList;
    private ArrayList<Song> songArrayList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AlbumRecycleAdapter(Context context, ArrayList albumTitleStringList) {
        this.context = context;
        this.albumTitleStringList = albumTitleStringList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String albumTitle = albumTitleStringList.get(position) ;
        songArrayList = MusicUtils.getSongListByTitle(context, albumTitle);
        holder.textViewAlbumTitle.setText(albumTitle);
        holder.textViewSongTotal.setText("曲目 " + songArrayList.size());
    }

    @Override
    public int getItemCount() {
        return albumTitleStringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewAlbumTitle, textViewSongTotal;
        private ImageButton imageButtonSetting;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewAlbumTitle = itemView.findViewById(R.id.texv_view_album_title);
            textViewSongTotal = itemView.findViewById(R.id.text_view_song_total);
            imageButtonSetting = itemView.findViewById(R.id.image_button_setting);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            imageButtonSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_artist_album,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.playAll:
                                    break;
                                case R.id.addToList:
                                    SelectPlayListDialog selectPlayListDialog = new SelectPlayListDialog();
                                    selectPlayListDialog.addListToFile(albumTitleStringList);
                                    selectPlayListDialog.show(((MainActivity)context).getSupportFragmentManager(),null);
                                    break;
                                case R.id.rename:
                                    final String title = albumTitleStringList.get(getAdapterPosition());
                                    final EditText editText = new EditText(context);
                                    editText.setText(title);
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                    alertDialog.setView(editText);
                                    alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int pos) {
                                            String editTextTitle = editText.getText().toString();
                                            songArrayList = MusicUtils.getSongListByTitle(context, title);
                                            MusicUtils.renameAllSongsInFolder(context, MusicUtils.TYPE_ALBUM, songArrayList, editTextTitle);
                                            refreshAdapterView();
                                        }
                                    });
                                    alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    alertDialog.show();
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
        albumTitleStringList = MusicUtils.getStringListByType(context, MusicUtils.TYPE_ALBUM);
        notifyDataSetChanged();
    }

}
