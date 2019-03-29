package com.example.main.simplemp3_2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.main.simplemp3_2.R;
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
        view = LayoutInflater.from(context).inflate(R.layout.item_album, null);
        TextView albumNameView = view.findViewById(R.id.txv_album_name);
        TextView songCountView = view.findViewById(R.id.txv_song_num);
        albumNameView.setText(albumNames.get(i));
        songCountView.setText("曲目 " + getSongCounter(albumNames.get(i)));
        return view;
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
