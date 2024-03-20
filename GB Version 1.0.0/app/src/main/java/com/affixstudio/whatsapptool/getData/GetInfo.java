package com.affixstudio.whatsapptool.getData;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.affixstudio.whatsapptool.serviceOur.serviceTool.isServiceRunning;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.private_chat;
import com.affixstudio.whatsapptool.activityOur.scheduleAction;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.serviceOur.NotificationService;
import com.affixstudio.whatsapptool.serviceOur.mediaWatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetInfo {
    @SuppressLint("Range")
    public String getFileName(Uri uri, Context context) {
        try {


            String result = null;
            if (uri.getScheme().equals("content")) {
                Cursor cursor =context.getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally
                {
                    cursor.close();
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            return result; //todo set doc in the list and show in the show dialog
        }catch (Exception e)
        {

            Log.e(context.toString(),e.getMessage());
            return "failed";
        }
    }

    public String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }
    String[] icons;
    public ArrayList<String> getPrediction(int mediaType,Context c,Long fileModified)
    {

        try {


        getMiliDiff gmf=new getMiliDiff();
        ArrayList<String> predictedName=new ArrayList<>();
         icons=c.getResources().getStringArray(R.array.privateMediaIcon);
        String query2="CREATE TABLE IF NOT EXISTS "+c.getString(R.string.privateMediaRecordTable)+" (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER ) ";

        database db=new database(c,c.getString(R.string.privateMediaRecordTable),query2,1);

        ArrayList<String> names=new ArrayList<>();
        ArrayList<String> date=new ArrayList<>();
        ArrayList<String> message=new ArrayList<>();

        Cursor cu=db.getinfo(1);
        while (cu.moveToNext())
        {
            names.add(cu.getString(1));
            message.add(cu.getString(2));
            date.add(cu.getString(3));
        }


        for (int i=names.size()-1;i>=0;i--)
        {
            if (predictedName.size()==3)
            {
                break;
            }else if (!predictedName.contains(names.get(i))) // is the already in the list
            {
                Log.i("GetInfo","!predictedName.contains  "+!predictedName.contains(names.get(i)) +" names.get(i) "+names.get(i));
                if (isContain(message.get(i),mediaType)  ) // is the message of media
                {
                    Log.i("GetInfo","message.contains  message "+message.get(i)+" icons "+icons[mediaType]);
                    if (gmf.isRecordedBeforeFile(fileModified,date.get(i))) // will check the records which is only recorded before file saved
                    {
                        Log.i("GetInfo","gmf.isRecordedBeforeFile");
                        predictedName.add(names.get(i));
                        Log.i("GetInfo","predictedName size "+predictedName.size()+" predictedName = "+predictedName.get(predictedName.size()-1));
                    }
                }
            }



        }



        return predictedName;
        }catch (Exception e)
        {
            Log.e("GetInfo",e.getMessage());
            return new ArrayList<>();
        }
    }

    private boolean isContain(String s, int mediaType) {

        if (mediaType==0)
        {
            return s.contains(icons[mediaType]) || s.contains(icons[5]);
        }else if (mediaType==1)
        {
            return s.contains(icons[mediaType]) || s.contains(icons[6]);
        }else {
            return s.contains(icons[mediaType]);
        }



    }


    public boolean shouldOn(Context context,String tegName)
    {
        SharedPreferences sp=context.getSharedPreferences("affix",MODE_PRIVATE);
        if (!isServiceRunning(mediaWatcher.class.getName(), context))
        {
            if (sp.getBoolean(tegName,false))
            {
                return !arePermissionDenied(context) && !noNotificationPermission(context);
            }else {
                return false;
            }
        }else
        {
            return true;
        }




    }

    public boolean arePermissionDenied(Context context) {

        String identifier="app%2F";

        List<UriPermission> list = context.getContentResolver().getPersistedUriPermissions();


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
            if (ActivityCompat.checkSelfPermission(context, permissions) != PackageManager.PERMISSION_GRANTED) {

                return true;
            }
        }

        return false;
    }

    public Boolean noNotificationPermission(Context c) {
        String theList = android.provider.Settings.Secure.getString(c.getContentResolver(), "enabled_notification_listeners");
        String[] theListList = theList.split(":");
        String me = (new ComponentName(c, NotificationService.class)).flattenToString();
        for ( String next : theListList ) {
            if ( me.equals(next) ) return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void requestPermissionQ(Context ctx, ActivityResultLauncher<Intent> activityResultLauncher) {
        StorageManager sm = (StorageManager) ctx.getSystemService(Context.STORAGE_SERVICE);

        Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
        String startDir= "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia" ;
//        if (selection.equals("Whatsapp"))
//        {
//            startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
//        }else {
//            startDir = "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses";
//        }
        //Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses (whatsapp)
        //Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses (whatsapp business)

        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

        String scheme = uri.toString();
        scheme = scheme.replace("/root/", "/document/");
        scheme += "%3A" + startDir;

        uri = Uri.parse(scheme);

        Log.d("URI", uri.toString());

        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        activityResultLauncher.launch(intent);
        Log.i("status","requestPermissionQ");


    }

   public boolean noStoragePermission(Context c)
    {
        boolean t=false;
        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(c, permissions) != PackageManager.PERMISSION_GRANTED) {


                t= true;
            }
        }
        return t;
    }
    final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public Uri getShareableUri(Uri uri,Context c)
    {

        File f=new File(uri.getPath());
        if (f.exists())
        {
          return  FileProvider.getUriForFile(
                  c,
                  "com.affixstudio.gbVersion.provider",
                  f);
        }else
        {
            return null;
        }

    }

    static  public boolean isOnline(Context c)
    {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }



}
