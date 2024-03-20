package com.affixstudio.whatsapptool.modelOur;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.scheduleAction;
import com.affixstudio.whatsapptool.activityOur.schedule_sms;
import com.affixstudio.whatsapptool.getData.NotifyUser;

import java.util.ArrayList;

public class scheduleBrodcust extends WakefulBroadcastReceiver  {


    Context ctx;

    KeyguardManager.KeyguardLock keyguard;
    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock;
    @SuppressLint("WakelockTimeout")
    @Override
    public void onReceive(Context context, Intent intent) {

        ctx=context;


        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);


        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "affix:MyWakeLock");
        wakeLock.acquire();


        KeyguardManager km1 = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
         keyguard = km1.newKeyguardLock("MyApp");
        keyguard.disableKeyguard();


        if (!intent.getBooleanExtra("isTest",false))
        {
            if (isDatabaseEmpty()) //when deleted all the data but still have pending intent
            {

                return;
            }
        }




//        Intent i=new Intent(Intent.ACTION_VIEW);
//        String url="https://wa.me/+"+intent.getStringExtra("countryCode")+intent.getStringExtra("number")+"?text="+intent.getStringExtra("message")+"    ";
//        //i.setPackage("com.whatsapp");
//        i.setData(Uri.parse(url));
//        i.setPackage(intent.getStringExtra("package"));
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        Intent i=new Intent(new Intent(ctx, scheduleAction.class));



        //cat transfer multple file





        i.putExtra("package",intent.getStringExtra("package"));

        Log.i("packageBrod",intent.getStringExtra("package"));
        i.putExtra("id",intent.getIntExtra("id",0));
        i.putExtra("name",intent.getStringExtra("name"));
        i.putExtra("number",intent.getStringExtra("number"));
        i.putExtra("type",intent.getIntExtra("type",0));
        i.putExtra("isUnknown",intent.getBooleanExtra("isUnknown",false));
        i.putExtra("isTest",intent.getBooleanExtra("isTest",false));

        Log.i("scheduleBT","scheduleBT sendSimpleText "+intent.getBooleanExtra("sendSimpleText",false));
        if (intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM)!=null)
        {
            Log.i("scheduleBT","getIntent().getParcelableArrayListExtra!=null");
            ArrayList<Uri> uris =intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM));

        }else {

            i.putExtra("sendSimpleText", intent.getBooleanExtra("sendSimpleText",false));
        }

        Log.i("packageSetSB",intent.getStringExtra("package"));
        if (intent.getStringExtra("message")!=null)
        {
            i.putExtra("message",intent.getStringExtra("message"));
        }
        if (intent.getStringExtra(Intent.EXTRA_TEXT)!=null)
        {
            Log.i("scheduleBrodcust","intent.getStringExtra(Intent.EXTRA_TEXT)  "+intent.getStringExtra(Intent.EXTRA_TEXT) +" !");

            if (!intent.getStringExtra(Intent.EXTRA_TEXT).equals(""))
            {
                Log.i("scheduleBrodcust","Extra text not empty");
                i.putExtra("EXTRA_TEXT",intent.getStringExtra(Intent.EXTRA_TEXT));
            }

        }


        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);




        if (km.inKeyguardRestrictedInputMode())
        {

            Log.i("scheduleBrodcust"," inKeyguardRestrictedInputMode");



            SharedPreferences.Editor spedit=context.getSharedPreferences("affix",Context.MODE_PRIVATE).edit();


            spedit.putBoolean("timeToOn", false);// this will permit accessibility to work in whatsapp to avoid unnecessary click
            spedit.putBoolean("sendMedia", false);
            spedit.putBoolean("globalBack", false);
            spedit.putBoolean("sendTextStatus", false);
            spedit.putBoolean("sendTextStatus2", false);
            spedit.putBoolean("isUnknown", false);
            spedit.putBoolean("sendSimpleText", false);
            spedit.putInt("isFailed",0).apply();

            notifyWhenLocked(i);


        }else {
            context.startActivity(i);
        }

    }
    void notifyWhenLocked(Intent intent)
    {

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent=PendingIntent.getActivity(ctx,6,
                intent,PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);// todo changed
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {


            NotificationChannel channel = new NotificationChannel(ctx.getString(R.string.app_name), "Schedule",
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager= (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);


        }



        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // setting notification sound

        NotificationCompat.Builder Nbuilder=new NotificationCompat.Builder(ctx,
                ctx.getString(R.string.app_name));


        Nbuilder.setContentIntent(pendingIntent);
        Nbuilder.setAutoCancel(true);
        Nbuilder.setSmallIcon(R.drawable.schedule_icon);
        Nbuilder.setContentTitle(ctx.getString(R.string.notification_title_when_screen_locked));
        Nbuilder.setContentText(ctx.getString(R.string.notification_Msg_when_screen_locked));
        Nbuilder.setSound(sound);
        Nbuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        Nbuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


        NotificationManager manager= (NotificationManager) ctx
                .getSystemService(NOTIFICATION_SERVICE);


        manager.notify(1,Nbuilder.build());
    }



    private boolean isDatabaseEmpty() {

        boolean isDatabaseEmpty=true;
        String query="CREATE TABLE IF NOT EXISTS scheduleMessage(_id INTEGER PRIMARY KEY autoincrement, Name text DEFAULT 'Not set',Message text,Number text,Date text,OpDate text,OpTime text,isDraft INTEGER ,TextCountryCode text DEFAULT '',SendThrough text DEFAULT 'com.whatsapp',State text DEFAULT '',ImageURIs text DEFAULT ''" +
                ",VideoURIs text DEFAULT '',AudioURIs text DEFAULT '',DocURIs text DEFAULT '') ";

        database db=new database(ctx,ctx.getString(R.string.schiduleTableName),query,1);

        Cursor cursor= db.getinfo(1);

        while (cursor.moveToNext())
        {
            isDatabaseEmpty=false;
        }


        cursor.close();
        db.close();


        return isDatabaseEmpty;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        wakeLock.release();
        keyguard.reenableKeyguard();
    }
}
