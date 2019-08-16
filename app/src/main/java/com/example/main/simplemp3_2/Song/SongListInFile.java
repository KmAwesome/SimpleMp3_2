package com.example.main.simplemp3_2.Song;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class SongListInFile {
    private final String TAG = "SongListInFile";
    private ArrayList<Song> songList;
    private Context context;
    private InitSongList initSongList;

    public SongListInFile(Context context) {
        this.context = context;
        songList = new ArrayList<>();
        initSongList = new InitSongList(context);
    }

    public void setTitleListFile(String title) {
        ArrayList<String> songTitleList = getTitleListFile();
        if (!songTitleList.contains(title) && title.length() > 0) {
            songTitleList.add(title);
            Log.i(TAG, "setTitleListFile: " + songTitleList);
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput("TitleList.bin",Context.MODE_PRIVATE));
                objectOutputStream.writeObject(songTitleList);
                objectOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setTitleListFile(ArrayList<String> titles) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput("TitleList.bin",Context.MODE_PRIVATE));
            objectOutputStream.writeObject(titles);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTitleListFile() {
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

    public void setSongListFile(String songTitle, ArrayList<String> songStringList) {

        ArrayList<String> songs = readSongListInFile(songTitle);

        for (int i=0; i<songs.size(); i++) {
            if (!songStringList.contains(songs.get(i))) {
                songStringList.add(songs.get(i));
            }
        }

        Log.i(TAG, "setSongListFile: " + songStringList);

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(songTitle + ".bin",Context.MODE_PRIVATE));
            objectOutputStream.writeObject(songStringList);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Song> getSongListFile(String songTitle) {

        if (songList != null) {
            songList.clear();
        }

        ArrayList<Song> songs = initSongList.getSongList();
        ArrayList<String> songTitles = readSongListInFile(songTitle);
        for (String s : songTitles) {
            for (int i=0; i<songs.size(); i++) {
                if (s.contains(songs.get(i).getTitle())) {
                    songList.add(songs.get(i));
                }
            }
        }
        return songList;
    }

    public void removeSongListFile(String songTitle) {
        try {
            File file = context.getFileStreamPath(songTitle + ".bin");
            if (file.exists()) {
                file.delete();
            }
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


}
