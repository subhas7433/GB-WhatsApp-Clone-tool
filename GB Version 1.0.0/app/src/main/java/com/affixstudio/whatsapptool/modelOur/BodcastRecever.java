package com.affixstudio.whatsapptool.modelOur;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.affixstudio.whatsapptool.R;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class BodcastRecever extends BroadcastReceiver {
    Context context;
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;
    static MediaRecorder recorder= new MediaRecorder();
    static AudioManager audioManager;
    static File audiofile;
    private boolean record;

    public BodcastRecever(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        Toast.makeText(context, ""+stateStr, Toast.LENGTH_SHORT).show();

        int state;
//        if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//            state = TelephonyManager.CALL_STATE_IDLE;
//            Toast.makeText(context, "no call "+state, Toast.LENGTH_SHORT).show();}
//         else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//            state = TelephonyManager.CALL_STATE_OFFHOOK;
//            Toast.makeText(context, "ON call "+state, Toast.LENGTH_SHORT).show();
//        }



    }

    public  void startRecord(String name){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        int source=Integer.parseInt(SP.getString("RECORDER","2"));
        File sampleDir;
        String dir= getFolderPath(context);
        if(dir.isEmpty()){
            String Appname = context.getString(R.string.app_name);
            sampleDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/" + Appname);
        }else {
            sampleDir = new File(dir);
        }
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        String file_name = name;
        try {
            audiofile = File.createTempFile(file_name, ".3gpp", sampleDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (source){
            case 0:
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                break;
            case 1:
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                audioManager =(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(3,audioManager.getStreamMaxVolume(3),0);
                audioManager.setSpeakerphoneOn(true);
                break;
            case 2:
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                break;
            case 3:
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                break;
            case 4:
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                break;
            default:
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                break;
        }
        recorder.setAudioSamplingRate(8000);
        recorder.setAudioEncodingBitRate(12200);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        try {
            recorder.prepare();
            recorder.start();
            record = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getFolderPath(Context context) {
        SharedPreferences directorypreference = context.getSharedPreferences("DIRECTORY", Context.MODE_PRIVATE);
        String s = directorypreference.getString("DIR", Environment.getExternalStorageDirectory().getAbsolutePath() + "/CallRecorder");
        return s;
    }
}
