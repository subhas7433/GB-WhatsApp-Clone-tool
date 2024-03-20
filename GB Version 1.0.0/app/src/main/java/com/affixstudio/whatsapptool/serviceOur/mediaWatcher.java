package com.affixstudio.whatsapptool.serviceOur;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.getData.NotifyUser;
import com.affixstudio.whatsapptool.modelOur.StatusSaverUtill;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.private_view_media_small;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class mediaWatcher extends Service {
    private String t="mediaWatcher";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {



        return null;
    }



    // mediaObserver md=new mediaObserver(android.os.Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp");
    contentObserver sticker,documents,images,video,audio,GIF;
    voiceObserver voiceNotes;
    @Override
    public void onCreate()
    {
        super.onCreate();
        //new version android.os.Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Voice Notes"
        File Voicef;
        String stickerPath,documentsPath,imagesPath,videoPath,audioPath,GifPath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Voicef  =new File(android.os.Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Voice Notes");
            stickerPath=android.os.Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Stickers";
            documentsPath=android.os.Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Documents";
            imagesPath=android.os.Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images";
            videoPath=android.os.Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Video";
            audioPath=android.os.Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Audio";
            GifPath=android.os.Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Animated Gifs";

        }
        else
        {
            Voicef  =new File(android.os.Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Voice Notes");
            stickerPath=android.os.Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Stickers";
            documentsPath=android.os.Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Documents";
            imagesPath=android.os.Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Images";
            videoPath=android.os.Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Video";
            audioPath=android.os.Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Audio";
            GifPath=android.os.Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Animated Gifs";
        }


        File[] files=Voicef.listFiles();
        File f3=new File(files[0].getAbsolutePath());

        int a=0;

        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.compare(f1.lastModified(), f2.lastModified());
            }
        });


//        int folderNumber=Integer.parseInt(f3.getAbsolutePath().replace(Voicef.getAbsolutePath()+"/",""))+1;
//
//
//
//
//        //was saving voice notes in the file
//        Log.i("event","is exist "+Voicef.getAbsolutePath()+"/"+folderNumber);
//        Log.i("event","IBinder");




        images=new contentObserver(imagesPath,getApplicationContext(),0);

        video=new contentObserver(videoPath,getApplicationContext(),1);
        audio=new contentObserver(audioPath,getApplicationContext(),2);
        documents=new contentObserver(documentsPath,getApplicationContext(),3);

        voiceNotes=new voiceObserver(files[files.length-1].getAbsolutePath(),getApplicationContext(),4);
        sticker=new contentObserver(stickerPath,getApplicationContext(),5);
        GIF=new contentObserver(GifPath,getApplicationContext(),6);

    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        voiceNotes.startWatching();
        sticker.startWatching();
        documents.startWatching();
        images.startWatching();
        video.startWatching();
        audio.startWatching();
        GIF.startWatching();

//        String s="CREATE TABLE IF NOT EXISTS "+getString(R.string.privateMediaNamesTable)+" (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER ) ";
//
//        database db=new database(getApplicationContext(),getString(R.string.privateMediaNamesTable),s,1);

        StatusSaverUtill ssU=new StatusSaverUtill(getApplicationContext());

