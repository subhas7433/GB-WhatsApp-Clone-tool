package com.affixstudio.whatsapptool.modelOur;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.induvidualChat;
import com.affixstudio.whatsapptool.getData.NotifyUser;
import com.affixstudio.whatsapptool.model.utils.NotificationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotificationRecever  {
    public static final String WA_PACKAGE = "com.whatsapp";
     Context ctx;
    StatusBarNotification sbn;


    public NotificationRecever(Context ctx) {
        this.ctx = ctx;
    }
    public  static String query="CREATE TABLE IF NOT EXISTS whatsappMessages (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER,unread INTEGER DEFAULT '1' ) ";

   
    public void onNotificationPosted(StatusBarNotification sbn) {


        if (!sbn.getPackageName().equals(WA_PACKAGE)) return;
        Notification notification=sbn.getNotification();

//        String s=sbn.getNotification().actions[0].title.toString();
//        i("sbn.getNotification().actions[0].title "+sbn.getNotification().actions[0].title.toString());
//        Bundle bundle = sbn.getNotification().extras;
//        if (bundle != null) {
//            for (String key : bundle.keySet()) {
//                Log.i("MainFragment", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
//
//            }
//        }






        try{


            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());



            String title=String.valueOf(sbn.getNotification().extras.get("android.title"));

            String message= sbn.getNotification().extras.get("android.text").toString();





            SharedPreferences sp=ctx.getSharedPreferences("affix",MODE_PRIVATE);




        database database=new database(ctx,ctx.getString(R.string.recover_message_table_name),query,2);
       // database.getinfo(1);
      //  Cursor c=    database.getinfo(1);
      //  database.checkData(c);


        i("before datafound fun ");
        int dataFound=0; // is the data already entered

        // avoiding the double entry
        if (sp.getBoolean(ctx.getString(R.string.privateChatOnTag),false) )
        {
            i("datafound fun 1");
            String query="CREATE TABLE IF NOT EXISTS "+ctx.getString(R.string.privateChatTableName)+" (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER ,unread INTEGER DEFAULT '1') ";

            database data=new database(ctx,ctx.getString(R.string.privateChatTableName),query,1);
                    // database.getinfo(1);

            Cursor cursor=data.getinfo(1);
                    data.checkData(cursor);


                        dataFound=isDuplicate(data, message, title, currentDateandTime);

                        data.close();

            i("datafound fun 2");
        }

            if (sp.getBoolean(ctx.getString(R.string.recoverMediaOnTag),false)) {

                i("datafound fun 3");
                dataFound=isDuplicate(database, message, title, currentDateandTime);
                i("datafound fun 4");
            }




            i("before entering in the insert fun");

           // Toast.makeText(ctx, "title " + title+"    message:"+message, Toast.LENGTH_LONG).show();
        if (dataFound==0)
        {


            if (title.contains("("))
            {
                Log.i("NotificationRecever","title.contains(\"(\")");
                title=title.replace(title.substring(title.indexOf("(")-1,title.indexOf(")")+1),"");
                Log.i("NotificationRecever",title+"");

            }

            if (!message.equals("Checking for new messages") && !title.contains("WhatsApp")

                    && !title.contains("You")
                    && !title.equals("Downloading document")
                    && sbn.getNotification().extras.get("last_row_id")!=null /*to avoid other unwanted whatsapp notification*/
                    && !message.contains("new messages") && !title.equals("Backup in progress")
                    && !message.contains(title+":")
            && !message.contains("Sending video to") && NotificationUtils.isNewNotification(sbn) )
            {

                Log.i("NotificationRecever"," entered to insert");


                // Toast.makeText(ctx, ""+currentTime, Toast.LENGTH_SHORT).show();
                database db = new database(ctx, ctx.getString(R.string.recover_message_table_name),query,2);
                db.checkData(db.getinfo(1));


                if (message.equals("message was deleted")) {


                   // Toast.makeText(ctx, ""+db.getId().size(), Toast.LENGTH_SHORT).show();
                    db.updateColumn("isdeleted = 1"," WHERE _id= "+db.getId().size());


                    database newdb = new database(ctx, ctx.getString(R.string.recover_message_table_name),query,2);
                    Cursor cursor = newdb.getinfo(1);  // when search data is needed passing the parameter 1
                    newdb.checkData(cursor);



                    ArrayList<String> FilteredMessage = new ArrayList<>();

                    for (int i = 0; newdb.getMessage().size() > i; i++)
                    {

                        if (newdb.getRecevedFrom().get(i).equals(title))
                        {

                            FilteredMessage.add(newdb.getMessage().get(i));
                        }

                    }


                    notifyUser(title , FilteredMessage.get(FilteredMessage.size() - 1));
                }
                else
                {

                    if (sp.getBoolean(ctx.getString(R.string.privateChatOnTag),false) )
                    {
                        recordForPrivateChat(title, message, currentDateandTime);
                    }


                    if (sp.getBoolean(ctx.getString(R.string.recoverMediaOnTag),false))
                    {
                        db.insertData(title, message, currentDateandTime, ctx.getString(R.string.recover_message_table_name)); // inserting in recover message
                    }

                    // refresh layout
                    NotifyUser notifiyUser=new NotifyUser(ctx);
                    notifiyUser.UpdateUiOnNew(ctx.getString(R.string.updateChatUpdateBroadcast));
                    Log.i("Inserted",title);


                }


            }

        }



        }catch (Exception e)
        {
            Log.e("NotificationReceiver",e.getMessage());

        }
    }

    private int isDuplicate(database database, String message, String title, String currentDateandTime) {


        for (int i=0;i<database.getMessage().size();i++)
        {

            if (database.getMessage().get(i).equals(message) &&
                    database.getRecevedFrom().get(i).equals(title)
                    && database.getTime().get(i).equals(currentDateandTime))
            {
                return 1;

            }


        }
        return 0;
    }

    private void recordForPrivateChat(String title, String message, String currentDateandTime)
    {
         String query="CREATE TABLE IF NOT EXISTS "+ctx.getString(R.string.privateChatTableName)+" (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER ,unread INTEGER DEFAULT '1') ";

        database database=new database(ctx,ctx.getString(R.string.privateChatTableName),query,1); // for message section
        database.insertData(title, message, currentDateandTime,ctx.getString(R.string.privateChatTableName));


        String query2="CREATE TABLE IF NOT EXISTS "+ctx.getString(R.string.privateMediaRecordTable)+" (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER ) ";

        if (isMedia(message))
        {
            Log.i("NotificationReceiver","contain "+message);
            database database2=new database(ctx,ctx.getString(R.string.privateMediaRecordTable),query2,1); // for media history
            database2.insertData(title, message, currentDateandTime,ctx.getString(R.string.privateMediaRecordTable));
        }



    }


    private boolean isMedia(String m) {

        return  m.contains("🎵") ||
                m.contains("👾") ||
                m.contains("📷") ||
                m.contains("🎥") ||
                m.contains("📄") ||
                m.contains("🎤") ;

    }

    public void notifyUser(String title,String message) {


        try{

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {


                NotificationChannel channel = new NotificationChannel(ctx.getString(R.string.app_name), "Test",
                        NotificationManager.IMPORTANCE_HIGH);

                NotificationManager manager= (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);

              //  Toast.makeText(ctx, "true", Toast.LENGTH_SHORT).show();

            }

            Intent intent=new Intent(ctx, induvidualChat.class);
            intent.putExtra("user",title); // passing the user name who deleted the message

            // Setting flag to create a start a new activity if not already running or destroy the old one
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent=PendingIntent.getActivity(ctx,0,
                    intent,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_ONE_SHOT);

            Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // setting notification sound

            NotificationCompat.Builder Nbuilder=new NotificationCompat.Builder(ctx,
                    ctx.getString(R.string.app_name));


            Nbuilder.setAutoCancel(true);
            Nbuilder.setSmallIcon(R.drawable.message_icon);
            Nbuilder.setContentTitle(title+ " deleted a message");
            Nbuilder.setContentText(message);
            Nbuilder.setSound(sound);
            Nbuilder.setContentIntent(pendingIntent);
            Nbuilder.setPriority(NotificationCompat.PRIORITY_HIGH);



            NotificationManager manager= (NotificationManager) ctx
                    .getSystemService(NOTIFICATION_SERVICE);


            manager.notify(1,Nbuilder.build());

        }catch (Exception e)
        {
            Log.e("Notify user",e.getMessage());

        }





    }

     void saveDp(Bitmap finalBitmap,String name) {

        try{



        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {  // making file directory if doesnt exist
                file = new File (ctx.getExternalFilesDir(null) + "/Dp");
            } else {
                file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dp");
            }

            if (!file.exists()) {
                file.mkdirs();
            }
            Log.i("Exist",file.getPath());



        String fname = name+".jpg";
        File image = new File (file.getAbsolutePath()+"/"+fname);
        if (file.exists ()) image.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.i("saved","true");


        } catch (Exception e) {
            Log.i("saved",e.getMessage());
        }
        }catch (Exception e)
        {
            Log.e("saveDp",e.getMessage());

        }
    }
    void i(String s)
    {
        Log.i("NotificationReceiver",s);
    }
}
