package com.affixstudio.whatsapptool.activityOur;


import static com.affixstudio.whatsapptool.fragmentsOur.DecorationText.Adepter.isInstalled;
import static com.affixstudio.whatsapptool.getData.GetInfo.isOnline;
import static com.affixstudio.whatsapptool.modelOur.sound.playSound;
import static com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.adopterOur.Adepter;
import com.affixstudio.whatsapptool.getData.GetInfo;
import com.affixstudio.whatsapptool.getData.NetworkChangeReceiver;
import com.affixstudio.whatsapptool.getData.getQuickMessage;
import com.affixstudio.whatsapptool.modelOur.StatusSaverUtill;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomInfoDialog;
import com.affixstudio.whatsapptool.modelOur.scheduleBrodcust;
import com.affixstudio.whatsapptool.modelOur.setOnRecycleClick;
import com.affixstudio.whatsapptool.modelOur.whatsappAccessibility;
import com.affixstudio.whatsapptool.playPrivateMedia;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class schedule_sms extends AppCompatActivity implements setOnRecycleClick {

    private static final int CONTACT_PICKER_REQUEST = 123;
    TextInputEditText ContactName,whatsappNumber, messageEB;
    RecyclerView schiduleRecycler;
    LinearLayout editSchedule;

    TextInputEditText dateTV,time;
    Button TextCountryCode;
    int month=0;
    int day=0;
    int years=0;
    int selectedHour=0;
    int selectedMin=0;
    int totalMedia=0;// media count which are selected
    database db;
    ArrayList <String> names=new ArrayList<>();
    ArrayList <String> message=new ArrayList<>();
    ArrayList <String> WANumebr=new ArrayList<>();
    ArrayList <String> date=new ArrayList<>();
    ArrayList<String> Opdate=new ArrayList<>();
    ArrayList<String> Optime=new ArrayList<>();
    ArrayList<Integer> isDraft=new ArrayList<>();
    ArrayList<Integer> id=new ArrayList<>();
    ArrayList<String> CountryCode=new ArrayList<>();
    ArrayList<String> state=new ArrayList<>();
    ArrayList<String> imagesUri=new ArrayList<>();
    ArrayList<String> videoUri=new ArrayList<>();
    ArrayList<String> audioUri=new ArrayList<>();
    ArrayList<String> docUri=new ArrayList<>();
    ArrayList<String> sendThrough=new ArrayList<String>();

    String seefulltxt;
    TextView turnOFFBattery;



    int d=0;// draft Count

    // By this the program will do a query when save button is pressed "is schedule time has changed or not
    boolean isEdting=false; // to know is the user setting new schedule or editing the oldone
    int position=0;

    CountryCodePicker ccp;
    boolean isPickingContact=false;
    final int PEMISSION_OF_ACCESSEBLITY=444;
    RadioButton RbWhats,RbWB;
    String sendto="com.whatsapp";
    private int isSave=1;

    final String TAG="schedule_sms";
    String opStingDate="13-12-1998";// old data
    int firstTimeGettingIntent=1; // when just come with intent and not used it
    //private InterstitialAd mInterstitialAd;
    SharedPreferences sp;
    SharedPreferences.Editor spEdit;
    private final int PICK_FROM_GALLARY=321;
    int scheduleType=0; // 1 = status schedule and 2 = normal schedule
    MaterialCheckBox unknowPersonCheckBox,statusTestCheckBox;
    LinearLayout chooseContactCard;
    LinearLayout numberEntryCard;
    private int dataPickingType; //which data type is picking. as image, video or audio
    private boolean isPickingMedia=false;
    private boolean isTest=true;

    StatusSaverUtill sst;

    private TextView showFullHistory,mediaCount,screenName;
    private int stateInt=-1; // to show the state of schedule of success list or pending list . 0=show all schedule
    private String indentifierForMainList="All"; // to know which radio button is selected
    TextView deleteAllSchedule;

    LinearLayout mainLayout;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume()
    {
        try {


            super.onResume();
            // mainLayout=findViewById(R.id.scheduleMainLayout);
            Log.i(TAG,"onResume");

            if (!isOnline(this))
            {
                startActivity(new Intent(this,no_internet_Screen.class));
            }
            i("!isPickingMedia"+!isPickingMedia+"  !editSchedule.isShown "+!editSchedule.isShown()
                    +" editSchedule.getVisibility= "+editSchedule.getVisibility() );
//            MaxAdView adView = findViewById( R.id.bn_appLovin );
//            adView.setListener(new MaxAdViewAdListener() {
//                @Override
//                public void onAdExpanded(MaxAd maxAd) {
//
//                }
//
//                @Override
//                public void onAdCollapsed(MaxAd maxAd) {
//
//                }
//
//                @Override
//                public void onAdLoaded(MaxAd maxAd) {
//                    adView.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onAdDisplayed(MaxAd maxAd) {
//
//                }
//
//                @Override
//                public void onAdHidden(MaxAd maxAd) {
//
//                }
//
//                @Override
//                public void onAdClicked(MaxAd maxAd) {
//
//                }
//
//                @Override
//                public void onAdLoadFailed(String s, MaxError maxError) {
//
//                }
//
//                @Override
//                public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
//
//                }
//            });
//
//            if (haveSub.equals("no"))
//            {
//
//                adView.loadAd();
//            }

            i("firstTime "+firstTime);

            if ((isAccessibilityServiceEnabled(schedule_sms.this, whatsappAccessibility.class)))
            {
                if (sp.getBoolean(getString(R.string.fristInScheduleTeg),true))
                {
                    bottomInfoDialog bid=new bottomInfoDialog();
                    bid.showinfo(schedule_sms.this,8,getString(R.string.fristInScheduleTeg));

                }
                else
                {
                    Log.i(TAG,""+sp.getBoolean("fristInSchedule",true));
                }

                if ( !isPickingContact && findViewById(R.id.permissionLayout).isShown()
                        // && !editSchedule.isShown()
                        && getIntent().getIntExtra("fromChat", 0) == 0)
                {


                    //  i("!isPickingContact  "+!isPickingContact+" !mainLayout.isShown() "+!mainLayout.isShown());

                    i("mainLayout.isShown()  ");
                    callDataFromDb(); // refreshing the layout after granting permission
                    divideHistory(indentifierForMainList); // refreshing the history


                    // todo set not unknown when media is selected




                }
                else if (isAccessibilityServiceEnabled(schedule_sms.this, whatsappAccessibility.class)
                        && !isPickingContact
                        && getIntent().getIntExtra("fromChat", 0) == 1)
                {



                    i("!isPickingContact   fromChat, 0) == 1");

                    setIntentChat(); //getting data from Direct chat


                }
                if (isPickingContact)
                {

                    ContactName.setText(ContactName.getText().toString()+sp.getString("pickedChatName","not set")+",");
                    i("isPickingContact");

                    isPickingContact=false;
                    Toast.makeText(this, ""+sp.getString("pickedChatName","not set"), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                isPickingMedia=false;

            }

        }catch (Exception e){
            Log.e("schedule_sms",e.getMessage());
        }

    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);  //todo when picking media layout is refreshing and media count is also showing wrong
    }
    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule_sms);



        i("oncreate");

        try {


            TextView grantPermission=findViewById(R.id.grantPermission);
            grantPermission.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    i("grantPermission");
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent,PEMISSION_OF_ACCESSEBLITY);
                }
            });
            seefulltxt=getString(R.string.see_full_txt);


            findViewById(R.id.dateLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i("findViewById(R.id.dateLayout)");
                }
            });


            i("on create");
            String query="CREATE TABLE IF NOT EXISTS scheduleMessage(_id INTEGER PRIMARY KEY autoincrement, Name text DEFAULT 'Not set',Message text,Number text,Date text,OpDate text,OpTime text,isDraft INTEGER ,TextCountryCode text DEFAULT '',SendThrough text DEFAULT 'com.whatsapp',State text DEFAULT '',ImageURIs text DEFAULT ''" +
                    ",VideoURIs text DEFAULT '',AudioURIs text DEFAULT '',DocURIs text DEFAULT '') ";

            db=new database(this,getString(R.string.schiduleTableName),query,1);

            sst=new StatusSaverUtill(this);


            schiduleRecycler=findViewById(R.id.scheduleRecycler);
            editSchedule=findViewById(R.id.EditSchedule);


            mainLayout=findViewById(R.id.scheduleMainLayout);
            ContactName=findViewById(R.id.ContactName);
            whatsappNumber=findViewById(R.id.whatsappNumber);
            time=findViewById(R.id.time);
            dateTV=findViewById(R.id.dateTV);
            messageEB=findViewById(R.id.messageEB);
            TextCountryCode=findViewById(R.id.TextCountryCode);
            screenName=findViewById(R.id.screenName); // top bar name

            mediaCount=findViewById(R.id.mediaCount);

            sp=getSharedPreferences("affix",MODE_PRIVATE);
            spEdit=sp.edit();

            unknowPersonCheckBox=findViewById(R.id.unknowPersonCheckBox);
            statusTestCheckBox=findViewById(R.id.statusTestCheckBox);
            chooseContactCard=findViewById(R.id.chooseContactCard);
            numberEntryCard=findViewById(R.id.numberEntryCard);
            deleteAllSchedule=findViewById(R.id.deleteAllSchedule);
            turnOFFBattery=findViewById(R.id.betteryOp);

            pickedImage=new ArrayList<>();
            pickedVideo=new ArrayList<>();
            pickedAudio=new ArrayList<>();
            pickedDoc=new ArrayList<>();

            pickedImageP=new ArrayList<>();
            pickedVideoP=new ArrayList<>();
            pickedAudioP=new ArrayList<>();
            pickedDocP=new ArrayList<>();


//            AppLovinNative lovinNative=new AppLovinNative(R.layout.native_ad_applovin_mid,this);
//            lovinNative.mid(findViewById(R.id.ad_frame));


            unknowPersonCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    whatsappNumber.setText("");
                    if (b)
                    {
                        chooseContactCard.setVisibility(View.GONE);
                        numberEntryCard.setVisibility(View.VISIBLE);
                        findViewById(R.id.onlyTextChooser).setVisibility(View.VISIBLE);
                        findViewById(R.id.selectedFileLA).setVisibility(View.GONE);
                        findViewById(R.id.documentsPickerLayout).setVisibility(View.GONE);
                        messageEB.setHint("Type Messages (Required)");
                        if (isTest)
                        {
                            statusTestCheckBox.setChecked(false);
                            findViewById(R.id.selectDataLA).setVisibility(View.GONE);
//                            findViewById(R.id.askUserLayout).setVisibility(View.GONE);
                        }

                    }else
                    {
                        messageEB.setHint("Type Messages (Optional)");
                        if (!isTest || !statusTestCheckBox.isChecked())
                        {
                            chooseContactCard.setVisibility(View.VISIBLE);
                            numberEntryCard.setVisibility(View.GONE);
                            findViewById(R.id.onlyTextChooser).setVisibility(View.GONE);
                            findViewById(R.id.selectedFileLA).setVisibility(View.VISIBLE);
                            findViewById(R.id.documentsPickerLayout).setVisibility(View.VISIBLE);

                        }else if (isTest)
                        {
                            findViewById(R.id.selectDataLA).setVisibility(View.GONE);
//                            findViewById(R.id.askUserLayout).setVisibility(View.GONE);
                        }


                    }
                }
            });
            statusTestCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        isTest=true;
                        unknowPersonCheckBox.setChecked(false);
                        setEditLayoutForStatus();
                        findViewById(R.id.unknowPersonCheckBoxLayout).setVisibility(View.VISIBLE);
                        findViewById(R.id.attentionLayout).setVisibility(View.GONE);
                        statusTestCheckBox.setVisibility(View.VISIBLE);
                        isTest=true;
                        findViewById(R.id.selectDataLA).setVisibility(View.GONE);
