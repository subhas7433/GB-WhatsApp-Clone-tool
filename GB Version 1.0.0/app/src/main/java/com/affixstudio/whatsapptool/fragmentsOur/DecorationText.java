package com.affixstudio.whatsapptool.fragmentsOur;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activity.helper.font;
import com.affixstudio.whatsapptool.activity.helper.strArray;
import com.affixstudio.whatsapptool.activityOur.schedule_sms;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;


public class DecorationText extends Fragment {


    ArrayList<font> fontArrayList;
    EditText decorationET;
    String fontText;
    RecyclerView recyclerView;
    Adepter adepter;
    LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_decoration_text, container, false);

        recyclerView=v.findViewById(R.id.decorationRecycle);
         linearLayout= v.findViewById(R.id.decorationLayout) ;



       // recyclerView.setAdapter(adepter);

         decorationET=v.findViewById(R.id.decorationET);

        fontArrayList=new ArrayList<>();

        decorationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                makeStylish(charSequence);
               // Log.i("info",charSequence.toString());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new Adepter(getContext(),linearLayout,fontArrayList));
                Log.i("CharSequence",charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        v.findViewById(R.id.decorationCross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decorationET.setText("");
            }
        });

        decorationET.setText("Example Text");

        return v;
    }

    private void makeStylish(CharSequence charSequence) {
        try {


        char [] charArray=charSequence.toString().toLowerCase().toCharArray();
        Log.i("charArray info",""+charArray.length);
        String [] str=new String[29];
        for(int i=0;i<29;i++)
        {
            str[i]=applyStylish(charArray,strArray.strings[i]);
        }
        styleTheFont(str);
        }catch (Exception e)
        {
            Log.e("makeStylish",e.toString());
        }
    }

    private void styleTheFont(String[] str) {
       if (!this.fontArrayList.isEmpty())
        {
            this.fontArrayList.clear();
        }


        this.fontText=decorationET.getText().toString().trim();
       try {


        if (!fontText.isEmpty())
        {
            for(int i=0;i<29;i++)
            {
                font font=new font();
                font.fontText=str[i];
                this.fontArrayList.add(font);
            }





        }

       }catch (Exception e)
       {
           Log.e("fontArrayList",e.toString());
       }




    }

    private String applyStylish(char[] charArray, String[] string) {

        StringBuffer stringBuffer=new StringBuffer();
        try {


        for(int i=0;i<charArray.length;i++)
        {
            if(charArray[i]-'a' < 0 ||  charArray[i]-'a'>25)
            {
                stringBuffer.append(charArray[i]);
            }else
            {
                stringBuffer.append(string[charArray[i]- 'a']);
            }
        }
        }catch (Exception e)
        {
            Log.e("applyStylish",e.toString());
        }
        return stringBuffer.toString();

    }



    public static class Adepter extends  RecyclerView.Adapter<Adepter.viewHolder> {

        Context mcontext;
        LinearLayout layout;
        ArrayList <font> dataSet;

        public Adepter(Context context, LinearLayout linearLayout,ArrayList <font> dataSet ) {
            this.mcontext=context;
            this.layout=linearLayout;
            this.dataSet=dataSet;
        }


        @NonNull
        @Override
        public Adepter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.decoration_text_recycle,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull Adepter.viewHolder holder, int position) {
            holder.decorationText.setText(dataSet.get(holder.getAdapterPosition()).fontText);

            //        animation
            holder.linearLayout.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.recycle));

            String text=holder.decorationText.getText().toString();

            holder.decorationWA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInstalled(mcontext,"com.whatsapp"))
                    {
                        Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.setPackage("com.whatsapp");
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT,text);
                        mcontext.startActivity(intent);
                    }else
                    {
                        Snackbar.make(layout,"Whatsapp is not installed in your phone.", BaseTransientBottomBar.LENGTH_SHORT)
                                .setTextColor(ContextCompat.getColor(mcontext,R.color.white))
                                .setBackgroundTint(ContextCompat.getColor(mcontext, R.color.red)).show();
                    }

                }
            });

            holder.decorationShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Share Using");
                    intent.putExtra(Intent.EXTRA_TEXT,text);
                    mcontext.startActivity(intent);
                }
            });

            holder.decorationWB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isInstalled(mcontext,"com.whatsapp.w4b"))
                    {
                        Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.setPackage("com.whatsapp.w4b");
                        intent.putExtra(Intent.EXTRA_TEXT,text);
                        mcontext.startActivity(intent);
                    }
                    else
                    {
                        Snackbar.make(layout,"Whatsapp business is not installed in your phone.", BaseTransientBottomBar.LENGTH_SHORT)
                                .setTextColor(ContextCompat.getColor(mcontext,R.color.white))
                                .setBackgroundTint(ContextCompat.getColor(mcontext, R.color.red)).show();
                    }

                }
            });
            holder.schOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                        Toast.makeText(mcontext, "Opening Schedule Messages", Toast.LENGTH_LONG).show();



                        Intent i=new Intent(mcontext,schedule_sms.class);

                        i.putExtra("Directmessage",text);

                        i.putExtra("fromChat",1);
                        mcontext.startActivity(i);



                }
            });







            holder.decorationCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager=(ClipboardManager)  mcontext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("Decoration Text",text);
                    clipboardManager.setPrimaryClip(clipData);

                    Snackbar.make(layout,"Copied", BaseTransientBottomBar.LENGTH_SHORT)
                            .setTextColor(ContextCompat.getColor(mcontext,R.color.white))
                            .setBackgroundTint(ContextCompat.getColor(mcontext,R.color.txt_repeat_cl)).show();
                }
            });
            SharedPreferences sp= mcontext.getSharedPreferences("affix",Context.MODE_PRIVATE);
            if (sp.getInt(mcontext.getString(R.string.isScheduleONTag),1)==0) {
                holder.schOpen.setVisibility(View.GONE);
            }
        }
        public static boolean isInstalled(Context context,String packageName)
        {

            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (intent.resolveActivity(packageManager) != null) {
                try {
                    packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                    return true;
                } catch (PackageManager.NameNotFoundException ignore) {

                }
            }
            return false;


        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        public class viewHolder extends RecyclerView.ViewHolder {
            LinearLayout linearLayout;
            TextView decorationText;
            ImageView decorationCopy,decorationShare,decorationWA,decorationCross,decorationWB,schOpen;
            TextInputEditText decorationET;

            public viewHolder(@NonNull View itemView) {
                super(itemView);

                linearLayout = itemView.findViewById(R.id.linerView);

                decorationText=itemView.findViewById(R.id.decorationTB);
                decorationCopy=itemView.findViewById(R.id.decorationCopy);
                decorationShare=itemView.findViewById(R.id.decorationShare);
                decorationWA=itemView.findViewById(R.id.decorationWA);
                decorationWB=itemView.findViewById(R.id.decorationWB);
                decorationET=itemView.findViewById(R.id.decorationET);
                decorationCross=itemView.findViewById(R.id.decorationCross);
                schOpen=itemView.findViewById(R.id.schOpen);
            }
        }
    }
}