//        Log.i(t,""+intent.getParcelableExtra(Intent.EXTRA_STREAM));
        File saveFileDir=ssU.makeMainDirectory(); // main directory


       getContentResolver().registerContentObserver(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true, new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);




                    }

                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        super.onChange(selfChange, uri);
                        try {
                        Log.i("event","IBinder "+uri);
                        Media media = readFromMediaStore(
                                getApplicationContext(),
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        File file = new File(media.file.getPath());
                        File uFile = new File(String.valueOf(uri));
                        Date fileData = new Date(file.lastModified());

                        if (false) {

                            Log.i("event","System.currentTimeMillis() - fileData.getTime() > 3000");

                        } else {

                            Log.i("mediaWatcher","saveInfolderAndNotifiy");
                            Log.i("event",uFile.getPath()+"   "+selfChange+"  file path  "+file.getPath());
                            if (file.getPath().contains("WhatsApp") && !file.getPath().contains("GBWhats"))
                            {
                                Log.i("mediaWatcher",file.getPath());


                                saveInfolderAndNotifiy(file,0,saveFileDir);
                                String saved = file.getName(); //#todo got last downloaded
                            }
                        }




                        }catch (Exception e)
                        {
                            Log.e("error",e.getMessage());
                        }
                    }
                });


        File saveAudioDir=ssU.makeAudioDirectory(); // main directory
        // audio
        getContentResolver().registerContentObserver(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                true, new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        Media media = readFromMediaStore(
                                getApplicationContext(),
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

                        File file = new File(media.file.getPath());
                        Date fileData = new Date(file.lastModified());

                        if (false) {
                            Log.i("event","System.currentTimeMillis() - fileData.getTime() > 3000");
                        } else
                        {

                            try {


                                Log.i("audioFilePath",file.getPath());
                            if (file.getPath().contains("WhatsApp") && !file.getPath().contains("GBWhats"))
                            {
                                saveInfolderAndNotifiy(file,2,saveAudioDir);


                            }
                            }catch (Exception e)
                            {
                                Log.e("mediaWatcher",e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        super.onChange(selfChange, uri);
                        Log.i(t,"boolean selfChange, Uri uri  "+uri);
                        Media media = readFromMediaStore(
                                getApplicationContext(),
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

                        File file = new File(media.file.getPath());
                        Date fileData = new Date(file.lastModified());
                        Log.i(t,"boolean selfChange, Uri uri  "+file.getPath());
                    }
                });


        File saveVideoDir=ssU.makeVideoDirectory(); // main directory
        // video
        getContentResolver().registerContentObserver(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                true, new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);






                    }

                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        super.onChange(selfChange, uri);
                        if (!uri.toString().contains("video"))
                        {
                            Log.i(t,MediaStore.Video.Media.EXTERNAL_CONTENT_URI+" file path boolean selfChange, Uri uri "+ uri);


                            return;
                        }
                        Media media = readFromMediaStore(getApplicationContext(),
                                uri);
                        Log.i(t,MediaStore.Video.Media.EXTERNAL_CONTENT_URI+" file path boolean selfChange, Uri uri "+ uri);



                        try {
                            File file=null;
                            if (media.file!=null)
                            {
                                Log.i(t," file path not null ");
                                file = new File(media.file.getPath());
                            }

                            Date fileData = new Date(file.lastModified());

                            Log.i("VideoFilePath",media.file.getPath()+"           "+file.getPath());

                            if (file.getPath().contains("WhatsApp"))
                            {
                                saveInfolderAndNotifiy(file,1,saveVideoDir);
                            }


                        }catch (Exception e)
                        {
                            Log.e("mediaWatcher",e.getMessage());
                        }

                    }
                });

        return super.onStartCommand(intent, flags, startId);

    }

    private void saveInfolderAndNotifiy(File file, int mType,File saveFileDir)
    {

        Log.i("mediaWatcher","saveInfolderAndNotifiy");
        try {

            String[] s={"Image","Video","Audio","Document","Voice" };

            String mediaType=s[mType];


            String query1="CREATE TABLE IF NOT EXISTS "+getString(R.string.allStoredMediaNamesTable)+" (_id INTEGER PRIMARY KEY autoincrement,MediaName text, MediaType INTEGER,time text) ";
            database db1=new database(getApplicationContext(),getString(R.string.allStoredMediaNamesTable),query1,1);



        File f=new File(saveFileDir+"/"+file.getName());

        SharedPreferences sp=getSharedPreferences("affix",MODE_PRIVATE);

            Log.i("mediaWatcher"," sp.getBoolean(getString(R.string.recoverMediaOnTag),false)  "+sp.getBoolean(getString(R.string.recoverMediaOnTag),false));

        if ( (((sp.getBoolean(getString(R.string.privateChatOnTag),false)) && (sp.getBoolean(getString(R.string.privateMediaOnTag),false))) // is switch on?
        || sp.getBoolean(getString(R.string.recoverMediaOnTag),false)))
        {

            Log.i("mediaWatcher"," privateChatOnTag should save in database");
            if (notDuplicate(file.getName(), db1)) // when ever deleting the file it saving again from whatsapp folder to  avoid that recording in database and checking
            {


                if (!f.exists())
                {


                    InputStream inputStream=getApplicationContext().getContentResolver().openInputStream(Uri.fromFile(file));
                    byte[] bytes= IOUtils.toByteArray(inputStream);
                    FileOutputStream out=new FileOutputStream(new File(saveFileDir+"/"+file.getName()));
                    out.write(bytes);

                    out.close();
                    recordInDatabase(db1,mType,file.getName()); // recording in the main names table


                    if (sp.getBoolean(getString(R.string.privateMediaOnTag),false) // is private media on?
                            && sp.getBoolean(getString(R.string.privateChatOnTag),false))
                    {
                        String query="CREATE TABLE IF NOT EXISTS "+getString(R.string.privateMediaNamesTable)+" (_id INTEGER PRIMARY KEY autoincrement,MediaName text, MediaType INTEGER,time text) ";
                        database db=new database(getApplicationContext(),getString(R.string.privateMediaNamesTable),query,1);
                        db.insertInMediaNames(mType,file.getName());

                        NotifyUser nu=new NotifyUser(getApplicationContext());
                        nu.UpdateUiOnNew(getApplicationContext().getString(R.string.updateUIbrodcustRecever));//sending broadcast to update ui


                        if (sp.getBoolean(getString(R.string.privateNotificationEnableTag),false)) // when notification is enabled
                        {
                            Intent intent = new Intent(getApplicationContext(), private_view_media_small.class); //#todo settting image uri for private chat


                            intent.putExtra("mediaType",mType);
                            intent.putExtra("isPrivateActivity",true);

                            nu.whenMediaUpdated(getString(R.string.mediaReceivedNotiMessage),"No Blue Tick",intent);



                        }
                    }








                }


            }
        }

        }catch (Exception e)
        {
            Log.i("mediaWatcher",e.getMessage());
        }

    }



    private void recordInDatabase(database db, int mType, String name) {

        try {



        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf=new SimpleDateFormat("h:mm a d/M/yy",Locale.getDefault());

        String date=sdf.format(new Date());


        ContentValues values=new ContentValues();

        values.put("MediaName",name);
        values.put("MediaType",mType);
        values.put("time",date);
        db.getWritableDatabase().insert(getString(R.string.allStoredMediaNamesTable),null,values);


        }catch (Exception e)
        {
            Log.e("mediaWatcher",e.getMessage());
        }

    }

    private boolean notDuplicate(String fileName,database db)
    {

        Cursor c=db.getinfo(1);


        while (c.moveToNext())
        {
            if (c.getString(1).equals(fileName))
            {
                Log.i("mediaWatcher","notDuplicate false");
                return false;
            }

        }

        Log.i("mediaWatcher","notDuplicate true");
        c.close();

        return true;
    }

    int i=1;
    private Media readFromMediaStore(Context context, Uri uri)
    {
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, "date_added DESC");
        Media media = null;
        if (cursor.moveToNext())

        {
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            String filePath = cursor.getString(dataColumn);
            int mimeTypeColumn = cursor
                    .getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);
            String mimeType = cursor.getString(mimeTypeColumn);
            media = new Media(new File(filePath), mimeType);
        }
        cursor.close();
        return media;
    }

    private class Media {
        private File file;

        private String type;

        public Media(File file, String type) {
            this.file = file;
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public File getFile() {
            return file;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        voiceNotes.stopWatching();
        sticker.stopWatching();
        documents.stopWatching();
        images.stopWatching();
        audio.stopWatching();
        video.stopWatching();
        GIF.stopWatching();

    }
}
