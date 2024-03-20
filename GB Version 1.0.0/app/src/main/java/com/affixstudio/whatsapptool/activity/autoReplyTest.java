package com.affixstudio.whatsapptool.activity;

import static com.affixstudio.whatsapptool.modelOur.sound.playSound;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.model.CustomRepliesData;
import com.affixstudio.whatsapptool.model.preferences.PreferencesManager;
import com.affixstudio.whatsapptool.modelOur.database;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class autoReplyTest extends AppCompatActivity {

    AutoCompleteTextView selectSection,selectPersonName;
    TextView incomingMessage,reply;
    TextInputEditText enterIncomingMessage;

    int replyModeString;
    String name="Not Selected";
    private int personSelectionIndex=0;
    private SharedPreferences sp;
    String e;

    int error =0;


    ArrayList<String> incoming=new ArrayList<>(); // typed text in the box
    ArrayList<String> autoReplied=new ArrayList<>(); // replied text
    ArrayList<String> senderName=new ArrayList<>(); // selected Person Name
    ArrayList<String> time=new ArrayList<>(); // chat time
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_reply_test);
        getWindow().setStatusBarColor(getResources().getColor(R.color.primary, this.getTheme()));

        sp=getSharedPreferences("affix",0);
        ArrayList<String> names=sendersName();

        ImageButton whatsBtn=findViewById(R.id.whatsBtn);
        selectSection=findViewById(R.id.selectSection);
        selectPersonName=findViewById(R.id.selectPersonName);
        incomingMessage=findViewById(R.id.incomingMessage);
        reply=findViewById(R.id.reply);
        enterIncomingMessage=findViewById(R.id.enterIncomingMessage);

        String[] replyMode={"Everyone","My List"/*,"My Group List"*/,"Except My List"};
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(autoReplyTest.this,  android.R.layout.simple_list_item_1
                ,replyMode);

        ArrayAdapter<String> senderNameAdepter=new ArrayAdapter<String>(autoReplyTest.this,  android.R.layout.simple_list_item_1
                ,names);


        selectSection.setAdapter(arrayAdapter);

        findViewById(R.id.goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        selectSection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                replyModeString=i;
                info("selectSection "+i);
            }
        });


        selectPersonName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                name=names.get(i);

                personSelectionIndex=i;
            }
        });

        selectPersonName.setAdapter(senderNameAdepter);


         adapter=new Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.auto_recycle_test,null)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {

                TextView name=h.itemView.findViewById(R.id.senderName);
                TextView incomingText=h.itemView.findViewById(R.id.incomingMessage);
                TextView autoRepliedText=h.itemView.findViewById(R.id.reply);
                TextView recycleTime=h.itemView.findViewById(R.id.recycleTime);
                TextView timeReply=h.itemView.findViewById(R.id.textView13);

                ConstraintLayout replyLayout=h.itemView.findViewById(R.id.autoLayout);
                LinearLayout senderLayout=h.itemView.findViewById(R.id.senderLayout);

                name.setText(senderName.get(position));
                incomingText.setText(incoming.get(position));

                recycleTime.setText(time.get(position));


                senderLayout.setTranslationY(500);
                senderLayout.animate().translationYBy(-500).setDuration(800);
                replyLayout.setTranslationY(500);
                replyLayout.setVisibility(View.VISIBLE);
                replyLayout.animate().translationYBy(-500).setDuration(3000);
                autoRepliedText.setText(autoReplied.get(position));

                timeReply.setText(time.get(position));











            }

            @Override
            public int getItemCount() {
                return incoming.size();
            }
        };


        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);

        Calendar now =Calendar.getInstance(Locale.getDefault());

        whatsBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                 e=enterIncomingMessage.getText().toString();
                info("replyModeString "+replyModeString);



                if (!e.isEmpty())
                {
                    CustomRepliesData customRepliesData=new CustomRepliesData(autoReplyTest.this,e,"");

                    incoming.add(e);
                    senderName.add(name+"("+replyMode[replyModeString]+")");

                    String amOrPm="am";
                    if (now.get(Calendar.AM_PM)== Calendar.PM)
                    {
                        amOrPm="pm";
                    }
                    time.add(now.get(Calendar.HOUR)+":"+now.get(Calendar.MINUTE)+" "+amOrPm);


                    if (replyModeString!=2) // everyOne and in contactlist
                    {
                        info("replyModeString!=2");

                        if (personSelectionIndex==0 ) // unknown sender
                        {

                            if (replyModeString==0) // when selected everyOne
                            {
                                info("replyModeString==0 ");
                                defaultAns(customRepliesData);
                            }else {
                                info("replyModeString==0 else");
                                noAns("(Sender is not in the list)");
                            }


                        }else{

                            info("personSelectionIndex==0 else");

                            showReply();
                        }


                    }
                    else { // exclude list
                        info("replyModeString!=2 else");
                        if (personSelectionIndex==0) // unknown sender
                        {
                            info("replyModeString!=2 else personSelectionIndex==0");
                            defaultAns(customRepliesData);
                        }else {
                            info("replyModeString!=2 else personSelectionIndex==0 else");
                            noAns("(Sender is in exclude list)");
                        }

                    }
                    incomingMessage.setText(e);
                    enterIncomingMessage.setText("");

                    adapter.notifyItemRangeChanged(incoming.size()-1,incoming.size());

                    recyclerView.smoothScrollToPosition(incoming.size()-1);

                    findViewById(R.id.demoLayout).setVisibility(View.GONE);



                }



            }
        });




    }




    @SuppressLint("ResourceAsColor")
    private void defaultAns(CustomRepliesData customRepliesData) {
        reply.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));
        reply.setText(customRepliesData.getOrElse(getString(R.string.auto_reply_default_message)));
        autoReplied.add(customRepliesData.getOrElse(getString(R.string.auto_reply_default_message)));

    }
    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    private void noAns(String reason) {
        reply.setTextColor(ContextCompat.getColor(this,R.color.red));
        reply.setText("No Answer will be sent " +reason);
        autoReplied.add("No Answer will be sent " +reason);
    }

    private void showReply()

    {
        CustomRepliesData c=new CustomRepliesData(this,e,name);
        reply.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));

        if (personSelectionIndex>personMark && PreferencesManager.getPreferencesInstance(this).isGroupReplyEnabled()) //group is not enable
        {
            autoReplied.add(c.getTextToSendOrElse());
            reply.setText(c.getTextToSendOrElse());
        }else if (personSelectionIndex<=personMark) // when selected a person name
        {
            autoReplied.add(c.getTextToSendOrElse());
            reply.setText(c.getTextToSendOrElse());

        }else
        {
            reply.setTextColor(ContextCompat.getColor(this, R.color.red));
            info("showReply noAns");
            noAns("(Group Reply is not enabled)");
        }



    }


    int personMark=0 ; // indicate the index of last person name in names list. After that group name

    ArrayList<String> sendersName()
    {
        personMark=0;
        ArrayList<String> names=new ArrayList<>();
        String query="CREATE TABLE IF NOT EXISTS "+getString(R.string.contactList)+"(_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',Number text DEFAULT 'Not set',ContactID text DEFAULT 'Not set') ";
        String query4="CREATE TABLE IF NOT EXISTS "+getString(R.string.groupList)+"(_id INTEGER PRIMARY KEY autoincrement, GroupName text DEFAULT 'Not set') ";

        database db=new database(this,getString(R.string.contactList),query,1);

        names.add("Unknown Person");
        Cursor c=db.getinfo(1);

        while (c.moveToNext())
        {
            names.add(c.getString(1));
            personMark++;
        }

        db=new database(this,getString(R.string.groupList),query4,1);
        c=db.getinfo(1);

        while (c.moveToNext())
        {
            names.add(c.getString(1));
        }

        return names;
    }

    void info(String string)
    {
        Log.i("autoReply",string);
    }
}