package com.affixstudio.whatsapptool.model;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.service.notification.StatusBarNotification;
import android.text.Editable;
import android.util.Log;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.model.preferences.PreferencesManager;
import com.affixstudio.whatsapptool.modelOur.database;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages user entered custom auto reply text data.
 */
public class CustomRepliesData {
    public static final String KEY_CUSTOM_REPLY_ALL = "user_custom_reply_all";
    public static final int MAX_NUM_CUSTOM_REPLY = 10;
    public static final int MAX_STR_LENGTH_CUSTOM_REPLY = 500;
    public static final String RTL_ALIGN_INVISIBLE_CHAR = " \u200F\u200F\u200E "; // https://android.stackexchange.com/a/190024
    private static final String APP_SHARED_PREFS = CustomRepliesData.class.getSimpleName();
    private static final String T = "customReplies";
    private static SharedPreferences _sharedPrefs;
    private static CustomRepliesData _INSTANCE;
    private final Context thisAppContext;
    private final PreferencesManager preferencesManager;
    StatusBarNotification sbn;
    int isTest=0; // when calling this class from testAuto


    public CustomRepliesData(Context context,StatusBarNotification sbn) {
        thisAppContext = context.getApplicationContext();
        _sharedPrefs = context.getApplicationContext()
                .getSharedPreferences(APP_SHARED_PREFS, MODE_PRIVATE);
        preferencesManager = PreferencesManager.getPreferencesInstance(thisAppContext);
        this.sbn=sbn;
        recevedMessage=sbn.getNotification().extras.getString("android.text");
        sender=sbn.getNotification().extras.getString("android.title");
        init();
    }
    private CustomRepliesData(Context context) {
        thisAppContext = context.getApplicationContext();
        _sharedPrefs = context.getApplicationContext()
                .getSharedPreferences(APP_SHARED_PREFS, MODE_PRIVATE);
        preferencesManager = PreferencesManager.getPreferencesInstance(thisAppContext);

        init();
    }

    public CustomRepliesData(Context thisAppContext, String recevedMessage, String sender) {
        preferencesManager = PreferencesManager.getPreferencesInstance(thisAppContext);
        this.thisAppContext = thisAppContext;
        this.recevedMessage = recevedMessage;
        this.sender = sender;
        isTest=1;
    }

    public  CustomRepliesData getInstance(Context context, StatusBarNotification sbn)
    {
        if (_INSTANCE == null) {
            _INSTANCE = new CustomRepliesData(context,sbn);
        }
        return _INSTANCE;
    }

    public static CustomRepliesData getInstance(Context context)
    {
        if (_INSTANCE == null) {
            _INSTANCE = new CustomRepliesData(context);
        }
        return _INSTANCE;
    }

    /**
     * Execute this code when the singleton is first created. All the tasks that needs to be done
     * when the instance is first created goes here. For example, set specific keys based on new install
     * or app upgrade, etc.
     */
    private void init() {
        // Set default auto reply message on first install
        if (!_sharedPrefs.contains(KEY_CUSTOM_REPLY_ALL)) {
            set(thisAppContext.getString(R.string.auto_reply_default_message));
        }
    }

    /**
     * Stores given auto reply text to the database and sets it as current
     *
     * @param customReply String that needs to be set as current auto reply
     * @return String that is stored in the database as current custom reply
     */
    public String set(String customReply) {
        if (!isValidCustomReply(customReply)) {
            return null;
        }
        JSONArray previousCustomReplies = getAll();
        previousCustomReplies.put(customReply);
        if (previousCustomReplies.length() > MAX_NUM_CUSTOM_REPLY) {
            previousCustomReplies.remove(0);
        }
        SharedPreferences.Editor editor = _sharedPrefs.edit();
        editor.putString(KEY_CUSTOM_REPLY_ALL, previousCustomReplies.toString());
        editor.apply();
        return customReply;
    }

    /**
     * Stores given auto reply text to the database and sets it as current
     *
     * @param customReply Editable that needs to be set as current auto reply
     * @return String that is stored in the database as current custom reply
     */
    public String set(Editable customReply) {
        return (customReply != null)
                ? set(customReply.toString())
                : null;
    }

