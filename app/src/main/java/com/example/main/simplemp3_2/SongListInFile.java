package com.example.main.simplemp3_2;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class SongListInFile {
    private final String TAG = "SongListInFile";
    private ArrayList<String> songTitleList;
    private ArrayList<Song> songList;
    private Context context;
    private InitSongList initSongList;

    public SongListInFile(Context context) {
        songTitleList = new ArrayList<>();
        songList = new ArrayList<>();
        this.context = context;
        initSongList = ((MainActivity)context).getInitSongList();
    }

    public ArrayList<String> readSongTitleInFile() {
        File file = context.getFileStreamPath("TitleList.bin");
        if (file.exists()){
            try {
                InputStream inputStream = context.openFileInput("TitleList.bin");
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                songTitleList = (ArrayList<String>) objectInputStream.readObject();
            } catch (Exception e) { e.printStackTrace(); }
        }
        return songTitleList;
    }

    public void writeTitleListToFile(String... listTitle) {
        for (String title : listTitle){
            songTitleList.add(title);
        }

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput("TitleList.bin",Context.MODE_PRIVATE));
            objectOutputStream.writeObject(songTitleList);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Song> getSongListInFile(String songTitle) {
        if (songList != null) {
            songList.clear();
        }
        ArrayList<Song> allSongList = initSongList.getAllSongList();
        ArrayList<String> songStringList = readSongListInFile(songTitle);
        for (int i=0; i<allSongList.size(); i++) {
            for (String s : songStringList) {
                if (allSongList.get(i).getTitle().contains(s)) {
                    songList.add(allSongList.get(i));
                }
            }
        }
        return songList;
    }

    public void writeSongListToFile(String songTitle, ArrayList<String> songStringList) {
        try {
            ArrayList<String> songs = readSongListInFile(songTitle);
            for (int i=0; i<songStringList.size(); i++) {
                if (!songs.contains(songStringList.get(i))) {
                    songs.add(songStringList.get(i));
                }
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(songTitle + ".bin",Context.MODE_PRIVATE));
            objectOutputStream.writeObject(songs);
            objectOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> readSongListInFile(String songTitle) {
        ArrayList<String> songStringList = new ArrayList<>();
        try {
            InputStream inputStream = context.openFileInput(songTitle + ".bin");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            songStringList = (ArrayList<String>) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songStringList;
    }

    public void removeSongListInFile(String songTitle) {
        try {
            File file = context.getFileStreamPath(songTitle + ".bin");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
