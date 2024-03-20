package com.affixstudio.whatsapptool.modelOur;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.getData.NotifyUser;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class whatsappAccessibility extends  AccessibilityService {


    SharedPreferences sp;
    SharedPreferences.Editor se;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override

    public void onAccessibilityEvent(AccessibilityEvent event) {



        try {

            sp = getApplicationContext().getSharedPreferences("affix", MODE_PRIVATE);
            se=sp.edit();

            if (Objects.isNull(getRootInActiveWindow())) {

                return;
            }

            AccessibilityNodeInfo currentNode=getRootInActiveWindow();

            // Log.i("accebilityEvent"," Event "+event.getClassName());







            if (sp.getBoolean("gettingChatName",false))
            {
                if (event.getClassName().equals("com.whatsapp.Conversation"));
                {
                    for (int i=0;i<currentNode.getChildCount();i++)
                    {
                        if (currentNode.getChild(i).getClassName().toString().equals("android.widget.LinearLayout"))
                        {
                            if (!Objects.isNull(currentNode.getChild(i).getChild(0)))
                            {
                                se.putString("pickedChatName",currentNode.getChild(i).getChild(0).getText().toString())
                                        .putBoolean("gettingChatName",false).apply();
                                Thread.sleep(500);
                                Toast.makeText(getApplicationContext(), "Going back...", Toast.LENGTH_SHORT).show();
                                performGlobalAction(GLOBAL_ACTION_BACK);
                                Thread.sleep(500);
                                performGlobalAction(GLOBAL_ACTION_BACK);


                            }
                        }
                    }

                }
            }

//               i("nodeInfoCompat.getClassName()  "+event.getClassName() +" \n\n\n");
//                for (int i=0;i<currentNode.getChildCount();i++)
//                {
//
//                    Log.i("accebility name",i+" Component type "+currentNode.getChild(i).getClassName().toString());
//
//
//                    if (currentNode.getChild(i).getActionList()!=null)
//                    {
//                        Log.i("accebility child","Parent index "+i+" child count of "+currentNode.getChild(i).getClassName().toString()+"   : "+currentNode.getChild(i).getChildCount());
//                        if (currentNode.getChild(i)!=null)
//                        {
//
//                            for (int k=0;k<currentNode.getChild(i).getChildCount();k++)
//                            {
//
//                                Thread.sleep(10);
//                                Log.i("accebility child",i+" is child with child count "+currentNode.getChild(i).getChildCount());
//                                if (currentNode.getChild(i).getChild(k)!=null)
//                                {
//                                    Log.i("accebility.getChild(i)"," currentNode.getChild("+i+").getChild("+k+") text is  : "+currentNode.getChild(i).getChild(k).getText());
////                                    for (int j=0;j<currentNode.getChild(i).getChild(0).getActionList().size();j++)
////                                    {
////                                        Log.i("accebility id",j+" "+currentNode.getChild(i).getChild(0).getActionList().get(j).toString());
////
////                                    }
//                                }
//
//
//
//
//                            }
//
//
//                        }
//
//
//                    }
//
//                    Thread.sleep(10);
//                }



            AccessibilityNodeInfoCompat nodeInfoCompat = AccessibilityNodeInfoCompat.wrap(currentNode);

            // Log.i("whatsappAccessibility","className "+nodeInfoCompat.getCollectionItemInfo().toString()) ;

            if (Objects.isNull(nodeInfoCompat))
            {
                return;
            }
            else {
                i("nodeInfoCompat "+nodeInfoCompat.getChildCount());
            }

           // if (nodeInfoCompat.getPackageName()!=null)
            if (true)
            {
                if (sp.getBoolean("isUnknown",false))
                {
                    simpleTextWA(nodeInfoCompat.getPackageName().toString());
                }

               i("nodeInfoCompat.getClassName()  "+event.getClassName() +" \n\n\n");

                showEventInfo(currentNode);// development purpose only




                if (sp.getBoolean("timeToOn", false))
                {

                    Log.d("Accessibility", "sendMedia " +sp.getBoolean("sendMedia",false));
                    Log.d("Accessibility", "globalBack " +sp.getBoolean("globalBack",false));
                    Log.d("Accessibility", "statusSchedule " +sp.getBoolean("statusSchedule",false)+"\n\n\n");
                    if (nodeInfoCompat.getPackageName().toString().equals("com.whatsapp.w4b"))
                    {


                        if (sp.getBoolean("sendTextStatus", false))
                        {
                            i("sp.getBoolean(sendTextStatus,false) WB");


                            sendTextStatusWB();

                        }
                        else if (sp.getBoolean("sendTextStatus2", false))
                        {

                            sendTextStatusWB2();
                            SharedPreferences.Editor se = sp.edit();
                            if (sendTextStatusWB2Help > 3) {
                                se.putBoolean("sendTextStatus2", false).putBoolean("timeToOn", false).apply();
                                Thread.sleep(500);
                                performGlobalAction(GLOBAL_ACTION_HOME);
                                sucess("Status Uploaded","Your Scheduled status has been uploaded successfully");

                                sendTextStatusWB2Help = 0;
                            }


                        }
                        else if (sp.getBoolean("statusSchedule", false))
                        {
                            i("sp.getBoolean(statusSchedule,false) WB");
                            statusSelectWB(nodeInfoCompat.getPackageName().toString());

                        }
                        else if (sp.getBoolean("sendMedia",false))
                        {

                            i("sp.getBoolean(sendMedia,false)");

                            sendMediaWB(nodeInfoCompat.getPackageName().toString());

                        }
                        else if (sp.getBoolean("globalBack",false))
                        {

                            i("sp.getBoolean(\"globalBack\",false)");
                            gobackWB();
                        }
                        else
                        {
                            i("else");
                            selectChatWB(nodeInfoCompat.getPackageName().toString());
                        }
                    }
                    else
                    {

                        if (sp.getBoolean("sendTextStatus",false))
                        {
                            i("sp.getBoolean(sendTextStatus,false)");
                            //sendTextStatus();
                            sendTextStatusW();

                        }else if (sp.getBoolean("sendTextStatus2",false))
                        {

                            i("sendTextStatus2 ");
                            sendTextStatus2();
//                            SharedPreferences.Editor se= sp.edit();
//                            se.putBoolean("sendTextStatus2",false).apply();
//                            i=0;


                        }else if (sp.getBoolean("statusSchedule",false))
                        {
                            i("sp.getBoolean(statusSchedule,false)");
                            statusSelect(nodeInfoCompat.getPackageName().toString());
                            i=0;

                        }else if (sp.getBoolean("sendMedia",false))
                        {

                            i("sp.getBoolean(sendMedia,false)");

                            //sendMedia(nodeInfoCompat.getPackageName().toString());
                            sendMediaW();

                        }else if (sp.getBoolean("globalBack",false))
                        {

                            i("sp.getBoolean(\"globalBack\",false)");
                            goBack();
                        }
                        else
                        {
                            i("else");
                            selectChat(nodeInfoCompat.getPackageName().toString());
                        }
                    }

                }
            }

















        }catch(Exception e)
        {
            Log.e("WhatsappAccessibility", " MAIN  " + e);
        }


    }

    private void showEventInfo(AccessibilityNodeInfo currentNode)
    {
        //                for (int i=0;i<currentNode.getChildCount();i++)
//                {
//                    Log.i("accebility name",i+" getClassName "+currentNode.getContentDescription());
//                    Log.i("accebility name",i+" getClassName "+currentNode.getChild(i).getClassName().toString());
//
//
//                    if (currentNode.getChild(i).getActionList()!=null)
//                    {
//                        Log.i("accebility child",i+" "+currentNode.getChild(i).getChildCount());
//                        if (currentNode.getChild(i)!=null)
//                        {
//
//                            for (int k=0;k<currentNode.getChild(i).getChildCount();k++)
//                            {
//
//                                Thread.sleep(10);
//                                Log.i("accebility child",i+" "+currentNode.getChild(i).getChildCount());
//                                if (currentNode.getChild(i).getChild(k)!=null)
//                                {
//                                    Log.i("accebility.getChild(i)",k+" currentNode.getChild(i).getChild(k) "+currentNode.getChild(i).getChild(k).getText());
//                                    for (int j=0;j<currentNode.getChild(i).getChild(0).getActionList().size();j++)
//                                    {
//                                        Log.i("accebility id",j+" "+currentNode.getChild(i).getChild(0).getActionList().get(j).toString());
//
//                                    }
//                                }
//
//
//
//
//                            }
//
//
//                        }
//
//
//                    }
//
//                    Thread.sleep(10);
//                }
    }

    private void statusSelect(String packageName)  {
        try {


        if (Objects.isNull(getRootInActiveWindow())) {

            return;
        }

        i("status select");
        AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());




        Thread.sleep(200);
        List<AccessibilityNodeInfoCompat> contectPickerNode=nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName+":id/contactpicker_row_name");
        //  Log.d("accessibility","contectPickerNode "+contectPickerNode);
        if (!Objects.isNull(contectPickerNode) && !contectPickerNode.isEmpty())
        {

            AccessibilityNodeInfoCompat contectPicker=contectPickerNode.get(0);

            // Log.d("accessibility","contectPicker "+contectPicker);
            if (!Objects.isNull(contectPicker))
            {
                Log.d("accessibility","select contectPicker.getParent().isCheckable() "+contectPicker.getParent().isCheckable());

                if (contectPicker.getParent().isClickable())
                {
                    Log.d("accessibility","contectPicker.getParent()");
                    contectPicker.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    SharedPreferences.Editor se= sp.edit();
                    se.putBoolean("sendTextStatus",true).putBoolean("statusSchedule",false).apply();
                    Log.d("accessibility","statusSelect    putBoolean(statusSchedule,false)");

                }


            }

        }
        }catch (Exception e)
        {
            Log.e("accessibility","statusSelect "+e);
        }

    }

    private void statusSelectWB(String packageName) throws InterruptedException {
        if (Objects.isNull(getRootInActiveWindow())) {

            return;
        }

        i("statusSelectWB name ");
        i("status select");
        AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());




        Thread.sleep(200);
        List<AccessibilityNodeInfoCompat> contectPickerNode=nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName+":id/contactpicker_row_name");
        //  Log.d("accessibility","contectPickerNode "+contectPickerNode);
        if (!Objects.isNull(contectPickerNode) && !contectPickerNode.isEmpty())
        {

            AccessibilityNodeInfoCompat contectPicker=contectPickerNode.get(0);

            Log.d("accessibility","contectPicker "+contectPicker);


            if (!Objects.isNull(contectPicker))
            {
                Log.d("accessibility","select contectPicker.getParent().isCheckable() "+contectPicker.getParent().isCheckable());
                Log.d("accessibility","select contectPicker.getParent().isClickable() "+contectPicker.getParent().isClickable());
                if (contectPicker.getParent().isClickable())
                {
                    Log.d("accessibility","contectPicker.getParent()");
                    contectPicker.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    SharedPreferences.Editor se= sp.edit();
                    se.putBoolean("sendTextStatus",true).putBoolean("statusSchedule",false).apply();
                }


            }
        }








    }
    void sendTextStatusWB()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {


                if (Objects.isNull(getRootInActiveWindow())) {

                    return;
                }

                AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());
                if (Objects.isNull(nodeInfoCompat))
                {
                    i("nodeInfoCompat==null");
                    return;
                }
                i("nodeInfoCompat.getChildCount() "+nodeInfoCompat.getChildCount());
                if (nodeInfoCompat.getChildCount()>4)
                {
                    i("nodeInfoCompat.getChild(4) "+nodeInfoCompat.getChild(5).getClassName());
                    if (!Objects.isNull(nodeInfoCompat.getChild(5)))
                    {
                        if (nodeInfoCompat.getChild(5).getClassName().equals("android.widget.ImageButton"))
                        {

                            Log.d("accessibility","nodeInfoCompat.getChild(4).getClassName().equals(android.widget.ImageButton)");
                            if (nodeInfoCompat.getChild(5).isVisibleToUser())
                            {
                                Log.d("accessibility","nodeInfoCompat.getChild(4).isVisibleToUser()");

                                nodeInfoCompat.getChild(5).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                SharedPreferences.Editor se= sp.edit();
                                se.putBoolean("sendTextStatus2",true)
                                        .putBoolean("sendTextStatus",false)
                                        .apply();

                            }
                        }
                    }
                }

            }
        }).start();
    }
    void sendTextStatusW() //for test
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                i("sendTextStatusW");

                if (Objects.isNull(getRootInActiveWindow())) {

                    return;
                }

                AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());
                if (Objects.isNull(nodeInfoCompat))
                {
                    i("nodeInfoCompat==null");
                    return;
                }
                i("nodeInfoCompat.getChildCount() "+nodeInfoCompat.getChildCount());
                if (nodeInfoCompat.getChildCount()>4)
                {

                    if (!Objects.isNull(nodeInfoCompat.getChild(5)))
                    {
                        i("nodeInfoCompat.getChild(5) "+nodeInfoCompat.getChild(5).getClassName());

                        if (nodeInfoCompat.getChild(5).getClassName().equals("android.widget.ImageButton"))
                        {

                            Log.d("accessibility","nodeInfoCompat.getChild(4).getClassName().equals(android.widget.ImageButton)");
                            if (nodeInfoCompat.getChild(5).isVisibleToUser())
                            {
                                Log.d("accessibility","nodeInfoCompat.getChild(4).isVisibleToUser()");

                                nodeInfoCompat.getChild(5).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                SharedPreferences.Editor se= sp.edit();
                                se.putBoolean("sendTextStatus2",true)
                                        .putBoolean("sendTextStatus",false)
                                        .apply();

                            }
                        }
                    }
                }

            }
        }).start();
    }




    int sendTextStatusWB2Help=0;

    void sendTextStatusWB2()
    {



        if (Objects.isNull(getRootInActiveWindow())) {

            return;
        }




        AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());

        if (Objects.isNull(nodeInfoCompat))
        {
            return;
        }

        List<AccessibilityNodeInfoCompat> sendNodeList=nodeInfoCompat.findAccessibilityNodeInfosByViewId("com.whatsapp.w4b:id/send");

        if (!Objects.isNull(sendNodeList) && !sendNodeList.isEmpty())
        {

            i("sendNodeList!=null && !sendNodeList.isEmpty() WB");

            AccessibilityNodeInfoCompat sendField = sendNodeList.get(0);

            if (sendField.isVisibleToUser())
            {

                i("sendField.isVisibleToUser() ");
                i("sendTextStatusWB2Help == "+sendTextStatusWB2Help);

                try {


                    if (i==1/*sendTextStatusWB2Help == 3*/) {
                        i=2;
//                                sendTextStatusWB2Help = 4;
//                                if (sendField.isVisibleToUser())
//                                {
//                                    Thread.sleep(5000);
//                                    sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
//                                }else {
//                                    Thread.sleep(200);
//                                }
                        SharedPreferences.Editor se = sp.edit();
                        se.putBoolean("sendTextStatus2", false).putBoolean("timeToOn", false).apply();

                        Thread.sleep(50);

                        performGlobalAction(GLOBAL_ACTION_HOME);
                        sucess("Status Uploaded","Your Scheduled status has been uploaded successfully");


                    }
                    else
                    {

                        Thread.sleep(3000);
                        sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
                        i=1;
//                                if (sendTextStatusWB2Help<2)
//                                {
//                                    if (sendTextStatusWB2Help==1)
//                                    {
//                                        Thread.sleep(3000);
//                                    }
//
//                                    i("sendTextStatusWB2Help+=1 "+sendTextStatusWB2Help);
//                                    // just finished status schedule test ,now do for unknown
//                                    sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
//                                    sendTextStatusWB2Help+=1;
//                                }
//
//                                if (sendTextStatusWB2Help==2)
//                                {
//                                    Thread.sleep(500);
//                                    i("sendTextStatusWB2Help "+sendTextStatusWB2Help);
//                                    performGlobalAction(GLOBAL_ACTION_HOME);
//                                    sendTextStatusWB2Help=3;
//                                }


                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }












//                        Log.d("accessibility","!sendField.isVisibleToUser() ");
//                        Log.d("accessibility","sendField.performAction");
//
//                        try {
//                            Thread.sleep(5000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        SharedPreferences.Editor se= sp.edit();
//
//                        se.putBoolean("sendTextStatus2",false).putBoolean("timeToOn",false).apply();
            }
        }



    }



    void sucess(String title,String message)
    {
        if (sp.getBoolean("isTest",false))
        {
            notificationForSucceed("Test Successful","Your test task is complete");
            se.putInt("id",0).putInt("isFailed",2).apply();
        }
        else
        {

            SharedPreferences.Editor se=sp.edit();
            String query="CREATE TABLE IF NOT EXISTS scheduleMessage(_id INTEGER PRIMARY KEY autoincrement, Name text DEFAULT 'Not set',Message text,Number text,Date text,OpDate text,OpTime text,isDraft INTEGER ,TextCountryCode text DEFAULT '91',SendThrough text DEFAULT 'com.whatsapp',State text DEFAULT '',ImageURIs text DEFAULT 'Not set'" +
                    ",VideoURIs text DEFAULT 'Not set',AudioURIs text DEFAULT 'Not set',DocURIs text DEFAULT '') ";

            database db =new database(getApplicationContext(),getString(R.string.schiduleTableName),query,1);
            db.getWritableDatabase().execSQL("UPDATE "+getString(R.string.schiduleTableName)+" SET State='Success' WHERE _id="+sp.getInt("id",0));
            // notificationForSucceed("Message Sent","Scheduled message send Successfully");
            se.putInt("id",0).putInt("isFailed",2).apply();


            notificationForSucceed(title,message);
        }

    }

    String[] names;
    int namesIndex=0;
    void selectChat(String packageName)
    {

        names=sp.getString("name","").split(",");

        if (Objects.isNull(getRootInActiveWindow())) {

            return;
        }
        AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());


        Log.i("accessibility","selectChat");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {


        List<AccessibilityNodeInfoCompat> searchNode=nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName+":id/menuitem_search");

        if (!Objects.isNull(searchNode) && !searchNode.isEmpty())
        {

            AccessibilityNodeInfoCompat searchIcon=searchNode.get(0);
            if (searchIcon.isVisibleToUser())
            {
                Log.d("accessibility","searchIcon.performAction");
                searchIcon.performAction(AccessibilityNodeInfo.ACTION_CLICK); // search icon click
            }
        }
        List<AccessibilityNodeInfoCompat> searchTextNode=nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName+":id/search_src_text");
        if (!Objects.isNull(searchTextNode) && !searchTextNode.isEmpty())
        {
            AccessibilityNodeInfoCompat searchText=searchTextNode.get(0);
            if (searchText.isVisibleToUser())
            {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bundle b=new Bundle();
                if (namesIndex<names.length)
                {
                    b.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, names[namesIndex]/* sp.getString("name","")*/);
                    searchText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,b); // setting name
                }




                //cant select contact from list

            }
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



            // currentNode.getChild(2).getChild(1).performAction(AccessibilityNodeInfo.ACTION_SELECT);
            List<AccessibilityNodeInfoCompat> contectPickerNode = nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName + ":id/contactpicker_row_name");
            //  Log.d("accessibility","contectPickerNode "+contectPickerNode);
            if (!Objects.isNull(contectPickerNode) && !contectPickerNode.isEmpty()) {

                AccessibilityNodeInfoCompat contectPicker = contectPickerNode.get(0);


                if (!Objects.isNull(contectPicker)) {
//                Log.d("accessibility","select contectPicker.getParent().isCheckable() "+contectPicker.getParent().isCheckable());
//                Log.d("accessibility","select contectPicker.getParent().isClickable() "+contectPicker.getParent().isClickable());
                    if (contectPicker.getParent().isClickable()) {

                        try {

                        Log.d("accessibility", "contectPicker.getParent() select chat");
                        contectPicker.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK); // selecting chat
                        namesIndex=namesIndex+1;




                        if (namesIndex<names.length)
                        {


                            Thread.sleep(20);
                            i("Names length :"+names.length+", select chat names index : "+namesIndex);
                            selectChat(packageName);

                        }else{
                            SharedPreferences.Editor se = sp.edit();
                            se.putBoolean("sendMedia", true).apply();
                        }




                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }






            }
        });





    }

    void sendTextStatus()
    {
        i("sendTextStatus");
        i("i= "+i);


        if (Objects.isNull(getRootInActiveWindow())) {

            return;
        }

        AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());
        List<AccessibilityNodeInfoCompat> sendNodeList=nodeInfoCompat.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");

        i("sendNodeList.isEmpty() "+(!Objects.isNull(sendNodeList))+" sendNodeList.isEmpty() "+!sendNodeList.isEmpty()+" sendNodeList.size() "+sendNodeList.size());
        if (!Objects.isNull(sendNodeList) && !sendNodeList.isEmpty())
        {

            i("\nsendNodeList!=null && !sendNodeList.isEmpty() ");
            AccessibilityNodeInfoCompat sendField= sendNodeList.get(0);

            if (sendField.isVisibleToUser())
            {
                i("sendField.isVisibleToUser() ");

                if (i==1)
                {
                    i=2;
                    SharedPreferences.Editor se= sp.edit();
                    se.putBoolean("sendTextStatus",false).putBoolean("sendTextStatus2",true).apply();
                    // notificationForSucceed("Status Uploaded","Scheduled status has been uploaded successfully");


                }else {
                    sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
                    //   performGlobalAction(GLOBAL_ACTION_HOME);
                    i("i= "+i);
                    i=1;
                }
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                            sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
//
//                        SharedPreferences.Editor se= sp.edit();
//                        se.putBoolean("sendTextStatus2",true)
//                                .putBoolean("sendTextStatus",false)
//                                .apply();
//                        try {
//                            Thread.sleep(5000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }

            }
        }



    }
    void sendTextStatus2()
    {
        try {
            i("sendTextStatus2");
            i("i= "+i);


            if (Objects.isNull(getRootInActiveWindow())) {

                return; // was setting 5 names from action pages to sp
            }




            AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());

            if (i==1)
            {
                i("i==1");

                i=2;
                SharedPreferences.Editor se= sp.edit();
                se.putBoolean("sendTextStatus2",false).putBoolean("timeToOn",false).apply();
                Thread.sleep(1000);
                performGlobalAction(GLOBAL_ACTION_HOME);
                sucess("Status Uploaded","Scheduled status has been uploaded successfully");

            }


            List<AccessibilityNodeInfoCompat> sendNodeList=nodeInfoCompat.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");

            i("sendTextStatus2   Objects.isNull(sendNodeList) "+Objects.isNull(sendNodeList));
            if (!Objects.isNull(sendNodeList) && !sendNodeList.isEmpty())
            {

                AccessibilityNodeInfoCompat sendField= sendNodeList.get(0);

                if (sendField.isVisibleToUser())
                {




                    Thread.sleep(1000);

                    if (i==1)
                    {
                        i("i==1");

                        i=2;
                        SharedPreferences.Editor se= sp.edit();
                        se.putBoolean("sendTextStatus2",false).putBoolean("timeToOn",false).apply();
                        Thread.sleep(1000);
                        performGlobalAction(GLOBAL_ACTION_HOME);
                        sucess("Status Uploaded","Scheduled status has been uploaded successfully");


                    }
                    else
                    {
                        Toast.makeText(this, "Please wait..", Toast.LENGTH_LONG).show();
                        Thread.sleep(5000);
                        sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);

                        i=1;
                    }










                }
            }

        }
        catch (Exception e) {
            Log.i("whatsappAccessibility",""+e.getMessage());
        }
    }

    int i=0;

    void sendMedia(String packageName)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {


                if (Objects.isNull(getRootInActiveWindow())) {

                    return;
                }
                AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());
                List<AccessibilityNodeInfoCompat> sendNodeList=nodeInfoCompat.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
                if (!Objects.isNull(sendNodeList) && !sendNodeList.isEmpty())
                {
                    AccessibilityNodeInfoCompat sendField= sendNodeList.get(0);
                    if (sendField.isVisibleToUser())
                    {
                        Log.d("accessibility","!sendField.isVisibleToUser() ");
                        Log.d("accessibility","sendField.performAction");

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
                        try {
                           // Toast.makeText(getApplicationContext(), "Please wait..", Toast.LENGTH_LONG).show();
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }



                        SharedPreferences.Editor se= sp.edit();
                        se.putBoolean("globalBack",true).putBoolean("sendMedia",false).apply();
                        performGlobalAction(GLOBAL_ACTION_BACK);

//                        if (mediaHelp2>10)
//                        {
//                            mediaHelp2=3;
//
//                            SharedPreferences.Editor se= sp.edit();
//                            se.putBoolean("globalBack",true).putBoolean("sendMedia",false).apply();
//                            i("-------------------------media end--------------------------------------");
//
//                        }else {
//                            i("mediaSend i = "+i);
//                            mediaHelp++;
//
//                            try {
//                            if (i==1) {
//
//
//                                    Thread.sleep(5000);
//                                sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
//                            }
//
//
//
//                            if (i==1)
//                            {
//                                Thread.sleep(200);
//
//                                performGlobalAction(GLOBAL_ACTION_HOME);
//                            }
//                            //performGlobalAction(GLOBAL_ACTION_HOME);
//                                i("mediaSend i = "+i);
//                            if (i==0)
//                            {
//                                i=1;
//                                sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
//                            }
//                                mediaHelp2++;
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
                    }
                }

            }
        }).start();


    }


    void gobackWB()
    {
        try {
            SharedPreferences.Editor se= sp.edit();
            se.putBoolean("sendMedia",false).putBoolean("sendSimpleText",false)
                    .putBoolean("simpleTextWA",false).putString("name","name").apply();
            Thread.sleep(1000);

            performGlobalAction(GLOBAL_ACTION_HOME);
            sucess("Message Sent","Your Scheduled message has been sent successfully");

            Thread.sleep(1000);
            // performGlobalAction(GLOBAL_ACTION_BACK);



            se.putBoolean("globalBack",false).putBoolean("timeToOn",false).apply();


        }catch (InterruptedException ignored)
        {

        }

    }
    void goBack(){
        //      Executors.newSingleThreadExecutor().execute(() -> {


        i("goback");

        try {
            SharedPreferences.Editor se= sp.edit();
            se.putBoolean("sendMedia",false).putBoolean("sendSimpleText",false)
                    .putBoolean("simpleTextWA",false).putString("name","name").apply();
            Thread.sleep(1000);

            performGlobalAction(GLOBAL_ACTION_HOME);
            // Log.d("accessibility","GLOBAL_ACTION_BACK ");
            se.putBoolean("globalBack",false).putBoolean("timeToOn",false).apply();
            i("home pressed");

            sucess("Message Sent","Your Scheduled message has been sent successfully");


             /*   new Thread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                        Thread.sleep(1000);
                        performGlobalAction(GLOBAL_ACTION_BACK);
                        if (sp.getBoolean("sendSimpleText",false))
                        {


                            // performGlobalAction(GLOBAL_ACTION_HOME);

                                Thread.sleep(1000);

                            performGlobalAction(GLOBAL_ACTION_BACK);
                        }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();*/


        }catch (InterruptedException ignored)
        {

        }

        //    });





        // database database=new database(getApplicationContext(),getString(R.string.schiduleTableName),)



        //all working fine #todo save database as done


    }



    void selectChatWB(String packageName)
    {

       new Thread(new Runnable() {
           @Override
           public void run() {


               try {






            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (Objects.isNull(getRootInActiveWindow())) {

                return;
            }
            AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());








            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
               if (Objects.isNull(nodeInfoCompat))
               {
                   return;
               }
            List<AccessibilityNodeInfoCompat> searchNode=nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName+":id/menuitem_search");

            if (!Objects.isNull(searchNode) && !searchNode.isEmpty())
            {

                AccessibilityNodeInfoCompat searchIcon=searchNode.get(0);
                if (searchIcon.isVisibleToUser())
                {
                    Log.i("accessibility","searchIcon.performAction");
                    searchIcon.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
            List<AccessibilityNodeInfoCompat> searchTextNode=nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName+":id/search_src_text");
            if (!Objects.isNull(searchTextNode) && !searchTextNode.isEmpty())
            {
                AccessibilityNodeInfoCompat searchText=searchTextNode.get(0);
                if (searchText.isVisibleToUser())
                {
                    i("selectChatWB name = "+sp.getString("name",""));
                    Bundle b=new Bundle();
                    b.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, sp.getString("name",""));
                    searchText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,b);

                    //cant select contact from list

                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // currentNode.getChild(2).getChild(1).performAction(AccessibilityNodeInfo.ACTION_SELECT);
            List<AccessibilityNodeInfoCompat> contectPickerNode=nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName+":id/contactpicker_row_name");
            //  Log.d("accessibility","contectPickerNode "+contectPickerNode);
            if (!Objects.isNull(contectPickerNode) && !contectPickerNode.isEmpty())
            {

                AccessibilityNodeInfoCompat contectPicker=contectPickerNode.get(0);

                Log.d("accessibility","contectPicker "+contectPicker);


                if (!Objects.isNull(contectPicker))
                {

                    Log.d("accessibility","select contectPicker.getParent().isClickable() "+contectPicker.getParent().isClickable());
                    if (contectPicker.getParent().isClickable() )
                    {
                        Log.d("accessibility","selectChatWB contectPicker.getParent()");
                        contectPicker.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        SharedPreferences.Editor se= sp.edit();
                        se.putBoolean("sendMedia",true).apply();
                        Log.d("accessibility","selectChatWB putBoolean(sendMedia,true)");
                    }


                }
            }



               }catch (Exception e)
               {
                   Log.e("accessibility","selectChatWB "+e);
               }

           }

       }).start();
    }

    void sendMediaWB(String packageName)
    {

        new Thread(new Runnable() {
            @Override
            public void run() {


                if (Objects.isNull(getRootInActiveWindow())) {

                    return;
                }
                AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());







                if (Objects.isNull(nodeInfoCompat))
                {
                    return;
                }
                if (nodeInfoCompat.getChildCount()>4)
                {
                    if (!Objects.isNull(nodeInfoCompat.getChild(4)))
                    {
                        if (nodeInfoCompat.getChild(4).getClassName().equals("android.widget.ImageButton"))
                        {

                            Log.d("accessibility","nodeInfoCompat.getChild(4).getClassName().equals(android.widget.ImageButton)");
                            if (nodeInfoCompat.getChild(4).isVisibleToUser())
                            {
                                Log.d("accessibility","nodeInfoCompat.getChild(4).isVisibleToUser()");

                                nodeInfoCompat.getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK);

                            }
                        }
                    }
                }






                List<AccessibilityNodeInfoCompat> sendNodeList=nodeInfoCompat.findAccessibilityNodeInfosByViewId("com.whatsapp.w4b:id/send");
                if (!Objects.isNull(sendNodeList) && !sendNodeList.isEmpty())
                {
                    AccessibilityNodeInfoCompat sendField= sendNodeList.get(0);
                    if (sendField.isVisibleToUser())
                    {
                        Log.d("accessibility","!sendField.isVisibleToUser() ");
                        Log.d("accessibility","sendField.performAction");

                        try {
                          //  Toast.makeText(getApplicationContext(), "Please wait..", Toast.LENGTH_LONG).show();
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor se= sp.edit();
                        se.putBoolean("globalBack",true).putBoolean("sendMedia",false).apply();
                    }
                }

            }
        }).start();



