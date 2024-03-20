package com.affixstudio.whatsapptool.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.model.preferences.PreferencesManager;
import com.affixstudio.whatsapptool.model.utils.NotificationHelper;
import com.affixstudio.whatsapptool.receivers.NotificationServiceRestartReceiver;
import com.affixstudio.whatsapptool.serviceOur.NotificationService;
import com.affixstudio.whatsapptool.serviceOur.mediaWatcher;

import java.util.List;

public class KeepAliveService extends Service {
    private static final int FOREGROUND_NOTIFICATION_ID = 10;

    @Override
    public void onCreate() {
        Log.d("DEBUG", "KeepAliveService onCreate");
        super.onCreate();
        startForeground(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)

    {
//        super.onStartCommand(intent, flags, startId);
        Log.d("DEBUG", "KeepAliveService onStartCommand");
        startNotificationService();
        SharedPreferences sp=getSharedPreferences("affix",MODE_PRIVATE);

        if (sp.getBoolean(getString(R.string.privateChatOnTag),false)
        && arePermissionDenied() && !noNotificationPermission())
        {

            intent=new Intent(getApplicationContext(), mediaWatcher.class);

        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("DEBUG", "KeepAliveService onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("DEBUG", "KeepAliveService onUnbind");
        tryReconnectService();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DEBUG", "KeepAliveService onDestroy");
        tryReconnectService();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("DEBUG", "KeepAliveService onTaskRemoved");
        tryReconnectService();
    }

    public void tryReconnectService() {
        if (PreferencesManager.getPreferencesInstance(getApplicationContext()).isServiceEnabled()
                && PreferencesManager.getPreferencesInstance(getApplicationContext()).isForegroundServiceNotificationEnabled()) {
            Log.d("DEBUG", "KeepAliveService tryReconnectService");
            //Send broadcast to restart service
            Intent broadcastIntent = new Intent(getApplicationContext(), NotificationServiceRestartReceiver.class);
            broadcastIntent.setAction("Watomatic-RestartService-Broadcast");
            sendBroadcast(broadcastIntent);
        }
    }

    private void startNotificationService() {
        if (!isMyServiceRunning()) {
            Log.d("DEBUG", "KeepAliveService startNotificationService");
            Intent mServiceIntent = new Intent(this, NotificationService.class);
            startService(mServiceIntent);
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NotificationService.class.equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    private void startForeground(Service service) {
        Log.e("DEBUG", "startForeground");
        NotificationCompat.Builder notificationBuilder = NotificationHelper.getInstance(getApplicationContext()).getForegroundServiceNotification(service);
        service.startForeground(FOREGROUND_NOTIFICATION_ID, notificationBuilder.build());
    }

    private boolean arePermissionDenied() {

        String identifier="app%2F";

        List<UriPermission> list = getContentResolver().getPersistedUriPermissions();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            boolean isGot=true;
            for (int i=0;i<list.size();i++)
            {
                if (list.get(i).getUri().toString().contains(identifier))
                {
                    //indexInPermissionList=i;
                    isGot=false; // already got the permission
                    break;
                }
            }
            //  Log.i("arePermissionDenied",identifier+" indexInPermissionList "+indexInPermissionList+"  "+getActivity().getContentResolver().getPersistedUriPermissions().size());

//            if (isGot)
//            {
//                requestPermissionQ();
//            }
            return isGot;

            // return getContentResolver().getPersistedUriPermissions().size() <= 0;
        }

        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED) {

                return true;
            }
        }

        return false;
    }
    final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public Boolean noNotificationPermission() {
        String theList = android.provider.Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        String[] theListList = theList.split(":");
        String me = (new ComponentName(this, NotificationService.class)).flattenToString();
        for ( String next : theListList ) {
            if ( me.equals(next) ) return false;
        }
        return true;
    }
}
