package com.affixstudio.whatsapptool.fragmentsOur;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.affixstudio.whatsapptool.CustomSpinnerItem;
import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.ads.AppLovinNative;
import com.affixstudio.whatsapptool.modelOur.dialog.bottomInfoDialog;
import com.affixstudio.whatsapptool.modelOur.setOnRecycleClick;
import com.affixstudio.whatsapptool.modelOur.whatsappStatusModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;


public class StatusFragment extends Fragment implements setOnRecycleClick,AdapterView.OnItemSelectedListener{
    final static int APP_STORAGE_ACCESS_REQUEST_CODE = 501; // Any value
    private static final int REQUEST_PERMISSIONS = 987;
    private static final String TAG = "status";
    private static final int READ_STORAGE_ANDROID11 = 898;
    View v;


    private ArrayList<whatsappStatusModel> list;
    private com.affixstudio.whatsapptool.adopterOur.adopter adopter;

    RadioButton allStatus,downloadedStatus;




    String selection="Whatsapp";
    String contentType="allStatus";
    SwipeRefreshLayout refresh;
    int notFirstTime=0;

    setOnRecycleClick setOnRecycleClick;

    public StatusFragment(com.affixstudio.whatsapptool.modelOur.setOnRecycleClick setOnRecycleClick) {
        this.setOnRecycleClick = setOnRecycleClick;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.i("Fragment","status");
        v=inflater.inflate(R.layout.fragment_status, container, false);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.white, getContext().getTheme()));
        Spinner spinner=v.findViewById(R.id.whatsappSclector);

        allStatus=v.findViewById(R.id.RBCatagory);

        downloadedStatus=v.findViewById(R.id.RBsv);

        TextView
                imagelb=v.findViewById(R.id.imagelb),
                videolB=v.findViewById(R.id.textView34),
                noVideolB=v.findViewById(R.id.noVideoLB),
                noImagelb=v.findViewById(R.id.noImageLB);


        v.findViewById(R.id.tutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomInfoDialog bid=new bottomInfoDialog();
                bid.showinfo(getContext(),7,getString(R.string.fristInStatusTeg));
            }
        });


        // native ads

        AppLovinNative lovinNative=new AppLovinNative(R.layout.native_ad_applovin_midium,getActivity());
        lovinNative.mid(v.findViewById(R.id.fl_adplaceholder));
        lovinNative.mid(v.findViewById(R.id.ad_frameDownload));











        if (!isInstalled(getContext(),"com.whatsapp") && (!isInstalled(getContext(),"com.whatsapp.w4b"))  /*(!isInstalled(getContext(),"com.whatsapp")) || !(isInstalled(getContext(),"com.whatsapp.w4b"))*/)
        {
            Log.i("whatsappInstalled",""+isInstalled(getContext(),"com.whatsapp"));
            v.findViewById(R.id.noWhatsappLayout).setVisibility(View.VISIBLE);
            v.findViewById(R.id.mainLayout).setVisibility(View.GONE);
            Log.i(TAG,"mainLayout gone isInstalled");

        }else { // after the confirmation of whatsapp installations setting up layout





            SharedPreferences sp=getContext().getSharedPreferences("affix",MODE_PRIVATE);
            SharedPreferences.Editor spEdit=sp.edit();

            Log.i("status","entered first else");
            allStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if (b) {


                        contentType = "allStatus";
                        i("allstatus checked ");
                        videolB.setText("Video Status");
                        imagelb.setText("Image Status");
                        noImagelb.setText("Watch a status in WhatsApp");
                        noVideolB.setText("Watch a status in WhatsApp");
                        refreshFiles();




                    }
                }
            });




            downloadedStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (b) {
                        contentType = "downloadedStatus";
                        videolB.setText("Downloaded Video");
                        imagelb.setText("Downloaded Image");
                        noImagelb.setText("You didn't download any Image");
                        noVideolB.setText("You didn't download any Video");
                        if (!NoStoragePermission()) {
                            v.findViewById(R.id.permissionLayout).setVisibility(View.GONE);
                            refreshFiles();
                            // getDownloadedStatus();
                        }else {
                            requestPermissions(PERMISSIONS,READ_STORAGE_ANDROID11);

                        }
//                        AdLoader  adLoader=new AdLoader.Builder(getContext(),getString(R.string.nativeAdsId))
//                                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                                    @Override
//                                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
//                                        // Assumes you have a placeholder FrameLayout in your View layout
//                                        // (with id fl_adplaceholder) where the ad is to be placed.
//                                        FrameLayout frameLayout =
//                                                v.findViewById(R.id.ad_frameDownload);
//                                        // Assumes that your ad layout is in a file call native_ad_layout.xml
//                                        // in the res/layout folder
//                                        NativeAdView adView = (NativeAdView) inflater
//                                                .inflate(R.layout.native_ad_applovin_small, null);
//                                        // This method sets the text, images and the native ad, etc into the ad
//                                        // view.
//                                        nativeAds nativeClass=new nativeAds();
//                                        nativeClass.populateNativeAdViewSmall(nativeAd, adView);
//
//                                        frameLayout.removeAllViews();
//                                        frameLayout.addView(adView);
//                                    }
//                                }).build();
//
//
//                        loadNativeAds(adLoader);


                    }
                }
            });

            /***********************spinner work*******************************/
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.WhatsappChooser, android.R.layout.simple_spinner_item);
            //     Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //    Apply the adapter to the spinner
            spinner.setAdapter(adapter);


            selection = "Whatsapp";
            list = new ArrayList<>();

            if (isInstalled(getContext(), "com.whatsapp") && !isInstalled(getContext(), "com.whatsapp.w4b")) {
                selection = "Whatsapp";
                spinner.setVisibility(View.GONE);
            } else if (!isInstalled(getContext(), "com.whatsapp") && isInstalled(getContext(), "com.whatsapp.w4b")) {
                selection = "Whatsapp Business";
                spinner.setVisibility(View.GONE);
            }


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selection = spinner.getSelectedItem().toString();


                    if (notFirstTime==2) {

                        refreshFiles();
                        Log.i("status","getData Spinner");
//                        if (contentType.equals("downloadedStatus"))
//                        {
//                            if (!NoStoragePermission()) {
//                                getDownloadedStatus();
//                            }else {
//                                requestPermissions(PERMISSIONS,READ_STORAGE_ANDROID11);
//
//                            }
//                        }else if (!arePermissionDenied())
//                        {
//
//                            if (contentType.equals("downloadedStatus")) {
//                                getDownloadedStatus();
//
//                            }else {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//
//                                    executeNew();
//
//                                } else
//                                {
//
//                                    getdata();
//
//                                }
//                            }
//                        }
                    }else
                    {
                        Log.i("status","not gotData Spinner");
                        notFirstTime=2;
                    }





                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            /***********************refresh work*******************************/


            refresh = v.findViewById(R.id.swipLayout);


            refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshFiles();

