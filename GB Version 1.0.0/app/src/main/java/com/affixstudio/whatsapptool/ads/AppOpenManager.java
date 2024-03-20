package com.affixstudio.whatsapptool.ads;

import static androidx.lifecycle.Lifecycle.Event.ON_START;
import static com.affixstudio.whatsapptool.activityOur.startScreen.haveSub;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;

import java.util.ArrayList;
import java.util.Objects;


/** Prefetches App Open Ads. */
public class AppOpenManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String LOG_TAG = "AppOpenManager";


    private final MyApplication myApplication;
    private boolean notAlreadyShowingUnity=true;

    /** Constructor */
  //  MaxAppOpenAd maxAppOpenAd;
    public AppOpenManager(MyApplication myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);


    }


    private void showAdIfReady() // applovin
    {
        if (!haveSub.equals("no") )
        {
            return;
        }
//        if (!AppLovinSdk.getInstance( currentActivity ).isInitialized() ) return;
//
//        if ( maxAppOpenAd.isReady() )
//        {
//            maxAppOpenAd.showAd();
//        }
//        else
//        {
//            maxAppOpenAd.loadAd();
//        }
    }
    private long loadTime = 0;
    /** Request an ad */



    private static boolean isShowingAd = false;








    private Activity currentActivity;

    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreCreated(activity, savedInstanceState);
        currentActivity=activity;
        subIDs.add("weekly");
        subIDs.add("monthly");
        subIDs.add("yearly");
    }
    boolean isUnityReady=false;

    /** LifecycleObserver methods */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
//        showAdIfAvailable(); // todo integrate interstial on resume
//        loadInterstitialUnity();
    //    showAdIfReady();

        showInterIfReady();
        Log.i(LOG_TAG, "onStart");
    }

    private void showInterIfReady() {

        try {
//            if (Objects.isNull(interstitialAd))
//            {
//                return;
//            }
//            if (!haveSub.equals("no") || currentActivity.toString().contains("startScreen") || !notAlreadyShowingUnity)
//            {
//                return;
//            }
//            if (  !AppLovinSdk.getInstance(myApplication.getApplicationContext() ).isInitialized() ) return;
//
//            if ( interstitialAd.isReady() )
//            {
//                interstitialAd.showAd();
//            }
//            else
//            {
//                interstitialAd.loadAd();
//            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    // MaxInterstitialAd interstitialAd;
    int retryAttempt=0;
    private void loadApplovinInter() {
        try {
            if (Objects.isNull(currentActivity) )
            {
                Log.e(LOG_TAG, "Current Activity is null, cannot load AppLovin Interstitial");
                return;
            }
//            if (  !AppLovinSdk.getInstance(myApplication.getApplicationContext() ).isInitialized() ) return;
//
//            interstitialAd = new MaxInterstitialAd( myApplication.getApplicationContext().getString(R.string.appLovin_interstitial), currentActivity );
//            interstitialAd.setListener(new MaxAdListener() {
//                @Override
//                public void onAdLoaded(MaxAd maxAd) {
//                    retryAttempt = 0;
//
//                }
//
//                @Override
//                public void onAdDisplayed(MaxAd maxAd) {
//                    notAlreadyShowingUnity=true;
//                }
//
//                @Override
//                public void onAdHidden(MaxAd maxAd) {
//                    interstitialAd.loadAd();
//                    notAlreadyShowingUnity=false;
//                }
//
//                @Override
//                public void onAdClicked(MaxAd maxAd) {
//                    notAlreadyShowingUnity=false;
//                }
//
//                @Override
//                public void onAdLoadFailed(String s, MaxError maxError)
//                {
//                    if (!interstitialAd.isReady()) {
//                        retryAttempt++;
//                        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                interstitialAd.loadAd();
//                            }
//                        }, delayMillis);
//                        notAlreadyShowingUnity = true;
//                    }
//                }
//
//                @Override
//                public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
//                    interstitialAd.loadAd();
//                    notAlreadyShowingUnity=true;
//                }
//            });
//            interstitialAd.loadAd();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    /** ActivityLifecycleCallback methods */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

      //  loadApplovinInter();




    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }
    ArrayList<String> subIDs=new ArrayList<>();
    @Override
    public void onActivityResumed(Activity activity) {


    }

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;

    }

    private IUnityAdsShowListener showListener=new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
         //   loadInterstitialUnity();
            isUnityReady=false;
            notAlreadyShowingUnity=true;

        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
            isUnityReady=false;
            notAlreadyShowingUnity=false;
           // loadInterstitialUnity();
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {

        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {

           notAlreadyShowingUnity=true;
        }

    };


}
