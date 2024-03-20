package com.affixstudio.whatsapptool.activityOur;

import static com.affixstudio.whatsapptool.getData.GetInfo.isOnline;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.ads.OurAds;
import com.affixstudio.whatsapptool.getData.NetworkChangeReceiver;
import com.affixstudio.whatsapptool.intro;
import com.affixstudio.whatsapptool.modelOur.ApiResponse;
import com.affixstudio.whatsapptool.modelOur.GetData;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class startScreen extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 123;
    public static int shouldShowUnity=1;
    public static int purchaseON=0;
    int progress = 33;
    LinearProgressIndicator lpi;

    SharedPreferences sp;
    private AppUpdateManager appUpdateManager;
    BroadcastReceiver receiver;

    public static String haveSub="no"; // todo change it to no

    public static ArrayList<OurAds> adsDetails=new ArrayList<>();
    boolean wasUpdating=false; // to avoid unnecessary onresume call


    public static int premiumDays=0;
    public static String lastPremiumUpdated="0";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);


        try {
            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(startScreen.this, R.color.primary));
            // no internet broadcast
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

            receiver=new NetworkChangeReceiver();
            registerReceiver(receiver,filter);
            sp=getSharedPreferences("affix",MODE_PRIVATE);

            // logo animation
            ImageView imageView=findViewById(R.id.imageView16);
            imageView.setTranslationY(400);
            imageView.animate().translationYBy(-400).setDuration(700);




            lpi = findViewById(R.id.lpi);
            lpi.setIndeterminate(false); //#todo inapp update not working





//            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
//
//            String date=sdf.format(new Date());

//            AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
//            AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {
//                @Override
//                public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
//                {
//                    // AppLovin SDK is initialized, start loading ads
//                }
//            } );