//        Executors.newSingleThreadExecutor().execute(() -> {
//
//            AccessibilityNodeInfoCompat nodeInfoCompat=AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());
//            if (getRootInActiveWindow()==null) {
//
//                return;
//            }
//
//
//            if (nodeInfoCompat.getChild(4)==null)
//            {
//                return;
//
//            }
//            if (!nodeInfoCompat.getChild(4).getClassName().equals("android.widget.ImageButton"))
//            {
//
//                return;
//
//            }
//            Log.d("accessibility","nodeInfoCompat.getChild(4).getClassName().equals(android.widget.ImageButton)");
//            if (!nodeInfoCompat.getChild(4).isVisibleToUser())
//            {
//                return;
//
//
//            }
//            Log.d("accessibility","nodeInfoCompat.getChild(4).isVisibleToUser()");
//
//            nodeInfoCompat.getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//
//
//            List<AccessibilityNodeInfoCompat> sendNodeList=nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName+":id/send");
//            if (sendNodeList==null || sendNodeList.isEmpty())
//            {
//               return;
//            }
//            //Toast.makeText(this, "sendNodeList", Toast.LENGTH_SHORT).show();
//            //  Log.d("accessibility","sendNodeList!=null");
//            AccessibilityNodeInfoCompat sendField= sendNodeList.get(0);
//            if (!sendField.isVisibleToUser())
//            {
//                return;
//
//
//
//            }else {
//                Log.d("accessibility","sendField.isVisibleToUser not");
//            }
//            //                Log.d("accessibility","sendField.isVisibleToUser() ");
////                Log.d("accessibility","sendField.performAction");
//            sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
//
//
////                AccessibilityNodeInfoCompat nodeInfoCompat=AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());
////                if (getRootInActiveWindow()==null) {
////
////                    return;
////                }
////
////
////                if (nodeInfoCompat.getChild(4)!=null)
////                {
////                    if (nodeInfoCompat.getChild(4).getClassName().equals("android.widget.ImageButton"))
////                    {
////
////                        Log.d("accessibility","nodeInfoCompat.getChild(4).getClassName().equals(android.widget.ImageButton)");
////                        if (nodeInfoCompat.getChild(4).isVisibleToUser())
////                        {
////                            Log.d("accessibility","nodeInfoCompat.getChild(4).isVisibleToUser()");
////
////                            nodeInfoCompat.getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK);
////
////                        }
////                    }
////                }
////                List<AccessibilityNodeInfoCompat> sendNodeList=nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName+":id/send");
////                if (sendNodeList!=null && !sendNodeList.isEmpty())
////                {
////                    //Toast.makeText(this, "sendNodeList", Toast.LENGTH_SHORT).show();
////                    //  Log.d("accessibility","sendNodeList!=null");
////                    AccessibilityNodeInfoCompat sendField= sendNodeList.get(0);
////                    if (sendField.isVisibleToUser())
////                    {
//////                Log.d("accessibility","sendField.isVisibleToUser() ");
//////                Log.d("accessibility","sendField.performAction");
////                        sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
////
////
////                    }else {
////                        Log.d("accessibility","sendField.isVisibleToUser not");
////                    }
////                }
//            SharedPreferences.Editor se= sp.edit();
//            se.putBoolean("globalBack",true).putBoolean("sendMedia",false).apply();
//
//        });