    /**
     * Get last set auto reply text
     * Prefer using {@link CustomRepliesData::getOrElse} to avoid {@code null}
     *
     * @return Auto reply text or {@code null} if not set
     */
    public String get() {
        JSONArray allCustomReplies = getAll();
        try {
            return (allCustomReplies.length() > 0)
                    ? (String) allCustomReplies.get(allCustomReplies.length() - 1)
                    : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get last set auto reply text if present or else return {@param defaultText}
     *
     * @param defaultText default auto reply text
     * @return Return auto reply text if present or else return given {@param defaultText}
     */
    public String getOrElse(String defaultText) {
        String currentText = get();
        return (currentText != null)
                ? currentText
                : defaultText;
    }

    public String getTextToSendOrElse() {
        sp=thisAppContext.getSharedPreferences("affix",MODE_PRIVATE);
        String currentText = getOrElse(thisAppContext.getString(R.string.auto_reply_default_message));
        if (preferencesManager.isAppendWatomaticAttributionEnabled()) {
            currentText += "" + RTL_ALIGN_INVISIBLE_CHAR + thisAppContext.getString(R.string.sent_using_Watomatic);
        }

        if (isTest==1)
        {
            if (sp.getBoolean("specificGroupReplyOn",false)
                    || sp.getBoolean("specificReplyOn",false) )
            {

                Log.i(T,"specific on and specificGroupReplyOn "+sp.getBoolean("specificGroupReplyOn",false)
                        +" specificReplyOn = "+sp.getBoolean("specificReplyOn",false) );
                String reply=getReply();
                if (!reply.isEmpty())
                {
                    return reply;
                }

            }

        }else if (sbn!=null)
        {
            if (sp.getBoolean("specificGroupReplyOn",false)
            || sp.getBoolean("specificReplyOn",false) )
            {

                Log.i(T,"specific on and specificGroupReplyOn "+sp.getBoolean("specificGroupReplyOn",false)
                +" specificReplyOn = "+sp.getBoolean("specificReplyOn",false) );
                String reply=getReply();
                if (!reply.isEmpty())
                {
                    return reply;
                }

            }

            Log.i(T,"sbn!=null");
        }

        Log.i(T,currentText);
        return currentText; //#todo change this for custom replay
    }

    ArrayList<String> names=new ArrayList<>();
    ArrayList<String> message=new ArrayList<>();
    ArrayList<String> reply=new ArrayList<>();
    ArrayList<String> matchType=new ArrayList<>();
    ArrayList<String> customised=new ArrayList<>();
    ArrayList<Integer> isGroup=new ArrayList<>();

    String recevedMessage;
    String sender;
    SharedPreferences sp;


    private String getReply() {



         if (sender.contains(":"))
         {
             sender=sender.substring(0,sender.indexOf(":"));
         }




        String query="CREATE TABLE IF NOT EXISTS "+thisAppContext.getString(R.string.specificReply)+"(_id INTEGER PRIMARY KEY autoincrement, SenderName text DEFAULT 'Not set',Message text  DEFAULT 'Not set',Replay text  DEFAULT 'Not set',MatchType text,SendTo text DEFAULT 'com.whatsapp',isGroup text DEFAULT '1',Customised text DEFAULT 'true') ";/* Group =1 is not Group 2=yes*/
        database db=new database(thisAppContext,thisAppContext.getString(R.string.specificReply),query,1);


        if (names.size()>0)
        {
            names.clear();
            message.clear();
            reply.clear();
            matchType.clear();
            customised.clear();
            isGroup.clear();
        }




            Cursor c= db.getinfo(1);

            while (c.moveToNext())
            {
                names.add(c.getString(1));
                message.add(c.getString(2));
                reply.add(c.getString(3));
                matchType.add(c.getString(4));
                customised.add(c.getString(7));
                isGroup.add(c.getInt(6));
            }
            Log.i(T,sender+" name size "+names.size());


            if (names.contains(sender))
            {
                return specfic(); // getting specific reply
            }



        return "";
    }


    String specfic(){
        if (names.size()>0)
        {
            for (int i=0;i<names.size();i++)
            {


                if (sender.equals(names.get(i)))
                {
                    Log.i(T,sender+"; name  :"+names.get(i)+"");
                    if (isGroup.get(i)==2 && preferencesManager.isGroupReplyEnabled()
                    && sp.getBoolean("specificGroupReplyOn",false)) // is group and reply for group enable
                    {

                        Log.i(T,"isGroup.get(i)==2 && preferencesManager");
                        if (customised.get(i).equals("true"))
                        {
                             Log.i(T,"customised.get(i).equals()");
                            if (matchType.get(i).equals("exact"))
                            {
                                Log.i(T,sender+" matchType.contains(exact),recevedMessage "+recevedMessage +" dbMessage; "+message.get(i));
                                if (message.get(i).equalsIgnoreCase(recevedMessage))
                                {
                                    return reply.get(i);
                                }
                            }
                            else
                            {
                                Log.i(T,sender+" matchType.contains(contain),recevedMessage "+recevedMessage +" dbMessage; "+message.get(i)+" index ="+i);
                                if (recevedMessage.contains(message.get(i)))
                                {
                                    return reply.get(i);
                                }
                            }

                        }
                        else {
                            Log.i(T,"customised.get(i).equals() else");
                            return reply.get(i);
                        }
                    }
                    else if (isGroup.get(i)==1  && sp.getBoolean("specificReplyOn",false))
                    {
                        if (customised.get(i).equals("true"))
                        {
                            // Log.i(T,sender+" equals "+names.get(i));
                            if (matchType.get(i).equals("exact"))
                            {
                                Log.i(T,sender+" matchType.contains(exact),recevedMessage "+recevedMessage +" dbMessage; "+message.get(i));
                                if (message.get(i).equalsIgnoreCase(recevedMessage))
                                {
                                    return reply.get(i);
                                }
                            }
                            else
                            {
                                Log.i(T,sender+" matchType.contains(contain),recevedMessage "+recevedMessage +" dbMessage; "+message.get(i)+" index ="+i);
                                if (recevedMessage.contains(message.get(i)))
                                {
                                    return reply.get(i);
                                }
                            }

                        }
                        else {
                            return reply.get(i);
                        }
                    }
                    else {
                        return "";
                    }


                }

            }
        }
    return "";
    }

    private JSONArray getAll() {
        JSONArray allCustomReplies = new JSONArray();
        try {
            allCustomReplies = new JSONArray(_sharedPrefs.getString(KEY_CUSTOM_REPLY_ALL, "[]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allCustomReplies;
    }

    public static boolean isValidCustomReply(String userInput) {
        return (userInput != null) &&
                !userInput.isEmpty() &&
                (userInput.length() <= MAX_STR_LENGTH_CUSTOM_REPLY);
    }

    public static boolean isValidCustomReply(Editable userInput) {
        return (userInput != null) &&
                isValidCustomReply(userInput.toString());
    }
}
