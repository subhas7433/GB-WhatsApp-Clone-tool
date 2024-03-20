package com.affixstudio.whatsapptool;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.affixstudio.whatsapptool.getData.GetInfo;
import com.affixstudio.whatsapptool.getData.GetPrivateMediaFile;
import com.affixstudio.whatsapptool.getData.getMiliDiff;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;

public class playPrivateMedia extends AppCompatActivity {

    int mediaType=0;
    private boolean isFromOtherScreen=false;
    private boolean isFromPrivate=false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_private_media);
        getWindow().setStatusBarColor(getResources().getColor(R.color.full_screen_header_bg, this.getTheme()));


        TextView
                fileName = findViewById(R.id.fileName),
                downloadtime = findViewById(R.id.downloadtime);
        ImageButton
                mediaBack = findViewById(R.id.mediaBack);

        VideoView fullVideo = findViewById(R.id.fullVideo);
        ImageView fullImage = findViewById(R.id.fullImage);

        mediaBack.setOnClickListener(view -> {
            onBackPressed();
        });


        Intent in = getIntent();
        Uri uri = Uri.parse(in.getStringExtra("uri"));
        mediaType = in.getIntExtra("mediaType", 0);
        isFromOtherScreen = in.getBooleanExtra(getString(R.string.isFromOtherScreenPlayPrivateMedia), false); //is coming not from private or recover
        File file = new File(uri.getPath());

        isFromPrivate = in.getBooleanExtra(getString(R.string.isPrivateActivityTag), false);
        if (isFromPrivate) {
            downloadtime.setText(new getMiliDiff().getDateForMilis(file.lastModified()));
        } else if (isFromOtherScreen) {
            downloadtime.setText(new getMiliDiff().getDateForMilis(file.lastModified()));
        } else {
            downloadtime.setText(new getMiliDiff().getRecoverdDate(playPrivateMedia.this, file.getName()));
        }


        findViewById(R.id.more).setOnClickListener(view -> {
            showMore(file); // show bottom dialog when clicked more
        });
        if (isFromOtherScreen) {
            findViewById(R.id.more).setVisibility(View.GONE);
        }


        if (mediaType == 1) {


            MediaController mediaController = new MediaController(playPrivateMedia.this);
            fullVideo.setVisibility(View.VISIBLE);
            fullImage.setVisibility(View.GONE);
            fullVideo.setVideoURI(uri);
            fullVideo.setMediaController(mediaController);
            fullVideo.start();


            fullVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });


        } else {

            fullImage.setVisibility(View.VISIBLE);
            fullVideo.setVisibility(View.GONE);
            fullImage.setImageURI(uri);
            fullImage.setOnTouchListener(new ImageMatrixTouchHandler(playPrivateMedia.this));


        }

    }










    void showMore(File file)
    {
        Uri uri=Uri.fromFile(file);
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(this);
        View v= View.inflate(this,R.layout.media_menu_dialog_fullscreen,null);
        LinearLayout predictionHori=v.findViewById(R.id.predictionHori);
        TextView

                name1=v.findViewById(R.id.name1),
                name2=v.findViewById(R.id.name2),
                name3=v.findViewById(R.id.name3);


        TextView
                whatsappBtn=v.findViewById(R.id.whatsapp),
                whatsappBsBtn=v.findViewById(R.id.whatsappBsBtn),
                shareBtn=v.findViewById(R.id.shareBtn),
                deletebtn=v.findViewById(R.id.deletebtn);



        GetInfo getInfo=new GetInfo();
        ArrayList<String> prediction=getInfo.getPrediction(mediaType,playPrivateMedia.this,file.lastModified());

        if (prediction.size()>0)
        {

            predictionHori.setVisibility(View.VISIBLE);
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

        Uri shareableUri=uri;
        if (!isFromOtherScreen)
        {
            shareableUri=getInfo.getShareableUri(uri,this);
        }

        String mime = getInfo.getMimeType(this,uri);
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setType(mime);
        intentShareFile.putExtra(Intent.EXTRA_STREAM, shareableUri);


        Uri finalShareableUri = shareableUri;
        shareBtn.setOnClickListener(view -> {

            Intent ShareFile = new Intent(Intent.ACTION_SEND);

            ShareFile.setType(mime);
            ShareFile.putExtra(Intent.EXTRA_STREAM, finalShareableUri);
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


        deletebtn.setOnClickListener(view -> { SharedPreferences sp=getSharedPreferences("affix",MODE_PRIVATE);
            SharedPreferences.Editor spE=sp.edit();

            Dialog bs=new Dialog(playPrivateMedia.this);

            bs.getWindow().setBackgroundDrawable(playPrivateMedia.this.getDrawable(R.drawable.custom_dialog));


            View dV=getLayoutInflater().inflate(R.layout.delete_warning,null);

            Button deleteYes=dV.findViewById(R.id.delete),
                    dismiss=dV.findViewById(R.id.dismiss);
            CheckBox dontShowAgain=dV.findViewById(R.id.dontShow);
            TextView warningText=dV.findViewById(R.id.warningText);

            warningText.setText(getText(R.string.warningTextDeleteSingle));



            dontShowAgain.setVisibility(View.VISIBLE);

            GetPrivateMediaFile Gm=new GetPrivateMediaFile();
            int tableID=R.string.privateMediaNamesTable;
            if (!isFromPrivate)
            {

                tableID=R.string.recover_media_table_name;
            }

            int finalTableID = tableID;

            deleteYes.setOnClickListener(view1 -> {
                if (dontShowAgain.isChecked())
                {
                    spE.putBoolean(getString(R.string.dontShowInFullAllDelete),true).apply();
                    // Deleting the file
                    if (Gm.deleteSingleMediaRecord(finalTableID,this,getInfo.getFileName(uri,this),uri))
                    {
                        onBackPressed();

                    }
                }else {
                    // Deleting the file

                    if (Gm.deleteSingleMediaRecord(finalTableID,this,getInfo.getFileName(uri,this),uri))
                    {
                        onBackPressed();

                    }
                }
                bs.dismiss();

            });
            dismiss.setOnClickListener(view1 -> {
                bs.dismiss();
            });

            bs.setContentView(dV);




            if (sp.getBoolean(getString(R.string.dontShowInFullAllDelete),false))
            {
                // Deleting the file

                if (Gm.deleteSingleMediaRecord(tableID,this,getInfo.getFileName(uri,this),uri))
                {
                    onBackPressed();

                }

            }
            else
            {
                bs.show();

            }










        });


        bottomSheetDialog.setContentView(v);
        bottomSheetDialog.show();
    }
}