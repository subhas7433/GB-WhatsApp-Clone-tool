package com.affixstudio.whatsapptool.fragmentsOur;


import static android.content.Context.POWER_SERVICE;
import static com.affixstudio.whatsapptool.activityOur.startScreen.adsDetails;
import static com.affixstudio.whatsapptool.activityOur.startScreen.haveSub;
import static com.affixstudio.whatsapptool.activityOur.startScreen.purchaseON;
import static com.affixstudio.whatsapptool.activityOur.startScreen.shouldShowUnity;
import static com.affixstudio.whatsapptool.fragmentsOur.DecorationText.Adepter.isInstalled;
import static com.affixstudio.whatsapptool.modelOur.sound.playSound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activity.main.MainActivity;
import com.affixstudio.whatsapptool.activityOur.Call_block;
import com.affixstudio.whatsapptool.activityOur.private_chat;
import com.affixstudio.whatsapptool.activityOur.schedule_sms;
import com.affixstudio.whatsapptool.adopterOur.SliderAdapter;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.modelOur.setOnRecycleClick;
import com.affixstudio.whatsapptool.whatsappwebtogo.WebviewActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment  {
    setOnRecycleClick setOnRecycleClick;
  //  private MaxAd loadedNativeAd;

    public HomeFragment(com.affixstudio.whatsapptool.modelOur.setOnRecycleClick setOnRecycleClick) {
        this.setOnRecycleClick = setOnRecycleClick;
    }

    public static int shouldRecoverMediaFocused=0; // helps to open  shortcuts
    public HomeFragment() {
    }



    View view;
    SliderView sliderView;
    int[] images = {R.drawable.ads_callblock,
            R.drawable.ads_no_blu_tick,
            R.drawable.ads_restore_msg,
            R.drawable.ads_auto_reply};

  //  MaxInterstitialAd interstitialAd;
    int retryAttempt=0;
    Context c;
    public static boolean surveyLoaded=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        view=inflater.inflate(R.layout.fragment_home, container, false);
        c=getContext();
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.layout_bg, getContext().getTheme()));


        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

        String date=sdf.format(new Date());



        String query="CREATE TABLE IF NOT EXISTS userPremiumInfoTable (_id INTEGER PRIMARY KEY autoincrement, DateRemaining text DEFAULT '0',LastUpdated text DEFAULT '0') ";

        // Load the first ad
        database db=new database(c,getString(R.string.userPremiumInfoTable),query,1);


//        Params params = new Params.Builder(getString(R.string.pollfishAPIKey))
//                .rewardMode(true)
//                .pollfishSurveyCompletedListener(new PollfishSurveyCompletedListener()
//                {
//                    @Override
//                    public void onPollfishSurveyCompleted(@NonNull SurveyInfo surveyInfo) {
//
//                        surveyLoaded=false;
//                        if (Objects.isNull(surveyInfo.getRewardValue()))
//                        {
//                            return;
//                        }
//                        Toast.makeText(c, "You Got premium of "+surveyInfo.getRewardValue()+" days", Toast.LENGTH_LONG).show();
//                        premiumDays+=surveyInfo.getRewardValue();
//                        String sql="UPDATE "+getString(R.string.userPremiumInfoTable)+" SET DateRemaining="+premiumDays;
//                        db.getWritableDatabase().execSQL(sql);
//                       // days.setText(premiumDays+"");
//                    }
//                }).offerwallMode(true)
//                .pollfishSurveyNotAvailableListener(new PollfishSurveyNotAvailableListener() {
//                    @Override
//                    public void onPollfishSurveyNotAvailable()
//                    {
//
//                    }
//                })
//                .pollfishSurveyReceivedListener(new PollfishSurveyReceivedListener()
//                {
//                    @Override
//                    public void onPollfishSurveyReceived(@Nullable SurveyInfo surveyInfo)
//                    {
//                        surveyLoaded=true;
//                        //Toast.makeText(c, "Survey received", Toast.LENGTH_SHORT).show();
//
//                    }
//                })
//                .releaseMode(true)
//                .build();
//        Pollfish.initWith(getActivity(), params);
//




