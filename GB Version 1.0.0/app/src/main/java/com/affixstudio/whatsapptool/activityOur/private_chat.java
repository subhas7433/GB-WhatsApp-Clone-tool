package com.affixstudio.whatsapptool.activityOur;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.affixstudio.whatsapptool.fragmentsOur.HomeFragment.shouldRecoverMediaFocused;
import static com.affixstudio.whatsapptool.getData.GetInfo.isOnline;
import static com.affixstudio.whatsapptool.modelOur.sound.playSound;
import static com.affixstudio.whatsapptool.serviceOur.serviceTool.isServiceRunning;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.fragment_private_recover_chat;
import com.affixstudio.whatsapptool.getData.GetInfo;
import com.affixstudio.whatsapptool.getData.NetworkChangeReceiver;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomInfoDialog;
import com.affixstudio.whatsapptool.serviceOur.NotificationService;
import com.affixstudio.whatsapptool.serviceOur.mediaWatcher;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class private_chat extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION = 234;
    private static final int READ_STORAGE_ANDROID11 = 2121;
    Intent intent;
    private final String tag="privateChat";
    Context ctx = private_chat.this;
    int isWentForPermission=0; // 0= false
    SwitchCompat s;

    SharedPreferences sp;
    SharedPreferences.Editor spE;
    GetInfo gi=new GetInfo();

    Dialog notiPer,storageP;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_main);
        getWindow().setStatusBarColor(getResources().getColor(R.color.VA_off, this.getTheme()));

        sp=getSharedPreferences("affix",MODE_PRIVATE);
         spE=sp.edit();


        fragment_private_recover_chat recoverSms=new fragment_private_recover_chat();
        com.affixstudio.whatsapptool.fragment_private_media fragment_private_media = new com.affixstudio.whatsapptool.fragment_private_media();
        com.affixstudio.whatsapptool.fragment_private_setting fragment_private_setting = new com.affixstudio.whatsapptool.fragment_private_setting();
        TabLayout tabLayout = findViewById(R.id.tab_privatechat);
        ViewPager viewPager = findViewById(R.id.viewPager_privateChat);

        private_chat.Adapter adapter = new private_chat.Adapter(getSupportFragmentManager());

        tabLayout.setupWithViewPager(viewPager);

        adapter.addFragment(recoverSms, "Message");
        adapter.addFragment(fragment_private_media, "Media");
        adapter.addFragment(fragment_private_setting, "Setting");

        viewPager.setAdapter(adapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.message_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.image_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.setting_icon);


        if (shouldRecoverMediaFocused==1)
        {

            tabLayout.selectTab(tabLayout.getTabAt(1));
            shouldRecoverMediaFocused=0;
        }



         s=findViewById(R.id.privateChatSwitch);
         intent=new Intent(private_chat.this, mediaWatcher.class);

         findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 onBackPressed();
             }
         });

         notiPer=new Dialog(private_chat.this);
         storageP=new Dialog(private_chat.this);

         notiPer.setCancelable(false);
         storageP.setCancelable(false);
        notiPer.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog));
        storageP.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog));

         View notiV=getLayoutInflater().inflate(R.layout.permission_notification,null);
         View stoV=getLayoutInflater().inflate(R.layout.permission_storage,null);

         TextView grantNoti=notiV.findViewById(R.id.granNotiPer);
        TextView grantStorage=stoV.findViewById(R.id.grantPermission);

         ImageView closeN=notiV.findViewById(R.id.close);
         ImageView closeS=stoV.findViewById(R.id.close);

         grantNoti.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 notiPer.dismiss();
                 isWentForPermission=1;
                 startActivityForResult(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS),NOTIFICATION_PERMISSION);

             } //todo fix multinotification issue in private chat
         });
        closeN.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 notiPer.dismiss();
                 isWentForPermission=0;
                 s.setChecked(false);

             }
         });


        grantStorage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 storageP.dismiss();
                 isWentForPermission=1;
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                 {//todo do fix notification when deleted media
                     requestPermissionQ();
                 }
             }
         });
        closeS.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 storageP.dismiss();
                 isWentForPermission=0;
                 s.setChecked(false);
             }
         });

        notiPer.setContentView(notiV);
        storageP.setContentView(stoV);


        findViewById(R.id.tutorial).setOnClickListener(view -> {
            bottomInfoDialog bd=new bottomInfoDialog();
            bd.showinfo(private_chat.this,10,getString(R.string.fristInPrivateTeg));
        });



//        if (haveSub.equals("no"))
//        {
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
//            adView.loadAd();
//        }





        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                if (!NoStoragePermission())
                {
                    turnOnPrivateChat(b);
                }
                else if (b)
                {
                    new AlertDialog.Builder(private_chat.this).setCancelable(false)
                                    .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                          String  packageName = "com.affixstudio.gbwhats";

                                            try {
                                                //Open the specific App Info page:
                                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.parse("package:" + packageName));
                                                startActivity(intent);

                                            } catch ( ActivityNotFoundException e ) {
                                                //e.printStackTrace();

                                                //Open the generic Apps page:
                                                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                                startActivity(intent);

                                            }
                                        }
                                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).setTitle("Permission Required")
                            .setMessage("Without  storage permission this page will malfunction.")
                            .show();


                }



            }
        });


        s.setChecked(gi.shouldOn(private_chat.this,getString(R.string.privateChatOnTag)));



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            requestPermissionQ();
//        }

