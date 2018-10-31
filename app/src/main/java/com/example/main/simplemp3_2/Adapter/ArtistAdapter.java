package com.example.main.simplemp3_2.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Model.Song;

import java.util.ArrayList;

public class ArtistAdapter extends BaseAdapter{
    private String TAG = "ArtistAdapter";
    private ArrayList<String> artistList;
    private LayoutInflater layoutInflater;
    private ArrayList<Song> songlist;
    private Context context;

    public ArtistAdapter(Context c, ArrayList<String> artistList,ArrayList<Song> songlist){
        this.context = c;
        layoutInflater = LayoutInflater.from(c);
        this.artistList = artistList;
        this.songlist = songlist;
    }

    class ViewHolder{
        public ImageButton songSetting;
        public TextView artistView;
        public TextView songCountView;

        public ViewHolder(ImageButton songSetting, TextView artistView, TextView songCountView) {
            this.songSetting = songSetting;
            this.artistView = artistView;
            this.songCountView = songCountView;
            
            songSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }
            });
        }
    }

    @Override
    public int getCount() {
        return artistList.size();
    }

    @Override
    public Object getItem(int i) {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View converView, ViewGroup viewGroup) {
        ViewHolder viewholder;
        if(converView == null){
            converView = layoutInflater.inflate(R.layout.item_file,null);
            TextView artistView = converView.findViewById(R.id.song_Artist);
            ImageButton songSetting = converView.findViewById(R.id.song_setting);
            TextView songCountView = converView.findViewById(R.id.song_num);
            viewholder = new ViewHolder(songSetting,artistView,songCountView);
            converView.setTag(viewholder);
        }else {
            viewholder = (ViewHolder) converView.getTag();
        }
        viewholder.artistView.setText(artistList.get(position));
        viewholder.songCountView.setText("曲目 " + getSongCounter(artistList.get(position)));
        viewholder.songSetting.getTag(position);
        
        return converView;
    }

    public String getSongCounter(String fileName){
        int count = 0;
        if (songlist.size() > 0){
            for(int i = 0; i< songlist.size(); i++){
                if(songlist.get(i).getArtist().equals(fileName)){
                    count++;
                }
            }
        }
        return String.valueOf(count);
    }

}