//                            findViewById(R.id.askUserLayout).setVisibility(View.GONE);
                        screenName.setText(getString(R.string.scheduleNameWhenTesting));

                    }else
                    {

                        isTest=true;
                        setEditLayoutForNormal();
                        statusTestCheckBox.setVisibility(View.VISIBLE);
                        findViewById(R.id.attentionLayout).setVisibility(View.GONE);
                        isTest=true;
                        findViewById(R.id.selectDataLA).setVisibility(View.GONE);
//                            findViewById(R.id.askUserLayout).setVisibility(View.GONE);
                        screenName.setText(getString(R.string.scheduleNameWhenTesting));



                    }
                }
            });




            findViewById(R.id.addNewSchedule).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setEditLayoutForNormal();
                    isSave=1;
                    isEdting=false;

                }
            });
            findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();

                }
            });

            // picking image
            findViewById(R.id.imageButton3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPickingMedia=true;
                    dataPickingType=1;
                    showChooser("image/*");
                }
            });

            // picking video
            findViewById(R.id.imageButton5).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPickingMedia=true;
                    dataPickingType=2;
                    showChooser("video/*");
                }
            });
            // picking audio
            findViewById(R.id.musicbtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPickingMedia=true;
                    dataPickingType=3;
                    showChooser("audio/*");
                }
            });


            AutoCompleteTextView quick_ms_select=findViewById(R.id.quick_ms_select);

            getQuickMessage gQm=new getQuickMessage(schedule_sms.this,schedule_sms.this); // getting all quickMessage by the class
            String[] allMessage=gQm.allMessage();
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(schedule_sms.this, android.R.layout.simple_list_item_1,allMessage);

            quick_ms_select.setAdapter(arrayAdapter);

            quick_ms_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    messageEB.setText(messageEB.getText()+" "+allMessage[i]);
                }
            });
            deleteAllSchedule.setOnClickListener(view -> {


                new AlertDialog.Builder(schedule_sms.this)
                        .setTitle("Delete All Record").setIcon(R.drawable.alert_icon)
                        .setMessage("Do you really want to delete all the records?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ProgressDialog pd=new ProgressDialog(schedule_sms.this);
                                pd.setMessage("Deleting..");
                                pd.setCancelable(false);
                                pd.show();

                                for (int j=0;j<id1.size();j++)
                                {
                                    try {
                                        Thread.sleep(5);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (state1.get(j).equals("Pending"))
                                    {

                                        setParamsForAllDelete(j);
                                    }

                                }
                                db.getWritableDatabase().execSQL("DELETE FROM "+getString(R.string.schiduleTableName)+" WHERE 1");

                                deleteAllScheduledMedia(sst.makeScheduleMediaDirectory());
                                showSnackBar("All Schedule record deleted successfully", R.color.colorSecondaryVariant);
                                callDataFromDb();


                                pd.dismiss();


                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

            });



            View DisV=getLayoutInflater().inflate(R.layout.disclosure,null);
            Dialog disclosure = new Dialog(this);
            disclosure.setCancelable(false);
            disclosure.getWindow().setBackgroundDrawable(schedule_sms.this.getDrawable(R.drawable.custom_dialog));

            // closeDialog=new BottomSheetDialog(this);

            DisV.findViewById(R.id.accTitle).setVisibility(View.VISIBLE);
            DisV.findViewById(R.id.accDis).setVisibility(View.VISIBLE);
            Button agree=DisV.findViewById(R.id.agree);
            Button discline=DisV.findViewById(R.id.decline);

            findViewById(R.id.pick).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    dataPickingType=4;
                    isPickingMedia=true;
                    showChooser("*/*");
                }
            });

            findViewById(R.id.pickContact).setOnClickListener(view -> {
                showPickContact();
            });

            agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spEdit.putBoolean("disclosure",true); // disclosure showed
                    spEdit.apply();
                    disclosure.dismiss();

                }
            });
            discline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disclosure.dismiss();
                    onBackPressed();
                }
            });


            disclosure.setContentView(DisV);





            findViewById(R.id.scheduleStatus).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    i("R.id.scheduleStatus");
                    isEdting=false;
                    isSave=1;
                    setEditLayoutForStatus();




                }
            });

            findViewById(R.id.scheduleTest).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {

                    isTest=true;
                    setEditLayoutForNormal();
                    findViewById(R.id.attentionLayout).setVisibility(View.GONE);
                    findViewById(R.id.selectDataLA).setVisibility(View.GONE);
//                    findViewById(R.id.askUserLayout).setVisibility(View.GONE);
                    statusTestCheckBox.setVisibility(View.VISIBLE);
                    isTest=true;

                    statusTestCheckBox.setChecked(false);
                    screenName.setText(getString(R.string.scheduleNameWhenTesting));

                    isTest=true;


                }
            });

            RadioButton statusRadio=findViewById(R.id.statusRadio),
                    contactsRadio=findViewById(R.id.contactRadio),
                    allRadio=findViewById(R.id.allHistoryRadio);


            allRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        indentifierForMainList="All";
                        divideHistory("All");
                    }
                }
            });
            statusRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        indentifierForMainList="Status";
                        divideHistory("Status");
                    }
                }
            });
            contactsRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        indentifierForMainList="normal";
                        divideHistory("normal");
                    }
                }
            });

            MaterialCheckBox
                    successCheckBox=findViewById(R.id.successCheckBox),
                    pendingCheckBox=findViewById(R.id.pendingCheckBox);




            successCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    i("successCheckBox "+successCheckBox.isChecked());
                    setHistoryAccordingToState(pendingCheckBox.isChecked(),successCheckBox.isChecked());
                }
            });

            pendingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    setHistoryAccordingToState(pendingCheckBox.isChecked(),successCheckBox.isChecked());
                }


            });




            findViewById(R.id.schTutorial).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    bottomInfoDialog bid=new bottomInfoDialog();

                    bid.showinfo(schedule_sms.this,8,getString(R.string.fristInScheduleTeg));

                    // startActivity(new Intent(schedule_sms.this, AppTutorials.class).putExtra("screenCode",9));
                }
            });
            RbWB=findViewById(R.id.whatsBs);
            RbWhats=findViewById(R.id.whats);



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

            if (!isInstalled(this,"com.whatsapp.w4b"))
            {
                findViewById(R.id.sendToLayout).setVisibility(View.GONE);
            }else
            {
                RbWhats.setChecked(true);
            }




            ccp=findViewById(R.id.ccp);

            ccp.registerCarrierNumberEditText(whatsappNumber);



            TextCountryCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {

                    ccp.setVisibility(View.VISIBLE);
                    TextCountryCode.setVisibility(View.GONE);
                    // findViewById(R.id.space).setVisibility(View.GONE);
                    Toast.makeText(schedule_sms.this, "Press again to select Country Code", Toast.LENGTH_LONG).show();

                }
            });









            Calendar now = Calendar.getInstance(Locale.getDefault());
            dateTV.setOnClickListener(view ->  {
                {




                    int Y,M,D;
                    Y=now.get(Calendar.YEAR);
                    M=now.get(Calendar.MONTH);
                    D=now.get(Calendar.DAY_OF_MONTH);




                    DatePickerDialog dpd = newInstance(
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                                    month = monthOfYear + 1;
                                    day=dayOfMonth;
                                    years=year;

                                    opStingDate= dayOfMonth+"-" + month + "-" +year;
                                    dateTV.setText(opStingDate);
                                }
                            },
                            Y,M,D


                    );
                    Log.i("datePressed",""+Y+"/"+M+"/"+D);
                    // If you're calling this from a support Fragment
                    dpd.show(getSupportFragmentManager(), "Datepickerdialog");
                }


            });




            time.setOnClickListener(view ->  {
                Calendar now1 = Calendar.getInstance(Locale.getDefault());
                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                Date dt=null;
                int h=0,m = 0;
                if (!isEdting )
                {
                    h=now1.get(Calendar.HOUR);
                    m=now1.get(Calendar.MINUTE);
                }else
                {
                    if (Optime1.size()!=0) {


                        try {
                            dt = dateFormat.parse(Optime1.get(position));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (dt != null) {
                            h = dt.getHours();
                            m = dt.getMinutes();
                        } else {
                            h = now1.get(Calendar.HOUR);
                            m = now1.get(Calendar.MINUTE);
                        }
                    }

                }


                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                                                                                     @Override
                                                                                     public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                                                         time.setText(""+hourOfDay+":"+minute+":"+00);
                                                                                         selectedHour = hourOfDay;
                                                                                         selectedMin=minute;
                                                                                     }
                                                                                 }, h,
                        m, now.get(Calendar.SECOND), false);
                timePickerDialog.show(getSupportFragmentManager(), "Timepickerdialog");



            });



            Intent intent=new Intent(schedule_sms.this, TextFunction.class);
            intent.putExtra("fromHome",false);


            findViewById(R.id.Caption).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(schedule_sms.this, caption.class);
                    Bundle b = ActivityOptions.makeSceneTransitionAnimation(schedule_sms.this).toBundle();
                    startActivity(i,b);
                }
            });
            findViewById(R.id.Caption1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(schedule_sms.this, caption.class);
                    Bundle b = ActivityOptions.makeSceneTransitionAnimation(schedule_sms.this).toBundle();
                    startActivity(i,b);
                }
            });
            findViewById(R.id.imageButton6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("value",0);
                    Bundle b = ActivityOptions.makeSceneTransitionAnimation(schedule_sms.this).toBundle();
                    startActivity(intent,b);


                }
            });
            findViewById(R.id.imageButton6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("value",0);
                    Bundle b = ActivityOptions.makeSceneTransitionAnimation(schedule_sms.this).toBundle();
                    startActivity(intent,b);


                }
            });


            findViewById(R.id.save_message_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i("saveButton","sava btn click");
                    onSaveClick(ContactName.getText().toString());








                }




            });





            turnOFFBattery.setOnClickListener(view -> {

                offOptimizition();



            });


              /* ********************************** reward ads ************************************
            AdRequest adRequest = new AdRequest.Builder().build();

            RewardedAd.load(this, getString(R.string.RewardAdsId),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.toString());
                            mRewardedAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
                            Log.d(TAG, "Ad was loaded.");
                        }
                    });

            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    mRewardedAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    mRewardedAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });


            // ********************************** reward ads End ************************************/
            // ********************************** Interstitial ads  ************************************/
//            MobileAds.initialize(this, new OnInitializationCompleteListener() {
//                @Override
//                public void onInitializationComplete(InitializationStatus initializationStatus) {}
//            });
//            loadInterstitial();

