package com.affixstudio.whatsapptool.activityOur;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.getData.GetPrivateMediaFile;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class settingActivity extends AppCompatActivity implements View.OnClickListener {
    String[] qestionsArray,ansArray;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //getSupportActionBar().hide(); //hide the title bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));
         qestionsArray=getResources().getStringArray(R.array.faqQuestion);
         ansArray=getResources().getStringArray(R.array.faqAns);

         TextView vName=findViewById(R.id.appVersion);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            vName.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        Adapter adapter=new Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.faqs_recycle,parent,false)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }

                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
               TextView ans= (TextView)  holder.itemView.findViewById(R.id.ans);
               ans.setText(ansArray[position]);
                holder.itemView.findViewById(R.id.faqRecycleLayout).startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycle));

                TextView quest= (TextView)  holder.itemView.findViewById(R.id.questions);
                quest.setText(qestionsArray[position]);
            }

            @Override
            public int getItemCount() {
                return ansArray.length;
            }
        };
        recyclerView=findViewById(R.id.faqRecycler);

        recyclerView.setAdapter(adapter);
        showContectUsDialog();

        // delete all data of Schedule ,Private chat, recover chat
        GetPrivateMediaFile pmf=new GetPrivateMediaFile();

        Dialog bs=new Dialog(settingActivity.this);

        bs.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog));


        View dV=getLayoutInflater().inflate(R.layout.delete_warning,null);

        Button deleteYes=dV.findViewById(R.id.delete),
                dismiss=dV.findViewById(R.id.dismiss);

        TextView warningText=dV.findViewById(R.id.warningText);




        warningText.setText(getText(R.string.warningTextDeleteAllSetting));



        deleteYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bs.dismiss();
                pmf.deleteAllMediaAndData(settingActivity.this);
            }
        });
        dismiss.setOnClickListener(view -> {
            bs.dismiss();
        });

        bs.setContentView(dV);


        findViewById(R.id.deleteAllMedia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bs.show();

            }
        });
        findViewById(R.id.yt_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl("https://www.youtube.com/channel/UC4YSINX3rKIzjrKxpdSRNWg/videos");
            }
        });
        findViewById(R.id.verifiedPlayBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl("https://affixstudio.co.in/no-data-collected.html");
            }
        });


        findViewById(R.id.coplainVA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFeedBackDia(0);
            }
        });

        // request a feature
        findViewById(R.id.requestFLA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showFeedBackDia(1);

            }
        });

        findViewById(R.id.feedbackFLA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showFeedBackDia(2);
            }
        });





    }
    ArrayList<String> mostUsedOp=new ArrayList<>();
    ArrayList<String> mostUsedSelected=new ArrayList<>();

    private void showFeedBackDia(int i) {

        String titles="Feedback",teglines="Our improvement will be done by your feedback.";
        if(i==0)
        {
            titles="Complain";
            teglines="Complain is a way of helping to improve";

        }
        else if (i==1)
        {
            titles="Request a Feature";
            teglines="Help us to serve you better";
        }

        Dialog dialog=new Dialog(this);

        View view=getLayoutInflater().inflate(R.layout.feedback_dialog,null);

        TextView
                title=view.findViewById(R.id.title),
                tagLine=view.findViewById(R.id.tagLine);



        mostUsedOp.add("Auto Reply");
        mostUsedOp.add("No Blue Tick");
        mostUsedOp.add(getString(R.string.scheduleScreenName));
        mostUsedOp.add("Status Download");
        mostUsedOp.add("Recover Chat");
        mostUsedOp.add("WhatsApp Web");
        mostUsedOp.add("Others");

       LinearLayout feedbackVA=view.findViewById(R.id.feedbackVA);

       if (i==2)
       {
           feedbackVA.setVisibility(View.VISIBLE);
       }


       Button
               dismiss=view.findViewById(R.id.dismiss),
               submit=view.findViewById(R.id.delete);


        TextInputEditText inputFeedTxt=view.findViewById(R.id.inputFeedTxt);


       title.setText(titles);
       tagLine.setText(teglines);

       dismiss.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               dialog.dismiss();
           }
       });
       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               String s=inputFeedTxt.getText().toString();
               if (!s.equals(""))
               {
                   dialog.dismiss();
                   saveInFirebase(i,s);
               }else {
                   Toast.makeText(settingActivity.this, "Type some thing in box", Toast.LENGTH_LONG).show();
               }

           }
       });

        CheckBox
                auto=view.findViewById(R.id.auto),
                pChat=view.findViewById(R.id.pChat),
                sChat=view.findViewById(R.id.sChat),
                sDownload=view.findViewById(R.id.sDownload),
                rChat=view.findViewById(R.id.rChat),
                web=view.findViewById(R.id.web),
                other=view.findViewById(R.id.other);


        //adding onchecked listener
        addOnCheck(auto,0);
        addOnCheck(pChat,1);
        addOnCheck(sChat,2);
        addOnCheck(sDownload,3);
        addOnCheck(rChat,4);
        addOnCheck(web,5);
        addOnCheck(other,6);


