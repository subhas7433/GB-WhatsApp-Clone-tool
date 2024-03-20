package com.affixstudio.whatsapptool.model.logs;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.affixstudio.whatsapptool.model.utils.Constants;

@Database(entities = {MessageLog.class, AppPackage.class}, version = 2)
public abstract class MessageLogsDB extends RoomDatabase {
    private static final String DB_NAME = Constants.LOGS_DB_NAME;
    private static MessageLogsDB _instance;

    public static synchronized MessageLogsDB getInstance(Context context) {
        if (_instance == null) {
            _instance = Room.databaseBuilder(context.getApplicationContext(), MessageLogsDB.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return _instance;
    }

    public abstract MessageLogsDao logsDao();

    public abstract AppPackageDao appPackageDao();
}
