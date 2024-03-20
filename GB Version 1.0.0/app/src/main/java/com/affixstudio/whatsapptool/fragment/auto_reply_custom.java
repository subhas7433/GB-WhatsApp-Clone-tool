package com.affixstudio.whatsapptool.fragment;

import static android.app.Activity.RESULT_OK;
import static com.affixstudio.whatsapptool.activityOur.schedule_sms.isAccessibilityServiceEnabled;
import static com.affixstudio.whatsapptool.activityOur.startScreen.haveSub;
import static com.affixstudio.whatsapptool.fragmentsOur.DecorationText.Adepter.isInstalled;
import static com.affixstudio.whatsapptool.fragmentsOur.HomeFragment.shouldRecoverMediaFocused;
import static com.affixstudio.whatsapptool.modelOur.sound.playSound;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.getData.getQuickMessage;
import com.affixstudio.whatsapptool.model.preferences.PreferencesManager;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomSmallDialog;
import com.affixstudio.whatsapptool.modelOur.whatsappAccessibility;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;




public class auto_reply_custom extends Fragment {

    SwitchCompat  groupReplySwitch;
    private static final int CONTACT_PICK_REQUEST = 1212;
    private static final int CONTACT_SINGLE_PICKER_REQUEST =1312 ;
    private static final int PEMISSION_OF_ACCESSEBLITY = 63;
    database db;
    database specificDB;
    BottomSheetDialog contactListDialog,addNewSpecific;


    ArrayList<Integer> id=new ArrayList<>();// database row id
    ArrayList<String> contactNames=new ArrayList<>();

    ContactInfo contactInfo=new ContactInfo();
    specificContactInfo specificContactInfo=new specificContactInfo();


    ArrayList<ContactInfo> contactInfos=new ArrayList<>();
    ArrayList<specificContactInfo> specificContactInfos=new ArrayList<>();
    ArrayList<specificContactInfo> specificGroupList=new ArrayList<>();

    SharedPreferences sp;
    SharedPreferences.Editor spEdit;
    RecyclerView.Adapter mAdepter;
    RecyclerView contactListRecycleView,specificRecycle; // recycle in the bottom pop up
    TextView listDialogTitle,clearAllContact;
    LinearLayout noContactTV;



    LinearLayout myContactListLayout,specificLayout; // layouts in the bottom pop up dialog
    private RecyclerView.Adapter<RecyclerView.ViewHolder> specificContactAdapter;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> specificContactGroupAdapter;

    int listType=0; // indication which button is clicked " view list  or My contact  or Group " 0,1,2 consequently


    View view;
    private boolean isEditingSpecificReply=false;
     boolean isPickingContact=false;
    private boolean isPickingForSpecific=false;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view= inflater.inflate(R.layout.fragment_auto_reply_custom, container, false);
        sp=getActivity().getSharedPreferences("affix",0);
        spEdit=sp.edit();


//        AppLovinNative lovinNative=new AppLovinNative(R.layout.native_ad_applovin_mid,getActivity());
//
//        lovinNative.mid(view.findViewById(R.id.ad_frame));
       // lovinNative.small(view.findViewById(R.id.ad_frame2));









        String query="CREATE TABLE IF NOT EXISTS "+getString(R.string.contactList)+"(_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',Number text DEFAULT 'Not set',ContactID text DEFAULT 'Not set') ";
        db=new database(getContext(),getString(R.string.contactList),query,1);

        String query2="CREATE TABLE IF NOT EXISTS "+getString(R.string.specificReply)+"(_id INTEGER PRIMARY KEY autoincrement, SenderName text DEFAULT 'Not set',Message text  DEFAULT 'Not set',Replay text  DEFAULT 'Not set',MatchType text,SendTo text DEFAULT 'com.whatsapp',isGroup text DEFAULT '1',Customised text DEFAULT 'true') ";/* Group =1 is not Group 2=yes*/
        specificDB=new database(getContext(),getString(R.string.specificReply),query2,1);

        query4="CREATE TABLE IF NOT EXISTS "+getString(R.string.groupList)+"(_id INTEGER PRIMARY KEY autoincrement, GroupName text DEFAULT 'Not set') ";

        Cursor c=db.getinfo(1);
        if (contactInfos.size()>0)
        {

            contactInfos.clear();

        }
        while (c.moveToNext())
        {
            contactInfo=new ContactInfo(c.getString(1)
                    ,c.getString(3),c.getString(2),c.getInt(0));
            contactInfos.add(contactInfo);

        }

        Cursor c1=specificDB.getinfo(1);
        if (specificContactInfos.size()>0)
        {

            specificContactInfos.clear();

        }
        while (c1.moveToNext())
        {
            specificContactInfo=new specificContactInfo(c1.getString(1)
                    ,c1.getString(2),c1.getString(3),c1.getString(7),c1.getInt(6),c1.getInt(0)
            ,c1.getString(4));
            specificContactInfos.add(specificContactInfo);

        }

        filteredSpecificList=new ArrayList<>(specificContactInfos);
        filteredSpecificGroupList=new ArrayList<>();


        if (shouldRecoverMediaFocused==1)
        {
            ScrollView customAutoScrollView=view.findViewById(R.id.customAutoScrollView);
//            Space groupSpecificSpace=view.findViewById(R.id.groupSpecificSpace);
//            customAutoScrollView.scrollTo(0,(int) groupSpecificSpace.getY());
            shouldRecoverMediaFocused=0;

        }


        ImageView pickContact=view.findViewById(R.id.pickContact);
        pickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                   // launchMultiplePhonePicker();
                   showContactPicker();
                }catch (Exception e)
                {
                    Toast.makeText(getContext(), ""+e, Toast.LENGTH_LONG).show();
                }





            }
        });


        addGroupToListDialog=new BottomSheetDialog(getContext());
        addGroupToListDialog.setContentView(addNewGroupName());
        view.findViewById(R.id.pickGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addGroupToListDialog.show();
            }
        });


        RadioButton
                everyOne=view.findViewById(R.id.everyOne),
                contactList=view.findViewById(R.id.contactList),
                exceptContectList=view.findViewById(R.id.exceptContectList),
                exceptPhoneContact=view.findViewById(R.id.exceptPhoneContact);
        SwitchCompat specificContactReply=view.findViewById(R.id.specificContactReply);
        SwitchCompat specificGroupReply=view.findViewById(R.id.specificGroupReply);
        MaterialCardView testAutoBtn = view.findViewById(R.id.testAutoBtn);
        Button viewGroupContact = view.findViewById(R.id.viewGroupContact);

        ImageButton addSpecificAutoBt=view.findViewById(R.id.addSpecificAutoBt)
                ,addSpecificGroupAutoBt=view.findViewById(R.id.addSpecificGroupAutoBt);


        groupReplySwitch = view.findViewById(R.id.groupReplySwitch);







         PreferencesManager preferencesManager=PreferencesManager.getPreferencesInstance(getContext());;
        groupReplySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Ignore if this is not triggered by user action but just UI update in onResume() #62
            if (preferencesManager.isGroupReplyEnabled() == isChecked) {
                groupReplySwitch.setTextColor(ContextCompat.getColor(getContext(),R.color.colorSecondaryVariant));
                return;
            }
            if (isChecked) {
                playSound(getContext(), R.raw.switch_sound);
                groupReplySwitch.setTextColor(ContextCompat.getColor(getContext(),R.color.colorSecondaryVariant));
                Toast.makeText(getContext(), R.string.group_reply_on_info_message, Toast.LENGTH_SHORT).show();
            } else {
                groupReplySwitch.setTextColor(ContextCompat.getColor(getContext(),R.color.hint));
                Toast.makeText(getContext(), R.string.group_reply_off_info_message, Toast.LENGTH_SHORT).show();
            }
            preferencesManager.setGroupReplyPref(isChecked);


        });

        if (preferencesManager.isGroupReplyEnabled())
        {
            groupReplySwitch.setChecked(true);
        }









        addSpecificAutoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialogTitle.setText("Specific Contact");
                myContactListLayout.setVisibility(View.GONE);
                specificLayout.setVisibility(View.VISIBLE);
                contactNameBox.setHint("Contact name (case sensitive)");
                add_specificTitle.setText("New Contact");
                listType=1;
                makeEmpty();
                addNewSpecific.show();
            }
        });

        addSpecificGroupAutoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialogTitle.setText("Specific Group");
                myContactListLayout.setVisibility(View.GONE);
                specificLayout.setVisibility(View.VISIBLE);
                contactNameBox.setHint("Group name (case sensitive)");
                add_specificTitle.setText("New Group");
                listType=2;
                makeEmpty();
                addNewSpecific.show();
                add_specificTitle.setText("New Group");
            }
        });

        specificContactReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (haveSub.equals("no") && b)
