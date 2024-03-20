package com.affixstudio.whatsapptool.activity;

import static com.affixstudio.whatsapptool.modelOur.sound.playSound;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.modelOur.database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

public class specificContactReply extends AppCompatActivity {

    private static final String T = "specificContact";
    private String matchType="exact";
    private final int CONTACT_PICKER_REQUEST=1000;
    private boolean isPickingContact=false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
    TextInputEditText senderName,message,reply;
    ArrayList<String> names=new ArrayList<>();
    ArrayList<String> messageList=new ArrayList<>();
    ArrayList<String> replyList=new ArrayList<>();
    ArrayList<String> matchTypeList = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();
    database db;

    FloatingActionButton addContactBtn,oldMessageContactBtn,addGroupBtn,oldMessageGroupBtn;
    LinearLayout mainLayout,groupHistroy,contactHistroy,addContact,addGroup;
    Button addNew,delete;
    int id=0;  //id is the database column id to modify data
    RadioButton exact,contain;


    boolean isGroup=false; // indicating the layout is showing of group

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_contact_reply);

        String query="CREATE TABLE IF NOT EXISTS "+getString(R.string.specificReply)+"(_id INTEGER PRIMARY KEY autoincrement, SenderName text DEFAULT 'Not set',Message text,Replay text,MatchType text,SendTo text DEFAULT 'com.whatsapp',isGroup text DEFAULT '0') ";
         db=new database(specificContactReply.this,getString(R.string.specificReply),query,1);




        senderName=findViewById(R.id.senderName);
         message=findViewById(R.id.message);
                reply=findViewById(R.id.Reply);

        addContactBtn=findViewById(R.id.add_new_Reply);
        oldMessageContactBtn=findViewById(R.id.oldMessage);
        addGroupBtn=findViewById(R.id.add_new_group);
        oldMessageGroupBtn=findViewById(R.id.groupOldMessage);
        addContact=findViewById(R.id.chatLayout);
        mainLayout=findViewById(R.id.mainLayout);
        groupHistroy=findViewById(R.id.groupHistroy);
        contactHistroy=findViewById(R.id.contactHistroy);
        addGroup=findViewById(R.id.addGroup);
        

         addNew=findViewById(R.id.addReply);
        delete=findViewById(R.id.delete);
        recyclerView=findViewById(R.id.contactRecycler);
        exact=findViewById(R.id.exact);
        contain=findViewById(R.id.contain);
        
        
        

        if (getIntent()!=null)
        {
            isGroup=getIntent().getBooleanExtra("isGroup",false); //#todo set the layout properly
            showProperLayout(getIntent().getBooleanExtra("isGroup",false));
        }
        getData();

        exact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    matchType="exact";
                }
            }
        });
        contain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    matchType="contain";
                }
            }
        });

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (Objects.requireNonNull(senderName.getText()).toString().isEmpty())
                {

                    showSnackBar("Enter Contact Name",R.color.red);

                }else if (Objects.requireNonNull(message.getText()).toString().isEmpty())
                {
                    showSnackBar("Enter a Message",R.color.red);

                }else if (Objects.requireNonNull(reply.getText()).toString().isEmpty())
                {
                    showSnackBar("Enter a Reply.",R.color.red);

                }else {

                    if (addNew.getText().toString().equalsIgnoreCase("SAVE"))
                    {

                        String s="UPDATE "+getString(R.string.specificReply)+" SET SenderName='"+senderName.getText().toString()
                                +"'," +
                                "Message='"+message.getText().toString()+"',Replay='"+reply.getText().toString()
                                +"',MatchType='"+matchType+"' WHERE _id="+id;

                        db.getWritableDatabase().execSQL(s);

                        showSnackBar("Saved Successfully.", R.color.colorSecondaryVariant);
                        makeEmpty();
                        getData();

                    }else {
                        SQLiteDatabase sl=db.getReadableDatabase();
                        ContentValues contentValues=new ContentValues();

                        contentValues.put("SenderName",senderName.getText().toString());
                        contentValues.put("Message",message.getText().toString());
                        contentValues.put("Replay",reply.getText().toString());
                        contentValues.put("MatchType",matchType);

                        long s=sl.insert(getString(R.string.specificReply),null,contentValues);
                        if (s != -1)
                        {
                            showSnackBar("Added Successfully.", R.color.colorSecondaryVariant);

                            makeEmpty();
                            getData();
                        }
                    }

                }





            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sql="DELETE FROM "+getString(R.string.specificReply)+" WHERE _id='"+id+"'";
                db.getWritableDatabase().execSQL(sql);
                showSnackBar("Deleted Successfully.", R.color.colorSecondaryVariant);
                makeEmpty();
                getData();
            }
        });

        findViewById(R.id.contactBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickContact();
            }
        });

        exact.setChecked(true);



        oldMessageContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                showOldList();
            }
        });

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                makeEmpty();
                showAddLayout();


            }
        });





    }

    private void showProperLayout(boolean isGroup) {

        if (isGroup)
        {

        }


    }

    @Override
    public void onBackPressed() {

        if (addContact.isShown())
        {
            addContact.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else
        {
            super.onBackPressed();
        }
    }

    RecyclerView  recyclerView;
    private void getData()
    {
        if (names.size()>0)
        {
            ids.clear();
            names.clear();
            messageList.clear();
            replyList.clear();
            matchTypeList.clear();

        }
        Cursor c= db.getinfo(1);

        while (c.moveToNext())
        {
            ids.add(c.getInt(0));
            names.add(c.getString(1));
            messageList.add(c.getString(2));
            replyList.add(c.getString(3));
            matchTypeList.add(c.getString(4));
        }


        if (replyList.size()>0)
        {
            //  findViewById(R.id.chatLayout).setVisibility(View.GONE);
            //   recyclerView.setVisibility(View.VISIBLE);

            recyclerView.setAdapter(new RecyclerView.Adapter() {
                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sch_msg_history_recycle,parent,false)) {
                        @NonNull
                        @Override
                        public String toString() {
                            return super.toString();
                        }
                    };
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, @SuppressLint("RecyclerView") int position) {

                    int i=names.size()-1-position; //in reverse order
                    TextView name=h.itemView.findViewById(R.id.name);
                    TextView message=h.itemView.findViewById(R.id.number);
                    TextView reply=h.itemView.findViewById(R.id.Messages);


                    name.setText(names.get(i));
                    message.setText(messageList.get(i));
                    reply.setText(replyList.get(i));

                    CardView listCard=h.itemView.findViewById(R.id.schiduleRVCard);
                    listCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            editOld(i);



                        }
                    });



                }

                @Override
                public int getItemCount() {
                    return names.size();
                }
            });
        }

        showOldList();

    }

    void showAddLayout(){


        addContactBtn.setVisibility(View.GONE);
        oldMessageContactBtn.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        addContact.setVisibility(View.VISIBLE);


    }
    void showOldList(){
        addContactBtn.setVisibility(View.VISIBLE);
        oldMessageContactBtn.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        addContact.setVisibility(View.GONE);
    }
    void editOld(int position)
    {

        id=ids.get(position);
        Log.i(T,"position "+id);
        addNew.setText("Save");
        message.setText(messageList.get(position));
        senderName.setText(names.get(position));
        reply.setText(replyList.get(position));
        delete.setVisibility(View.VISIBLE);
        if (matchTypeList.get(position).equals("exact"))
        {
            exact.setChecked(true);
        }else {
            contain.setChecked(true);
        }
        showAddLayout();
    }

    void makeEmpty(){
        message.setText("");
        senderName.setText("");
        reply.setText("");
        addNew.setText("ADD");
        delete.setVisibility(View.GONE);
    }

    @SuppressLint("IntentReset")
    public void showPickContact() {
        Log.i(T,"showPickContact");



        isPickingContact=true;
        Context mContext=this;

        final int REQUEST = 112;

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS};
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions(specificContactReply.this, PERMISSIONS, REQUEST );
            } else {


                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, CONTACT_PICKER_REQUEST);

                /* new MultiContactPicker.Builder(this) //Activity/fragment context
                .theme(R.style.AppTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(this, R.color.primary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(this, R.color.primary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                //.setSelectedContacts("10", "5" / myList) //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)

                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out) //Optional - default: No animation overrides
                .showPickerForResult(CONTACT_PICKER_REQUEST);}}*/
            }}
    }


    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {


            if(requestCode == CONTACT_PICKER_REQUEST && resultCode==RESULT_OK){
           /* if(resultCode == RESULT_OK) {
                List<ContactResult> results = MultiContactPicker.obtainResult(data);
                Log.d("MyTag", String.valueOf(results.get(0).getPhoneNumbers().get(0)));
            } else if(resultCode == RESULT_CANCELED){
                System.out.println("User closed the picker without selecting items.");
            }*/


                Uri uri = data.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);









                senderName.setText(name);


                Log.d(T, "ZZZ number : " + number +" , name : "+name);


                cursor.close();


            }


        }catch (Exception e)
        {
            Log.e("schedule_sms",e.getMessage());
        }
    }


    private static boolean hasPermissions(Context context, String...  permissions) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    void showSnackBar(String message,int color)
    {
        if (color==R.color.red)
        {
            playSound(this,R.raw.error_sound);
        }
        Snackbar.make(findViewById(R.id.mainLayout),message, BaseTransientBottomBar.LENGTH_SHORT)
                .setTextColor(ContextCompat.getColor(this,R.color.white))
                .setBackgroundTint(ContextCompat.getColor(this, color)).show();
    }
}