//            String query="CREATE TABLE IF NOT EXISTS userPremiumInfoTable (_id INTEGER PRIMARY KEY autoincrement, DateRemaining text DEFAULT '0',LastUpdated text DEFAULT '0') ";
//
//            // Load the first ad
//            database db=new database(this,getString(R.string.userPremiumInfoTable),query,1);
//            Cursor cursor=db.getinfo(0);
//
//            if (cursor.moveToNext())
//            {
//                premiumDays=cursor.getInt(1);
//                lastPremiumUpdated=cursor.getString(2);
//            }
//            else {
//
//                ContentValues values=new ContentValues();
//                values.put("DateRemaining","1"); // todo make it 0
//                values.put("LastUpdated",date);
//                db.getWritableDatabase().insert(getString(R.string.userPremiumInfoTable),null,values);
//                premiumDays=0;
//                lastPremiumUpdated=date;
//
//            }
//
//            Log.i("start","premiumDays "+premiumDays+" lastPremiumUpdated "+lastPremiumUpdated);
//            if (premiumDays>0 && lastPremiumUpdated.equals(date)) // decreasing premium  date
//            {
//                String[] oldDate=lastPremiumUpdated.split("/");
//                String[] newDate=date.split("/");
//
//                int dateDifference= 1    /*getDateDifference(oldDate,newDate)*/;
//                int remaingDate=0;
//
//                if (dateDifference<premiumDays)
//                {
//                    remaingDate=premiumDays-dateDifference;
//                }
//                String sql="UPDATE "+getString(R.string.userPremiumInfoTable)+" SET DateRemaining='"+remaingDate+"' LastUpdated='"+date+"'";
//                db.getWritableDatabase().execSQL(sql);
//                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
//                premiumDays=remaingDate;
//
//            }







        } catch (Exception e) {
            Log.e("inAppUpdate",e.getMessage());
        }


    }
    void checkUpdate()
    {



        SharedPreferences.Editor se=sp.edit();

//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                Log.i("startScreen","snapshot.exists() "+snapshot.exists());
//
//                if (snapshot.exists())
//                {
//                    Log.i("startScreen","isScheduleON "+snapshot.child("isScheduleON").getValue());
//
//
//                    se.putInt(getString(R.string.isScheduleONTag), Integer.parseInt(snapshot.child("isScheduleON").getValue().toString())) ;
//                    se.putInt(getString(R.string.appUpdateTypeTag), Integer.parseInt(snapshot.child("appUpdateType").getValue().toString()) );
//                    shouldShowUnity=Integer.parseInt(snapshot.child("shouldShowUnity").getValue().toString());
//                    purchaseON=Integer.parseInt(snapshot.child("purchaseON").getValue().toString());
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e)
//                    {
//                        e.printStackTrace();
//                    }
//                    se.apply();
//
//
//
//                    Log.i("startScreen","isScheduleON se "+sp.getInt(getString(R.string.isScheduleONTag),0));
//
//
//                    getUpdate();
//                }else {
//                    getUpdate();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                getUpdate();
//            }
//        });

        getUpdate();

    }
    int updateType=0;
    InstallStateUpdatedListener listener;
    void getUpdate()
    {
        /* immedate = 1
         * flexible=0 */
        lpi.setProgressCompat(progress,true);

        updateType=sp.getInt(getString(R.string.appUpdateTypeTag),1);

        Log.i("inAppUpdate","first");
        appUpdateManager = AppUpdateManagerFactory.create(startScreen.this); //
        listener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                Toast.makeText(this, "App updated", Toast.LENGTH_LONG).show();
                appUpdateManager.completeUpdate();
            }

        };
        appUpdateManager.registerListener(listener);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();


        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {


            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(updateType)) // update available

            {
                try {

                    wasUpdating=true;
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, updateType, this, MY_REQUEST_CODE);


                } catch (IntentSender.SendIntentException e) {
                    Log.e("inAppUpdate","error "+e.getMessage());
                }

            } else
            {
                Log.i("inAppUpdate","condition false");

                GetData getData=new GetData(new ApiResponse() {
                    @Override
                    public void response(String response)
                    {
                        try {

                            if (!response.equals("100"))
                            {


                                JSONArray ja=new JSONArray(response);
                                adsDetails.clear();

                                for (int i=0; i<ja.length();i++)
                                {
                                    String jo=ja.getString(i);
                                    JSONObject jb=new JSONObject(jo);

                                    OurAds ad=new OurAds(jb.getString("ImageLink"),jb.getString("OpeningLink"));
                                    Picasso.get().load(ad.imageLink);

                                    adsDetails.add(ad);


                                }



                            }
                            setProgress();
                        }catch (Exception e)
                        {
                            setProgress();
                        }






                    }
                });

                getData.Start(getString(R.string.getAdsUrl));

            }
            Log.i("inAppUpdate","last");
        });

        //comment korta hoba update er somoy
          setProgress();
    }
    private void setProgress() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                progress = progress + 33;
                lpi.setProgressCompat(progress,true);

                if (progress == 99){
                    if (sp.getBoolean("isNewUser",true)){
                        startActivity(new Intent(startScreen.this, intro.class));
                    }else
                    {
                        Intent i = new Intent(startScreen.this, MainActivity.class);
                        startActivity(i);
                    }

                }
            }
        };
        timer.schedule(task,50,50);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if ( resultCode != RESULT_OK && updateType==1 ) // when update type immediate and failed
            {


                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.alert_icon)
                        .setTitle("Update failed")
                        .setMessage("Please close the application and try again.")
                        .setCancelable(true)

                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAffinity();
                                System.exit(0);
                            }
                        })
                        .show();
            }
            else {
                setProgress();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!wasUpdating)
        {
            if (isOnline(startScreen.this) )
            {
                if (!verifyInstallerId()) // todo make it true
                {
                    checkUpdate();
                }else {
                    new AlertDialog.Builder(this).setTitle("Invalid")
                            .setMessage("Please install the app from play store")
                            .setPositiveButton("Install", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.playstoreUrl))));
                                }
                            }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finishAffinity();
                                    System.exit(1);
                                }
                            }).show();
                }


            }else {
                startActivity(new Intent(this,no_internet_Screen.class));
            }
        }

    }

    boolean verifyInstallerId() {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        // The package name of the app that has installed your app
        final String installer = getPackageManager().getInstallerPackageName(getPackageName());
        return installer != null && validInstallers.contains(installer);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Log.i("startScreen","on destroy");
            unregisterReceiver(receiver);
            appUpdateManager.unregisterListener(listener);
        }catch (Exception e)
        {

            Log.e("e",e.getMessage());
        }
    }
}
