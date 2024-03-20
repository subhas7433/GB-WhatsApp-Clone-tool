package com.affixstudio.whatsapptool.modelOur;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class scheduleOffBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        SharedPreferences sp = context.getSharedPreferences("affix", MODE_PRIVATE);
        SharedPreferences.Editor spedit = sp.edit();


        spedit.putBoolean("timeToOn", false);// this will permit accessibility to work in whatsapp to avoid unnecessary click
        spedit.putBoolean("sendMedia", false);
        spedit.putBoolean("globalBack", false);
        spedit.putBoolean("sendTextStatus", false);
        spedit.putBoolean("sendTextStatus2", false);
        spedit.putBoolean("isUnknown", false);
        spedit.putBoolean("sendSimpleText", false);
        spedit.putInt("isFailed",0).apply();

        Log.i("broadCastOff","time to on is "+sp.getBoolean("timeToOn",false));
    }
}
