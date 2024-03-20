package com.affixstudio.whatsapptool.activityOur;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.fragmentsOur.AsciiFaces;
import com.affixstudio.whatsapptool.fragmentsOur.DecorationText;
import com.affixstudio.whatsapptool.fragmentsOur.QuickMessage;
import com.affixstudio.whatsapptool.fragmentsOur.TextRepeater;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TextFunction extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (getIntent().getBooleanExtra("fromHome",true) && getIntent().getBooleanExtra("directChat",false)){
//            finish();
//            startActivity(new Intent(this,MainActivity.class));
//        }else
//        {
//
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_function);
        //getSupportActionBar().hide(); //hide the title bar

        AsciiFaces asciiFaces=new AsciiFaces();
        DecorationText decorationText=new DecorationText();
        QuickMessage quickMessage=new QuickMessage();
        TextRepeater textRepeater=new TextRepeater();


        //FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //sendAnalytics sendAnalytics=new sendAnalytics(mFirebaseAnalytics);

        /*
        * #todo have to set intent value to back to Dirct chat or schedule
        *
        *
        *
        * */


        TabLayout tabLayout = findViewById(R.id.tab_for_textFun);
        ViewPager viewPager=findViewById(R.id.viewPagerTextFun);


        Adapter adapter = new Adapter(getSupportFragmentManager());



        tabLayout.setupWithViewPager(viewPager);

        adapter.addFragment(asciiFaces,"Ascii");
        adapter.addFragment(decorationText,"Design");
        adapter.addFragment(quickMessage,"Quickly");
        adapter.addFragment(textRepeater,"Repeat");



        viewPager.setAdapter(adapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.ascii_fc_logo);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_functions_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.quick_sms_focused);
        tabLayout.getTabAt(3).setIcon(R.drawable.repeat_icon);

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                if (getIntent().getIntExtra("value",0)==0)
                {
                    //sendAnalytics.send(getString(R.string.firebaseItemNameAsciiFace));
                    tabLayout.selectTab(tabLayout.getTabAt(0));
                }else if (getIntent().getIntExtra("value",0)==1)
                {
                    //sendAnalytics.send(getString(R.string.firebaseItemNameDesignText));
                    tabLayout.selectTab(tabLayout.getTabAt(1));
                }else if (getIntent().getIntExtra("value",0)==2)
                {
                    //sendAnalytics.send(getString(R.string.firebaseItemNameQuickMsg));
                    tabLayout.selectTab(tabLayout.getTabAt(2));
                }else if (getIntent().getIntExtra("value",0)==3)
                {
                    //sendAnalytics.send(getString(R.string.firebaseItemNameTextRepeter));
                    tabLayout.selectTab(tabLayout.getTabAt(3));
                }else if (getIntent().getIntExtra("value",0)==4)
                {
                    tabLayout.selectTab(tabLayout.getTabAt(4));
                }
            }
        });

        thread.start();




        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });









    }
}
class Adapter extends FragmentPagerAdapter {




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


