package com.affixstudio.whatsapptool;

import static com.affixstudio.whatsapptool.modelOur.NotificationRecever.query;
import static com.affixstudio.whatsapptool.modelOur.sound.playSound;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.serviceOur.NotificationService;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;


public class fragment_private_recover_chat extends Fragment {

    View v;
    final int NOTIFICATION_PERMISSION =234 ;
    ImageView topImg;
    TextView topNotice;

    BroadcastReceiver updateUIReciver;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_private_recover_chat, container, false);
        topNotice=v.findViewById(R.id.workProperly);
        topImg=v.findViewById(R.id.workProperlyImg);
        String deleteSql="DELETE FROM "+getString(R.string.recover_message_table_name)+" WHERE 1";



//         Animation
//        profileIMG = (CircleImageView) findViewById(R.id.profile_image);
//        userName = (TextView) findViewById(R.id.chatName);
//        chatList = (CardView) findViewById(R.id.chatNameListCard);
//        chatList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                nextScreenAni(profileIMG,userName,sharedIntent);
//            }
//        });

        try {

            Log.i("recoverChat",getContext().toString().contains("private_chat")+"  getContext().toString() "+getContext().toString());
            if (getContext().toString().contains("private_chat"))
            {
                String query="CREATE TABLE IF NOT EXISTS "+getString(R.string.privateChatTableName)+" (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER,unread INTEGER DEFAULT '1' ) ";

                db=new database(getContext(),getString(R.string.privateChatTableName),query,1);
                deleteSql="DELETE FROM "+getString(R.string.privateChatTableName)+" WHERE 1";
            }else {
                db=new database(getContext(),getString(R.string.recover_message_table_name),query,2);
                deleteSql="DELETE FROM "+getString(R.string.recover_message_table_name)+" WHERE 1";
            }




            SwipeRefreshLayout refresh = v.findViewById(R.id.recoverRefresh);

            refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (VerifyNotificationPermission()) {
                        makeList();

                    }
                    refresh.setRefreshing(false);
                }
            });


            String finalDeleteSql = deleteSql; // auto generated
            v.findViewById(R.id.clearAll).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(getContext()).setIcon(R.drawable.alert_icon)
                            .setTitle("Delete All")
                            .setMessage("Do you really want to delete all the records?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    db.getWritableDatabase().execSQL(finalDeleteSql);
                                    showSnackBar("All record deleted successfully", R.color.snackbarBg);
                                    makeList();

                                }
                            }).setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();

                }
            });


            if (getContext().toString().contains("private_chat"))
            {
                topNotice.setText(getString(R.string.noBlueTickTopNotice));
                topNotice.setTextColor(getResources().getColor(R.color.double_tick_cl));
                topImg.setImageDrawable(getResources().getDrawable(R.drawable.double_tick));
            }

            IntentFilter filter = new IntentFilter();

            filter.addAction(getString(R.string.updateChatUpdateBroadcast));

             updateUIReciver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    //UI update here
                    makeList();

                }
            };
            getContext().registerReceiver(updateUIReciver,filter);






                makeList();



        }catch ( Exception e)
        {
            Log.e("recoverSms",e.getMessage());
        }



        return v;
    }






    @Override
    public void onResume() {
        super.onResume();
        Log.i("recoverSms","onResume");
        if (VerifyNotificationPermission())
        {
            Log.i("recoverSms","OnActivity resume "+VerifyNotificationPermission());
            makeList();

        }
        else
        {
            // v.findViewById(R.id.permissionLayout).setVisibility(View.VISIBLE);
            topNotice.setVisibility(View.GONE);
            v.findViewById(R.id.noChatLayout).setVisibility(View.VISIBLE);
        }


    }
    database db;
    private void makeList()
    {



        Cursor cursor= db.getinfo(1);  // when search data is needed passing the parameter 1



        try {

            db.checkData(cursor);

            RecyclerView recycleview = v.findViewById(R.id.chatNameRecycle);
            topNotice.setVisibility(View.VISIBLE);
            if (db.getRecevedFrom().size()==0)
            {
                v.findViewById(R.id.noChatLayout).setVisibility(View.VISIBLE);
                Log.i("recoverSms","db.getRecevedFrom().size()==0");
                v.findViewById(R.id.linearLayout7).setVisibility(View.GONE);

              //  v.findViewById(R.id.permissionLayout).setVisibility(View.GONE);

            }else
            {
                Log.i("recoverSms","makeList else "+db.getRecevedFrom().size());
                String name="name";
                ArrayList<String> nameList=new ArrayList<>();
                ArrayList<String> LastMessageList=new ArrayList<>();
                ArrayList<String> time=new ArrayList<>();
                ArrayList<Integer> unread=new ArrayList<>();
                ArrayList<Long> timeLong=new ArrayList<>();

                ArrayList<String> nameListM=new ArrayList<>();
                ArrayList<String> LastMessageListM=new ArrayList<>();
                ArrayList<String> timeM=new ArrayList<>();
                ArrayList<Integer> unreadM=new ArrayList<>();

                //  Log.i("lastMessageList",db.getMessage().get(10)+" m");



                ArrayList<Long> OldtimeLong=new ArrayList<>();
                for (int i=0;i<db.getRecevedFrom().size();i++)
                {

                    if (!nameList.contains(db.getRecevedFrom().get(i))) //checking is the name already in the list
                    {

                        int lastIndex=0; //latest message index
                        int unreadCount=0;
                        nameList.add(db.getRecevedFrom().get(i)); // adding new sender name to the list
                        name=db.getRecevedFrom().get(i); // sender name

                        for (int j=0;j<db.getRecevedFrom().size();j++)
                        {
                            if (name.equals(db.getRecevedFrom().get(j)))
                            {
                                if (db.getUnread().get(j)==1) // when the message is not read
                                {
                                    unreadCount+=1;
                                }
                                lastIndex=j; // getting the latest message index
                            }
                        }
                        Log.i("lastIndex",lastIndex+" m");

                        LastMessageList.add(db.getMessage().get(lastIndex));
                        time.add(db.getTime().get(lastIndex));
                        unread.add(unreadCount);




                        /*new entry*/
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy");
                        Date d1= null;
                        Date d2= null;
                        try {
                            d1 = sdf.parse(db.getTime().get(lastIndex));
                            d2=sdf.parse(sdf.format(new Date()));
                        } catch (ParseException e) {
                            Log.e("time",e.getMessage());
                        }

                        long diff=0;
                        if (d2!=null){
                            diff = d2.getTime() - d1.getTime();//as given
                        }
                        timeLong.add(diff);
                        OldtimeLong.add(diff);
                    }
                }
                Log.i("lastMessageList",time.size()+" m");


                Collections.sort(timeLong);

                Log.i("lastMessageList"," nameList "+nameList.get(0));
                for (int i=0;i<timeLong.size();i++)
                {
                    for (int k=0;k<OldtimeLong.size();k++) // checking whole old time list
                    {
                        if (timeLong.get(i).equals(OldtimeLong.get(k))) // finding the new time list item index in old list
                        {
                            if (!nameListM.contains(nameList.get(k))) // avoiding duplicate
                            {
                                // add name to the new list
                                nameListM.add(nameList.get(k));
                                LastMessageListM.add(LastMessageList.get(k));
                                timeM.add(time.get(k));
                                unreadM.add(unread.get(k));
                            }

                        }
                    }
//                    Log.i("lastMessageList","OldtimeLong = "+OldtimeLong.get(i)+"");
//                    Log.i("lastMessageList","timeLong = "+timeLong.get(i)+"");
//                    Log.i("lastMessageList","nameListM = "+nameListM.get(i)+"");
//                    Log.i("lastMessageList","nameList = "+nameList.get(i)+"");
                }



                int fromRecoverMessage=1;
                if (getContext().toString().contains("private_chat"))
                {
                    fromRecoverMessage=0;
                }

                com.affixstudio.whatsapptool.adopterOur.chatList chatList=new com.affixstudio.whatsapptool.adopterOur.chatList(getContext(),nameListM,LastMessageListM,timeM,unreadM,fromRecoverMessage,getActivity()/*nameList,LastMessageList,time*/);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

                v.findViewById(R.id.linearLayout7).setVisibility(View.VISIBLE);
                v.findViewById(R.id.noChatLayout).setVisibility(View.GONE);
               // v.findViewById(R.id.permissionLayout).setVisibility(View.GONE);
                topNotice.setVisibility(View.VISIBLE);

                recycleview.setAdapter(chatList);
                recycleview.setLayoutManager(layoutManager);

            }

            db.close();
            cursor.close();
        }catch (Exception e)
        {

            Log.e("recoverSms",e.getMessage());
            //Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }

//    Animation function

//    private void nextScreenAni(ImageView mytLinkImg, TextView mytLinkTxt, Intent intent) {
//        Pair[] pairs = new Pair[2];
//        pairs[0] = new Pair<View, String>(mytLinkImg,"imageTransition");
//        pairs[1] = new Pair<View, String>(mytLinkTxt,"nameTransition");
//
//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(recoverSms.this,pairs);
//        startActivity(intent, options.toBundle());
//    }

    public Boolean VerifyNotificationPermission() {
        Context c = getContext();
        if (c == null) {
            // Context is null, handle this case appropriately
            return false;
        }
        String theList = android.provider.Settings.Secure.getString(c.getContentResolver(), "enabled_notification_listeners");

        if (Objects.isNull(theList))
        {
            return false;
        }
        // Check if theList is not null before splitting
        if (theList != null && !theList.isEmpty()) {
            String[] theListList = theList.split(":");
            String me = (new ComponentName(c, NotificationService.class)).flattenToString();
            for (String next : theListList) {
                if (me.equals(next)) return true;
            }
        } else {

            // theList is null, handle this case appropriately
            Log.e("VerifyNotification", "No enabled notification listeners found.");
            return false;
        }
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==NOTIFICATION_PERMISSION )
        {
            if (VerifyNotificationPermission())
            {
                Log.i("recoverSms","OnActivity result");
                makeList();
            }

        }
    }
    void showSnackBar(String message,int color)
    {
        if (color==R.color.red)
        {
            playSound(getContext(),R.raw.error_sound);
        }
        Snackbar.make(v.findViewById(R.id.mainLayout),message, BaseTransientBottomBar.LENGTH_SHORT)
                .setTextColor(ContextCompat.getColor(getContext(),R.color.white))
                .setBackgroundTint(ContextCompat.getColor(getContext(), color)).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(updateUIReciver);
    }
}