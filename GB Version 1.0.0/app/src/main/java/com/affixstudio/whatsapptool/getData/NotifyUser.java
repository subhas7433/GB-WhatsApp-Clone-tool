package com.affixstudio.whatsapptool.getData;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.schedule_sms;

public class NotifyUser {

    Context context;
    String notificationName;// this will visible to the user in the manage notification section

    public NotifyUser(Context context, String notificationName) {
        this.context = context;
        this.notificationName = notificationName;
    }


    public NotifyUser(Context context) {
        this.context = context;
    }

    public  void notifi(String title, String message)
    {


        Intent intent=new Intent(context, schedule_sms.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent=PendingIntent.getActivity(context,6,
                intent,PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);// todo changed
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {


            NotificationChannel channel = new NotificationChannel(context.getString(R.string.app_name), notificationName,
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager= (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);


        }



        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // setting notification sound

        NotificationCompat.Builder Nbuilder=new NotificationCompat.Builder(context,
                context.getString(R.string.app_name));


        Nbuilder.setContentIntent(pendingIntent);
        Nbuilder.setAutoCancel(true);
        Nbuilder.setSmallIcon(R.drawable.schedule_icon);
        Nbuilder.setContentTitle(title);
        Nbuilder.setContentText(message);
        Nbuilder.setSound(sound);
        Nbuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        Nbuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


        NotificationManager manager= (NotificationManager) context
                .getSystemService(NOTIFICATION_SERVICE);


        manager.notify(1,Nbuilder.build());

    }


    public void UpdateUiOnNew( String action)
    {
        Intent local = new Intent();

        local.setAction(action);

        context.sendBroadcast(local);
    }



    public void whenMediaUpdated(String message,String title,Intent intent)
    {
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent=PendingIntent.getActivity(context,6,
                intent,PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);// todo changed

        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // setting notification sound

        NotificationCompat.Builder Nbuilder=new NotificationCompat.Builder(context,
                context.getString(R.string.app_name));


        Nbuilder.setAutoCancel(true);
        Nbuilder.setSmallIcon(R.drawable.message_icon);

        Nbuilder.setContentTitle(title);
        Nbuilder.setContentText(message);
        Nbuilder.setSound(sound);
        Nbuilder.setContentIntent(pendingIntent);
        Nbuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager= (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.app_name), context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getString(R.string.app_name));


            manager.createNotificationChannel(channel);
        }
        manager.notify(6,Nbuilder.build());
    }
}