//        auto.setOnCheckedChangeListener((compoundButton, b) -> {
//
//            if (b)
//            {
//                if (!mostUsedSelected.contains(mostUsedOp.get(0)))
//                {
//                    mostUsedSelected.add(mostUsedOp.get(0));
//                }
//
//
//            }else {
//                if (mostUsedSelected.contains(mostUsedOp.get(0)))
//                {
//                    mostUsedSelected.add(mostUsedOp.get(0));
//                }
//            }
//        });
//        pChat.setOnCheckedChangeListener((compoundButton, b) -> {
//
//        });
//        sChat.setOnCheckedChangeListener((compoundButton, b) -> {
//
//        });
//        sDownload.setOnCheckedChangeListener((compoundButton, b) -> {
//
//        });
//        rChat.setOnCheckedChangeListener((compoundButton, b) -> {
//
//        });
//        web.setOnCheckedChangeListener((compoundButton, b) -> {
//
//        });
//        other.setOnCheckedChangeListener((compoundButton, b) -> {

//        });







       dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog));
       dialog.setContentView(view);
       dialog.show();



    }

    private void addOnCheck(CheckBox checkBox,int i) {

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            Log.i("settingActivity","checkBox listener");
            if (b)
            {
                if (!mostUsedSelected.contains(mostUsedOp.get(i)))
                {
                    mostUsedSelected.add(mostUsedOp.get(i));
                }


            }else {
                mostUsedSelected.remove(mostUsedOp.get(i));
            }
        });

    }

    public void goBack(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.firstLayout).isShown()){
            Intent i = new Intent(this, MainActivity.class);
            Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(i,b);
        }else
            recyclerView.setVisibility(View.GONE);
        findViewById(R.id.firstLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.verifiedPlayBt).setVisibility(View.VISIBLE);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        switch (view.getId())
        {
            case R.id.faq_btn:
                findViewById(R.id.firstLayout).setVisibility(View.GONE);
                findViewById(R.id.verifiedPlayBt).setVisibility(View.GONE);
                findViewById(R.id.faqRecycler).setVisibility(View.VISIBLE);
                return;
            case R.id.rate_us_btn:
                openUrl(getString(R.string.playstoreUrl));
                return;
            case R.id.terms_btn:
                openUrl("https://affixst.blogspot.com/2023/03/gb-version-terms-conditions.html");//#todo change url
                return;
            case R.id.privacy_btn:
                openUrl("https://affixst.blogspot.com/2023/03/gb-version-privacy-policy.html");
                return;
            case R.id.share_app_btn:

                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share using");
                intent.putExtra( Intent.EXTRA_TEXT,getString(R.string.shareAppText)+" "+getString(R.string.playstoreUrl));
                startActivity(intent);
                return;
            case R.id.chat_btn:
                contactDialog.show();
                contactDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog));
                return;
            case R.id.deleteAllMedia:
//                Uri uri= Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
//                        "/GBWhats/");
//
//                Intent in=new Intent(Intent.ACTION_VIEW);
//                in.setDataAndType(uri,"resource/folder");
//                startActivity(in);

                Intent in = new Intent(Intent.ACTION_PICK);
                Uri uri = Uri.parse("file://"+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
                       "/GBWhats/");
                in.setDataAndType(uri, DocumentsContract.Document.MIME_TYPE_DIR);
                startActivity(Intent.createChooser(in, "Open folder"));




        }
    }

    Dialog contactDialog;
    private void showContectUsDialog() {


        View contactUs=getLayoutInflater().inflate(R.layout.contact_us_layout,null);
         contactDialog=new Dialog(settingActivity.this);

        LinearLayout whatsapp = contactUs.findViewById(R.id.whatsapp),
                    email = contactUs.findViewById(R.id.email),
                    contectFaq=contactUs.findViewById(R.id.contactFaq),
                    appTutorial=contactUs.findViewById(R.id.appTutorial);


        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringUri="https://api.whatsapp.com/send?phone=+919830818465" ;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(stringUri))/*.setPackage("com.whatsapp")*/);
//                if (isInstalled(settingActivity.this,"com.whatsapp"))
//                {
//
//                }else {
//                    Toast.makeText(settingActivity.this, "Whatsapp is not installed", Toast.LENGTH_LONG).show();
//                }
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"+getString(R.string.contactEmail)));
                startActivity(emailIntent);
            }
        });

        contectFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactDialog.dismiss();
                findViewById(R.id.firstLayout).setVisibility(View.GONE);
                findViewById(R.id.verifiedPlayBt).setVisibility(View.GONE);
                findViewById(R.id.faqRecycler).setVisibility(View.VISIBLE);
            }
        });

        appTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(settingActivity.this, AppTutorials.class);
                i.putExtra("setting",true);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(settingActivity.this).toBundle();
                startActivity(i,b);

            }
        });


        contactDialog.setContentView(contactUs);



    }

    void saveInFirebase(int i, String text)
    {





        String titles="Feedback";
        if(i==0)
        {
            titles="Complain";


        }
        else if (i==1)
        {
            titles="Request a Feature";

        }

  //      FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("Text", text);

        if (i==2)
        {
            String used= "";
            for (String s:mostUsedSelected)
            {
                used=used+" "+s+",";
            }
            user.put("Most used", used);

        }

// Add a new document with a generated ID
//        db.collection(titles)
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
    }



    private void openUrl(String s) {
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData( Uri.parse(s));
        startActivity(intent);
    }
}