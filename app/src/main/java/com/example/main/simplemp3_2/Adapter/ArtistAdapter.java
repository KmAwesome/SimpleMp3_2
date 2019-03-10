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

import com.example.main.simplemp3_2.InitSongList;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.MusicController;
import com.example.main.simplemp3_2.SelectPlayListFragmentDialog;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song;

import java.util.ArrayList;

public class ArtistAdapter extends BaseAdapter{
    private static String TAG = "ArtistAdapter";
    private ArrayList<String> artistList;
    private LayoutInflater layoutInflater;
    private ArrayList<Song> songlist;
    private Context context;
    private InitSongList initSongList;
    private MusicController musicController;

    public ArtistAdapter(Context c, ArrayList<String> artistList,ArrayList<Song> songlist){
        this.context = c;
        layoutInflater = LayoutInflater.from(c);
        this.artistList = artistList;
        this.songlist = songlist;
        initSongList = ((MainActivity)context).getInitSongList();
        musicController =  ((MainActivity)context).getMusicController();
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

    class ViewHolder implements View.OnClickListener{
        private ImageButton songSetting;
        private TextView artistView;
        private TextView songCountView;

        public ViewHolder(ImageButton m_songSetting, TextView m_artistView, TextView m_songCountView) {
            artistView = m_artistView;
            songCountView = m_songCountView;
            songSetting = m_songSetting;
            songSetting.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            showPopupMenu(view);
        }
    }

    @Override
    public View getView(int position, View converView, ViewGroup viewGroup) {
        ViewHolder viewholder;
        if(converView == null){
            converView = layoutInflater.inflate(R.layout.item_artist,null);
            TextView artistView = converView.findViewById(R.id.song_Artist);
            ImageButton songSetting = converView.findViewById(R.id.song_setting);
            TextView songCountView = converView.findViewById(R.id.song_num);
            viewholder = new ViewHolder(songSetting,artistView,songCountView);
            converView.setTag(viewholder);
        }else {
            viewholder = (ViewHolder) converView.getTag();
        }
        viewholder.songSetting.setTag(position);
        viewholder.artistView.setText(artistList.get(position));
        viewholder.songCountView.setText("曲目 " + getSongCounter(artistList.get(position)));
        
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

    private void showPopupMenu(View view){
        final int postion = (int)view.getTag();
        PopupMenu popupMenu = new PopupMenu(context,view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_artist,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.playAll:
                        playAll(artistList.get(postion));
                        break;
                    case R.id.addToList:
                        SelectPlayListFragmentDialog selectPlayListFragmentDialog = new SelectPlayListFragmentDialog();
                        selectPlayListFragmentDialog.addListToFile(getArtistSongs(artistList.get(postion)));
                        selectPlayListFragmentDialog.show(((MainActivity)context).getFragmentManager(),null);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void playAll(String artist){
        ArrayList<Song> mSongs = new ArrayList<>();
        for (int i=0; i<songlist.size(); i++){
            if (songlist.get(i).getArtist().contains(artist)){
                mSongs.add(songlist.get(i));
            }
        }
        initSongList.setSongList(mSongs);
        musicController.setSongPos(0);
        musicController.playSong();
    }

    private ArrayList<String> getArtistSongs(String artist){
        ArrayList<String> mSongs = new ArrayList<>();
        for (int i=0; i<songlist.size(); i++){
            if (songlist.get(i).getArtist().contains(artist)){
                mSongs.add(songlist.get(i).getTitle());
            }
        }
        return mSongs;
    }
}
