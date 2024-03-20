package com.affixstudio.whatsapptool.fragmentsOur;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.affixstudio.whatsapptool.fragmentsOur.HomeFragment.shouldRecoverMediaFocused;
import static com.affixstudio.whatsapptool.modelOur.sound.playSound;
import static com.affixstudio.whatsapptool.serviceOur.serviceTool.isServiceRunning;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.private_chat;
import com.affixstudio.whatsapptool.fragment_private_media;
import com.affixstudio.whatsapptool.fragment_private_recover_chat;
import com.affixstudio.whatsapptool.getData.GetInfo;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomInfoDialog;
import com.affixstudio.whatsapptool.serviceOur.mediaWatcher;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class recoverSms extends Fragment {

    View v;
    final int NOTIFICATION_PERMISSION =234 ;
    SharedPreferences sp;
    SharedPreferences.Editor spE;
    SwitchCompat recoverChatSwitch;
    Intent intent;

    Dialog notiPer,storageP;
    @Override
    public void onResume() {
        super.onResume();


        if (isWentForPermission==1) // was trying to on the private chat

        {
            i("resume isWentForPermission 1");
            turnOnRecoverChat(true );
            isWentForPermission=0;
        }else {
            recoverChatSwitch.setChecked(getInfo.shouldOn(ctx,getString(R.string.recoverMediaOnTag)));
        }







    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_recover_main, container, false);

        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.VA_off, getContext().getTheme()));
        ctx=getContext();
        fragment_private_recover_chat fragment_restore_chat = new fragment_private_recover_chat();
        fragment_private_media media = new fragment_private_media();

        TabLayout tabLayout = v.findViewById(R.id.tab_recoverMsg);
        ViewPager viewpager = v.findViewById(R.id.viewpager_recover);

        Adapter adapter = new Adapter(getActivity().getSupportFragmentManager());

        tabLayout.setupWithViewPager(viewpager);

        adapter.addFragment(fragment_restore_chat, "Message");
        adapter.addFragment(media, "Media");

        viewpager.setAdapter(adapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.message_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.image_icon);

         sp=ctx.getSharedPreferences("affix", Context.MODE_PRIVATE);
         spE=sp.edit();
        intent=new Intent(ctx, mediaWatcher.class);

        v.findViewById(R.id.tutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomInfoDialog bid=new bottomInfoDialog();
                bid.showinfo(getContext(),6,getString(R.string.fristInRecoveTeg));
            }
        });

        if (shouldRecoverMediaFocused==1)
        {
            tabLayout.selectTab(tabLayout.getTabAt(1));
            shouldRecoverMediaFocused=0;
        }





         recoverChatSwitch=v.findViewById(R.id.recoverChatSwitch);
        recoverChatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) // todo check the permission before turning on
                    //todo when deleteing any file just delete it from database not from file
                {


                    turnOnRecoverChat(b);


                }
                else
                {

                    turnOnRecoverChat(b);
                }
            }
        });




        notiPer=new Dialog(getContext());
        storageP=new Dialog(getContext());

        notiPer.setCancelable(false);
        storageP.setCancelable(false);

        notiPer.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.custom_dialog));
        storageP.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.custom_dialog));

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    startActivityForResult(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS),NOTIFICATION_PERMISSION);
                }

            } //todo fix multinotification issue in private chat
        });
        closeN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notiPer.dismiss();
                isWentForPermission=0;
                recoverChatSwitch.setChecked(false);

            }
        });


        grantStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageP.dismiss();
                isWentForPermission=1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getInfo.requestPermissionQ(ctx,activityResultLauncher);
                }
            }
        });
        closeS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageP.dismiss();
                isWentForPermission=0;
                recoverChatSwitch.setChecked(false);
            }
        });

        notiPer.setContentView(notiV);
        storageP.setContentView(stoV);





        recoverChatSwitch.setChecked(getInfo.shouldOn(ctx,getString(R.string.recoverMediaOnTag)));








        return v;
    }

    GetInfo getInfo=new GetInfo();
    Context ctx;
    private int isWentForPermission=0;

    private void turnOnRecoverChat(boolean isOn) {


     //   Executors.newSingleThreadExecutor().execute(() -> {

            if (isOn) //means trying to  turning on
            {

                if (getInfo.arePermissionDenied(ctx))
                {
                    i("arePermissionDenied");
                    playSound(ctx,R.raw.error_sound);
                    storageP.show();



                }else if (getInfo.noNotificationPermission(ctx)) //
                {
                    i("noNotificationPermission");
                    notiPer.show();

                }else
                {
                    isWentForPermission=0;
                    v.findViewById(R.id.recoverTop).setBackgroundColor(getContext().getResources().getColor(R.color.VA_on));
                    getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.VA_on, getContext().getTheme()));
                    if (!isServiceRunning(mediaWatcher.class.getName(), ctx))
                    {
                        i("!isServiceRunning");


                                ctx.startService(intent);
                                playSound(ctx,R.raw.switch_sound);
                                isWentForPermission=0;
                                spE.putBoolean(getString(R.string.recoverMediaOnTag),true).apply(); // using it in the notification service

                    }else {
                        if (!sp.getBoolean(getString(R.string.recoverMediaOnTag),false))
                        {

                            ctx.stopService(intent);

                        }
                    }



                }

            }
            else
            {
                isWentForPermission=0;
                v.findViewById(R.id.recoverTop).setBackgroundColor(getContext().getResources().getColor(R.color.VA_off));
                if (isServiceRunning(mediaWatcher.class.getName(), ctx) // is service running
                        && !sp.getBoolean(getString(R.string.privateMediaOnTag),false)
                        && !sp.getBoolean(getString(R.string.privateChatOnTag),false) )// if private media on then shouldn't stop the service


                {
                    i(" stopService(intent) ");
                    getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.VA_off, getContext().getTheme()));
                    ctx.stopService(intent);
                }

                spE.putBoolean(getString(R.string.recoverMediaOnTag),false).apply(); // using it in the notification service
            }
       // } );




    }



    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent data = result.getData();

                    assert data != null;

                    Log.d("HEY: ", data.getData().toString());


                    ctx.getContentResolver().takePersistableUriPermission(
                            data.getData(),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);





                    Toast.makeText(ctx, "Permission Granted", Toast.LENGTH_SHORT).show();

                }
            }
    );

    void i(String s)
    {
        Log.i("recoverChat",s);
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