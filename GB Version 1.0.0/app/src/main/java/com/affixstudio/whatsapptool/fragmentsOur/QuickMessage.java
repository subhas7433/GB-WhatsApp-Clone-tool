package com.affixstudio.whatsapptool.fragmentsOur;

import android.app.Dialog;
import android.app.MediaRouteButton;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.modelOur.setOnRecycleClick;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;


public class QuickMessage extends Fragment implements setOnRecycleClick {


    String selection;
    String[] all;
    database db;
    AutoCompleteTextView language;
    RecyclerView recyclerView;
    View v;
     String tab="category";
     String langSelected="Mixed";
    private TextView noSavedmessage;
    RadioButton saved;
    RadioButton category;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         v=inflater.inflate(R.layout.fragment_quick_message, container, false);

        Runnable task=new Runnable() {
            @Override
            public void run() {


         recyclerView=v.findViewById(R.id.recyclerView);
        Adapter<Adepter.viewHolder> a=new Adepter(getContext(),getResources().getStringArray(R.array.hindi),v.findViewById(R.id.quickMessageLayout),false,getActivity());

        recyclerView.setAdapter(a);


        String query="CREATE TABLE IF NOT EXISTS quickMessage(_id INTEGER PRIMARY KEY autoincrement,Title text , Message text,Language text)";
        db=new database(getContext(),getString(R.string.saved_quick_message),query,2);

        String [] languageList=getActivity().getResources().getStringArray(R.array.QuickMessageLanguage);

        View dialogLayout=getLayoutInflater().inflate(R.layout.add_new_qk_msg,null); //for language
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getContext(),  android.R.layout.simple_list_item_1
                ,languageList);//for language

        TextInputEditText addMsgTitle=dialogLayout.findViewById(R.id.AddMsgTitle),
                        addMsgMessage=dialogLayout.findViewById(R.id.add_messageTB);
        ImageButton closeDialog=dialogLayout.findViewById(R.id.closeADDMessage);
        Button saveMsg=dialogLayout.findViewById(R.id.saveMsg);
        AutoCompleteTextView actv=dialogLayout.findViewById(R.id.language); // select language when saving new message



         saved= v.findViewById(R.id.RBSaved);
         category= v.findViewById(R.id.RBCatagory);



        actv.setAdapter(arrayAdapter);

        LinearLayout layout=v.findViewById(R.id.quickMessageLayout);




        language = v.findViewById(R.id.language);
         noSavedmessage=v.findViewById(R.id.noSavedMessage);






        //spinner.setAdapter(arrayAdapter);
        language.setAdapter(arrayAdapter);

        String [] hindi=getResources().getStringArray(R.array.hindi);
        String [] english=getResources().getStringArray(R.array.english);
        String [] bengali=getResources().getStringArray(R.array.bengali);
         all=getResources().getStringArray(R.array.QuickMessage);



        Adapter<Adepter.viewHolder> a1=new Adepter(getContext(),all,v.findViewById(R.id.quickMessageLayout),false,getActivity());
        recyclerView.setAdapter(a1);

        language.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 // Toast.makeText(getContext(), ""+languageList[i], Toast.LENGTH_SHORT).show();


                langSelected=languageList[i];
                if (tab.equals("saved")) // indicating which tab is opened
                {

                    getSaveMessage();
                    if (db.getQKmessage().size()==0)
                    {
                        v.findViewById(R.id.scrollView).setVisibility(View.GONE);
                        noSavedmessage.setVisibility(View.VISIBLE);
                        v.findViewById(R.id.noSavedMsgLayout).setVisibility(View.VISIBLE);
                        if (langSelected.equals("Mixed"))
                        {
                            noSavedmessage.setText("No saved Messages");
                        }else {
                            noSavedmessage.setText("No saved Messages in "+langSelected);
                        }
                    }
                    else

                    {
                        v.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                        noSavedmessage.setVisibility(View.GONE);
                        v.findViewById(R.id.noSavedMsgLayout).setVisibility(View.GONE);
                        Adapter<Adepter.viewHolder> a=new Adepter(getContext(), getSaveMessage(),v.findViewById(R.id.quickMessageLayout),
                                true,db.getQKid(),db.getQKtitle(),db.getQKlanguage(), QuickMessage.this,getActivity());
                        recyclerView.setAdapter(a);
                    }
                   /* if (languageList[i].equals("Mixed"))
                    {


                        Adapter<Adepter.viewHolder> a=new Adepter(getContext(), getSaveMessage(),v.findViewById(R.id.quickMessageLayout),
                                true,db.getQKid(),db.getQKtitle(),db.getQKlanguage(), QuickMessage.this);
                        recyclerView.setAdapter(a);

                    }else if (languageList[i].equals("English"))
                    {
                        Adapter<Adepter.viewHolder> a=new Adepter(getContext(), getSaveMessage(),v.findViewById(R.id.quickMessageLayout),
                                true,db.getQKid(),db.getQKtitle(),db.getQKlanguage(), QuickMessage.this);
                        recyclerView.setAdapter(a);
                    }else if (languageList[i].equals("हिन्दी"))
                    {
                        Adapter<Adepter.viewHolder> a=new Adepter(getContext(), getSaveMessage(),v.findViewById(R.id.quickMessageLayout),
                                true,db.getQKid(),db.getQKtitle(),db.getQKlanguage(), QuickMessage.this);
                        recyclerView.setAdapter(a);
                    }else if (languageList[i].equals("বাংলা"))
                    {
                        Adapter<Adepter.viewHolder> a=new Adepter(getContext(),bengali,v.findViewById(R.id.quickMessageLayout),false);
                        recyclerView.setAdapter(a);
                    }*/
                }else {
                    if (languageList[i].equals("Mixed"))
                    {

                        Adapter<Adepter.viewHolder> a=new Adepter(getContext(),all,v.findViewById(R.id.quickMessageLayout),false,getActivity());
                        recyclerView.setAdapter(a);
                    }else if (languageList[i].equals("English"))
                    {
                        Adapter<Adepter.viewHolder> a=new Adepter(getContext(),english,v.findViewById(R.id.quickMessageLayout),false,getActivity());
                        recyclerView.setAdapter(a);
                    }else if (languageList[i].equals("हिन्दी"))
                    {
                        Adapter<Adepter.viewHolder> a=new Adepter(getContext(),hindi,v.findViewById(R.id.quickMessageLayout),false,getActivity());
                        recyclerView.setAdapter(a);
                    }else if (languageList[i].equals("বাংলা"))
                    {
                        Adapter<Adepter.viewHolder> a=new Adepter(getContext(),bengali,v.findViewById(R.id.quickMessageLayout),false,getActivity());
                        recyclerView.setAdapter(a);
                    }
                }
                

            }
        });
       //language.setText("All");



        Dialog addMsg=new Dialog(getContext());

        saveMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                String message=addMsgMessage.getText().toString();
                String title=addMsgTitle.getText().toString();
                String Lang=actv.getText().toString();
                if (Lang.isEmpty())
                {
                    Lang="Mixed";
                }

                if (!message.isEmpty())
                {
                    if (db.insertData2(title,message,getString(R.string.saved_quick_message),Lang))
                    {
                        addMsg.dismiss();
                        saved.setChecked(false);
                        saved.setChecked(true);
                        Snackbar.make(layout,"Saved", BaseTransientBottomBar.LENGTH_SHORT)
                                .setTextColor(ContextCompat.getColor(getContext(),R.color.white))
                                .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.snackbarBg)).show();

                        Log.i("quickMessage","saveMessage size "+getSaveMessage().length);


                        Adapter<Adepter.viewHolder> a=new Adepter(getContext(),getSaveMessage(),v.findViewById(R.id.quickMessageLayout),
                                true,db.getQKid(),db.getQKtitle(),db.getQKlanguage(),QuickMessage.this,getActivity());
                        recyclerView.setAdapter(a);
                        addMsgMessage.setText("");
                    }
                    else
                    {
                        Toast.makeText(getContext(), "database failed", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    Snackbar.make(layout,"Enter Message.", BaseTransientBottomBar.LENGTH_SHORT)
                            .setTextColor(ContextCompat.getColor(getContext(),R.color.white))
                            .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.red)).show();
                }
                }catch (Exception e)
                {
                    Log.e("saveMessageButton",""+e);
                }

            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMsg.dismiss();
            }
        });




        addMsg.setContentView(dialogLayout);



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.QuickMessageCategory, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner



        FloatingActionButton addNewMessage=v.findViewById(R.id.add_new_message);

        addNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMsg.show();
            }
        });




        saved.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                try {

                    tab="saved";

                    showSavedMessages();


                }catch (Exception e)
                {
                    Log.e("SaveRadio",""+e);
                }

            }

        });



        category.setChecked(true);

        category.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    Adapter<Adepter.viewHolder> a=new Adepter(getContext(),all,v.findViewById(R.id.quickMessageLayout),false,getActivity());
                    if (langSelected.equals("Mixed"))
                    {
                        a=new Adepter(getContext(),all,v.findViewById(R.id.quickMessageLayout),false,getActivity());
                    }else if (langSelected.equals("English"))
                    {
                        a=new Adepter(getContext(),english,v.findViewById(R.id.quickMessageLayout),false,getActivity());
                    }else if (langSelected.equals("हिन्दी"))
                    {
                        a=new Adepter(getContext(),hindi,v.findViewById(R.id.quickMessageLayout),false,getActivity());
                    }else if (langSelected.equals("বাংলা"))
                    {
                        a=new Adepter(getContext(),bengali,v.findViewById(R.id.quickMessageLayout),false,getActivity());
                    }

                    noSavedmessage.setVisibility(View.GONE);
                    v.findViewById(R.id.noSavedMsgLayout).setVisibility(View.GONE);
                    tab="quickmessage";
                    recyclerView.setVisibility(View.VISIBLE);
                    v.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                    //spinner.setVisibility(View.VISIBLE);

                    recyclerView.setAdapter(a);

                }

            }
        });

            }
        };
        v.postDelayed(task,5);
        return v;
    }

    private void showSavedMessages() {
        getSaveMessage();

        //was setting language when saved tab is pressed
        Log.i("quickmessage","savemessage size "+db.getQKmessage().size()+" "+db.getQKlanguage().size()+db.getQKid().size()+" "+db.getQKtitle().size());

        if (db.getQKmessage().size()==0){
            v.findViewById(R.id.scrollView).setVisibility(View.GONE);
            v.findViewById(R.id.noSavedMsgLayout).setVisibility(View.VISIBLE);
            noSavedmessage.setVisibility(View.VISIBLE);
            if (langSelected.equals("Mixed"))
            {
                noSavedmessage.setText("No saved Messages");
            }else {
                noSavedmessage.setText("No saved Messages in "+langSelected);
            }
        }else {
            v.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
            noSavedmessage.setVisibility(View.GONE);
            v.findViewById(R.id.noSavedMsgLayout).setVisibility(View.GONE);
            Adapter<Adepter.viewHolder> a=new Adepter(getContext(), getSaveMessage(),v.findViewById(R.id.quickMessageLayout),
                    true,db.getQKid(),db.getQKtitle(),db.getQKlanguage(), QuickMessage.this,getActivity());
            recyclerView.setAdapter(a);
        }
    }


    private String[] getSaveMessage() {
        db.setdata(langSelected);
        String[] array=new String[db.getQKmessage().size()];

        Log.i("quickmessage","savemessage size "+db.getQKmessage().size());
        // Toast.makeText(getContext(), ""+db.getQKmessage().size(), Toast.LENGTH_SHORT).show();
        for (int i=0;i<db.getQKmessage().size();i++)
        {

            array[i]=db.getQKmessage().get(i);
        }
        return  array;
    }


    @Override
    public void onItemEdit(int position, String title) {

        saved.setChecked(false);
        saved.setChecked(true);
        tab="saved";
//        Adapter<Adepter.viewHolder> a=new Adepter(getContext(),getSaveMessage(),v.findViewById(R.id.quickMessageLayout),
//                true,db.getQKid(),db.getQKtitle(),db.getQKlanguage(),QuickMessage.this);
//        recyclerView.setAdapter(a);
    }
}

