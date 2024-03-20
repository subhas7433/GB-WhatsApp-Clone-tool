package com.affixstudio.whatsapptool.activity.main;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.affixstudio.whatsapptool.fragmentsOur.HomeFragment.shouldRecoverMediaFocused;
import static com.affixstudio.whatsapptool.getData.GetInfo.isOnline;
import static com.affixstudio.whatsapptool.modelOur.sound.playSound;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activity.BaseActivity;
import com.affixstudio.whatsapptool.activityOur.no_internet_Screen;
import com.affixstudio.whatsapptool.fragment.auto_reply_custom;
import com.affixstudio.whatsapptool.getData.NetworkChangeReceiver;
import com.affixstudio.whatsapptool.model.preferences.PreferencesManager;
import com.affixstudio.whatsapptool.model.utils.Constants;
import com.affixstudio.whatsapptool.model.utils.CustomDialog;
import com.affixstudio.whatsapptool.model.utils.ServieUtils;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomInfoDialog;
import com.affixstudio.whatsapptool.serviceOur.NotificationService;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {




    public static final int REQ_NOTIFICATION_LISTENER = 100;
    SwitchCompat mainAutoReplySwitch;
    private PreferencesManager preferencesManager;
    private final List<MaterialCheckBox> supportedAppsCheckboxes = new ArrayList<>();
    private final List<View> supportedAppsDummyViews = new ArrayList<>();
    private int wentForPermission=0;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auto);
        getWindow().setStatusBarColor(getResources().getColor(R.color.VA_off, this.getTheme()));


        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        receiver=new NetworkChangeReceiver();
        registerReceiver(receiver,filter);

        com.affixstudio.whatsapptool.fragment.auto_reply_Setting autoreply_setting = new com.affixstudio.whatsapptool.fragment.auto_reply_Setting();
        com.affixstudio.whatsapptool.fragment.auto_reply_history autoreply_history = new com.affixstudio.whatsapptool.fragment.auto_reply_history();
        auto_reply_custom autoreply_specific = new auto_reply_custom();


        preferencesManager = PreferencesManager.getPreferencesInstance(this);
        // Assign Views
        mainAutoReplySwitch = findViewById(R.id.mainAutoReplySwitch);
        TabLayout tabLayout = findViewById(R.id.tab_Autoreply);
        ViewPager viewPager = findViewById(R.id.viewPager_Autoreply);



        Adapter adapter = new Adapter(getSupportFragmentManager());

        tabLayout.setupWithViewPager(viewPager);

        adapter.addFragment(autoreply_setting, "Setting");
        adapter.addFragment(autoreply_specific, "Custom");
        adapter.addFragment(autoreply_history, "History");

        viewPager.setAdapter(adapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.setting_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.contact_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.history_icon);






        if (shouldRecoverMediaFocused==1)
        {
            tabLayout.selectTab(tabLayout.getTabAt(1));
        }
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        notiPer=new Dialog(MainActivity.this);


        notiPer.setCancelable(false);
        View notiV=getLayoutInflater().inflate(R.layout.permission_notification,null);
        TextView grantNoti=notiV.findViewById(R.id.granNotiPer);
        ImageView closeN=notiV.findViewById(R.id.close);
