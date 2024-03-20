package com.affixstudio.whatsapptool.modelOur;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.affixstudio.whatsapptool.activity.main.MainActivity.REQ_NOTIFICATION_LISTENER;
import static com.affixstudio.whatsapptool.activityOur.schedule_sms.isAccessibilityServiceEnabled;

import static com.affixstudio.whatsapptool.activityOur.startScreen.haveSub;
import static com.affixstudio.whatsapptool.fragmentsOur.DecorationText.Adepter.isInstalled;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.PurchaseActivity;
import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.ads.AppLovinNative;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomSmallDialog;
import com.affixstudio.whatsapptool.serviceOur.NotificationService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class callBlockHelper  {

    View v;
    int WAorWB;
    public int listType=1;
    Context c;
    Activity a;
     boolean isPickingForSpecific=false;
     boolean isSearching=false;
     boolean allChecked=false;

    public callBlockHelper(View v, int WAorWB, Context c, Activity a) {
        this.v = v;
        this.WAorWB = WAorWB;
        this.c = c;
        this.a = a;
    }

    public  void OnDestory()
    {
        db.close();
    }

    SharedPreferences sp;
    SharedPreferences.Editor spEdit;

    private static final int PEMISSION_OF_ACCESSEBLITY = 63;



    ArrayList<ContactInfo> contactInfos=new ArrayList<ContactInfo>();

    RecyclerView contactListRecycleView; // recycle in the bottom pop up
    TextView clearAllContact;
    LinearLayout noContactTV;


    ArrayList<ContactInfo> filteredContactList;
    ContactInfo contactInfo;

    BottomSheetDialog addGroupToListDialog,contactListDialog;
    DBNewVersion db;


    LinearLayout myContactListLayout;
    LinearLayout specificLayout;
    String getContactQuery;
    TextInputLayout searchLayout;

    SwitchCompat selectedSwitch; // need this when turning on a switch to reserve the switch
    RecyclerView callBlockRecycler;
    String tableName;
   public void setView( FragmentManager manager)
    {

        sp=c.getSharedPreferences("affix",0);
        spEdit=sp.edit();

        tableName=c.getString(R.string.CallBlockListtable2);

        getContactQuery="SELECT * FROM "+tableName+" WHERE IsGroup='0' AND WAorWB='"+WAorWB+"'";

        String query="CREATE TABLE IF NOT EXISTS CallBlockListtable2 (_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',Number text DEFAULT 'Not set',IsGroup text DEFAULT '0',WAorWB text DEFAULT '1') ";


        try {
            db=new DBNewVersion(c,tableName,query,1,tableName);
        }catch (Exception e)
        {
            Log.e(this.toString(),e.getMessage());
        }



        Calendar now=Calendar.getInstance(Locale.getDefault());

        String sMilisTag=c.getString(R.string.callBlockStartMilisTAG),eMilisTag=c.getString(R.string.callBlockEndMilisTAG);
        String IssMilisChangedTag=c.getString(R.string.IssMilisChangedCBlockTag),IseMilisChangedTag=c.getString(R.string.IseMilisChangedCBlockTag);
        String firstTime=c.getString(R.string.callScheduleIndiTAG);
        String scheduleCallBlockOnTAG=c.getString(R.string.WAscheduleCallBlockOnTAG);


        String voiceOnTAG=c.getString(R.string.WAvoicecallblockOnTAG);
        String videoOnTAG=c.getString(R.string.WAVideocallblockOnTAG);

        String blockCallsTAG=c.getString(R.string.blockCallOfTAG);




        if (WAorWB==2) // when business
        {
            scheduleCallBlockOnTAG=c.getString(R.string.WBscheduleCallBlockOnTAG);
            IssMilisChangedTag=c.getString(R.string.WBIssMilisChangedCBlockTag);
            IseMilisChangedTag=c.getString(R.string.WBIseMilisChangedCBlockTag);

            sMilisTag=c.getString(R.string.WBcallBlockStartMilisTAG);
            eMilisTag=c.getString(R.string.WBcallBlockEndMilisTAG);

            firstTime=c.getString(R.string.WBcallScheduleIndiTAG);

            blockCallsTAG=c.getString(R.string.WBblockCallOfTAG);

            voiceOnTAG=c.getString(R.string.WBvoicecallblockOnTAG);
            videoOnTAG=c.getString(R.string.WBVideocallblockOnTAG);
        }

        int blockCallsOf=sp.getInt(blockCallsTAG,0); // block calls of






        Button startTime=v.findViewById(R.id.startCallTime); // schedule auto startTime Button
        Button endTime=v.findViewById(R.id.endCallTime); // schedule auto endTime Button
        MaterialCheckBox scheduleOn=v.findViewById(R.id.schCallBlock);

        ImageView pickContact=v.findViewById(R.id.pickContact);



        String finalFirstTime = firstTime;
        String finalScheduleCallBlockOnTAG = scheduleCallBlockOnTAG;


        String finalEMilisTag = eMilisTag;
        String finalSMilisTag = sMilisTag;
        String finalIssMilisChangedTag = IssMilisChangedTag;
        String finalIseMilisChangedTag = IseMilisChangedTag;
        scheduleOn.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
            {
                spEdit.putBoolean(finalScheduleCallBlockOnTAG,true).apply();
                v.findViewById(R.id.callTimeLayout).setVisibility(View.VISIBLE);

//                    long hour=sp.getLong("autoStartTimeMilis",79200000)/3600000;
//
                ;


                Log.i("autoReply","Start milis "+sp.getLong(finalSMilisTag,79200000)+" End milis "+sp.getLong(finalEMilisTag,21600000));
                if (sp.getBoolean(finalFirstTime,true)) // if first time turning on schedule
                {
                    Log.i("autoReply","schedule on 1st");
                    startTime.setText(getTimeString(sp.getLong(finalSMilisTag,59434000)+19800000));
                    endTime.setText(getTimeString(sp.getLong(finalEMilisTag,1834000)+19800000));


                    spEdit.putBoolean(finalFirstTime,false).apply();

                }else {
                    Log.i("autoReply","schedule on 2st");
                    startTime.setText(getTimeString(sp.getLong(finalSMilisTag,79200000)));// when showing default schedule
                    if (sp.getBoolean(finalIssMilisChangedTag,false))
                    {
                        startTime.setText(getTimeString(sp.getLong(finalSMilisTag,79200000)+19800000)); // time added because it showing 5.30 hours before
                    }

                    endTime.setText(getTimeString(sp.getLong(finalEMilisTag,21600000))); // when showing default schedule
                    if (sp.getBoolean(finalIseMilisChangedTag,false))
                    {
                        endTime.setText(getTimeString(sp.getLong(finalEMilisTag,21600000)+19800000)); // time added because it showing 5.30 hours before 79200000
                    }

                } // todo schedule is not working



//                    spEdit.putLong(sTag,sp.getLong(sTag,-19800000)+19800000)
//                            .putLong(eTag,sp.getLong(eTag,1800000)+19800000).apply();

            }
            else
            {
                spEdit.putBoolean(finalScheduleCallBlockOnTAG,false).apply();
                v.findViewById(R.id.callTimeLayout).setVisibility(View.GONE);
            }
        });


        scheduleOn.setChecked(sp.getBoolean(finalScheduleCallBlockOnTAG,false));
        AppLovinNative nativeAd =new AppLovinNative(R.layout.native_ad_applovin_small,a);
        nativeAd.small(v.findViewById(R.id.netiveHolder));



        startTime.setOnClickListener(view1 -> {

            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                                                                                 @Override
                                                                                 public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

                                                                                     long t=getMilis(hourOfDay,minute,second);

                                                                                     spEdit.putLong(finalSMilisTag,t).putBoolean(finalIssMilisChangedTag,true).apply();
                                                                                     startTime.setText(getTimeString(t+19800000)); // time added because it showing 5.30 hours before
                                                                                 }
                                                                             },now.get(Calendar.HOUR) ,
                    now.get(Calendar.MINUTE), now.get(Calendar.SECOND), false);
            timePickerDialog.show(manager, "Timepickerdialog");



        });

        endTime.setOnClickListener(view1 -> {

            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                                                                                 @SuppressLint("SetTextI18n")
                                                                                 @Override
                                                                                 public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

                                                                                     long t=getMilis(hourOfDay,minute,second);

                                                                                     String forceAM="";
                                                                                     Log.i("auto","t = "+t);
                                                                                     if (t<0)
                                                                                     {
                                                                                         Log.i("auto","t = "+t);
                                                                                         forceAM="am"; // to show am when t is returning negative figure
                                                                                     }
                                                                                     spEdit.putLong(finalEMilisTag,t).putBoolean(finalIseMilisChangedTag,true).apply();
                                                                                     String k=getTimeString((t+19800000)); // time added because it showing 5.30 hours before
                                                                                     endTime.setText(k);
                                                                                     Log.i("auto","time ="+k);
                                                                                 }
                                                                             },now.get(Calendar.HOUR) ,
                    now.get(Calendar.MINUTE), now.get(Calendar.SECOND), false);
            timePickerDialog.show(manager, "Timepickerdialog");



        });



        callBlockRecycler=v.findViewById(R.id.callBlockRecycler);
        showHistory();





        SwitchCompat
                voiceSwitch=v.findViewById(R.id.waVoiceSwitch),
                videoSwitch=v.findViewById(R.id.waVideoSwitch);


        String finalVoiceOnTAG = voiceOnTAG;
        voiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedSwitch=voiceSwitch;
                if (b)
                {


                    if (isListenerEnabled(c,NotificationService.class))
                    {
                        spEdit.putBoolean(finalVoiceOnTAG,true).apply();

                    }else
                    {
                        setNotiPermissionDialog(voiceSwitch);
                    }

                }
                else
                {
                    spEdit.putBoolean(finalVoiceOnTAG,false).apply();
                }
            }
        });

        String finalVideoOnTAG = videoOnTAG;
        videoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedSwitch=videoSwitch;

                if (haveSub.equals("no") && b)
                {
                    c.startActivity(new Intent(c, PurchaseActivity.class));
                    videoSwitch.setChecked(false);


                    return;
                }
                if (b )
                {

                    if (isListenerEnabled(c,NotificationService.class))
                    {
                        spEdit.putBoolean(finalVideoOnTAG,true).apply();

                    }else
                    {
                        setNotiPermissionDialog(videoSwitch);
                    }


                }
                else
                {
                    spEdit.putBoolean(finalVideoOnTAG,false).apply();
                }
            }
        });



        if (isListenerEnabled(c,NotificationService.class))
        {
            voiceSwitch.setChecked(sp.getBoolean(voiceOnTAG,false));
            videoSwitch.setChecked(sp.getBoolean(videoOnTAG,false));
        }


        RadioButton
                everyOne=v.findViewById(R.id.allContact),
                exceptPhoneContact=v.findViewById(R.id.exceptPhoneContact),
                MyList=v.findViewById(R.id.MyList),
                exceptMyList=v.findViewById(R.id.exceptMyList);


        String finalBlockCallsTAG = blockCallsTAG;
        everyOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    spEdit.putInt(finalBlockCallsTAG,0).apply();
                }
            }
        });
        exceptPhoneContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b  )
                {
                    boolean isHave=isHasContactPermission(PERMISSIONS);
                    if (isHave)
                    {
                        spEdit.putInt(finalBlockCallsTAG,1).apply();
                    }else{

                        Toast.makeText(c, "Please enable Contact permission", Toast.LENGTH_LONG).show();
                        a.requestPermissions(PERMISSIONS, 2);
                        exceptPhoneContact.setChecked(false);
                        spEdit.putInt(finalBlockCallsTAG,0).apply();
                    }

                }
            }
        });
        MyList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    spEdit.putInt(finalBlockCallsTAG,2).apply();
                }
            }
        });
        exceptMyList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    spEdit.putInt(finalBlockCallsTAG,3).apply();
                }
            }
        });


        if (blockCallsOf==0)
        {
            everyOne.setChecked(true);
        }else if (blockCallsOf==1 && isHasContactPermission(PERMISSIONS))
        {
            exceptPhoneContact.setChecked(true);
        }else if (blockCallsOf==2)
        {
            MyList.setChecked(true);
        }else if (blockCallsOf==3)
        {
            exceptMyList.setChecked(true);
        }else {
            everyOne.setChecked(true);
        }





        contactListDialog=new BottomSheetDialog(c);
        View dialogView=a.getLayoutInflater().inflate(R.layout.contact_list,null);

        contactListRecycleView=dialogView.findViewById(R.id.showContactRView);


        noContactTV=dialogView.findViewById(R.id.noDataLayout);

        TextView listDialogTitle=dialogView.findViewById(R.id.listDialogTitle);
         clearAllContact=dialogView.findViewById(R.id.clearAllContact);


        ImageView DialogpickContact=dialogView.findViewById(R.id.pickContact);
        TextInputEditText searchContact=dialogView.findViewById(R.id.autoReplyTextInputEditText);

         myContactListLayout=dialogView.findViewById(R.id.MyContactList);
         specificLayout=dialogView.findViewById(R.id.specificContactLayout);

         searchLayout=dialogView.findViewById(R.id.searchLayout);

        mAdpterInisialize();
        // todo was setting the layout
        contactListDialog.setContentView(dialogView);
        v.findViewById(R.id.viewGroupList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {



                String groupQuery="Select * from "+tableName+" where IsGroup='1' and WAorWB='"+WAorWB+"'";;


                listType=4;
                listDialogTitle.setText("My Group List");
                specificLayout.setVisibility(View.GONE);

                Cursor cursor=db.getData(groupQuery);
                if (contactInfos.size()>0)
                {

                    contactInfos.clear();

                }
                while (cursor.moveToNext())
                {
                    contactInfo=new ContactInfo(cursor.getString(1)
                            ,cursor.getInt(0));
                    contactInfos.add(contactInfo);

                }

                filteredContactList=new ArrayList<>(contactInfos);



                i("filteredContactList "+filteredContactList.size());
                if (filteredContactList.size()>0)
                {
                    mAdpterInisialize();
                    noContactTV.setVisibility(View.GONE);

                    showViewWhenHasContacts();

                    contactListRecycleView.removeAllViews();
                    contactListRecycleView.setAdapter(mAdepter);

                    i("myContactListLayout visible "+myContactListLayout.getVisibility()+" contactListRecycleView visible "+contactListRecycleView.getVisibility());
                }else {
                    hideViewWhenNoContacts();

                }
                searchContact.setText("");
                contactListDialog.show();



            }
        });
        v.findViewById(R.id.deleteAllMediaSmall).setOnClickListener(new View.OnClickListener() { // show contact list
            @Override
            public void onClick(View view) {

               DBNewVersion db=new DBNewVersion(c,tableName,query,1,tableName);



                listType=0;
                listDialogTitle.setText("My Contact List");
                specificLayout.setVisibility(View.GONE);

                Cursor cursor=db.getData(getContactQuery);
                if (contactInfos.size()>0)
                {

                    contactInfos.clear();

                }
                while (cursor.moveToNext())
                {
                    contactInfo=new ContactInfo(cursor.getString(1)
                            ,cursor.getString(2),cursor.getInt(0));
                    contactInfos.add(contactInfo);

                }

                filteredContactList=new ArrayList<>(contactInfos);


                if (filteredContactList.size()>0){
                    mAdpterInisialize();

                    showViewWhenHasContacts();



                    contactListRecycleView.removeAllViews();
                    contactListRecycleView.setAdapter(mAdepter);
                }else {
                    hideViewWhenNoContacts();

                }
                searchContact.setText("");


                contactListDialog.show();
                db.close();
            }
        });

        pickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // launchMultiplePhonePicker();
                    showContactPicker();
                }catch (Exception e)
                {
                    Toast.makeText(c, ""+e, Toast.LENGTH_LONG).show();
                }





            }
        });

        addGroupToListDialog=new BottomSheetDialog(c);
        addGroupToListDialog.setContentView(addNewGroupName());
        v.findViewById(R.id.pickGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addGroupToListDialog.show();
            }
        });

        DialogpickContact.setOnClickListener(view1 -> {
            contactListDialog.dismiss();
            if (listType==0)
            {
                showContactPicker();
            }else if (listType==4)
            {
                addGroupToListDialog.show();

            }


        });

        clearAllContact.setOnClickListener(view1 -> {

            if (listType==4)
            {

                db.getReadableDatabase().execSQL("DELETE FROM "+tableName+" WHERE IsGroup='1' AND WAorWB = '"+WAorWB+"'");
                db.close();
            }else {
                db.getReadableDatabase().execSQL("DELETE FROM "+tableName+" WHERE IsGroup='0' AND WAorWB = '"+WAorWB+"'");
            }

           hideViewWhenNoContacts();

        });
        searchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isSearching=true;
                filterContact(editable.toString());

            }
        });


        bottomSmallDialog bsd=new bottomSmallDialog();

        // schedule tutorial
        v.findViewById(R.id.imageButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.showinfo(c, 8);
            }
        });

        // block call of tutorial
        v.findViewById(R.id.whatSendTo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.showinfo(c, 9);
            }
        });

        // My list tutorial
        v.findViewById(R.id.mylistWhat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.showinfo(c, 3);
            }
        });
    }

    private void showHistory() {

       LinearLayout noHistoryLA=v.findViewById(R.id.noHistoryLA);

       TextView deleteAllSchedule=v.findViewById(R.id.deleteAllSchedule);
       TextView seeFullHistory=v.findViewById(R.id.seeFullHistory);


        seeFullHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seeFullHistory.getText().toString().equals(c.getString(R.string.see_full_txt)))
                {

                    v.findViewById(R.id.topMainLayout).setVisibility(View.GONE);
                    seeFullHistory.setText("SEE LESS");
                }else {
                    v.findViewById(R.id.topMainLayout).setVisibility(View.VISIBLE);
                    seeFullHistory.setText(c.getString(R.string.see_full_txt));
                }
            }
        });


       String tableName=c.getString(R.string.call_block_history_table);
       String query= c.getString(R.string.tableCreationSQLPart1)+" "+tableName+" (_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',IsGroup text DEFAULT '0',IsVideoCall text DEFAULT '0',WAorWB text DEFAULT '1',Time text DEFAULT 'Not set') ";

        DBNewVersion db=new DBNewVersion(c,c.getString(R.string.CallBlockList),query,1,tableName);



        Cursor cursor=db.getData("SELECT * FROM "+tableName+" WHERE WAorWB='"+WAorWB+"'");

        ArrayList<String> names=new ArrayList<>();
        ArrayList<Integer> IsVideoCall=new ArrayList<>();
        ArrayList<String> time=new ArrayList<>();

        while ( cursor.moveToNext())
        {
            names.add(cursor.getString(1));
            IsVideoCall.add(cursor.getInt(3));
            time.add(cursor.getString(5));
        }

        deleteAllSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(c)
                        .setMessage("Do you really want to delete all the history?")
                        .setTitle("Warning")
                        .setIcon(R.drawable.alert_icon)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String sql="DELETE FROM "+tableName+" WHERE WAorWB='"+WAorWB+"'";

                                db.getWritableDatabase().execSQL(sql);
                                Toast.makeText(c, "Deleted Successfully ", Toast.LENGTH_SHORT).show();
                                callBlockRecycler.setVisibility(View.GONE);
                                noHistoryLA.setVisibility(View.VISIBLE);
                                deleteAllSchedule.setVisibility(View.GONE);
                            }
                        }).show()
                ;



            }
        });



        if (names.size()>0)
        {
            noHistoryLA.setVisibility(View.GONE);
            deleteAllSchedule.setVisibility(View.VISIBLE);
            callBlockRecycler.setVisibility(View.VISIBLE);
            callBlockRecycler.setAdapter(new RecyclerView.Adapter() {
                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_call_block,null)) {
                        @Override
                        public String toString() {
                            return super.toString();
                        }
                    };
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {

                    int i=names.size()-1-position;

                    TextView timeAgo=h.itemView.findViewById(R.id.timeAgo);
                    TextView chatName=h.itemView.findViewById(R.id.chatName);
                    ImageView image=h.itemView.findViewById(R.id.call_block_ic);



                    chatName.setText(names.get(i));
                    timeGape(timeAgo,time.get(i));
                    if (IsVideoCall.get(i)==1)
                    {
                        image.setImageResource(R.drawable.video_call_block_focused);
                    }else {
                        image.setImageResource(R.drawable.call_blocked_focused);
                    }





                }

                @Override
                public int getItemCount() {
                    return names.size();
                }
            });
        }
        else
        {

            noHistoryLA.setVisibility(View.VISIBLE);
            deleteAllSchedule.setVisibility(View.GONE);

        }



        db.close();
        cursor.close();



    }
    
    void timeGape(TextView timeAgo,String time)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy");
        Date d1= null;
        Date d2= null;
        try {
            d1 = sdf.parse(time);
            d2=sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            Log.e("time",e.getMessage());
        }

        long diff=0;
        if (d2!=null){
            diff = d2.getTime() - d1.getTime();//as given
        }


        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);
        Log.i("chatlist",minutes+" minutes>0 1");
        if (days<9 && days>6)
        {
            timeAgo.setText("A week ago");
        }else if (days>9 )
        {
            timeAgo.setText(time);
        }else if (days<=6 && days>0)
        {
            timeAgo.setText(""+days+" day(s) ago");
        }else if (hours>0)
        {
            long min=hours*60;

            if (minutes>min)
            {   long MinDiff=minutes-min;
                timeAgo.setText(""+hours+" h "+MinDiff+"min ago");
            }else
            {
                timeAgo.setText(""+hours+" h ago");
            }

        }else if (minutes>0)
        {
            Log.i("chatlist","minutes>0 1");
            long sec=minutes*60;

            if (seconds>sec)
            {   long secDiff=seconds-sec;
                timeAgo.setText(""+minutes+" min "+secDiff+"sec ago");
            }else
            {
                timeAgo.setText(""+minutes+" min ago");
                Log.i("chatlist","minutes>0");
            }
        }else
        {
            timeAgo.setText("Few Moment ago");
        }
    }

    void showViewWhenHasContacts()
    {

        noContactTV.setVisibility(View.GONE);
        myContactListLayout.setVisibility(View.VISIBLE);
        clearAllContact.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.VISIBLE);
    }

    void hideViewWhenNoContacts()
    {
        searchLayout.setVisibility(View.GONE);
        noContactTV.setVisibility(View.VISIBLE);
        myContactListLayout.setVisibility(View.GONE);
        clearAllContact.setVisibility(View.GONE);
    }
    @SuppressLint("DefaultLocale")
     String getTimeString(long millis) {
        String amOrpm="am";
        long hour= TimeUnit.MILLISECONDS.toHours(millis);



        Log.i("hour",""+hour);
        if (hour>12)
        {
            hour=hour-12;
            amOrpm="pm";

        }else if (hour==0)
        {
            hour=12;
        }else if (hour==12){
            amOrpm="pm";
        }
        return String.format("%02d:%02d", hour,
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))+""+amOrpm;
    }

     long getMilis(int hourOfDay, int minute, int second)
    {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sTimef = new SimpleDateFormat("HH:mm:ss");
        Date timeD = null;
        long timeMillis=0;
        try {
            timeD = sTimef.parse(hourOfDay+":"+minute+":"+second);
            timeMillis = Objects.requireNonNull(timeD).getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeMillis;
    }


    RecyclerView.Adapter mAdepter;
    void mAdpterInisialize()
    {
        mAdepter=new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item,null)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
                int Op=h.getBindingAdapterPosition(); // Original position of the view
                int index=filteredContactList.size()-1-Op;


                CardView contactCard=h.itemView.findViewById(R.id.contactListCard);
                ImageView deleteContact=h.itemView.findViewById(R.id.deleteContact);
                TextView contactName=h.itemView.findViewById(R.id.contactName);
                TextView number=h.itemView.findViewById(R.id.contactNumber);
                if (listType!=4) // when not group
                {
                    number.setText(filteredContactList.get(index).number);
                    number.setVisibility(View.VISIBLE);
                }else {
                    number.setVisibility(View.GONE);
                }

                TextView textIcon=h.itemView.findViewById(R.id.textIcon);

                contactName.setText(filteredContactList.get(index).contactNames);


                textIcon.setText(""+contactName.getText().toString().charAt(0));

                final  View v=h.itemView;
                deleteContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {



                            String s="DELETE FROM "+tableName+" WHERE _id="+filteredContactList.get(index).id;
                            db.getReadableDatabase().execSQL(s);


//                            if (listType==4)
//                            {
//
//
//
//                                db.getReadableDatabase().execSQL(s);
//                                db.close();
//                            }else {
//
//
//                                SQLiteDatabase sl=db.getReadableDatabase();
//                                sl.execSQL(s);
//                            }



                            int i=0;
                            for (ContactInfo contactInfo:contactInfos)
                            {
                                if (contactInfo.contactNames.equals(filteredContactList.get(index).contactNames))
                                {
                                    break;

                                }
                                i++;
                            }


                            Log.i("autoReply","contactInfo index "+contactInfos.get(i).contactNames);
                            ;
                            contactInfos.remove(i);
                            synchronized(contactInfos){
                                contactInfos.notifyAll();
                            }
                            filteredContactList.remove(index);

                            synchronized(filteredContactList){
                                filteredContactList.notifyAll();
                            }
                            // contactCard.setVisibility(View.GONE);
                            mAdepter.notifyItemRemoved(Op-1);
                            mAdepter.notifyItemRangeChanged(Op-1,filteredContactList.size());

                            i("deleteContact filteredContactList.size "+filteredContactList.size());

                            if (filteredContactList.size()==0 && !isSearching)
                            {
                                i("filteredContactList.size()==0");

                                hideViewWhenNoContacts();
                            }

                        }catch (Exception e)
                        {
                            Log.e("autoReply",e.getMessage());
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return filteredContactList.size();
            }
        };

    }
    TextView groupName;
    View addNewGroupName()
    {

        View v=View.inflate(c,R.layout.add_group_in_list,null);
        groupName=v.findViewById(R.id.groupName);
        ImageButton pickGroupInlist=v.findViewById(R.id.contactBtn);

        pickGroupInlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPickingForSpecific=false;
                spEdit.putBoolean("isPickingContact",true).apply();
                showWhatsappPickContact("Group Name");
            }
        });

        v.findViewById(R.id.addGroupSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gName=groupName.getText().toString();
                if(!gName.isEmpty())
                {
                   

                    ContentValues values=new ContentValues();
                    values.put("ContactName",gName);
                    values.put("WAorWB",WAorWB);
                    values.put("IsGroup","1");
                    db.getReadableDatabase().insert(tableName,null,values);
                    groupName.setText("");
                    Toast.makeText(c, "New Group Name add", Toast.LENGTH_SHORT).show();
                    addGroupToListDialog.dismiss();
                }
            }
        });


        return v;
    }
    final String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS
    };
    ProgressDialog pd;
    public void showContactPicker() {

        try {



            if (isHasContactPermission(PERMISSIONS))
            {


                Log.i("autoreply","has contact permission");



                pd=new ProgressDialog(c);
                pd.setMessage("Getting Contacts..");



                getContact();



            }else {
                Toast.makeText(c, "Please enable Contact permission", Toast.LENGTH_LONG).show();
                a.requestPermissions(PERMISSIONS, 2);
            }


//
        } catch (Exception e)
        {
            Log.e("autoreply",e.getMessage());
            Toast.makeText(c, ""+e, Toast.LENGTH_LONG).show();
        }
    }

    public void showWhatsappPickContact(String nameType) {



        if (isAccessibilityServiceEnabled(c, whatsappAccessibility.class))
        {

            spEdit.putString("pickedChatName","").putBoolean("gettingChatName",true).
                    putBoolean("isPickingContact",true).apply();
            Intent i=new Intent(Intent.ACTION_MAIN);


            if (isInstalled(c,"com.whatsapp.w4b") && isInstalled(c,"com.whatsapp") )
            {

                new  AlertDialog.Builder(c).setMessage("Choose Contact from").setPositiveButton("WhatsApp", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        i.setComponent(new ComponentName("com.whatsapp","com.whatsapp.HomeActivity"));
                        c.startActivity(i);
                    }
                }).setNegativeButton("WhatsApp Business", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        i.setComponent(new ComponentName("com.whatsapp.w4b","com.whatsapp.HomeActivity")); //#todo set proper whatsapp business home class name
                        c.startActivity(i);
                    }
                }).show();


            }
            else if(isInstalled(c,"com.whatsapp.w4b"))
            {

                i.setComponent(new ComponentName("com.whatsapp.w4b","com.whatsapp.w4b.HomeActivity"));
                c.startActivity(i);

            }else if (isInstalled(c,"com.whatsapp"))
            {
                i.setComponent(new ComponentName("com.whatsapp","com.whatsapp.HomeActivity"));
                c.startActivity(i);

            }else {

                Toast.makeText(c, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();

            }




        }
        else {
            new AlertDialog.Builder(c).setTitle("Permission Required")
                    .setMessage("In order to pick "+nameType+" from WHATSAPP we need ACCESSIBILITY SERVICE PERMISSION")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            a.startActivityForResult(intent,PEMISSION_OF_ACCESSEBLITY);
                        }
                    }).setNegativeButton("TYPE MANUALLY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
        }



    }


    ContactInfo contactInfoFiltered;
    @SuppressLint("NotifyDataSetChanged")
    private void filterContact(String text)
    {

        filteredContactList= new ArrayList<>();


        Log.i("autoReply","filtered size "+filteredContactList.size());

        if (listType==0 || listType==4) // indicating it has been called from View LIst button
        {
            for (int i=0;i<contactInfos.size();i++)
            {
                if (contactInfos.get(i).contactNames.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))
                {
                    contactInfoFiltered=new ContactInfo(contactInfos.get(i).contactNames,
                            contactInfos.get(i).number,contactInfos.get(i).id);
                    filteredContactList.add(contactInfoFiltered);

                }
            }

            contactListRecycleView.removeAllViews();
            mAdepter.notifyDataSetChanged();
        }

        if (text.isEmpty())
        {
            isSearching=false;
        }

    }
    
    public class ContactInfo {

        String contactNames;
        String contactID;
        String number;
        int id;

        public ContactInfo() {

        }

        public ContactInfo(String contactNames, String number, int id) {
            this.contactNames = contactNames;

            this.number = number;
            this.id = id;
        }

        public ContactInfo(String contactNames, int id) {
            this.contactNames = contactNames;
            this.id = id;
        }

        public String getContactNames() {
            return contactNames;
        }

        public void setContactNames(String contactNames) {
            this.contactNames = contactNames;
        }

        public String getContactID() {
            return contactID;
        }

        public void setContactID(String contactID) {
            this.contactID = contactID;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }


    boolean isHasContactPermission(String[] PERMISSIONS)
    {
        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(c, permissions) != PackageManager.PERMISSION_GRANTED) {

                return false;
            }
        }
        return true;
    }


    ArrayList<String> phFromContact=new ArrayList<>();
    ArrayList<String> nameFromContact=new ArrayList<>();


    ArrayList<String> filteredPickedNameList=new ArrayList<>();
    ArrayList<String> filteredPickedNumList=new ArrayList<>();
    @SuppressLint({"Range", "StaticFieldLeak"})
    void getContact()
    {



        new AsyncTask<String, String, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                // pd.show();
                if (nameFromContact.size() <= 0)
                {
                    pd.show();
                }




            }

            @Override
            protected String doInBackground(String... strings) {
                // fetchContactFromDB();
                fetchContactData(); // fetching contact data from phone




                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pd.dismiss();

                i("phFromContact size " +phFromContact.get(0));


                filteredPickedNameList=new ArrayList<>(nameFromContact);
                filteredPickedNumList=new ArrayList<>(phFromContact);
                setLayoutForPickContact();

            }
        }.execute();



    }
    @SuppressLint("Range")
    void fetchContactData()
    {

        ContentResolver cr = c.getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);


        if ((cur != null ? cur.getCount() : 0) > 0) {

            while (!Objects.isNull(cur) && cur.moveToNext())
            {

                 String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0)
                {
//                    Cursor pCur = cr.query(
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                            new String[]{id}, null);

                    String phoneNo = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                    if (!nameFromContact.contains(name))
                    {
                        nameFromContact.add(name);
                        phFromContact.add(phoneNo);
                    }




//                        Log.i(TAG, "Name: " + name);
//                        Log.i(TAG, "Phone Number: " + phoneNo);


                }
            }
        }
        if(cur!=null){
            cur.close();
        }


    }
    RecyclerView.Adapter contactPickerAdepter;
    ArrayList<CheckBox> selectedCheckBoxes=new ArrayList<>();
    ArrayList<String> unselectedItems;
    ArrayList<String> selectedItems;
    void filterPickedContact(String text)
    {



        filteredPickedNameList= new ArrayList<>();
        filteredPickedNumList= new ArrayList<>();
//        Log.i("autoReply","filtered size "+filteredContactList.size());


        for (int i = 0; i < nameFromContact.size(); i++) {
            if (nameFromContact.get(i).toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))
            {
                filteredPickedNameList.add(nameFromContact.get(i));
                filteredPickedNumList.add(phFromContact.get(i));


            }
        }

        // pickContactRecycle.removeAllViews();
        contactPickerAdepter.notifyDataSetChanged();

    }
    void contactPickerAd() //setting adepter for picked contact recycler
    {

        selectedCheckBoxes=new ArrayList<>();
        selectedItems=new ArrayList<>();// user manual selection
        unselectedItems=new ArrayList<>();// user manual unselection
        isSearching=false;

        contactPickerAdepter=new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_from_phone_item,null)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
                int Op=h.getBindingAdapterPosition(); // Original position of the view


                TextView contactName=h.itemView.findViewById(R.id.contactName);
                TextView number=h.itemView.findViewById(R.id.contactNumber);
                CardView contactListCard=h.itemView.findViewById(R.id.contactListCard);


                number.setText(filteredPickedNumList.get(Op));
                TextView textIcon=h.itemView.findViewById(R.id.textIcon);
                CheckBox selectCheckBox=h.itemView.findViewById(R.id.select);

                contactName.setText(filteredPickedNameList.get(Op));


                textIcon.setText(""+contactName.getText().toString().charAt(0));


                selectCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
                    int i=nameFromContact.indexOf(filteredPickedNameList.get(Op));// finding in main list

                    if (b)
                    {
                        contactListCard.setBackgroundColor(c.getResources().getColor(R.color.VA_on));
                        if (!selectedItems.contains(filteredPickedNameList.get(Op)))
                        {
                            selectedItems.add(filteredPickedNameList.get(Op));
                            i("selectedItems "+selectedItems.size());
                        }
                        unselectedItems.remove(filteredPickedNameList.get(Op));

                    }
                    else
                    {

                        contactListCard.setBackgroundColor(c.getResources().getColor(R.color.white));
                        selectedItems.remove(filteredPickedNameList.get(Op));


                        if (!unselectedItems.contains(filteredPickedNameList.get(Op)))
                        {
                            unselectedItems.add(filteredPickedNameList.get(Op));
                            i("selectedItems "+unselectedItems.size());
                        }


                    }


                });

                contactListCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectCheckBox.setChecked(!selectCheckBox.isChecked());
                    }
                });





                if (!isSearching) // when search names, selection will not be refreshed
                {
                    selectedCheckBoxes.add(selectCheckBox);

                    // selectCheckBox.setChecked(allChecked);
                    i("selectedCheckBoxes "+selectedCheckBoxes.size());
                }

                if (selectedItems.contains(filteredPickedNameList.get(Op)))
                {
                    selectCheckBox.setChecked(true);
                }





            }

            @Override
            public int getItemCount() {
                return filteredPickedNameList.size();
            }
        };

    }
    boolean alreadyAllSelected=false;
    BottomSheetDialog pickContactDialog;
    @SuppressLint("StaticFieldLeak")
    void setLayoutForPickContact()
    {
        alreadyAllSelected=false;

        pickContactDialog=new BottomSheetDialog(c);

        View pV=a.getLayoutInflater().inflate(R.layout.pick_contact_dialog_layout,null);

        Button pickedDone=pV.findViewById(R.id.pickedDone);
        ImageButton refresh=pV.findViewById(R.id.refresh);
        ImageButton checkAll=pV.findViewById(R.id.selectAll);
        TextInputEditText autoReplyTextInputEditText=pV.findViewById(R.id.autoReplyTextInputEditText);

       RecyclerView pickContactRecycle=pV.findViewById(R.id.pickContactRecycle);



        autoReplyTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                isSearching=true;
                filterPickedContact(editable.toString());
            }
        });

        refresh.setOnClickListener(view1 -> {
            getContact();
            pickContactDialog.dismiss();
        });

        pickedDone.setOnClickListener(view1 -> {

//            isSearching=false;
//            selectedCheckBoxes.clear();
//            autoReplyTextInputEditText.setText("");



            new AsyncTask<String, String, String>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pd.show();
                    pickContactDialog.dismiss();
                }

                @Override
                protected String doInBackground(String... strings) {

                    ArrayList<String> n=new ArrayList<>();



                    Cursor cursor=db.getData(getContactQuery);

                    while (cursor.moveToNext()) // getting saved contact
                    {
                        n.add(cursor.getString(1));



                    }

                    i("pickedDone  pickedIndex size  "+ selectedCheckBoxes.size());
                    if (alreadyAllSelected) //means user selected all
                    {
                        for (int i=0;nameFromContact.size()>i;i++)
                        {
                            String name=nameFromContact.get(i);
                            String num=phFromContact.get(i);
                            if (!n.contains(name) && !unselectedItems.contains(name))
                            {
                                ContentValues values = new ContentValues();

                                values.put("ContactName", name);
                                values.put("Number", num);


                                db.getReadableDatabase().insert(tableName, null, values);
                            }
                        }

                    }else
                    {
                        for (int i=0;nameFromContact.size()>i;i++)
                        {
                            String name=nameFromContact.get(i);
                            String num=phFromContact.get(i);


                            if (!n.contains(name) && selectedItems.contains(name)) // todo view list not showing properly
                            {
                                ContentValues values = new ContentValues();

                                values.put("ContactName", name);
                                values.put("Number", num);
                                values.put("WAorWB",WAorWB);
                                values.put("IsGroup","0");

                                db.getReadableDatabase().insert(tableName, null, values);
                            }
                        }
                    }



                    cursor.close();


                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    pd.dismiss();
                    Toast.makeText(c, "List Updated", Toast.LENGTH_SHORT).show();



                }
            }.execute();





        });

        allChecked=false; // makeing it false. Unless all will be selected second time
        contactPickerAd(); // refreshing adepter

        pickContactRecycle.setAdapter(contactPickerAdepter);

        checkAll.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                selectAllContact();