//        requestPermissions(PERMISSIONS, 2);
//        if (arePermissionDenied()) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                requestPermissionQ();
//            }else
//            {
//
//            }
//
//        }else
//        {
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////                executeNew();
////            }else
////            {
////                getFile();
////            }
//
//        }





 //todo not showing media dialog and numbers picking not in auto and number with highfen



        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        receiver=new NetworkChangeReceiver();
        registerReceiver(receiver,filter);
    }



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


    @SuppressLint("StaticFieldLeak")
    private void turnOnPrivateChat(boolean isOn) {


        if (isOn) //means trying to  turning on
        {

            if (arePermissionDenied())
            {


                i("arePermissionDenied");
                playSound(ctx,R.raw.error_sound);

                storageP.show();

            }else if (noNotificationPermission()) //
            {
                i("noNotificationPermission");
               notiPer.show();


            }else
            {
                findViewById(R.id.linearLayout10).setBackgroundColor(getResources().getColor(R.color.VA_on));
                getWindow().setStatusBarColor(getResources().getColor(R.color.VA_on, this.getTheme()));
                if (!isServiceRunning(mediaWatcher.class.getName(), this))

                {
                    i("!isServiceRunning");


                    new AsyncTask<String, String, String>() {
                        @Override
                        protected String doInBackground(String... strings) {
                            startService(intent);//todo create own folder to save private media chats
                            return null;
                        }
                    }.execute();
                    if (sp.getBoolean(getString(R.string.privateMediaOnTag),false))
                    {
                        i("sp.getBoolean(getString(R.string.privateMediaOnTag),false)");

                    }

                    playSound(ctx,R.raw.switch_sound);
                    isWentForPermission=0;
                    spE.putBoolean(getString(R.string.privateChatOnTag),true).apply(); // using it in the notification service

                }
                else
                {

                    if (!sp.getBoolean(getString(R.string.privateMediaOnTag),false))
                    {

                            stopService(intent);

                    }
                }



            }

        }
        else
        {

            if (isServiceRunning(mediaWatcher.class.getName(), this) // is service running
                    && !sp.getBoolean(getString(R.string.recoverMediaOnTag),false)) // if recoverMediaOnTag on then shouldn't stop the service
            {
                i(" stopService(intent) ");
                stopService(intent);
            }
            i(" stopService(intent) off");
            spE.putBoolean(getString(R.string.privateChatOnTag),false).apply(); // using it in the notification service
            findViewById(R.id.linearLayout10).setBackgroundColor(getResources().getColor(R.color.VA_off));
            getWindow().setStatusBarColor(getResources().getColor(R.color.VA_off, this.getTheme()));
        }

        i++;
        i("i = "+i);

    }

    int i=0;
    final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onResume() {
        super.onResume();
    //    i("onResume isWentForPermission "+isWentForPermission);

        if (!isOnline(this))
        {
            startActivity(new Intent(this,no_internet_Screen.class));
        }
        if (isWentForPermission==1) // was trying to on the private chat

        {
            i("resume isWentForPermission 1");
            turnOnPrivateChat(true);
        }else {
            s.setChecked(gi.shouldOn(private_chat.this,getString(R.string.privateChatOnTag)));
        }
        if (arePermissionDenied() || noNotificationPermission()) // when no permission
        {
            s.setChecked(false); // stop the service
        }
    }
    void i(String s)
    {
        Log.i(ctx.toString(),s);
    }
    void e(String s)
    {
        Log.e(ctx.toString(),s);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_STORAGE_ANDROID11 )
        {
            if(NoStoragePermission())
            {

                s.setChecked(false);
                Toast.makeText(ctx, "Denied", Toast.LENGTH_SHORT).show();



            }else {
                s.setChecked(true);
            }
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        File file=new File(android.os.Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp");
        if (file.exists())
        {
            Log.i("file","exist");
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)

    private void requestPermissionQ() {
        StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
        String startDir= "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia" ;


        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

        String scheme = uri.toString();
        scheme = scheme.replace("/root/", "/document/");
        scheme += "%3A" + startDir;

        uri = Uri.parse(scheme);

        Log.d("URI", uri.toString());

        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        activityResultLauncher.launch(intent);
        Log.i("status","requestPermissionQ");


    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent data = result.getData();

                    assert data != null;

                    Log.d("HEY: ", data.getData().toString());


                    getContentResolver().takePersistableUriPermission(
                            data.getData(),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);





                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                }
            }
    );

    private boolean arePermissionDenied() {

        String identifier="app%2F";

        List<UriPermission> list = getContentResolver().getPersistedUriPermissions();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {

            boolean isGot=true;
            for (int i=0;i<list.size();i++)
            {
                if (list.get(i).getUri().toString().contains(identifier)
                        && !list.get(i).getUri().toString().contains("Statuses"))
                {

                    isGot=false; // already got the permission
                    break;
                }
            }

            return isGot;


        }

        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED)
            {

                return true;
            }
        }

        return false;
    }
    class Adapter extends FragmentStatePagerAdapter {

        List<Fragment> fragementArray=new ArrayList<>();
        List<String> stringArray=new ArrayList<>();

        public Adapter(@NonNull FragmentManager fm) {
            super(fm);
        }


        protected void addFragment(Fragment fragment,String s){
            fragementArray.add(fragment);
            stringArray.add(s);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragementArray.get(position);
        }

        @Override
        public int getCount() {
            return fragementArray.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return stringArray.get(position);
        }
    } //todo delete all in small view section and unreal chat count in recover and private

    public Boolean noNotificationPermission() {
        String theList = android.provider.Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        String[] theListList = theList.split(":");
        String me = (new ComponentName(this, NotificationService.class)).flattenToString();
        for ( String next : theListList ) {
            if ( me.equals(next) ) return false;
        }
        return true;
    }

    boolean NoStoragePermission()
    {
        boolean t=false;
        for (String permissions : PERMISSIONS)
        {
            if (ActivityCompat.checkSelfPermission(private_chat.this, permissions) != PackageManager.PERMISSION_GRANTED) {


                t= true;
            }
        }
        return t;
    }


}