package com.affixstudio.whatsapptool.activityOur;


import static com.affixstudio.whatsapptool.activityOur.startScreen.haveSub;
import static com.affixstudio.whatsapptool.activityOur.startScreen.lastPremiumUpdated;
import static com.affixstudio.whatsapptool.activityOur.startScreen.premiumDays;
import static com.affixstudio.whatsapptool.getData.GetInfo.isOnline;
import static com.affixstudio.whatsapptool.modelOur.NotificationRecever.query;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.fragmentsOur.ChatFragment;
import com.affixstudio.whatsapptool.fragmentsOur.HomeFragment;
import com.affixstudio.whatsapptool.fragmentsOur.StatusFragment;
import com.affixstudio.whatsapptool.fragmentsOur.recoverSms;
import com.affixstudio.whatsapptool.fragmentsOur.textFunction;
import com.affixstudio.whatsapptool.getData.NetworkChangeReceiver;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.modelOur.setOnRecycleClick;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements setOnRecycleClick {

    Fragment fragment=null;

    BottomNavigationView NbView;
     Dialog disclosure;
   // private MaxAd loadedNativeAd;

    //    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    //    getSupportActionBar().hide(); //hide the title bar

        // Obtain the FirebaseAnalytics instance.
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

//        sendAnalytics sendAnalytics=new sendAnalytics(mFirebaseAnalytics);





//        open new fragment
//        if (savedInstanceState == null){
//            getSupportFragmentManager().beginTransaction()
//                    .add(android.R.id.content, new splash_three()).commit();
//        }

        // startActivity(new Intent(MainActivity.this, new_year.class));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

        String date=sdf.format(new Date());




        String userquery="CREATE TABLE IF NOT EXISTS userPremiumInfoTable (_id INTEGER PRIMARY KEY autoincrement, DateRemaining text DEFAULT '0',LastUpdated text DEFAULT '0') ";

        // Load the first ad
        database userdb=new database(this,getString(R.string.userPremiumInfoTable),userquery,1);

        Cursor cursor=userdb.getinfo(0);

        if (cursor.moveToNext())
        {
            premiumDays=cursor.getInt(1);
            lastPremiumUpdated=cursor.getString(2);
        }
        else {

            ContentValues values=new ContentValues();
            values.put("DateRemaining","0");
            values.put("LastUpdated",date);
            userdb.getWritableDatabase().insert(getString(R.string.userPremiumInfoTable),null,values);
            premiumDays=0;
            lastPremiumUpdated=date;

        }

        if (premiumDays>0 && lastPremiumUpdated.equals(date)) // decreasing premium  date
        {
//            String[] oldDate=lastPremiumUpdated.split("/");
//            String[] newDate=date.split("/");

            int dateDifference=getDateDifference(lastPremiumUpdated,date);
            int remaingDate=0;

            if (dateDifference<premiumDays)
            {
                remaingDate=premiumDays-dateDifference;
            }
            String sql="UPDATE "+getString(R.string.userPremiumInfoTable)+" SET DateRemaining='"+remaingDate+"', LastUpdated='"+date+"'";
            userdb.getWritableDatabase().execSQL(sql);

            premiumDays=remaingDate;
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();

        }
        if (premiumDays>0)
        {
            haveSub="yes";
        }




        fragment=new HomeFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
        NbView=findViewById(R.id.NBer);


//        MaxAdView adView = findViewById( R.id.bn_appLovin );
//        adView.setListener(new MaxAdViewAdListener() {
//            @Override
//            public void onAdExpanded(MaxAd maxAd) {
//
//            }
//
//            @Override
//            public void onAdCollapsed(MaxAd maxAd) {
//
//            }
//
//            @Override
//            public void onAdLoaded(MaxAd maxAd) {
//                adView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAdDisplayed(MaxAd maxAd) {
//
//            }
//
//            @Override
//            public void onAdHidden(MaxAd maxAd) {
//
//            }
//
//            @Override
//            public void onAdClicked(MaxAd maxAd) {
//
//            }
//
//            @Override
//            public void onAdLoadFailed(String s, MaxError maxError) {
//
//            }
//
//            @Override
//            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
//
//            }
//        });
//
//        if (haveSub.equals("no"))
//        {
//            adView.loadAd();
//        }
        database db=new database(this,getString(R.string.recover_message_table_name),query,2);

        String tableName=getString(R.string.CallBlockListtable2);

        String query1="CREATE TABLE IF NOT EXISTS CallBlockListTable2 (_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',Number text DEFAULT 'Not set',IsGroup text DEFAULT '0',WAorWB text DEFAULT '1') ";



        database db2=new database(this,tableName,query1,1);
        db2.getWritableDatabase();
        db2.close();


        String query="CREATE TABLE IF NOT EXISTS Chat_history(_id INTEGER PRIMARY KEY autoincrement,Message text,Number text,Date text,isDraft INTEGER,TextCountryCode text DEFAULT '91',SendThrough text DEFAULT 'com.whatsapp') ";
        database db1=new database(this,getString(R.string.chatHistorytable),query,1);

        String query3="CREATE TABLE IF NOT EXISTS userPremiumInfoTable (_id INTEGER PRIMARY KEY autoincrement, DateRemaining text DEFAULT '0',LastUpdated text DEFAULT '0') ";


        database db3=new database(this,getString(R.string.userPremiumInfoTable),query3,1);





        SharedPreferences sp=getSharedPreferences("affix",MODE_PRIVATE);
        SharedPreferences.Editor spEdit=sp.edit();





        View close=getLayoutInflater().inflate(R.layout.close_dialog,null);
        closeDialog=new BottomSheetDialog(this); //#todo delete all unwanted files

        // closeDialog=new BottomSheetDialog(this);

        Button ok=close.findViewById(R.id.yes);
        Button no=close.findViewById(R.id.no);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                System.exit(0);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog.dismiss();
            }
        });
        //loadApplovinNative(close);
        closeDialog.setContentView(close);





        NbView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home: fragment=new HomeFragment(MainActivity.this); // to change navigation selection

                        break;
                    case R.id.navigation_status:fragment=new StatusFragment(MainActivity.this);

