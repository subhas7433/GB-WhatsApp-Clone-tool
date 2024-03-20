package com.affixstudio.whatsapptool.fragmentsOur;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.affixstudio.whatsapptool.fragmentsOur.DecorationText.Adepter.isInstalled;
import static com.affixstudio.whatsapptool.modelOur.sound.playSound;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.TextFunction;
import com.affixstudio.whatsapptool.activityOur.caption;
import com.affixstudio.whatsapptool.activityOur.schedule_sms;
import com.affixstudio.whatsapptool.adopterOur.Adepter;
import com.affixstudio.whatsapptool.ads.AppLovinNative;
import com.affixstudio.whatsapptool.getData.ArrayData;
import com.affixstudio.whatsapptool.getData.getQuickMessage;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomInfoDialog;
import com.affixstudio.whatsapptool.modelOur.setOnRecycleClick;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;


public class ChatFragment extends Fragment implements setOnRecycleClick {

    private static final int CONTACT_PICKER_REQUEST = 123;
    Intent mediaPickerData;
    private final int PICK_FROM_GALLARY=321;



    View v;
    Button sendButton;
    EditText WhatsappNumber,message;
    CountryCodePicker ccp;
    RadioButton RbWhats,RbWB;
    String sendto="com.whatsapp";
    database db;
    TextView TextCountryCode; // #todo set visibility in right condition

    RecyclerView chatRecycler;
   // FloatingActionButton new_chat,chatHistory;
    LinearLayout chatLayout;
    AutoCompleteTextView autoCompleteTextView;
    private String mimeType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_chat, container, false);
        try {


        v.findViewById(R.id.tutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomInfoDialog bid=new bottomInfoDialog();
                bid.showinfo(getContext(),3,getString(R.string.fristInDirectChatTeg));
            }
        });

        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.white, getContext().getTheme()));



        AppLovinNative lovinNative=new AppLovinNative(R.layout.native_ad_applovin_midium,getActivity());
        lovinNative.mid(v.findViewById(R.id.ad_frame));
//        MobileAds.initialize(getContext());
//
//
//       AdLoader adLoader=new AdLoader.Builder(getContext(),getString(R.string.nativeAdsId))
//                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                    @Override
//                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
//                        // Assumes you have a placeholder FrameLayout in your View layout
//                        // (with id fl_adplaceholder) where the ad is to be placed.
//                        FrameLayout frameLayout =
//                                v.findViewById(R.id.ad_frame);
//                        // Assumes that your ad layout is in a file call native_ad_layout.xml
//                        // in the res/layout folder
//                        NativeAdView adView = (NativeAdView) inflater
//                                .inflate(R.layout.native_admob_big, null);
//                        // This method sets the text, images and the native ad, etc into the ad
//                        // view.
//                        nativeAds nativeClass=new nativeAds();
//                        nativeClass.populateNativeAdViewBig(nativeAd, adView);
//
//                        frameLayout.removeAllViews();
//                        frameLayout.addView(adView);
//                    }
//                }).build();
//
//
//        AdRequest adRequest=new AdRequest.Builder().build();
//        adLoader.loadAd(adRequest);
        Runnable post=new Runnable() {
            @Override
            public void run() {

                String query="CREATE TABLE IF NOT EXISTS Chat_history(_id INTEGER PRIMARY KEY autoincrement,Message text,Number text,Date text,isDraft INTEGER,TextCountryCode text DEFAULT '91',SendThrough text DEFAULT 'com.whatsapp') ";
                db=new database(getContext(),getString(R.string.chatHistorytable),query,1);


                arrayData=new ArrayData();

                sendButton=v.findViewById(R.id.sendDirectChat);
                WhatsappNumber=v.findViewById(R.id.whatsappNumber);
                message=v.findViewById(R.id.Directmessage);
                ccp=v.findViewById(R.id.ccp);
                ccp.registerCarrierNumberEditText(message);

               // new_chat=v.findViewById(R.id.add_new_chat);
               // chatHistory=v.findViewById(R.id.chatHistory);
                chatRecycler=v.findViewById(R.id.chatRecycler);
                chatLayout=v.findViewById(R.id.chatLayout);




                RbWB=v.findViewById(R.id.whatsBs);
                RbWhats=v.findViewById(R.id.whats);
                TextCountryCode=v.findViewById(R.id.TextCountryCode);

                String countryCode=ccp.getSelectedCountryCode();

                //AutoCompleteTextView spinner=v.findViewById(R.id.quick_ms_select);

                v.findViewById(R.id.pick).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showChooser();
                    }
                });



