package com.affixstudio.whatsapptool.serviceOur;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.affixstudio.whatsapptool.modelOur.NotificationRecever.WA_PACKAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.Call_block;
import com.affixstudio.whatsapptool.modelOur.DBNewVersion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class blockCalls {


    Context c;

    public blockCalls(Context c) {
        this.c = c;
    }

    StatusBarNotification sbn;
    int WAorWB=1;// means normal whatsapp
    int isGroup=0;
    public void onNotificationPosted(StatusBarNotification sbn)  {



        try {

            if (sbn.getNotification().extras.getBoolean("android.isGroupConversation"))
            {
                Log.i("blockCalls","is GroupConversation");
                isGroup=1;
            }else {
                Log.i("blockCalls","not GroupConversation");
            }
            if (Objects.isNull(sbn.getNotification().actions))
            {
                return;
            }
            String actionName=sbn.getNotification().actions[0].title.toString();

        if (!sbn.getPackageName().equals(WA_PACKAGE) &&
                !sbn.getPackageName().equals("com.whatsapp.w4b")) {
            return;
        }

         if (actionName.equals("Reply"))
        {

            return;
        }else if (!(actionName.equalsIgnoreCase("Ignore") || (actionName.equalsIgnoreCase("Decline"))))
        {

            Log.i("blockCalls","action title "+actionName);
            return;
        }
            Log.i("blockCalls","after return condition action title "+actionName);
        this.sbn=sbn;


        if (!sbn.getPackageName().equals(WA_PACKAGE))
        {
            WAorWB=2; // means  business
        }





        block(sbn);








        }catch (Exception e)
        {
            Log.i("blockCalls",e.getMessage());
        }
    }


    SharedPreferences sp;

    private void block(StatusBarNotification sbn) {

        sp=c.getSharedPreferences("affix",Context.MODE_PRIVATE);

        String voiceOnTAG=c.getString(R.string.WAvoicecallblockOnTAG);
        String videoOnTAG=c.getString(R.string.WAVideocallblockOnTAG);



        if (WAorWB==2) // when business
        {
            voiceOnTAG=c.getString(R.string.WBvoicecallblockOnTAG);
            videoOnTAG=c.getString(R.string.WBVideocallblockOnTAG);
        }

        String text=sbn.getNotification().extras.get("android.text").toString();

        if (text.contains("voice") && sp.getBoolean(voiceOnTAG,false))
        {
            checkSchedule();
        }else if (text.contains("video") && sp.getBoolean(videoOnTAG,false))
        {
            checkSchedule();
        }else if (sp.getBoolean(voiceOnTAG,false))
        {

                    checkSchedule();


        }



    }
    void checkSchedule()
    {
        Calendar now=Calendar.getInstance(Locale.getDefault());


        String sMilisTag=c.getString(R.string.callBlockStartMilisTAG),eMilisTag=c.getString(R.string.callBlockEndMilisTAG);

        String scheduleCallBlockOnTAG=c.getString(R.string.WAscheduleCallBlockOnTAG);

        if (WAorWB==2) {
            scheduleCallBlockOnTAG = c.getString(R.string.WBscheduleCallBlockOnTAG);


            sMilisTag = c.getString(R.string.WBcallBlockStartMilisTAG);
            eMilisTag = c.getString(R.string.WBcallBlockEndMilisTAG);


        }

        if (sp.getBoolean(scheduleCallBlockOnTAG,false))
        {
            if (sp.getLong(sMilisTag,59434000)>sp.getLong(eMilisTag,1834000))
            {
                Log.i("notificationService"," start time is grater");
                if (sp.getLong(sMilisTag,59434000)<=getMilis(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND))
                        || sp.getLong(eMilisTag,1834000)>getMilis(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND))) // by default 10.00 pm to 6.00 am
                {
                     callBlocksOf();
                }
            }else {
                if (sp.getLong(sMilisTag,59434000)<=getMilis(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND))
                        && sp.getLong(eMilisTag,1834000)>getMilis(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND))) // by default 10.00 pm to 6.00 am
                {
                    callBlocksOf();
                }
            }


        }

        else
        {
            callBlocksOf();

        }
    }

    private void callBlocksOf() {

        try {
            String tableName=c.getString(R.string.CallBlockListtable2);
            String query="CREATE TABLE IF NOT EXISTS CallBlockListtable2 (_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',Number text DEFAULT 'Not set',IsGroup text DEFAULT '0',WAorWB text DEFAULT '1') ";

              DBNewVersion db=new DBNewVersion(c,c.getString(R.string.CallBlockListtable2),query,1,tableName);


        String name=sbn.getNotification().extras.get("android.title").toString();

        String blockCallsTAG=c.getString(R.string.blockCallOfTAG);

        if (WAorWB==2)
        {
            blockCallsTAG=c.getString(R.string.WBblockCallOfTAG);
        }


        int blockCallsOf=sp.getInt(blockCallsTAG,0);

        if (blockCallsOf==0) //everyOne
        {
            cutTheCall();
        }
        else if (blockCallsOf==1 ) // except phone contact
        {

            if (!isHasContactPermission())
            {
                notifyUser("","","Allow contact permission to block calls");
                return;
            }
            if (!hasInPhoneBook(name))
            {
                cutTheCall();
            }

        } else if (blockCallsOf>1) // my list and except my list
        {

            @SuppressLint("Recycle") Cursor cursor=db.getReadableDatabase().rawQuery("SELECT * FROM "+tableName+" WHERE WAorWB='"+WAorWB+"' AND ContactName='"+name+"'",null);

            if (cursor.moveToNext()) // data found in the list
            {
                if (blockCallsOf==2) // when block my list is selected
                {
                    cutTheCall();
                }


            }else if(blockCallsOf==3) // when block except my list is selected
            {
                cutTheCall();
            }
            cursor.close();

        }
        }catch (Exception e)
        {

            Log.e("blockCalls",e.getMessage());
        }
    }
    void cutTheCall()
    {
        String tableName=c.getString(R.string.call_block_history_table);
        String query=c.getString(R.string.tableCreationSQLPart1)+" "+tableName+" (_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',IsGroup text DEFAULT '0',IsVideoCall text DEFAULT '0',WAorWB text DEFAULT '1',Time text DEFAULT 'Not set') ";

        DBNewVersion db=new DBNewVersion(c,c.getString(R.string.CallBlockList),query,1,tableName);
      try {
          // todo fix not showing history



            if (sbn.getNotification().actions.length>0)
            {

                    sbn.getNotification().actions[0].actionIntent.send();


                    int isVideo=0;
                    String callType="Voice";
                    if (sbn.getNotification().extras.get("android.text").toString().toLowerCase().contains("video"))
                    {
                        isVideo=1;
                        callType="Video";
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    String name=sbn.getNotification().extras.get("android.title").toString();

                    // (_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',IsGroup text DEFAULT '0',IsVideoCall text DEFAULT '0',WAorWB text DEFAULT '1',Time text DEFAULT 'Not set')
                    ContentValues values=new ContentValues();

                    values.put("ContactName",name);
                    values.put("IsGroup",isGroup);
                    values.put("WAorWB",WAorWB);
                    values.put("Time",currentDateandTime);
                    values.put("IsVideoCall",isVideo);

                    db.getWritableDatabase().insert(c.getString(R.string.call_block_history_table),null,values);


                    notifyUser(callType,name,"");


            }

        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

    }

    private void notifyUser(String callType, String name,String meg) {

        String message=callType+" call from "+name+" has been blocked";

        String title="Call Blocked";

        int icon=R.drawable.call_blocked_focused;

        Intent intent=new Intent();
        if (meg.isEmpty())
        {
            intent=new Intent(c, Call_block.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }else {
            message=meg;
            title="Can't Block Calls";
            icon=R.drawable.alert_icon;

            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", c.getPackageName(), null);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }







        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent=PendingIntent.getActivity(c,6,
                intent,PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);// todo changed
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {


            NotificationChannel channel = new NotificationChannel(c.getString(R.string.app_name), "Call Blocked Notification",
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager= (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);


        }



        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // setting notification sound

        NotificationCompat.Builder Nbuilder=new NotificationCompat.Builder(c,
                c.getString(R.string.app_name));


        Nbuilder.setContentIntent(pendingIntent);
        Nbuilder.setAutoCancel(true);
        Nbuilder.setSmallIcon(icon);
        Nbuilder.setContentTitle(title);
        Nbuilder.setContentText(message);
        Nbuilder.setSound(sound);
        Nbuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        Nbuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


        NotificationManager manager= (NotificationManager) c
                .getSystemService(NOTIFICATION_SERVICE);


        manager.notify(1,Nbuilder.build());
    }

    @SuppressLint("Range")
    boolean hasInPhoneBook(String title)
    {

        ContentResolver cr = c.getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);


        if ((cur != null ? cur.getCount() : 0) > 0) {

            while (!Objects.isNull(cur) && cur.moveToNext())
            {

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

               if (name.equals(title))
               {
                   cur.close();
                   return  true;
               }
            }
        }
        if(cur!=null){
            cur.close();
        }

        return  false;

    }

    private long getMilis(int hourOfDay, int minute, int second) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sTimef = new SimpleDateFormat("HH:mm:ss");
        Date timeD = null;
        long timeMillis=0;
        try {
            timeD = sTimef.parse(hourOfDay+":"+minute+":"+second);
            timeMillis = Objects.requireNonNull(timeD).getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeMillis;
    }
    boolean isHasContactPermission()
    {
        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(c, permissions) != PackageManager.PERMISSION_GRANTED) {

                return false;
            }
        }
        return true;
    }
    final String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS
    };
}
