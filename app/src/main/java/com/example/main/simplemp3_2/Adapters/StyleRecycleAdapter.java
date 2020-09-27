package com.example.main.simplemp3_2.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Dialog.SelectPlayListDialog;
import com.example.main.simplemp3_2.Models.Song;

import java.util.ArrayList;

public class StyleRecycleAdapter extends RecyclerView.Adapter<StyleRecycleAdapter.ViewHolder> {
    private final static String TAG = "StyleRecycleAdapter";
    private Context context;
    private ArrayList<String> styleStringList;
    private OnItemClickListener onItemClickListener;
    private ArrayList<Song> songList;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public StyleRecycleAdapter(Context context, ArrayList<String> styleStringList) {
        this.context = context;
        this.styleStringList = styleStringList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_style, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewSongStyle.setText(styleStringList.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSongStyle, textViewSongTotal;
        private ImageButton imageButtonSetting;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewSongStyle = itemView.findViewById(R.id.text_view_song_style);
            textViewSongTotal = itemView.findViewById(R.id.texv_view_song_total);
            imageButtonSetting = itemView.findViewById(R.id.image_button_setting);
            imageButtonSetting.setOnClickListener(imageButtonSettingListener);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    } else {
                        throw new RuntimeException(" must setOnItemClickListner");
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return styleStringList.size();
    }

    View.OnClickListener imageButtonSettingListener = new View.OnClickListener() {
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
                            selectPlayListDialog.show(((MainActivity)context).getSupportFragmentManager(),null);
                            break;
                        case R.id.rename:
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    };

    private ArrayList<String> getSongsFromStyleName(String styleName){
        ArrayList<String> mSongs = new ArrayList<>();
        for (int i=0; i<songList.size(); i++){
            if (songList.get(i).getStyle().contains(styleName)){
                mSongs.add(songList.get(i).getTitle());
            }
        }
        return mSongs;
    }
}
