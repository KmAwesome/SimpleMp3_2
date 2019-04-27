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
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Dialog.SelectPlayListFragmentDialog;
import com.example.main.simplemp3_2.Song.Song;
import java.util.ArrayList;

public class SongStyleAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> styleNameList;
    private ArrayList<Song> songList;

    public SongStyleAdapter(Context context, ArrayList<String> styleNameList, ArrayList<Song> songList) {
        this.context = context;
        this.styleNameList = styleNameList;
        this.songList = songList;
    }

    @Override
    public int getCount() {
        return styleNameList.size();
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
        view = LayoutInflater.from(context).inflate(R.layout.item_song_style, null);
        TextView songStyleName = view.findViewById(R.id.txv_style_name);
        TextView songNum = view.findViewById(R.id.txv_song_num);
        ImageButton songSetting = view.findViewById(R.id.imgbtn_setting);
        songSetting.setOnClickListener(onClickListener);
        songSetting.setTag(i);
        String songStyle = styleNameList.get(i);
        songStyleName.setText(songStyle);
        songNum.setText("曲目 " + getSongNum(songStyle));
        return view;
    }

    private String getSongNum(String styleName) {
        int num = 0;
        for (int i=0; i<songList.size(); i++) {
            if (songList.get(i).getStyle().equals(styleName)) {
                num++;
            }
        }
        return String.valueOf(num);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int postion = (int)view.getTag();
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_artist,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.playAll:
                            playAll(styleNameList.get(postion));
                            break;
                        case R.id.addToList:
                            SelectPlayListFragmentDialog selectPlayListFragmentDialog = new SelectPlayListFragmentDialog();
                            selectPlayListFragmentDialog.addListToFile(getSongsFromStyleName(styleNameList.get(postion)));
                            selectPlayListFragmentDialog.show(((MainActivity)context).getSupportFragmentManager(),null);
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    };

    private void playAll(String styleName){
        ArrayList<Song> mSongs = new ArrayList<>();
        for (int i=0; i<songList.size(); i++){
            if (songList.get(i).getStyle().contains(styleName)){
                mSongs.add(songList.get(i));
            }
        }
        if (mSongs.size() > 0) {
            MusicController musicController = ((MainActivity)context).getMusicController();
            musicController.setSongList(mSongs);
            musicController.setSongIndex(0);
            musicController.playSong();
        }
    }

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