//        Cursor cursor=db.getinfo(0);
//
//        if (cursor.moveToNext())
//        {
//            premiumDays=cursor.getInt(1);
//            lastPremiumUpdated=cursor.getString(2);
//        }
//        else {
//
//            ContentValues values=new ContentValues();
//            values.put("DateRemaining","1");
//            values.put("LastUpdated","12/12/22");
//            db.getWritableDatabase().insert(getString(R.string.userPremiumInfoTable),null,values);
//            premiumDays=0;
//            lastPremiumUpdated=date;
//
//        }
//
//        if (premiumDays>0 && lastPremiumUpdated.equals(date)) // decreasing premium  date
//        {
//            String[] oldDate=lastPremiumUpdated.split("/");
//            String[] newDate=date.split("/");
//
//            int dateDifference=1/*getDateDifference(oldDate,newDate)*/;
//            int remaingDate=0;
//
//            if (dateDifference<premiumDays )
//            {
//                remaingDate=premiumDays-dateDifference;
//            }
//            String sql="UPDATE "+getString(R.string.userPremiumInfoTable)+" SET DateRemaining='"+remaingDate+"', LastUpdated='"+date+"'";
//            db.getWritableDatabase().execSQL(sql);
//
//            premiumDays=remaingDate;
//            Toast.makeText(c, "Updated", Toast.LENGTH_SHORT).show();
//
//        }
//        if (premiumDays>0)
//        {
//            haveSub="yes";
//        }

        EditText whatsappNumber=view.findViewById(R.id.whatsappNumber); // home direct chat
        CountryCodePicker ccp=view.findViewById(R.id.ccp);




//
//        interstitialAd = new MaxInterstitialAd( getString(R.string.appLovin_interstitial), getActivity() );
//        interstitialAd.setListener(new MaxAdListener() {
//            @Override
//            public void onAdLoaded(MaxAd maxAd) {
//                retryAttempt = 0;
//            }
//
//            @Override
//            public void onAdDisplayed(MaxAd maxAd) {
//
//            }
//
//            @Override
//            public void onAdHidden(MaxAd maxAd) {
//                interstitialAd.loadAd();
//                openFun();
//            }
//
//            @Override
//            public void onAdClicked(MaxAd maxAd) {
//
//            }
//
//            @Override
//            public void onAdLoadFailed(String s, MaxError maxError) {
//                if (!interstitialAd.isReady()) // if there is not ads load ads
//                {
//                    retryAttempt++;
//                    long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            interstitialAd.loadAd();
//                        }
//                    }, delayMillis);
//                }
//            }
//
//            @Override
//            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
//                interstitialAd.loadAd();
//            }
//        });
//
//
//
//
//
//        if (haveSub.equals("no"))
//        {
//            interstitialAd.loadAd();
//            loadApplovinNative();
//
//        }
//        interstitialAd.loadAd();
//        loadApplovinNative();
//        adView.loadAd();

        // FrameLayout unityBannerHolder=view.findViewById(R.id.fl_adplaceholder);
        // loadNativeAds();
        UnityAds.initialize (getContext(), getString(R.string.gameID), false);

        loadInterstitialUnity();





        //unityBannerHolder.addView(nativeB);

        // Initialize the SDK:


        SliderView sliderView = view.findViewById(R.id.image_slider);
        CardView slidesCard=view.findViewById(R.id.slidesCard);
        Log.i("Home","adsDetails "+adsDetails.size());
        if (adsDetails.size()>0 && haveSub.equals("no"))
        {
            slidesCard.setVisibility(View.VISIBLE);
            SliderAdapter sliderAdapter = new SliderAdapter(images,getContext(),setOnRecycleClick);

            sliderView.setSliderAdapter(sliderAdapter);
            sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
            sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
            sliderView.startAutoCycle();
        }
        else {
            slidesCard.setVisibility(View.GONE);
        }




        pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
        betteryOpLA=view.findViewById(R.id.betteryOpLA);

        view.findViewById(R.id.betteryOp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offOptimizition();
            }
        });
        if (!pm.isIgnoringBatteryOptimizations(getContext().getPackageName()))
        {
            betteryOpLA.setVisibility(View.VISIBLE);
        }else {
            betteryOpLA.setVisibility(View.GONE);
        }



        view.findViewById(R.id.directchat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setOnRecycleClick.onItemEdit(7, ""); //sending data to mainActivity to change navigation selection


            }
        });
        view.findViewById(R.id.videoBlockLA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Call_block.class));