//                {
//                    getContext().startActivity(new Intent(getContext(), PurchaseActivity.class));
//                    specificContactReply.setChecked(false);
//
//
//                    return;
//                }
                if (b)
                {
                    spEdit.putBoolean("specificReplyOn",true).apply();

                }else {
                    spEdit.putBoolean("specificReplyOn",false).apply();

                }
            }
        });

        specificGroupReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (haveSub.equals("no") && b)
//                {
//                    getContext().startActivity(new Intent(getContext(), PurchaseActivity.class));
//                    specificGroupReply.setChecked(false);
//
//
//                    return;
//                }
                if (b)
                {
                    spEdit.putBoolean("specificGroupReplyOn",true).apply();

                }else {
                    spEdit.putBoolean("specificGroupReplyOn",false).apply();

                }
            }
        });



        everyOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    spEdit.putInt("replyTo",0).apply();// reply to everyone
                    view.findViewById(R.id.listCardGroup).setBackgroundColor(getContext().getResources().getColor(R.color.white));
                    view.findViewById(R.id.listCard).setBackgroundColor(getContext().getResources().getColor(R.color.white));
                }
            }
        });
        contactList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    spEdit.putInt("replyTo",1).apply();// reply to contactList
                    view.findViewById(R.id.listCardGroup).setBackgroundColor(getContext().getResources().getColor(R.color.VA_on));
                    view.findViewById(R.id.listCard).setBackgroundColor(getContext().getResources().getColor(R.color.VA_on));
                }
            }
        });
        exceptContectList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    spEdit.putInt("replyTo",2).apply(); // reply to exceptContectList
                    view.findViewById(R.id.listCardGroup).setBackgroundColor(getContext().getResources().getColor(R.color.VA_off));
                    view.findViewById(R.id.listCard).setBackgroundColor(getContext().getResources().getColor(R.color.VA_off));
                }
            }
        });
        exceptPhoneContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    spEdit.putInt("replyTo",3).apply();// reply to exceptPhoneContact
                }
            }
        });

        //*******                      contact bottom notification                            ******//

        contactListDialog=new BottomSheetDialog(getContext());
       // contactListDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.bottom_nav_bg));
      //  contactListDialog.getWindow().setBackgroundDrawableResource(R.drawable.bottom_nav_bg);



        View dialogView=getActivity().getLayoutInflater().inflate(R.layout.contact_list,null);

         contactListRecycleView=dialogView.findViewById(R.id.showContactRView);
        specificRecycle=dialogView.findViewById(R.id.specificRecycle);

         noContactTV=dialogView.findViewById(R.id.noDataLayout);

         listDialogTitle=dialogView.findViewById(R.id.listDialogTitle);
                clearAllContact=dialogView.findViewById(R.id.clearAllContact);


        ImageView DialogpickContact=dialogView.findViewById(R.id.pickContact);
        TextInputEditText searchContact=dialogView.findViewById(R.id.autoReplyTextInputEditText);

        myContactListLayout=dialogView.findViewById(R.id.MyContactList);
        specificLayout=dialogView.findViewById(R.id.specificContactLayout);


        //*******                      add specific bottom notification                  ******//

        setLayoutForAddSpecificDialog();






        searchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterContact(editable.toString());
            }
        });

        DialogpickContact.setOnClickListener(view1 -> {
            contactListDialog.dismiss();
            if (listType==0)
            {
                showContactPicker();
            }else if (listType==4)
            {
                addGroupToListDialog.show();

            }else

            {
                createAutoReply.setText("Create");
                makeEmpty();
                addNewSpecific.show(); // was doing on specific item click
               ;
                if ( listType==2)
                {
                   // addNewSpecific.show();
                    add_specificTitle.setText("New Group");
                    contactBtn.setBackground(getContext().getDrawable(R.drawable.group_icon_focused));
                }else if (listType==1)
                {
                    add_specificTitle.setText("New Contact");
                    contactBtn.setBackground(getContext().getDrawable(R.drawable.contact_icon));
                }

            }
        });

        clearAllContact.setOnClickListener(view1 -> {

            if (listType==4)
            {
                String query1="CREATE TABLE IF NOT EXISTS "+getString(R.string.groupList)+"(_id INTEGER PRIMARY KEY autoincrement, GroupName text DEFAULT 'Not set') ";
                database db=new database(getContext(),getString(R.string.groupList),query1,1);
                db.getReadableDatabase().execSQL("DELETE FROM "+getString(R.string.groupList)+" WHERE 1");
                db.close();
            }else {
                db.getReadableDatabase().execSQL("DELETE FROM "+getString(R.string.contactList)+" WHERE 1");
            }

            noContactTV.setVisibility(View.VISIBLE);
            contactListRecycleView.setVisibility(View.GONE);
            clearAllContact.setVisibility(View.GONE);

        });






        contactListDialog.setContentView(dialogView);



        mAdpterInisialize(); // no use

        view.findViewById(R.id.deleteAllMediaSmall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listType=0;
                listDialogTitle.setText("My Contact List");
                specificLayout.setVisibility(View.GONE);

                Cursor c=db.getinfo(1);
                if (contactInfos.size()>0)
                {

                    contactInfos.clear();

                }
                while (c.moveToNext())
                {
                    contactInfo=new ContactInfo(c.getString(1)
                            ,c.getString(3),c.getString(2),c.getInt(0));
                    contactInfos.add(contactInfo);

                }

                filteredContactList=new ArrayList<>(contactInfos);


                if (filteredContactList.size()>0){
                    noContactTV.setVisibility(View.GONE);
                    myContactListLayout.setVisibility(View.VISIBLE);
                    contactListRecycleView.setAdapter(mAdepter);
                    clearAllContact.setVisibility(View.VISIBLE);
                }else {
                    noContactTV.setVisibility(View.VISIBLE);
                    myContactListLayout.setVisibility(View.GONE);
                    clearAllContact.setVisibility(View.GONE);

                }
                searchContact.setText("");


                contactListDialog.show();
            }
        });

        view.findViewById(R.id.viewGroupList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query="CREATE TABLE IF NOT EXISTS "+getString(R.string.groupList)+"(_id INTEGER PRIMARY KEY autoincrement, GroupName text DEFAULT 'Not set') ";
                database db=new database(getContext(),getString(R.string.groupList),query,1);

                listType=4;
                listDialogTitle.setText("My Group List");
               specificLayout.setVisibility(View.GONE);

                Cursor c=db.getinfo(1);
                if (contactInfos.size()>0)
                {

                    contactInfos.clear();

                }
                while (c.moveToNext())
                {
                    contactInfo=new ContactInfo(c.getString(1)
                            ,c.getInt(0));
                    contactInfos.add(contactInfo);

                }

                filteredContactList=new ArrayList<>(contactInfos);



                i("filteredContactList "+filteredContactList.size());
                if (filteredContactList.size()>0){
                    mAdpterInisialize();
                    noContactTV.setVisibility(View.GONE);
                    myContactListLayout.setVisibility(View.VISIBLE);
                    contactListRecycleView.removeAllViews();
                    contactListRecycleView.setAdapter(mAdepter);
                    contactListRecycleView.setVisibility(View.VISIBLE);
                    clearAllContact.setVisibility(View.VISIBLE);
                    i("myContactListLayout visible "+myContactListLayout.getVisibility()+" contactListRecycleView visible "+contactListRecycleView.getVisibility());
                }else {
                    noContactTV.setVisibility(View.VISIBLE);
                    myContactListLayout.setVisibility(View.GONE);
                    clearAllContact.setVisibility(View.GONE);

                }
                searchContact.setText("");
                contactListDialog.show();
                db.close();
            }
        });




        specificContactAdapter= new RecyclerView.Adapter<>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_specific_customlist,null)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {

                int Op=h.getBindingAdapterPosition(); // Original position of the view
                int index=filteredSpecificList.size()-1-Op;

                CardView specificRVCard=h.itemView.findViewById(R.id.specificRVCard);
                TextView
                        senderName=h.itemView.findViewById(R.id.senderName),
                        incomingMessage=h.itemView.findViewById(R.id.incomingMessage),
                        replyMessages=h.itemView.findViewById(R.id.replyMessages),
                        contactIcon=h.itemView.findViewById(R.id.contactIcon);


                ImageButton deleteRecords=h.itemView.findViewById(R.id.deleteRecords);


                senderName.setText(filteredSpecificList.get(index).SenderName);
                incomingMessage.setText(filteredSpecificList.get(index).Message);
                replyMessages.setText(filteredSpecificList.get(index).Replay);
                contactIcon.setText(""+filteredSpecificList.get(index).SenderName.charAt(0));

                senderName.setText(filteredSpecificList.get(index).SenderName);

                deleteRecords.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {



                            String s="DELETE FROM "+getString(R.string.specificReply)+" WHERE _id="+filteredSpecificList.get(index).id;
                            Log.i("autoReply","filteredSpecificList index "+filteredSpecificList.get(index).id);

                            SQLiteDatabase sl=specificDB.getReadableDatabase();
                            sl.execSQL(s);

                            int i=0;
                            for (specificContactInfo specificContactInfo:specificContactInfos)
                            {
                                if (specificContactInfo.SenderName.equals(filteredSpecificList.get(index).SenderName))
                                {
                                    break;

                                }
                                i++;
                            }


                            Log.i("autoReply","contactInfo index "+specificContactInfos.get(i).SenderName);
                            ;
                            specificContactInfos.remove(i);
                            synchronized (specificContactInfos)
                            {
                                specificContactInfos.notifyAll();
                            }
                            filteredSpecificList.remove(index);
                            synchronized (filteredSpecificList)
                            {
                                filteredSpecificList.notifyAll();
                            }

                            specificContactAdapter.notifyItemRemoved(Op-1);
                            specificContactAdapter.notifyItemRangeChanged(Op-1,filteredSpecificList.size());
                            if (filteredSpecificList.size()==0)
                            {
                                noContactTV.setVisibility(View.VISIBLE);
                                myContactListLayout.setVisibility(View.GONE);
                                clearAllContact.setVisibility(View.GONE);
                            }


                        }catch (Exception e)
                        {
                            Log.e("autoReply",e.getMessage());
                        }



                    }
                });

                specificRVCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEditSpecific(filteredSpecificList.get(index));
                    }
                });





            }

            @Override
            public int getItemCount() {
                return filteredSpecificList.size();
            }

        };




           RecyclerView.Adapter contactPickerAdapter= new RecyclerView.Adapter<>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_specific_customlist,null)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {

                int Op=h.getBindingAdapterPosition(); // Original position of the view
                int index=filteredSpecificList.size()-1-Op;

                CardView specificRVCard=h.itemView.findViewById(R.id.specificRVCard);
                TextView
                        senderName=h.itemView.findViewById(R.id.senderName),
                        incomingMessage=h.itemView.findViewById(R.id.incomingMessage),
                        replyMessages=h.itemView.findViewById(R.id.replyMessages),
                        contactIcon=h.itemView.findViewById(R.id.contactIcon);


                ImageButton deleteRecords=h.itemView.findViewById(R.id.deleteRecords);


                senderName.setText(filteredSpecificList.get(index).SenderName);
                incomingMessage.setText(filteredSpecificList.get(index).Message);
                replyMessages.setText(filteredSpecificList.get(index).Replay);
                contactIcon.setText(""+filteredSpecificList.get(index).SenderName.charAt(0));

                senderName.setText(filteredSpecificList.get(index).SenderName);

                deleteRecords.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {



                            String s="DELETE FROM "+getString(R.string.specificReply)+" WHERE _id="+filteredSpecificList.get(index).id;
                            Log.i("autoReply","filteredSpecificList index "+filteredSpecificList.get(index).id);

                            SQLiteDatabase sl=specificDB.getReadableDatabase();
                            sl.execSQL(s);

                            int i=0;
                            for (specificContactInfo specificContactInfo:specificContactInfos)
                            {
                                if (specificContactInfo.SenderName.equals(filteredSpecificList.get(index).SenderName))
                                {
                                    break;

                                }
                                i++;
                            }


                            Log.i("autoReply","contactInfo index "+specificContactInfos.get(i).SenderName);
                            ;
                            specificContactInfos.remove(i);
                            synchronized (specificContactInfos)
                            {
                                specificContactInfos.notifyAll();
                            }
                            filteredSpecificList.remove(index);
                            synchronized (filteredSpecificList)
                            {
                                filteredSpecificList.notifyAll();
                            }

                            specificContactAdapter.notifyItemRemoved(Op-1);
                            specificContactAdapter.notifyItemRangeChanged(Op-1,filteredSpecificList.size());
                            if (filteredSpecificList.size()==0)
                            {
                                noContactTV.setVisibility(View.VISIBLE);
                                myContactListLayout.setVisibility(View.GONE);
                                clearAllContact.setVisibility(View.GONE);
                            }


                        }catch (Exception e)
                        {
                            Log.e("autoReply",e.getMessage());
                        }



                    }
                });

                specificRVCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEditSpecific(filteredSpecificList.get(index));
                    }
                });





            }

            @Override
            public int getItemCount() {
                return filteredSpecificList.size();
            }

        };









        view.findViewById(R.id.viewSpecificContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listType=1;

                showSpecificContactBottomDialog();




//                Intent i = new Intent(getContext(), com.affixstudio.whatsapptool.activity.specificContactReply.class);
//                i.putExtra("isGroup",false);
//                Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
//                startActivity(i,b);
            }
        });


        specificContactGroupAdapter=new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new  RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_specific_customlist,null)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
                int Op=h.getBindingAdapterPosition(); // Original position of the view
                int index=filteredSpecificGroupList.size()-1-Op;

                CardView specificRVCard=h.itemView.findViewById(R.id.specificRVCard);
                TextView
                        senderName=h.itemView.findViewById(R.id.senderName),
                        incomingMessage=h.itemView.findViewById(R.id.incomingMessage),
                        replyMessages=h.itemView.findViewById(R.id.replyMessages),
                        contactIcon=h.itemView.findViewById(R.id.contactIcon);


                ImageButton deleteRecords=h.itemView.findViewById(R.id.deleteRecords);


                senderName.setText(filteredSpecificGroupList.get(index).SenderName);
                incomingMessage.setText(filteredSpecificGroupList.get(index).Message);
                replyMessages.setText(filteredSpecificGroupList.get(index).Replay);
                contactIcon.setText(""+filteredSpecificGroupList.get(index).SenderName.charAt(0));

                senderName.setText(filteredSpecificGroupList.get(index).SenderName);

                deleteRecords.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {



                            String s="DELETE FROM "+getString(R.string.specificReply)+" WHERE _id="+filteredSpecificGroupList.get(index).id;
                            Log.i("autoReply","filteredSpecificGroupList index "+filteredSpecificGroupList.get(index).id);

                            SQLiteDatabase sl=specificDB.getReadableDatabase();
                            sl.execSQL(s);

                            int i=0;
                            for (specificContactInfo specificContactInfo:specificContactInfos)
                            {
                                if (specificContactInfo.SenderName.equals(filteredSpecificGroupList.get(index).SenderName))
                                {
                                    break;

                                }
                                i++;
                            }


                            Log.i("autoReply","contactInfo index "+specificContactInfos.get(i).SenderName);
                            ;
                            specificContactInfos.remove(i);
                            synchronized (specificContactInfos)
                            {
                                specificContactInfos.notifyAll();
                            }
                            filteredSpecificGroupList.remove(index);
                            synchronized (filteredSpecificGroupList)
                            {
                                filteredSpecificGroupList.notifyAll();
                            }
                            specificContactGroupAdapter.notifyItemRemoved(Op-1);
                            specificContactGroupAdapter.notifyItemRangeChanged(Op-1,filteredSpecificGroupList.size());

                            if (filteredSpecificGroupList.size()==0)
                            {
                                noContactTV.setVisibility(View.VISIBLE);
                                myContactListLayout.setVisibility(View.GONE);
                                clearAllContact.setVisibility(View.GONE);
                            }

                        }catch (Exception e)
                        {
                            Log.e("autoReply",e.getMessage());
                        }



                    }
                });
                specificRVCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEditSpecific(filteredSpecificGroupList.get(index));
                    }
                });


            }

            @Override
            public int getItemCount() {
                return filteredSpecificGroupList.size();
            }
        };


        view.findViewById(R.id.viewGroupContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listType=2;

                showGroupSpcificBottomDialog();


//                Intent i = new Intent(getContext(), com.affixstudio.whatsapptool.activity.specificContactReply.class);
//                i.putExtra("isGroup",true);
//                Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
//                startActivity(i,b);
            }


        });


        testAutoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), com.affixstudio.whatsapptool.activity.autoReplyTest.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(i,b);
            }
        });

        if (sp.getBoolean("specificReplyOn",false))
        {
            specificContactReply.setChecked(true);
        }
        if (sp.getBoolean("specificGroupReplyOn",false))
        {
            specificGroupReply.setChecked(true);
        }

        int replyTo=sp.getInt("replyTo",0);
        if (replyTo==0)
        {
            everyOne.setChecked(true);
        }else if (replyTo==1)
        {
            contactList.setChecked(true);
        }else if (replyTo==2)
        {
            exceptContectList.setChecked(true);
        }else if (replyTo==3)
        {
            exceptPhoneContact.setChecked(true);
        }

        bottomSmallDialog bsd=new bottomSmallDialog();

        view.findViewById(R.id.whatSendTo).setOnClickListener(new View.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(View view) {
                                                                      bsd.showinfo(getContext(), 2);
                                                                  }
                                                              });
        view.findViewById(R.id.mylistWhat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.showinfo(getContext(), 3);
            }
        });
        view.findViewById(R.id.specificReplyWhat).setOnClickListener(new View.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(View view) {
                                                                      bsd.showinfo(getContext(), 4);
                                                                  }
                                                              });





        return view;
    }

