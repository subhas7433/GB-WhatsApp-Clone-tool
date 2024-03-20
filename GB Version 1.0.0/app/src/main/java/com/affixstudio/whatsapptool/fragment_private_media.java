package com.affixstudio.whatsapptool;

import static com.affixstudio.whatsapptool.modelOur.sound.playSound;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.activityOur.settingActivity;
import com.affixstudio.whatsapptool.ads.AppLovinNative;
import com.affixstudio.whatsapptool.getData.GetInfo;
import com.affixstudio.whatsapptool.getData.GetPrivateMediaFile;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class fragment_private_media extends Fragment  {


    View v;

    ArrayList<Uri> imageUri=new ArrayList<>();
    ArrayList<Uri> videoUri=new ArrayList<>();
    ArrayList<Uri> audioUri=new ArrayList<>();
    ArrayList<Uri> docUri=new ArrayList<>();
    ArrayList<Uri> voiceUri=new ArrayList<>();
    ArrayList<Uri> stickerUri=new ArrayList<>();


    BroadcastReceiver updateUIReciver;
    String[] folderName;
    TextView deleteAll;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_private_recover_media, container, false);



        try {


            AppLovinNative lovinNative=new AppLovinNative(R.layout.native_ad_applovin_midium,getActivity());
            lovinNative.mid(v.findViewById(R.id.ad_frame));
            lovinNative.mid(v.findViewById(R.id.ad_frame1));



        folderName=getResources().getStringArray(R.array.mediaFolderNames);



        SharedPreferences sp=getContext().getSharedPreferences("affix", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEdit=sp.edit();

        deleteAll=v.findViewById(R.id.deleteAll);


        Dialog bs=new Dialog(getContext());

        bs.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.custom_dialog));


        View dV=getLayoutInflater().inflate(R.layout.delete_warning,null);

        Button deleteYes=dV.findViewById(R.id.delete),
                dismiss=dV.findViewById(R.id.dismiss);

        TextView warningText=dV.findViewById(R.id.warningText);




        warningText.setText(getText(R.string.warningTextDeleteAll));


        deleteYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bs.dismiss();
                deleteFiles();
            }
        });
        dismiss.setOnClickListener(view -> {
            bs.dismiss();
        });

        bs.setContentView(dV);

      deleteAll.setOnClickListener(view -> {


          bs.show();


        });




            Dialog whyNotShowingD=new Dialog(getContext());
            whyNotShowingD.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.custom_dialog));

            View wVi=getActivity().getLayoutInflater().inflate(R.layout.why_media_not_showing,null);
            TextView

                    contactUs=wVi.findViewById(R.id.contactUs),
                    folderPG=wVi.findViewById(R.id.folderPG),
                    notificationPG=wVi.findViewById(R.id.notificationPG),
                    storagePgranted=wVi.findViewById(R.id.storagePgranted),
                    gotoWhatsapp=wVi.findViewById(R.id.wastoragePgranted);

            contactUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(getContext(), settingActivity.class));
                }
            });

            GetInfo info=new GetInfo();
            v.findViewById(R.id.workProperly).setOnClickListener(view -> {

                setGranted(folderPG,info.arePermissionDenied(getContext()));
                setGranted(notificationPG,info.noNotificationPermission(getContext()));
                setGranted(storagePgranted,info.noStoragePermission(getContext()));



                whyNotShowingD.setContentView(wVi);
                whyNotShowingD.show();



            });

            gotoWhatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=getContext().getPackageManager()
                            .getLaunchIntentForPackage("com.whatsapp");
                    intent.setClassName("com.whatsapp","com.whatsapp.settings.SettingsDataUsageActivity");
                    startActivity(intent);
                }
            });








            Button
                seeMoreImage=v.findViewById(R.id.seeMoreImage),
                seeMoreVideo=v.findViewById(R.id.seeMoreVideo),
                seeMoreAudio=v.findViewById(R.id.seeMoreAudio),
                seeMoreDocumnet=v.findViewById(R.id.seeMoreDocumnet),
                seeMoreVoicenote=v.findViewById(R.id.seeMoreVoicenote),
                seeMoreSticker=v.findViewById(R.id.seeMoreSticker);



        boolean isPrivateActivity=isPrivateActivity(); // because this fragement used in two activity, private and recover

        Intent intent=new Intent(getContext(),private_view_media_small.class);
        intent.putExtra("isPrivateActivity",isPrivateActivity);
            seeMoreImage.setOnClickListener(view -> {
            intent.putExtra("mediaType",0);
            startActivity(intent);
        });

            seeMoreVideo.setOnClickListener(view -> {
            intent.putExtra("mediaType",1);
            startActivity(intent);
        });

            seeMoreAudio.setOnClickListener(view -> {
            intent.putExtra("mediaType",2);
            startActivity(intent);
        });

            seeMoreDocumnet.setOnClickListener(view -> {
            intent.putExtra("mediaType",3);
            startActivity(intent);
        });

            seeMoreVoicenote.setOnClickListener(view -> {
            intent.putExtra("mediaType",4);
            startActivity(intent);
        });
            seeMoreSticker.setOnClickListener(view ->
            {

                intent.putExtra("mediaType",5);
                startActivity(intent);

            });



            v.findViewById(R.id.turnOnAllMediaDownload).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("com.whatsapp.settings.SettingsDataUsageActivity"))
