package com.affixstudio.whatsapptool.modelOur;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBNewVersion extends SQLiteOpenHelper {


    Context c;
    String tableName;
    String query;
    public DBNewVersion(@Nullable Context context, @Nullable String DBname, @Nullable String query, int version,String tableName) {
        super(context, DBname, null, version);

        this.tableName=tableName;
        this.c=context;
        this.query=query;

    }

    Cursor getData(String query)
    {
        return this.getReadableDatabase().rawQuery(query
                , null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        try {
            sqLiteDatabase.execSQL(query);
        }catch (Exception e)
        {
            Log.e(this.toString(),e.getMessage());
        }



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
