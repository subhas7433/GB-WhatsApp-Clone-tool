package com.affixstudio.whatsapptool;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.getData.GetInfo;
import com.affixstudio.whatsapptool.getData.GetPrivateMediaFile;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomSmallDialog;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class private_view_media_small extends AppCompatActivity {

    int mediaType=0;
    boolean isFromPrivateMedia=true;
    String[] folderName;

    GetPrivateMediaFile gpm=new GetPrivateMediaFile();
    Context c=this;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_view_media_small);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        folderName=getResources().getStringArray(R.array.mediaFolderNames);
        TextView title=findViewById(R.id.pageTitle),
                deleteAll=findViewById(R.id.deleteAllMediaSmall);
        recyclerView=findViewById(R.id.recycler);

      //  MobileAds.initialize(c);



        mediaType=getIntent().getIntExtra("mediaType",0);
        isFromPrivateMedia=getIntent().getBooleanExtra(getString(R.string.isPrivateActivityTag),true);

        i("mediaType "+getIntent().getIntExtra("mediaType",0));
        title.setText(folderName[mediaType]);



        Dialog bs=new Dialog(private_view_media_small.this);

        bs.getWindow().setBackgroundDrawable(private_view_media_small.this.getDrawable(R.drawable.custom_dialog));


        View dV=getLayoutInflater().inflate(R.layout.delete_warning,null);

        Button deleteYes=dV.findViewById(R.id.delete),
                dismiss=dV.findViewById(R.id.dismiss);
        TextView warningText=dV.findViewById(R.id.warningText);

        warningText.setText(getText(R.string.warningTextDeleteAll));



        deleteYes.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bs.dismiss();
                deleteAllfiles();

            }
        });
        dismiss.setOnClickListener(view -> {
            bs.dismiss();
        });

        bs.setContentView(dV);

        deleteAll.setOnClickListener(view -> {

           bs.show();

        });


        refreshRecycler();

        findViewById(R.id.mediaBack).setOnClickListener(view -> {
            onBackPressed();
        });


        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.updateUIbrodcustRecever));

        updateUIReciver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //UI update here
                refreshRecycler();

            }
        };
        registerReceiver(updateUIReciver,filter);

    }
    BroadcastReceiver updateUIReciver;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateUIReciver);
    }

    private void deleteAllfiles() {
        if (isFromPrivateMedia)
        {
            if (gpm.deleteRecordedMediaOneFolder(folderName[mediaType],private_view_media_small.this,R.string.privateMediaNamesTable))
            {
                onBackPressed();
            }

        } else {
            if (gpm.deleteRecordedMediaOneFolder(folderName[mediaType],private_view_media_small.this,R.string.recover_media_table_name))// todo fix all clear in recover section

            {
                onBackPressed();
            }
        }


        Toast.makeText(c, "Deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecycler();
    }
    ArrayList<Uri> uris;
    void refreshRecycler()

    {
         uris=new ArrayList<>();
        @SuppressLint("StaticFieldLeak") AsyncTask asyncTask=new AsyncTask() {


            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                pd=new ProgressDialog(c);
                pd.setMessage("Getting files..");
                pd.setCancelable(false);
                pd.show();
                super.onPreExecute();
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                if (isFromPrivateMedia)
                {
                    uris=gpm.getRecordedFiles(folderName[mediaType],c,getString(R.string.privateMediaNamesTable));
                    // uris=gpm.getFile(folderName[mediaType]);
                }else {
                    uris=gpm.getRecordedFiles(folderName[mediaType],c,getString(R.string.recover_media_table_name));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                pd.dismiss();
                if (uris.size()>0)
                {

                    recyclerView.setAdapter(getAdepter(uris));
                }
                else
                {
                    Toast.makeText(c, "No files is there to show.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                super.onPostExecute(o);
            }
        };

        asyncTask.execute();





    }

    RecyclerView.Adapter getAdepter(ArrayList<Uri> uris)
    {

        Context[]  context=new Context[1];
        return new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                context[0]=parent.getContext();
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.private_media_recycleview,parent,false)) {
                    @NonNull
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position)
            {
                try {


                    FrameLayout adFrame=h.itemView.findViewById(R.id.ad_frame);

                    if (getItemViewType(position)==0)
                    {




                        adFrame.setVisibility(View.VISIBLE);
                        if (mediaType<2 || mediaType==5) //video and image
                        {
//                            AppLovinNative lovinNative=new AppLovinNative(R.layout.native_ad_applovin_midium,private_view_media_small.this);
//                            lovinNative.mid(adFrame);

                        }
                        else
                        {

                            // doc and other
//                            AppLovinNative lovinNative2=new AppLovinNative(R.layout.native_ad_mid_private_small,private_view_media_small.this);
//                            lovinNative2.small(adFrame);
                        }

                    }

                int i=uris.size()-1-position;

                Uri uri=uris.get(i);


                GetInfo gi=new GetInfo();
                Uri shareableUri=gi.getShareableUri(uri,c);





                GetInfo getInfo=new GetInfo();
                String mime = getInfo.getMimeType(c,uri);

                LinearLayout predictionHori=h.itemView.findViewById(R.id.predictionHori);

                TextView
                        name1=h.itemView.findViewById(R.id.name1),
                        name2=h.itemView.findViewById(R.id.name2),
                        docName=h.itemView.findViewById(R.id.docName),
                        name3=h.itemView.findViewById(R.id.name3);

                ImageButton
                        whatsappBtn=h.itemView.findViewById(R.id.whatsappBtn),
                        whatsappBsBtn=h.itemView.findViewById(R.id.whatsappBsBtn),
                        shareBtn=h.itemView.findViewById(R.id.shareBtn),
                        playButton=h.itemView.findViewById(R.id.playButton),
                        deletebtn=h.itemView.findViewById(R.id.deletebtn);

                ImageView
                        nameTutorial=h.itemView.findViewById(R.id.nameTutorial),
                        docIcon=h.itemView.findViewById(R.id.docIcon),
                        thumbnail=h.itemView.findViewById(R.id.thumbnail);

                CardView
                        docCard=h.itemView.findViewById(R.id.docCard),
                        mediaCard=h.itemView.findViewById(R.id.mediaCard);

                LinearLayout mainLayout=h.itemView.findViewById(R.id.mainLayout);


                nameTutorial.setOnClickListener(view -> {
                    bottomSmallDialog bsd=new bottomSmallDialog();
                    bsd.showinfo(c, 5);
                });




                ArrayList<String> prediction=getInfo.getPrediction(mediaType,c,new File(uri.getPath()).lastModified());
                    i("mediaType "+mediaType); //todo private chat is not showing
                    i("prediction.size() "+prediction.size());
                    if (prediction.size()>0)
                    {
                        predictionHori.setVisibility(View.VISIBLE);
                        i("prediction.size()>0 ");
                        if (prediction.size()==1)
                        {
                            name1.setVisibility(View.VISIBLE);
                            name1.setText(prediction.get(0));
                        }else if (prediction.size()==2)
                        {
                            name1.setText(prediction.get(0));
                            name2.setVisibility(View.VISIBLE);
                            name2.setText(prediction.get(1));
                        }else if (prediction.size()==3)
                        {
                            name1.setText(prediction.get(0));
                            name2.setVisibility(View.VISIBLE);
                            name2.setText(prediction.get(1));
                            name3.setVisibility(View.VISIBLE);
                            name3.setText(prediction.get(2));
                        }
                    }



                if (mediaType<2 || mediaType==5) //video and image
                {
                    Glide.with(c).load(uri).into(thumbnail); // setting image
                    mediaCard.setVisibility(View.VISIBLE);
                    docCard.setVisibility(View.GONE);






                    if (mediaType==1)//video
                    {
                        playButton.setVisibility(View.VISIBLE);
                        playButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent in=new Intent(private_view_media_small.this,playPrivateMedia.class);
                                in.putExtra("mediaType",mediaType);
                                in.putExtra("uri",uri.toString());
                                in.putExtra(getString(R.string.isPrivateActivityTag),true); // its for the file modification date
                                startActivity(in);
                            }
                        });
                    }
                    thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if (mediaType>1 &&  mediaType!=5)
                            {
                                Intent intent=new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(shareableUri,mime); // sharing with file provider
                            }
                            else
                            {
                                Intent in=new Intent(private_view_media_small.this,playPrivateMedia.class);
                                in.putExtra("mediaType",mediaType);
                                in.putExtra("uri",uri.toString());
                                in.putExtra(getString(R.string.isPrivateActivityTag),isFromPrivateMedia); // its for the file modification date

                                startActivity(in);

                            }
                        }
                    });






                }
                else
                {
                    mediaCard.setVisibility(View.GONE);
                    docCard.setVisibility(View.VISIBLE);



                    docName.setText(getInfo.getFileName(uri,c));

                    if (mediaType==2)//audio
                    {
                        docIcon.setImageResource(R.drawable.music_icon);
                    }
                    else if (mediaType==3) //doc
                    {
                        docIcon.setImageResource(R.drawable.doument_icon);
                    }
                    else  //voice
                    {
                        docIcon.setImageResource(R.drawable.voice_icon);
                    }

                    docCard.setOnClickListener(view -> {

                        Intent it=new Intent(Intent.ACTION_VIEW);
                        it.setDataAndType(shareableUri,mime);
                        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(it,"Select app" ));
                    });

                }



                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                intentShareFile.setType(mime);
                intentShareFile.putExtra(Intent.EXTRA_STREAM, shareableUri);


                shareBtn.setOnClickListener(view -> {

                    Intent ShareFile = new Intent(Intent.ACTION_SEND);

                    ShareFile.setType(mime);
                    ShareFile.putExtra(Intent.EXTRA_STREAM, shareableUri);
                    startActivity(Intent.createChooser(ShareFile, "Share File"));

                });

                whatsappBtn.setOnClickListener(view -> {

                    intentShareFile.setPackage("com.whatsapp");

                    startActivity(intentShareFile);
                });

                whatsappBsBtn.setOnClickListener( view -> {

                    intentShareFile.setPackage("com.whatsapp.w4b");
                    startActivity(Intent.createChooser(intentShareFile, "Share File"));
                });


                deletebtn.setOnClickListener(view -> {

                    SharedPreferences sp=getSharedPreferences("affix",MODE_PRIVATE);
                    SharedPreferences.Editor spE=sp.edit();

                    Dialog bs=new Dialog(private_view_media_small.this);

                    bs.getWindow().setBackgroundDrawable(private_view_media_small.this.getDrawable(R.drawable.custom_dialog));


                    View dV=getLayoutInflater().inflate(R.layout.delete_warning,null);

                    Button deleteYes=dV.findViewById(R.id.delete),
                            dismiss=dV.findViewById(R.id.dismiss);
                    CheckBox dontShowAgain=dV.findViewById(R.id.dontShow);
                    TextView warningText=dV.findViewById(R.id.warningText);

                    warningText.setText(getText(R.string.warningTextDeleteSingle));

                    dontShowAgain.setVisibility(View.VISIBLE);

                    GetPrivateMediaFile Gm=new GetPrivateMediaFile();
                    int tableID=R.string.privateMediaNamesTable;
                    if (!isFromPrivateMedia)
                    {

                        tableID=R.string.recover_media_table_name;
                    }

                    int finalTableID = tableID;

                    deleteYes.setOnClickListener(view1 -> {
                        bs.dismiss();
                        if (dontShowAgain.isChecked())
                        {
                            spE.putBoolean(getString(R.string.dontShowInsmallAllDelete),true).apply();
                            // Deleting the file

                            if (Gm.deleteSingleMediaRecord(finalTableID,c,getInfo.getFileName(uri,c),uri))
                            {
                                recyclerView.removeAllViews();
                                refreshRecycler();

                            }
                        }else {
                            // Deleting the file

                            if (Gm.deleteSingleMediaRecord(finalTableID,c,getInfo.getFileName(uri,c),uri))
                            {
                                recyclerView.removeAllViews();
                                refreshRecycler();

                            }
                        }


                    });
                    dismiss.setOnClickListener(view1 -> {
                        bs.dismiss();
                    });

                    bs.setContentView(dV);




                    if (sp.getBoolean(getString(R.string.dontShowInsmallAllDelete),false))
                    {
                        // Deleting the file

                        if (Gm.deleteSingleMediaRecord(tableID,c,getInfo.getFileName(uri,c),uri))
                        {
                            recyclerView.removeAllViews();
                            refreshRecycler();

                        }

                    }
                    else
                    {
                        bs.show();

                    }








                });




                }
                catch (Exception e)
                {
                    Log.e(this.toString(),e.getMessage());
                }

            }

            @Override
            public int getItemViewType(int position) {
                if (position!=0 && position%2==0)
                {
                    return 0;
                }else {
                    return 1;
                }
            }

            @Override
            public int getItemCount() {
                return uris.size();
            }
        };
    }



    void i(String s)
    {
        Log.i(c.toString(),s);
    }
}