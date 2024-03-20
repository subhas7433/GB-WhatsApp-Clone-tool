package com.affixstudio.whatsapptool.fragment;

import static com.affixstudio.whatsapptool.model.utils.Constants.MAX_DAYS;
import static com.affixstudio.whatsapptool.model.utils.Constants.MIN_DAYS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activity.autoReplyTest;
import com.affixstudio.whatsapptool.activity.customreplyeditor.CustomReplyEditorActivity;
import com.affixstudio.whatsapptool.activity.enabledapps.EnabledAppsActivity;
import com.affixstudio.whatsapptool.activity.main.MainActivity;
import com.affixstudio.whatsapptool.adapter.SupportedAppsAdapter;
import com.affixstudio.whatsapptool.model.App;
import com.affixstudio.whatsapptool.model.CustomRepliesData;
import com.affixstudio.whatsapptool.model.preferences.PreferencesManager;
import com.affixstudio.whatsapptool.model.utils.Constants;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomInfoDialog;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomSmallDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.tabs.TabLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class auto_reply_Setting extends Fragment {
    private static final int REQ_NOTIFICATION_LISTENER = 100;
    private static final int CONTACT_PICK_REQUEST = 1212;
    CardView autoReplyTextPreviewCard, timePickerCard;
    TextView autoReplyTextPreview, timeSelectedTextPreview, timePickerSubTitleTextPreview;
    CustomRepliesData customRepliesData;
    String autoReplyTextPlaceholder;

    CardView supportedAppsCard;
    private PreferencesManager preferencesManager;
    private int days = 0;
    private final List<MaterialCheckBox> supportedAppsCheckboxes = new ArrayList<>();
    private final List<View> supportedAppsDummyViews = new ArrayList<>();
    private Activity mActivity;
    private SupportedAppsAdapter supportedAppsAdapter;
    private List<App> enabledApps = new ArrayList<>();
    private TextView editEnabledAppsButton;

    database db;
    BottomSheetDialog contactListDialog;

    ArrayList<Integer> id=new ArrayList<>();// database row id
    ArrayList<String> contactNames=new ArrayList<>();

    SharedPreferences sp;
    SharedPreferences.Editor spEdit;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view= inflater.inflate(R.layout.fragment_auto_reply_setting, container, false);

         mActivity=getActivity();
        customRepliesData = CustomRepliesData.getInstance(mActivity);
        preferencesManager = PreferencesManager.getPreferencesInstance(mActivity);


        sp=getActivity().getSharedPreferences("affix",0);
        spEdit=sp.edit();


        String query="CREATE TABLE IF NOT EXISTS "+getString(R.string.contactList)+"(_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',Number text DEFAULT 'Not set') ";
        db=new database(getContext(),getString(R.string.contactList),query,1);



        autoReplyTextPreviewCard = view.findViewById(R.id.mainAutoReplyTextCardView);
        autoReplyTextPreview = view.findViewById(R.id.textView4);
        supportedAppsCard = view.findViewById(R.id.supportedAppsSelectorCardView);
        editEnabledAppsButton = view.findViewById(R.id.editEnabledAppsButton);


        supportedAppsCard.setOnClickListener(v -> launchEnabledAppsActivity());
        view.findViewById(R.id.enableApps).setOnClickListener(v -> launchEnabledAppsActivity());

        editEnabledAppsButton.setOnClickListener(v -> launchEnabledAppsActivity());

        MaterialCheckBox materialCheckBox=view.findViewById(R.id.materialCheckBox);
//        RadioButton
//                everyOne=view.findViewById(R.id.everyOne),
//                contactList=view.findViewById(R.id.contactList),
//                exceptContectList=view.findViewById(R.id.exceptContectList),
//                exceptPhoneContact=view.findViewById(R.id.exceptPhoneContact);

        SwitchCompat specificContactReply=view.findViewById(R.id.specificContactReply);



//       AppLovinNative lovinNative=new AppLovinNative(R.layout.native_ad_applovin_mid,getActivity());
//       lovinNative.mid(view.findViewById(R.id.ad_frame));





        contactListDialog=new BottomSheetDialog(getContext());

        View dialogView=getActivity().getLayoutInflater().inflate(R.layout.contact_list,null);

        Button startTime=view.findViewById(R.id.startTime); // schedule auto startTime Button
        Button endTime=view.findViewById(R.id.endTime); // schedule auto endTime Button

        RecyclerView contactListRecycleView=dialogView.findViewById(R.id.showContactRView);
        TextView noContactTV=dialogView.findViewById(R.id.noContactTV);





        materialCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    spEdit.putBoolean("scheduleAutoOn",true).apply();
                    view.findViewById(R.id.autoSelectTimeLayout).setVisibility(View.VISIBLE);

//                    long hour=sp.getLong("autoStartTimeMilis",79200000)/3600000;
//
                    ;
                    String sTag=getString(R.string.autoReplyStartTAG),eTag=getString(R.string.autoReplyEndTAG);

                    Log.i("autoReply","Start milis "+sp.getLong(sTag,79200000)+" End milis "+sp.getLong(eTag,21600000));
                    if (sp.getBoolean(getString(R.string.scheduleFstIndicater),true)) // if first time turning on schedule
                    {
                        Log.i("autoReply","schedule on 1st");
                        startTime.setText(getTimeString(sp.getLong(sTag,59434000)+19800000));
                        endTime.setText(getTimeString(sp.getLong(eTag,1834000)+19800000));


                        spEdit.putBoolean(getString(R.string.scheduleFstIndicater),false).apply();

                    }else {
                        Log.i("autoReply","schedule on 2st");
                        startTime.setText(getTimeString(sp.getLong(sTag,79200000)+19800000)); // time added because it showing 5.30 hours before
                        endTime.setText(getTimeString(sp.getLong(eTag,21600000)+19800000)); // time added because it showing 5.30 hours before 79200000


                    } // todo schedule is not working



//                    spEdit.putLong(sTag,sp.getLong(sTag,-19800000)+19800000)
//                            .putLong(eTag,sp.getLong(eTag,1800000)+19800000).apply();

                }else {
                    spEdit.putBoolean("scheduleAutoOn",false).apply();
                    view.findViewById(R.id.autoSelectTimeLayout).setVisibility(View.GONE);
                }
            }
        });
        if (sp.getBoolean("scheduleAutoOn",false))
        {
            materialCheckBox.setChecked(true);
        }

        Calendar now=Calendar.getInstance(Locale.getDefault());



        startTime.setOnClickListener(view1 -> {

            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                                                                                 @Override
                                                                                 public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

                                                                                     long t=getMilis(hourOfDay,minute,second);

                                                                                     spEdit.putLong("autoStartTimeMilis",t).apply();
                                                                                     startTime.setText(getTimeString(t+19800000)); // time added because it showing 5.30 hours before
                                                                                 }
                                                                             },now.get(Calendar.HOUR) ,
                    now.get(Calendar.MINUTE), now.get(Calendar.SECOND), false);
            timePickerDialog.show(getChildFragmentManager(), "Timepickerdialog");



        });

        view.findViewById(R.id.autoTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent i = new Intent(getContext(), autoReplyTest.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(i,b);

            }
        });

        bottomSmallDialog bsd=new bottomSmallDialog();
        view.findViewById(R.id.imageButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.showinfo(getContext(), 0);
            }
        });
        view.findViewById(R.id.replyFrequencyAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.showinfo(getContext(), 1);
            }
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
                                                                                     spEdit.putLong("autoEndTimeMilis",t).apply();
                                                                                     String k=getTimeString((t+19800000)); // time added because it showing 5.30 hours before
                                                                                     endTime.setText(k);
                                                                                     Log.i("auto","time ="+k);
                                                                                 }
                                                                             },now.get(Calendar.HOUR) ,
                    now.get(Calendar.MINUTE), now.get(Calendar.SECOND), false);
            timePickerDialog.show(getChildFragmentManager(), "Timepickerdialog");



        });



        contactListDialog.setContentView(dialogView);




        view.findViewById(R.id.viewSpecificContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), com.affixstudio.whatsapptool.activity.specificContactReply.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(i,b);
            }
        });


        RecyclerView enabledAppsList = view.findViewById(R.id.enabled_apps_list);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, getSpanCount(mActivity));
        enabledAppsList.setLayoutManager(layoutManager);
        supportedAppsAdapter = new SupportedAppsAdapter(Constants.EnabledAppsDisplayType.HORIZONTAL, getEnabledApps(), v ->
                launchEnabledAppsActivity()
        );
        enabledAppsList.setAdapter(supportedAppsAdapter);

        autoReplyTextPlaceholder = getResources().getString(R.string.mainAutoReplyTextPlaceholder);

        timePickerCard = view.findViewById(R.id.replyFrequencyTimePickerCardView);
        timePickerSubTitleTextPreview = view.findViewById(R.id.timePickerSubTitle);

        timeSelectedTextPreview = view.findViewById(R.id.timeSelectedText);

        ImageView imgMinus = view.findViewById(R.id.imgMinus);
        ImageView imgPlus = view.findViewById(R.id.imgPlus);

        autoReplyTextPreviewCard.setOnClickListener(this::openCustomReplyEditorActivity);
        autoReplyTextPreview.setText(customRepliesData.getTextToSendOrElse());
        // Enable group chat switch only if main switch id ON
       // groupReplySwitch.setEnabled(preferencesManager.isServiceEnabled());




        imgMinus.setOnClickListener(v -> {
            if (days > MIN_DAYS) {
                days--;
                saveNumDays();
            }
        });

        imgPlus.setOnClickListener(v -> {
            if (days < MAX_DAYS) {
                days++;
                saveNumDays();
            }
        });

        setNumDays();

        specificContactReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    spEdit.putBoolean("specificReplyOn",true).apply();
                    view.findViewById(R.id.spicificLayout).setVisibility(View.VISIBLE);
                }else {
                    spEdit.putBoolean("specificReplyOn",false).apply();
                    view.findViewById(R.id.spicificLayout).setVisibility(View.GONE);
                }
            }
        });