//                chatHistory.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                chatLayout.setVisibility(View.GONE);
//                chatRecycler.setTranslationX(1200);
//                chatRecycler.animate().translationXBy(-1200).setDuration(200);
//                chatRecycler.setVisibility(View.VISIBLE);
//                ccp.setVisibility(View.VISIBLE);
//                TextCountryCode.setVisibility(View.GONE);
//
//                chatHistory.setVisibility(View.GONE);
//                        emptyFiled();
//                        callDataFromDb();
//
//
//                    }
//                });
//                new_chat.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        chatRecycler.setVisibility(View.GONE);
//                        v.findViewById(R.id.noHistory).setVisibility(View.GONE);
//                        chatLayout.setTranslationX(1200);
//                        chatLayout.animate().translationXBy(-1200).setDuration(200);
//                        chatLayout.setVisibility(View.VISIBLE);
//                        ccp.setVisibility(View.VISIBLE);
//                        TextCountryCode.setVisibility(View.GONE);
//                       // v.findViewById(R.id.space).setVisibility(View.GONE);
//                        new_chat.setVisibility(View.GONE);
//                        chatHistory.setVisibility(View.VISIBLE);
//                        emptyFiled();
//                    }
//                });





                SwitchCompat SheduleOn=v.findViewById(R.id.onschedule_timer);

                RbWhats.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b)
                        {
                            sendto="com.whatsapp";
                        }
                    }
                });
                RbWB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b)
                        {
                            sendto="com.whatsapp.w4b";
                        }
                    }
                });

                if (!isInstalled(getContext(),"com.whatsapp.w4b"))
                {
                    //v.findViewById(R.id.sendToLayout).setVisibility(View.GONE); //#todo set all function
                }else
                {
                    RbWhats.setChecked(true);
                }


                getQuickMessage gQm=new getQuickMessage(getContext(),getActivity());
                String[] allMessage=gQm.allMessage();
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getContext(),  android.R.layout.simple_list_item_1
                        ,allMessage);




                //spinner.setAdapter(arrayAdapter);

                autoCompleteTextView=v.findViewById(R.id.quick_ms_select);
                autoCompleteTextView.setAdapter(arrayAdapter);
                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //  Toast.makeText(getContext(), ""+messageList[i], Toast.LENGTH_SHORT).show();
                        message.setText(""+message.getText()+" "+allMessage[i]);
                    }
                });


                v.findViewById(R.id.Caption).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getContext(), caption.class);
                        Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                        startActivity(i,b);
                    }
                });

                v.findViewById(R.id.contactBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPickContact();
                    }
                });


                Intent intent=new Intent(getContext(), TextFunction.class);
                intent.putExtra("fromHome",false);
                intent.putExtra("directChat",true);

                v.findViewById(R.id.Ascii_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra("value",0);
                        Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                        startActivity(intent,b);

              /*  Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("**");
                startActivityForResult(
                        Intent.createChooser(chooseFile, "Choose a file"),
                        PICK_FROM_GALLARY
                );*/ //picking photo from galary

               /* Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, PICK_FROM_GALLARY);*/
                    }
                });
                v.findViewById(R.id.Decoration_Text).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra("value",1);
                        Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                        startActivity(intent,b);
                    }
                });
                v.findViewById(R.id.Text_Repeater).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        intent.putExtra("value",3);
                        Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                        startActivity(intent,b);

                    }
                });

                SharedPreferences sp= getContext().getSharedPreferences("affix",Context.MODE_PRIVATE);
                if (sp.getInt(getContext().getString(R.string.isScheduleONTag),1)==0) {
                    v.findViewById(R.id.scheduleGo).setVisibility(View.GONE);
                }
                SheduleOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b)
                        {
                            Intent i=new Intent(getContext(), schedule_sms.class);
                            i.putExtra("whatsappNumber",WhatsappNumber.getText().toString());
                            i.putExtra("Directmessage",message.getText().toString());
                            i.putExtra("sendThrouth",sendto);
                            i.putExtra("countryCode",ccp.getSelectedCountryCode());
                            i.putExtra("fromChat",1);
                            startActivity(i);





                            SheduleOn.setChecked(false);
                        }
                    }
                });



                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Toast.makeText(getContext(), ""+ccp.getSelectedCountryCode(), Toast.LENGTH_LONG).show();
                        String number=WhatsappNumber.getEditableText().toString().trim();

                        if (!isInstalled(getContext(),sendto))
                        {
                            showSnackBar(sendto.substring(4,11)+" is not installed",R.color.red);
                        }else if (number.length()==10)
                        {
                            SharedPreferences sp = getContext().getSharedPreferences("affix", MODE_PRIVATE);

                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor spedit=sp.edit();
                            spedit.putBoolean("timeToOn",true);
                            spedit.apply();


//                            Intent intent = new Intent( getContext(), scheduleBrodcust.class);
//                            PendingIntent pi = PendingIntent.getService(getContext(), 212, intent, 0);
//                            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
//                             alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//                                    + 50000, pi);


                           // startActivity(intent);
                         //   sendToWhatsapp(number);

                            try {
                                String mobile = number;

                                String stringUri=null;
                                if (message.getEditableText()==null)
                                {
                                    stringUri="https://api.whatsapp.com/send?phone=" + countryCode +mobile;
                                }else
                                {
                                    stringUri="https://api.whatsapp.com/send?phone=" + countryCode +mobile+"&text=" + message.getEditableText().toString();
                                }
                                String msg = "Its Working";


                                saveInDatabase();



                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(stringUri)).setPackage(sendto));
                            }catch (Exception e){
                                //whatsapp app not install
                            }

                   /* Uri uri = Uri.parse("smsto:" + number);
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.setPackage("com.whatsapp");
                    startActivity(Intent.createChooser(i, ""));*/





                        }else {
                            showSnackBar("Please enter correct number.",R.color.red);
                        }



                    }
                });





            }
        };






        v.postDelayed(post,300);
        }catch (Exception e)
        {
            e.printStackTrace();

        }

        return v;
    }



    void sendToWhatsapp(String number)
    {
        try {
            Intent i=new Intent(Intent.ACTION_MAIN);
            i.putExtra(Intent.EXTRA_STREAM,mediaPickerData.getData());
            i.putExtra("jib","91"+number+"@s.whatsapp.net");
            i.putExtra(Intent.EXTRA_TEXT,"hello");
            i.setAction(Intent.ACTION_SEND);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.setPackage("com.whatsapp");
            i.setType(mimeType);
            startActivity(i);

        }catch (Exception e)
        {
            Log.e("chatFragment",e.getMessage());
        }

    }
    private void saveInDatabase() {

        try {


            Calendar now=Calendar.getInstance(Locale.getDefault());;
            String amORpm="am";
            if (now.get(Calendar.AM_PM)==1)
            {
                amORpm="pm";
            }

            String datevalue=now.get(Calendar.HOUR)+":"+now.get(Calendar.MINUTE)+" "+amORpm+" "+now.get(Calendar.DAY_OF_MONTH)+"/"+now.get(Calendar.MONTH)+"/"+now.get(Calendar.YEAR);

            ContentValues contentValues=new ContentValues();
            contentValues.put("Message",message.getText().toString());
            contentValues.put("Date",datevalue);
            contentValues.put("Number",WhatsappNumber.getText().toString());
            contentValues.put("SendThrough",sendto);
            int code=91;

            if (ccp.isShown())
            {
                code=Integer.parseInt(ccp.getSelectedCountryCode().replace("+",""));
            }else
            {
                code=Integer.parseInt(TextCountryCode.getText().toString().replace("+",""));
            }

            contentValues.put("TextCountryCode",code);


            db.getWritableDatabase().insert(getString(R.string.chatHistorytable),null,contentValues);

        }catch (Exception e)
        {
            Log.e("saveInDatabase",e.getMessage());
        }

    }

    public void showPickContact() {




        // isPickingContact=true;
        Context mContext=getContext();

        final int REQUEST = 112;

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS};
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {


                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, CONTACT_PICKER_REQUEST);

                /* new MultiContactPicker.Builder(this) //Activity/fragment context
                .theme(R.style.AppTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(this, R.color.primary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(this, R.color.primary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                //.setSelectedContacts("10", "5" / myList) //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)

                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out) //Optional - default: No animation overrides
                .showPickerForResult(CONTACT_PICKER_REQUEST);}}*/
            }}
    }

    ArrayData arrayData;
    private void callDataFromDb() {


        v.postDelayed(new Runnable() {
            @Override
            public void run() {


                Log.i("callDataFromDb","start");
                if (!arrayData.Empty() ) // clearing duplicate data
                {

                    arrayData.ArrayClear();

                }


                Cursor cursor = db.getWritableDatabase().rawQuery("Select * from " +getString(R.string.chatHistorytable) /*table and database name is same*/
                        , null);






                while (cursor.moveToNext())
                {
                    arrayData.setId(cursor.getInt(0));
                    arrayData.setMessage(cursor.getString(1));
                    arrayData.setWANumebr(cursor.getString(2));
                    arrayData.setDate(cursor.getString(3));
                    arrayData.setIsDraft(cursor.getInt(4));    // was doing history section of chat
                    arrayData.setCountryCode(cursor.getString(5));
                    arrayData.setSendThrough(cursor.getString(6));



           /* if (cursor.getInt(7)==0)

            Log.i("cursor","draft="+cursor.getInt(7));
            if (cursor.getInt(7)==0)  // seperating saved from draft
            {
                id.add(d,cursor.getInt(0)); // d is giving index for draft to show in the top
                names.add(d,cursor.getString(1));
                message.add(d,cursor.getString(2));
                WANumebr.add(d,cursor.getString(3));
                date.add(d,cursor.getString(4));
                Opdate.add(d,cursor.getString(5));
                Optime.add(d,cursor.getString(6));
                isDraft.add(d,cursor.getInt(7));
                CountryCode.add(d,cursor.getString(8));
                d+=1;
            }else {
                id.add(cursor.getInt(0));
                names.add(cursor.getString(1));
                message.add(cursor.getString(2));
                WANumebr.add(cursor.getString(3));
                date.add(cursor.getString(4));
                Opdate.add(cursor.getString(5));
                Optime.add(cursor.getString(6));
                isDraft.add(cursor.getInt(7));
                CountryCode.add(cursor.getString(8));
            }
*/



                }

                Log.i("callDataFromDb","message array size "+arrayData.getMessage().size());

                if (arrayData.Empty()   )
                {
                    v.findViewById(R.id.noHistory).setVisibility(View.VISIBLE);
                    chatRecycler.setVisibility(View.GONE);

//                    new_chat.setVisibility(View.VISIBLE);
//                    chatHistory.setVisibility(View.GONE);

                    // if (issave)

                    chatLayout.setVisibility(View.GONE);

                    Log.i("callDataFromDb","isEditing");

                }/*else if (names.isEmpty() && message.isEmpty() && date.isEmpty() && WANumebr.isEmpty() )
        {
            findViewById(R.id.noHistory).setVisibility(View.GONE);
            schiduleRecycler.setVisibility(View.GONE);
            editSchedule.setVisibility(View.VISIBLE);
            add_new_Schedule.setVisibility(View.GONE);
            Log.i("layoutshow","not isEditing");
        }*/
                else
                {

                    Adepter adepter=new Adepter(getContext(),arrayData,1,ChatFragment.this);


                    Log.i("callDataFromDb",""+arrayData.getMessage().size());
                    chatRecycler.setAdapter(adepter);

                    chatRecycler.setVisibility(View.VISIBLE);
                    v.findViewById(R.id.noHistory).setVisibility(View.GONE);
                    chatLayout.setVisibility(View.GONE);
//                    new_chat.setVisibility(View.VISIBLE);
//                    chatHistory.setVisibility(View.GONE);
                    Log.i("layoutshow","else");


                }


                cursor.close();
            }
        },2);
    }

    private static boolean hasPermissions(Context context, String...  permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }




    private void showChooser() {

        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,PICK_FROM_GALLARY);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_PICKER_REQUEST && resultCode==RESULT_OK){
           /* if(resultCode == RESULT_OK) {
                List<ContactResult> results = MultiContactPicker.obtainResult(data);
                Log.d("MyTag", String.valueOf(results.get(0).getPhoneNumbers().get(0)));
            } else if(resultCode == RESULT_CANCELED){
                System.out.println("User closed the picker without selecting items.");
            }*/


            Uri uri = data.getData();
            String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

            Cursor cursor = getContext().getContentResolver().query(uri, projection,
                    null, null, null);
            cursor.moveToFirst();

            int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = cursor.getString(numberColumnIndex);

            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name = cursor.getString(nameColumnIndex);









            //ContactName.setText(name);
            WhatsappNumber.setText(number.replace("+91","").replace(" ","").replace("-",""));

            Log.d(TAG, "ZZZ number : " + number +" , name : "+name);



            cursor.close();

        }

        if (requestCode==PICK_FROM_GALLARY)
        {
            if (resultCode == Activity.RESULT_OK) {
                mediaPickerData=data;
                //pick image from gallery
                Uri selectedImage = data.getData();
                 mimeType = getActivity().getContentResolver().getType(selectedImage);
                File file=new File(selectedImage.getPath());
                Log.i("selectedImage",mimeType);


                String fileExtention=mimeType.substring(mimeType.indexOf("/")+1);











            }
        }  // was picking media to send in whatsapp
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
    public void onItemEdit(int position, String title) {

        if (!arrayData.getSendThrough().get(position).equals("com.whatsapp"))
        {
            RbWB.setChecked(true);
        }else {
            RbWhats.setChecked(true);
        }

        if (!arrayData.getMessage().get(position).isEmpty())
        {
            message.setText(arrayData.getMessage().get(position));
        }
        if (!arrayData.getWANumebr().get(position).isEmpty())
        {
            WhatsappNumber.setText(arrayData.getWANumebr().get(position));
        }
        chatRecycler.setVisibility(View.GONE);

        chatLayout.setTranslationX(1200);
        chatLayout.animate().translationXBy(-1200).setDuration(200);
        chatLayout.setVisibility(View.VISIBLE);
//        new_chat.setVisibility(View.GONE);
//        chatHistory.setVisibility(View.VISIBLE);


    }
    void emptyFiled(){
        message.setText("");
        WhatsappNumber.setText("");
        autoCompleteTextView.setText("");
        RbWhats.setChecked(true);

    }
}