//
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
//        if (haveSub.equals("no"))
//        {
//            adView.loadAd();
//        }

        closeN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                notiPer.dismiss();
                mainAutoReplySwitch.setChecked(false);


            }
        });

        grantNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notiPer.dismiss();
                launchNotificationAccessSettings();


            } //todo fix multinotification issue in private chat
        });

        notiPer.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog));
        notiPer.setContentView(notiV);
        LinearLayout mainLayout=findViewById(R.id.linearLayout10);

        mainAutoReplySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {


            Log.i("autoMain","checked "+isChecked+"  isListenerEnabled  "+isListenerEnabled(this, NotificationService.class));
            if (isChecked && !isListenerEnabled(this, NotificationService.class))
            {
//                launchNotificationAccessSettings();
                playSound(this,R.raw.error_sound);
                showPermissionsDialog();

            } else
            {

                preferencesManager.setServicePref(isChecked);
                Log.i("autoMain","checked and islistening");
                if (isChecked)
                {
                    playSound(this,R.raw.switch_sound);
                    startNotificationService();
                    mainLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.VA_on));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.VA_on, this.getTheme()));
                } else {
                    // groupReplySwitch.setChecked(false);
                    mainLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.VA_off));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.VA_off, this.getTheme()));
                    // Toast.makeText(this, "Auto reply service stopped.", Toast.LENGTH_SHORT).show();
                    stopNotificationService();
                }
                mainAutoReplySwitch.setText(
                        isChecked
                                ? R.string.mainAutoReplySwitchOnLabel
                                : R.string.mainAutoReplySwitchOffLabel
                );

                setSwitchState();

                // Enable group chat switch only if main switch id ON
                //   groupReplySwitch.setEnabled(isChecked);
            }
        });


        findViewById(R.id.autoRpTutorial).setOnClickListener(view -> {

            bottomInfoDialog bid=new bottomInfoDialog();
            bid.showinfo(MainActivity.this,0,getString(R.string.fristInAutoReplyTag));

        });

        if (preferencesManager.isServiceEnabled())
        {

            mainAutoReplySwitch.setChecked(true);
        }






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

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnline(this))
        {
            startActivity(new Intent(this, no_internet_Screen.class));
        }else if (wentForPermission==1)
        {
            Log.i("autoMain","resume mainAutoReplySwitch "+isListenerEnabled(this,NotificationService.class));

            mainAutoReplySwitch.setChecked(!isListenerEnabled(this,NotificationService.class));
            mainAutoReplySwitch.setChecked(isListenerEnabled(this,NotificationService.class));
            wentForPermission=0;
        }


    }














    Dialog notiPer;
    private void showPermissionsDialog() {


        notiPer.show();

//        CustomDialog customDialog = new CustomDialog(this);
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.PERMISSION_DIALOG_TITLE, getString(R.string.permission_dialog_title));
//        bundle.putString(Constants.PERMISSION_DIALOG_MSG, getString(R.string.permission_dialog_msg));
//
//
//        customDialog.showDialog(bundle, null, (dialog, which) -> {
//            if (which == -2) {
//                //Decline
//                showPermissionDeniedDialog();
//            } else {
//                //Accept
//
//            }
//        });
    }
    private void startNotificationService() {
        if (preferencesManager.isForegroundServiceNotificationEnabled()) {
            ServieUtils.getInstance(this).startNotificationService();
        }
    }
    public void launchNotificationAccessSettings() {
        //We should remove it few versions later
        enableService(true);//we need to enable the service for it so show in settings

        wentForPermission=1;
        final String NOTIFICATION_LISTENER_SETTINGS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            NOTIFICATION_LISTENER_SETTINGS = ACTION_NOTIFICATION_LISTENER_SETTINGS;
        } else {
            NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
        }
        Intent i = new Intent(NOTIFICATION_LISTENER_SETTINGS);
        startActivityForResult(i, REQ_NOTIFICATION_LISTENER);
    }


    private void enableService(boolean enable) {
        PackageManager packageManager = this.getPackageManager();
        ComponentName componentName = new ComponentName(this, NotificationService.class);
        int settingCode = enable
                ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        // enable dummyActivity (as it is disabled in the manifest.xml)
        packageManager.setComponentEnabledSetting(componentName, settingCode, PackageManager.DONT_KILL_APP);

    }
    private void showPermissionDeniedDialog() {
        CustomDialog customDialog = new CustomDialog(this);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PERMISSION_DIALOG_DENIED_TITLE, getString(R.string.permission_dialog_denied_title));
        bundle.putString(Constants.PERMISSION_DIALOG_DENIED_MSG, getString(R.string.permission_dialog_denied_msg));
        bundle.putBoolean(Constants.PERMISSION_DIALOG_DENIED, true);
        customDialog.showDialog(bundle, null, (dialog, which) -> {
            if (which == -2) {
                //Decline
                setSwitchState();
            } else {
                //Accept
                launchNotificationAccessSettings();
            }
        });
    }

    private void setSwitchState() {
        mainAutoReplySwitch.setChecked(preferencesManager.isServiceEnabled());

        enableOrDisableEnabledAppsCheckboxes(mainAutoReplySwitch.isChecked());
    }

    private void enableOrDisableEnabledAppsCheckboxes(boolean enabled) {
        for (MaterialCheckBox checkbox : supportedAppsCheckboxes) {
            checkbox.setEnabled(enabled);
        }
        for (View dummyView : supportedAppsDummyViews) {
            dummyView.setVisibility(enabled ? View.GONE : View.VISIBLE);
        }
    }
    private void stopNotificationService() {
        ServieUtils.getInstance(this).stopNotificationService();
    }
    //https://stackoverflow.com/questions/20141727/check-if-user-has-granted-notificationlistener-access-to-my-app/28160115
    //TODO: Use in UI to verify if it needs enabling or restarting
    public boolean isListenerEnabled(Context context, Class notificationListenerCls) {
        ComponentName cn = new ComponentName(context, notificationListenerCls);
        String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQ_NOTIFICATION_LISTENER )
        {
           // mainAutoReplySwitch.setChecked(isListenerEnabled(this, NotificationService.class));

        }



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
    }
}