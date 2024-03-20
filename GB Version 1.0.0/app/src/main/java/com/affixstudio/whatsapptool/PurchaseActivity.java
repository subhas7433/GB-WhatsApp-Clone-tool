package com.affixstudio.whatsapptool;


import static com.affixstudio.whatsapptool.activityOur.startScreen.premiumDays;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.affixstudio.whatsapptool.modelOur.database;

import java.util.ArrayList;



public class PurchaseActivity extends AppCompatActivity {
    private int purchaseType=0;

    ArrayList<String> subIDs=new ArrayList<>();
    TextView days;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);


        subIDs.add("weekly");
        subIDs.add("monthly");
        subIDs.add("yearly");

        String query="CREATE TABLE IF NOT EXISTS userPremiumInfoTable (_id INTEGER PRIMARY KEY autoincrement, DateRemaining text DEFAULT '0',LastUpdated text DEFAULT '0') ";

        // Load the first ad
        database db=new database(this,getString(R.string.userPremiumInfoTable),query,1);



        days=findViewById(R.id.days);






        findViewById(R.id.showSurvey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (surveyLoaded)
//                {
//                    Pollfish.show();
//                }else {
//                    Toast.makeText(PurchaseActivity.this, "Survey is loading. Try after a min.", Toast.LENGTH_LONG).show();
//                }

            }
        });





        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }



    ArrayList<String> ownedSubs=new ArrayList<>();






    @Override
    protected void onResume() {
        super.onResume();
        if (premiumDays==0)
        {
            findViewById(R.id.otherMemCard).setVisibility(View.GONE);
        }else {
            findViewById(R.id.otherMemCard).setVisibility(View.VISIBLE);
        }
        days.setText(premiumDays+" days left");
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}