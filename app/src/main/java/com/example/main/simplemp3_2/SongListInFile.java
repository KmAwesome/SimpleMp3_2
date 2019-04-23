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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SongListInFile {
    private final String TAG = "SongListInFile";
    private ArrayList<Song> songList;
    private Context context;
    private InitSongList initSongList;

    public SongListInFile(Context context) {
        songList = new ArrayList<>();
        this.context = context;
        initSongList = new InitSongList(context);
    }

    public ArrayList<String> readTitleListInFile() {
        ArrayList<String> stringTitles = new ArrayList<>();
        File file = context.getFileStreamPath("TitleList.bin");
        if (file.exists()){
            try {
                InputStream inputStream = context.openFileInput("TitleList.bin");
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                stringTitles = (ArrayList<String>) objectInputStream.readObject();
            } catch (Exception e) { e.printStackTrace(); }
        }
        return stringTitles;
    }

    public void writeTitleListToFile(String title) {
        ArrayList<String> songTitleList = readTitleListInFile();
        if (!songTitleList.contains(title) && title.length() > 0) {
            songTitleList.add(title);
            writeFile(songTitleList);
        }
    }

    public void writeTitleListToFile(ArrayList<String> titles) {
        writeFile(titles);
    }

    private void writeFile(ArrayList<String> songTitleList) {
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
        ArrayList<Song> allSongList = initSongList.getSongList();
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

    public void removeSongInPlayList(String playListTitle, String songTitle) {
        try {
            ArrayList<String> songs = readSongListInFile(playListTitle);
            for (int i=0; i<songs.size(); i++) {
                if (songs.get(i).contains(songTitle)) {
                    songs.remove(i);
                }
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(playListTitle + ".bin",Context.MODE_PRIVATE));
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