//    private void loadNativeAds(AdLoader adLoader) {
//        AdRequest adRequest=new AdRequest.Builder().build();
//        adLoader.loadAd(adRequest);
//    }


    private void mAdpterInisialize()
    {
        mAdepter=new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item,null)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
                int Op=h.getBindingAdapterPosition(); // Original position of the view
                int index=filteredContactList.size()-1-Op;


                CardView contactCard=h.itemView.findViewById(R.id.contactListCard);
                ImageView deleteContact=h.itemView.findViewById(R.id.deleteContact);
                TextView contactName=h.itemView.findViewById(R.id.contactName);
                TextView number=h.itemView.findViewById(R.id.contactNumber);
                if (listType!=4) // when not group
                {
                    number.setText(filteredContactList.get(index).number);
                    number.setVisibility(View.VISIBLE);
                }else {
                    number.setVisibility(View.GONE);
                }

                TextView textIcon=h.itemView.findViewById(R.id.textIcon);

                contactName.setText(filteredContactList.get(index).contactNames);


                textIcon.setText(""+contactName.getText().toString().charAt(0));

                final  View v=h.itemView;
                deleteContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {




                            if (listType==4)
                            {
                                database d=new database(getContext(),getString(R.string.groupList),query4,1);
                                String s="DELETE FROM "+getString(R.string.groupList)+" WHERE _id="+filteredContactList.get(index).id;

                                d.getReadableDatabase().execSQL(s);
                                d.close();
                            }else {

                                String s="DELETE FROM "+getString(R.string.contactList)+" WHERE _id="+filteredContactList.get(index).id;
                                SQLiteDatabase sl=db.getReadableDatabase();
                                sl.execSQL(s);
                            }



                            int i=0;
                            for (ContactInfo contactInfo:contactInfos)
                            {
                                if (contactInfo.contactNames.equals(filteredContactList.get(index).contactNames))
                                {
                                    break;

                                }
                                i++;
                            }


                            Log.i("autoReply","contactInfo index "+contactInfos.get(i).contactNames);
                            ;
                            contactInfos.remove(i);
                            synchronized(contactInfos){
                                contactInfos.notifyAll();
                            }
                            filteredContactList.remove(index);

                            synchronized(filteredContactList){
                                filteredContactList.notifyAll();
                            }
                           // contactCard.setVisibility(View.GONE);
                            mAdepter.notifyItemRemoved(Op-1);
                            mAdepter.notifyItemRangeChanged(Op-1,filteredContactList.size());

                            if (filteredContactList.size()==0)
                            {
                                noContactTV.setVisibility(View.VISIBLE);
                                myContactListLayout.setVisibility(View.GONE);
                                clearAllContact.setVisibility(View.GONE);
                            }

                        }catch (Exception e)
                        {
                            Log.e("autoReply",e.getMessage());
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return filteredContactList.size();
            }
        };

    }


    int currentEditingRowID=0; // id of row which is currently editing

    private void showEditSpecific(auto_reply_custom.specificContactInfo specificContactInfo) {
        currentEditingRowID=specificContactInfo.id;
        makeEmpty();
        isEditingSpecificReply=true;

        senderName.setText(specificContactInfo.SenderName);
        if (specificContactInfo.Customised.equals("true"))
        {
            customisedCheckBox.setChecked(true);
            messageIncoming.setText(specificContactInfo.Message);

            if (specificContactInfo.matchType.equals("exact"))
            {
                exect.setChecked(true);
            }else {
                contain.setChecked(true);
            }

        }else {
            customisedCheckBox.setChecked(false);
        }
        Reply.setText(specificContactInfo.Replay);
        createAutoReply.setText("Save");
        addNewSpecific.show();


                 if ( listType==2)
                {
                   // addNewSpecific.show();
                    add_specificTitle.setText("Edit Group");
                    contactBtn.setBackground(getContext().getDrawable(R.drawable.group_icon_focused));

                }else if (listType==1)
                {
                    add_specificTitle.setText("Edit Contact");
                    contactBtn.setBackground(getContext().getDrawable(R.drawable.contact_icon));
                }

    }

    private void showSpecificContactBottomDialog() {

        listDialogTitle.setText("Specific Contact");
        myContactListLayout.setVisibility(View.GONE);
        specificLayout.setVisibility(View.VISIBLE);
        contactNameBox.setHint("Contact name (case sensitive)");
        Cursor c=specificDB.getinfo(1);
        if (specificContactInfos.size()>0)
        {

            specificContactInfos.clear();

        }
        while (c.moveToNext())
        {
            if (c.getInt(6)!=2)
            {
                specificContactInfo=new specificContactInfo(c.getString(1)
                        ,c.getString(2),c.getString(3),c.getString(7),c.getInt(6),c.getInt(0)
                ,c.getString(4));
                specificContactInfos.add(specificContactInfo);
            }


        }

        filteredSpecificList=new ArrayList<>(specificContactInfos);


        if (filteredSpecificList.size()>0){
            noContactTV.setVisibility(View.GONE);
            specificLayout.setVisibility(View.VISIBLE);
            specificRecycle.setAdapter(specificContactAdapter);
            clearAllContact.setVisibility(View.VISIBLE);
        }else {
            noContactTV.setVisibility(View.VISIBLE);
            specificLayout.setVisibility(View.GONE);
            clearAllContact.setVisibility(View.GONE);

        }


        //Toast.makeText(getContext(), "showSpecificContactBottomDialog "+filteredSpecificList.size(), Toast.LENGTH_SHORT).show();
        contactBtn.setBackground(getResources().getDrawable(R.drawable.contact_icon));

        contactListDialog.show();

    }

    public void showGroupSpcificBottomDialog() {
        listDialogTitle.setText("Specific Group");
        myContactListLayout.setVisibility(View.GONE);
        specificLayout.setVisibility(View.VISIBLE);
        contactNameBox.setHint("Group name (case sensitive)");
        if (specificGroupList.size()>0){
            specificGroupList.clear();
        }
        Cursor c=specificDB.getinfo(1);
        if (specificContactInfos.size()>0)
        {

            specificContactInfos.clear();

        }
        while (c.moveToNext())
        {
            specificContactInfo=new specificContactInfo(c.getString(1)
                    ,c.getString(2),c.getString(3),c.getString(7),c.getInt(6),c.getInt(0)
            ,c.getString(4));
            specificContactInfos.add(specificContactInfo);

        }

        for (int i=0;i<specificContactInfos.size();i++)
        {
            if (specificContactInfos.get(i).isGroup==2)
            {

                specificContactInfo=new specificContactInfo(specificContactInfos.get(i).SenderName,
                        specificContactInfos.get(i).Message,specificContactInfos.get(i).Replay,specificContactInfos.get(i).Customised
                        ,specificContactInfos.get(i).isGroup,specificContactInfos.get(i).id,
                        specificContactInfos.get(i).matchType);
                specificGroupList.add(specificContactInfo);

            }
        }


        filteredSpecificGroupList=new ArrayList<>(specificGroupList);


        if (filteredSpecificGroupList.size()>0){
            noContactTV.setVisibility(View.GONE);
            specificRecycle.setVisibility(View.VISIBLE);
            specificRecycle.setAdapter(specificContactGroupAdapter);
            clearAllContact.setVisibility(View.VISIBLE);
        }else {
            noContactTV.setVisibility(View.VISIBLE);
            specificLayout.setVisibility(View.GONE);
            clearAllContact.setVisibility(View.GONE);
        }


        contactBtn.setBackground(getContext().getDrawable(R.drawable.group_icon_focused));

        specificRecycle.removeAllViews();
        contactListDialog.show();




    }
    TextInputEditText senderName,messageIncoming,Reply;
    TextView previewIncomingMsg,previewAutoMsg,add_specificTitle;
    String messageIncommingString;
    MaterialCheckBox customisedCheckBox;
    ImageButton contactBtn;
    TextInputLayout contactNameBox;
    RadioButton exect,contain;
    Button createAutoReply;
    private void setLayoutForAddSpecificDialog() {


        addNewSpecific=new BottomSheetDialog(getContext());

        View addNew=getActivity().getLayoutInflater().inflate(R.layout.add_specific_auto,null);

        Button deleteAutoReply=addNew.findViewById(R.id.deleteReplyBt);
                createAutoReply=addNew.findViewById(R.id.createReplyBt);

         previewIncomingMsg=addNew.findViewById(R.id.previewIncomingMsg);
         previewAutoMsg=addNew.findViewById(R.id.previewAutoMsg);

         senderName=addNew.findViewById(R.id.senderName);
         messageIncoming=addNew.findViewById(R.id.message);
         Reply=addNew.findViewById(R.id.Reply);
         contactNameBox=addNew.findViewById(R.id.contactNameBox);
         add_specificTitle=addNew.findViewById(R.id.add_specificTitle);

         ImageButton
                 customisedIncomingTutorial=addNew.findViewById(R.id.customisedIncomingTutorial),
                 messageMatchTutorial=addNew.findViewById(R.id.messageMatchTutorial);



         bottomSmallDialog bsd=new bottomSmallDialog();

         customisedIncomingTutorial.setOnClickListener(view -> {

             bsd.showinfo(getContext(),6);
         });
         messageMatchTutorial.setOnClickListener(view -> {
             bsd.showinfo(getContext(),7);
         });




        AutoCompleteTextView quick_ms_select=addNew.findViewById(R.id.quick_ms_select);
        LinearLayout customisedMsgLayout=addNew.findViewById(R.id.customisedMsgLayout);
         contactBtn=addNew.findViewById(R.id.contactBtn);

         customisedCheckBox=addNew.findViewById(R.id.customisedCheckBox);
                 exect=addNew.findViewById(R.id.exact);
                contain=addNew.findViewById(R.id.contain);

        getQuickMessage gQm=new getQuickMessage(getContext(),getActivity()); // getting all quickMessage by the class
        String[] allMessage=gQm.allMessage();

         messageIncommingString=messageIncoming.getText().toString();

        contain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    if (customisedCheckBox.isChecked())
                    {
                        if (!messageIncommingString.isEmpty())
                        {
                            previewIncomingMsg.setText(messageIncommingString+" "+allMessage[new Random().nextInt(allMessage.length)]);
                        }
                    }

                }
            }
        });

        exect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    if (customisedCheckBox.isChecked())
                    {
                        if (!messageIncommingString.isEmpty())
                        {
                            previewIncomingMsg.setText(messageIncommingString);
                        }
                    }

                }
            }
        });


        customisedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    customisedMsgLayout.setVisibility(View.VISIBLE);
                    if (messageIncommingString.isEmpty())
                    {
                        exect.setChecked(true);
                        previewIncomingMsg.setText(getString(R.string.incomingMessagePreview));
                    }else {
                        previewIncomingMsg.setText(messageIncommingString);
                    }
                }else {
                    customisedMsgLayout.setVisibility(View.GONE);
                    previewIncomingMsg.setText("Any incoming message");
                }
            }
        });


        messageIncoming.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                messageIncommingString=editable.toString();
                if (customisedCheckBox.isChecked()) {


                    if (contain.isChecked()) {

                        if (!messageIncommingString.isEmpty()) {
                            previewIncomingMsg.setText(messageIncommingString + " " + allMessage[new Random().nextInt(allMessage.length)]);
                        }


                    } else {
                        previewIncomingMsg.setText(editable.toString());
                    }

                }else {
                    previewIncomingMsg.setText(editable.toString());
                }


            }
        });
        Reply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                previewAutoMsg.setText(editable.toString());
            }
        });

        customisedCheckBox.setChecked(true);


        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,allMessage);

        quick_ms_select.setAdapter(arrayAdapter);
        quick_ms_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Reply.setText(Reply.getText().toString()+" "+allMessage[i]);
            }
        });

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isPickingForSpecific=true; //todo can't save new  group and contact in specific
                showSinglePickContact();
            }
        });


        createAutoReply.setOnClickListener(view1 -> {

            String stringSenderName= Objects.requireNonNull(senderName.getText()).toString();
            String replyString= Objects.requireNonNull(Reply.getText()).toString();
            String imcomingString= Objects.requireNonNull(messageIncoming.getText()).toString();

            if (stringSenderName.isEmpty())
            {
                senderName.setError("Enter Sender Name");
            }else  if (!customisedCheckBox.isChecked())
            {

                if (replyString.isEmpty())
                {
                    Reply.setError("Type Auto Reply text");
                }else {
                    createNewAutoReply(stringSenderName,replyString,imcomingString
                    ,false,false,false);
                }
            } else
            {
                if (imcomingString.isEmpty())
                {
                    messageIncoming.setError("Type Income message");
                }else if(replyString.isEmpty())
                {
                    Reply.setError("Type Auto Reply text");
                }else {
                    createNewAutoReply(stringSenderName, replyString, imcomingString, true,
                            contain.isChecked(), exect.isChecked());

                }



             }

        });
        deleteAutoReply.setOnClickListener(view1 -> {
            makeEmpty();
            addNewSpecific.dismiss();
            if (listType==1)
            {
                showSpecificContactBottomDialog();
            }else
            {
                showGroupSpcificBottomDialog();
            }

        });






        addNewSpecific.setContentView(addNew);


    }

    @SuppressLint("Range")
    void fetchContactData()
    {

        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);


        if ((cur != null ? cur.getCount() : 0) > 0) {

            while (!Objects.isNull(cur) && cur.moveToNext())
            {

                 String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0)
                {


                        String phoneNo = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                        if (!nameFromContact.contains(name))
                        {
                            nameFromContact.add(name);
                            phFromContact.add(phoneNo);
                        }

                }
            }
        }
        if(cur!=null){
            cur.close();
        }


    }


    ArrayList<String> phFromContact=new ArrayList<>();
    ArrayList<String> nameFromContact=new ArrayList<>();
    ProgressDialog pd;

    @SuppressLint({"Range", "StaticFieldLeak"})
    void getContact()
    {



        new AsyncTask<String, String, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

               // pd.show();
                if (nameFromContact.size() <= 0)
                {
                    pd.show();
                }




            }

            @Override
            protected String doInBackground(String... strings) {
               // fetchContactFromDB();
                fetchContactData(); // fetching contact data from phone




                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pd.dismiss();

                i("phFromContact size " +phFromContact.get(0));


                    filteredPickedNameList=new ArrayList<>(nameFromContact);
                    filteredPickedNumList=new ArrayList<>(phFromContact);
                    setLayoutForPickContact();

            }
        }.execute();



    }


    BottomSheetDialog pickContactDialog;
    RecyclerView pickContactRecycle;
    boolean isSearching=false;
    @SuppressLint("StaticFieldLeak")
    private  void setLayoutForPickContact()
    {
        alreadyAllSelected=false;

        pickContactDialog=new BottomSheetDialog(getContext());

        View pV=getActivity().getLayoutInflater().inflate(R.layout.pick_contact_dialog_layout,null);

        Button pickedDone=pV.findViewById(R.id.pickedDone);
        ImageButton refresh=pV.findViewById(R.id.refresh);
        ImageButton checkAll=pV.findViewById(R.id.selectAll);
        TextInputEditText autoReplyTextInputEditText=pV.findViewById(R.id.autoReplyTextInputEditText);

         pickContactRecycle=pV.findViewById(R.id.pickContactRecycle);



        autoReplyTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                isSearching=true;
                filterPickedContact(editable.toString());
            }
        });

        refresh.setOnClickListener(view1 -> {
            getContact();
            pickContactDialog.dismiss();
        });

        pickedDone.setOnClickListener(view1 -> {

//            isSearching=false;
//            selectedCheckBoxes.clear();
//            autoReplyTextInputEditText.setText("");



            new AsyncTask<String, String, String>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pd.show();
                    pickContactDialog.dismiss();
                }

                @Override
                protected String doInBackground(String... strings) {

                    ArrayList<String> n=new ArrayList<>();

                    String query="CREATE TABLE IF NOT EXISTS "+getString(R.string.contactList)+"(_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',Number text DEFAULT 'Not set',ContactID text DEFAULT 'Not set') ";
                    database db=new database(getContext(),getString(R.string.contactList),query,1);

                    Cursor c=db.getinfo(1);

                    while (c.moveToNext()) // getting saved contact
                    {
                        n.add(c.getString(1));



                    }

                    i("pickedDone  pickedIndex size  "+ selectedCheckBoxes.size());
                    if (alreadyAllSelected) //means user selected all
                    {
                        for (int i=0;nameFromContact.size()>i;i++)
                        {
                            String name=nameFromContact.get(i);
                            String num=phFromContact.get(i);
                            if (!n.contains(name) && !unselectedItems.contains(name))
                            {
                                ContentValues values = new ContentValues();

                                values.put("ContactName", name);
                                values.put("Number", num);


                                db.getReadableDatabase().insert(getString(R.string.contactList), null, values);
                            }
                        }

                    }else
                    {
                        for (int i=0;nameFromContact.size()>i;i++)
                        {
                            String name=nameFromContact.get(i);
                            String num=phFromContact.get(i);


                            if (!n.contains(name) && selectedItems.contains(name)) // todo view list not showing properly
                            {
                                ContentValues values = new ContentValues();

                                values.put("ContactName", name);
                                values.put("Number", num);


                                db.getReadableDatabase().insert(getString(R.string.contactList), null, values);
                            }
                        }
                    }


                    db.close();
                    c.close();


                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    pd.dismiss();
                    Toast.makeText(getContext(), "List Updated", Toast.LENGTH_SHORT).show();



                }
            }.execute();



            for (CheckBox cb:selectedCheckBoxes)
            {


//                if (cb.isChecked())
//                {
//                    int i=selectedCheckBoxes.indexOf(cb);
//                    String
//
//                    i("pickedDone  name  "+ name);
//                    if (!n.contains(name))
//                    {
//                        ContentValues values = new ContentValues();
//
//                        values.put("ContactName", name);
//                        values.put("Number", num);
//
//
//                        db.getReadableDatabase().insert(getString(R.string.contactList), null, values);
//                    }
//                }




            }




        });

        allChecked=false; // makeing it false. Unless all will be selected second time
        contactPickerAd(); // refreshing adepter

        pickContactRecycle.setAdapter(contactPickerAdepter);

        checkAll.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                selectAllContact();