//                if (((isUnityReady && shouldShowUnity==1)|| interstitialAd.isReady()) && haveSub.equals("no"))
//                {
//                    openActivity=1;
//                    showInterstitialUnity();
//                }else {
//                    startActivity(new Intent(getContext(), Call_block.class));
//                }

            }
        });
        view.findViewById(R.id.openMediaRecoverLA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shouldRecoverMediaFocused=1;
                try {
                    // getContext().startActivity(new Intent(getContext(), recoverMessage.class));

                    setOnRecycleClick.onItemEdit(6,""); //sending data to mainActivity to change navigation selection
                }catch (Exception e)
                {
                    Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.findViewById(R.id.openNoBlueMediaLA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shouldRecoverMediaFocused=1;
                startActivity(new Intent(getContext(), private_chat.class));

//                if (((isUnityReady && shouldShowUnity==1)|| interstitialAd.isReady()) && haveSub.equals("no"))
//                {
//                    openActivity=4;
//                    showInterstitialUnity();
//                }else {
//                    shouldRecoverMediaFocused=1;
//                    startActivity(new Intent(getContext(), private_chat.class));
//                }



            }
        });
        view.findViewById(R.id.specificAutoLA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shouldRecoverMediaFocused=1;
                startActivity(new Intent(getContext(), MainActivity.class));


//                if (((isUnityReady && shouldShowUnity==1)|| interstitialAd.isReady()) && haveSub.equals("no"))
//                {
//                    openActivity=5;
//                    showInterstitialUnity();
//                }else {
//                    shouldRecoverMediaFocused=1;
//                    startActivity(new Intent(getContext(), MainActivity.class));
//                }

            }
        });
        view.findViewById(R.id.scheduleCallBlockLA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getContext(), Call_block.class));



//                if (((isUnityReady && shouldShowUnity==1)|| interstitialAd.isReady()) && haveSub.equals("no"))
//                {
//                    openActivity=2;
//                    showInterstitialUnity();
//                }else {
//                    startActivity(new Intent(getContext(), Call_block.class));
//                }

            }
        });

        CardView openAutoreply = view.findViewById(R.id.autoReply_card_btn);
        openAutoreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getContext(),MainActivity.class);
                startActivity(i);

//                if (((isUnityReady && shouldShowUnity==1)|| interstitialAd.isReady()) && haveSub.equals("no"))
//                {
//                    openActivity=6;
//                    showInterstitialUnity();
//                }else {
//                    Intent i = new Intent(getContext(),MainActivity.class);
//                    startActivity(i);
//                }



            }
        });

        CardView OpenRecoverMessageBT=view.findViewById(R.id.recoverMessageButton);
        OpenRecoverMessageBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // getContext().startActivity(new Intent(getContext(), recoverMessage.class));
//                    sendAnalytics.send(getString(R.string.firebaseItemNameRecoverOpen));

                    setOnRecycleClick.onItemEdit(6,""); //sending data to mainActivity to change navigation selection

                }catch (Exception e)
                {
                    Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
                }

            }
        });

        view.findViewById(R.id.startDirectChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sendAnalytics.send(getString(R.string.firebaseItemNameDirectChat));
                setOnRecycleClick.onItemEdit(4, ""); //sending data to mainActivity to change navigation selection
            }
        });

        view.findViewById(R.id.removeAds).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   startActivity(new Intent(getContext(), PurchaseActivity.class));
            }
        });

        if (purchaseON==1)  // todo change it to zero
        {
            view.findViewById(R.id.removeAds).setVisibility(View.GONE);
        }
        view.findViewById(R.id.openWhatsGPT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=com.affixstudio.chat.ai.ad")));

            }
        });





        CardView quickMessage=view.findViewById(R.id.quickMessage);
        quickMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

