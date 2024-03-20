package com.affixstudio.whatsapptool.getData;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.affixstudio.whatsapptool.activityOur.no_internet_Screen;

import java.util.List;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private boolean isConnected=false;


    @Override
    public void onReceive(Context context, Intent intent) {

        isNetworkAvailable(context);

    }


    private void isNetworkAvailable(Context context)
    {

        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null)
        {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
            {
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected)
                        {
                            // Toast.makeText(context, "Now you are connected to Internet!", Toast.LENGTH_SHORT).show();

                            isConnected = true;

                            //do your processing here ---
                            //if you need to post any data to the server or get status
                            //update from the server
                        }

                        return;
                    }
                }
            }
        }




        if (!isAppIsInBackground(context))
        {
            context.startActivity(new Intent(context, no_internet_Screen.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

//        new android.app.AlertDialog.Builder(MainActivity.this)
//                .setIcon(R.drawable.ic_baseline_warning_24)
//                .setTitle("No Internet")
//                .setCancelable(false)
//                .setMessage("Your phone is not connected with the internet.Please turn on the internet connection and try again.")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        MainActivity.this.finish();
//
//                        startActivity(new Intent(MainActivity.this,MainActivity.class));
//                    }
//                })
//                .show();
        // Toast.makeText(context, "You are not connected to Internet!", Toast.LENGTH_SHORT).show();

        isConnected = false;
    }
    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }

        return isInBackground;
    }
}