//        everyOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b){
//                    spEdit.putInt("replyTo",0).apply();// reply to everyone
//                }
//            }
//        });
//        contactList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b){
//                    spEdit.putInt("replyTo",1).apply();// reply to contactList
//                }
//            }
//        });
//        exceptContectList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b){
//                    spEdit.putInt("replyTo",2).apply(); // reply to exceptContectList
//                }
//            }
//        });
//        exceptPhoneContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b){
//                    spEdit.putInt("replyTo",3).apply();// reply to exceptPhoneContact
//                }
//            }
//        });
//


//
//        int replyTo=sp.getInt("replyTo",0);
//        if (replyTo==0)
//        {
//            everyOne.setChecked(true);
//        }else if (replyTo==1)
//        {
//            contactList.setChecked(true);
//        }else if (replyTo==2)
//        {
//            exceptContectList.setChecked(true);
//        }else if (replyTo==3)
//        {
//            exceptPhoneContact.setChecked(true);
//        }

        if (sp.getBoolean("specificReplyOn",false))
        {
            specificContactReply.setChecked(true);
        }



        if (sp.getBoolean(getString(R.string.fristInAutoReplyTag),true))
        {
            showBottomDialog();
            spEdit.putBoolean(getString(R.string.fristInAutoReplyTag),false);
            spEdit.apply();
            Log.i("autoReply","sp.getBoolean(\"fristInAutoReply\",true)");
        }
        else
        {
            Log.i("autoReply",""+sp.getBoolean(getString(R.string.fristInAutoReplyTag),true));
        }


