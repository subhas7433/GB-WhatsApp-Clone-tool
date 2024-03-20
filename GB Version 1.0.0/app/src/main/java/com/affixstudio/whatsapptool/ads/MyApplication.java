package com.affixstudio.whatsapptool.ads;

import android.app.Application;

public class MyApplication extends Application {

     static AppOpenManager appOpenManager;


    @Override
    public void onCreate() {
        super.onCreate();
//        AudienceNetworkAds
//                .buildInitSettings(this)
//                .initialize();
//        AdSettings.setDataProcessingOptions( new String[] {} );
//        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
//        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener()
//        {
//            @Override
//            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
//            {
//                appOpenManager = new AppOpenManager(MyApplication.this);
//            }
//        });

       // loadInterstitialUnity();

    }

}