//        List<AccessibilityNodeInfoCompat> searchTextNode=nodeInfoCompat.findAccessibilityNodeInfosByViewId("com.whatsapp.w4b:id/search_src_text");
//        if (searchTextNode!=null && !searchTextNode.isEmpty())
//        {
//            AccessibilityNodeInfoCompat searchText=searchTextNode.get(0);
//            if (searchText.isVisibleToUser())
//            {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (searchText.getText().toString().length()>0)
//                {
//
//                    Bundle b=new Bundle();
//                    b.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "");
//                    searchText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,b);
//
//                    //cant select contact from list
//                }
//
//
//            }
//        }















    }
    void sendMediaW()//tset
    {

        new Thread(new Runnable() {
            @Override
            public void run() {


                if (Objects.isNull(getRootInActiveWindow())) {

                    return;
                }
                AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());







                if (Objects.isNull(nodeInfoCompat))
                {
                    return;
                }
//                if (nodeInfoCompat.getChildCount()>4)
//                {
//                    if (nodeInfoCompat.getChild(4)!=null)
//                    {
//                        if (nodeInfoCompat.getChild(4).getClassName().equals("android.widget.ImageButton"))
//                        {
//
//                            Log.d("accessibility","nodeInfoCompat.getChild(4).getClassName().equals(android.widget.ImageButton)");
//                            if (nodeInfoCompat.getChild(4).isVisibleToUser())
//                            {
//                                Log.d("accessibility","nodeInfoCompat.getChild(4).isVisibleToUser()");
//
//                                nodeInfoCompat.getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//
//                            }
//                        }
//                    }
//                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                for (int i=0;i<nodeInfoCompat.getChildCount();i++) {
//
//                    Log.i("accebility name", i + " getClassName " + nodeInfoCompat.getChild(i).getClassName().toString());
//                }



                    if (nodeInfoCompat.getChildCount()>0)
                    {
                        i("nodeInfoCompat.getChildCount()>0 "+nodeInfoCompat.getChildCount());
                        int index=3;
                        for(int i=0;i<nodeInfoCompat.getChildCount();i++)
                        {

                            //    i(i+"nodeInfoCompat.getChild "+nodeInfoCompat.getChild(i).getClassName());
                            if (nodeInfoCompat.getChild(i).getClassName().toString().equals("android.widget.ImageButton"))
                            {
                                index=i;
                            }
                        }
                        if ((nodeInfoCompat.getChildCount()>9))
                        {
                            i(" nodeInfoCompat.getChildCount()>9");
                            // if ((nodeInfoCompat.getChild(10)!=null)&&(nodeInfoCompat.getChild(10).getClassName().equals("android.widget.ImageButton")))
                            if (sp.getBoolean("sendSimpleText",false))
                            {
                                simpleTextWA(nodeInfoCompat.getPackageName().toString());  // when simple text
                            } else
                            {


                                // when media
                            if ((!Objects.isNull(nodeInfoCompat.getChild(index)))&&(nodeInfoCompat.getChild(index).getClassName().equals("android.widget.ImageButton")))
                            {
                                i("nodeInfoCompat.getChild(2)!=null");



                                    Log.d("accessibility", "nodeInfoCompat.getChild(4).getClassName().equals(android.widget.ImageButton)");
                                    if (nodeInfoCompat.getChild(index).isVisibleToUser()) {

                                        i("nodeInfoCompat.getChild(10).isVisibleToUser()");

                                        try {

                                           // Toast.makeText(getApplicationContext(), "Please wait..", Toast.LENGTH_LONG).show();
                                            Thread.sleep(5000);

                                            nodeInfoCompat.getChild(index).performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
                                            i("nodeInfoCompat.getChild(10) clicked");
                                            Thread.sleep(1000); // todo set the send button through child index as nodeInfoCompat.getChild(2) in the status

                                            SharedPreferences.Editor se = sp.edit();
                                            se.putBoolean("globalBack", true).putBoolean("sendMedia", false).apply();

                                            //  Thread.sleep(50);


                                        } catch (Exception e) {
                                            if (Objects.requireNonNull(e.getMessage()).contains("Attempt to invoke virtual method")) {
                                                se.putBoolean("globalBack", true).putBoolean("sendMedia", false).apply();
                                            }
                                            Log.e("accessibility", e.getMessage());
                                        }


                                    }
                            }
                            }
                        }
                        else if ((nodeInfoCompat.getChildCount()>4))
                        {
                            i("nodeInfoCompat.getChild(4)!=null  " +((!Objects.isNull(nodeInfoCompat.getChild(index)))&&nodeInfoCompat.getChild(index).getClassName().equals("android.widget.ImageButton")));
                            if ((!Objects.isNull(nodeInfoCompat.getChild(index)))&&nodeInfoCompat.getChild(index).getClassName().equals("android.widget.ImageButton")) {
                                i("send is index 4  child");

                                if (nodeInfoCompat.getChild(index).isVisibleToUser())
                                {
                                    i("send is visible");
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    nodeInfoCompat.getChild(index).performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
                                }



                            }

                        }

                    }else {
                        i("child count "+nodeInfoCompat.getChildCount());
                    }









            }
        }).start();



















    }

    void simpleTextWA(String packageName){ // when sending only text message to whatsapp
        if (Objects.isNull(getRootInActiveWindow())) {
            // Toast.makeText(this, "getRootInActiveWindow", Toast.LENGTH_SHORT).show();
            return;
        }
        AccessibilityNodeInfoCompat nodeInfoCompat= AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());
        Log.i("accessibility","simpleTextWA");
        List<AccessibilityNodeInfoCompat> messageNodeList=nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName+":id/entry");

        if (Objects.isNull(messageNodeList) || messageNodeList.isEmpty())
        {
            //Toast.makeText(this, "messageNodeList", Toast.LENGTH_SHORT).show();
            return;
        }

        AccessibilityNodeInfoCompat messageField= messageNodeList.get(0);

        if (messageField== null || messageField.getText().length()==0)
        {
            //Toast.makeText(this, "messageField", Toast.LENGTH_SHORT).show();
            return;
        }


        List<AccessibilityNodeInfoCompat> sendNodeList=nodeInfoCompat.findAccessibilityNodeInfosByViewId(packageName+":id/send");
        if (Objects.isNull(sendNodeList) || sendNodeList.isEmpty())
        {
            //Toast.makeText(this, "sendNodeList", Toast.LENGTH_SHORT).show();
            return;
        }


        AccessibilityNodeInfoCompat sendField= sendNodeList.get(0);
        if (!sendField.isVisibleToUser())
        {
            //Toast.makeText(this, "sendField", Toast.LENGTH_SHORT).show();
            return;
        }

        // scheduleBrodcust scheduleBrodcust=new scheduleBrodcust();
        // scheduleBrodcust.notificationForSucceed();

        sendField.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);


        try {

            Thread.sleep(1000);

        }catch (InterruptedException ignored)
        {

        }

        if (sp.getBoolean("isUnknown",false))
        {
            performGlobalAction(GLOBAL_ACTION_HOME);
            SharedPreferences.Editor se= sp.edit();
            se.putBoolean("isUnknown",false).putBoolean("sendSimpleText",false).apply(); // will stop the accessibility to avoid unnecessary click

            sucess("Message Sent","Your scheduled message has been sent successfully");
        }else {
            se.putBoolean("globalBack", true).putBoolean("sendMedia", false).apply();
        }
        //  sucess("Message Sent","Your Scheduled message has been sent successfully");
    }


    private void notificationForSucceed(String title,String message)
    {

        try {


            i("notifi");

//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
//
//
//            NotificationChannel channel = new NotificationChannel(getString(R.string.app_name), "Test",
//                    NotificationManager.IMPORTANCE_HIGH);
//
//            NotificationManager manager= (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
//            manager.createNotificationChannel(channel);
//
//
//        }
//
//
//
//        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // setting notification sound
//
//        NotificationCompat.Builder Nbuilder=new NotificationCompat.Builder(getApplicationContext(),
//                getString(R.string.app_name));
//
//
//        Nbuilder.setAutoCancel(true);
//        Nbuilder.setSmallIcon(R.drawable.schedule_icon);
//        Nbuilder.setContentTitle(title);
//        Nbuilder.setContentText(message);
//        Nbuilder.setSound(sound);
//        Nbuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
//
//
//        NotificationManager manager= (NotificationManager) getApplicationContext()
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//        manager.notify(1,Nbuilder.build());


            NotifyUser notifyUser=new NotifyUser(getApplicationContext(),"Schedule Sms");
            notifyUser.notifi(title,message);



            i("end");

        }catch (Exception e)
        {
            Log.e("accessibility",e.getMessage());
        }

    }

    void i(String s)
    {
        Log.i("accessibility",s);
    }
    @Override
    public void onInterrupt() {

    }
}
