package com.affixstudio.whatsapptool.serviceOur;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.database.Cursor;
import android.net.Uri;
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

public class contentObserver extends FileObserver {


    Context c;

    String path;
    int indicator;
     String ChangedFileName="";

    public contentObserver(String path,Context c,int indicator) {
        super(path);
        this.c=c;
        this.path=path;
        this.indicator=indicator;

        Log.i("contentObserver","started");

    }


    int p=0;
    String store="null Private Sent";
    SharedPreferences sp;
    @Override
    public void onEvent(int i, @Nullable String s) {

        Log.i("contentObserver", indicator+"  "+i+"  " + s);
        sp = c.getSharedPreferences("affix",Context.MODE_PRIVATE);
        if (i==512)
        {
            try {
                Log.i("512", indicator+"  " + s);


                if (sp.getBoolean(c.getString(R.string.recoverMediaOnTag),false))
                {
                    saveInDatabase(s);
                }


            }catch (Exception e)
            {
                Log.e("contentObserver",e.getMessage());
            }


        } else if (s!=null && !store.contains(s) && p==0) //p is indication it is first time to prevent duplicate entry

       {


           Log.i(indicator+"changedName", "Entered  " + s);
           StatusSaverUtill ss = new StatusSaverUtill(c);
           String directoryPath = "/WhatsApp/";

           if (indicator == 3) //documents
           {
               directoryPath += "Documents";
               executeNew("WhatsApp Documents", ss.makeDirectory(directoryPath));
           } else if (indicator == 5) //sticker
           {
               directoryPath += "Stickers";

               executeNew("WhatsApp Stickers", ss.makeDirectory(directoryPath));

           }
           ChangedFileName = s;


       }


    }

    private void saveInDatabase(String s) {


        // just code to save deleted file names in database #todo have to test is it working
        String query="CREATE TABLE IF NOT EXISTS recoverMedia (_id INTEGER PRIMARY KEY autoincrement,MediaName text, MediaType INTEGER,time text) ";
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        try{
            File f=new File(path+"/"+s);
            Log.i("contentObserver",s+" exist in whatsapp "+f.exists());

            database db = new database(c, c.getString(R.string.recover_media_table_name),query,1);

            if (db.insertDeletedMediaData(s,indicator,currentDateandTime,c.getString(R.string.recover_media_table_name)))//indicator is number which specify the file type as set in mediaWatcher.jata
            {
                NotifyUser nu=new NotifyUser(c);
                nu.UpdateUiOnNew(c.getString(R.string.updateUIbrodcustRecever));//sending broadcast to update ui
                Intent intent = new Intent(c, private_view_media_small.class); //#todo settting image uri for private chat


                intent.putExtra("mediaType",indicator);
                intent.putExtra("isPrivateActivity",false);

                nu.whenMediaUpdated(c.getString(R.string.mediaDeletedNotiMsg),"Recover Chat",intent);

            }


        } catch (Exception e)
        {
            Log.e("contentObserver",e.getMessage());
        }



    }

    Uri returnUri=null;
    private void executeNew(String folderName,File mDirectory) {

        Log.i("tergetFileList","executeNew"+p);

        p=1;
        Executors.newSingleThreadExecutor().execute(() -> {


            List<UriPermission> list1 = c.getContentResolver().getPersistedUriPermissions();

            DocumentFile file1 = DocumentFile.fromTreeUri(c, list1.get(0).getUri());





            if (file1 == null) {

                return ;
            }


            DocumentFile[] tergetFileList = file1.listFiles();



            int e=0;

            for(int i=0;i<tergetFileList.length;i++)
            {
                if (tergetFileList[i].getName().equals(folderName))
                {
                    DocumentFile[] file=tergetFileList[i].listFiles();
     //               DocumentFile [] voiceFolder= file[32].listFiles(); // 32 index is latest file in realme 6
                    for (DocumentFile documentFile : file) {

                        Log.i("Uri",file.length+"  File Name = "+getFileName(documentFile.getUri())+"  Uri ");
                        if (documentFile.getName().equals(ChangedFileName))
                        {

                            Log.i("Uri",file.length+"  ChangedFileName = "+getFileName(documentFile.getUri())+"  Uri ");
                            returnUri= documentFile.getUri();
                            saveFile(ChangedFileName,mDirectory);

                            return;
                        }





                    }

                    Log.i("tergetFileList",i+" file Name"+tergetFileList[i].getName());
                }

                // Log.i("tergetFileList","first file "+file[0].getName());
            }



        });










    }

    void saveFile(String s,File makeDirectory)
    {



        Log.i("contentObserver",p+" changed file name "+s+" "+indicator);
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

                    p=0;
                }




                //#todo save the file name in data base to show in recover section when its deleted

            }
            catch (Exception e)
            {
                Log.i("contentObserverError",e.getMessage());
            }



        } else {

            Log.i("contentObserver","ELSE  s!=null && !store.contains(s) && p==0 && returnUri!=null");
            
        }
    }





    @SuppressLint("Range")
    public String getFileName(Uri uri)
    {
        String result = null;
        try {
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
        }catch (Exception e)
        {
            return "123 ";
        }

        return result;
    }
}