//                            .setPackage("com.whatsapp").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   Intent intent=getContext().getPackageManager()
                           .getLaunchIntentForPackage("com.whatsapp");
                           intent.setClassName("com.whatsapp","com.whatsapp.settings.SettingsDataUsageActivity");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Handle the exception if WhatsApp is not installed or the activity is not found
                        Toast.makeText(getContext(), "WhatsApp not installed or activity not found.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            IntentFilter filter = new IntentFilter();
            filter.addAction(getString(R.string.updateUIbrodcustRecever));

            updateUIReciver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    //UI update here
                    refreshFiles();

                }
            };
            getContext().registerReceiver(updateUIReciver,filter);
            refreshFiles();

        }catch (Exception e)
        {
            Log.e("private_media",e.getMessage());
        }
        return v;
    }

    private void setGranted(TextView textView, boolean arePermissionDenied) {
        textView.setBackground(getResources().getDrawable(R.drawable.custom_btn));
        if (arePermissionDenied)
        {
            textView.setText("Denied");

            textView.setBackgroundColor(getContext().getResources().getColor(R.color.red));

        }else {
            textView.setText("Granted");
            textView.setBackgroundColor(getContext().getResources().getColor(R.color.primary));
        }
    }

    private void deleteFiles() {
        GetPrivateMediaFile gpm=new GetPrivateMediaFile(); //can't delete data because same data have to show in the recover

        int tableID=R.string.recover_media_table_name;
        if(isPrivateActivity())
        {
            tableID=R.string.privateMediaNamesTable;
        }
        gpm.deleteMediaRecordsAllFolder(tableID,getContext());
        showSnackBar(R.color.colorSecondary);
        onResume();
    }

    private void refreshFiles() {



        try {



        RecyclerView
                imageRV=v.findViewById(R.id.imageRV),
                videoRV=v.findViewById(R.id.recycleVideo),
                audioRecycle=v.findViewById(R.id.audioRecycle),
                docRV=v.findViewById(R.id.docRecycle),
                stickerRV=v.findViewById(R.id.stickerRV),
                voiceRV=v.findViewById(R.id.voiceRV);
        LinearLayout
                showImageLA=v.findViewById(R.id.showImageLA),
                showVideoLA=v.findViewById(R.id.showVideoLA),
                showAudioLA=v.findViewById(R.id.showAudioLA),
                showDocLA=v.findViewById(R.id.showDocLA),
                showVoiceLA=v.findViewById(R.id.showVoiceLA),
                showStickerLA=v.findViewById(R.id.showStickerLA),


                noImageLA=v.findViewById(R.id.noImageLA),
                noVideoLA=v.findViewById(R.id.noVideoLA),
                noAudioLA=v.findViewById(R.id.noAudioLA),
                noDocLA=v.findViewById(R.id.noDocLA),
                noVoiceLA=v.findViewById(R.id.noVoiceLA),
                noStickerLA=v.findViewById(R.id.noSticlerLA);




        if (!NoStoragePermission())
        {


                i("has Storage Permission");

                 //Thread.sleep(100);
                 getdata(folderName[1], showVideoLA, noVideoLA, R.layout.media_video_recycleview, videoRV, 1);
                 //   Thread.sleep(10);
                 getdata(folderName[2], showAudioLA, noAudioLA, R.layout.media_audio_recycleview, audioRecycle, 2);
                  //  Thread.sleep(10);

                    getdata(folderName[3], showDocLA, noDocLA, R.layout.media_document_recycleview, docRV, 3);
               // Thread.sleep(10);
                    getdata(folderName[4], showVoiceLA, noVoiceLA, R.layout.media_voicenote_recycleview, voiceRV, 4);
              //  Thread.sleep(10);
                    getdata(folderName[5], showStickerLA, noStickerLA, R.layout.media_sticker_recycleview, stickerRV, 5);
                    getdata(folderName[0], showImageLA, noImageLA, R.layout.media_image_recycleview, imageRV, 0);


//            imageUri = getdata(imageFolderName, showImageLA, noImageLA, R.layout.media_image_recycleview, imageRV, 0);
//            videoUri = getdata(VideosFolderName, showVideoLA, noVideoLA, R.layout.media_video_recycleview, videoRV, 1);
//            audioUri = getdata(AudiosFolderName, showAudioLA, noAudioLA, R.layout.media_audio_recycleview, audioRecycle, 2);
//            docUri = getdata(DocumentsFolderName, showDocLA, noDocLA, R.layout.media_document_recycleview, docRV, 3);
//            voiceUri = getdata(VoicesFolderName, showVoiceLA, noVoiceLA, R.layout.media_voicenote_recycleview, voiceRV, 4);
//




        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, 2);

            }
        }

        }catch (Exception e)
        {
            Log.e(getContext().toString(),e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFiles();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(updateUIReciver);
    }

    RecyclerView.Adapter setAdapter(ArrayList<Uri> uris,
                                    int resourceLayout, int recycleType,
                                    RecyclerView recyclerView) // recycleType 0=image,1=video,2=audio,3 =doc
    {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        return new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(resourceLayout,parent,false))
                {
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



                i("uri "+uris.size()+" position "+position);

                if (uris.size()!=0)
                {



                    int p = uris.size()-1- position;
                    ImageView im;
                    ImageView delete;
                    LinearLayout mediaRecycleLayout;






                    if (recycleType == 0)//image
                    {
                        im = h.itemView.findViewById(R.id.imageImage);
                        delete = h.itemView.findViewById(R.id.deleteSticker); // using sticker layout
                        mediaRecycleLayout = h.itemView.findViewById(R.id.mediaImageRecycleLayout);
                        Glide.with(getActivity()).load(uris.get(p)).into(im); // setting image //todo no media not showing properly
                        delete.setVisibility(View.GONE);


                    }
                    else if (recycleType == 1) //video
                    {
                        im = h.itemView.findViewById(R.id.videoImage);
                        delete = h.itemView.findViewById(R.id.deleteVideo);
                        mediaRecycleLayout = h.itemView.findViewById(R.id.mediaVideoRecycleLayout);
                        Glide.with(getActivity()).load(uris.get(p)).into(im); // setting image


                    } else if (recycleType == 2) //audio
                    {
                        TextView audioName = h.itemView.findViewById(R.id.audioName);
                        audioName.setText(getFileName(uris.get(p)));
                        delete = h.itemView.findViewById(R.id.deleteAudio);
                        mediaRecycleLayout = h.itemView.findViewById(R.id.mediaAudioRecycleLayout);
                    }
                    else if (recycleType == 3 ) // doc
                    {
                        i("documents show selected files");
                        TextView docName = h.itemView.findViewById(R.id.documentsName);
                        docName.setText(getFileName(uris.get(p)));
                        delete = h.itemView.findViewById(R.id.deleteDoc);
                        mediaRecycleLayout = h.itemView.findViewById(R.id.media_document_layout);
                    }
                    else if (recycleType==4)
                    { // voice
                        i("voiceName show selected files");
                        TextView voiceName = h.itemView.findViewById(R.id.voiceName);
                        voiceName.setText(getFileName(uris.get(p)));
                        delete = h.itemView.findViewById(R.id.deleteAudio);
                        mediaRecycleLayout = h.itemView.findViewById(R.id.mediaVoiceRecycleLayout);
                    }
                    else
                    { // sticker
                        i("sticker show selected files");


                        mediaRecycleLayout = h.itemView.findViewById(R.id.mediaStickerRecycleLayout);
                        im = h.itemView.findViewById(R.id.sticker); // todo delete sticker
                        delete = h.itemView.findViewById(R.id.deleteImage);

                        Glide.with(getActivity()).load(uris.get(p)).into(im); // setting image //todo no media not showing properly

                    }


                  //  LinearLayout finalMediaRecycleLayout = mediaRecycleLayout;
                    int finalp = p;

                    mediaRecycleLayout.setOnClickListener(view -> {

                        i("mediaRecycleLayout.setOnClickListener");
                        GetInfo getInfo=new GetInfo();
                        String mime = getInfo.getMimeType(getContext(),uris.get(p));
                        if (recycleType>1 && recycleType!=5 )
                        {
                            GetInfo gi=new GetInfo();
                            Uri shareableUri=gi.getShareableUri(uris.get(p),getContext());

                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(shareableUri,mime);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(intent,"Select app" ));

                        }else
                        {
                            Intent in=new Intent(getContext(),playPrivateMedia.class);
                            in.putExtra("mediaType",recycleType);
                            in.putExtra("uri",uris.get(p).toString());
                            in.putExtra(getString(R.string.isPrivateActivityTag),isPrivateActivity());

                            startActivity(in);

                        }
                    });


                    delete.setVisibility(View.GONE);




                }

                }catch (Exception e)
                {
                    Log.e("privateMedia",e.getMessage());
                }

            }

            

            @Override
            public int getItemCount() {
                if (uris.size()<4)
                {
                    i("getItemCount "+uris.size());
                    return uris.size();
                }
                return 3;
            }
        };
    }

    private void ifNoFile()
    {

        v.findViewById(R.id.noMediaLA).setVisibility(View.VISIBLE);
        v.findViewById(R.id.mediaCard).setVisibility(View.GONE);
        deleteAll.setVisibility(View.GONE);



    }

    ArrayList<Uri> list;
    @SuppressLint("StaticFieldLeak")
    private ArrayList<Uri> getdata(String folderName, LinearLayout showContentLA, LinearLayout noContentLA,
                                   int recycleDesign, RecyclerView recyclerView, int recycleType) {
        LinearLayoutManager horizontalLM=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);


        GetPrivateMediaFile gpm = new GetPrivateMediaFile();
        list = new ArrayList<>();

             AsyncTask asyncTask=  new AsyncTask() {
                   @Override
                   protected Object doInBackground(Object[] objects) {
                       if (isPrivateActivity())
                       {
                           list = gpm.getRecordedFiles(folderName,getContext(),getString(R.string.privateMediaNamesTable));

                       }
                       else
                       {
                           list = gpm.getRecordedFiles(folderName,getContext(),getString(R.string.recover_media_table_name));
                       }

                       if (recycleType==0)
                       {
                           imageUri=list;

                       }else if (recycleType==1)
                       {
                           videoUri=list;

                       }else if (recycleType==2)
                       {
                           audioUri=list;
                       }else if (recycleType==3)
                       {
                           docUri=list;
                       }else if (recycleType==4)
                       {
                           i("voice list size "+list.size());
                           voiceUri=list;

                       }else if(recycleType==5)
                       {
                           stickerUri=list;
                       }



                       return list;
                   }


                 @Override
                 protected void onPostExecute(Object o) {

                       i("running "+recycleType);
                     try {


                         ArrayList<Uri> list1=new ArrayList<>();
                                 if (recycleType==0)
                                 {

                                     list1=imageUri;

                                 }else if (recycleType==1)
                                 {
                                     list1=videoUri;

                                 }else if (recycleType==2)
                                 {
                                     list1= audioUri;
                                 }else if (recycleType==3)
                                 {
                                     list1= docUri;
                                 }else if (recycleType==4)
                                 {
                                     list1=voiceUri;
                                 }else if(recycleType==5)
                                 {
                                     list1=stickerUri;
                                 }




                         if ((imageUri.size() + videoUri.size() + audioUri.size() + docUri.size() + voiceUri.size() +stickerUri.size() + list1.size()) > 0)
                         {

                             hasFile();//show main layout
                             if (list1.size() == 0) {
                                 i("list is empty ");
                                 noContentLayout(showContentLA, noContentLA);
                             } else {
                                 showContentLayout(showContentLA, noContentLA);
                                 recyclerView.setLayoutManager(horizontalLM);

//                                 if (recycleType==0)
//                                 {
//
//                                     recyclerView.setAdapter(setAdapter(imageUri, recycleDesign, recycleType, recyclerView));
//                                 }else if (recycleType==1)
//                                 {
//
//                                     recyclerView.setAdapter(setAdapter(videoUri, recycleDesign, recycleType, recyclerView));
//                                 }else if (recycleType==2)
//                                 {
//                                     audioUri=list;
//                                 }else if (recycleType==3)
//                                 {
//                                     docUri=list;
//                                 }else if (recycleType==4)
//                                 {
//                                     voiceUri=list;
//                                 }else if(recycleType==5)
//                                 {
//                                     stickerUri=list;
//                                 }
                                   recyclerView.setAdapter(setAdapter(list1, recycleDesign, recycleType, recyclerView));
                             }
                         }
                         else if (recycleType < 5) //if first 5 does not contain any file no nocontent will show
                         {
                             noContentLayout(showContentLA, noContentLA);
                         } else if (recycleType == 5)//if even sixth also don't contain any file then nofile will show
                         {
                             i("list.size() = " + list.size());
                             ifNoFile(); // Gone main layout
                         } // todo work on small screen


                     } catch (Exception e) {
                         Log.e("privateMedia", e.getMessage());
                     }
                     super.onPostExecute(o);
                 }
             };
             asyncTask.execute();







        return list;
    }





    boolean isPrivateActivity()
    {
        return  getContext().toString().contains("private_chat");
    }
    private void hasFile() {
        v.findViewById(R.id.noMediaLA).setVisibility(View.GONE);
        v.findViewById(R.id.mediaCard).setVisibility(View.VISIBLE);
        deleteAll.setVisibility(View.VISIBLE);
    }

    private void noContentLayout(LinearLayout showContentLA, LinearLayout noContentLA) {

        showContentLA.setVisibility(View.GONE);
        noContentLA.setVisibility(View.VISIBLE);
    }
    private void showContentLayout(LinearLayout showContentLA, LinearLayout noContentLA) {


        showContentLA.setVisibility(View.VISIBLE);
        noContentLA.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==2)
        {
            i("onActivityResult "+resultCode);
        }
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {

        try {


            String result = null;
            if (uri.getScheme().equals("content")) {
                Cursor cursor =getContext().getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally
                {
                    cursor.close();
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            return result; //todo set doc in the list and show in the show dialog
        }
        catch (Exception e)
        {

            Log.e("schedule_sms",e.getMessage());
            return "failed";
        }
    }


    public void i(String s)
    {
        Log.i("privateMedia",s);
    }
    final String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    boolean NoStoragePermission()
    {
        boolean t=false;
        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getContext(), permissions) != PackageManager.PERMISSION_GRANTED) {


                t= true;
            }
        }
        return t;
    }
    void showSnackBar(int color)
    {
        if (color==R.color.red)
        {
            playSound(getContext(),R.raw.error_sound);
        }
        Snackbar.make(v.findViewById(R.id.mainLayout), "All records deleted successfully", BaseTransientBottomBar.LENGTH_SHORT)
                .setTextColor(ContextCompat.getColor(getContext(),R.color.white))
                .setBackgroundTint(ContextCompat.getColor(getContext(), color)).show();
    }





}