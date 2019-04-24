package com.example.main.simplemp3_2.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.MusicController;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.SelectPlayListFragmentDialog;
import com.example.main.simplemp3_2.Song;
import java.util.ArrayList;

public class AlbumAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> albumNames;
    private ArrayList<Song> songList;

    public AlbumAdapter(Context context, ArrayList<String> albumNames, ArrayList<Song> songList) {
        this.context = context;
        this.albumNames = albumNames;
        this.songList = songList;
    }

    @Override
    public int getCount() {
        return albumNames.size();
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
        view = LayoutInflater.from(context).inflate(R.layout.item_album, null, false);
        TextView albumNameView = view.findViewById(R.id.txv_album_name);
        TextView songCountView = view.findViewById(R.id.txv_song_num);
        ImageButton songSetting = view.findViewById(R.id.imgbtn_setting);

        songSetting.setTag(i);
        songSetting.setOnClickListener(new View.OnClickListener() {
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
                                playAll(albumNames.get(postion));
                                break;
                            case R.id.addToList:
                                SelectPlayListFragmentDialog selectPlayListFragmentDialog = new SelectPlayListFragmentDialog();
                                selectPlayListFragmentDialog.addListToFile(getAlbumSongs(albumNames.get(postion)));
                                selectPlayListFragmentDialog.show(((MainActivity)context).getSupportFragmentManager(),null);
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        albumNameView.setText(albumNames.get(i));
        songCountView.setText("曲目 " + getSongCounter(albumNames.get(i)));
        return view;
    }

    private void playAll(String albumName){
        ArrayList<Song> mSongs = new ArrayList<>();
        for (int i=0; i<songList.size(); i++){
            if (songList.get(i).getAlbum().contains(albumName)){
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

    private ArrayList<String> getAlbumSongs(String albumName){
        ArrayList<String> mSongs = new ArrayList<>();
        for (int i=0; i<songList.size(); i++){
            if (songList.get(i).getAlbum().contains(albumName)){
                mSongs.add(songList.get(i).getTitle());
            }
        }
        return mSongs;
    }

    public String getSongCounter(String albumName) {
        int count = 0;
        if (songList.size() > 0){
            for(int i = 0; i< songList.size(); i++){
                if(songList.get(i).getAlbum().equals(albumName)){
                    count++;
                }
            }
        }
        return String.valueOf(count);
    }

}
