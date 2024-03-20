package com.affixstudio.whatsapptool.getData;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.modelOur.database;

public class getQuickMessage {

    Context context;
    Activity mActivity;

    public getQuickMessage(Context context, Activity mActivity) {
        this.context = context;
        this.mActivity = mActivity;
    }

    public String[] allMessage(){

        String [] messageList=mActivity.getResources().getStringArray(R.array.QuickMessage);

        String[] savedMessageList=getSaveMessage();
        String[] allMessage=new String[messageList.length+savedMessageList.length];


        System.arraycopy(savedMessageList,0,allMessage,0,savedMessageList.length);
        System.arraycopy(messageList,0,allMessage,savedMessageList.length,messageList.length);



        return allMessage;
    }
    String[] getSaveMessage() {
        String query="CREATE TABLE IF NOT EXISTS quickMessage(_id INTEGER PRIMARY KEY autoincrement,Title text , Message text,Language text)";
        database db=new database(context,context.getString(R.string.saved_quick_message),query,2);
        db.setdata("Mixed");
        String[] array=new String[db.getQKmessage().size()];

        Log.i("quickmessage","savemessage size "+db.getQKmessage().size());
        // Toast.makeText(getContext(), ""+db.getQKmessage().size(), Toast.LENGTH_SHORT).show();
        for (int i=0;i<db.getQKmessage().size();i++)
        {

            array[i]=db.getQKmessage().get(i);
        }
        return  array;
    }

}