// ********************************** Interstitial ads End  ************************************/


            showFullHistory=findViewById(R.id.seeFullHistory);

            showFullHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showFullHistory();
                }
            });


            findViewById(R.id.deleteNewSchedule).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    try {

                        if (isEdting )
                        {
                            i("delete on click state1 "+state1.get(position));
                            if (state1.get(position).equals("Pending")) // when task is set but not completed
                            {
                                new AlertDialog.Builder(schedule_sms.this).setTitle("Alert")
                                        .setIcon(R.drawable.delete_icon)
                                        .setMessage("Deleting this will cancel the scheduled task also")
                                        .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                i("delete position "+position);
                                                String sql="DELETE FROM scheduleMessage WHERE _id="+id1.get(position);
                                                db.getWritableDatabase().execSQL(sql);
                                                deleteOldAlarm(ContactName.getText().toString());

                                                makeAllEmpty();
                                                callDataFromDb();

                                                editSchedule.setVisibility(View.GONE);
                                                i("editSchedule.setVisibility(View.GONE)6");

                                                schiduleRecycler.setTranslationX(1200);
                                                schiduleRecycler.animate().translationXBy(-1200).setDuration(200);
                                                schiduleRecycler.setVisibility(View.VISIBLE);

                                            }
                                        }).setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).show();
                            }
                            else
                            {
                                String sql="DELETE FROM scheduleMessage WHERE _id="+id1.get(position);
                                db.getWritableDatabase().execSQL(sql);
                                makeAllEmpty();
                                callDataFromDb();

                                editSchedule.setVisibility(View.GONE);
                                i("editSchedule.setVisibility(View.GONE)1");

                                schiduleRecycler.setTranslationX(1200);
                                schiduleRecycler.animate().translationXBy(-1200).setDuration(200);
                                schiduleRecycler.setVisibility(View.VISIBLE);
                            }


                        }
                        else
                        {
                            makeAllEmpty();
                            callDataFromDb();

                            editSchedule.setVisibility(View.GONE);
                            i("editSchedule.setVisibility(View.GONE)2");


                            schiduleRecycler.setTranslationX(1200);
                            schiduleRecycler.animate().translationXBy(-1200).setDuration(200);
                            schiduleRecycler.setVisibility(View.VISIBLE);
                        }




                    }catch (Exception e)
                    {
                        Log.e(TAG,"delete - "+e.getMessage());
                    }



                }});

            findViewById(R.id.Decoration_Text).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("value",1);
                    Bundle b = ActivityOptions.makeSceneTransitionAnimation(schedule_sms.this).toBundle();
                    startActivity(intent,b);
                }
            });
            findViewById(R.id.Decoration_Text1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("value",1);
                    Bundle b = ActivityOptions.makeSceneTransitionAnimation(schedule_sms.this).toBundle();
                    startActivity(intent,b);
                }
            });
            findViewById(R.id.Text_Repeater).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {

                    intent.putExtra("value",3);
                    Bundle b = ActivityOptions.makeSceneTransitionAnimation(schedule_sms.this).toBundle();
                    startActivity(intent,b);

                }
            });
            findViewById(R.id.imageButton4).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    intent.putExtra("value",3);
                    Bundle b = ActivityOptions.makeSceneTransitionAnimation(schedule_sms.this).toBundle();
                    startActivity(intent,b);

                }
            });
            findViewById(R.id.viewSelectedFile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSelectedFile();
                }
            });









            if (!sp.getBoolean("disclosure",false))
            {
                disclosure.show();
            }
            callDataFromDb(); //to show in recycle view

            allRadio.setChecked(true);







        }catch (Exception e)
        {
            Log.e("schedule_sms",e.getMessage());
        }


        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        receiver=new NetworkChangeReceiver();
        registerReceiver(receiver,filter);
    }

    private void offOptimizition() {
        Intent intent1 = new Intent();
        String packageName=getPackageName();


        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        if (!pm.isIgnoringBatteryOptimizations(packageName))

        {
            intent1.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent1.setData(Uri.parse("package:" + packageName));
            startActivity(intent1);
        }
        else
        {
            Toast.makeText(this, "Already Done", Toast.LENGTH_LONG).show();
        }
    }


    BroadcastReceiver receiver;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
            spEdit.putBoolean("gettingChatName",false).apply();
        }catch (Exception e)
        {

            Log.e("e",e.getMessage());
        }
    }


    private void deleteOldAlarm(String contactName)
    {
        try {


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        Intent intent = new Intent(schedule_sms.this, scheduleBrodcust.class);

        String intentNumber=WANumebr1.get(position); // from array
        if (!WANumebr1.get(position).equals("Status") && unknowPersonCheckBox.isChecked()) // if the schedule was not status type then get the name or number
        {
            intentNumber=CountryCode1.get(position)+WANumebr1.get(position); // from text box
            intent.putExtra("isUnknown", true);
        }

        intent.putExtra("type", scheduleType);
        intent.putExtra("number", intentNumber); // number key is used for both unknown person's number or Contact Name
        intent.putExtra("message", message1.get(position));
        intent.putExtra(Intent.EXTRA_TEXT, message1.get(position));
        intent.putExtra("package", sendThrough1.get(position));
        intent.putExtra("id", id1.get(position));
        intent.putExtra("name", contactName);


        if (isTest)
        {
            intent.putExtra("isTest",true);
        }
        intent.putExtra("sendSimpleText", true);
        if ((pickedImageP.size()+pickedVideoP.size()+pickedAudioP.size()+pickedDocP.size())>0)
        {
            i(" deleting a media pendingintent");
            ArrayList<Uri> totalData = new ArrayList<>(pickedImageP);


            totalData.addAll(pickedImageP.size(),pickedVideoP);
            totalData.addAll(pickedImageP.size()+pickedVideoP.size(),pickedAudioP);
            totalData.addAll(pickedImageP.size()+pickedVideoP.size()+pickedAudioP.size(),pickedDocP);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,totalData);
            intent.putExtra("sendSimpleText", false);
        }




        i(position+" Del id1.getposition = "+id1.get(position));

        PendingIntent pi=PendingIntent.getBroadcast(schedule_sms.this, id1.get(position), intent, PendingIntent.FLAG_IMMUTABLE); //setting exact old pending intent   https://stackoverflow.com/questions/25009195/how-to-edit-reset-alarm-manager
        alarmManager.cancel(pi); //canceling old one https://stackoverflow.com/questions/4347950/how-to-get-and-cancel-a-pendingintent


        }catch (Exception e)
        {
            Log.i(this.toString(),e.getMessage());
        }

    }

    private void setHistoryAccordingToState(boolean pendingCheckBoxChecked, boolean successChecked) {

        if (pendingCheckBoxChecked && !successChecked )
        {
            stateInt=1;
            refreshAdepter(names1,message1,date1,WANumebr1,isDraft1,id1,sendThrough1,state1,imagesUri1,videoUri1,audioUri1,docUri1,stateInt); // 1 = show only success

        }
        else if (!pendingCheckBoxChecked && successChecked)
        {
            stateInt=2;
            refreshAdepter(names1,message1,date1,WANumebr1,isDraft1,id1,sendThrough1,state1,imagesUri1,videoUri1,audioUri1,docUri1,stateInt);//2 = show only pending
        }else if (pendingCheckBoxChecked && successChecked){
            stateInt=0;
            refreshAdepter(names1,message1,date1,WANumebr1,isDraft1,id1,sendThrough1,state1,imagesUri1,videoUri1,audioUri1,docUri1,stateInt); // 0= all show success and pending
        }else {
            stateInt=-1;
            refreshAdepter(names1,message1,date1,WANumebr1,isDraft1,id1,sendThrough1,state1,imagesUri1,videoUri1,audioUri1,docUri1,stateInt); // 0= all show success and pending

        }

    }

    private void showFullHistory() {
        LinearLayout HistoryLayout=findViewById(R.id.History);
        LinearLayout functionButtonLayout=findViewById(R.id.functionButtonLayout);

        if (showFullHistory.getText().toString().equals(seefulltxt))
        {
            showFullHistory.setText("SEE LESS");
            functionButtonLayout.setVisibility(View.GONE);
        }else {
            showFullHistory.setText(seefulltxt);
            functionButtonLayout.setVisibility(View.VISIBLE);
        }





    }

    private void setEditLayoutForNormal() {

        screenName.setText(getString(R.string.scheduleNameWhenAddingNew));
        scheduleType=2;
        if (!isTest)
        {
            editSchedule.setTranslationX(1200);
            editSchedule.animate().translationXBy(-1200).setDuration(200);
        }
        editSchedule.setVisibility(View.VISIBLE);
        i("editSchedule.setVisibility(View.VISIBLE)1");
        mainLayout.setVisibility(View.GONE);
        i("mainLayout.setVisibility(View.GONE)1");
        ccp.setVisibility(View.VISIBLE);
        TextCountryCode.setVisibility(View.GONE);
        statusTestCheckBox.setVisibility(View.GONE);
        findViewById(R.id.attentionLayout).setVisibility(View.GONE);
        findViewById(R.id.pick).setVisibility(View.VISIBLE);
        findViewById(R.id.musicbtn).setVisibility(View.VISIBLE);
        unknowPersonCheckBox.setChecked(false);
        findViewById(R.id.selectDataLA).setVisibility(View.VISIBLE);
//        findViewById(R.id.askUserLayout).setVisibility(View.VISIBLE);
        messageEB.setHint("Type Messages (Optional)");
        makeAllVisible();

        makeAllEmpty();
        isTest=false;
    }

    private void setEditLayoutForStatus()

    {
        screenName.setText(getString(R.string.scheduleNameWhenAddingNewStatus));
        scheduleType=1;


        chooseContactCard.setVisibility(View.GONE);
        numberEntryCard.setVisibility(View.GONE);
        editSchedule.setVisibility(View.VISIBLE);
        i("editSchedule.setVisibility(View.VISIBLE)2");
        if (!isTest)
        {
            editSchedule.setTranslationX(1200);
            editSchedule.animate().translationXBy(-1200).setDuration(200);
        }
        messageEB.setHint("Type Messages (Optional)");


        findViewById(R.id.attentionLayout).setVisibility(View.GONE);
        findViewById(R.id.onlyTextChooser).setVisibility(View.GONE);
        findViewById(R.id.selectedFileLA).setVisibility(View.VISIBLE);
        findViewById(R.id.pick).setVisibility(View.GONE);
        findViewById(R.id.musicbtn).setVisibility(View.GONE);
        findViewById(R.id.documentsPickerLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.selectDataLA).setVisibility(View.VISIBLE);
//        findViewById(R.id.askUserLayout).setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
        i("mainLayout.setVisibility(View.GONE)2");
        statusTestCheckBox.setVisibility(View.GONE);

        findViewById(R.id.unknowPersonCheckBoxLayout).setVisibility(View.GONE);
        makeAllEmpty();
        isTest=false;
    }

    private void makeAllVisible() {
        chooseContactCard.setVisibility(View.VISIBLE);
        numberEntryCard.setVisibility(View.GONE);
        findViewById(R.id.onlyTextChooser).setVisibility(View.GONE);
        findViewById(R.id.selectedFileLA).setVisibility(View.VISIBLE);
        findViewById(R.id.documentsPickerLayout).setVisibility(View.VISIBLE);

        findViewById(R.id.unknowPersonCheckBoxLayout).setVisibility(View.VISIBLE);
    }

    private void showChooser(String type) {
        isPickingMedia=true;
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent,PICK_FROM_GALLARY);

    }


    private void loadInterstitial() {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        if (mInterstitialAd==null)
//        {
//            InterstitialAd.load(this,getString(R.string.interstitalAdId), adRequest,
//                    new InterstitialAdLoadCallback() {
//                        @Override
//                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                            // The mInterstitialAd reference will be null until
//                            // an ad is loaded.
//                            mInterstitialAd = interstitialAd;
//                            Log.i(TAG, "onAdLoaded");
//                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
//                                @Override
//                                public void onAdClicked() {
//                                    // Called when a click is recorded for an ad.
//                                    Log.d(TAG, "Ad was clicked.");
//                                }
//
//                                @Override
//                                public void onAdDismissedFullScreenContent() {
//                                    // Called when ad is dismissed.
//                                    // Set the ad reference to null so you don't show the ad a second time.
//                                    Log.d(TAG, "Ad dismissed fullscreen content.");
//                                    mInterstitialAd = null;
//                                    loadInterstitial();
//                                }
//
//                                @Override
//                                public void onAdFailedToShowFullScreenContent(AdError adError) {
//                                    // Called when ad fails to show.
//                                    Log.e(TAG, "Ad failed to show fullscreen content.");
//                                    mInterstitialAd = null;
//                                }
//
//                                @Override
//                                public void onAdImpression() {
//                                    // Called when an impression is recorded for an ad.
//                                    Log.d(TAG, "Ad recorded an impression.");
//                                    loadInterstitial();
//                                }
//
//                                @Override
//                                public void onAdShowedFullScreenContent() {
//                                    // Called when ad is shown.
//                                    Log.d(TAG, "Ad showed fullscreen content.");
//                                    loadInterstitial();
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            // Handle the error
//                            Log.d(TAG, loadAdError.toString());
//                            mInterstitialAd = null;
//                        }
//                    });
//        }

    }


    void ShowWorning()
    {

    }

    String dateTBS="Select Date",timeTBS="Select Time";


    private void onSaveClick(String contactName)

    {


        Calendar now=Calendar.getInstance(Locale.getDefault());
        // recheck Dialog
        BottomSheetDialog recheckDia=new BottomSheetDialog(schedule_sms.this);
        //recheckDia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        View sView=getLayoutInflater().inflate(R.layout.schedule_recheck_dialog,null);
        CheckBox dontShowRecheck=sView.findViewById(R.id.showitagainCheckbox);

        ConstraintLayout batterOPLADIa=sView.findViewById(R.id.betteryOpLA);
        TextView betteryOPDia=sView.findViewById(R.id.betteryOp);


        betteryOPDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offOptimizition();
            }
        });
        Button edit=sView.findViewById(R.id.edit),
                save=sView.findViewById(R.id.done);





        save.setOnClickListener(view -> {
            recheckDia.dismiss();
            saveInDatabase(now,0,contactName); /* issave=0 representing that saveInDatabase() is calling from save button
                            which makes isdreft false*/
            if (dontShowRecheck.isChecked())
            {
                spEdit.putBoolean(getString(R.string.dontShowRecheckScheduleTag),true).apply();
            }
        });


        edit.setOnClickListener(view -> {
            recheckDia.dismiss();
        });

        recheckDia.setContentView(sView);


        dateTBS=dateTV.getText().toString();
        timeTBS=time.getText().toString();



        if (!isInstalled(schedule_sms.this,"com.whatsapp"))
        {
            showSnackBar("Whatsapp is not installed in your phone.",R.color.red);

        }
        else if (!isAccessibilityServiceEnabled(schedule_sms.this, whatsappAccessibility.class))
        {  //checking is accessibility service enabled


            new AlertDialog.Builder(schedule_sms.this)
                    .setMessage("Need permission to schedule messages")
                    .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent,PEMISSION_OF_ACCESSEBLITY);

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();



        }
        else
        {

            if (isEdting)
            {
                String timeS=time.getText().toString();
                String dateS=dateTV.getText().toString();

                if (!timeS.contains("Select"))
                {
                    String[] sb=timeS.split(":");

                    i("sb "+ Arrays.toString(sb));
                    selectedHour=Integer.parseInt(sb[0]);
                    selectedMin=Integer.parseInt(sb[1]);


                }
                if (!dateS.contains("Select"))
                {
                    String[] sb=dateS.split("-");

                    i("sb "+ Arrays.toString(sb));
                    day=Integer.parseInt(sb[0]);



                }


            }


            i("is test "+isTest);


            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sdf.parse(opStingDate);
                d2=sdf.parse(sdf.format(new Date()));
            } catch (Exception e) {
                Log.e("timeSche",e.getMessage());
            }

            long dateDiff=-5;
            if (d1!=null)
            {
                Log.i(TAG," Date "+d1);

                dateDiff=d1.getTime()-d2.getTime();
            }
            Log.i(TAG,opStingDate+" Date difference "+dateDiff+d1+" date2 ="+d2);

            i("onsave  clicked selectedHour "+selectedHour+"! hour of day "+now.get(Calendar.HOUR_OF_DAY)+"! (now.get(Calendar.HOUR_OF_DAY)>selectedHour) "+((now.get(Calendar.HOUR_OF_DAY)>selectedHour) )
            +"DAY_OF_MONTH "+now.get(Calendar.DAY_OF_MONTH)+" ! day "+day);
            if ( scheduleType!=1 && unknowPersonCheckBox.isChecked()
                    &&whatsappNumber.getText().toString()
                    .replace(" ","").length() !=10)
            {
                //checking is whatsapp number entered when scheduletype is normal and set for unknown

                showSnackBar("Enter a correct WhatsApp number",R.color.red);

            }else if ( scheduleType!=1 &&!unknowPersonCheckBox.isChecked()
                    && ContactName.getText().toString().isEmpty() )
            //checking is ContactName entered when scheduletype is normal and set for unknown
            {
                showSnackBar("Enter Group or Contact name", R.color.red);
            }
            else if (totalMedia==0 && messageEB.getText().toString().isEmpty())
            {
                showSnackBar("Enter a message or select a media.", R.color.red);
            }
            else if (!isTest && dateTBS.equals(getString(R.string.selectDateScheduleText))  )
            {
                showSnackBar("Select Date .", R.color.red);

            }else if(!isTest && timeTBS.equals(getString(R.string.selectTimeScheduleText)))
            {
                showSnackBar("Select Time.", R.color.red);
            }

            else if (!isTest &&  dateDiff<0)
            {

                showSnackBar("Select a higher Date",R.color.red);


            }
            else if (!isTest && (now.get(Calendar.HOUR_OF_DAY)>selectedHour) && (now.get(Calendar.DAY_OF_MONTH)==day) )
            {
                showSnackBar("You can't set time of past",R.color.red);
            }else if (!isTest && now.get(Calendar.HOUR_OF_DAY)==selectedHour && now.get(Calendar.MINUTE)>selectedMin ){
                showSnackBar("You can't set time of past",R.color.red);
            }else if (messageEB.getText().toString().isEmpty() && unknowPersonCheckBox.isChecked())
            {
                showSnackBar("Type a message",R.color.red);
            }
            else {
                if (isTest)
                {
                    setNewAlarmTask( (AlarmManager) getSystemService(ALARM_SERVICE),Calendar.getInstance(),contactName);
                }else {

                    if (sp.getBoolean(getString(R.string.dontShowRecheckScheduleTag),false))
                    {
                        saveInDatabase(now,0,contactName); /* issave=0 representing that saveInDatabase() is calling from save button
                            which makes isdreft false*/
                    }
                    else
                    {

                        String packageName=getPackageName();
                        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

                        if (!pm.isIgnoringBatteryOptimizations(packageName))
                        {

                            batterOPLADIa.setVisibility(View.VISIBLE);
                        }else {
                            batterOPLADIa.setVisibility(View.GONE);
                        }
                        recheckDia.show();
                    }

                }











            }

             /*   // info https://stackoverflow.com/questions/28299741/send-text-and-image-to-specific-contact-whatsapp

                String number = whatsappNumber.getText().toString().replace("+", "");
                number = "91" + number;
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                intentShareFile.setType("*");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, mediaPickerData.getData());
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                        "Share");
                String message=messageEB.getText().toString();
                intentShareFile.putExtra(Intent.EXTRA_TEXT, message+"    ");
                intentShareFile.putExtra("jid", number + "@s.whatsapp.net");
                intentShareFile.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(intentShareFile, "Share"));*/

        }




    }
    void showSelectedFile()
    {
        LinearLayoutManager horizontalLM=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager horizontalLM2=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager horizontalLM3=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager horizontalLM4=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        BottomSheetDialog bsd=new BottomSheetDialog(this);

        View v=getLayoutInflater().inflate(R.layout.schedule_selected_mediafile,null);
        RecyclerView
                imageRV=v.findViewById(R.id.imageRV),
                videoRV=v.findViewById(R.id.recycleVideo),
                audioRecycle=v.findViewById(R.id.audioRecycle),
                docRV=v.findViewById(R.id.docRecycle);
        LinearLayout
                imageLA=v.findViewById(R.id.sch_selected_ImageLA),
                videoLA=v.findViewById(R.id.sch_selected_Video),
                audioLA=v.findViewById(R.id.sch_selected_audio),
                docLA=v.findViewById(R.id.sch_selected_document),
                noMediaLA=v.findViewById(R.id.noMediaLA),
                selectedMeidaLA=v.findViewById(R.id.selectedMeidaLA);

        TextView deleteAll=v.findViewById(R.id.deleteAll);
        ImageButton
                imageD=v.findViewById(R.id.deleteimageAll),
                deleteVideoAll=v.findViewById(R.id.deleteVideoAll),
                deleteAudioAll=v.findViewById(R.id.deleteAudioAll),
                deleteDocAll=v.findViewById(R.id.deleteDocAll);



        if (pickedImage.size()>0)
        {
            imageLA.setVisibility(View.VISIBLE);
            imageRV.setLayoutManager(horizontalLM);
            imageRV.setAdapter(setAdapter(pickedImage,R.layout.media_image_recycleview,0,imageLA,selectedMeidaLA,noMediaLA,deleteAll,imageRV));
        }
        if (pickedVideo.size()>0)
        {
            videoRV.setLayoutManager(horizontalLM2);
            videoLA.setVisibility(View.VISIBLE);
            videoRV.setAdapter(setAdapter(pickedVideo,R.layout.media_video_recycleview,1,videoLA, selectedMeidaLA, noMediaLA, deleteAll, videoRV));
        }
        if (pickedAudio.size()>0)
        {
            audioLA.setVisibility(View.VISIBLE);
            audioRecycle.setLayoutManager(horizontalLM3);
            audioRecycle.setAdapter(setAdapter(pickedAudio,R.layout.media_audio_recycleview,2,audioLA, selectedMeidaLA, noMediaLA, deleteAll, audioRecycle));
        }
        if (pickedDoc.size()>0)
        {
            docLA.setVisibility(View.VISIBLE);
            docRV.setLayoutManager(horizontalLM4);
            docRV.setAdapter(setAdapter(pickedDoc,R.layout.media_document_recycleview,3,docLA, selectedMeidaLA, noMediaLA, deleteAll, docRV));
        }
        ifNoFile(selectedMeidaLA,noMediaLA,deleteAll);
        imageD.setOnClickListener(view -> {
            pickedImage.clear();
            imageLA.setVisibility(View.GONE);
            ifNoFile(selectedMeidaLA,noMediaLA,deleteAll);
        });
        deleteVideoAll.setOnClickListener(view -> {
            pickedVideo.clear();
            videoLA.setVisibility(View.GONE);
            ifNoFile(selectedMeidaLA,noMediaLA,deleteAll);
        });
        deleteAudioAll.setOnClickListener(view -> {
            pickedAudio.clear();
            audioLA.setVisibility(View.GONE);
            ifNoFile(selectedMeidaLA,noMediaLA,deleteAll);
        });
        deleteDocAll.setOnClickListener(view -> {
            pickedDoc.clear();
            docLA.setVisibility(View.GONE);
            ifNoFile(selectedMeidaLA,noMediaLA,deleteAll);
        });

        deleteAll.setOnClickListener(view -> {
            pickedImage.clear();
            pickedVideo.clear();
            pickedAudio.clear();
            pickedDoc.clear();
            ifNoFile(selectedMeidaLA,noMediaLA,deleteAll);
        });




        bsd.setContentView(v);
        bsd.show();//todo set selected file in bottom notification and set from editing
    }

    private void ifNoFile(LinearLayout selectedMeidaLA, LinearLayout noMediaLA, TextView deleteAll) {
        totalMedia=pickedAudio.size()+pickedVideo.size()+pickedImage.size()+pickedDoc.size();
        mediaCount.setText(String.valueOf(totalMedia));
        if (totalMedia==0)
        {
            noMediaLA.setVisibility(View.VISIBLE);
            selectedMeidaLA.setVisibility(View.GONE);
            deleteAll.setVisibility(View.GONE);
        }else {
            selectedMeidaLA.setVisibility(View.VISIBLE);
            deleteAll.setVisibility(View.VISIBLE);
        }

    }

    RecyclerView.Adapter setAdapter(ArrayList<Uri> uris, int resourceLayout, int recycleType, LinearLayout layout, LinearLayout selectedMeidaLA, LinearLayout noMediaLA, TextView deleteAll, RecyclerView recyclerView) // recycleType 0=image,1=video,2=audio,3 =doc
    {


        return new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(resourceLayout,parent,false)) {
                    @NonNull
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position)
            {


                if (uris.size()!=0)
                {


                    int p = uris.size() - 1 - position;
                    ImageView im;
                    ImageView delete;
                    LinearLayout mediaRecycleLayout;
                    if (recycleType == 0)//image
                    {
                        im = h.itemView.findViewById(R.id.imageImage);
                        delete = h.itemView.findViewById(R.id.deleteSticker);
                        mediaRecycleLayout = h.itemView.findViewById(R.id.mediaImageRecycleLayout);
                        ConstraintLayout imageMain = h.itemView.findViewById(R.id.imageMain);
                        Glide.with(schedule_sms.this).load(uris.get(p)).into(im); // setting image

                        imageMain.setOnClickListener(view -> {
                            i("on mediaLayout click");

                        });
                        im.setOnClickListener(view -> {
                            Intent in=new Intent(schedule_sms.this, playPrivateMedia.class);
                            in.putExtra("mediaType",recycleType);
                            in.putExtra("uri",uris.get(p).toString());
                            in.putExtra(getString(R.string.isFromOtherScreenPlayPrivateMedia),true);

                            startActivity(in);
                        });
                    }
                    else if (recycleType == 1) //video
                    {
                        im = h.itemView.findViewById(R.id.videoImage);
                        delete = h.itemView.findViewById(R.id.deleteVideo);
                        mediaRecycleLayout = h.itemView.findViewById(R.id.mediaVideoRecycleLayout);
                        Glide.with(schedule_sms.this).load(uris.get(p)).into(im); // setting image

                        im.setOnClickListener(view -> {
                            Intent in=new Intent(schedule_sms.this, playPrivateMedia.class);
                            in.putExtra("mediaType",recycleType);
                            in.putExtra("uri",uris.get(p).toString());
                            in.putExtra(getString(R.string.isFromOtherScreenPlayPrivateMedia),true);

                            startActivity(in);
                        });
                    } else if (recycleType == 2) //audio
                    {
                        TextView audioName = h.itemView.findViewById(R.id.audioName);
                        audioName.setText(getFileName(uris.get(p)));
                        delete = h.itemView.findViewById(R.id.deleteAudio);
                        mediaRecycleLayout = h.itemView.findViewById(R.id.mediaAudioRecycleLayout);
                    }
                    else
                    {
                        i("documents show selected files");
                        TextView docName = h.itemView.findViewById(R.id.documentsName);
                        docName.setText(getFileName(uris.get(p)));
                        delete = h.itemView.findViewById(R.id.deleteDoc);
                        mediaRecycleLayout = h.itemView.findViewById(R.id.media_document_layout);
                    }

                    mediaRecycleLayout.setOnClickListener(view -> {

                        i("mediaRecycleLayout.setOnClickListener");
                        GetInfo getInfo=new GetInfo();
                        String mime = getInfo.getMimeType(schedule_sms.this,uris.get(p));


                        if (recycleType>1 && recycleType!=5 )
                        {

                            GetInfo gi=new GetInfo();
                            Uri shareableUri=uris.get(p);

                            if(uris.get(p).getPath().contains("com.affixstudio.gbwhats"))
                            {
                                shareableUri=gi.getShareableUri(uris.get(p),schedule_sms.this);
                            }
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(shareableUri,mime);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(intent,"Select app" ));

                        }else
                        {
                            i("on mediaLayout click");
                            Intent in=new Intent(schedule_sms.this, playPrivateMedia.class);
                            in.putExtra("mediaType",recycleType);
                            in.putExtra("uri",uris.get(p).toString());
                            in.putExtra(getString(R.string.isFromOtherScreenPlayPrivateMedia),true);

                            startActivity(in);

                        }
                    });

                    LinearLayout finalMediaRecycleLayout = mediaRecycleLayout;
                    int finalp = p;
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            finalMediaRecycleLayout.setVisibility(View.GONE);

                            RecyclerView r = recyclerView;
                            if (recycleType == 0) //image
                            {
                                pickedImage.remove(uris.get(finalp));
                                r.setAdapter(setAdapter(pickedImage, resourceLayout, recycleType, layout, selectedMeidaLA, noMediaLA, deleteAll, recyclerView));
                                if (pickedImage.size() == 0) {
                                    layout.setVisibility(View.GONE);
                                }
                            } else if (recycleType == 1) //video
                            {
                                pickedVideo.remove(uris.get(finalp));
                                r.setAdapter(setAdapter(pickedVideo, resourceLayout, recycleType, layout, selectedMeidaLA, noMediaLA, deleteAll, recyclerView));
                                if (pickedVideo.size() == 0) {
                                    layout.setVisibility(View.GONE);
                                }
                            } else if (recycleType == 2) //audio
                            {
                                pickedAudio.remove(uris.get(finalp));
                                r.setAdapter(setAdapter(pickedAudio, resourceLayout, recycleType, layout, selectedMeidaLA, noMediaLA, deleteAll, recyclerView));

                                if (pickedAudio.size() == 0) {
                                    layout.setVisibility(View.GONE);
                                }
                            }
                            else
                            {
                                pickedDoc.remove(uris.get(finalp));
                                r.setAdapter(setAdapter(pickedDoc, resourceLayout, recycleType, layout, selectedMeidaLA, noMediaLA, deleteAll, recyclerView));

                                if (pickedDoc.size() == 0) {
                                    layout.setVisibility(View.GONE);
                                }
                            }
                            if (isEdting && uris.size()!=0) // delete the file from GBwhats folder
                            {
                                new File(uris.get(finalp).getPath()).delete();
                                i("deleted");
                            }
                            ifNoFile(selectedMeidaLA, noMediaLA, deleteAll);


                        }
                    });





                }



            }

            @Override
            public int getItemCount() {
                return uris.size();
            }
        };
    }


    private void makeAllEmpty() {
        Log.i(TAG,"makeAllEmpty");

        ContactName.setText("");
        whatsappNumber.setText("");
        messageEB.setText("");
        dateTV.setText(getString(R.string.selectDateScheduleText));
        time.setText(getString(R.string.selectTimeScheduleText));
        pickedImage.clear();
        pickedVideo.clear();
        pickedAudio.clear();
        pickedDoc.clear();

        pickedImageP.clear();
        pickedVideoP.clear();
        pickedAudioP.clear();
        pickedDocP.clear();

        totalMedia=pickedAudio.size()+pickedVideo.size()+pickedImage.size()+pickedDoc.size();
        mediaCount.setText(String.valueOf(totalMedia));

    }

    @SuppressLint("SetTextI18n")
    private void callDataFromDb() {


        Log.i(TAG,"callDataFromDb isAccessibilityServiceEnabled "+isAccessibilityServiceEnabled(schedule_sms.this, whatsappAccessibility.class));

        try {
            // scheduleType=0;
            if (!isAccessibilityServiceEnabled(schedule_sms.this, whatsappAccessibility.class)) {  //checking is accessibility service enabled

                findViewById(R.id.permissionLayout).setVisibility(View.VISIBLE);
                schiduleRecycler.setVisibility(View.GONE);
                deleteAllSchedule.setVisibility(View.GONE);
                findViewById(R.id.noHistoryTB).setVisibility(View.GONE);
                editSchedule.setVisibility(View.GONE);
                i("editSchedule.setVisibility(View.GONE)3");
                mainLayout.setVisibility(View.GONE);
                i("mainLayout.setVisibility(View.GONE)3");

            }
            else {
                findViewById(R.id.permissionLayout).setVisibility(View.GONE);

                clearDataFromList();


                Cursor cursor = db.getWritableDatabase().rawQuery("Select * from " + getString(R.string.schiduleTableName) /*table and database name is same*/
                        , null);
                setDataFromCursor(cursor);

                if (names.isEmpty())
                {
                    findViewById(R.id.noHistoryTB).setVisibility(View.VISIBLE);
                    schiduleRecycler.setVisibility(View.GONE);
                    deleteAllSchedule.setVisibility(View.GONE);
                }else {
                    findViewById(R.id.noHistoryTB).setVisibility(View.GONE);
                    schiduleRecycler.setVisibility(View.VISIBLE);
                    deleteAllSchedule.setVisibility(View.VISIBLE);
                    divideHistory(indentifierForMainList);

                }


                mainLayout.setVisibility(View.VISIBLE);

                i("mainLayout.setVisibility(View.VISIBLE)");
                editSchedule.setVisibility(View.GONE);
                i("editSchedule.setVisibility(View.GONE)4");





               /* if (names.isEmpty() && message.isEmpty() && date.isEmpty() && WANumebr.isEmpty() && firstTime == 1) // no history
                {
                    findViewById(R.id.noHistoryTB).setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.VISIBLE);
                    schiduleRecycler.setVisibility(View.GONE);




                    editSchedule.setVisibility(View.GONE);

                    Log.i("layoutshow", "isEditing");

                } else if (names.isEmpty() && message.isEmpty() && date.isEmpty() && WANumebr.isEmpty())
                {
                    findViewById(R.id.noHistoryTB).setVisibility(View.GONE);
                    schiduleRecycler.setVisibility(View.GONE);
                    editSchedule.setVisibility(View.VISIBLE);

                    Log.i("layoutshow", "not isEditing");
                } else {

                    showRecycler();


                }*/



                //Log.i("layoutshow", "sendThrough "+sendThrough.get(2));



                firstTime = 1;

                setIntentChat();

                cursor.close();

                screenName.setText(getString(R.string.scheduleScreenName));


            }


            String packageName=getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

            if (!pm.isIgnoringBatteryOptimizations(packageName))

            {
              findViewById(R.id.betteryOpLA).setVisibility(View.VISIBLE);
              turnOFFBattery.setTextColor(ContextCompat.getColor(this,R.color.blue));
            }
            else
            {
                findViewById(R.id.betteryOpLA).setVisibility(View.GONE);
                turnOFFBattery.setTextColor(ContextCompat.getColor(this,R.color.black));
            }



        }catch (Exception e)
        {
            Log.i("callDataFromDb ",e.getMessage());
        }




    }

    private void clearDataFromList1() {

        if (!names1.isEmpty() || !message1.isEmpty() || !WANumebr1.isEmpty() || !date1.isEmpty() || !Opdate1.isEmpty() || !Optime1.isEmpty() ||
                !isDraft1.isEmpty()) // clearing duplicate data
        {

            id1.clear();
            names1.clear();
            message1.clear();
            WANumebr1.clear();
            date1.clear();
            Opdate1.clear();
            Optime1.clear();
            isDraft1.clear();
            CountryCode1.clear();
            sendThrough1.clear();
            imagesUri1.clear();
            videoUri1.clear();
            audioUri1.clear();
            docUri1.clear();
            state1.clear();

        }


    }
    private void clearDataFromList() {

        if (!names.isEmpty() || !message.isEmpty() || !WANumebr.isEmpty() || !date.isEmpty() || !Opdate.isEmpty() || !Optime.isEmpty() ||
                !isDraft.isEmpty()
                || !imagesUri.isEmpty() || !audioUri.isEmpty()|| !videoUri.isEmpty()||!docUri.isEmpty()|| !state.isEmpty()) // clearing duplicate data
        {

            id.clear();
            names.clear();
            message.clear();
            WANumebr.clear();
            date.clear();
            Opdate.clear();
            Optime.clear();
            isDraft.clear();
            CountryCode.clear();
            imagesUri.clear();
            videoUri.clear();
            audioUri.clear();
            docUri.clear();
            state.clear();
            sendThrough.clear();

        }


    }

    ArrayList <String> names1=new ArrayList<>();
    ArrayList <String> message1=new ArrayList<>();
    ArrayList <String> WANumebr1=new ArrayList<>();
    ArrayList <String> date1=new ArrayList<>();
    ArrayList<String> Opdate1=new ArrayList<>();
    ArrayList<String> Optime1=new ArrayList<>();
    ArrayList<Integer> isDraft1=new ArrayList<>();
    ArrayList<Integer> id1=new ArrayList<>();
    ArrayList<String> CountryCode1=new ArrayList<>();
    ArrayList<String> sendThrough1=new ArrayList<String>();
    ArrayList<String> state1=new ArrayList<>();
    ArrayList<String> imagesUri1=new ArrayList<>();
    ArrayList<String> videoUri1=new ArrayList<>();
    ArrayList<String> audioUri1=new ArrayList<>();
    ArrayList<String> docUri1=new ArrayList<>();
    private void divideHistory(String indentifier)
    {

        clearDataFromList1();



        for (int i=0;i<names.size();i++)
        {

            if (indentifier.equals("Status"))
            {
                if (names.get(i).equals(indentifier))
                {

                    names1.add(names.get(i));
                    message1.add(message.get(i));
                    WANumebr1.add(WANumebr.get(i));
                    date1.add(date.get(i));
                    Opdate1.add(Opdate.get(i));
                    Optime1.add(Optime.get(i));
                    isDraft1.add(isDraft.get(i));
                    id1.add(id.get(i));
                    CountryCode1.add(CountryCode.get(i));
                    sendThrough1.add(sendThrough.get(i));
                    state1.add(state.get(i));
                    imagesUri1.add(imagesUri.get(i));
                    videoUri1.add(videoUri.get(i));
                    audioUri1.add(audioUri.get(i));
                    docUri1.add(docUri.get(i));
                }

            }else if (indentifier.equals("normal"))
            {

                if (!names.get(i).equals("Status"))
                {
                    names1.add(names.get(i));
                    message1.add(message.get(i));
                    WANumebr1.add(WANumebr.get(i));
                    date1.add(date.get(i));
                    Opdate1.add(Opdate.get(i));
                    Optime1.add(Optime.get(i));
                    isDraft1.add(isDraft.get(i));
                    id1.add(id.get(i));
                    CountryCode1.add(CountryCode.get(i));
                    sendThrough1.add(sendThrough.get(i));
                    state1.add(state.get(i));
                    imagesUri1.add(imagesUri.get(i));
                    videoUri1.add(videoUri.get(i));
                    audioUri1.add(audioUri.get(i));
                    docUri1.add(docUri.get(i));
                }
            }else {
                names1.add(names.get(i));
                message1.add(message.get(i));
                WANumebr1.add(WANumebr.get(i));
                date1.add(date.get(i));
                Opdate1.add(Opdate.get(i));
                Optime1.add(Optime.get(i));
                isDraft1.add(isDraft.get(i));
                id1.add(id.get(i));
                CountryCode1.add(CountryCode.get(i));
                sendThrough1.add(sendThrough.get(i));
                state1.add(state.get(i));
                imagesUri1.add(imagesUri.get(i));
                videoUri1.add(videoUri.get(i));
                audioUri1.add(audioUri.get(i));
                docUri1.add(docUri.get(i));
            }






        }


        refreshAdepter(names1,message1,date1,WANumebr1,isDraft1,id1,sendThrough1,state1,imagesUri1,videoUri1,audioUri1,docUri1, stateInt);
    }

    private void setDataFromCursor(Cursor cursor) {

        Log.i(TAG,"callDataFromDb cursor "+cursor.getCount());

        while (cursor.moveToNext()) {





            id.add(cursor.getInt(0));
            names.add(cursor.getString(1));
            message.add(cursor.getString(2));
            WANumebr.add(cursor.getString(3));
            date.add(cursor.getString(4));
            Opdate.add(cursor.getString(5));
            Optime.add(cursor.getString(6));
            isDraft.add(cursor.getInt(7));
            CountryCode.add(cursor.getString(8));
            sendThrough.add(cursor.getString(9));
            state.add(cursor.getString(10));
            imagesUri.add(cursor.getString(11));
            videoUri.add(cursor.getString(12));
            audioUri.add(cursor.getString(13));
            docUri.add(cursor.getString(14));




        }
    }

    private void refreshAdepter(
            ArrayList<String> names,
            ArrayList<String> message,
            ArrayList<String> date,
            ArrayList<String> WANumebr,

            ArrayList<Integer> isDraft,
            ArrayList<Integer> id1,
            ArrayList<String> sendThrough,
            ArrayList<String> state1, ArrayList<String> imagesUri1, ArrayList<String> videoUri1, ArrayList<String> audioUri1
            , ArrayList<String> docUri1, int state)
    {

        Adepter adepter = new Adepter( names, message, date, WANumebr,  isDraft, sendThrough,id1
                ,state1,imagesUri1,videoUri1,audioUri1,docUri1,this,d,editSchedule,this,state,Opdate1,Optime1);
        schiduleRecycler.removeAllViews();
        schiduleRecycler.setAdapter(adepter);
        i("adepter refreshed");

    }

    private void showRecycler() {

        Log.i(TAG, "message.size-" + message.size()+" names.size-" + names.size()
                +"date.size-" + date.size()+"WANumebr.size-" + WANumebr.size());

        //Log.i("Names Length", "" + names.size());

        schiduleRecycler.setVisibility(View.VISIBLE);
        deleteAllSchedule.setVisibility(View.VISIBLE);
        findViewById(R.id.noHistoryTB).setVisibility(View.GONE);
        editSchedule.setVisibility(View.GONE);
        i("editSchedule.setVisibility(View.GONE)5");

        Log.i("layoutshow", "else");
        if (isSave == 0) {
            schiduleRecycler.setTranslationX(1200);
            schiduleRecycler.animate().translationXBy(-1200).setDuration(200);
            schiduleRecycler.setVisibility(View.VISIBLE);

        } else {
            schiduleRecycler.setVisibility(View.VISIBLE);
            deleteAllSchedule.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(TAG,"onPause");
    }
    void i(String s){
        Log.i("schedule_sms",s);
    }

    boolean testInt=true;
    void setIntentChat()
    {
        if (getIntent()!=null && firstTimeGettingIntent==1 && testInt) {

            i("getIntent()!=null");
            if (getIntent().getIntExtra("fromChat", 0) == 1) {



                unknowPersonCheckBox.setChecked(true);
                isSave = 1;
                isEdting = false;

                schiduleRecycler.setVisibility(View.GONE);
                deleteAllSchedule.setVisibility(View.GONE);
                findViewById(R.id.noHistoryTB).setVisibility(View.GONE);
                findViewById(R.id.permissionLayout).setVisibility(View.GONE);
                mainLayout.setVisibility(View.GONE);
                i("mainLayout.setVisibility(View.GONE)4");
                editSchedule.setVisibility(View.VISIBLE);
                i("editSchedule.setVisibility(View.VISIBLE)3");


                if (getIntent().getStringExtra("whatsappNumber")!=null)
                {
                    whatsappNumber.setText("" + getIntent().getStringExtra("whatsappNumber"));
                }
                if (getIntent().getStringExtra("Directmessage")!=null)
                {
                    messageEB.setText("" + getIntent().getStringExtra("Directmessage"));
                }


                if (getIntent().getStringExtra("sendThrouth")!=null)
                {
                    if (getIntent().getStringExtra("sendThrouth").equals("com.whatsapp")) {
                        RbWhats.setChecked(true);
                    } else if (getIntent().getStringExtra("sendThrouth").equals("com.whatsapp.w4b")) {
                        RbWB.setChecked(true);
                    }
                }


                ccp.setVisibility(View.GONE);
                TextCountryCode.setVisibility(View.VISIBLE);

                if (getIntent().getStringExtra("countryCode")!=null)
                {
                    TextCountryCode.setText("+" + getIntent().getStringExtra("countryCode"));
                }


                // findViewById(R.id.space).setVisibility(View.VISIBLE);
                firstTimeGettingIntent = 0;
                testInt=false;
            }
        }
    }

    int firstTime=0;
    private void saveInDatabase(Calendar now,int issave,String contactName) /*indicating - function is called from saved button or back pressed. 1 = draft and 0=saved*/  {

        Log.i(TAG,"saveInDatabase"); // todo schedule is not canceled
        try {


            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy", Locale.getDefault());

            String datevalue= sdf.format(new Date());
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            String number=ContactName.getText().toString(); //number key is used for both unknown person's number or Contact Name


            i("unknowPersonCheckBox "+unknowPersonCheckBox.isChecked());
            if (scheduleType==2 && unknowPersonCheckBox.isChecked()) // when schedule is for unknown person
            {
                number=whatsappNumber.getText().toString();
            }else if (scheduleType==1 ) // when schedule type is status set number = Status
            {
                number="Status";
            }


            i("saveInDatabase number = "+number);





            if (isEdting && issave==0) //when editing an existing schedule and saved
            {

                Log.i("savingSchedule","isEdting && issave==0");

                deleteOldAlarm(ContactName.getText().toString());
                if (isEdited()) // when user changed the data


                {  // checking that isdata changed? then have to update pending intent
                    i("deleteing when editing and saved");



                }


                int p=position+1-d;

                // Toast.makeText(schedule_sms.this, "success"+month+now.get(Calendar.HOUR_OF_DAY) , Toast.LENGTH_SHORT).show();

                updateOldData(datevalue,number,issave,p); //updating old data when editing
                setNewAlarmTask(alarmManager,now,contactName); // setting new task




            }else if (isEdting && isNotEmpty())//when editing an existing schedule and not saved
            {
                int p=position+1-d;
                Log.i("savingSchedule","isEdting && isNotEmpty()");
                updateOldData(datevalue,number,issave,p);


            } else if (issave==1 && isNotEmpty()) //when not editing an existing schedule and not saved

            {
                Log.i("savingSchedule","issave==1 && isNotEmpty()");
                saveTask(datevalue,alarmManager,now, issave,contactName);//putting value in database, setting alarmManager


            }else if (issave==0) //when creating new  schedule and  saved
            {
                Log.i("savingSchedule","issave==0");
                saveTask(datevalue,alarmManager,now, issave,contactName);
            }

            //callDataFromDb();//to show in recycle view
            //  makeAllEmpty(); //making all filed empty
        }catch (Exception e)

        {
            Log.e("saveInDatabase",e.getMessage());
        }




    }

    private boolean isEdited() {
        return (!Opdate1.get(position).equals(dateTV.getText().toString())) || (!Optime1.get(position).equals(time.getText().toString())) ||
                (!message1.get(position).equals(messageEB.getText().toString()))
                || (!WANumebr1.get(position).equals(whatsappNumber.getText().toString())) ||
                (!sendThrough1.get(position).equals(sendto)) || pickedDocP.equals(pickedDoc) || pickedAudioP.equals(pickedAudio)
                ||pickedVideoP.equals(pickedVideo) || pickedImageP.equals(pickedImage);
    }

    @SuppressLint("StaticFieldLeak")
    private void saveTask(String datevalue, AlarmManager alarmManager, Calendar now, int issave, String contactName)

    {
        try {
            final long[] r = {0};

            ProgressDialog pd=new ProgressDialog(schedule_sms.this);
            pd.setMessage("Saving..");



            new AsyncTask<String, String, String>() {
                @Override
                protected void onPreExecute() {
                    pd.show();
                    Log.i(TAG,"saveTask");
                    super.onPreExecute();
                }

                @Override
                protected String doInBackground(String... strings) {
                    ContentValues contentValues=new ContentValues();

                    String stringName="Unknown";

                    if (!unknowPersonCheckBox.isChecked() &&
                            scheduleType!=1 )
                    {
                        stringName=ContactName.getText().toString();

                    }else if (scheduleType==1)
                    {
                        stringName="Status";
                    }

                    contentValues.put("Name",stringName); // name means scheduletype

                    contentValues.put("Message",messageEB.getText().toString());
                    contentValues.put("Date",datevalue);
                    contentValues.put("Number",whatsappNumber.getText().toString());

                    contentValues.put("OpDate",dateTBS);
                    contentValues.put("OpTime",timeTBS);
                    contentValues.put("SendThrough",sendto);




                    String Path= sst.makeScheduleMediaDirectory()+""; // getting destination folder means GBWhats

                    //****************   image    ***********************************************
                    StringBuilder pickedImageUriSB=new StringBuilder();

                    if (pickedImage.size()>1)
                    {




                                ArrayList<Uri> uris=new ArrayList<>(pickedImage);

                                i("uris size "+uris.size());
                                for (Uri uri:uris)
                                {
                                    setStingBuilder(pickedImageUriSB,uri,Path);

                                }
                                // i("pickedImageUriSB "+pickedImageUriSB.toString());
                                contentValues.put("ImageURIs",pickedImageUriSB.toString());

                                i("contentValues ImageURIs "+contentValues.get("ImageURIs"));

                    }
                    else if (pickedImage.size()!=0)
                    {
                        i("ImageURIs,pickedImageUriSB.toString()");
                        //pickedImageUriSB.append(saveFileIntoFolder(pickedImage.get(0), Path).toURI()+"").append("*");
                        setStingBuilder(pickedImageUriSB,pickedImage.get(0),Path);
                        contentValues.put("ImageURIs",pickedImageUriSB.toString());

                    }

                    //****************   video    ***********************************************
                    StringBuilder pickedVideoUriSB=new StringBuilder();
                    if (pickedVideo.size()>1)
                    {



                                ArrayList<Uri> uris=new ArrayList<>(pickedVideo);

                                i("uris size "+uris.size());
                                for (Uri uri:uris)
                                {
                                    setStingBuilder(pickedVideoUriSB,uri,Path);

                                }
                                contentValues.put("VideoURIs",pickedVideoUriSB.toString());



                    }else if (pickedVideo.size()!=0)
                    {
                        setStingBuilder(pickedVideoUriSB,pickedVideo.get(0),Path);

                        contentValues.put("VideoURIs",pickedVideoUriSB.toString());
                        i("pickedVideo.get(0) "+pickedVideo.get(0)+"   pickedVideoUriSB "+pickedVideoUriSB.toString());

                    }


                    //****************   audio    ***********************************************
                    StringBuilder pickedAudioUriSB=new StringBuilder();
                    if (pickedAudio.size()>1)
                    {



                                ArrayList<Uri> uris=new ArrayList<>(pickedAudio);

                                i("uris size "+uris.size());
                                for (Uri uri:uris)
                                {
                                    setStingBuilder(pickedAudioUriSB,uri,Path);

                                }
                                contentValues.put("AudioURIs",pickedAudioUriSB.toString());




                    }else if (pickedAudio.size()!=0){

                        setStingBuilder(pickedAudioUriSB,pickedAudio.get(0),Path);
                        contentValues.put("AudioURIs",pickedAudioUriSB.toString());
                    }



                    //****************   Doc    ***********************************************
                    StringBuilder pickedDocUriSB=new StringBuilder();
                    if (pickedDoc.size()>1)
                    {



                                ArrayList<Uri> uris=new ArrayList<>(pickedDoc);

                                i("uris size "+uris.size());
                                for (Uri uri:uris)
                                {
                                    setStingBuilder(pickedDocUriSB,uri,Path);

                                }
                                contentValues.put("DocURIs",pickedDocUriSB.toString());




                    }else if (pickedDoc.size()!=0)
                    {

                        setStingBuilder(pickedDocUriSB,pickedDoc.get(0),Path);
                        contentValues.put("DocURIs",pickedDocUriSB.toString());
                    }












                    if (issave==0) // 0 = saved
                    {
                        contentValues.put("isDraft",1); // 1 = not draft
                        contentValues.put("State","Pending");
                    }else
                    {
                        contentValues.put("isDraft",0); // 0 = not draft
                        contentValues.put("State","");
                    }

                    int code=91;

//        if (ccp.isShown())
//        {
//            code=Integer.parseInt(ccp.getSelectedCountryCode().replace("+",""));
//        }
//        else
//        {
//            code=Integer.parseInt(TextCountryCode.getText().toString().replace("+",""));
//        }
//
//        if(unknowPersonCheckBox.isChecked())
//        {
//            contentValues.put("TextCountryCode",code);
//        }



                    if(unknowPersonCheckBox.isChecked())
                    {
                        if (ccp.isShown())
                        {
                            code=Integer.parseInt(ccp.getSelectedCountryCode().replace("+",""));
                        }
                        else
                        {
                            code=Integer.parseInt(TextCountryCode.getText().toString().replace("+",""));
                        }
                        contentValues.put("TextCountryCode",code);
                    }

                    i("  content values imageuris "+contentValues.get("ImageURIs"));
                     r[0] =db.getWritableDatabase().insert(getString(R.string.schiduleTableName),null,contentValues);




                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    if (r[0] != -1)
                    {

                        if (issave==0) // means saved
                        {






                            setNewAlarmTask(alarmManager,now,contactName) ;






                        }
                        clearDataFromList();
                        Cursor cursor = db.getWritableDatabase().rawQuery("Select * from " + getString(R.string.schiduleTableName) /*table and database name is same*/
                                , null);
                        setDataFromCursor(cursor);
                        refreshAdepter(names,message,date,WANumebr,isDraft,id,sendThrough, state, imagesUri, videoUri, audioUri,docUri,stateInt );
                        showRecycler();
                        callDataFromDb();
                        makeAllEmpty();
                        pd.dismiss();
                    }else {
                        showSnackBar("Something went wrong",R.color.red);
                        pd.dismiss();
                    }
                    super.onPostExecute(s);
                }
            }.execute();




        }catch (Exception e)
        {

            Log.e("schedule_sms",e.getMessage());
        }
    }



    private boolean isNotEmpty() {
        Log.i(TAG,"isNotEmpty");

        i("ContactName = "+ContactName.getText().toString()+"  !"+" whatsappNumber "
                +!whatsappNumber.getText().toString().isEmpty()+" messageEB "+!messageEB.getText().toString().isEmpty()
                +" time "+!time.getText().toString().equals(getString(R.string.selectTimeScheduleText))+" dateTV "+!dateTV.getText().toString().equals(getString(R.string.selectDateScheduleText)));

        return(!ContactName.getText().toString().isEmpty()
                || !whatsappNumber.getText().toString().isEmpty()
                || !messageEB.getText().toString().isEmpty()
                || !time.getText().toString().equals(getString(R.string.selectTimeScheduleText))
                || !dateTV.getText().toString().equals(getString(R.string.selectDateScheduleText))
                || !pickedImage.isEmpty()
                || !pickedVideo.isEmpty()
                || !pickedAudio.isEmpty()
                || !pickedDoc.isEmpty()
        );
    }



    @SuppressLint("StaticFieldLeak")
    private void setNewAlarmTask(AlarmManager alarmManager, Calendar now,String contactName)
    {

        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();


        try
        {





            //  Toast.makeText(schedule_sms.this, "success"+month+now.get(Calendar.HOUR_OF_DAY) , Toast.LENGTH_SHORT).show();

            Log.i(TAG,"setNewAlarmTask");
            Intent intent = new Intent(schedule_sms.this, scheduleBrodcust.class);

            //intent.setPackage(sendto);

            intent.putExtra("number", ccp.getSelectedCountryCode()+""+whatsappNumber.getText().toString()); i("whatsappNumber "+ccp.getSelectedCountryCode()+" "+whatsappNumber.getText().toString());
            intent.putExtra(Intent.EXTRA_TEXT, messageEB.getText().toString()); i("messageEB "+messageEB.getText().toString());

            intent.putExtra("message", messageEB.getText().toString());
            intent.putExtra("package", sendto);

            i("type "+ scheduleType);

            intent.putExtra("type", scheduleType);

            if (unknowPersonCheckBox.isChecked()) // when schedule is for unknown person
            {
                intent.putExtra("isUnknown", true);
            }
            i("send to "+sendto);


            i("scheduleType "+scheduleType);








            //#todo have to delete old pending intent when changing sendto

            new AsyncTask<String, String, String>()
            {
                ArrayList<Uri> totalData;
                @Override
                protected void onPreExecute(){
                     super.onPreExecute();
                }
                @Override
                protected String doInBackground(String... strings) {
                    totalData = new ArrayList<>(pickedImage);


                    totalData.addAll(pickedImage.size(),pickedVideo);
                    totalData.addAll(pickedImage.size()+pickedVideo.size(),pickedAudio);
                    totalData.addAll(pickedImage.size()+pickedVideo.size()+pickedAudio.size(),pickedDoc);

                    ArrayList<Uri> totalDataFromStorage = new ArrayList<>(); // data From Storage. To get uris of our storage



                    String path=sst.makeScheduleMediaDirectory()+"";
                    for (Uri uri:totalData)
                    {
                        totalDataFromStorage.add(getStorageURI(uri,path));

                    }




                    i("total data size "+totalDataFromStorage.size());
                    intent.putExtra("sendSimpleText", true);


                    if (totalDataFromStorage.size()>0)
                    {
                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,totalDataFromStorage);
                        intent.putExtra("sendSimpleText", false);
                    }




                    return null;
                }
                @Override
                protected void onPostExecute(String s)
                {


                    try {


                    int i=1;
                    if (!id1.isEmpty() && isEdting)
                    {
                        i=id1.get(position);

                        i(position+" Edit id1.getposition gen = "+i);

                    }else if (!id1.isEmpty())
                    {
                        setDataFromCursor(db.getinfo(1));
                        i("id.get(id.size()-1) "+id.get(id.size()-1));
                        i=id.get(id.size()-1);

                        i(position+"  id1.getposition gen = "+i);
                    }

                    if (isTest)
                    {
                        i= new Random().nextInt(1000);
                        intent.putExtra("isTest",true);
                    }
                    intent.putExtra("id", i);
                    intent.putExtra("name", Objects.requireNonNull(contactName));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(schedule_sms.this,i , intent, 0|PendingIntent.FLAG_IMMUTABLE);


                    long finalMillis=1000;

                    if (!isTest) // is the user testing the schedule?
                    {


                        // To detect mili second
                        String myDate = dateTV.getText() + " " + time.getText();
                        i(" dataTV "+dateTV.getText()+" time "+time.getText());

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sTimef = new SimpleDateFormat("HH:mm:ss");
                        Date date = null;
                        Date timeD = null;
                        Date simpleDate = null;
                        Date simpleTime = null;
                        try {
                            date = sdf.parse(dateTBS);
                            timeD = sTimef.parse(timeTBS);
                            simpleDate = sdf.parse(sdf.format(new Date()));
                            simpleTime = sTimef.parse(sTimef.format(new Date()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long DateMillis = date.getTime();
                        long timeMillis = timeD.getTime();

                        long simpleSumMili = simpleDate.getTime() + simpleTime.getTime();
                        long sumMinils = DateMillis + timeMillis;

                        long nowMillis = now.getTimeInMillis();
                        finalMillis = sumMinils - simpleSumMili - 1000;

        //                Log.i(TAG, simpleTime.getTime() + "  datemilis " + DateMillis + "  timeMilis " + timeMillis + "    " + simpleSumMili + "    " + finalMillis + "  " + " timeNow " + nowMillis + "  " + simpleDate + " timeD " + simpleTime);

                        // Toast.makeText(schedule_sms.this, "" + nowMillis + "    " + myDate, Toast.LENGTH_SHORT).show();


                    }

                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                            + finalMillis, pendingIntent);



                    if (isTest)
                    {

                        showSnackBar("Just wait a moment",R.color.primary); //todo was testing test scheduled, status testschedule not working and not comming back when testing normal

                    }
                    else
                    {

                        showSnackBar("Scheduled successfully",R.color.primary);
                        i("finalMillis "+finalMillis);



//                 if (mInterstitialAd != null)
//                 { // updated
//                     Toast.makeText(schedule_sms.this, "Scheduled successfully", Toast.LENGTH_LONG).show();
//                    mInterstitialAd.show(schedule_sms.this);
//                    loadInterstitial();
//                }

                    }
                    makeAllEmpty();

                    progressDialog.dismiss(); // todo change onAll clear storage path and also private media and status in download
                    }catch (Exception e)
                    {
                        progressDialog.dismiss();
                        Log.e(TAG,e.getMessage());
                    }
                    super.onPostExecute(s);
                }


            }.execute();







        }catch (Exception e)
        {
            Log.e("schedule_sms",e.getMessage());
            progressDialog.dismiss();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void updateOldData(String datevalue, String number, int issave, int p)

    {
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);


        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                progressDialog.show();
                super.onPreExecute();
            }

            @Override

            protected String doInBackground(String... strings) {
                i("not Empty "+isNotEmpty());

                Log.i(TAG,"updateOldData");

                int draft=0;
                String state="";

                if (issave==0) // means its saved
                {
                    draft=1;
                    state="Pending";

                }




                String Path= sst.makeScheduleMediaDirectory()+""; // getting destination folder means GBWhats

                //****************   image    ***********************************************
                StringBuilder pickedImageUriSB=new StringBuilder();

                i("pickedImage.size() "+pickedImage.size());
                if (pickedImage.size()>1)
                {


                            ArrayList<Uri> uris=new ArrayList<>(pickedImage);

                            for (Uri uri:uris)
                            {
                                setStingBuilder(pickedImageUriSB,uri,Path);


                            }


                }
                else if (pickedImage.size()!=0)
                {
                    setStingBuilder(pickedImageUriSB,pickedImage.get(0),Path);
                    // pickedImageUriSB.append(saveFileIntoFolder(pickedImage.get(0), Path).toURI()+"").append("*");
                }

                //****************   video    ***********************************************
                StringBuilder pickedVideoUriSB=new StringBuilder();
                if (pickedVideo.size()>1)
                {
                    ArrayList<Uri> uris=new ArrayList<>(pickedVideo);

                    for (Uri uri:uris)
                    {
                                setStingBuilder(pickedVideoUriSB,uri,Path);

                    }





                }else if (pickedVideo.size()!=0)
                {

                    setStingBuilder(pickedVideoUriSB,pickedVideo.get(0),Path);

                    i("pickedVideo.get(0) "+pickedVideo.get(0)+"   pickedVideoUriSB "+pickedVideoUriSB.toString());

                }

                //****************   audio    ***********************************************
                StringBuilder pickedAudioUriSB=new StringBuilder();
                if (pickedAudio.size()>1)
                {

                    ArrayList<Uri> uris=new ArrayList<>(pickedAudio);

                    for (Uri uri:uris)
                    {
                                setStingBuilder(pickedAudioUriSB,uri,Path);
                    }




                }else if (pickedAudio.size()!=0){

                    setStingBuilder(pickedAudioUriSB,pickedAudio.get(0),Path);
                }
                //****************   Doc    ***********************************************
                StringBuilder pickedDocUriSB=new StringBuilder();
                if (pickedDoc.size()>1)
                {
                    ArrayList<Uri> uris=new ArrayList<>(pickedDoc);

                    for (Uri uri:uris)
                    {
                                setStingBuilder(pickedDocUriSB,uri,Path);

                            }




                }else if (pickedDoc.size()!=0)
                {
                    setStingBuilder(pickedDocUriSB,pickedDoc.get(0),Path);

                }











                Log.i("UpdateMessage",messageEB.getText().toString());


                String code="";
                if (unknowPersonCheckBox.isChecked())
                {
                    if (ccp.isShown())
                    {
                        code=ccp.getSelectedCountryCode().replace("+","");
                    }else {
                        code=TextCountryCode.getText().toString().replace("+","");
                    }
                }



                String stringName="Unknown";

                if (!unknowPersonCheckBox.isChecked() )
                {
                    if (ContactName.getText().toString().isEmpty())
                    {
                        stringName="Not set";
                    }else {
                        stringName=ContactName.getText().toString();
                    }


                }else if (scheduleType==1)
                {
                    stringName="Status";
                }




                String s="UPDATE "+getString(R.string.schiduleTableName)+" SET Name='"+stringName
                        +"'," +
                        "Message='"+messageEB.getText().toString()+"',Number='"+number
                        +"',Date='"+datevalue+"',OpDate='"+dateTV.getText().toString().replace(" ","_")+
                        "',OpTime='"+time.getText().toString().replace(" ","_")+"',isDraft='"+draft+"',TextCountryCode='"+code+"',SendThrough='"+sendto+"',State='"+state+"'," +
                        "ImageURIs='"+pickedImageUriSB+"',VideoURIs='"+pickedVideoUriSB+"',AudioURIs='"+pickedAudioUriSB+"',DocURIs='"+pickedDocUriSB+"' WHERE _id="+id1.get(position);

                db.getWritableDatabase().execSQL(s);

                Log.i("UpdateMessage",timeTBS.replace(" ","_"));
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                callDataFromDb();

                progressDialog.dismiss();
                super.onPostExecute(s);
            }
        }.execute();

    }

    private void setStingBuilder(StringBuilder pickedUriSB, Uri uri,String Path)
    {
        File f=new File(Path,getFileName(uri));//todo path+"/"+getFileName(uri)
        if (!f.exists())// checking is the file already exist in the GBwhats
        {
            File file=saveFileIntoFolder(uri, Path);
            if (file!=null)
            {
                pickedUriSB.append(file.toURI()+"").append("*");
            }

        }else { // file already exist in the GBwhats
            pickedUriSB.append(f.toURI()+"").append("*");
        }
    }
    private Uri getStorageURI(Uri uri, String Path)
    {
        File f=new File(Path,getFileName(uri));//todo path+"/"+getFileName(uri)
        if (!f.exists())// checking is the file already exist in the GBwhats
        {
            File file=saveFileIntoFolder(uri, Path);
            if (file!=null)
            {
                return Uri.fromFile(file);
            }

        }else { // file already exist in the GBwhats
            return Uri.fromFile(f);
        }
        return Uri.fromFile(f);
    }


    @Override
    public void onBackPressed()
    {
        screenName.setText(getString(R.string.scheduleScreenName));


        if (editSchedule.isShown() && isEdting && isDraft1.get(position)==1  ) // 1 = draft true
        {
            Log.i(TAG,"editSchedule.isShown() && isEdting && isDraft.get(position)==1  ");
            callDataFromDb();
        }
        else if (editSchedule.isShown() && isNotEmpty() && !isTest)
        {
            Log.i(TAG,"editSchedule.isShown() && isNotEmpty()");
            saveInDatabase(Calendar.getInstance(Locale.getDefault()),1,ContactName.getText().toString());/* issave=1 representing that saveInDatabase() is calling from back button
                     which makes isdreft false//*/
        }
        else if (editSchedule.isShown()  && isTest)
        {

            callDataFromDb();


        } else if (editSchedule.isShown() && !isNotEmpty())
        {
            i("editSchedule.isShown() && !isNotEmpty() ");
            callDataFromDb();


        }else
        {

            Log.i(TAG,"onbackPressed else");
            super.onBackPressed();
            // startActivity(new Intent(schedule_sms.this,MainActivity.class) );
        }
    }

    void showBottomTutorialDialog()
    {

        String[] list=getResources().getStringArray(R.array.list);
        String[] detalisList=getResources().getStringArray(R.array.details);
        String[] usageList=getResources().getStringArray(R.array.usage);
        String[] ListVideoUrl=getResources().getStringArray(R.array.urlList);

        BottomSheetDialog bsd=new BottomSheetDialog(this);
        bsd.setCancelable(false);

        View v=getLayoutInflater().inflate(R.layout.bottom_recycle,null);

        TextView pageTitle=v.findViewById(R.id.pageTitle);
        pageTitle.setText(list[8]);


//        TextView details= (TextView)  v.findViewById(R.id.details);
//        details.setText(detalisList[8]);
//
//        TextView usage= (TextView)  v.findViewById(R.id.usage);
//        usage.setText(usageList[8]);



        ImageView icon=(ImageView) v.findViewById(R.id.screenIcon);
        icon.setImageResource(R.drawable.schedule_icon_20);


        v.findViewById(R.id.understood).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.dismiss();
                final String[] PERMISSIONS = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, 2);
                }
            }
        });

        bsd.setContentView(v);
        bsd.show();



    }



    @Override
    public void onItemEdit(int position, String title)
    {
        //https://stackoverflow.com/questions/62011832/how-to-set-the-visibility-of-a-view-after-recyclerview-becomes-empty

        isTest=false;
        i("ID of position "+id.get(position));
        i("WANumebr1 of position "+WANumebr1.get(position));

        int localScheduleType=0;


        isSave=1; // making it 1 to for no animation
        if (title.equals("Status")) // when status
        {
            localScheduleType=1;
            setEditLayoutForStatus();
        }else /*if (!WANumebr1.get(position).isEmpty() || names1.get(position).isEmpty() )*/ //todo set success when schedule is success and also confirm when  tested and set ASK ME BEFORE SEND
        {
            localScheduleType=2;
            setEditLayoutForNormal();

        }

        this.position=position;





        ccp.setVisibility(View.GONE);
        TextCountryCode.setVisibility(View.VISIBLE);
        //findViewById(R.id.space).setVisibility(View.VISIBLE);

        String cccode="+"+CountryCode1.get(position);
        if (CountryCode1.get(position).equals(""))
        {
            cccode="+91";
        }
        TextCountryCode.setText(cccode);
        isEdting=true;

        getMediaDataFromDatabase(); // getting data from database and setting to the list






        if (!sendThrough1.get(position).equals("com.whatsapp"))
        {
            RbWB.setChecked(true);
        }else {
            RbWhats.setChecked(true);
        }

        if (!message1.get(position).isEmpty())
        {
            messageEB.setText(message1.get(position));
        }
        if (!Opdate1.get(position).isEmpty())
        {
            dateTV.setText(Opdate1.get(position).replace("_"," "));
            opStingDate=Opdate1.get(position);
        }
        if (!Optime1.get(position).isEmpty())
        {
            time.setText(Optime1.get(position).replace("_"," "));

        }



        i(position+" names1.get(position) = "+names1.get(position).equals("Unknown")
                +" ! WANumebr1.get(position) = "+WANumebr1.get(position)
                +"! state1.get(position) "+state1.get(position).equals("")+" ! ");

        //setting layout for unknown or status or contact
        if (CountryCode1.get(position).equals("91")
                && !WANumebr1.get(position).equals("Status"))
        {
            ContactName.setText("");
            unknowPersonCheckBox.setChecked(true);
            whatsappNumber.setText(WANumebr1.get(position));

        }else if ( !names1.get(position).isEmpty()/*WANumebr1.get(position).isEmpty() */)
        {

            unknowPersonCheckBox.setChecked(false);
            if (/*!names1.get(position).isEmpty() &&*/ !names1.get(position).equals("Not set"))
            {
                ContactName.setText(names1.get(position));
            }
        }

        scheduleType=localScheduleType;

        //  schiduleRecycler.setVisibility(View.GONE);

//        editSchedule.setTranslationX(1200);
//        editSchedule.animate().translationXBy(-1200).setDuration(200);
//        editSchedule.setVisibility(View.VISIBLE);
        if (title.equals("Status")) // when status
        {
            chooseContactCard.setVisibility(View.GONE);
        }


    }


    void setParamsForAllDelete(int position)
    {
       // Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
        isTest=false;
        i("ID of position "+id.get(position));
        i("WANumebr1 of position "+WANumebr1.get(position));

        int localScheduleType=0;


        isSave=1; // making it 1 to for no animation
        if (names1.get(position).equals("Status")) // when status
        {
            localScheduleType=1;
          //  setEditLayoutForStatus();
        }else /*if (!WANumebr1.get(position).isEmpty() || names1.get(position).isEmpty() )*/ //todo set success when schedule is success and also confirm when  tested and set ASK ME BEFORE SEND
        {
            localScheduleType=2;
           // setEditLayoutForNormal();

        }

        this.position=position;





        ccp.setVisibility(View.GONE);
        TextCountryCode.setVisibility(View.VISIBLE);
        //findViewById(R.id.space).setVisibility(View.VISIBLE);

        String cccode="+"+CountryCode1.get(position);
        if (CountryCode1.get(position).equals(""))
        {
            cccode="+91";
        }
        TextCountryCode.setText(cccode);
        isEdting=true;

        getMediaDataFromDatabase(); // getting data from database and setting to the list






        if (!sendThrough1.get(position).equals("com.whatsapp"))
        {
            RbWB.setChecked(true);
        }else {
            RbWhats.setChecked(true);
        }

        if (!message1.get(position).isEmpty())
        {
            messageEB.setText(message1.get(position));
        }
        if (!Opdate1.get(position).isEmpty())
        {
            dateTV.setText(Opdate1.get(position).replace("_"," "));
            opStingDate=Opdate1.get(position);
        }
        if (!Optime1.get(position).isEmpty())
        {
            time.setText(Optime1.get(position).replace("_"," "));

        }



        i(position+" names1.get(position) = "+names1.get(position).equals("Unknown")
                +" ! WANumebr1.get(position) = "+WANumebr1.get(position)
                +"! state1.get(position) "+state1.get(position).equals("")+" ! ");

        //setting layout for unknown or status or contact
        if (CountryCode1.get(position).equals("91")
                && !WANumebr1.get(position).equals("Status"))
        {
            ContactName.setText("");
            unknowPersonCheckBox.setChecked(true);
            whatsappNumber.setText(WANumebr1.get(position));

        }else if ( !names1.get(position).isEmpty()/*WANumebr1.get(position).isEmpty() */)
        {

            unknowPersonCheckBox.setChecked(false);
            if (/*!names1.get(position).isEmpty() &&*/ !names1.get(position).equals("Not set"))
            {
                ContactName.setText(names1.get(position));
            }
        }

        scheduleType=localScheduleType;

        //  schiduleRecycler.setVisibility(View.GONE);

//        editSchedule.setTranslationX(1200);
//        editSchedule.animate().translationXBy(-1200).setDuration(200);
//        editSchedule.setVisibility(View.VISIBLE);
    }

    private void getMediaDataFromDatabase()
    {

        try {



                if (imagesUri1.get(position).length()!=0)
            {
                String[] strings =imagesUri1.get(position).split("\\*");
                for (String s:strings)
                {

                    File file=new File(new URI(s));
                    i("getMediaDataFromDatabase image file exist"+file.exists()+" "+s);
                    if (file.exists())
                    {

                        pickedImageP.add(Uri.parse(s));
                    }

                }
            }


            if (videoUri1.get(position).length()!=0)
            {
                String[] strings =videoUri1.get(position).split("\\*");
                i("getMediaDataFromDatabase strings video length "+strings.length);
                for (String s:strings)
                {
                    i("getMediaDataFromDatabase videoFile path "+s);
                    File file=new File(new URI(s));
                    if (file.exists())
                    {

                        pickedVideoP.add(Uri.parse(s));
                    }

                }
            }

            if (audioUri1.get(position).length()!=0)
            {
                String[] strings =audioUri1.get(position).split("\\*");
                for (String s:strings)
                {
                    File file=new File(new URI(s));
                    if (file.exists())
                    {
                        pickedAudioP.add(Uri.parse(s));
                    }

                }
            }

            if (docUri1.get(position).length()!=0)
            {
                String[] strings =docUri1.get(position).split("\\*");
                for (String s:strings)
                {
                    File file=new File(new URI(s));
                    if (file.exists())
                    {
                        pickedDocP.add(Uri.parse(s));
                    }

                }
            }

            pickedImage=pickedImageP;
            pickedVideo=pickedVideoP;
            pickedAudio=pickedAudioP;
            pickedDoc=pickedDocP;
            i("pickedAudio.size() "+pickedAudio.size()+" pickedVideo.size() "+pickedVideo.size()+" pickedImage.size() "+pickedImage.size()+" pickedDoc.size() "+pickedDoc.size());
            totalMedia=pickedAudio.size()+pickedVideo.size()+pickedImage.size()+pickedDoc.size();
            mediaCount.setText(String.valueOf(totalMedia));
        }catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
    }



    @SuppressLint("IntentReset")
    public void showPickContact()

    {
        Log.i(TAG,"showPickContact");


        spEdit.putString("pickedChatName","").putBoolean("gettingChatName",true).apply();
        Intent i=new Intent(Intent.ACTION_MAIN);
        i.setComponent(new ComponentName("com.whatsapp","com.whatsapp.HomeActivity"));
        isPickingContact=true;


        if (isInstalled(this,"com.whatsapp.w4b") && isInstalled(this,"com.whatsapp") )
        {

            new  AlertDialog.Builder(this).setMessage("Choose Contact from").setPositiveButton("WhatsApp", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int index) {
                    i.setComponent(new ComponentName("com.whatsapp","com.whatsapp.HomeActivity"));
                    startActivity(i);
                }
            }).setNegativeButton("WhatsApp Business", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int index) {
                    i.setComponent(new ComponentName("com.whatsapp.w4b","com.whatsapp.HomeActivity")); //#todo set proper whatsapp business home class name
                    startActivity(i);
                }
            }).show();


        }
        else if(isInstalled(this,"com.whatsapp.w4b"))
        {

            i.setComponent(new ComponentName("com.whatsapp.w4b","com.whatsapp.HomeActivity"));
            startActivity(i);

        }else if (isInstalled(this,"com.whatsapp"))
        {
            i.setComponent(new ComponentName("com.whatsapp","com.whatsapp.HomeActivity"));
            startActivity(i);

        }else {

            Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();

        }


    }





    ArrayList<Uri> pickedImage;
    ArrayList<Uri> pickedVideo;
    ArrayList<Uri> pickedAudio;
    ArrayList<Uri> pickedDoc;

    ArrayList<Uri> pickedImageP;
    ArrayList<Uri> pickedVideoP;
    ArrayList<Uri> pickedAudioP;
    ArrayList<Uri> pickedDocP;
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {




            Log.i(TAG,"onActivityResult");


            if(requestCode == CONTACT_PICKER_REQUEST && resultCode==RESULT_OK){
           /* if(resultCode == RESULT_OK) {
                List<ContactResult> results = MultiContactPicker.obtainResult(data);
                Log.d("MyTag", String.valueOf(results.get(0).getPhoneNumbers().get(0)));
            } else if(resultCode == RESULT_CANCELED){
                System.out.println("User closed the picker without selecting items.");
            }*/


                Uri uri = data.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);









                ContactName.setText(name);
                whatsappNumber.setText(number.replace("+91","").replace(" ","").replace("-",""));

                Log.d(TAG, "ZZZ number : " + number +" , name : "+name);


                cursor.close();


            }

            if(requestCode==PEMISSION_OF_ACCESSEBLITY)
            {
                if (isAccessibilityServiceEnabled(schedule_sms.this, whatsappAccessibility.class))
                {
                    callDataFromDb();
                }
            }else {
                Log.i(TAG,"request  code "+PEMISSION_OF_ACCESSEBLITY);
            }


            if (requestCode==PICK_FROM_GALLARY)
            {
                if (resultCode == Activity.RESULT_OK) {
                    //
                    //pick image from gallery
                    if (data!=null)
                    {
                        if (data.getClipData()!=null)
                        {
                            for (int i=0;i<data.getClipData().getItemCount();i++)
                            {
                                if (dataPickingType==1) // picking image
                                {
                                    pickedImage.add(data.getClipData().getItemAt(i).getUri());
                                }else if (dataPickingType==2) // picking video
                                {
                                    pickedVideo.add(data.getClipData().getItemAt(i).getUri());

                                }else if (dataPickingType==3) //picking audio
                                {
                                    pickedAudio.add(data.getClipData().getItemAt(i).getUri());
                                }else {
                                    pickedDoc.add(data.getClipData().getItemAt(i).getUri());
                                }


                            }

                        }
                        else
                        {

                            if (dataPickingType==1) // picking image
                            {
                                pickedImage.add(data.getData());
                            }else if (dataPickingType==2) // picking video
                            {
                                pickedVideo.add(data.getData());

                            }else if (dataPickingType==3) // picking audio
                            {
                                pickedAudio.add(data.getData());
                            }else {
                                pickedDoc.add(data.getData());
                            }

                        }
                        totalMedia=pickedAudio.size()+pickedVideo.size()+pickedImage.size()+pickedDoc.size();
                        mediaCount.setText(String.valueOf(totalMedia));

                    }


                }
            }  // was picking media to send in whatsapp
        }catch (Exception e)
        {
            Log.e("schedule_sms",e.getMessage());
        }
    }




    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }

    void showSnackBar(String message,int color)
    {
        if (color==R.color.red)
        {
            playSound(this,R.raw.error_sound);
        }
        Snackbar.make(findViewById(R.id.mainLayout),message, BaseTransientBottomBar.LENGTH_SHORT)
                .setTextColor(ContextCompat.getColor(this,R.color.white))
                .setBackgroundTint(ContextCompat.getColor(this, color)).show();
    }




    File saveFileIntoFolder(Uri uri,String path)

    {
        try {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) // if api 30
            {
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(uri); //todo returning null uri. may be because of bad name
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try (

                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        FileOutputStream out = new FileOutputStream(new File(path, getFileName(uri)))
                )

                {

                int nRead;
                byte[] data = new byte[16384]; // A buffer size of 16KB

                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                byte[] recordData = buffer.toByteArray();
                out.write(recordData);
                return new File(path,getFileName(uri)); //todo path+"/"+getFileName(uri)

                } catch (IOException e) {
                        e.printStackTrace();
                }



//                    byte[] recordData = IOUtils.toByteArray(inputStream); //https://stackoverflow.com/questions/2436385/android-getting-from-a-uri-to-an-inputstream-to-a-byte-array
//                    //https://stackoverflow.com/questions/2091454/byte-to-inputstream-or-outputstream
//                    //  FileOutputStream out = new FileOutputStream(new File(path+"/"+getFileName(uri)));
//                    FileOutputStream out = new FileOutputStream(new File(path,getFileName(uri)));
//
//
//                    out.write(recordData);
//
//                    IOUtils.closeQuietly(out);

//

//                return new File(path,getFileName(uri)); //todo path+"/"+getFileName(uri)



            }else
            {


                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(uri); //todo returning null uri. may be because of bad name
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }




                    try (

                            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                            FileOutputStream out = new FileOutputStream(new File(path, getFileName(uri)))
                    )

                    {

                        int nRead;
                        byte[] data = new byte[16384]; // A buffer size of 16KB

                        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, nRead);
                        }

                        buffer.flush();
                        byte[] recordData = buffer.toByteArray();
                        out.write(recordData);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                return new File(path,getFileName(uri)); //todo path+"/"+getFileName(uri)