//                    sendAnalytics.send(getString(R.string.firebaseItemNameQuickMsg));
                    setOnRecycleClick.onItemEdit(2, ""); //sending data to mainActivity to change navigation selection

                }catch (Exception e)
                {
                    Log.e("setFQuickMessage",e.getMessage());
                }

            }
        });

        view.findViewById(R.id.noBlueTick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getContext(), private_chat.class));

//                if (((isUnityReady && shouldShowUnity==1)|| interstitialAd.isReady()) && haveSub.equals("no"))
//                {
//                    openActivity=9;
//                    showInterstitialUnity();
//                }else {
//                    startActivity(new Intent(getContext(), private_chat.class));
//                }

            }
        });

        CardView textRepeter=view.findViewById(R.id.textRepeter);
        textRepeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {



//                    sendAnalytics.send(getString(R.string.firebaseItemNameTextRepeter));
                    setOnRecycleClick.onItemEdit(3, ""); //sending data to mainActivity to change navigation selection
                }catch (Exception e)
                {
                    Log.e("setFTextRepeater",e.getMessage());
                }

            }
        });


        CardView smsTemplate=view.findViewById(R.id.smsTemplate);
        smsTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), com.affixstudio.whatsapptool.activityOur.caption.class);
                startActivity(i);

//                if (((isUnityReady && shouldShowUnity==1)|| interstitialAd.isReady()) && haveSub.equals("no"))
//                {
//                    openActivity=7;
//                    showInterstitialUnity();
//                }else {
//                    Intent i = new Intent(getContext(), com.affixstudio.whatsapptool.activityOur.caption.class);
//                    startActivity(i);
//                }

            }
        });

        CardView callBlock=view.findViewById(R.id.callBlock);
        callBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), com.affixstudio.whatsapptool.activityOur.Call_block.class);
                startActivity(i);

//                if (((isUnityReady && shouldShowUnity==1)|| interstitialAd.isReady()) && haveSub.equals("no"))
//                {
//                    openActivity=3;
//                    showInterstitialUnity();
//                }else {
//                    Intent i = new Intent(getContext(), com.affixstudio.whatsapptool.activityOur.Call_block.class);
//                    startActivity(i);
//                }
            }
        });


        CardView openSchedule=view.findViewById(R.id.scheduleButton);


        SharedPreferences sp= getContext().getSharedPreferences("affix", Context.MODE_PRIVATE);



        openSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (sp.getInt(getContext().getString(R.string.isScheduleONTag),1)==0) {
                        Toast.makeText(getContext(), "Coming soon..", Toast.LENGTH_LONG).show();
                    }else {
                        Intent i = new Intent(getContext(), schedule_sms.class);
                        startActivity(i);
//                        if (((isUnityReady && shouldShowUnity==1)|| interstitialAd.isReady()) && haveSub.equals("no"))
//                        {
//                            openActivity=0;
//                            showInterstitialUnity();
//                        }else {
//
//                        Intent i = new Intent(getContext(), schedule_sms.class);
//                        startActivity(i);
//
//                    }
                        // getContext().startActivity(new Intent(getContext(), schedule_sms.class));
                    }
                }catch (Exception e)
                {
                    Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        CardView web=view.findViewById(R.id.whatsappWeb);
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getContext(), WebviewActivity.class);
                startActivity(i);


//                if (((isUnityReady && shouldShowUnity==1)|| interstitialAd.isReady()) && haveSub.equals("no"))
//                {
//                    openActivity=8;
//                    showInterstitialUnity();
//
//
//                }
//                else
//                {
//                    Intent i = new Intent(getContext(), WebviewActivity.class);
//                    startActivity(i);
//                }
            }
        });








        CardView openDecoration=view.findViewById(R.id.decorationText);
        openDecoration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


