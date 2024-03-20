package com.affixstudio.whatsapptool.fragmentsOur;

import static com.affixstudio.whatsapptool.fragmentsOur.DecorationText.Adepter.isInstalled;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.schedule_sms;
import com.affixstudio.whatsapptool.ads.AppLovinNative;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.modelOur.setOnRecycleClick;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Adepter extends RecyclerView.Adapter<Adepter.viewHolder> {

    Context context;
    String[] arrayList;
    LinearLayout layout;
    Boolean IsSaved; // indicating recycle view is showing saved quick message
    ArrayList<Integer> id; //for deleting the saved qk message from database
    ArrayList<String> title;
    ArrayList<String> language;
    com.affixstudio.whatsapptool.modelOur.setOnRecycleClick setOnRecycleClick;
    ArrayList<String> arrayList1;
    Activity a;
    int viewTypeGopbal;

    public Adepter(Context context, String[] arrayList, LinearLayout layout, Boolean IsSaved,Activity a) {
        this.context = context;
        this.arrayList = arrayList;
        this.layout = layout;
        this.IsSaved = IsSaved;
        this.a = a;
         arrayList1=new ArrayList<>();
        Collections.addAll(arrayList1, arrayList);

    }

    public Adepter(Context context, String[] arrayList, LinearLayout layout,
                   Boolean IsSaved, ArrayList<Integer> id, ArrayList<String> title,
                   ArrayList<String> language, setOnRecycleClick setOnRecycleClick, Activity a) {
        this.context = context;
        this.arrayList = arrayList;
        this.layout = layout;
        this.IsSaved = IsSaved;
        this.id = id;
        this.title = title;
        this.language = language;
        this.a = a;
        this.setOnRecycleClick = setOnRecycleClick;
        arrayList1=new ArrayList<>();
        Collections.addAll(arrayList1, arrayList);

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {



        viewTypeGopbal=1;
//        if (viewType==0) // ads
//        {
//            viewTypeGopbal=0;
//            return  new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_frame, parent, false));
//        }



        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.decoration_text_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int p)
    {

        try {

            if (getItemViewType(p)==0)
            {

                AppLovinNative lovinNative=new AppLovinNative(R.layout.native_ad_applovin_small,a);
                lovinNative.small(holder.adFrame);




            }



                int position = p;
                if (IsSaved) {
                    position =  arrayList1.size() - 1 - p;
                }


                String message =  arrayList1.get(position);
                holder.QuickText.setText(message);

                //        animation
                holder.QkRecycleLayout.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycle));


                if (IsSaved) {
                    holder.more.setVisibility(View.VISIBLE);
                } else {
                    holder.more.setVisibility(View.GONE);
                }


                int finalPosition = position;
                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        try {


                            //creating a popup menu
                            PopupMenu popup = new PopupMenu(context, holder.more);
                            //inflating menu from xml resource
                            popup.inflate(R.menu.more_qk_message);
                            //adding click listener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.edit:

                                            try {


                                                View v = LayoutInflater.from(context).inflate(R.layout.add_new_qk_msg, null, false);

                                                TextInputEditText titleTB = v.findViewById(R.id.AddMsgTitle),
                                                        message = v.findViewById(R.id.add_messageTB);
                                                AutoCompleteTextView actv = v.findViewById(R.id.language);

                                                v.findViewById(R.id.titleTop).setVisibility(View.INVISIBLE);
                                                if (title.get(finalPosition) != null) {
                                                    titleTB.setText(title.get(finalPosition));
                                                }
                                                actv.setText(language.get(finalPosition));

                                                message.setText( arrayList1.get(finalPosition)); // arraylist is message

                                                Dialog dialog = new Dialog(context);
                                                dialog.setContentView(v);


                                                v.findViewById(R.id.saveMsg).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String lang = actv.getText().toString();
                                                        if (lang.isEmpty()) {
                                                            lang = "Mixed";
                                                        }


                                                        Log.i("qkEdit", "lang is" + lang);
                                                        String msg = message.getText().toString();
                                                        if (!msg.equals( arrayList1.get(finalPosition)) || !lang.equals(language.get(finalPosition))) {
                                                            String query = "CREATE TABLE IF NOT EXISTS quickMessage(_id INTEGER PRIMARY KEY autoincrement,Title text , Message text,Language text)";
                                                            database db = new database(context, context.getString(R.string.saved_quick_message), query, 2);
                                                            String sql = "UPDATE quickMessage SET Message='" + msg +/*",Title="+titleTB.getText().toString()+"*/"',Language='" + lang + "' WHERE _id=" + id.get(finalPosition);

                                                            db.getWritableDatabase().execSQL(sql);

                                                            holder.QuickText.setText(msg);
                                                            dialog.dismiss();
                                                            try {
                                                                Snackbar.make(layout, "Saved", BaseTransientBottomBar.LENGTH_SHORT)
                                                                        .setTextColor(ContextCompat.getColor(context, R.color.white))
                                                                        .setBackgroundTint(ContextCompat.getColor(context, R.color.txt_repeat_cl)).show();
                                                            } catch (Exception e) {
                                                                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                                                            }


                                                            setOnRecycleClick.onItemEdit(finalPosition, "");


                                                        } else {
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                });
                                                v.findViewById(R.id.closeADDMessage).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                actv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                    @Override
                                                    public void onFocusChange(View view, boolean b) {

                                                        String[] languageList = context.getResources().getStringArray(R.array.QuickMessageLanguage);
                                                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1
                                                                , languageList);//for language
                                                        actv.setAdapter(arrayAdapter);
                                                    }
                                                });


                                                dialog.show();


                                            } catch (Exception e) {
                                                Log.e("qkEdit", "" + e);
                                            }


                                            //handle menu1 click
                                            return true;
                                        case R.id.delete:
                                            //handle menu2 click

                                            new AlertDialog.Builder(context).setMessage("Do you really want to delete?")
                                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            arrayList1.remove(arrayList1.get(finalPosition));
                                                            synchronized (arrayList1)
                                                            {
                                                                arrayList1.notifyAll();
                                                            }
                                                            deleteQuickMessage(finalPosition, holder.QkRecycleLayout);
                                                        }
                                                    })
                                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }).show();

                                            return true;

                                        default:
                                            return false;
                                    }


                                }

                            });
                            //displaying the popup
                            popup.show();
                        } catch (Exception e) {
                            Log.e("QKmoreClick", "" + e);
                        }
                    }
                });
                SharedPreferences sp = context.getSharedPreferences("affix", Context.MODE_PRIVATE);
                if (sp.getInt(context.getString(R.string.isScheduleONTag), 1) == 0) {
                    holder.schOpen.setVisibility(View.GONE);
                }
                holder.schOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Toast.makeText(context, "Opening Schedule Messages", Toast.LENGTH_LONG).show();


                        Intent i = new Intent(context, schedule_sms.class);

                        i.putExtra("Directmessage", message);

                        i.putExtra("fromChat", 1);
                        context.startActivity(i);

                    }
                });

                holder.Copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                        ClipData clipData = ClipData.newPlainText("Quick Message", message);
                        clipboardManager.setPrimaryClip(clipData);

                        Snackbar.make(layout, "Copied", Snackbar.LENGTH_SHORT)
                                .setTextColor(ContextCompat.getColor(context, R.color.white))
                                .setBackgroundTint(ContextCompat.getColor(context, R.color.snackbarBg)).show();
                    }
                });

                holder.Share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);

                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Share using");
                        intent.putExtra(Intent.EXTRA_TEXT, message);
                        context.startActivity(intent);

                    }
                });
                holder.WA.setOnClickListener(view -> {
                    if (isInstalled(context, "com.whatsapp")) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.setPackage("com.whatsapp");
                        intent.putExtra(Intent.EXTRA_TEXT, message);
                        context.startActivity(intent);
                    } else {
                        Snackbar.make(layout, "Whatsapp is not installed in your phone.", BaseTransientBottomBar.LENGTH_SHORT)
                                .setTextColor(ContextCompat.getColor(context, R.color.white))
                                .setBackgroundTint(ContextCompat.getColor(context, R.color.red)).show();
                    }
                });

                holder.WB.setOnClickListener(view -> {
                    if (isInstalled(context, "com.whatsapp.w4b")) {

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.setPackage("com.whatsapp.w4b");
                        intent.putExtra(Intent.EXTRA_TEXT, message);
                        context.startActivity(intent);

                    } else {
                        Snackbar.make(layout, "Whatsapp business is not installed in your phone.", BaseTransientBottomBar.LENGTH_SHORT)
                                .setTextColor(ContextCompat.getColor(context, R.color.white))
                                .setBackgroundTint(ContextCompat.getColor(context, R.color.red)).show();
                    }

                });




        } catch (Exception e) {
            Log.e("Quickadepter", e.getMessage());
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (Objects.isNull(layout)) // when calling from text fun
        {
            return 1;
        }
        if (/*layout.getId()==R.id.captionLayout
                &&*/ position!=0 && position % 7==0)
        {
            return 0; // make ads
        }
        else
        {
            return 1; // make normal view
        }
    }

    private void deleteQuickMessage(int p, LinearLayout QkRecycleLayout) {

        String query = "CREATE TABLE IF NOT EXISTS quickMessage(_id INTEGER PRIMARY KEY autoincrement,Title text , Message text,Language text)";
        database db = new database(context, context.getString(R.string.saved_quick_message), query, 2);
        String sql = "DELETE FROM quickMessage WHERE _id=" + id.get(p);

        db.getWritableDatabase().execSQL(sql);

        setOnRecycleClick.onItemEdit(0,""); // refresh layout

       // QkRecycleLayout.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return arrayList.length;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView QuickText;
        ImageView Copy,Share, WA,WB, more,schOpen;
        LinearLayout QkRecycleLayout;

        FrameLayout adFrame;


        public viewHolder(@NonNull View itemView) {
            super(itemView);


            QuickText = itemView.findViewById(R.id.decorationTB);
            Copy = itemView.findViewById(R.id.decorationCopy);
            Share = itemView.findViewById(R.id.decorationShare);
            WA = itemView.findViewById(R.id.decorationWA);
            WB = itemView.findViewById(R.id.decorationWB);
            more = itemView.findViewById(R.id.more);
            QkRecycleLayout = itemView.findViewById(R.id.linerView);
            schOpen = itemView.findViewById(R.id.schOpen);
            adFrame=itemView.findViewById(R.id.ad_frame);



        }
    }

    public class adholder extends RecyclerView.ViewHolder{


        FrameLayout adFrame;
        public adholder(@NonNull View itemView) {
            super(itemView);
            adFrame=itemView.findViewById(R.id.ad_frame);
        }
    }
}
