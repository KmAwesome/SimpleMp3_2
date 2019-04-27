package com.example.main.simplemp3_2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.Song.MusicController;
import com.example.main.simplemp3_2.Song.Song;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.SongListInFile;

import java.util.ArrayList;

public class PlayListAdapter extends BaseAdapter {
    private static String TAG = "PlayListAdapter";
    private MusicController musicController;
    private SongListInFile songListInFile;
    private Context context;
    private ArrayList<String> songTitleList;
    private TextView txvPlayListTitle, txvPlayListNum;
    private ImageButton imgbtnSetting;

    public PlayListAdapter(Context context, ArrayList<String> songTitleList) {
        this.context = context;
        this.songTitleList = songTitleList;
        musicController =  ((MainActivity)context).getMusicController();
        songListInFile =  new SongListInFile(context);
    }

    @Override
    public int getCount() {
        return songTitleList.size();
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
        view = LayoutInflater.from(context).inflate(R.layout.item_playlist, null, false);
        txvPlayListTitle = view.findViewById(R.id.txv_playlist);
        txvPlayListTitle.setText(songTitleList.get(i));

        txvPlayListNum = view.findViewById(R.id.txv_playlist_num);
        txvPlayListNum.setText("曲目  " + songListInFile.getSongListInFile(songTitleList.get(i)).size());

        imgbtnSetting = view.findViewById(R.id.imgbtn_playlist_setting);
        imgbtnSetting.setTag(i);
        imgbtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
        return view;
    }

    private void showPopupMenu(View view){
        final int postion = (int)view.getTag();
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_playlist,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.playAll:
                        playAllSongsInPlayList(postion);
                        break;
                    case R.id.deletePlayList:
                        songListInFile.removeSongListInFile(songTitleList.get(postion));
                        songTitleList.remove(postion);
                        songListInFile.writeTitleListToFile(songTitleList);
                        notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void playAllSongsInPlayList(int postion) {
        ArrayList<Song> songlist = songListInFile.getSongListInFile(songTitleList.get(postion));
        if (songlist.size() > 0) {
            musicController.setSongList(songlist);
            musicController.setSongIndex(0);
            musicController.playSong();
        }
    }

}
