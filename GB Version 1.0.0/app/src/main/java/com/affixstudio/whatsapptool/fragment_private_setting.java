package com.affixstudio.whatsapptool;

import static com.affixstudio.whatsapptool.serviceOur.serviceTool.isServiceRunning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.affixstudio.whatsapptool.serviceOur.mediaWatcher;
import com.google.android.material.checkbox.MaterialCheckBox;


public class fragment_private_setting extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_private_setting, container, false);

        MaterialCheckBox enableNotification=v.findViewById(R.id.enableNotification);
        SharedPreferences sp=getContext().getSharedPreferences("affix", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEdit=sp.edit();


        enableNotification.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
            {
                spEdit.putBoolean(getString(R.string.privateNotificationEnableTag),true).apply();
            }else {
                spEdit.putBoolean(getString(R.string.privateNotificationEnableTag),false).apply();
            }
        });


        SwitchCompat switchCompat=v.findViewById(R.id.privateMediaSwitch);

        Intent mediaIntent=new Intent(getContext(), mediaWatcher.class);
        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)// set on correctly
            {
                if (sp.getBoolean(getString(R.string.privateChatOnTag),false))
                {
                    if (!isServiceRunning(mediaWatcher.class.getName(), getContext()))
                    {
                        getContext().startService(mediaIntent);
                    }

                    spEdit.putBoolean(getString(R.string.privateMediaOnTag),true).apply();
                }
            }
            else
            {
                if (isServiceRunning(mediaWatcher.class.getName(), getContext()) &&
                !sp.getBoolean(getString(R.string.recoverMediaOnTag),false))
                {
                    getContext().stopService(mediaIntent);
                }

                spEdit.putBoolean(getString(R.string.privateMediaOnTag),false).apply();
            }
        });
        switchCompat.setChecked(sp.getBoolean(getString(R.string.privateMediaOnTag),true));
        enableNotification.setChecked(sp.getBoolean(getString(R.string.privateNotificationEnableTag), false));


        return v;
    }
}