//                NotifyUser notifyUser=new NotifyUser(getContext());
//                notifyUser.UpdateUiOnNew(getString(R.string.selectAllPickedContactBroadCast));
            }
        });

        autoReplyTextInputEditText.setHint("Search among "+nameFromContact.size()+" contact(s)");

        pickContactDialog.setContentView(pV);

        pickContactDialog.show();





    }

    void selectAllContact()
    {
        for (CheckBox checkBox:selectedCheckBoxes)
        {
            // when already pressed selectall
            checkBox.setChecked(!alreadyAllSelected);
        }
        alreadyAllSelected=!alreadyAllSelected;

        selectedItems=new ArrayList<>(); // just clearing user manual selection
        unselectedItems=new ArrayList<>(); // just clearing user manual unselection
    }



    public void onResume() {

        if (sp.getBoolean("isPickingContact",false))
        {
            groupName.setText(sp.getString("pickedChatName","not set"));
            spEdit.putBoolean("isPickingContact",false).apply();

        }
        if (wentForPermission==1)
        {
            Log.i("autoMain","resume mainAutoReplySwitch "+isListenerEnabled(c, NotificationService.class));

            selectedSwitch.setChecked(!isListenerEnabled(c,NotificationService.class));
            selectedSwitch.setChecked(isListenerEnabled(c,NotificationService.class));
            wentForPermission=0;
        }
        // Toast.makeText(getContext(), "resume", Toast.LENGTH_SHORT).show();
    }
    void i(String s)
    {
        Log.i("callBlockHelper",s);
    }
    public boolean isListenerEnabled(Context context, Class notificationListenerCls) {
        ComponentName cn = new ComponentName(context, notificationListenerCls);
        String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());
    }


    Dialog notiPer;
    void setNotiPermissionDialog(SwitchCompat switchCompat)
    {
        notiPer=new Dialog(c);


        notiPer.setCancelable(false);
        View notiV=a.getLayoutInflater().inflate(R.layout.permission_notification,null);
        TextView grantNoti=notiV.findViewById(R.id.granNotiPer);
        ImageView closeN=notiV.findViewById(R.id.close);


        closeN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                notiPer.dismiss();
                switchCompat.setChecked(false);


            }
        });

        grantNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notiPer.dismiss();
                launchNotificationAccessSettings();


            } //todo fix multinotification issue in private chat
        });

        notiPer.getWindow().setBackgroundDrawable(c.getDrawable(R.drawable.custom_dialog));
        notiPer.setContentView(notiV);
        notiPer.show();
    }
    int wentForPermission=0;

    public void launchNotificationAccessSettings() {
        //We should remove it few versions later
        enableService();//we need to enable the service for it so show in settings

        //spEdit.putBoolean(c.getString(R.string.wentForNotiPermissionCallBlockTAG),true).apply();
        wentForPermission=1;

        final String NOTIFICATION_LISTENER_SETTINGS;
        NOTIFICATION_LISTENER_SETTINGS = ACTION_NOTIFICATION_LISTENER_SETTINGS;
        Intent i = new Intent(NOTIFICATION_LISTENER_SETTINGS);
        a.startActivityForResult(i, REQ_NOTIFICATION_LISTENER);
    }
    void enableService() {
        PackageManager packageManager = c.getPackageManager();
        ComponentName componentName = new ComponentName(c, NotificationService.class);
        int settingCode = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        // enable dummyActivity (as it is disabled in the manifest.xml)
        packageManager.setComponentEnabledSetting(componentName, settingCode, PackageManager.DONT_KILL_APP);

    }
}
