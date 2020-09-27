package com.example.main.simplemp3_2.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Dialog.SelectPlayListDialog;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Utils.MusicUtils;

import java.util.ArrayList;

public class FolderRecycleAdapter extends RecyclerView.Adapter<FolderRecycleAdapter.ViewHolder> {
    private static final String TAG = "AlbumRecycleAdapter";
    private Context context;
    private ArrayList<String> folderStringList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FolderRecycleAdapter(Context context, ArrayList<String> folderStringList) {
        this.context = context;
        this.folderStringList = folderStringList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewFolderName;
        private ImageButton imageButtonSetting;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewFolderName = itemView.findViewById(R.id.text_view_folder_name);
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
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_folder,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.playAll:
                                    Log.i(TAG, "onMenuItemClick: " + "playAll");
                                    break;
                                case R.id.addToList:
                                    SelectPlayListDialog selectPlayListDialog = new SelectPlayListDialog();
                                    selectPlayListDialog.addListToFile(folderStringList);
                                    selectPlayListDialog.show(((MainActivity)context).getSupportFragmentManager(),null);
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
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_folder, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String folderName = folderStringList.get(position) ;
        holder.textViewFolderName.setText(folderName);
        Log.i(TAG, "onBindViewHolder: " + folderName);
    }

    @Override
    public int getItemCount() {
        return folderStringList.size();
    }

    public void refreshAdapterView() {
        folderStringList = MusicUtils.getStringListByType(context, MusicUtils.TYPE_PATH);
        notifyDataSetChanged();
    }

}
