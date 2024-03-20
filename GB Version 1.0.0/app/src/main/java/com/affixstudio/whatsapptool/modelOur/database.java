package com.affixstudio.whatsapptool.modelOur;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.affixstudio.whatsapptool.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class database extends SQLiteOpenHelper {
    Context con;
    String databaseName;
    String query;


    ArrayList<String> id=new ArrayList<>();//from message recover
    ArrayList<String> RecevedFrom=new ArrayList<>();//from message recover
    ArrayList<String> Message=new ArrayList<>();//from message recover
    ArrayList<String> time=new ArrayList<>();//from message recover
    ArrayList<Integer> isdeleted=new ArrayList<>();//from message recover
     ArrayList<Integer> unread=new ArrayList<>();

    ArrayList<String> QKtitle=new ArrayList<>();//from Quick message
    ArrayList<Integer> QKid=new ArrayList<>();//from Quick message
    ArrayList<String> QKmessage=new ArrayList<>();//from Quick message
    ArrayList<String> QKlanguage=new ArrayList<>();//from Quick message


    public ArrayList<String> getQKlanguage() {
        return QKlanguage;
    }

    public void setQKlanguage(ArrayList<String> QKlanguage) {
        this.QKlanguage = QKlanguage;
    }

    public database(@Nullable Context context, @Nullable String name, String query, int version) {
        super(context, name,null,version);
        this.con=context;
        this.databaseName=name;
        this.query=query;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //String query="CREATE TABLE IF NOT EXISTS whatsappMessages (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER ) ";
        try
        {
            //String query="CREATE TABLE IF NOT EXISTS quickMessage (_id INTEGER PRIMARY KEY autoincrement,Title text , Message text)";
            sqLiteDatabase.execSQL(query);
            //Toast.makeText(con, "created", Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            Log.e("Table Failed",""+e);
        }



        // Toast.makeText(con, "created", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }















    public ArrayList<String> getQKtitle() {
        return QKtitle;
    }

    public void setQKtitle(ArrayList<String> QKtitle) {
        this.QKtitle = QKtitle;
    }

    public ArrayList<Integer> getQKid() {
        return QKid;
    }

    public void setQKid(ArrayList<Integer> QKid) {
        this.QKid = QKid;
    }

    public ArrayList<String> getQKmessage() {
        return QKmessage;
    }

    public void setQKmessage(ArrayList<String> QKmessage) {
        this.QKmessage = QKmessage;
    }

    public Context getCon() {
        return con;
    }

    public ArrayList<Integer> getIsdeleted() {
        return isdeleted;
    }
    public ArrayList<Integer> getUnread() {
        return unread;
    }

    public void setIsdeleted(ArrayList<Integer> isdeleted) {
        this.isdeleted = isdeleted;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    public ArrayList<String> getId() {
        return id;
    } //from message recover

    public ArrayList<String> getRecevedFrom() {
        return RecevedFrom;
    } //from message recover

    public void setId(ArrayList<String> id) {
        this.id = id;
    }//from message recover

    public void setRecevedFrom(ArrayList<String> recevedFrom) {
        RecevedFrom = recevedFrom;
    }//from message recover

    public void setMessage(ArrayList<String> message) {
        Message = message;
    }//from message recover

    public void setTime(ArrayList<String> time) {
        this.time = time;
    }//from message recover

    public ArrayList<String> getMessage() {
        return Message;
    }//from message recover

    public ArrayList<String> getTime() {
        return time;
    }//from message recover



    public boolean insertData(String RecevedFrom,String Message,String time,String tableName)
    {
        SQLiteDatabase sl=this.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("RecevedFrom",RecevedFrom);
        contentValues.put("Message",Message);
        contentValues.put("time",time);

        long r=sl.insert(tableName,null,contentValues);

        return r != -1;


    }

    public boolean insertInMediaNames(int mType, String name)
    {

        try {



            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf=new SimpleDateFormat("h:mm a d/M/yy", Locale.getDefault());

            String date=sdf.format(new Date());


            ContentValues values=new ContentValues();

            values.put("MediaName",name);
            values.put("MediaType",mType);
            values.put("time",date);
            getWritableDatabase().insert(con.getString(R.string.privateMediaNamesTable),null,values);

            return true;


        }catch (Exception e)
        {
            Log.e("mediaWatcher",e.getMessage());
            return false;
        }

    }

    public boolean insertDeletedMediaData(String MediaName,int MediaType,String time,String tableName)
    {
        SQLiteDatabase sl=this.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("MediaName",MediaName);
        contentValues.put("MediaType",MediaType);
        contentValues.put("time",time);

        long r=sl.insert(tableName,null,contentValues);

        return r != -1;


    }

    public boolean insertData2(String title,String Message,String tableName,String language)
    {
        long r=-1;
        try {


        SQLiteDatabase sl=this.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        if (!title.isEmpty())
        {
            contentValues.put("Title",title);
        }

        contentValues.put("Message",Message);
        contentValues.put("Language",language);


         r =sl.insert(tableName,null,contentValues);
        }catch (Exception e)
        {
            Log.e("insert failed",""+e);
        }
        return r != -1;


    }
    public Cursor getinfo(int search)
    {
        Cursor cursor=null;
        try{



             cursor = this.getReadableDatabase().rawQuery("Select * from " + databaseName/*table and database name is same*/
                    , null);

        }catch (Exception e)
        {
            Log.i("Database"," "+e.getMessage());
         //   Toast.makeText(con, "From database--  "+e, Toast.LENGTH_SHORT).show();
        }
        /*if (search==1) creating problem with returning null cursor
        {
            checkData(cursor);
        }*/
        ;
        return cursor;

    }
    public void updateColumn(String columnNameAndValue,String condition)
    {

        try (SQLiteDatabase SqLiteDatabase =this.getWritableDatabase()) {
            String query = "UPDATE " + databaseName +
                    " SET " + columnNameAndValue + condition;
            SqLiteDatabase.execSQL(query);
        }
    }

    public void checkData(Cursor cursor) //from message recover
    {
        ArrayList<String> id=new ArrayList<>();
        ArrayList<String> RecevedFrom=new ArrayList<>();
        ArrayList<String> Message=new ArrayList<>();
        ArrayList<String> time=new ArrayList<>();
        ArrayList<Integer> isdeleted=new ArrayList<>();
        ArrayList<Integer> unread=new ArrayList<>();
        while (cursor.moveToNext())
        {
            id.add(cursor.getString(0));
            RecevedFrom.add(cursor.getString(1));
            Message.add(cursor.getString(2));
            time.add(cursor.getString(3));
            isdeleted.add(cursor.getInt(4));
            unread.add(cursor.getInt(5));

        }

        setId(id);
        setRecevedFrom(RecevedFrom);
        setMessage(Message);
        setTime(time);
        setIsdeleted(isdeleted);
        setUnread(unread);
    }

    private void setUnread(ArrayList<Integer> unread) {
        this.unread=unread;
    }


    public  void setdata(String lang)
    {
        Cursor cursor=null;
        try{



            if (lang.equals("Mixed"))
            {
                cursor = this.getWritableDatabase().rawQuery("Select * from  " + databaseName/*table and database name is same*/
                        , null);
            }else {
                cursor = this.getWritableDatabase().rawQuery("Select * from  " + databaseName+" where Language='"+lang+"'"/*table and database name is same*/
                        , null);
            }




            ArrayList<Integer> QKid=new ArrayList<>();
            ArrayList<String> QKmessage=new ArrayList<>();
            ArrayList<String> QKtitle=new ArrayList<>();
            ArrayList<String> QKlang=new ArrayList<>();

            while (cursor.moveToNext())
            {
                QKid.add(cursor.getInt(0));
                QKtitle.add(cursor.getString(1));
                QKmessage.add(cursor.getString(2));
                QKlang.add(cursor.getString(3));

            }

            setQKid(QKid);
            setQKtitle(QKtitle);
            setQKmessage(QKmessage);
            setQKlanguage(QKlang);

        }catch (Exception e)
        {
            Log.e("setData",""+e);
        }
    }


    public void delete()
    {

    }
}
