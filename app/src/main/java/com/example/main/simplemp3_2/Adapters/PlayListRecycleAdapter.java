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

import com.example.main.simplemp3_2.Utils.MusicController;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.FileUtils;

import java.util.ArrayList;

public class PlayListRecycleAdapter extends RecyclerView.Adapter<PlayListRecycleAdapter.ViewHolder> {
    private static String TAG = "PlayListRecycleAdapter";
    private Context context;
    private MusicController musicController;
    private ArrayList<String> playListStringList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PlayListRecycleAdapter(Context context, ArrayList<String> playListStringList) {
        this.context = context;
        this.playListStringList = playListStringList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView texvViewPlayList;
        private TextView texvViewSongTotal;
        private ImageButton imageButtonSetting;

        public ViewHolder(View itemView) {
            super(itemView);
            texvViewPlayList = itemView.findViewById(R.id.texv_view_play_list);
            texvViewSongTotal = itemView.findViewById(R.id.texv_view_song_total);
            imageButtonSetting = itemView.findViewById(R.id.image_button_setting);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(getAdapterPosition());
                }
            });

            imageButtonSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(view, getAdapterPosition());
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_play_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.texvViewPlayList.setText(playListStringList.get(position));
        ArrayList<String> songStringList = FileUtils.readSongsFromPlayListDataByTitle(context, playListStringList.get(position));
        holder.texvViewSongTotal.setText("曲目 " + songStringList.size());
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (playListStringList != null) {
            return playListStringList.size();
        } else {
            return 0;
        }
    }

    private void showPopupMenu(View view, final int postion){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_playlist,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.playAll:

                        break;
                    case R.id.deletePlayList:
                        FileUtils.deletePlayListData(context, playListStringList.get(postion));
                        playListStringList.remove(postion);
                        refreshAdapterView();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    public void onItemClick(int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
    }

    public void refreshAdapterView() {
        playListStringList = FileUtils.readPlayListData(context);
        notifyDataSetChanged();
    }

}