package com.affixstudio.whatsapptool.serviceOur;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.FileObserver;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.getData.NotifyUser;
import com.affixstudio.whatsapptool.modelOur.StatusSaverUtill;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.private_view_media_small;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class voiceObserver extends FileObserver {


    Context c;
    String path;
    int indicator;
    public voiceObserver(String path, Context c,int indicator) {
        super(path);
        this.path=path;
        Log.i("voiceObserver", "Path  " + path);
        this.c=c;
        this.indicator=indicator;

    }

    @Override
    public void onEvent(int i, @Nullable String s) {
        if (i==512)
        {
            try {

                saveInDatabase(s);
            }catch (Exception e)
            {
                Log.e("contentObserver",e.getMessage());
            }


        }


        if (s!=null && !store.contains(s) && p==0) //p is indication it is first time to prevent duplicate entry

        {

            Log.i("changedName", "Entered  " + s);
            StatusSaverUtill ss = new StatusSaverUtill(c);
            String directoryPath = "/WhatsApp/Voices";

            Log.i("contentObserver",Build.VERSION.SDK_INT+" api");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                executeNew("WhatsApp Voice Notes", ss.makeDirectory(directoryPath),s);
            }else
            {
                if (p==0)
                {

                    Log.i("contentObserver","else");
                    p=1;
                    executeOld(path,s,ss.makeDirectory(directoryPath));
                }

            }




            // not getting gif and stickers



        }


    }





    Uri returnUri=null;

    void executeOld(String path,String s,File makeDirectory)
    {
        File targetFile = new File(path);
        File[] voiceNotes=targetFile.listFiles();
        for (File value : voiceNotes) { // end voice files

            Log.i("executeOld"," value Name "+value.getName());
            if (value.getName().equals(s)) {
                InputStream inputStream = null;
                try {
                    inputStream = c.getContentResolver().openInputStream(Uri.fromFile(value));
                    byte[] recordData = IOUtils.toByteArray(inputStream); //https://stackoverflow.com/questions/2436385/android-getting-from-a-uri-to-an-inputstream-to-a-byte-array
                    //https://stackoverflow.com/questions/2091454/byte-to-inputstream-or-outputstream
                    FileOutputStream out = new FileOutputStream(new File(makeDirectory.getPath() +"/"+s));


                    out.write(recordData);

                    IOUtils.closeQuietly(out);
                    recordInDB(s);

                    Log.i("contentObserver",makeDirectory.getName()+" saved successfully");
                    p=0;
                } catch (Exception e) {
                    Log.e("contentObserver",e.getMessage());
                }
                return;

            }


        }





    }
    private void executeNew(String folderName, File mDirectory, String ChangedFileName) {

        Log.i("tergetFileList","executeNew"+p);

        p=1;
        Executors.newSingleThreadExecutor().execute(() -> {


            List<UriPermission> list1 = c.getContentResolver().getPersistedUriPermissions();

            DocumentFile file1 = DocumentFile.fromTreeUri(c, list1.get(0).getUri());





            if (file1 == null) {

                return ;
            }


            DocumentFile[] tergetFileList = file1.listFiles(); //



            int e=1;
            int vfName=0;
            int latestVoiceNumberFile=0;
            DocumentFile [] voiceFolder=null;


            for(int i=0;i<tergetFileList.length;i++) // list of whatsapp folders
            {


                if (tergetFileList[i].getName().equals(folderName))
                {


                    DocumentFile[] file=tergetFileList[i].listFiles();

                    for (DocumentFile vf:file){

                        Log.i("tergetFileList"," vf Name "+getFileName(vf.getUri()));

                        if (!getFileName(vf.getUri()).contains(".")) {


                            vfName = Integer.parseInt(getFileName(vf.getUri()));
                            if (vfName > e) {

                                if (latestVoiceNumberFile<vfName)
                                {
                                    voiceFolder = vf.listFiles(); // getting latest number file
                                    latestVoiceNumberFile=vfName;
                                    Log.i("tergetFileList",e+" vf Name e "+vfName);
                                }

                            }
                            e = vfName;
                        }
                    }



                    for (DocumentFile value : voiceFolder)
                    { // end voice files

                        Log.i("tergetFileList"," value Name "+getFileName(value.getUri()));

                        if (getFileName(value.getUri()).equals(ChangedFileName))
                        {
                            returnUri = value.getUri();
                            Log.i("voiceGot",getFileName(value.getUri()));

                            saveFile(ChangedFileName,mDirectory);
                            return;

                        }


                    }






                    Log.i("tergetFileList",i+" file Name "+tergetFileList[i].getName());
                }

                // Log.i("tergetFileList","first file "+file[0].getName());
            }



        });










    }

    void saveFile(String s, File makeDirectory)
    {



        Log.i("contentObserver",p+" changed file name "+s+" ");

        if(/*s!=null && !store.contains(s) && p==0 &&*/ returnUri!=null )
        {

            try {




                if (p==1)
                {
                    InputStream inputStream = null;
                    try {
                        inputStream = c.getContentResolver().openInputStream(returnUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    byte[] recordData = IOUtils.toByteArray(inputStream); //https://stackoverflow.com/questions/2436385/android-getting-from-a-uri-to-an-inputstream-to-a-byte-array
                    //https://stackoverflow.com/questions/2091454/byte-to-inputstream-or-outputstream
                    FileOutputStream out = new FileOutputStream(new File(makeDirectory.getPath() +"/"+s));


                    out.write(recordData);

                    IOUtils.closeQuietly(out);
                    Log.i("contentObserver",makeDirectory.getName()+" saved successfully");
                    recordInDB(s);
                    p=0;
                }


//                File victim=new File(returnUri.toString());
//                if (!victim.isDirectory() )
//
//                {
//
//                    byte[] recordData = IOUtils.toByteArray(inputStream); //https://stackoverflow.com/questions/2436385/android-getting-from-a-uri-to-an-inputstream-to-a-byte-array
//                    //https://stackoverflow.com/questions/2091454/byte-to-inputstream-or-outputstream
//                    FileOutputStream out = new FileOutputStream(new File(makeDirectory +"/"+s));
//
//
//                    out.write(recordData);
//
//                    IOUtils.closeQuietly(out);
//
////                    InputStream inputStream= c.getContentResolver().openInputStream(Uri.fromFile(victim));
////                    byte[] bytes= IOUtils.toByteArray(inputStream);
////                    FileOutputStream out=new FileOutputStream(new File(makeDirectory +"/"+s));
////                    out.write(bytes);
////
////                    out.close();
//                    Log.i("contentObserver",makeDirectory.getName()+" saved successfully");
//                    p=1;
//
//                }else {
//                    Log.i("contentObserver","victim.isDirectory");
//                }


                //#todo save the file name in data base to show in recover section when its deleted

            }catch (Exception e)
            {
                Log.i("contentObserverError",e.getMessage());
            }



        } else {

            Log.i("contentObserver","ELSE  s!=null && !store.contains(s) && p==0 && returnUri!=null");

        }
    }

    String store="null Private Sent";
    int p=0;
    private void saveInDatabase(String s) {


        // just code to save deleted file names in database #todo have to test is it working
        String query="CREATE TABLE IF NOT EXISTS recoverMedia (_id INTEGER PRIMARY KEY autoincrement,MediaName text, MediaType INTEGER,time text) ";
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        try{

            database db = new database(c, c.getString(R.string.recover_media_table_name),query,1);

            if (db.insertDeletedMediaData(s,indicator,currentDateandTime,c.getString(R.string.recover_media_table_name)))//indicator is number which specify the file type as set in mediaWatcher.jata
            {
                NotifyUser nu=new NotifyUser(c);
                nu.UpdateUiOnNew(c.getString(R.string.updateUIbrodcustRecever));//sending broadcast to update ui

                SharedPreferences sp= c.getSharedPreferences("affix",Context.MODE_PRIVATE);


                if (sp.getBoolean(c.getString(R.string.privateNotificationEnableTag),false)) // when notification is enabled
                {
                    Intent intent = new Intent(c, private_view_media_small.class); //#todo settting image uri for private chat


                    Log.i("voiceObserver","indicator is "+indicator);
                    intent.putExtra("mediaType",indicator);
                    intent.putExtra("isPrivateActivity",false);

                    nu.whenMediaUpdated(c.getString(R.string.mediaReceivedNotiMessage),"Recover Chat",intent);



                }
            }


        }catch (Exception e)
        {
            Log.e("contentObserver",e.getMessage());
        }



    }


    void recordInDB(String s)
    {
        SharedPreferences sp= c.getSharedPreferences("affix",Context.MODE_PRIVATE);
        String query="CREATE TABLE IF NOT EXISTS "+c.getString(R.string.privateMediaNamesTable)+" (_id INTEGER PRIMARY KEY autoincrement,MediaName text, MediaType INTEGER,time text) ";
        database db=new database(c,c.getString(R.string.privateMediaNamesTable),query,1);
        db.insertInMediaNames(indicator,s);

        NotifyUser nu=new NotifyUser(c);
        nu.UpdateUiOnNew(c.getString(R.string.updateUIbrodcustRecever));//sending broadcast to update ui


        if (sp.getBoolean(c.getString(R.string.privateNotificationEnableTag),false)) // when notification is enabled
        {
            Intent intent = new Intent(c, private_view_media_small.class); //#todo settting image uri for private chat


            intent.putExtra("mediaType",indicator);
            intent.putExtra("isPrivateActivity",true);

            nu.whenMediaUpdated(c.getString(R.string.mediaReceivedNotiMessage),"No Blue Tick",intent);



        }

    }
    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = c.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
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
        return result;
    }

}
