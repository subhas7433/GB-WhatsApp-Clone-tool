package com.affixstudio.whatsapptool.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.getData.GetInfo;
import com.affixstudio.whatsapptool.serviceOur.mediaWatcher;

public class t extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            String action = intent != null ? intent.getAction() : null;
            if (action!=null)
            {
                if (action.equals("android.intent.action.MY_PACKAGE_REPLACED"))
                {

                    restart(context);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }




    }

     private void restart(Context context)
     {

         GetInfo getInfo=new GetInfo();

         Intent start=new Intent(context, mediaWatcher.class);
         if (getInfo.shouldOn(context,context.getString(R.string.privateMediaOnTag))
         || getInfo.shouldOn(context,context.getString(R.string.recoverMediaOnTag)) )
         {
            context.startService(start);
         }





     }
 }
