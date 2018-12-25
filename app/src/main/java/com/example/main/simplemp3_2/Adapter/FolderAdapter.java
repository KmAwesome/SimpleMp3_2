package com.example.main.simplemp3_2.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.Model.SelectPlayListFragmentDialog;
import com.example.main.simplemp3_2.Model.Song;
import com.example.main.simplemp3_2.R;
import java.util.ArrayList;

public class FolderAdapter extends BaseAdapter {
    private static String TAG = "FolderAdapter";
    private ArrayList<String> arrayListFileStr;
    private ArrayList<Song> songlist;
    private LayoutInflater inflate_folder;
    private Context context;

    public FolderAdapter(Context context,ArrayList<String> fileStr,ArrayList<Song> songlist){
        this.context = context;
        inflate_folder = LayoutInflater.from(context);
        this.arrayListFileStr = fileStr;
        this.songlist = songlist;
    }

    @Override
    public int getCount() { return arrayListFileStr.size(); }

    @Override
    public Object getItem(int i) {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class ViewHolder implements View.OnClickListener{
        public TextView txv_folderName;
        public ImageButton imgbtn_setting;

        @Override
        public void onClick(View view) {
            try {
                showPopupMenu(view);
            }catch (Exception e){e.printStackTrace();}
        }
    }

    @Override
    public View getView(int position, View converView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(converView == null){
            viewHolder = new ViewHolder();
            converView = inflate_folder.inflate(R.layout.item_folder,null);
            viewHolder.imgbtn_setting = converView.findViewById(R.id.imgbtn_setting);
            viewHolder.txv_folderName = converView.findViewById(R.id.txv_folderName);
            viewHolder.imgbtn_setting.setTag(position);
            viewHolder.imgbtn_setting.setOnClickListener(viewHolder);
            converView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) converView.getTag();
        }
        viewHolder.txv_folderName.setText(arrayListFileStr.get(position));

        return converView;
    }

    private void showPopupMenu(View view){
        final int postion = (int)view.getTag();
        final PopupMenu popupMenu = new PopupMenu(context,view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_artist,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.playAll:
                        playAll(arrayListFileStr.get(postion));
                        break;
                    case R.id.addToList:
                        SelectPlayListFragmentDialog selectPlayListFragmentDialog = new SelectPlayListFragmentDialog();
                        selectPlayListFragmentDialog.show(((MainActivity)context).getFragmentManager(),null);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void playAll(String folderName){
        ArrayList<Song> mSongs;
        mSongs = new ArrayList<>();
        if (songlist != null){
            for (int i=0; i<songlist.size(); i++){
                if (songlist.get(i).getPath().contains(folderName)){
                    mSongs.add(songlist.get(i));
                }
            }
        }
        ((MainActivity)context).setSonglist(mSongs);
        ((MainActivity)context).setSongPos(0);
        ((MainActivity)context).playSong();
    }


}
