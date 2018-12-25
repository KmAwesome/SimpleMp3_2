package com.example.main.simplemp3_2.Model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.example.main.simplemp3_2.Fragment.PlayListFragment;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import static com.example.main.simplemp3_2.MainActivity.playList;

public class SelectPlayListFragmentDialog extends DialogFragment{
    private final static String TAG = "SelectPlayListFragmentDialog";
    private ArrayList<String> songArrayList = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList<String> addListName;
        addListName = new ArrayList<>();
        for (int i=0; i<playList.size(); i++){
            addListName.add(playList.get(i).getTitle());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("請選擇播放清單");
        builder.setItems(addListName.toArray(new String[addListName.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                playList.get(i).addToList(songArrayList);
                writePlayListToFile();
            }
        });
        return builder.create();
    }

    public void setAddtoList(String songName) {
        songArrayList.add(songName);
    }

    public void setAddtoList(ArrayList<String> songNameList) {
        songArrayList.addAll(songNameList);
    }

    private void writePlayListToFile() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(getActivity().openFileOutput("PlayList.bin",Context.MODE_PRIVATE));
            objectOutputStream.writeObject(playList);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
