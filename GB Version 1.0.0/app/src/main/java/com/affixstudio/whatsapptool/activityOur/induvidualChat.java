package com.affixstudio.whatsapptool.activityOur;

import static com.affixstudio.whatsapptool.getData.GetInfo.isOnline;
import static com.affixstudio.whatsapptool.modelOur.NotificationRecever.query;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.adopterOur.chatMessageRecycle;
import com.affixstudio.whatsapptool.fragmentsOur.DecorationText;
import com.affixstudio.whatsapptool.getData.NetworkChangeReceiver;
import com.affixstudio.whatsapptool.modelOur.database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class induvidualChat extends AppCompatActivity {
    database db;
    String sqlToDelete;

    String userNameS;
    BroadcastReceiver updateUIReciver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_induvidual_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        }

        try {

            refresh();

            TextView whatsapp=findViewById(R.id.whatsappOpen);
            TextView whatsappB=findViewById(R.id.whatsappB);


            if (!DecorationText.Adepter.isInstalled(induvidualChat.this,"com.whatsapp"))
            {
                whatsapp.setVisibility(View.GONE);
            }
            if (!DecorationText.Adepter.isInstalled(induvidualChat.this,"com.whatsapp.w4b"))
            {
                whatsappB.setVisibility(View.GONE);
            }




            whatsapp.setOnClickListener(view -> {

                startActivity(getPackageManager().getLaunchIntentForPackage("com.whatsapp"));
            });


            whatsappB.setOnClickListener(view -> {
                startActivity(getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b"));
            });




            IntentFilter filter = new IntentFilter();

            filter.addAction(getString(R.string.updateChatUpdateBroadcast));

             updateUIReciver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    //UI update here
                    refresh();

                }
            };
            registerReceiver(updateUIReciver,filter);





    }catch (Exception e)
    {
        e.printStackTrace();
    }


        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        receiver=new NetworkChangeReceiver();
        registerReceiver(receiver,filter);
    }



    BroadcastReceiver receiver;

    void refresh()
    {
        TextView userName=findViewById(R.id.userName);

        Intent intent=getIntent();
        Bundle extra=intent.getExtras();
        userNameS=getIntent().getStringExtra("user");
        String markAsRead=null;

        if (extra.getBoolean("fromPrivateChat"))
        {
            String query="CREATE TABLE IF NOT EXISTS "+getString(R.string.privateChatTableName)+" (_id INTEGER PRIMARY KEY autoincrement,RecevedFrom text, Message text,time text,isdeleted INTEGER, unread INTEGER '1' ) ";

            db=new database(this,getString(R.string.privateChatTableName),query,1);
            TextView  workProperly = findViewById(R.id.workProperly);
            workProperly.setText(getString(R.string.noBlueTickTopNotice));
            sqlToDelete="DELETE FROM "+getString(R.string.privateChatTableName)+" WHERE RecevedFrom='"+userNameS+"'";
            markAsRead="UPDATE "+getString(R.string.privateChatTableName)+" SET unread='0' WHERE RecevedFrom='"+userNameS+"'";

        }else {
            db=new database(this,getString(R.string.recover_message_table_name),query,2);
            sqlToDelete="DELETE FROM "+getString(R.string.recover_message_table_name)+" WHERE RecevedFrom='"+userNameS+"'";
            markAsRead="UPDATE "+getString(R.string.recover_message_table_name)+" SET unread='0' WHERE RecevedFrom='"+userNameS+"'";

        }

        db.checkData(db.getinfo(1));

        db.getWritableDatabase().execSQL(markAsRead);

        ArrayList<String> message = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        ArrayList<Integer> isdeleted = new ArrayList<>();







        userName.setText(extra.getString("user"));


        // filtering the user time and message
        for (int i=0;i<db.getRecevedFrom().size();i++){

            if (db.getRecevedFrom().get(i).equals(extra.getString("user")))
            {
                message.add(db.getMessage().get(i));
                time.add(db.getTime().get(i));
                isdeleted.add(db.getIsdeleted().get(i));
            }

        }

        //Toast.makeText(this, ""+message.size(), Toast.LENGTH_SHORT).show();

        chatMessageRecycle chatMessageRecycle=new chatMessageRecycle(this,message,time,isdeleted,extra.getBoolean("fromPrivateChat"));




        RecyclerView recyclerView=findViewById(R.id.messageRecycle);
        recyclerView.setAdapter(chatMessageRecycle);
        /*  recyclerView.setLayoutManager(layoutManager);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void goback(View view) {
        onBackPressed();
       // startActivity(new Intent(this, MainActivity.class).putExtra("fragmentNo",3));
    }

    public void deleteChat(View view) {
        new AlertDialog.Builder(this).setMessage("Do you really want to delete this chat?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {






                        db.getWritableDatabase().execSQL(sqlToDelete);
                        Toast.makeText(induvidualChat.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
//                        Intent in= new Intent(induvidualChat.this,recoverMessage.class);
//                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(in);
                        }catch (Exception e)
                        {
                            Log.e("deleteChat",e.getMessage());
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnline(this))
        {
            startActivity(new Intent(this,no_internet_Screen.class));
        }
        refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        unregisterReceiver(updateUIReciver);
        try {
            unregisterReceiver(receiver);
        }catch (Exception e)
        {

            Log.e("e",e.getMessage());
        }
    }

}