//                    sendAnalytics.send(getString(R.string.firebaseItemNameDesignText));
                    setOnRecycleClick.onItemEdit(1, ""); //sending data to mainActivity to change navigation selection

                }catch (Exception e)
                {
                    Log.e("setFDecorationText",e.getMessage());
                }

            }
        });
        CardView openAscciface=view.findViewById(R.id.asciiFace);
        openAscciface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


//                    sendAnalytics.send(getString(R.string.firebaseItemNameAsciiFace));
                    setOnRecycleClick.onItemEdit(0, ""); //sending data to mainActivity to change navigation selection
                }catch (Exception e)
                {
                    Log.e("setFasciiFaces",e.getMessage());
                }
            }
        });



        TextView openDirectChat=view.findViewById(R.id.startDirectChat);
        openDirectChat.setOnClickListener(view1 -> {
//            sendAnalytics.send(getString(R.string.firebaseItemNameDirectChat));
            setOnRecycleClick.onItemEdit(4, ""); //sending data to mainActivity to change navigation selection

        });

        CardView openStatus=view.findViewById(R.id.openStatus);
        openStatus.setOnClickListener(view1 -> {
//            sendAnalytics.send(getString(R.string.firebaseItemNameStatusDownload));
            setOnRecycleClick.onItemEdit(5, ""); //sending data to mainActivity to change navigation selection
        });


        return view;
    }



    private void showInterstitialUnity() {
        openFun();
//        if ( interstitialAd.isReady() && shouldShowUnity==0)
//        {
//            interstitialAd.showAd();
//        }else if (isUnityReady && shouldShowUnity==1)
//        {
//            UnityAds.show(getActivity(),getString(R.string.interstitalAdIdUnity),showListener);
//        }else {
//            openFun();
//        }

    }
    PowerManager pm;
    LinearLayout betteryOpLA;
    @Override
    public void onResume() {
        super.onResume();
        if (!pm.isIgnoringBatteryOptimizations(getContext().getPackageName()))
        {
            betteryOpLA.setVisibility(View.VISIBLE);
        }
        else {
            betteryOpLA.setVisibility(View.GONE);
        }
    }
    private void offOptimizition() {

        Intent intent1 = new Intent();
        String packageName=getActivity().getPackageName();




        if (!pm.isIgnoringBatteryOptimizations(packageName))

        {
            intent1.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent1.setData(Uri.parse("package:" + packageName));
            startActivity(intent1);
        }
        else
        {
            Toast.makeText(getContext(), "Already Done", Toast.LENGTH_LONG).show();
        }
    }

    void showSnackBar(String message, int color)
    {
        if (color==R.color.red)
        {
            playSound(getContext(),R.raw.error_sound);
        }
        Snackbar.make(view.findViewById(R.id.snackLayout),message, BaseTransientBottomBar.LENGTH_SHORT)
                .setTextColor(ContextCompat.getColor(getContext(),R.color.white))
                .setBackgroundTint(ContextCompat.getColor(getContext(), color)).show();
    }
    private void sendDirectMessage(String countryCode, String editNumber,String packageName)
    {


        //Toast.makeText(getContext(), ""+ccp.getSelectedCountryCode(), Toast.LENGTH_LONG).show();
        String number=editNumber.trim();

        if (!isInstalled(getContext(),packageName))
        {
            showSnackBar(packageName.substring(4,11)+" is not installed",R.color.red);
        }else if (number.length()==10)
        {

            try {

                String stringUri="https://api.whatsapp.com/send?phone="+countryCode + number;
//                if (message.getEditableText()==null)
//                {
//                    stringUri="https://api.whatsapp.com/send?phone=" + countryCode +mobile;
//                }else
//                {
//                    stringUri="https://api.whatsapp.com/send?phone=" + countryCode +mobile+"&text=" + message.getEditableText().toString();
//                }
//                String msg = "Its Working";


                //  saveInDatabase();



                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(stringUri)).setPackage(packageName));
            }catch (Exception e){
                Log.e("homeDirect",e.getMessage());
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


    private void openTextFragemnt(Bundle bundle) {

        textFunction textFunction=new textFunction();
        textFunction.setArguments(bundle);


        getFragmentManager().beginTransaction()
                .replace(R.id.container,  textFunction)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    boolean isUnityReady=false;
    private void loadInterstitialUnity() {
        if (shouldShowUnity==0)
        {
            return;
        }
        UnityAds.load("Interstitial_Android", new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String s) {
                isUnityReady=true;
            }

            @Override
            public void onUnityAdsFailedToLoad(String s, UnityAds.UnityAdsLoadError unityAdsLoadError, String s1) {

            }
        });


    }
    int openActivity=-1;
    private IUnityAdsShowListener showListener=new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            loadInterstitialUnity();
            isUnityReady=false;
            switch (openActivity) // schedule
            {
                case 0: //schedule
                    Intent i = new Intent(getContext(), schedule_sms.class);
                    startActivity(i);
                    return;
                case 1://
                    startActivity(new Intent(getContext(), Call_block.class));
                    return;
                case 2:
                    startActivity(new Intent(getContext(), Call_block.class));
                    return;
                case 3:
                    Intent in = new Intent(getContext(), Call_block.class);
                    startActivity(in);
                    return;
                case 4:
                    shouldRecoverMediaFocused = 1;
                    startActivity(new Intent(getContext(), private_chat.class));
                    return;
                case 5: shouldRecoverMediaFocused = 1;
                    startActivity(new Intent(getContext(), com.affixstudio.whatsapptool.activity.main.MainActivity.class));
                    return;
                case 6:

                    startActivity(new Intent(getContext(), com.affixstudio.whatsapptool.activity.main.MainActivity.class));
                    return;
                case 7:Intent il = new Intent(getContext(), com.affixstudio.whatsapptool.activityOur.caption.class);
                    startActivity(il);
                    return;

                case 8: startActivity(new Intent(getContext(), WebviewActivity.class));

                    return;
                case 9: startActivity(new Intent(getContext(), private_chat.class));


            }
        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
            isUnityReady=false;
            loadInterstitialUnity();
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {

        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            openFun();

        }
    };

    private void openFun() {
        switch (openActivity) // schedule
        {
            case 0: //schedule
                Intent i = new Intent(getContext(), schedule_sms.class);
                startActivity(i);
                return;
            case 1://
                startActivity(new Intent(getContext(), Call_block.class));
                return;
            case 2:
                startActivity(new Intent(getContext(), Call_block.class));
                return;
            case 3:
                Intent in = new Intent(getContext(), Call_block.class);
                startActivity(in);
                return;
            case 4:
                shouldRecoverMediaFocused = 1;
                startActivity(new Intent(getContext(), private_chat.class));
                return;
            case 5: shouldRecoverMediaFocused = 1;
                startActivity(new Intent(getContext(), com.affixstudio.whatsapptool.activity.main.MainActivity.class));
                return;
            case 6:

                startActivity(new Intent(getContext(), com.affixstudio.whatsapptool.activity.main.MainActivity.class));
                return;
            case 7:Intent il = new Intent(getContext(), com.affixstudio.whatsapptool.activityOur.caption.class);
                startActivity(il);
                return;

            case 8: startActivity(new Intent(getContext(), WebviewActivity.class));

                return;
            case 9: startActivity(new Intent(getContext(), private_chat.class));


        }
    }

//    private void loadApplovinNative() {
//
//
//
//
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
//        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader( getString(R.string.applovinNativeADID), getContext() );
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
//                        nativeAdLoader.loadAd( new MaxNativeAdView( binder, c ) );
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
//                nativeAdLoader.loadAd( new MaxNativeAdView( binder, c) );
//            }
//
//            @Override
//            public void onNativeAdExpired(MaxAd maxAd) {
//                super.onNativeAdExpired(maxAd);
//                nativeAdLoader.loadAd( new MaxNativeAdView( binder,c ) );
//            }
//        });
//
//        nativeAdLoader.loadAd( new MaxNativeAdView( binder,c  ) );
//    }


}