//                NotifyUser notifyUser=new NotifyUser(getContext());
//                notifyUser.UpdateUiOnNew(getString(R.string.selectAllPickedContactBroadCast));
            }
        });

        autoReplyTextInputEditText.setHint("Search among "+nameFromContact.size()+" contact(s)");

        pickContactDialog.setContentView(pV);

        pickContactDialog.show();





    }


    boolean alreadyAllSelected=false;
    void selectAllContact()
    {
        for (CheckBox checkBox:selectedCheckBoxes)
        {
            // when already pressed selectall
            checkBox.setChecked(!alreadyAllSelected);
        }
        alreadyAllSelected=!alreadyAllSelected;

        selectedItems=new ArrayList<>(); // just clearing user manual selection
        unselectedItems=new ArrayList<>(); // just clearing user manual unselection
    }

    ArrayList<CheckBox> selectedCheckBoxes=new ArrayList<>();

    RecyclerView.Adapter contactPickerAdepter;
    boolean allChecked=false;
    ArrayList<String> unselectedItems;
    ArrayList<String> selectedItems;
    private void contactPickerAd() //setting adepter for picked contact recycler
    {

        selectedCheckBoxes=new ArrayList<>();
        selectedItems=new ArrayList<>();// user manual selection
        unselectedItems=new ArrayList<>();// user manual unselection
        isSearching=false;

        contactPickerAdepter=new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_from_phone_item,null)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
                int Op=h.getBindingAdapterPosition(); // Original position of the view


                TextView contactName=h.itemView.findViewById(R.id.contactName);
                TextView number=h.itemView.findViewById(R.id.contactNumber);
                CardView contactListCard=h.itemView.findViewById(R.id.contactListCard);


                number.setText(filteredPickedNumList.get(Op));
                TextView textIcon=h.itemView.findViewById(R.id.textIcon);
                CheckBox selectCheckBox=h.itemView.findViewById(R.id.select);

                contactName.setText(filteredPickedNameList.get(Op));


                textIcon.setText(""+contactName.getText().toString().charAt(0));


                selectCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
                    int i=nameFromContact.indexOf(filteredPickedNameList.get(Op));// finding in main list

                    if (b)
                    {
                        contactListCard.setBackgroundColor(getContext().getResources().getColor(R.color.VA_on));
                        if (!selectedItems.contains(filteredPickedNameList.get(Op)))
                        {
                            selectedItems.add(filteredPickedNameList.get(Op));
                            i("selectedItems "+selectedItems.size());
                        }
                        unselectedItems.remove(filteredPickedNameList.get(Op));

                    }
                    else
                    {

                        contactListCard.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                        selectedItems.remove(filteredPickedNameList.get(Op));


                        if (!unselectedItems.contains(filteredPickedNameList.get(Op)))
                        {
                            unselectedItems.add(filteredPickedNameList.get(Op));
                            i("selectedItems "+unselectedItems.size());
                        }


                    }


                });

                contactListCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectCheckBox.setChecked(!selectCheckBox.isChecked());
                    }
                });





                if (!isSearching) // when search names, selection will not be refreshed
                {
                    selectedCheckBoxes.add(selectCheckBox);

                   // selectCheckBox.setChecked(allChecked);
                    i("selectedCheckBoxes "+selectedCheckBoxes.size());
                }

                if (selectedItems.contains(filteredPickedNameList.get(Op)))
                {
                    selectCheckBox.setChecked(true);
                }





            }

            @Override
            public int getItemCount() {
                return filteredPickedNameList.size();
            }
        };

    }

    ArrayList<String> filteredPickedNameList=new ArrayList<>();
    ArrayList<String> filteredPickedNumList=new ArrayList<>();


    private void filterPickedContact(String text)
    {



        filteredPickedNameList= new ArrayList<>();
        filteredPickedNumList= new ArrayList<>();
//        Log.i("autoReply","filtered size "+filteredContactList.size());


        for (int i = 0; i < nameFromContact.size(); i++) {
            if (nameFromContact.get(i).toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))
            {
                filteredPickedNameList.add(nameFromContact.get(i));
                filteredPickedNumList.add(phFromContact.get(i));


            }
        }

       // pickContactRecycle.removeAllViews();
        contactPickerAdepter.notifyDataSetChanged();

    }



    ImageButton pickGroupInlist;
    String query4;
    BottomSheetDialog addGroupToListDialog;
    TextInputEditText groupName;

    View addNewGroupName()
    {

        View v=View.inflate(getContext(),R.layout.add_group_in_list,null);
        groupName=v.findViewById(R.id.groupName);
        pickGroupInlist=v.findViewById(R.id.contactBtn);

        pickGroupInlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPickingForSpecific=false;
                spEdit.putBoolean("isPickingContact",true).apply();
                showWhatsappPickContact("Group Name");
            }
        });

        v.findViewById(R.id.addGroupSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gName=groupName.getText().toString();
                if(!gName.isEmpty())
                {
                     database db=new database(getContext(),getString(R.string.groupList),query4,1);

                    ContentValues values=new ContentValues();
                    values.put("GroupName",gName);
                    db.getReadableDatabase().insert(getString(R.string.groupList),null,values);
                    groupName.setText("");
                    Toast.makeText(getContext(), "New Group Name add", Toast.LENGTH_SHORT).show();
                    addGroupToListDialog.dismiss();
                }
            }
        });


        return v;
    }

    public void showWhatsappPickContact(String nameType) {



        if (isAccessibilityServiceEnabled(getContext(), whatsappAccessibility.class))
        {

            spEdit.putString("pickedChatName","").putBoolean("gettingChatName",true).
                    putBoolean("isPickingContact",true).apply();
            Intent i=new Intent(Intent.ACTION_MAIN);


            if (isInstalled(getContext(),"com.whatsapp.w4b") && isInstalled(getContext(),"com.whatsapp") )
            {

               new  AlertDialog.Builder(getContext()).setMessage("Choose Contact from").setPositiveButton("WhatsApp", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int index) {
                       i.setComponent(new ComponentName("com.whatsapp","com.whatsapp.HomeActivity"));
                       startActivity(i);
                   }
               }).setNegativeButton("WhatsApp Business", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int index) {
                       i.setComponent(new ComponentName("com.whatsapp.w4b","com.whatsapp.HomeActivity")); //#todo set proper whatsapp business home class name
                       startActivity(i);
                   }
               }).show();


            }
            else if(isInstalled(getContext(),"com.whatsapp.w4b"))
            {

               i.setComponent(new ComponentName("com.whatsapp.w4b","com.whatsapp.w4b.HomeActivity"));
               startActivity(i);

            }else if (isInstalled(getContext(),"com.whatsapp"))
            {
                i.setComponent(new ComponentName("com.whatsapp","com.whatsapp.HomeActivity"));
                startActivity(i);

            }else {

                Toast.makeText(getContext(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show();

            }




        }
        else {
            new AlertDialog.Builder(getContext()).setTitle("Permission Required")
                    .setMessage("In order to pick "+nameType+" from WHATSAPP we need ACCESSIBILITY SERVICE PERMISSION")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent,PEMISSION_OF_ACCESSEBLITY);
                        }
                    }).setNegativeButton("TYPE MANUALLY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
        }



    }

    private void createNewAutoReply(String stringSenderName,
                                    String replyString, String imcomingString,
                                    boolean customised, boolean contain, boolean exact) {



        String matchType="exact";
        if (customised)
        {
            if (contain)
            {
                matchType="contain";

            }else {

                matchType="exact";
            }
        }else {
            imcomingString="Any incoming message";
        }

        if (isEditingSpecificReply)
        {
            i("isEditingSpecificReply");

            String s="UPDATE "+getString(R.string.specificReply)+" SET SenderName='"+stringSenderName
                    +"'," +
                    "Message='"+imcomingString+"',Replay='"+replyString
                    +"',MatchType='"+matchType+"',Customised='"+customised+"',isGroup='"+listType+"' WHERE _id="+currentEditingRowID;

            specificDB.getWritableDatabase().execSQL(s);

            Toast.makeText(getContext(), "Changes saved", Toast.LENGTH_LONG).show();

            makeEmpty();
            addNewSpecific.dismiss();
            if (listType==1)
            {
                showSpecificContactBottomDialog();
            }else
            {
                showGroupSpcificBottomDialog();
            }


        }else {
            i("isEditingSpecificReply else");

            SQLiteDatabase sl=specificDB.getReadableDatabase();
            ContentValues contentValues=new ContentValues();

            contentValues.put("SenderName",stringSenderName);
            contentValues.put("Message",imcomingString);
            contentValues.put("Replay",replyString);
            contentValues.put("Customised",""+customised);
            contentValues.put("MatchType",matchType);
            contentValues.put("isGroup",listType);




            long s=sl.insert(getString(R.string.specificReply),null,contentValues);
            if (s != -1)
            {
                Toast.makeText(getContext(), "Added successfully", Toast.LENGTH_LONG).show();


                makeEmpty();
                addNewSpecific.dismiss();
                if (listType==1)
                {
                    showSpecificContactBottomDialog();
                }else
                {
                    showGroupSpcificBottomDialog();
                }
            }
        }

    }

    private void makeEmpty() {

        senderName.setText("");
        messageIncoming.setText("");
        Reply.setText("");
        previewIncomingMsg.setText(getString(R.string.incomingMessagePreview));
        previewAutoMsg.setText(getString(R.string.defaultAutoReplyPreview));
        customisedCheckBox.setChecked(true);

    }

    @SuppressLint("IntentReset")
    public void showSinglePickContact() {

        isPickingForSpecific=true;


        // isPickingContact=true;
        Context mContext=getContext();

        final int REQUEST = 112;

        if (listType==2)
        {

            showWhatsappPickContact("Group Name");
        }else {
            if (Build.VERSION.SDK_INT >= 23) {
                String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS};
                if (!isHasContactPermission( PERMISSIONS)) {
                    Toast.makeText(getContext(), "Please enable Contact permission", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
                } else {


                    try {
                        Uri uri = Uri.parse("content://contacts");
                        Intent intent = new Intent(Intent.ACTION_PICK, uri);
                        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);


                        startActivityForResult(intent,CONTACT_SINGLE_PICKER_REQUEST);
                    }catch (Exception e)
                    {
                        if(e.getMessage().contains("No Activity found"))
                        {
                            isPickingForSpecific=true;
                            showWhatsappPickContact("Contact");
                        }
                    }





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


    }


    ArrayList<ContactInfo> filteredContactList;
    ArrayList<specificContactInfo> filteredSpecificList;
    ArrayList<specificContactInfo> filteredSpecificGroupList;

    ContactInfo contactInfoFiltered;







    @SuppressLint("NotifyDataSetChanged")
    private void filterContact(String text)
    {

        filteredContactList= new ArrayList<>();
        filteredSpecificList= new ArrayList<>();
        filteredSpecificGroupList= new ArrayList<>();

        Log.i("autoReply","filtered size "+filteredContactList.size());

        if (listType==0 || listType==4) // indicating it has been called from View LIst button
        {
            for (int i=0;i<contactInfos.size();i++)
            {
                if (contactInfos.get(i).contactNames.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))
                {
                    contactInfoFiltered=new ContactInfo(contactInfos.get(i).contactNames,
                            contactInfos.get(i).contactID,contactInfos.get(i).number,contactInfos.get(i).id);
                    filteredContactList.add(contactInfoFiltered);

                }
            }

            contactListRecycleView.removeAllViews();
            mAdepter.notifyDataSetChanged();
        }else if (listType==1)
        {
            for (int i=0;i<specificContactInfos.size();i++)
            {

                if (specificContactInfos.get(i).SenderName.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) &&
                        specificContactInfos.get(i).isGroup!=2 )
                {
                    specificContactInfo=new specificContactInfo(specificContactInfos.get(i).SenderName,
                            specificContactInfos.get(i).Message,specificContactInfos.get(i).Replay,specificContactInfos.get(i).Customised
                    ,specificContactInfos.get(i).isGroup,specificContactInfos.get(i).id,specificContactInfos.get(i).matchType);
                    filteredSpecificList.add(specificContactInfo);

                }
            }
            specificRecycle.removeAllViews();
            specificContactAdapter.notifyDataSetChanged();
        }else {

            for (int i=0;i<specificGroupList.size();i++)
            {
                if (specificGroupList.get(i).SenderName.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))
                {
                    specificContactInfo=new specificContactInfo(specificGroupList.get(i).SenderName,
                            specificGroupList.get(i).Message,specificGroupList.get(i).Replay,specificGroupList.get(i).Customised
                            ,specificGroupList.get(i).isGroup,specificGroupList.get(i).id,specificGroupList.get(i).matchType);
                    filteredSpecificGroupList.add(specificContactInfo);

                }
            }
            specificRecycle.removeAllViews();
            specificContactAdapter.notifyDataSetChanged();
        }



    }




    //#todo add My group list
    public static final int REQUEST_CODE_PICK_CONTACT = 1;
    public static final int  MAX_PICK_CONTACT= 10;


    private void showContactPicker() {

        try {


            final String[] PERMISSIONS = {
                    Manifest.permission.READ_CONTACTS
            };
            if (isHasContactPermission(PERMISSIONS))
            {


                Log.i("autoreply","has contact permission");



                pd=new ProgressDialog(getContext());
                pd.setMessage("Getting Contacts..");



                getContact();



            }else {
                Toast.makeText(getContext(), "Please enable Contact permission", Toast.LENGTH_LONG).show();
                requestPermissions(PERMISSIONS, 2);
            }


//
        } catch (Exception e)
        {
            Log.e("autoreply",e.getMessage());
            Toast.makeText(getContext(), ""+e, Toast.LENGTH_LONG).show();
        }
    }

    boolean isHasContactPermission(String[] PERMISSIONS)
    {
        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getContext(), permissions) != PackageManager.PERMISSION_GRANTED) {

                return false;
            }
        }
        return true;
    }