//                    if (contentType.equals("downloadedStatus"))
//                    {
//                        if (!NoStoragePermission()) {
//                            getDownloadedStatus();
//                        }else {
//                            requestPermissions(PERMISSIONS,READ_STORAGE_ANDROID11);
//
//                        }
//                    }else if (!arePermissionDenied())
//                    {
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//
//                            executeNew();
//
//                        } else  {
//
//                            getdata();
//
//                        }
//                    }

                    refresh.setRefreshing(false);

                }
            });

            /***********************Permission work*******************************/




            v.findViewById(R.id.grantPermission).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                        requestPermissionQ();

                    }else
                    {
                        requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
                    }

                }
            });


            allStatus.setChecked(true);



        }
        return v;
    }

//    private void loadNativeAds(AdLoader adLoader) {
//        AdRequest adRequest=new AdRequest.Builder().build();
//        adLoader.loadAd(adRequest);
//    }

    private void askPermission() {

        v.findViewById(R.id.permissionLayout).setVisibility(View.VISIBLE);
        v.findViewById(R.id.mediaCard).setVisibility(View.GONE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            v.findViewById(R.id.permissionImage).setVisibility(View.VISIBLE);

        }else {
            v.findViewById(R.id.permissionImage).setVisibility(View.GONE);
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestPermissionQ() {
        StorageManager sm = (StorageManager) getContext().getSystemService(Context.STORAGE_SERVICE);

        Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
        String startDir ;
        if (selection.equals("Whatsapp"))
        {
            startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
        }else {
            startDir = "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses";
        }
        //Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses (whatsapp)
        //Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses (whatsapp business)

        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

        String scheme = uri.toString();
        scheme = scheme.replace("/root/", "/document/");
        scheme += "%3A" + startDir;

        uri = Uri.parse(scheme);

        Log.d("URI", uri.toString());

        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        activityResultLauncher.launch(intent);
        Log.i("status","requestPermissionQ");


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("status","videogetdata called2");

        if (requestCode == READ_STORAGE_ANDROID11 )
        {
            if(NoStoragePermission())
            {
                allStatus.setChecked(true);
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();
            }else {
                v.findViewById(R.id.permissionLayout).setVisibility(View.GONE);
                refreshFiles();
                //  getDownloadedStatus();

                Toast.makeText(getContext(), "Permission Accepted", Toast.LENGTH_LONG).show();


            }



        }

        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0)
        {
            if (arePermissionDenied())
            {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();
            }else {
                Log.i("status","videogetdata called2 3 "+grantResults.length);
                refreshFiles();
                // getdata();
                Toast.makeText(getContext(), "Permission Accepted", Toast.LENGTH_LONG).show();


            }

        }


    }

    final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent data = result.getData();

                    assert data != null;

                    Log.d("HEY: ", data.getData().toString());

                    getActivity().getContentResolver().takePersistableUriPermission(
                            data.getData(),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    notFirstTime=2;
                    v.findViewById(R.id.permissionLayout).setVisibility(View.GONE);
                    v.findViewById(R.id.mediaCard).setVisibility(View.VISIBLE);
                    refreshFiles();
                    // executeNew();


                    Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                }
            }
    );

    int indexInPermissionList=0;

    private boolean arePermissionDenied() {

        String identifier;
        if (selection.equals("Whatsapp"))
        {
            identifier="app%2F";
        }else {
            identifier="w4b%2F";
        }
        List<UriPermission> list = getActivity().getContentResolver().getPersistedUriPermissions();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            boolean isGot=true;
            for (int i=0;i<list.size();i++)
            {
                if (list.get(i).getUri().toString().contains(identifier)
                        && list.get(i).getUri().toString().contains("Statuses"))
                {
                    Log.i("status","permission Uri "+list.get(i).getUri());
                    indexInPermissionList=i;
                    i("permission granted");
                    isGot=false; // already got the permission
                    break;
                }
            }
            Log.i("arePermissionDenied",identifier+" indexInPermissionList "+indexInPermissionList+"  "+getActivity().getContentResolver().getPersistedUriPermissions().size());

            if (isGot)
            {
                askPermission();
            }else {
                v.findViewById(R.id.permissionLayout).setVisibility(View.GONE);
                v.findViewById(R.id.mediaCard).setVisibility(View.VISIBLE);
            }
            return isGot;

            // return getContentResolver().getPersistedUriPermissions().size() <= 0;
        }

        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getContext(), permissions) != PackageManager.PERMISSION_GRANTED) {
                askPermission();
                return true;
            }
        }
        v.findViewById(R.id.permissionLayout).setVisibility(View.GONE);
        v.findViewById(R.id.mediaCard).setVisibility(View.VISIBLE);
        return false;
    }

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

    private void refreshFiles() {


        String[] mType={"Image","Video"};

        try {



            RecyclerView
                    imageRV=v.findViewById(R.id.imageRV),
                    videoRV=v.findViewById(R.id.recycleVideo);




            if (contentType.equals("downloadedStatus"))
            {
                if (!NoStoragePermission()) {
                    getDownloadedStatus(mType[0],imageRV);
                    getDownloadedStatus(mType[1],videoRV);
                }
            }else {
                if (!arePermissionDenied())
                {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                        executeNew(mType[0],imageRV);
                        executeNew(mType[1],videoRV);

                    } else  {

                        getdata(mType[0],imageRV);
                        getdata(mType[1],videoRV);

                    }
                }
            }





        }catch (Exception e)
        {
            Log.e(getContext().toString(),e.getMessage());
        }
    }


    @SuppressLint("StaticFieldLeak")
    private void getDownloadedStatus(String mediaType, RecyclerView recyclerView) {
        try {
            refresh.setRefreshing(true);
            LinearLayoutManager horizontalLM=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
            recyclerView.setLayoutManager(horizontalLM);


            new AsyncTask<String, String, String>() {
                ArrayList<whatsappStatusModel> list=new ArrayList<>();
                whatsappStatusModel model;
                @Override
                protected String doInBackground(String... strings) {



                    String targetPath = "";
                    File[] tergetFileList = null;
                    // Toast.makeText(getContext(), ""+selection, Toast.LENGTH_SHORT).show();

                    //Toast.makeText(getContext(), "Whatsapp "+selection, Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    {
                        targetPath=Environment.getExternalStorageDirectory()+File.separator +
                                "Download/"+getContext().getString(R.string.app_name);
                        //Environment.DIRECTORY_DOWNLOADS+"/GBWhats";
                    }
                    else {
                        targetPath = Environment.getExternalStorageDirectory() + File.separator +
                                "Download//"+getContext().getString(R.string.app_name);
                    }



                    File targetFile=new File(targetPath);


                    if(targetFile.exists()){
                        Log.i("status"," targetFile.exists()");


                        tergetFileList=targetFile.listFiles();

                        Arrays.sort(tergetFileList,(o1,o2)->

                        {if (o1.lastModified()>o2.lastModified())
                            return -1;

                        else if (o1.lastModified()<o2.lastModified())
                            return +1;

                        else return 0;



                        });

                        for (File file : tergetFileList) {

                            if (mediaType.equals("Image"))
                            {
                                try {
                                    if ((Objects.requireNonNull(file.getName())).endsWith(".png") ||
                                            (Objects.requireNonNull(file.getName())).endsWith(".jpg")) {


                                        model = new whatsappStatusModel(getFileName(Uri.fromFile(file)), Uri.fromFile(file)
                                                , file.getName(), file.getAbsolutePath());
                                        list.add(model);
                                    }
                                } catch (Exception e) {
                                    Log.e("imageStatus", e.getMessage());
                                }

                            } else
                            {

                                if ((Objects.requireNonNull(file.getName())).endsWith(".mp4")) {
                                    model = new whatsappStatusModel(getFileName(Uri.fromFile(file)), Uri.fromFile(file)
                                            , file.getName(), file.getAbsolutePath());
                                    list.add(model);
                                }}


                        }}


                    return null;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    if (!list.isEmpty()) {

                        recyclerView.setVisibility(View.VISIBLE);
                        adopter = new com.affixstudio.whatsapptool.adopterOur.adopter(list, getActivity(),StatusFragment.this,v.findViewById(R.id.mainLayout));
                        recyclerView.setAdapter(adopter);
                        if (mediaType.equals("Image"))
                        {
                            v.findViewById(R.id.showImageLA).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.noImageLA).setVisibility(View.GONE);
                        }else if (mediaType.equals("Video"))
                        {
                            v.findViewById(R.id.showVideoLA).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.noVideoLA).setVisibility(View.GONE);
                        }
                    } else
                    {
                        Log.i("status","list.size "+list.size());

                        if (mediaType.equals("Image"))
                        {
                            v.findViewById(R.id.showImageLA).setVisibility(View.GONE);
                            v.findViewById(R.id.noImageLA).setVisibility(View.VISIBLE);
                        }else if (mediaType.equals("Video"))
                        {
                            v.findViewById(R.id.showVideoLA).setVisibility(View.GONE);
                            v.findViewById(R.id.noVideoLA).setVisibility(View.VISIBLE);
                        }

                    }
                    refresh.setRefreshing(false);
                    super.onPostExecute(s);
                }
            }.execute();

