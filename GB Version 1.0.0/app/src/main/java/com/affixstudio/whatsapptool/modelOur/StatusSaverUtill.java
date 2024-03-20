package com.affixstudio.whatsapptool.modelOur;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.affixstudio.whatsapptool.R;

import java.io.File;

public class StatusSaverUtill {
    public static File rootDirectoryWhatsapp=null;

    Context con;

    public StatusSaverUtill(Context con) {
        this.con = con;
    }

    public static File detectPath(Context c)

    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            rootDirectoryWhatsapp = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+c.getString(R.string.app_name) );
        } else {
            rootDirectoryWhatsapp = new File(Environment.getExternalStorageDirectory() + "/Download"+"/"+c.getString(R.string.app_name));
        }
        return rootDirectoryWhatsapp;
    }

    public File makeScheduleMediaDirectory(){

        File f=new File(con.getExternalFilesDir(null),con.getString(R.string.schedule_media_folder_name));;
        try {


            if (!f.exists()) {

                Log.i("makeMainDirectory","dont exist");
                if (f.mkdirs())
                {
                    Log.i("makeMainDirectory","created");
                    return f;
                }

            }
        }catch (Exception e)
        {
            Log.e("makeMainDirectory",e.getMessage());
        }
        return f;
    }

    public File makeMainDirectory()
    {

        File f=null;
        try {


            String folder_main = "GBWhats";


            f = new File(con.getExternalFilesDir(null),"/WhatsApp/Images");
            // f=new File(con.getExternalFilesDir(null),folder_main);
            if (!f.exists()) {
                f.mkdirs();
                Log.i("makeMainDirectory","dont exist");
                return f;
            }
        }catch (Exception e)
        {
            Log.e("makeMainDirectory",e.getMessage());
        }
        return f;
    }

    public File makeAudioDirectory(){

        File f=null;
        try {


            String folder_main = "GBWhats";


            f = new File(con.getExternalFilesDir(null),"/WhatsApp/Audios");
            // f=new File(con.getExternalFilesDir(null),folder_main);
            if (!f.exists()) {
                f.mkdirs();
                Log.i("makeMainDirectory","dont exist");
                return f;
            }
        }catch (Exception e)
        {
            Log.e("makeMainDirectory",e.getMessage());
        }
        return f;
    }
    public File makeVideoDirectory(){

        File f=null;
        try {


            String folder_main = "GBWhats";


            f = new File(con.getExternalFilesDir(null),"/WhatsApp/Videos");
            // f=new File(con.getExternalFilesDir(null),folder_main);
            if (!f.exists()) {
                f.mkdirs();
                Log.i("makeMainDirectory","dont exist");
                return f;
            }
        }catch (Exception e)
        {
            Log.e("makeMainDirectory",e.getMessage());
        }
        return f;
    }

    public File makeDirectory(String path){

        File f=null;
        try {





            f = new File(con.getExternalFilesDir(null),path );

            if (!f.exists())
            {

                f.mkdirs();
                Log.i("makeMainDirectory","dont exist");
                return f;

            }
            else
            {
                return f;
            }

        }
        catch (Exception e)
        {
            Log.e("makeMainDirectory",e.getMessage());
        }
        return f;
    }
    public File makeGIFDirectory(){

        File f=null;
        try {


            String folder_main = "GBWhats";


            f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/GBWhats/GIF");
            // f=new File(con.getExternalFilesDir(null),folder_main);
            if (!f.exists()) {
                f.mkdirs();
                Log.i("makeMainDirectory","dont exist");
                return f;
            }
        }catch (Exception e)
        {
            Log.e("makeMainDirectory",e.getMessage());
        }
        return f;
    }
    public static void createFolder(Context c)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            rootDirectoryWhatsapp = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+c.getString(R.string.app_name) );
        } else {
            rootDirectoryWhatsapp = new File(Environment.getExternalStorageDirectory() + "/Download"+"/"+c.getString(R.string.app_name));
        }
        if (!rootDirectoryWhatsapp.exists())
            rootDirectoryWhatsapp.mkdir();




    }


}