//    ActivityResultLauncher<Intent> activityResultContract=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//        if (result.getResultCode() == RESULT_OK &&
//                result.getData() != null && result.getData().hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {
//
//            // we got a result from the contact picker
//
//            // process contacts
//
//            List<Contact> contacts = (List<Contact>) result.getData().getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
//
//                int i=0;
//                for (Contact contact1:contacts)
//                {
//
//                    if (contactInfos.size()>i )
//                    {
//                        if ( !contactInfos.get(i).contactNames.equals(contact1.getDisplayName()))
//
//                        {
//                            Log.i("autoReply","contact already not exist");
//
//
//
//                            ContentValues values = new ContentValues();
//
//                            values.put("ContactName", contact1.getDisplayName());
//                            values.put("Number", contact1.getPhone(0));
//
//
//                            db.getReadableDatabase().insert(getString(R.string.contactList), null, values);
//                            i++;
//                        }
//                    }else
//                    {
//                        ContentValues values = new ContentValues();
//
//                        values.put("ContactName", contact1.getDisplayName());
//                        values.put("Number", contact1.getPhone(0));
//
//
//                        db.getReadableDatabase().insert(getString(R.string.contactList), null, values);
//                        i++;
//                    }
//
//
//
////                    if (!contactNames.contains(contact.getDisplayName())) {
////                    ContentValues values = new ContentValues();
////
////                    values.put("ContactName", contact.getDisplayName());
////                    values.put("Number", contact.getPhone(0));
////
////
////                    db.getReadableDatabase().insert(getString(R.string.contactList), null, values);
////                }
//
//
//                try {
//                    Thread.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            // process groups
//            List<Group> groups = (List<Group>) result.getData().getSerializableExtra(ContactPickerActivity.RESULT_GROUP_DATA);
//            for (Group group : groups) {
//                // process the groups...
//            }
//
//    }});

    @Override
    public void onResume() {
        super.onResume();
        if (sp.getBoolean("isPickingContact",false))
        {
            if (isPickingForSpecific) // when picking for specific reply
            {
                //Toast.makeText(getContext(), "isPickingContact", Toast.LENGTH_SHORT).show();
                senderName.setText(sp.getString("pickedChatName","not set"));
                spEdit.putBoolean("isPickingContact",false).apply();
            }
            else {
                groupName.setText(sp.getString("pickedChatName","not set"));
                spEdit.putBoolean("isPickingContact",false).apply();
            }

        }
       // Toast.makeText(getContext(), "resume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //todo fix contact picking crash
//            if(requestCode == CONTACT_PICK_REQUEST)
//            {
//                Log.i("autoReply","results "+CONTACT_PICK_REQUEST);
//                if(data != null) {
//                    List<ContactResult> results = MultiContactPicker.obtainResult(data);
//
//                    Log.i("autoReply","results "+results.size());
//                    int i=0;
//                    for (ContactResult result:results)
//                    {
//                        if (!contactInfos.get(i).contactNames.equals(result.getDisplayName()))
//
//                        {
//                            Log.i("autoReply","contact already not exist");
//
//
//                            List<PhoneNumber> p=result.getPhoneNumbers();
//                            ContentValues values = new ContentValues();
//
//                            values.put("ContactName", result.getDisplayName());
//                            values.put("Number", p.get(0).getNumber());
//                            values.put("ContactID", result.getContactID());
//
//                            db.getReadableDatabase().insert(getString(R.string.contactList), null, values);
//                            i++;
//                        }
//
//
//                    }
//
//                } else if(resultCode == RESULT_CANCELED){
//                    System.out.println("User closed the picker without selecting items.");
//                }
//            }

            if(requestCode == CONTACT_SINGLE_PICKER_REQUEST && resultCode==RESULT_OK){



                Uri uri = data.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getContext().getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);









                //ContactName.setText(name);
                senderName.setText(name);





                cursor.close();

            }


        }catch (Exception e)
        {
            Log.e("Autoreply",e.getMessage());
        }



    }

    public class ContactInfo {

        String contactNames;
        String contactID;
        String number;
        int id;

        public ContactInfo() {

        }

        public ContactInfo(String contactNames, String contactID, String number, int id) {
            this.contactNames = contactNames;
            this.contactID = contactID;
            this.number = number;
            this.id = id;
        }

        public ContactInfo(String contactNames, int id) {
            this.contactNames = contactNames;
            this.id = id;
        }

        public String getContactNames() {
            return contactNames;
        }

        public void setContactNames(String contactNames) {
            this.contactNames = contactNames;
        }

        public String getContactID() {
            return contactID;
        }

        public void setContactID(String contactID) {
            this.contactID = contactID;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public class specificContactInfo {

        String SenderName;
        String Message;
        String Replay;
        String Customised;
        int isGroup;
        int id;
        String matchType;



        public specificContactInfo() {

        }

        public specificContactInfo(String senderName,
                                   String message, String replay,
                                   String customised, int isGroup,
                                   int id, String matchType) {
            SenderName = senderName;
            Message = message;
            Replay = replay;
            Customised = customised;
            this.isGroup = isGroup;
            this.id = id;
            this.matchType = matchType;
        }
    }

    void i(String s){
        Log.i("autoReplyCustom",s);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}