//        getActivity().getActionBar().setSelectedNavigationItem(2);
        view.findViewById(R.id.induTabChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TabLayout tabs = (TabLayout)((MainActivity)getActivity()).findViewById(R.id.tab_Autoreply);
                Objects.requireNonNull(tabs.getTabAt(1)).select(); //https://stackoverflow.com/questions/16315280/how-to-switch-tabs-programmatically-in-android-from-fragment#:~:text=for%20Material%20support%20you%20switch,sendBroadcast(yourintent)%3B
            }
        });
        view.findViewById(R.id.groupTabChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TabLayout tabs = (TabLayout)((MainActivity)getActivity()).findViewById(R.id.tab_Autoreply);
                Objects.requireNonNull(tabs.getTabAt(1)).select(); //https://stackoverflow.com/questions/16315280/how-to-switch-tabs-programmatically-in-android-from-fragment#:~:text=for%20Material%20support%20you%20switch,sendBroadcast(yourintent)%3B
            }
        });



        return view;




    }

    @SuppressLint("DefaultLocale")
    private String getTimeString( long millis) {
        String amOrpm="am";
        long hour=TimeUnit.MILLISECONDS.toHours(millis);



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

    private long getMilis(int hourOfDay, int minute, int second)
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

    private void openCustomReplyEditorActivity(View v) {
        Intent intent = new Intent(mActivity, CustomReplyEditorActivity.class);
        startActivity(intent);
    }

    private List<App> getEnabledApps() {
        if (enabledApps != null) {
            enabledApps.clear();
        }
        enabledApps = new ArrayList<>();
        for (App app : Constants.SUPPORTED_APPS) {
            if (preferencesManager.isAppEnabled(app)) {
                enabledApps.add(app);
            }
        }
        return enabledApps;
    }
    public static int getSpanCount(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 35; // You can vary the value held by the scalingFactor
        return (int) (dpWidth / scalingFactor);
    }
    private void saveNumDays() {
        preferencesManager.setAutoReplyDelay((long) days * 24 * 60 * 60 * 1000);//Save in Milliseconds
        setNumDays();
    }
    private void setNumDays() {
        long timeDelay = (preferencesManager.getAutoReplyDelay() / (60 * 1000));//convert back to minutes
        days = (int) timeDelay / (60 * 24);//convert back to days
        if (days == 0) {
            timeSelectedTextPreview.setText("•");
            timePickerSubTitleTextPreview.setText(R.string.time_picker_sub_title_default);
        } else {
            timeSelectedTextPreview.setText(String.valueOf(days));
            timePickerSubTitleTextPreview.setText(String.format(getResources().getString(R.string.time_picker_sub_title), days));
        }
    }
    void showBottomDialog(){


        bottomInfoDialog bsd=new bottomInfoDialog();
        bsd.showinfo(getContext(),0,getString(R.string.fristInAutoReplyTag));
//        String[] list=getResources().getStringArray(R.array.list);
//        String[] detalisList=getResources().getStringArray(R.array.details);
//        String[] usageList=getResources().getStringArray(R.array.usage);
//        String[] ListVideoUrl=getResources().getStringArray(R.array.urlList);
//
//        BottomSheetDialog bsd=new BottomSheetDialog(getContext());
//
//        View v=getLayoutInflater().inflate(R.layout.bottom_recycle,null);
//
//        TextView pageTitle=v.findViewById(R.id.pageTitle);
//        pageTitle.setText(list[0]);
//
//
//        TextView details= (TextView)  v.findViewById(R.id.details);
//        details.setText(detalisList[0]);
//
//        TextView usage= (TextView)  v.findViewById(R.id.usage);
//        usage.setText(usageList[0]);
//
//
//
//        ImageView icon=(ImageView) v.findViewById(R.id.screenIcon);
//        icon.setImageResource(R.drawable.schedule_icon_20);
//
//        v.findViewById(R.id.watchVideo).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Intent.ACTION_VIEW);
//                intent.setData( Uri.parse(ListVideoUrl[0]));
//                startActivity(intent);
//            }
//        });
//
//        bsd.setContentView(v);
//        bsd.show();



    }
    private void launchEnabledAppsActivity() {
        Intent intent = new Intent(mActivity, EnabledAppsActivity.class);
        mActivity.startActivity(intent);
    }

}