//                    byte[] recordData = IOUtils.toByteArray(inputStream); //https://stackoverflow.com/questions/2436385/android-getting-from-a-uri-to-an-inputstream-to-a-byte-array
//                    //https://stackoverflow.com/questions/2091454/byte-to-inputstream-or-outputstream
//                    //  FileOutputStream out = new FileOutputStream(new File(path+"/"+getFileName(uri)));
//                    FileOutputStream out = new FileOutputStream(new File(path,getFileName(uri)));
//
//
//                    out.write(recordData);
//
//                    IOUtils.closeQuietly(out);

//
//                    return new File(path,getFileName(uri)); //todo path+"/"+getFileName(uri)





            }
        }catch (Exception e)
        {
            Log.e("schedule_sms",e.getMessage());
            return null;
        }
        return null;
    }


    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        try {


            String result = null;
            if (uri.getScheme().equals("content")) {
                Cursor cursor =getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally
                {
                    cursor.close();
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            return result; //todo set doc in the list and show in the show dialog
        }catch (Exception e)
        {

            Log.e("schedule_sms",e.getMessage());
            return "failed";
        }
    }

    public void deleteAllScheduledMedia(File dir) {

        try {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (dir.isDirectory())
                    {

                        String[] children = dir.list();
                        for (int i = 0; i < children.length; i++)
                        {
                            new File(dir, children[i]).delete();

                        }





                    }
                }
            }).start();


//        if (dir.isDirectory())
//        {
//
//            String[] children = dir.list();
//            i("deleteAllScheduledMedia dir.isDirectory() "+dir.isDirectory()+" children size "+ (children != null ? children.length : 0));
//
//            for (String child : children) {
//                boolean success = deleteAllScheduledMedia(new File(dir, child));
//                if (!success) {
//                    return false;
//                }
//            }
//        }
//
//            return dir.delete();
        }catch (Exception e)
        {
            Log.e("schedule_sms","deleteAllScheduledMedia "+e.getMessage());
        }
        // The directory is now empty so delete it

    }
}