//            Runnable post=new Runnable() {
//                @Override
//                public void run() {
//
//
//                    ArrayList<whatsappStatusModel> list=new ArrayList<>();
//                    whatsappStatusModel model;
//                    String targetPath = "";
//                    File[] tergetFileList = null;
//                    // Toast.makeText(getContext(), ""+selection, Toast.LENGTH_SHORT).show();
//
//                    //Toast.makeText(getContext(), "Whatsapp "+selection, Toast.LENGTH_SHORT).show();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
//                    {
//                        targetPath=Environment.getExternalStorageDirectory()+File.separator +
//                                "Download/GBWhats";
//                        //Environment.DIRECTORY_DOWNLOADS+"/GBWhats";
//                    }
//                    else {
//                        targetPath = Environment.getExternalStorageDirectory() + File.separator +
//                                "Download/GBWhats";
//                    }
//
//
//
//                    File targetFile=new File(targetPath);
//
//
//                    if(targetFile.exists()){
//                        Log.i("status"," targetFile.exists()");
//
//
//                        tergetFileList=targetFile.listFiles();
//
//                        Arrays.sort(tergetFileList,(o1,o2)->
//
//                        {if (o1.lastModified()>o2.lastModified())
//                            return -1;
//
//                        else if (o1.lastModified()<o2.lastModified())
//                            return +1;
//
//                        else return 0;
//
//
//
//                        });
//
//                        for (File file : tergetFileList) {
//
//                            if (mediaType.equals("Image"))
//                            {
//                                try {
//                                    if ((Objects.requireNonNull(file.getName())).endsWith(".png") ||
//                                            (Objects.requireNonNull(file.getName())).endsWith(".jpg")) {
//
//
//                                        model = new whatsappStatusModel(getFileName(Uri.fromFile(file)), Uri.fromFile(file)
//                                                , file.getName(), file.getAbsolutePath());
//                                        list.add(model);
//                                    }
//                                } catch (Exception e) {
//                                    Log.e("imageStatus", e.getMessage());
//                                }
//
//                            } else
//                            {
//
//                                if ((Objects.requireNonNull(file.getName())).endsWith(".mp4")) {
//                                    model = new whatsappStatusModel(getFileName(Uri.fromFile(file)), Uri.fromFile(file)
//                                            , file.getName(), file.getAbsolutePath());
//                                    list.add(model);
//                                }}
//
//
//                        }
//
//                        Log.i("status","list size in download"+list.size());
//                        v.findViewById(R.id.noImage).setVisibility(View.GONE);
//                        recyclerView.setVisibility(View.GONE);
//
//                        v.findViewById(R.id.noStatusLayout).setVisibility(View.GONE);
//                        v.findViewById(R.id.noVideos).setVisibility(View.GONE);
//                        if (!list.isEmpty())
//                        {
//                            recyclerView.setVisibility(View.VISIBLE);
//                            adopter=new com.affixstudio.whatsapptool.adopterOur.adopter(list,getActivity(),StatusFragment.this,v.findViewById(R.id.layout));
//                            recyclerView.setAdapter(adopter);
//                            v.findViewById(R.id.noDownloaded).setVisibility(View.GONE);
//                        }else
//                        {
//                            recyclerView.setVisibility(View.GONE);
//                            v.findViewById(R.id.noDownloaded).setVisibility(View.VISIBLE);
//
//                        }
//
//                    }
//                    //Do something
//
//                    if (!list.isEmpty()) {
//
//                        recyclerView.setVisibility(View.VISIBLE);
//                        adopter = new com.affixstudio.whatsapptool.adopterOur.adopter(list, getActivity(),StatusFragment.this,v.findViewById(R.id.mainLayout));
//                        recyclerView.setAdapter(adopter);
//                        if (mediaType.equals("Image"))
//                        {
//                            v.findViewById(R.id.showImageLA).setVisibility(View.VISIBLE);
//                            v.findViewById(R.id.noImageLA).setVisibility(View.GONE);
//                        }else if (mediaType.equals("Video"))
//                        {
//                            v.findViewById(R.id.showVideoLA).setVisibility(View.VISIBLE);
//                            v.findViewById(R.id.noVideoLA).setVisibility(View.GONE);
//                        }
//                    } else
//                    {
//                        Log.i("status","list.size "+list.size());
//
//                        if (mediaType.equals("Image"))
//                        {
//                            v.findViewById(R.id.showImageLA).setVisibility(View.GONE);
//                            v.findViewById(R.id.noImageLA).setVisibility(View.VISIBLE);
//                        }else if (mediaType.equals("Video"))
//                        {
//                            v.findViewById(R.id.showVideoLA).setVisibility(View.GONE);
//                            v.findViewById(R.id.noVideoLA).setVisibility(View.VISIBLE);
//                        }
//
//                    }
//                    refresh.setRefreshing(false);
//                }
//            };
//            v.postDelayed(post,10);
//            refresh.setRefreshing(false);
        }catch (Exception e)
        {
            Log.e("downloaded",e.getMessage());
        }
    }

    private void getdata(String mediaType,RecyclerView recyclerView) {
        refresh.setRefreshing(true);
        LinearLayoutManager horizontalLM=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(horizontalLM);
        Runnable post=new Runnable() {
            @Override
            public void run() {
                ArrayList<whatsappStatusModel> list=new ArrayList<>();

                whatsappStatusModel model;
                String targetPath="";
                File[] tergetFileList=null;
                //Toast.makeText(getContext(), ""+selection, Toast.LENGTH_SHORT).show();
                if (selection.equals("Whatsapp")) {
                    // Toast.makeText(getContext(), "Whatsapp "+selection, Toast.LENGTH_SHORT).show();
                    targetPath = Environment.getExternalStorageDirectory() + File.separator +
                            "/whatsapp/media/.Statuses";
                }else {
                    targetPath =Environment.getExternalStorageDirectory() + File.separator +
                            "/whatsapp Business/media/.Statuses";
                }
                File targetFile = new File(targetPath);




                Log.i("status","targetFile.exists() "+targetFile.exists());
                if (targetFile.exists()) {


                    tergetFileList = targetFile.listFiles();
                    Arrays.sort(tergetFileList, (o1, o2) ->
                    {
                        if (o1.lastModified() > o2.lastModified())
                            return -1;
                        else if (o1.lastModified() < o2.lastModified())
                            return +1;
                        else return 0;


                    });

                    for (File file : tergetFileList) {
                        if (mediaType.equals("Image"))
                        {
                            try {
                                if (Uri.fromFile(file).toString().endsWith(".png") ||
                                        Uri.fromFile(file).toString().endsWith(".jpg")) {
                                    model = new whatsappStatusModel(getFileName(Uri.fromFile(file)), Uri.fromFile(file)
                                            , file.getName(), file.getAbsolutePath());
                                    list.add(model);
                                }
                            } catch (Exception e) {
                                Log.e("imageStatus", e.getMessage());
                            }

                        } else if (mediaType.equals("Video"))
                        {
                            if (Uri.fromFile(file).toString().endsWith(".mp4")) {
                                model = new whatsappStatusModel(getFileName(Uri.fromFile(file)), Uri.fromFile(file)
                                        , file.getName(), file.getAbsolutePath());
                                list.add(model);
                            }
                        }
                    }
                }



                Log.i("status","list.size "+list.size());
                if (!list.isEmpty()) {

                    recyclerView.setVisibility(View.VISIBLE);
                    adopter = new com.affixstudio.whatsapptool.adopterOur.adopter(list, getActivity(),StatusFragment.this,v.findViewById(R.id.mainLayout));
                    recyclerView.setAdapter(adopter);
                    if (mediaType.equals("Image"))
                    {
                        v.findViewById(R.id.showImageLA).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.noImageLA).setVisibility(View.GONE);
                    }else if (mediaType.equals("Video"))
                    {
                        v.findViewById(R.id.showVideoLA).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.noVideoLA).setVisibility(View.GONE);
                    }
                } else
                {
                    Log.i("status","list.size "+list.size());

                    if (mediaType.equals("Image"))
                    {
                        v.findViewById(R.id.showImageLA).setVisibility(View.GONE);
                        v.findViewById(R.id.noImageLA).setVisibility(View.VISIBLE);
                    }else if (mediaType.equals("Video"))
                    {
                        v.findViewById(R.id.showVideoLA).setVisibility(View.GONE);
                        v.findViewById(R.id.noVideoLA).setVisibility(View.VISIBLE);
                    }

                }

                refresh.setRefreshing(false);
            }
        };
        v.postDelayed(post,10);
        refresh.setRefreshing(false);

    }


    whatsappStatusModel model;


    private void executeNew(String mediaType,RecyclerView recyclerView) {

        refresh.setRefreshing(true);
        LinearLayoutManager horizontalLM=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(horizontalLM);
        i("executeNew");

        Executors.newSingleThreadExecutor().execute(() -> {




            List<UriPermission> list1 = requireActivity().getContentResolver().getPersistedUriPermissions();

            DocumentFile file1 = DocumentFile.fromTreeUri(requireActivity(), list1.get(indexInPermissionList).getUri());





            if (file1 == null) {


                i("file==null");
                return ;
            }
            ArrayList<whatsappStatusModel> list=new ArrayList<>();
            //  list.clear();

            DocumentFile[] tergetFileList = file1.listFiles();

            i("media Type "+mediaType);
            for (DocumentFile file : tergetFileList) {
                if (mediaType.equals("Image"))
                {
                    try {
                        if ((Objects.requireNonNull(file.getName())).endsWith(".png") ||
                                (Objects.requireNonNull(file.getName())).endsWith(".jpg")) {


                            model = new whatsappStatusModel(file.getName(), file.getUri()
                                    , file.getName(), file.getUri().toString());

                            list.add(model);
                        }
                    } catch (Exception e) {
                        Log.e("imageStatus", e.getMessage());
                    }

                } else
                {

                    if ((Objects.requireNonNull(file.getName())).endsWith(".mp4")) {
                        model = new whatsappStatusModel(file.getName(), file.getUri()
                                , file.getName(), file.getUri().toString());
                        list.add(model);
                    }}


            }//*/

            i("execute list size "+list.size());
            v.post(() -> {

                if (!list.isEmpty()) {

                    recyclerView.setVisibility(View.VISIBLE);
                    adopter = new com.affixstudio.whatsapptool.adopterOur.adopter(list, getActivity(),StatusFragment.this,v.findViewById(R.id.mainLayout));
                    recyclerView.setAdapter(adopter);
                    if (mediaType.equals("Image"))
                    {
                        v.findViewById(R.id.showImageLA).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.noImageLA).setVisibility(View.GONE);
                    }else if (mediaType.equals("Video"))
                    {
                        v.findViewById(R.id.showVideoLA).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.noVideoLA).setVisibility(View.GONE);
                    }
                } else
                {
                    Log.i("status","list.size "+list.size());

                    if (mediaType.equals("Image"))
                    {
                        v.findViewById(R.id.showImageLA).setVisibility(View.GONE);
                        v.findViewById(R.id.noImageLA).setVisibility(View.VISIBLE);
                    }else if (mediaType.equals("Video"))
                    {
                        v.findViewById(R.id.showVideoLA).setVisibility(View.GONE);
                        v.findViewById(R.id.noVideoLA).setVisibility(View.VISIBLE);
                    }

                }
                refresh.setRefreshing(false);
            });
        });








    }


    void i(String s)
    {
        Log.i("status",s);
    }





    int deletedInDownload=0;// indicating that a file has been deleted from download tab and to refresh the layout its helping
    @Override
    public void onItemEdit(int position, String title) {
        deletedInDownload=1;


        refreshFiles();
        //   downloadedStatus.setChecked(true); //updating downloaded layout after deleting
        // Toast.makeText(getContext(), ""+"working", Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
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
        return result;
    }

    public  boolean isInstalled( Context context,String packageName)
    {

        PackageManager packageManager = getContext().getPackageManager();
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


    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        CustomSpinnerItem item=(CustomSpinnerItem) adapterView.getSelectedItem();
//        Toast.makeText(this,item.getSpinnerItemName(), Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> adapterView) {

    }




}