//                        sendAnalytics.send(getString(R.string.firebaseItemNameStatusDownload));
                        break;
                    case R.id.navigation_chat:fragment=new ChatFragment();

//                        sendAnalytics.send(getString(R.string.firebaseItemNameDirectChat));


                        break;
                    case R.id.navigation_restore_ms:fragment=new recoverSms();
//                        sendAnalytics.send(getString(R.string.firebaseItemNameRecoverOpen));

                        break;
                    case R.id.navigation_text_fun: setTextFunction();            ;//:fragment=new textFunction();
//                        sendAnalytics.send(getString(R.string.firebaseItemNameTextFun));
                        break;
                }

                if (fragment!=null)
                {
//
                    openFragmentAnim();
                }

                return true;
            }
        });
        if (getIntent()!=null && getIntent().getIntExtra("fragmentNo",0)==3)
        {
            NbView.setSelectedItemId(R.id.navigation_restore_ms);
        }else {
            NbView.setSelectedItemId(R.id.navigation_home);
        }


//        pm = (PowerManager) getSystemService(POWER_SERVICE);
//        betteryOpLA=findViewById(R.id.betteryOpLA);
//
//        findViewById(R.id.betteryOp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                offOptimizition();
//            }
//        });
//        if (!pm.isIgnoringBatteryOptimizations(getPackageName()))
//        {
//            betteryOpLA.setVisibility(View.VISIBLE);
//        }else {
//            betteryOpLA.setVisibility(View.GONE);
//        }


        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        receiver=new NetworkChangeReceiver();
        registerReceiver(receiver,filter);

        db3.close();



    }

    PowerManager pm;
    LinearLayout betteryOpLA;

    BroadcastReceiver receiver;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e)
        {

            Log.e("e",e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnline(this))
        {
            startActivity(new Intent(this,no_internet_Screen.class));
        }
//        if (!pm.isIgnoringBatteryOptimizations(getPackageName()))
//        {
//            betteryOpLA.setVisibility(View.VISIBLE);
//        }
//        else {
//            betteryOpLA.setVisibility(View.GONE);
//        }
    }
    private void offOptimizition() {

        Intent intent1 = new Intent();
        String packageName=getPackageName();




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

    int value=-1;
    private void setTextFunction() {
        if (value!=-1) // when not calling from button
        {
            textFunction textFunction=new textFunction();
            textFunction.setArguments(bundle);


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,  textFunction)
                    .commit();
        }else

        {

            Fragment f=new textFunction();
//            bundle.putInt("value",0);
//            textFunction textFunction=new textFunction();
//            textFunction.setArguments(bundle);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, textFunction)
//                    .commit();\
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();


            ft.replace(R.id.container,f)
                    .commit();

        }
        value=-1;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
    public void openSetting(View view){
        Intent i = new Intent(this,settingActivity.class);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(i,b);
    }
    public void openFaq(View view){
        Intent i = new Intent(this, AppTutorials.class);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(i,b);
    }

    public  void openFragmentAnim(){
        try {


            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.from_right,R.anim.to_left);
//        transaction.addToBackStack(null);
            transaction.replace(R.id.container,fragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();

        }catch (Exception e){
            Log.e("openFragmentAnim",e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment.toString().contains("HomeFragment")
        && fragment.toString().contains("id"))
        {
            Log.i("MainActivity","info "+fragment.toString());


            closeDialog.show();
        }else
        {
            Log.i("MainActivity","info "+fragment.toString());
            NbView.setSelectedItemId(R.id.navigation_home);
        }



    }

    BottomSheetDialog closeDialog;


    Bundle bundle=new Bundle();
    @Override
    public void onItemEdit(int position, String title) {
        // Log.i("onclick",position+"");
        // BottomNavigationView NbView=findViewById(R.id.NBer);
        if (position==-1)
        {
            NbView.setSelectedItemId(R.id.navigation_chat );
        }else if (position==5)
        {
            NbView.setSelectedItemId(R.id.navigation_status );
        }else if (position==-4)
        {
            NbView.setSelectedItemId(R.id.navigation_home );
        }else if (position==6)
        {
            NbView.setSelectedItemId(R.id.navigation_restore_ms );
        }else if (position==7)
        {
            NbView.setSelectedItemId(R.id.navigation_chat );
        }else
        {
            value=position;
            bundle.putInt("value",position);
            NbView.setSelectedItemId(R.id.navigation_text_fun );
        }

        // NbView.setSelectedItemId(R.id.navigation_text_fun);
    }

    private int getDateDifference(String date1String, String date2String) {
//        LocalDate date1 = LocalDate.of(Integer.parseInt(oldDate[2]), Integer.parseInt(oldDate[1]), Integer.parseInt(oldDate[0]));
//        LocalDate date2 = LocalDate.of(Integer.parseInt(newDate[2]), Integer.parseInt(newDate[1]), Integer.parseInt(newDate[0]));
//
//        Period period = Period.between(date1, date2);
//
//        return period.getDays();

//        String date1String = "12/06/21"; // Example date 1
//        String date2String = "25/06/21"; // Example date 2
        long daysBetween=0;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        try {
            // Parse the date strings to Date objects
            Date date1 = format.parse(date1String);
            Date date2 = format.parse(date2String);

            // Create two Calendar instances
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            // Calculate the difference between the dates
            long milliseconds1 = cal1.getTimeInMillis();
            long milliseconds2 = cal2.getTimeInMillis();
            long diff = milliseconds2 - milliseconds1;

            // Convert the difference to days
            daysBetween = diff / (24 * 60 * 60 * 1000);

            // Print the result
            System.out.println("Days between: " + daysBetween);
            return (int) daysBetween;
        } catch (ParseException e) {
            e.printStackTrace();
            return (int) daysBetween;
        }
    }


//    private void loadApplovinNative(View view) {
//        MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder( R.layout.native_ad_applovin_big)
//                .setTitleTextViewId( R.id.title_text_view )
//                .setBodyTextViewId( R.id.body_text_view )
//                //  .setStarRatingContentViewGroupId( R.id.star_rating_view )
//                .setAdvertiserTextViewId( R.id.advertiser_textView )
//                .setIconImageViewId( R.id.icon_image_view )
//                .setMediaContentViewGroupId( R.id.media_view_container )
//                // .setOptionsContentViewGroupId( R.id.options_view )
//                .setCallToActionButtonId( R.id.cta_button )
//                .build();
//
//        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader( getString(R.string.applovinNativeADID), this );
//        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener()
//        {
//            @Override
//            public void onNativeAdLoaded(@Nullable MaxNativeAdView maxNativeAdView, MaxAd maxAd) {
//                Log.i("home","native loaded ");
//
//
//                FrameLayout frameLayout =
//                        view.findViewById(R.id.fl_adplaceholder);
//                if ( loadedNativeAd != null )
//                {
//                    nativeAdLoader.destroy( loadedNativeAd );
//                }
//
//                // Save ad for cleanup.
//                loadedNativeAd = maxAd;
//
//                frameLayout.removeAllViews();
//                frameLayout.addView( maxNativeAdView );
//
//
//            }
//
//            @Override
//            public void onNativeAdLoadFailed(String s, MaxError maxError)
//            {
//                super.onNativeAdLoadFailed(s, maxError);
//
//                Log.i("home","Native load failed "+maxError.getMessage());
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        nativeAdLoader.loadAd( new MaxNativeAdView( binder, MainActivity.this ) );
//                    }
//                },5000);
//
//
//
//            }
//
//            @Override
//            public void onNativeAdClicked(MaxAd maxAd) {
//                super.onNativeAdClicked(maxAd);
//                nativeAdLoader.loadAd( new MaxNativeAdView( binder, MainActivity.this) );
//            }
//
//            @Override
//            public void onNativeAdExpired(MaxAd maxAd) {
//                super.onNativeAdExpired(maxAd);
//                nativeAdLoader.loadAd( new MaxNativeAdView( binder,MainActivity.this ) );
//            }
//        });
//
//        nativeAdLoader.loadAd( new MaxNativeAdView( binder,this  ) );
//    }

}