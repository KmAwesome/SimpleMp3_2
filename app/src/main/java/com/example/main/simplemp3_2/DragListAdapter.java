package com.example.main.simplemp3_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.main.simplemp3_2.Dialog.SelectPlayListFragmentDialog;
import com.example.main.simplemp3_2.Service.MusicService;
import com.example.main.simplemp3_2.Song.InitSongList;
import com.example.main.simplemp3_2.Song.MusicControl;
import com.example.main.simplemp3_2.Song.Song;
import com.example.main.simplemp3_2.Song.SongListInFile;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.main.simplemp3_2.Service.MusicService.ACTION_PAUSE;

public class DragListAdapter extends BaseAdapter implements View.OnClickListener {
    private final String TAG = "DragListAdapter";
    private ArrayList<Song> songList;
    private String playListTitle;
    private Context context;
    private MusicControl musicController;
    private InitSongList initSongList;

    public DragListAdapter(Context context, ArrayList<Song> songList, String playListTitle) {
        this.context = context;
        this.songList = songList;
        this.playListTitle = playListTitle;
        initSongList = new InitSongList(context);
        if (context instanceof MainActivity)
            musicController = ((MainActivity)context).getMusicController();
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_drag_song, null);

        Song song = songList.get(position);

        TextView txvTitle = view.findViewById(R.id.song_titile);
        txvTitle.setText(song.getTitle());

        TextView txvArtsit = view.findViewById(R.id.song_artist);
        txvArtsit.setText(song.getArtist());

        TextView txvSongDuration = view.findViewById(R.id.song_duration);
        String sDuration = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(song.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(song.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(song.getDuration()))
        );
        txvSongDuration.setText(sDuration);

        ImageButton btnSetting = view.findViewById(R.id.song_setting);
        btnSetting.setTag(position);
        btnSetting.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        final int pos = (Integer) view.getTag();
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_song_in_playlist, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.removeFromList:
                        SongListInFile songListInFile = new SongListInFile(context);
                        songListInFile.removeSongInPlayList(playListTitle, songList.get(pos).getTitle());
                        songList.remove(pos);
                        notifyDataSetChanged();
                        ((MainActivity)context).refreshAllFragment();
                        break;
                    case R.id.musicInfo:
                        musicInfo(pos);
                        break;
                    case R.id.addToList:
                        SelectPlayListFragmentDialog selectPlayListFragmentDialog = new SelectPlayListFragmentDialog();
                        selectPlayListFragmentDialog.addSongToFile(songList.get(pos).getTitle());
                        selectPlayListFragmentDialog.show(((MainActivity)context).getSupportFragmentManager(),null);
                        break;
                    case R.id.delete:
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("確定刪除此歌曲?");
                        alertDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteSong(songList.get(pos).getPath(), pos);
                            }
                        });
                        alertDialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alertDialog.show();
                        notifyDataSetChanged();
                        ((MainActivity)context).refreshAllFragment();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void musicInfo(int pos) {
        Song mysong = songList.get(pos);
        Intent intent = new Intent();
        intent.setClass(context, MusicInfoActivity.class);
        intent.putExtra("Title", mysong.getTitle());
        intent.putExtra("Artist", mysong.getArtist());
        intent.putExtra("Album", mysong.getAlbum());
        intent.putExtra("Id", mysong.getId());
        intent.putExtra("Duration", mysong.getDuration());
        intent.putExtra("Path", mysong.getPath());
        intent.putExtra("Pos", pos);
        intent.putExtra("Style", mysong.getStyle());
        context.startActivity(intent);
    }

    private void deleteSong(String musicPath, int i) {
        File file = new File(musicPath);
        if (file.exists()) {
            file.delete();

            if (musicController.getSongIndex() == i) {
                musicController.pauseSong();
            }

            songList.remove(i);

            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.MediaColumns.DATA + "='" + musicPath + "'", null);

            songList = initSongList.getSongList();

            musicController.setSongList(songList);
            if (musicController.getSongList().size() == 0) {
                musicController.updateWidget(ACTION_PAUSE);
            }
        }
    }

    public void update(int start, int end) {
        Song song = songList.get(start);
        songList.remove(start);
        songList.add(end, song);
        notifyDataSetChanged();
    }

    public void updateSongOrderToFile() {
        ArrayList<String> songTitles = new ArrayList<>();
        for (int i=0; i<songList.size(); i++) {
            songTitles.add(songList.get(i).getTitle());
        }
        SongListInFile songListInFile = new SongListInFile(context);
        songListInFile.writeSongListToFile(playListTitle, songTitles);
    }

}

