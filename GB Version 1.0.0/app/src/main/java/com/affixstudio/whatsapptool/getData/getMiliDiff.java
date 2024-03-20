package com.affixstudio.whatsapptool.getData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.modelOur.database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class getMiliDiff {




   public long finalMilis(String dateTV,String time)
   {


        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sTimef = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        Date timeD = null;
        Date simpleDate = null;
        Date simpleTime = null;
        try {
            date = sdf.parse(dateTV);
            timeD = sTimef.parse(time);
            simpleDate = sdf.parse(sdf.format(new Date()));
            simpleTime = sTimef.parse(sTimef.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long DateMillis = date.getTime();
        long timeMillis = timeD.getTime();

        long simpleSumMili = simpleDate.getTime() + simpleTime.getTime();
        long sumMinils = DateMillis + timeMillis;


        return sumMinils - simpleSumMili - 1000;
    }


    public String getDateForMilis(long milis)
    {


        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("h:mm a d/M/yy");




        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milis);
        return formatter.format(calendar.getTime());

    }
    public boolean isRecordedBeforeFile(Long fileMilis,String recordDate)
    {

        long recordMilis=0;
        try {


            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy");

            Date recordD = null;
            recordD = sdf.parse(recordDate);

            recordMilis = Objects.requireNonNull(recordD).getTime();

            Log.i("getMiliDiff","recordMilis = "+recordMilis+", fileMilis = "+fileMilis+"| recordMilis < fileMilis "+(recordMilis < fileMilis));

        }
        catch (Exception e)
        {
            Log.e("getMiliDiff",e.getMessage());
            return false;
        }

        return (fileMilis>recordMilis) || (fileMilis==recordMilis);
    }

    public String getRecoverdDate(Context c,String fileName)
    {
        String query="CREATE TABLE IF NOT EXISTS recoverMedia (_id INTEGER PRIMARY KEY autoincrement,MediaName text, MediaType INTEGER,time text) ";
        database db = new database(c, c.getString(R.string.recover_media_table_name),query,1);

        Cursor cursor=db.getinfo(1);
        while (cursor.moveToNext())
        {
            if (cursor.getString(1).equals(fileName))
            {
               return cursor.getString(3);
            }
        }

        return "No Date Detected";
    }
}
