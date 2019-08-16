package com.example.main.simplemp3_2.ListArtist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.example.main.simplemp3_2.Activity.MainActivity;
import com.example.main.simplemp3_2.Song.MusicController;
import com.example.main.simplemp3_2.Dialog.SelectPlayListFragmentDialog;
import com.example.main.simplemp3_2.R;
import com.example.main.simplemp3_2.Song.Song;
import java.util.ArrayList;

public class ArtistAdapter extends BaseAdapter{
    private static String TAG = "ArtistAdapter";
    private ArrayList<String> artistList;
    private ArrayList<Song> songlist;
    private Context context;
    private MusicController musicController;

    public ArtistAdapter(Context context, ArrayList<String> artistList, ArrayList<Song> songlist) {
        this.context = context;
        this.artistList = artistList;
        this.songlist = songlist;
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

    class ViewHolder {
        private ImageButton btnSetting;
        private TextView artistView;
        private TextView songCountView;
    }

    @Override
    public View getView(int position, View converView, ViewGroup viewGroup) {
        ViewHolder holder;
        if(converView == null){
            holder = new ViewHolder();
            converView = LayoutInflater.from(context).inflate(R.layout.item_artist,null);
            holder.artistView = converView.findViewById(R.id.song_Artist);
            holder.btnSetting = converView.findViewById(R.id.song_setting);
            holder.songCountView = converView.findViewById(R.id.song_num);
            converView.setTag(holder);
        }else {
            holder = (ViewHolder) converView.getTag();
        }
        holder.artistView.setText(artistList.get(position));
        holder.songCountView.setText("曲目 " + getSongCounter(artistList.get(position)));
        holder.btnSetting.setTag(position);
        holder.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
        return converView;
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
                        selectPlayListFragmentDialog.show(((MainActivity)context).getSupportFragmentManager(),null);
                        break;
                    case R.id.rename:
                        final String artistName = artistList.get(postion);
                        final EditText editText = new EditText(context);
                        editText.setText(artistName);
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setView(editText);
                        alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                ContentResolver contentResolver = context.getContentResolver();
                                ContentValues values = new ContentValues();
                                for (int i=0; i<songlist.size(); i++) {
                                    if (songlist.get(i).getArtist().contains(artistName)){ ;
                                        try {
                                            values.put(MediaStore.Audio.Media.ARTIST, editText.getText().toString());
                                            contentResolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, MediaStore.Audio.Media.TITLE + "=?", new String[] {songlist.get(i).getTitle()});
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        ((MainActivity)context).refreshAllFragment();
                                    }
                                }
                            }
                        });

                        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alertDialog.show();
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
        if (mSongs.size() > 0) {
            musicController.setSongList(mSongs);
            musicController.setSongIndex(0);
            musicController.playSong();
        }
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
