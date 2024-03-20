package com.affixstudio.whatsapptool.activityOur;

import static com.affixstudio.whatsapptool.activityOur.startScreen.haveSub;
import static com.affixstudio.whatsapptool.getData.GetInfo.isOnline;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.CustomSpinnerAdapter;
import com.affixstudio.whatsapptool.CustomSpinnerItem;
import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.fragmentsOur.Adepter;
import com.affixstudio.whatsapptool.getData.NetworkChangeReceiver;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomInfoDialog;

import java.util.ArrayList;

public class caption extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner customSpinner;
    ArrayList<CustomSpinnerItem> customList;
    String selection="All";
    RecyclerView recyclerView;
    int isFirstTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
    //    getSupportActionBar().hide(); //hide the title bar

        customSpinner = findViewById(R.id.customIconSpinner);
        recyclerView = findViewById(R.id.recyclerView);
        customList=getCustomList();

        bottomInfoDialog bid=new bottomInfoDialog();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        findViewById(R.id.captionTutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                bid.showinfo(caption.this,2,getString(R.string.fristInCaptionTeg));
               // startActivity(new Intent(caption.this, AppTutorials.class).putExtra("screenCode",2));
            }
        });


        CustomSpinnerAdapter adapter=new CustomSpinnerAdapter(this,customList);
        if (customSpinner != null) {
            customSpinner.setAdapter(adapter);
            customSpinner.setOnItemSelectedListener(this);
        }
        String[] catagory=getResources().getStringArray(R.array.Category);
        customSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isFirstTime==2)
                {
                    Log.i("caption","custom spinner"+isFirstTime);
                    Log.i("caption","onItemSelected index"+i);
                    if (i!=0)
                    {
                        selection=catagory[i-1];
                    }else {
                        selection="All";
                    }

                    setList();
                }
                isFirstTime=2;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (haveSub.equals("no"))
        {
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
        }


    }

    private ArrayList<CustomSpinnerItem> getCustomList() {
        customList=new ArrayList<>();
        customList.add(new CustomSpinnerItem("Category", R.drawable.category_24));
        customList.add(new CustomSpinnerItem("All",R.drawable.cp_all));
        customList.add(new CustomSpinnerItem("Greeting",R.drawable.cp_greeting));
        customList.add(new CustomSpinnerItem("Sales",R.drawable.cp_sales));
        customList.add(new CustomSpinnerItem("Promotion",R.drawable.cp_promotion));
        customList.add(new CustomSpinnerItem("Love",R.drawable.cp_love));
        customList.add(new CustomSpinnerItem("Romantic",R.drawable.cp_romantic));
        customList.add(new CustomSpinnerItem("WishBox",R.drawable.cp_wishbox));
        customList.add(new CustomSpinnerItem("Shayari",R.drawable.cp_shayari));
        customList.add(new CustomSpinnerItem("Motivation",R.drawable.cp_motivation));
        customList.add(new CustomSpinnerItem("Festival",R.drawable.cp_festival));
        customList.add(new CustomSpinnerItem("Inspiration",R.drawable.cp_inspiration));
        customList.add(new CustomSpinnerItem("Sad",R.drawable.cp_sad));
        return customList;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        CustomSpinnerItem item=(CustomSpinnerItem) adapterView.getSelectedItem();
      //  Toast.makeText(this,item.getSpinnerItemName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        isFirstTime=1;
        switch (view.getId())
        {
            case R.id.allCaption : selection="All";
                setList();
                return;
            case R.id.greetingCaption : selection="Greeting";
                setList();
                return;
            case R.id.SalesCaption:  selection="Sales";
                setList();
                return;
            case R.id.PromotionCaption : selection="Promotion";
                setList();
                return;
            case R.id.LoveCaption : selection="Love";
                setList();
                return;
            case R.id.RomanticCaption : selection="Romantic";
                setList();
                return;
            case R.id.WishCaption : selection="WishBox";
                setList();
                return;

            case R.id.ShayariCaption : selection="Shayari";
                setList();
                return;
            case R.id.MotivationCaption : selection="Motivation";
                setList();
                return;

            case R.id.FastivalCaption : selection="Festival";
                setList();
                return;
            case R.id.InspirationCaption:  selection="Inspiration";
                setList();
                return;
            case R.id.SadCaption : selection="Sad";
                setList();
                return;




        }

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        receiver=new NetworkChangeReceiver();
        registerReceiver(receiver,filter);
    }



    BroadcastReceiver receiver;
    @Override
    protected void onDestroy()
    {
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
    }






    void setList(){


        int [] array={R.array.Greeting,R.array.Sales,R.array.Promotion,R.array.Romantic,R.array.Festival,R.array.Love,
                R.array.Shayari,R.array.Motivation,R.array.Inspiration,R.array.WishBox,R.array.Sad};

        String[] Greeting=getResources().getStringArray(array[0]);
        String[] Sales=getResources().getStringArray(array[1]);
        String[] Promotion=getResources().getStringArray(array[2]);
        String[] Romantic=getResources().getStringArray(array[3]);
        String[] Festival=getResources().getStringArray(array[4]);
        String[] Love=getResources().getStringArray(array[5]);
        String[] Shayari=getResources().getStringArray(array[6]);
        String[] Motivation=getResources().getStringArray(array[7]);
        String[] Inspiration=getResources().getStringArray(array[8]);
        String[] WishBox=getResources().getStringArray(array[9]);
        String[] sad=getResources().getStringArray(array[10]);
        if (selection.equals("All"))
        {


            int totalLength=Greeting.length+Sales.length+Promotion.length
                    +Romantic.length+ Festival.length+Love.length
                    + Shayari.length+ Motivation.length+ Inspiration.length+
                    WishBox.length;

            String[] all = new String[totalLength + 1];

            System.arraycopy(Greeting,0,all,0,Greeting.length); // i1 means first of coped array i2 means coped array length
            System.arraycopy(Sales,0,all,Greeting.length,Sales.length);
            System.arraycopy(Promotion,0,all,Sales.length+Greeting.length,Promotion.length);
            System.arraycopy(Romantic,0,all,Sales.length+Greeting.length+Promotion.length,Romantic.length);
            System.arraycopy(Festival,0,all,Romantic.length+Sales.length+Greeting.length+Promotion.length,Festival.length);
            System.arraycopy(Love,0,all,Romantic.length+Festival.length+Sales.length+Greeting.length+Promotion.length,Love.length);
            System.arraycopy(Shayari,0,all,Love.length+Festival.length+Sales.length+Greeting.length+Promotion.length,Shayari.length);
            System.arraycopy(Motivation,0,all,Shayari.length+Love.length+Festival.length+Sales.length+Greeting.length+Promotion.length,Motivation.length);
            System.arraycopy(Inspiration,0,all,Motivation.length+Shayari.length+Love.length+Festival.length+Sales.length+Greeting.length+Promotion.length,Inspiration.length);
            System.arraycopy(WishBox,0,all,Motivation.length+Shayari.length+Love.length+Festival.length+Sales.length+Greeting.length+Promotion.length+Inspiration.length,WishBox.length);
            System.arraycopy(sad,0,all,Motivation.length+Shayari.length+Love.length+Festival.length+Sales.length+Greeting.length+Promotion.length+Inspiration.length+WishBox.length,sad.length);



            Adepter a=new Adepter(this,all,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);

        } else if (selection.equals("Greeting"))
        {
            RecyclerView.Adapter<Adepter.viewHolder> a=new Adepter(this,Greeting,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);
        }else if (selection.equals("Sales"))
        {
            RecyclerView.Adapter<Adepter.viewHolder> a=new Adepter(this,Sales,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);
        }else if (selection.equals("Promotion"))
        {
            RecyclerView.Adapter<Adepter.viewHolder> a=new Adepter(this,Promotion,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);
        }
        else if (selection.equals("Romantic"))
        {
            RecyclerView.Adapter<Adepter.viewHolder> a=new Adepter(this,Romantic,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);
        }else if (selection.equals("Festival"))
        {
            RecyclerView.Adapter<Adepter.viewHolder> a=new Adepter(this,Festival,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);
        }else if (selection.equals("Love"))
        {
            RecyclerView.Adapter<Adepter.viewHolder> a=new Adepter(this,Love,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);
        }else if (selection.equals("Shayari"))
        {
            RecyclerView.Adapter<Adepter.viewHolder> a=new Adepter(this,Shayari,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);
        }
        else if (selection.equals("Motivation"))
        {
            RecyclerView.Adapter<Adepter.viewHolder> a=new Adepter(this,Motivation,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);
        }else if (selection.equals("Inspiration"))
        {
            RecyclerView.Adapter<Adepter.viewHolder> a=new Adepter(this,Inspiration,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);
        }else if (selection.equals("Sad"))
        {
            RecyclerView.Adapter<Adepter.viewHolder> a=new Adepter(this,sad,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);
        }else if (selection.equals("WishBox"))
        {
            RecyclerView.Adapter<Adepter.viewHolder> a=new Adepter(this,WishBox,findViewById(R.id.captionLayout),false,this);
            recyclerView.setAdapter(a);
        }
        findViewById(R.id.catagoryLayout).setVisibility(View.GONE);
        findViewById(R.id.listLayout).setVisibility(View.VISIBLE);
        customSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (findViewById(R.id.listLayout).isShown())
        {
            findViewById(R.id.catagoryLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.listLayout).setVisibility(View.GONE);
            customSpinner.setVisibility(View.VISIBLE);
        }
    }
}