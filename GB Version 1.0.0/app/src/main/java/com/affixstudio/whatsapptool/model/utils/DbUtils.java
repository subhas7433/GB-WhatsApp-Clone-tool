package com.affixstudio.whatsapptool.model.utils;

import android.content.Context;
import android.service.notification.StatusBarNotification;

import com.affixstudio.whatsapptool.model.CustomRepliesData;
import com.affixstudio.whatsapptool.model.logs.AppPackage;
import com.affixstudio.whatsapptool.model.logs.MessageLog;
import com.affixstudio.whatsapptool.model.logs.MessageLogsDB;

public class DbUtils {
    private final Context mContext;

    public DbUtils(Context context) {
        mContext = context;
    }

    public long getNunReplies() {
        MessageLogsDB messageLogsDB = MessageLogsDB.getInstance(mContext.getApplicationContext());
        return messageLogsDB.logsDao().getNumReplies();
    }

    public void purgeMessageLogs() {
        MessageLogsDB messageLogsDB = MessageLogsDB.getInstance(mContext.getApplicationContext());
        messageLogsDB.logsDao().purgeMessageLogs();
    }

    public void logReply(StatusBarNotification sbn, String title) {
        CustomRepliesData customRepliesData =new CustomRepliesData(mContext, sbn);
        MessageLogsDB messageLogsDB = MessageLogsDB.getInstance(mContext.getApplicationContext());
        int packageIndex = messageLogsDB.appPackageDao().getPackageIndex(sbn.getPackageName());
        if (packageIndex <= 0) {
            AppPackage appPackage = new AppPackage(sbn.getPackageName());
            messageLogsDB.appPackageDao().insertAppPackage(appPackage);
            packageIndex = messageLogsDB.appPackageDao().getPackageIndex(sbn.getPackageName());
        }
        MessageLog logs = new MessageLog(packageIndex, title, sbn.getNotification().when, customRepliesData.getTextToSendOrElse(), System.currentTimeMillis());
        messageLogsDB.logsDao().logReply(logs);
    }

    public long getLastRepliedTime(String packageName, String title) {
        MessageLogsDB messageLogsDB = MessageLogsDB.getInstance(mContext.getApplicationContext());
        return messageLogsDB.logsDao().getLastReplyTimeStamp(title, packageName);
    }

    public long getFirstRepliedTime() {
        MessageLogsDB messageLogsDB = MessageLogsDB.getInstance(mContext.getApplicationContext());
        return messageLogsDB.logsDao().getFirstRepliedTime();
    }
}
