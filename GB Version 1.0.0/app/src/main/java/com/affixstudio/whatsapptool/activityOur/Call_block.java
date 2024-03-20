package com.affixstudio.whatsapptool.activityOur;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.WACallblock;
import com.affixstudio.whatsapptool.WBCallBlock;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomInfoDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.TimeUnit;

public class Call_block extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_block);






        TabLayout tabLayout = findViewById(R.id.tab_for_textFun);
        ViewPager viewPager=findViewById(R.id.viewPagerTextFun);


        Adapter adapter = new Adapter(getSupportFragmentManager());



        tabLayout.setupWithViewPager(viewPager);

        Fragment whatsapp=new WACallblock();
        Fragment whatsappBusiness=new WBCallBlock();

        adapter.addFragment(whatsapp,"WhatsApp");
        adapter.addFragment(whatsappBusiness,"WA Business");




        viewPager.setAdapter(adapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.whatsapp_focused);
        tabLayout.getTabAt(1).setIcon(R.drawable.ws_business_20);


        String tableName=getString(R.string.CallBlockListtable2);

        String query="CREATE TABLE IF NOT EXISTS CallBlockListTable2 (_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',Number text DEFAULT 'Not set',IsGroup text DEFAULT '0',WAorWB text DEFAULT '1') ";



        database db=new database(this,tableName,query,1);
        db.getWritableDatabase();
        db.close();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.callblockTutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomInfoDialog bid=new bottomInfoDialog();

                bid.showinfo(Call_block.this,12,getString(R.string.fristInCallBlockTeg));

            }
        });

//        UnityAds.initialize (this, getString(R.string.gameID), false);
//
//        //loadInterstitialUnity();
//
//        UnityBannerAds unityBannerAds=new UnityBannerAds(this,findViewById(R.id.fl_adplaceholder),0);
//        unityBannerAds.show();

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



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("DefaultLocale")
    private String getTimeString(long millis) {
        String amOrpm="am";
        long hour= TimeUnit.MILLISECONDS.toHours(millis);



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
}