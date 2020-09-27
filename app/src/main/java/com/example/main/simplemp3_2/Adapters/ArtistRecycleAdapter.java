package com.example.main.simplemp3_2.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Dialog.SelectPlayListDialog;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Models.Song;
import com.example.main.simplemp3_2.Utils.MusicUtils;

import java.util.ArrayList;

public class ArtistRecycleAdapter extends RecyclerView.Adapter<ArtistRecycleAdapter.ViewHolder> {
    private final String TAG = "ArtistAdapter";
    private Context context;
    private ArrayList<String> artistStringList;
    private ArrayList<Song> songArrayList;
    private onItemClickListener listener;
    private String artistTitle;

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public ArtistRecycleAdapter(Context context, ArrayList<String> artistStringList) {
        this.context = context;
        this.artistStringList = artistStringList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        artistTitle = artistStringList.get(position);
        songArrayList = MusicUtils.getSongListByTitle(context, artistTitle);
        holder.textViewArtistTitle.setText(artistTitle);
        holder.textViewSongTotal.setText("曲目 " + songArrayList.size());
    }

    @Override
    public int getItemCount() {
        return artistStringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewArtistTitle, textViewSongTotal;
        private ImageButton imageButtonSetting;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewArtistTitle = itemView.findViewById(R.id.texv_view_artist_title);
            textViewSongTotal = itemView.findViewById(R.id.texv_view_song_total);
            imageButtonSetting = itemView.findViewById(R.id.image_button_setting);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
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
                                    selectPlayListDialog.addListToFile(artistStringList);
                                    selectPlayListDialog.show(((MainActivity)context).getSupportFragmentManager(),null);
                                    break;
                                case R.id.rename:
                                    final String title = artistStringList.get(getAdapterPosition());
                                    final EditText editText = new EditText(context);
                                    editText.setText(title);
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                    alertDialog.setView(editText);
                                    alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int pos) {
                                            String editTextTitle = editText.getText().toString();
                                            songArrayList = MusicUtils.getSongListByTitle(context, title);
                                            MusicUtils.renameAllSongsInFolder(context, MusicUtils.TYPE_ARTIST, songArrayList, editTextTitle);
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
        artistStringList = MusicUtils.getStringListByType(context, MusicUtils.TYPE_ARTIST);
        notifyDataSetChanged();
    }
}
