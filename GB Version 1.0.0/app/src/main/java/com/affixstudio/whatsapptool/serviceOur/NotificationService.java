package com.affixstudio.whatsapptool.serviceOur;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.SpannableString;
import android.util.Log;

import androidx.core.app.RemoteInput;

import com.affixstudio.whatsapptool.NotificationWear;
import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.getData.NotifyUser;
import com.affixstudio.whatsapptool.model.CustomRepliesData;
import com.affixstudio.whatsapptool.model.preferences.PreferencesManager;
import com.affixstudio.whatsapptool.model.utils.ContactsHelper;
import com.affixstudio.whatsapptool.model.utils.DbUtils;
import com.affixstudio.whatsapptool.model.utils.NotificationHelper;
import com.affixstudio.whatsapptool.model.utils.NotificationUtils;
import com.affixstudio.whatsapptool.modelOur.NotificationRecever;
import com.affixstudio.whatsapptool.modelOur.database;

import static java.lang.Math.max;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class NotificationService extends NotificationListenerService {
    private final String TAG = NotificationService.class.getSimpleName();
    CustomRepliesData customRepliesData;
    private DbUtils dbUtils;
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
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        Calendar now=Calendar.getInstance(Locale.getDefault());





        blockCalls bc=new blockCalls(getApplicationContext());
        bc.onNotificationPosted(sbn);




        sp=getApplicationContext().getSharedPreferences("affix",MODE_PRIVATE);


        NotificationRecever recordInDb=new NotificationRecever(getApplicationContext());
        recordInDb.onNotificationPosted(sbn);



        if (canReply(sbn) && shouldReply(sbn))
        {

            String sTag=getString(R.string.autoReplyStartTAG),eTag=getString(R.string.autoReplyEndTAG);
            if (sp.getBoolean("scheduleAutoOn",false) )
            {
                Log.i("notificationService","system "+(sp.getLong(sTag,59434000)<=getMilis(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND)))+" autoStart = "+(sp.getLong(eTag,1834000)>getMilis(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND))));


                if (sp.getLong(sTag,59434000)>sp.getLong(eTag,1834000))
                {
                    Log.i("notificationService"," start time is grater");  //#todo set any sms reply for specific
                    if (sp.getLong(sTag,59434000)<=getMilis(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),
                            now.get(Calendar.SECOND))
                            || sp.getLong(eTag,1834000)>getMilis(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),
                            now.get(Calendar.SECOND))) // by default 10.00 pm to 6.00 am
                    {
                        sendReply(sbn);
                    }
                }else {
                    if (sp.getLong(sTag,59434000)<=getMilis(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),
                            now.get(Calendar.SECOND))
                            && sp.getLong(eTag,1834000)>getMilis(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),
                            now.get(Calendar.SECOND))) // by default 10.00 pm to 6.00 am
                    {
                        sendReply(sbn);
                    }
                }


            }
            else
            {
                sendReply(sbn);
            }

        }
    }





    private boolean canReply(StatusBarNotification sbn) {
        return isServiceEnabled() &&
                isSupportedPackage(sbn) &&
                NotificationUtils.isNewNotification(sbn) &&
                isGroupMessageAndReplyAllowed(sbn) &&
                canSendReplyNow(sbn);
    }

    private boolean shouldReply(StatusBarNotification sbn) {

        PreferencesManager prefs = PreferencesManager.getPreferencesInstance(this);
        boolean isGroup = sbn.getNotification().extras.getBoolean("android.isGroupConversation");

        //Check contact based replies
        if (prefs.isContactReplyEnabled() && !isGroup) {
            //Title contains sender name (at least on WhatsApp)
            String senderName = sbn.getNotification().extras.getString("android.title");
            //Check if should reply to contact
            boolean isNameSelected =
                    (ContactsHelper.getInstance(this).hasContactPermission()
                            && prefs.getReplyToNames().contains(senderName)) ||
                            prefs.getCustomReplyNames().contains(senderName);
            //If contact is on the list and contact reply is on blacklist mode,
            // or contact is not in the list and reply is on whitelist mode,
            // we don't want to reply
            return (!isNameSelected || !prefs.isContactReplyBlacklistMode()) &&
                    (isNameSelected || prefs.isContactReplyBlacklistMode());
        }

        //Check more conditions on future feature implementations

        //If we got here, all conditions to reply are met
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //START_STICKY  to order the system to restart your service as soon as possible when it was killed.
        return START_STICKY;
    }

    SharedPreferences sp;
    database db;
    ArrayList<String> contactList=new ArrayList<>();
    private void sendReply(StatusBarNotification sbn) {
        NotificationWear notificationWear = NotificationUtils.extractWearNotification(sbn);
        // Possibly transient or non-user notification from WhatsApp like
        // "Checking for new messages" or "WhatsApp web is Active"
        if (notificationWear.getRemoteInputs().isEmpty()) {
            return;
        }
        String sender=sbn.getNotification().extras.getString("android.title");

        String query="CREATE TABLE IF NOT EXISTS "+getString(R.string.contactList)+"(_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',Number text DEFAULT 'Not set') ";

        db=new database(getApplicationContext(),getString(R.string.contactList),query,1);
        int replyTo=sp.getInt("replyTo",0);



        if (replyTo==0)//reply to everyOne
        {
            reply(sbn);
        }
        else if (replyTo==1)//reply to contactList
        {
            getData();
            if (sbn.getNotification().extras.getBoolean("android.isGroupConversation"))
            {
                reply(sbn);
            }else {
                if (contactList.contains(sender))
                {
                    reply(sbn);
                }
            }


        }
        else if (replyTo==2) //reply to except ContactList
        {
            getData();
            if (!contactList.contains(sender))
            {
                reply(sbn);
            }

        } //reply to exceptPhoneContact


    }
    void reply(StatusBarNotification sbn){



        NotificationWear notificationWear = NotificationUtils.extractWearNotification(sbn);
        customRepliesData =new CustomRepliesData(this,sbn); //#todo pass the sbn from here for custom replay

        RemoteInput[] remoteInputs = new RemoteInput[notificationWear.getRemoteInputs().size()];

        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle localBundle = new Bundle();//notificationWear.bundle;
        int i = 0;

        String replyText="";
        for (RemoteInput remoteIn : notificationWear.getRemoteInputs()) {
            remoteInputs[i] = remoteIn;
            // This works. Might need additional parameter to make it for Hangouts? (notification_tag?)


            replyText=customRepliesData.getTextToSendOrElse();
            localBundle.putCharSequence(remoteInputs[i].getResultKey(), replyText);//#todo change this to change reply
            i++;
        }



        RemoteInput.addResultsToIntent(remoteInputs, localIntent, localBundle);

        try {

            if (notificationWear.getPendingIntent() != null) {
                if (dbUtils == null) {
                    dbUtils = new DbUtils(getApplicationContext());
                }
                dbUtils.logReply(sbn, NotificationUtils.getTitle(sbn));
                notificationWear.getPendingIntent().send(this, 0, localIntent);
                if (PreferencesManager.getPreferencesInstance(this).isShowNotificationEnabled()) {
                    NotificationHelper.getInstance(getApplicationContext()).sendNotification(sbn.getNotification().extras.getString("android.title"), sbn.getNotification().extras.getString("android.text"), sbn.getPackageName());
                }
                cancelNotification(sbn.getKey());
                if (canPurgeMessages()) {
                    dbUtils.purgeMessageLogs();
                    PreferencesManager.getPreferencesInstance(this).setPurgeMessageTime(System.currentTimeMillis());
                }



                saveInDatabase(replyText,sbn);

                NotifyUser nu=new NotifyUser(getApplicationContext());
                nu.UpdateUiOnNew(getApplicationContext().getString(R.string.autoReplyHistoryBroadCast));//sending broadcast to update ui

            }
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, "replyToLastNotification error: " + e.getLocalizedMessage());
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void saveInDatabase(String replyText, StatusBarNotification sbn) { // saving history in the database
        try{


            String query="CREATE TABLE IF NOT EXISTS "+getString(R.string.autoReplyHistory)+"(_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',ReceivedMessage text DEFAULT 'Not set',Replied text DEFAULT 'Not set',isGroup text DEFAULT 'false',Time text DEFAULT 'Not set') ";

            db=new database(getApplicationContext(),getString(R.string.autoReplyHistory),query,1);

            ContentValues values=new ContentValues();
            values.put("ContactName",sbn.getNotification().extras.get("android.title").toString());
            values.put("ReceivedMessage",sbn.getNotification().extras.get("android.text").toString());
            values.put("Replied",replyText);
            values.put("isGroup",sbn.getNotification().extras.getBoolean("android.isGroupConversation")); // returns 1 if true and 0 for false
            values.put("Time",new SimpleDateFormat("h:mm a d/M/yy").format(new Date()));

            db.getReadableDatabase().insert(getString(R.string.autoReplyHistory),null,values);

        }catch (Exception e)
        {
            Log.e("NotificationService",e.getMessage());
        }


    }

    void getData()
    {
        if (contactList.size()>0)
        {
            contactList.clear();
        }

        Cursor c=db.getinfo(1);
        while (c.moveToNext())
        {
            contactList.add(c.getString(1));
        }
    }


    private boolean canPurgeMessages() {
        //Added L to avoid numeric overflow expression
        //https://stackoverflow.com/questions/43801874/numeric-overflow-in-expression-manipulating-timestamps
        long daysBeforePurgeInMS = 30 * 24 * 60 * 60 * 1000L;
        return (System.currentTimeMillis() - PreferencesManager.getPreferencesInstance(this).getLastPurgedTime()) > daysBeforePurgeInMS;
    }

    private boolean isSupportedPackage(StatusBarNotification sbn) {
        return PreferencesManager.getPreferencesInstance(this)
                .getEnabledApps()
                .contains(sbn.getPackageName());
    }

    private boolean canSendReplyNow(StatusBarNotification sbn) {
        // Do not reply to consecutive notifications from same person/group that arrive in below time
        // This helps to prevent infinite loops when users on both end uses Watomatic or similar app
        int DELAY_BETWEEN_REPLY_IN_MILLISEC = 10 * 1000;

        String title = NotificationUtils.getTitle(sbn);
        String selfDisplayName = sbn.getNotification().extras.getString("android.selfDisplayName");
        if (title != null && title.equalsIgnoreCase(selfDisplayName)) { //to protect double reply in case where if notification is not dismissed and existing notification is updated with our reply
            return false;
        }
        if (dbUtils == null) {
            dbUtils = new DbUtils(getApplicationContext());
        }
        long timeDelay = PreferencesManager.getPreferencesInstance(this).getAutoReplyDelay();
        return (System.currentTimeMillis() - dbUtils.getLastRepliedTime(sbn.getPackageName(), title) >= max(timeDelay, DELAY_BETWEEN_REPLY_IN_MILLISEC));
    }

    private boolean isGroupMessageAndReplyAllowed(StatusBarNotification sbn) {
        String rawTitle = NotificationUtils.getTitleRaw(sbn);
        //android.text returning SpannableString
        SpannableString rawText = SpannableString.valueOf("" + sbn.getNotification().extras.get("android.text"));

        // Detect possible group image message by checking for colon and text starts with camera icon #181
        boolean isPossiblyAnImageGrpMsg = ((rawTitle != null) && (": ".contains(rawTitle) || "@ ".contains(rawTitle)))
                && ((rawText != null) && rawText.toString().contains("\uD83D\uDCF7"));


        if (!sbn.getNotification().extras.getBoolean("android.isGroupConversation")) {
            return !isPossiblyAnImageGrpMsg;
        } else {
            return PreferencesManager.getPreferencesInstance(this).isGroupReplyEnabled();
        }
    }

    private boolean isServiceEnabled() {
        return PreferencesManager.getPreferencesInstance(this).isServiceEnabled();
    }
}
