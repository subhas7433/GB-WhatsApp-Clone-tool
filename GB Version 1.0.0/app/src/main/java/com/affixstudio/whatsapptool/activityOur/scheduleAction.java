package com.affixstudio.whatsapptool.activityOur;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.getData.NotifyUser;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.modelOur.scheduleOffBroadcast;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

public class scheduleAction extends AppCompatActivity {
    String t = "scheduleAction";
    CountDownTimer countDownTimer;
    int totalQueChat=0;
    @Override
    protected void onResume() {
        super.onResume();
        i("resume");






            if (lonched==1)
            {
                totalQueChat=sp.getInt(getString(R.string.totaQueChatNamesTAG),0); // getting total que when coming back from whatsapp
                if(totalQueChat==0)
                {

                    if (sp.getInt("isFailed",0)==1) // failed
                    {
                        i(" isFailed 1");
                        spedit.putBoolean("timeToOn", false);// this will permit accessibility to work in whatsapp to avoid unnecessary click
                        spedit.putBoolean("sendMedia", false);
                        spedit.putBoolean("globalBack", false);
                        spedit.putBoolean("sendTextStatus", false);
                        spedit.putBoolean("sendTextStatus2", false);
                        spedit.putBoolean("isUnknown", false);
                        spedit.putBoolean("sendSimpleText", false);
                        spedit.putInt("isFailed",0).apply();
                        NotifyUser notifyUser=new NotifyUser(this,"Schedule Status");
                        notifyUser.notifi("Failed","Scheduled task was failed.");
                        onBackPressed();
                    }else if (sp.getInt("isFailed",0)==2) // success
                    {
                        spedit.putInt("isFailed",0).apply();
                        onBackPressed();
                    }else if (sp.getInt("isFailed",0)==3) // when screen was locked
                    {
                        i(" isFailed 3");
                        spedit.putInt("isFailed",1).apply();

                    }


                }else {

                    mainFun();
                }
            }else {
                lonched=1;
            }




    }
    int lonched=0;// to avoid fake resume
    SharedPreferences sp;
    String packageFromDB="com.whatsapp";
    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor spedit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_action);

        sp = getSharedPreferences("affix", MODE_PRIVATE);
        spedit = sp.edit();

        mainFun();

    }

    ArrayList<Uri> uris;
    void mainFun()
    {
        try {


            TextView countDownT=findViewById(R.id.time);
            i("OnCreate");

            findViewById(R.id.cancelSchedule).setOnClickListener(view -> {
                countDownTimer.cancel();
                spedit.putInt("isFailed",1).apply();
                onResume();
            });
            if (getIntent()!=null)
            {




                Intent i = new Intent(Intent.ACTION_SEND);



                spedit.putBoolean("timeToOn", true);// this will permit accessibility to work in whatsapp to avoid unnecessary click
                spedit.putBoolean("sendMedia", false);
                spedit.putBoolean("globalBack", false);
                spedit.putBoolean("sendTextStatus", false);
                spedit.putBoolean("sendTextStatus2", false);
                spedit.putBoolean("isUnknown", false);
                spedit.putBoolean("sendSimpleText", false);
                spedit.putInt("isFailed", 1);// this parameter is to detect is the task was failed in this screen or in the whatsapp app
                //1= started the task, 0 = came to this screen from our app, 2 = the is success
                spedit.apply();


                i("statusSchedule "+getIntent().getIntExtra("type",0));

                if (getIntent().getIntExtra("type",0)==1)

                //when sending status
                {
                    spedit.putBoolean("statusSchedule", true).apply();

                }else
                {
                    spedit.putBoolean("statusSchedule", false).apply();
                }

                if (getIntent().getBooleanExtra("isTest",false))
                {
                    spedit.putBoolean("isTest", true).apply();

                }else {
                    spedit.putBoolean("isTest", false).apply();
                }



                spedit.putInt("id", getIntent().getIntExtra("id", 0));
                spedit.apply();


                

                if (getIntent().getBooleanExtra("isTest",false))
                {
                    if (!Objects.isNull(getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM))
                    && Objects.isNull(uris)) // when brought values and in test mode, not already set
                    {
                        uris=getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                    }

                }else if (Objects.isNull(uris)) // when not already set
                {
                    uris = getMedia(getIntent().getIntExtra("id", 0));
                }
                spedit.putString("name",getNames()).apply();
                totalQueChat=sp.getInt(getString(R.string.totaQueChatNamesTAG),0); // getting totalQue

                if (uris.size()>0) // todo show in full screen the medias
                //when sending media
                {


                    Log.i(t, "getIntent().getData()!=null    "+getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM));

                    //    ArrayList<Uri> uris = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);


//                    Log.i(t, "File exist "+f.exists());
//



                    i.setType("*/*"); // detecting data type

                    if (uris.size()>1)
                    {
                        Log.i(t, "Uri uri:uris");


                        ArrayList<Uri> uris1=new ArrayList<>();
                        for (Uri uri:uris)
                        {
                            Log.i(t, "Uri uri:uris");
                            uris1.add(FileProvider.getUriForFile(
                                    scheduleAction.this,
                                    "com.affixstudio.gbVersion.provider",
                                    new File(uri.getPath()))); // allowing other apps to get the file
                        }
                        i.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris1); // todo first time getting the old uri which is not from gb dir

                        //todo fix send problem of accessability in media send fun
                        i.setAction(Intent.ACTION_SEND_MULTIPLE);
                    } else
                    {

                        i.putExtra(Intent.EXTRA_STREAM,FileProvider.getUriForFile(
                                scheduleAction.this,
                                "com.affixstudio.gbVersion.provider",
                                new File(uris.get(0).getPath())));

                    }




                    if (getIntent().getStringExtra("EXTRA_TEXT") != null) {
                        Log.i(t, "getIntent().getStringExtra(Intent.EXTRA_TEXT)!=null");

                        //          char[] c=getIntent().getCharArrayExtra("EXTRA_TEXT");
                        i.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra("EXTRA_TEXT"));
                    }


                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setPackage(packageFromDB);

                    Log.i("packageSet", getIntent().getStringExtra("package"));

                    i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);



                    countDownTimer= new CountDownTimer(9000, 1000) {

                        @SuppressLint("SetTextI18n")
                        public void onTick(long millisUntilFinished) {
                            countDownT.setText("" + millisUntilFinished / 1000);
                            //here you can have your logic to set text to edittext
                        }

                        public void onFinish() {
                            setOffBroadCast(); // to prevent unnecessary touch
                            startActivity(i);
                        }

                    };
                    countDownTimer.start();




                    //Toast.makeText(this, "Schedule is started. Don't touch the screen.", Toast.LENGTH_LONG).show();


                }
                else
                {
                    Log.i(t, "isUnknown "+getIntent().getBooleanExtra("isUnknown",false));


                    if (getIntent().getBooleanExtra("isUnknown",false))
                    {
                        spedit.putBoolean("isUnknown", true).putBoolean("timeToOn", false).apply();

                        String phoneNumberWithCountryCode = getIntent().getStringExtra("number");
                        String message = getIntent().getStringExtra("message");


                        countDownTimer= new CountDownTimer(9000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                countDownT.setText("" + millisUntilFinished / 1000);
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                setOffBroadCast(); // to prevent unnecessary touch
                                startActivity(
                                        new Intent(Intent.ACTION_VIEW,
                                                Uri.parse(
                                                        String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message)
                                                )
                                        ).setPackage(packageFromDB)
                                );
                            }

                        };
                        countDownTimer.start();


                    }
                    else
                    {
                        i("only text");
                        spedit.putBoolean("isUnknown", false).apply();

                        spedit.putBoolean("sendSimpleText", true); // when sending only text
                        spedit.putBoolean("simpleTextWA", false); // when simple text is sending this command to start selectchat() first in accessibility
                        spedit.putBoolean("sendMedia",false).apply();
                        Intent in=new Intent(Intent.ACTION_SEND);
                        //in.setType("*/*");
                        //in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        in.setType("text/plain");

                        in.setAction(Intent.ACTION_SEND);
                        in.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra("EXTRA_TEXT") );

                        in.setPackage(packageFromDB);
                        Log.i(t, "EXTRA_TEXT "+getIntent().getStringExtra("EXTRA_TEXT"));





                        countDownTimer= new CountDownTimer(9000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                countDownT.setText("" + millisUntilFinished / 1000);
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                setOffBroadCast(); // to prevent unnecessary touch
                                startActivity(in);
                            }

                        };
                        countDownTimer.start();





                        // todo check all the schedule fun is it working well
                        // todo cancel the current schedule

                    }


                }





            }


        }catch (Exception e)
        {
            Log.e("scheduleAction",e.getMessage());
        }
    }

    private String getNames() {
        StringBuilder name=new StringBuilder();

        for (int i=totalQueChat;i>0;i--)
        {

            if ((totalQueChat-1)==5) // just taking 5 names from the list
            {
                name.append(names[i-1]+",");
            }else {
                return name.toString();
            }

        }

        return name.toString();
    }

    String[] names=new String[]{};
    private ArrayList<Uri> getMedia(int id) throws URISyntaxException {

        ArrayList<Uri> uris=new ArrayList<>();
        String query="CREATE TABLE IF NOT EXISTS scheduleMessage(_id INTEGER PRIMARY KEY autoincrement, Name text DEFAULT 'Not set',Message text,Number text,Date text,OpDate text,OpTime text,isDraft INTEGER ,TextCountryCode text DEFAULT '',SendThrough text DEFAULT 'com.whatsapp',State text DEFAULT '',ImageURIs text DEFAULT ''" +
                ",VideoURIs text DEFAULT '',AudioURIs text DEFAULT '',DocURIs text DEFAULT '') ";

        database db=new database(this,getString(R.string.schiduleTableName),query,1);

        Cursor cursor=db.getinfo(1);
        while (cursor.moveToNext())
        {
            if (cursor.getInt(0)==id)
            {

                packageFromDB=cursor.getString(9);

                setNames(cursor); // get names in val from database

                ArrayList<Uri> uris1=addUri(11,cursor);
                if (uris1.size()>0)
                {
                    uris.addAll(uris1);
                }
                uris1=addUri(12,cursor);
                if (uris1.size()>0)
                {
                    uris.addAll(uris1);
                }
                uris1=addUri(13,cursor);
                if (uris1.size()>0)
                {
                    uris.addAll(uris1);
                }
                uris1=addUri(14,cursor);
                if (uris1.size()>0)
                {
                    uris.addAll(uris1);
                }


                break;



            }
//                imagesUri.add(cursor.getString(11));
//            videoUri.add(cursor.getString(12));
//            audioUri.add(cursor.getString(13));
//            docUri.add(cursor.getString(14));
        }

        return uris;

    }

    private void setNames(Cursor cursor) {

        names=cursor.getString(1).split(",");

        totalQueChat=names.length;
        spedit.putInt(getString(R.string.totaQueChatNamesTAG),names.length).apply(); // set all names as Que


    }

    ArrayList<Uri> addUri(int i,Cursor cursor) throws URISyntaxException {

        ArrayList<Uri> uris=new ArrayList<>();
        if (!cursor.getString(i).equals(""))
        {
            String[] strings =cursor.getString(i).split("\\*");
            for (String s:strings)
            {

                File file=new File(new URI(s));
                i("getMediaDataFromDatabase image file exist"+file.exists()+" "+s);

                if (file.exists())
                {

                    uris.add(Uri.parse(s));
                }

            }
        }


        return uris;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void i(String s)
    {
        Log.i(t,s);
    }
    private void setOffBroadCast() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, scheduleOffBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,12121 , intent, 0|PendingIntent.FLAG_IMMUTABLE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + 120000, pendingIntent); // setting off accessibility after 2 min



    }
}