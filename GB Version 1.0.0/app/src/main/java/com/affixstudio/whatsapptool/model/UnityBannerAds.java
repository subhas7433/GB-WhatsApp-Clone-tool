package com.affixstudio.whatsapptool.model;

import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;

import com.affixstudio.whatsapptool.R;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class UnityBannerAds {

    Activity a;
    FrameLayout frameLayout;
    int indi;

    public UnityBannerAds(Activity a, FrameLayout frameLayout, int indi) {
        this.a = a;
        this.frameLayout = frameLayout;
        this.indi = indi;
    }



  public void show()
    {
        UnityBannerSize size=new UnityBannerSize(320, 50);

        if (indi==1)
        {
            size=new UnityBannerSize(450,450);
        }
        BannerView nativeB =new BannerView(a,a.getString(R.string.unityBannerID), size);
        nativeB.setListener(bannerListener);

        nativeB.load();
    }
    private BannerView.IListener bannerListener = new BannerView.IListener()
    {
        @Override
        public void onBannerLoaded(BannerView bannerAdView)
        {


            frameLayout.addView(bannerAdView);
            // Called when the banner is loaded.
            Log.v("UnityAdsExample", "onBannerLoaded: " + bannerAdView.getPlacementId());
            // Enable the correct button to hide the ad

        }


        @Override
        public void onBannerClick(BannerView bannerAdView) {
            // Called when a banner is clicked.
            Log.v("UnityAdsExample", "onBannerClick: " + bannerAdView.getPlacementId());
        }

        @Override
        public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {

        }

        @Override
        public void onBannerLeftApplication(BannerView bannerAdView) {
            // Called when the banner links out of the application.
            Log.v("UnityAdsExample", "onBannerLeftApplication: " + bannerAdView.getPlacementId());
        }
    };

}
