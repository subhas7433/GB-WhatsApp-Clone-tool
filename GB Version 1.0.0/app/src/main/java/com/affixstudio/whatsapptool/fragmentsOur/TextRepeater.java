package com.affixstudio.whatsapptool.fragmentsOur;

import static com.affixstudio.whatsapptool.fragmentsOur.DecorationText.Adepter.isInstalled;
import static com.affixstudio.whatsapptool.modelOur.sound.playSound;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.schedule_sms;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;


public class TextRepeater extends Fragment {
    EditText textRepeat,numberOfRepeat,resultText;
    ImageButton Copy,Share,WA;


    ImageView WB;
    LinearLayout layout;
    String message="";
   
    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_text_repeater, container, false);

        textRepeat=v.findViewById(R.id.repeaterET);
        numberOfRepeat=v.findViewById(R.id.numberOfrepeat);
        resultText=v.findViewById(R.id.repeatResult);
        Copy=v.findViewById(R.id.copy);
        Share=v.findViewById(R.id.share);
        WA=v.findViewById(R.id.whatsapp);
        WB=v.findViewById(R.id.whatsappBusiness);
        layout=v.findViewById(R.id.repeatLayout);

        v.findViewById(R.id.clear_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textRepeat.setText("");
                resultText.setText("");
                numberOfRepeat.setText("");
            }
        });



        resultText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                message=charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        v.findViewById(R.id.convertRepeat).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                if (!textRepeat.getText().toString().isEmpty())
                {

                    if (numberOfRepeat.getText().toString().isEmpty())
                    {


                        showSnackBar("Enter number of repeat.",R.color.red);


                    } else if (Integer.parseInt(numberOfRepeat.getText().toString().trim())>5000)
                    {
                        showSnackBar("Maximum limit is 5000.",R.color.red);
                    }
                    else
                    {

                        String Originaltext=textRepeat.getText().toString();
                        final String[] text = {textRepeat.getText().toString()};
                        int n=Integer.parseInt(numberOfRepeat.getText().toString().trim());


                        new AsyncTask<String, String, String>() {

                            ProgressDialog pd;
                            @Override
                            protected void onPreExecute() {
                                pd=new ProgressDialog(getContext());
                                pd.setMessage("Converting..");
                                pd.show();
                                super.onPreExecute();
                            }
                            @Override
                            protected String doInBackground(String... strings) {
                                for (int i=1;i<n;i++)
                                {
                                    text[0] = text[0] +"\n"+Originaltext;
                                }
                                return null;
                            }
                            @Override
                            protected void onPostExecute(String s) {
                                pd.dismiss();
                                resultText.setText(text[0]);
                                message=resultText.getText().toString();
                                super.onPostExecute(s);
                            }


                        }.execute();

                    }

                }else
                {
                    showSnackBar("Enter text.",R.color.red);
                }
            }
        });




        Context context=getContext();

        Copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboardManager=(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clipData= ClipData.newPlainText("Repeat text",message);
                clipboardManager.setPrimaryClip(clipData);

                showSnackBar("Copied",R.color.txt_repeat_cl);

            }
        });

        SharedPreferences sp= getContext().getSharedPreferences("affix",Context.MODE_PRIVATE);
        if (sp.getInt(getContext().getString(R.string.isScheduleONTag),1)==0) {
            v.findViewById(R.id.schOpen).setVisibility(View.GONE);
        }
        v.findViewById(R.id.schOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!message.isEmpty())
                {
                    Toast.makeText(context, "Opening Schedule Messages", Toast.LENGTH_LONG).show();



                    Intent i=new Intent(context, schedule_sms.class);

                    i.putExtra("Directmessage",message);

                    i.putExtra("fromChat",1);
                    context.startActivity(i);
                }else {
                    showSnackBar("Please enter text",R.color.red);
                }

            }
        });

        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_SEND);

                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Share using");
                intent.putExtra(Intent.EXTRA_TEXT,message);
                context.startActivity(intent);

            }
        });
        WA.setOnClickListener(view -> {
            if (isInstalled(context,"com.whatsapp"))
            {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_TEXT,message);
                context.startActivity(intent);
            }else
            {
                showSnackBar("Whatsapp is not installed in your phone.", R.color.red);

            }
        });

        WB.setOnClickListener(view -> {
            if (isInstalled(context,"com.whatsapp.w4b"))
            {

                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp.w4b");
                intent.putExtra(Intent.EXTRA_TEXT,message);
                context.startActivity(intent);

            }else
            {


                showSnackBar("Whatsapp business is not installed in your phone.",R.color.red);
            }

        });



        if (!textRepeat.getText().toString().isEmpty())
        {


            if (!(Integer.parseInt(numberOfRepeat.getText().toString().trim())>5000)
            && !(numberOfRepeat.getText().toString().isEmpty()))
            {

                String Originaltext=textRepeat.getText().toString();
                final String[] text = {textRepeat.getText().toString()};
                int n=Integer.parseInt(numberOfRepeat.getText().toString().trim());


                new AsyncTask<String, String, String>() {


                    @Override
                    protected void onPreExecute() {

                        super.onPreExecute();
                    }
                    @Override
                    protected String doInBackground(String... strings) {
                        for (int i=1;i<n;i++)
                        {
                            text[0] = text[0] +"\n"+Originaltext;
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(String s) {

                        resultText.setText(text[0]);
                        message=resultText.getText().toString();
                        super.onPostExecute(s);
                    }


                }.execute();

            }

        }




        return v;
    }

    void showSnackBar(String message,int color)
    {
        if (color==R.color.red)
        {
            playSound(getContext(),R.raw.error_sound);
        }
        Snackbar.make(layout,message, BaseTransientBottomBar.LENGTH_SHORT)
                .setTextColor(ContextCompat.getColor(getContext(),R.color.white))
                .setBackgroundTint(ContextCompat.getColor(getContext(), color)).show();
    }
}