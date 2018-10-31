package com.example.main.simplemp3_2;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.main.simplemp3_2.MainActivity;

public class PhoneListener extends PhoneStateListener {
    private String TAG = "PhoneListener";
    private MainActivity mainActivity;

    public PhoneListener(){

    }

    public PhoneListener(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if (state == TelephonyManager.CALL_STATE_RINGING) {
            Log.i(TAG, "CALL_STATE_RINGING ");
        } else if(state == TelephonyManager.CALL_STATE_IDLE) {
            Log.i(TAG, "CALL_STATE_IDLE " + mainActivity);
            //mainActivity.goSong();
        } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
            Log.i(TAG, "CALL_STATE_OFFHOOK ");
            mainActivity.pauseSong();
        }
        super.onCallStateChanged(state, incomingNumber);

    }
}
