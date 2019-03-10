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
import com.example.main.simplemp3_2.Fragment.PlayListFragment;
import com.example.main.simplemp3_2.InitSongList;
import com.example.main.simplemp3_2.MainActivity;
import com.example.main.simplemp3_2.MusicController;
import com.example.main.simplemp3_2.Song;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.SongListInFile;

import java.util.ArrayList;

public class PlayListAdapter extends BaseAdapter {
    private static String TAG = "PlayListAdapter";
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Song> songlist;
    private ArrayList<String> songTitleList;
    private TextView txvPlayListTitle;
    private ImageButton imgbtnSetting;
    private PlayListFragment playListFragment;
    private InitSongList initSongList;
    private MusicController musicController;
    private SongListInFile songListInFile;

    public PlayListAdapter(Context context, ArrayList<String> songTitleList,PlayListFragment playListFragment) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.songTitleList = songTitleList;
        this.playListFragment = playListFragment;
        initSongList = ((MainActivity)context).getInitSongList();
        musicController =  ((MainActivity)context).getMusicController();
        songListInFile =  ((MainActivity)context).getSongListInFile();
        songlist  = initSongList.getSongList();
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
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_playlist, null);
            txvPlayListTitle = view.findViewById(R.id.txv_playlist);
            imgbtnSetting = view.findViewById(R.id.imgbtn_playlist_setting);
            imgbtnSetting.setOnClickListener(showPopUpMenu);
            imgbtnSetting.setTag(i);
        }

        txvPlayListTitle.setText(songTitleList.get(i));

        return view;
    }

    View.OnClickListener showPopUpMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                showPopupMenu(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void showPopupMenu(View view){
        final int postion = (int)view.getTag();
        PopupMenu popupMenu = new PopupMenu(context,view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_playlist,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.playAll:
                        //playAllSongsInPlayList(songListInFile.readSongListInFile(menuItem.get));
                        break;
                    case R.id.deletePlayList:
//                        playList.remove(postion);
//                        playListFragment.writePlayListToFile();
//                        notifyDataSetChanged();
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
        initSongList.setSongList(songlist);
        musicController.setSongPos(0);
        musicController.playSong();
    }

}
