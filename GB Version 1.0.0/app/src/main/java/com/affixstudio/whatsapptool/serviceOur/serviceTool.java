package com.affixstudio.whatsapptool.serviceOur;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.util.List;

public class serviceTool {


    public static boolean isServiceRunning(String serviceClassName ,Context c){
        final ActivityManager activityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }
}