package com.affixstudio.whatsapptool.fragmentsOur;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomInfoDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class textFunction extends Fragment {

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.white, getContext().getTheme()));

         v = inflater.inflate(R.layout.fragment_text_function, container, false);

      //  FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

     //   sendAnalytics sendAnalytics=new sendAnalytics(mFirebaseAnalytics);

        com.affixstudio.whatsapptool.fragmentsOur.AsciiFaces asciiFaces = new com.affixstudio.whatsapptool.fragmentsOur.AsciiFaces();
        com.affixstudio.whatsapptool.fragmentsOur.DecorationText decorationText = new com.affixstudio.whatsapptool.fragmentsOur.DecorationText();
        com.affixstudio.whatsapptool.fragmentsOur.QuickMessage quickMessage = new com.affixstudio.whatsapptool.fragmentsOur.QuickMessage();
        com.affixstudio.whatsapptool.fragmentsOur.TextRepeater textRepeater = new com.affixstudio.whatsapptool.fragmentsOur.TextRepeater();

        TabLayout tabLayout = v.findViewById(R.id.tab_for_textFun);
        ViewPager viewPager = v.findViewById(R.id.viewPagerTextFun);
        Adapter adapter = new Adapter(getActivity().getSupportFragmentManager());

        tabLayout.setupWithViewPager(viewPager);

        adapter.addFragment(asciiFaces, "Ascii");
        adapter.addFragment(decorationText, "Design");
        adapter.addFragment(quickMessage, "Quickly");
        adapter.addFragment(textRepeater, "Repeat");

        viewPager.setAdapter(adapter);


        tabLayout.getTabAt(0).setIcon(R.drawable.ascii_fc_logo);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_functions_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.quick_sms_focused);
        tabLayout.getTabAt(3).setIcon(R.drawable.repeat_icon);

        Bundle bundle =this.getArguments(); //nguyencse https://stackoverflow.com/questions/16036572/how-to-pass-values-between-fragments





       // tabLayout.selectTab(tabLayout.getTabAt(ThreadLocalRandom.current().nextInt(1, 3 + 1)));

        v.findViewById(R.id.tutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               int i= tabLayout.getSelectedTabPosition();
                bottomInfoDialog bid=new bottomInfoDialog();
               if (i==0) // asic
               {
                 //  sendAnalytics.send(getString(R.string.firebaseItemNameAsciiFace));
                   bid.showinfo(getContext(),1,getString(R.string.fristInTextFunTeg));
               }else if (i==1) //decoration
               {
                  // sendAnalytics.send(getString(R.string.firebaseItemNameDesignText));
                   bid.showinfo(getContext(),4,getString(R.string.fristInTextFunTeg));

               }else if (i==2) // quick
               {
                 //  sendAnalytics.send(getString(R.string.firebaseItemNameQuickMsg));
                   bid.showinfo(getContext(),5,getString(R.string.fristInTextFunTeg));
               }else if (i==3) // repeat
               {
                 //  sendAnalytics.send(getString(R.string.firebaseItemNameTextRepeter));
                   bid.showinfo(getContext(),9,getString(R.string.fristInTextFunTeg));
               }


            }
        });

        if (bundle != null) {
            // handle your code here.

            if (bundle.getInt("value",0) == 0) { //getting data from another fragment
                viewPager.setCurrentItem(0);
                tabLayout.selectTab(tabLayout.getTabAt(0));
            } else if (bundle.getInt("value",0) == 1) {
                tabLayout.selectTab(tabLayout.getTabAt(1));
            } else if (bundle.getInt("value",0) == 2) {
                tabLayout.selectTab(tabLayout.getTabAt(2));
            } else if (bundle.getInt("value",0) == 3) {
                tabLayout.selectTab(tabLayout.getTabAt(3));
            } else if (bundle.getInt("value",0) == 4) {
                tabLayout.selectTab(tabLayout.getTabAt(4));
            }
        }





        return v;
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