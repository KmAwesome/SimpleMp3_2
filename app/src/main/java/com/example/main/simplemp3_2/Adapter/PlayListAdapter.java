package com.example.main.simplemp3_2.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.main.simplemp3_2.Fragment.PlayListFragment;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.Model.SelectPlayListFragmentDialog;
import com.example.main.simplemp3_2.Model.Song;
import com.example.main.simplemp3_2.R;

import java.util.ArrayList;

import static com.example.main.simplemp3_2.MainActivity.playList;

public class PlayListAdapter extends BaseAdapter implements View.OnClickListener {
    private static String TAG = "PlayListAdapter";
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Song> songlist;
    private TextView txvPlayList;
    private ImageButton imgbtnSetting;
    private PlayListFragment playListFragment;

    public PlayListAdapter(Context context, ArrayList<Song> songlist,PlayListFragment playListFragment) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.songlist = songlist;
        this.playListFragment = playListFragment;
    }

    @Override
    public int getCount() {
        return playList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_playlist, null);
            txvPlayList = view.findViewById(R.id.txv_playlist);
            imgbtnSetting = view.findViewById(R.id.imgbtn_playlist_setting);
            imgbtnSetting.setOnClickListener(this);
            imgbtnSetting.setTag(i);
        }

        if (playList != null){
            txvPlayList.setText(playList.get(i).getTitle());
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        try {
            showPopupMenu(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPopupMenu(View view){
        final int postion = (int)view.getTag();
        PopupMenu popupMenu = new PopupMenu(context,view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_playlist,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.playAll:
                        playAllSongsInPlayList(playList.get(postion).getPlayListSong());
                        break;
                    case R.id.deletePlayList:
                        playList.remove(postion);
                        playListFragment.writePlayListToFile();
                        notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void playAllSongsInPlayList(ArrayList<String> songTitleList) {
        ArrayList<Song> playSongs;
        playSongs = new ArrayList<>();
        for (int i=0; i<songlist.size(); i++) {
            if (songTitleList.contains(songlist.get(i).getTitle())){
                playSongs.add(songlist.get(i));
            }
        }
        ((MainActivity)context).setSonglist(playSongs);
        ((MainActivity)context).setSongPos(0);
        ((MainActivity)context).playSong();
    }

}
