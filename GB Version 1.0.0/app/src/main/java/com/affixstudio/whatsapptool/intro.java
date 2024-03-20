package com.affixstudio.whatsapptool;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.affixstudio.whatsapptool.activityOur.MainActivity;
import com.affixstudio.whatsapptool.fragmentsOur.splash_one;
import com.affixstudio.whatsapptool.fragmentsOur.splash_three;
import com.affixstudio.whatsapptool.fragmentsOur.splash_two;
import com.github.appintro.AppIntro;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class intro extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getSupportActionBar().hide(); //hide the title bar
        sp=getSharedPreferences("affix",MODE_PRIVATE);
        edit=sp.edit();
        addSlide(new splash_one());
        addSlide(new splash_two());
        addSlide(new splash_three());
        addSlide(new splash_four());

        setSkipButtonEnabled(true);
        setIndicatorEnabled(true);
        showStatusBar(true);


        View close=getLayoutInflater().inflate(R.layout.close_dialog,null);
        closeDialog=new BottomSheetDialog(this); //#todo delete all unwanted files

        // closeDialog=new BottomSheetDialog(this);

        Button ok=close.findViewById(R.id.yes);
        Button no=close.findViewById(R.id.no);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog.dismiss();
            }
        });
        closeDialog.setContentView(close);

    }
    BottomSheetDialog closeDialog;
    @Override
    public void onBackPressed() {
        closeDialog.show();
    }

    SharedPreferences sp;
    SharedPreferences.Editor edit;
    @Override
    protected void onSkipPressed(Fragment currentFragment) {

        edit.putBoolean("isNewUser",false);
        edit.apply();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        edit.putBoolean("isNewUser",false);
        edit.apply();
        startActivity(new Intent(this, MainActivity.class));
    }
}