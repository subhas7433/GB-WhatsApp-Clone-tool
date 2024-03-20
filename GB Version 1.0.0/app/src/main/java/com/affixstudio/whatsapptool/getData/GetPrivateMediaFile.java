package com.affixstudio.whatsapptool.getData;

import static com.affixstudio.whatsapptool.modelOur.NotificationRecever.query;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.modelOur.StatusSaverUtill;
import com.affixstudio.whatsapptool.modelOur.database;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class GetPrivateMediaFile {



    public ArrayList<Uri> getFile(String folderName)
    {

        ArrayList<Uri> list=new ArrayList<>();
     //   Executors.newSingleThreadExecutor().execute(() -> {


            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());


            try {


                String targetPath="";

                targetPath =   Environment.getExternalStorageDirectory()+
                        "/GBWhats/WhatsApp/"+folderName;

                File targetFile = new File(targetPath);


                Log.i("GetPrivateMediaFile",targetPath+"   targetFile.exists() "+targetFile.exists());





                if (targetFile.exists())
                {
                    Log.i("GetPrivateMediaFile","targetFile.listFiles() "+ Objects.requireNonNull(targetFile.listFiles()).length);

                    for (File file: targetFile.listFiles())
                    {

                        if (!Uri.fromFile(file).toString().endsWith(".nomedia"))
                        {
                            list.add(Uri.fromFile(file));
                        }

                    }
                //    return list;


                }else {
                //    return new ArrayList<>();
                }
            }catch (Exception e)
            {
                Log.i("GetPrivateMediaFile",e.getMessage());
             //   return new ArrayList<>();
            }

      //  });


        return list;
    }

    public ArrayList<Uri> getRecordedFiles(String folderName, Context c,String tableName)
    {

        ArrayList<Uri> list=new ArrayList<>();
        //   Executors.newSingleThreadExecutor().execute(() -> {


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        try {




            File targetFile = new File(c.getExternalFilesDir(null),"/WhatsApp/"+folderName);


           // Log.i("GetPrivateMediaFile",targetPath+"   targetFile.exists() "+targetFile.exists());


            ArrayList<String> mediaName=getRecoveredFileNames(c,tableName);

            if (mediaName.size()==0) // no media is deleted
            {
                return new ArrayList<>();
            }



            if (targetFile.exists())
            {

                Log.i("GetPrivateMediaFile","folderPath = "+targetFile+" targetFile.listFiles() "+ Objects.requireNonNull(targetFile.listFiles()).length);

                for (File file: targetFile.listFiles())
                {

                    if (!Uri.fromFile(file).toString().endsWith(".nomedia")
                    && mediaName.contains(file.getName())) // is the name available in database
                    {

                        list.add(Uri.fromFile(file));
                    }

                }
                //    return list;


            }else {
                //    return new ArrayList<>();
            }
        }catch (Exception e)
        {
            Log.i("GetPrivateMediaFile",e.getMessage());
            //   return new ArrayList<>();
        }

        //  });


        return list;
    }



    public void deleteAllScheduledMedia(File dir) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        try {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (dir.isDirectory())
                    {

                        String[] children = dir.list();
                        Log.i("privateMedia","children "+ Objects.requireNonNull(children).length);
                        for (int i = 0; i < Objects.requireNonNull(children).length; i++)
                        {
                            try {
                                Thread.sleep(20);
                                File f=new File(dir, children[i]);
                                if (f.isDirectory())
                                {
                                    for (File file: Objects.requireNonNull(f.listFiles()))
                                    {
                                        file.delete();
                                    }

                                }else {
                                    f.delete();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        }





                    }else {
                        Log.i("privateMedia","dir.exists() "+dir.exists());
//                        String[] children = dir.list();
//                        Log.i("privateMedia","children "+ children.length);
//                        Log.i("privateMedia","!dir.isDirectory()");

                    }
                }
            }).start();



        }catch (Exception e)
        {
            Log.e("privateMedia","deleteAllScheduledMedia "+e.getMessage());
        }
        // The directory is now empty so delete it

    }

    @SuppressLint("StaticFieldLeak")
    public boolean deleteRecordedMediaOneFolder(String folderName, Context c, int tableNameID) // to delete just a  folder  from small screen
    {
        ProgressDialog pd=new ProgressDialog(c);
        pd.setCancelable(false);
        pd.setMessage("Deleting wait..");



        new AsyncTask<String, String, String>() {


            @Override
            protected String doInBackground(String... strings) {
                try {

                    String sql=c.getString(R.string.tableCreationSQLPart1)+" "+ c.getString(tableNameID)+" "+c.getString(R.string.privateMediaNamesSQL);

                    database db=new database(c,c.getString(tableNameID),sql,1);

                    StatusSaverUtill ssu=new StatusSaverUtill(c);



                    if (tableNameID==R.string.recover_media_table_name) // if the deleting from recover then delete the file if not in the #private table
                    {
                        Cursor cursor=db.getinfo(1);
                        String sql2=c.getString(R.string.tableCreationSQLPart1)+" "+ c.getString(R.string.privateMediaNamesTable)+" "+c.getString(R.string.privateMediaNamesSQL);
                        database db1=new database(c,c.getString(R.string.privateMediaNamesTable),sql2,1);

                        Cursor privateRecords=db1.getinfo(1);

                        while (cursor.moveToNext())
                        {
                            boolean isFoundInPrivate=false;

                            while (privateRecords.moveToNext())
                            {
                                if (cursor.getString(1).equals(privateRecords.getString(1)))
                                {
                                    isFoundInPrivate=true;
                                    break;

                                }

                            }

                            if (!isFoundInPrivate)
                            {
                                String path = ssu.makeDirectory("WhatsApp") +"/"+
                                        folderName + "/" + cursor.getString(1);

                                File file = new File(path);

                                if (file.exists())
                                {
                                    file.delete();
                                }

                            }
                        }

                        cursor.close();
                        privateRecords.close();


                        // deleting media icon records
                        String sql3=c.getString(R.string.tableCreationSQLPart1)+" "+ c.getString(R.string.privateMediaRecordTable)+" "+c.getString(R.string.privateMediaRecordSQL);
                        database db3=new database(c,c.getString(tableNameID),sql3,1);
                        db3.getWritableDatabase().execSQL("DELETE FROM "+c.getString(R.string.privateMediaRecordTable)+" WHERE 1");

                        db1.close();
                        db3.close();

                    }


                    String deleteQuery="DELETE FROM "+c.getString(tableNameID)+" WHERE 1";
                    db.getWritableDatabase().execSQL(deleteQuery);

                    db.close();
                }catch (Exception e)
                {
                    Log.e(this.toString(),e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPreExecute() {

                pd.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {


                pd.dismiss();
                super.onPostExecute(s);
            }
        }.execute();


        return true;


    }

    @SuppressLint("StaticFieldLeak")
    public boolean deleteSingleMediaRecord(int tableNameID, Context c,String fileName,Uri uri)
    {
        ProgressDialog pd=new ProgressDialog(c);
        pd.setCancelable(false);
        pd.setMessage("Deleting ..");



        new AsyncTask<String, String, String>() {


            @Override
            protected String doInBackground(String... strings) {
                try {
                    String[] folderNames=c.getResources().getStringArray(R.array.mediaFolderNames);
                    String sql=c.getString(R.string.tableCreationSQLPart1)+" "+ c.getString(tableNameID)+" "+c.getString(R.string.privateMediaNamesSQL);

                    database db=new database(c,c.getString(tableNameID),sql,1);

                    StatusSaverUtill ssu=new StatusSaverUtill(c);


                    boolean shouldNotDelete=false; // indication should the file be deleted?

                    if (tableNameID==R.string.recover_media_table_name) // if the deleting from recover then delete the file if not in the #private table
                    {
                        Cursor cursor=db.getinfo(1);

                        String sql2=c.getString(R.string.tableCreationSQLPart1)+" "+ c.getString(R.string.privateMediaNamesTable)+" "+c.getString(R.string.privateMediaNamesSQL);

                        database db1=new database(c,c.getString(R.string.privateMediaNamesTable),sql2,1);

                        Cursor privateRecords=db1.getinfo(1);


                        while (cursor.moveToNext())
                        {
                            if (cursor.getString(1).equals(fileName))
                            {
                                shouldNotDelete=true;
                                break;

                            }

                        }
                        if (!shouldNotDelete) // when file doesn't found in previous loop
                        {
                            while (privateRecords.moveToNext())
                            {
                                if (privateRecords.getString(1).equals(fileName))
                                {
                                    shouldNotDelete = true;
                                    break;

                                }
                            }
                        }


                        //deleting from private chat table
                        db1.getWritableDatabase().execSQL("DELETE FROM "+c.getString(R.string.privateMediaNamesTable)+" WHERE MediaName='"+fileName+"'");

                        cursor.close();
                        privateRecords.close();



                        db1.close();
                        if (!shouldNotDelete)
                        {
                            File file=new File(uri.getPath());
                            if (file.exists())
                            {
                                file.delete();
                            }
                        }


                    }


                    String deleteQuery="DELETE FROM "+c.getString(tableNameID)+" WHERE MediaName='"+fileName+"'";
                    db.getWritableDatabase().execSQL(deleteQuery);

                    db.close();
                }catch (Exception e)
                {
                    Log.e(this.toString(),e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPreExecute() {

                pd.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {


                pd.dismiss();
                super.onPostExecute(s);
            }
        }.execute();

        return true;
    }

    @SuppressLint("StaticFieldLeak")
    public boolean deleteMediaRecordsAllFolder(int tableNameID, Context c) // for recover and private
    {
        ProgressDialog pd=new ProgressDialog(c);
        pd.setCancelable(false);
        pd.setMessage("Deleting wait..");



            new AsyncTask<String, String, String>() {


                @Override
                protected String doInBackground(String... strings) {
                    try {
                    String[] folderNames=c.getResources().getStringArray(R.array.mediaFolderNames);
                    String sql=c.getString(R.string.tableCreationSQLPart1)+" "+ c.getString(tableNameID)+" "+c.getString(R.string.privateMediaNamesSQL);

                    database db=new database(c,c.getString(tableNameID),sql,1);

                        StatusSaverUtill ssu=new StatusSaverUtill(c);



                    if (tableNameID==R.string.recover_media_table_name) // if the deleting from recover then delete the file if not in the #private table
                    {
                        Cursor cursor=db.getinfo(1);
                        String sql2=c.getString(R.string.tableCreationSQLPart1)+" "+ c.getString(R.string.privateMediaNamesTable)+" "+c.getString(R.string.privateMediaNamesSQL);
                        database db1=new database(c,c.getString(R.string.privateMediaNamesTable),sql2,1);

                        Cursor privateRecords=db1.getinfo(1);

                        while (cursor.moveToNext())
                        {
                            boolean isFoundInPrivate=false; // todo set view image and video in full screen from status and schedule
                            while (privateRecords.moveToNext())
                            {




                                        if (cursor.getString(1).equals(privateRecords.getString(1)))
                                        {
                                            isFoundInPrivate=true;
                                            break;

                                        }




                            }
                            if (!isFoundInPrivate)
                            {



                                for (String folderName : folderNames) // delete from all folder
                                {


                                    String path = ssu.makeDirectory("WhatsApp") +"/"+
                                            folderName + "/" + cursor.getString(1);


                                    File file = new File(path);

                                    if (file.exists()) {
                                        file.delete();
                                    }


                                }
                            }
                        }

                        cursor.close();
                        privateRecords.close();


                        // deleting media icon records
                        String sql3=c.getString(R.string.tableCreationSQLPart1)+" "+ c.getString(R.string.privateMediaRecordTable)+" "+c.getString(R.string.privateMediaRecordSQL);
                        database db3=new database(c,c.getString(tableNameID),sql3,1);
                        db3.getWritableDatabase().execSQL("DELETE FROM "+c.getString(R.string.privateMediaRecordTable)+" WHERE 1");

                        db1.close();
                        db3.close();

                    }


                    String deleteQuery="DELETE FROM "+c.getString(tableNameID)+" WHERE 1";
                    db.getWritableDatabase().execSQL(deleteQuery);

                    db.close();
                    }catch (Exception e)
                    {
                        Log.e(this.toString(),e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void onPreExecute() {

                    pd.show();
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {


                    pd.dismiss();
                    super.onPostExecute(s);
                }
            }.execute();


            return true;


    }



    public ArrayList<String> getRecoveredFileNames(Context c,String tableName)
    {
        String query="CREATE TABLE IF NOT EXISTS "+tableName+" (_id INTEGER PRIMARY KEY autoincrement,MediaName text, MediaType INTEGER,time text) ";
        database db = new database(c, tableName,query,1);
        Cursor cursor=db.getinfo(1);
        ArrayList<String> list=new ArrayList<>();
        while (cursor.moveToNext())
        {
            list.add(cursor.getString(1));
        }

        Log.i(getClass().toString(),"TableName = "+tableName+" list Size  "+list.size());
        cursor.close();
        db.close();
        return list;
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteAllMediaAndData(Context c)
    {



        new AsyncTask<String, String, String>() {

            ProgressDialog pd;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd=new ProgressDialog(c);
                pd.setMessage("Deleting..");
                pd.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {


                File file=new File(c.getExternalFilesDir(null).toString());

                Log.i("deleteAll",c.getExternalFilesDir(null).toString());

                if (file.isDirectory())
                {
                    Log.i("deleteAll"," file.isDirectory() ");


                    if (!Objects.isNull(file.listFiles()))
                    {
                        Log.i("deleteAll"," !Objects.isNull(file.listFiles()) ");

                        for (File f:file.listFiles())
                        {
                            Log.i("deleteAll"," File f:file.listFiles() ");


                                if (f.isDirectory())
                                {
                                    Log.i("deleteAll"," f.isDirectory() ");
                                    if (!Objects.isNull(f.listFiles()))
                                    {
                                        Log.i("deleteAll"," !Objects.isNull(f.listFiles()) "+f.listFiles().length);
                                        for (File f1:f.listFiles())
                                        {
                                            if (f1.isDirectory())
                                            {
                                                if (!Objects.isNull(f1.listFiles()))
                                                {
                                                    for (File f2:f1.listFiles())
                                                    {
                                                        f2.delete();
                                                    }
                                                }
                                                f1.delete(); //todo delete the database

                                            }
                                            else
                                            {
                                                f1.delete();
                                            }



                                        }

                                    }
                                    f.delete();
                                }
                                else
                                {
                                    f.delete();
                                }


                        }

                    }
                    else
                    {

                        file.delete();

                    }

                    file.delete();
                }
                else
                {
                    file.delete();

                }

                // private chats
                    String query="CREATE TABLE IF NOT EXISTS "+c.getString(R.string.privateChatTableName)+" (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER, unread INTEGER '1' ) ";
                    database db=new database(c,c.getString(R.string.privateChatTableName),query,1);
                    db.getWritableDatabase().execSQL("DELETE FROM "+c.getString(R.string.privateChatTableName)+" WHERE 1");

                    // private media records
                    query="CREATE TABLE IF NOT EXISTS "+c.getString(R.string.privateMediaRecordTable)+" (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER ) ";
                    db=new database(c,c.getString(R.string.privateMediaRecordTable),query,1);
                    db.getWritableDatabase().execSQL("DELETE FROM "+c.getString(R.string.privateMediaRecordTable)+" WHERE 1");

                    //Private media names
                     query="CREATE TABLE IF NOT EXISTS "+c.getString(R.string.privateMediaNamesTable)+" (_id INTEGER PRIMARY KEY autoincrement,MediaName text, MediaType INTEGER,time text) ";
                     db=new database(c,c.getString(R.string.privateMediaNamesTable),query,1);
                     db.getWritableDatabase().execSQL("DELETE FROM "+c.getString(R.string.privateMediaNamesTable)+" WHERE 1");


                    // recover message
                    query="CREATE TABLE IF NOT EXISTS "+c.getString(R.string.recover_message_table_name)+" (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER, unread INTEGER '1' ) ";
                    db=new database(c,c.getString(R.string.recover_message_table_name),query,2);
                    db.getWritableDatabase().execSQL("DELETE FROM "+c.getString(R.string.recover_message_table_name)+" WHERE 1");

                    query="CREATE TABLE IF NOT EXISTS "+c.getString(R.string.recover_media_table_name)+" (_id INTEGER PRIMARY KEY autoincrement,MediaName text, MediaType INTEGER,time text) ";
                    db=new database(c,c.getString(R.string.recover_media_table_name),query,2);
                    db.getWritableDatabase().execSQL("DELETE FROM "+c.getString(R.string.recover_media_table_name)+" WHERE 1");

                    // schedule message
                    query="CREATE TABLE IF NOT EXISTS scheduleMessage(_id INTEGER PRIMARY KEY autoincrement, Name text DEFAULT 'Not set',Message text,Number text,Date text,OpDate text,OpTime text,isDraft INTEGER ,TextCountryCode text DEFAULT '',SendThrough text DEFAULT 'com.whatsapp',State text DEFAULT '',ImageURIs text DEFAULT ''" +
                            ",VideoURIs text DEFAULT '',AudioURIs text DEFAULT '',DocURIs text DEFAULT '') ";
                    db=new database(c,c.getString(R.string.schiduleTableName),query,1);
                    db.getWritableDatabase().execSQL("DELETE FROM "+c.getString(R.string.schiduleTableName)+" WHERE 1");

                    db.close();

                //


                }catch (Exception e)
                {
                    Log.e("deleteAll",e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pd.dismiss();
                Toast.makeText(c, "Deleted successfully", Toast.LENGTH_SHORT).show();
            }
        }.execute();







    }

}
