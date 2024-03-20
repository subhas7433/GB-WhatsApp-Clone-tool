package com.affixstudio.whatsapptool.adopterOur;

import static com.affixstudio.whatsapptool.fragmentsOur.DecorationText.Adepter.isInstalled;
import static com.affixstudio.whatsapptool.modelOur.sound.playSound;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.modelOur.StatusSaverUtill;
import com.affixstudio.whatsapptool.modelOur.whatsappStatusModel;
import com.affixstudio.whatsapptool.playPrivateMedia;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class adopter extends RecyclerView.Adapter<adopter.viewholder> {

    private ArrayList<whatsappStatusModel> list;
    private String saveFilePath; // getting destination folder means myStorySever
    private LayoutInflater inflater;
    private Context context;
    LinearLayout layout;
    String appName;
    String folderNameInDownload="WAKit%20Prime";
    com.affixstudio.whatsapptool.modelOur.setOnRecycleClick setOnRecycleClick;

    public adopter(ArrayList<whatsappStatusModel> list, Context context, com.affixstudio.whatsapptool.modelOur.setOnRecycleClick setOnRecycleClick,
                   LinearLayout layout) {
        this.list = list;
        this.context = context;
        this.setOnRecycleClick = setOnRecycleClick;
        this.layout = layout;
        saveFilePath= StatusSaverUtill.detectPath(context)+"";
        appName=context.getString(R.string.app_name);
        folderNameInDownload=appName.replace(" ","%20");
    }
    SharedPreferences sp;
    SharedPreferences.Editor spEdit;
    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (inflater==null)
            inflater=LayoutInflater.from(parent.getContext());
        sp= context.getSharedPreferences("affix",Context.MODE_PRIVATE);
        spEdit=sp.edit();
        return new viewholder(inflater.inflate( R.layout.status_fullview_recycle,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, @SuppressLint("RecyclerView") int position) {


        whatsappStatusModel item=list.get(position);
        if (item.getUri().toString().endsWith(".mp4"))
            holder.play.setVisibility(View.VISIBLE);
        else holder.play.setVisibility(View.GONE);

        Glide.with(context).load(item.getPath()).into(holder.imageView);

        int mediaType=0; // image

        String fileExtention="";
        if (item.getUri().toString().endsWith(".mp4"))
        {
            mediaType=1; // means video
            fileExtention=".mp4";

        }else if (item.getUri().toString().endsWith(".jpg"))
        {
            fileExtention=".jpg";
        }
        else
            fileExtention=".png";


        int finalMediaType = mediaType;






        

        if (item.getUri().toString().contains(folderNameInDownload)) //if its calling from downloaded then show delete button
        {
            holder.download.setVisibility(View.GONE);  ///now have to show downloaded button
            holder.delete.setVisibility(View.VISIBLE);
        }

        File savedfile= new File (saveFilePath+"/"+item.getName());
        if (savedfile.exists() && !item.getUri().toString().contains(folderNameInDownload) ) //if file is already then show downloaded icon
        {
            Log.i("status","item.getUri().toString() = "+item.getUri().toString());
            holder.download.setVisibility(View.GONE);  ///now have to show downloaded button
            holder.downloaded.setVisibility(View.VISIBLE);

        }
        holder.downloaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "File is already downloaded. Check the downloaded section.", Toast.LENGTH_LONG).show();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    File file=new File(item.getUri().getPath());
                    new AlertDialog.Builder(context).setMessage("Do you really want delete this file?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (file.exists())
                                    {
                                        Log.i("adepter","file "+file.getPath());
                                        if (file.delete()){
                                            Log.i("adepter","Deleted ");
                                            setOnRecycleClick.onItemEdit(position, "");
                                            Toast.makeText(context, "File is deleted.", Toast.LENGTH_SHORT).show();
                                        }else {
                                            new AlertDialog.Builder(context).setMessage("To delete this file Open File manager. Then go to 'Downloads/" +appName+ "'.")
                                                    .setTitle("Delete Failed")
                                                    .setCancelable(false)
                                                    .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                        }
                                                    }).show();
                                        }
                                    }


                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();

                }catch (Exception e)
                {
                    Log.e("adepter","delete errror "+e.getMessage());
                }

            }
        });

        try{


            String finalFileExtention = fileExtention;
            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //   context.startActivity(new Intent(context, chat_new.class).putExtra(Intent.EXTRA_STREAM,item.getUri()));

                    Log.i("adepterStatus","downloadClick");
                    if (sp.getBoolean("statusDownloadPermission",false))
                    {
                        Log.i("adepterStatus","sp.getBoolean(\"statusDownloadPermission\",false)");


                        downLoadFile(item, holder);

                    }else
                    {
                        Log.i("adepterStatus","sp.getBoolean(\"statusDownloadPermission\",false)");


                        new AlertDialog.Builder(context)
                                .setMessage("I have Permission to download and share of")
                                .setTitle("Authority")
                                .setPositiveButton("Nothing", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).setPositiveButton("This Status", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {


                                        downLoadFile(item, holder);
                                    }
                                }).setNegativeButton("All Statuses", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        spEdit.putBoolean("statusDownloadPermission",true);
                                        spEdit.apply();
                                        downLoadFile(item, holder);
                                    }
                                }).show();
                    }
                    //*///


                }
            });

            String fileType=null;

            if (fileExtention.endsWith(".jpg") || fileExtention.endsWith(".png"))
            {
                fileType="image/*";
            }else
            {
                fileType="video/*";
            }

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            item.getUri().getPath();
            File fileWithinMyDir = new File(item.getUri().getPath());
            String finalFileType = fileType;
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (sp.getBoolean("statusDownloadPermission",false))
                    {
                        shareFile(intentShareFile,finalFileType,item,fileWithinMyDir);
                    }else {
                        new AlertDialog.Builder(context)
                                .setMessage("I have Permission to download and share of")
                                .setTitle("Authority")
                                .setPositiveButton("Nothing", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).setPositiveButton("This Status", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        shareFile(intentShareFile,finalFileType,item,fileWithinMyDir);
                                    }
                                }).setNegativeButton("All Status", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        spEdit.putBoolean("statusDownloadPermission",true);
                                        spEdit.apply();
                                        shareFile(intentShareFile,finalFileType,item,fileWithinMyDir);
                                    }
                                }).show();
                    }


                }
            });


            Intent shareFileWithURI =new Intent(Intent.ACTION_SEND);
            shareFileWithURI.setType(finalFileType);
            if (isApi30())
            {
                Uri uri=item.getUri();
                if (item.getUri().toString().contains(folderNameInDownload))
                {
                    uri=Uri.parse(item.getPath());
                }

                shareFileWithURI.putExtra(Intent.EXTRA_STREAM, uri);
            }else {
                shareFileWithURI.putExtra(Intent.EXTRA_STREAM, Uri.parse(item.getPath()));
            }


            holder.whatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!isInstalled(context,"com.whatsapp"))
                    {
                        showSnackBar("Whatsapp is not installed",R.color.red);
                    }else
                    {
                        if (sp.getBoolean("statusDownloadPermission",false))
                        {



                            // intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                            shareFileWithURI.setPackage("com.whatsapp");
                            context.startActivity(shareFileWithURI);
                        }else {
                            new AlertDialog.Builder(context)
                                    .setMessage("I have Permission to download and share of")
                                    .setTitle("Authority")
                                    .setPositiveButton("Nothing", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).setPositiveButton("This Status", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {


                                            shareFileWithURI.setPackage("com.whatsapp");
                                            context.startActivity(shareFileWithURI);
                                        }
                                    }).setNegativeButton("All Statuses", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            spEdit.putBoolean("statusDownloadPermission", true);
                                            spEdit.apply();


                                            shareFileWithURI.setPackage("com.whatsapp");
                                            context.startActivity(shareFileWithURI);
                                        }
                                    }).show();

                        }





                    }



                }
            });

            holder.whatsappBsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!isInstalled(context,"com.whatsapp.w4b"))
                    {
                        showSnackBar("Whatsapp Business is not installed",R.color.red);
                    }else
                    {
                        if (sp.getBoolean("statusDownloadPermission",false))
                        {


                            // intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                            shareFileWithURI.setPackage("com.whatsapp.w4b");
                            context.startActivity(shareFileWithURI);
                        }else {
                            new AlertDialog.Builder(context)
                                    .setMessage("I have Permission to download and share of")
                                    .setTitle("Authority")
                                    .setPositiveButton("Nothing", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).setPositiveButton("This Status", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {



                                            // shareFileWithURI.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                                            shareFileWithURI.setPackage("com.whatsapp.w4b");
                                            context.startActivity(shareFileWithURI);
                                        }
                                    }).setNegativeButton("All Statuses", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            spEdit.putBoolean("statusDownloadPermission", true);
                                            spEdit.apply();
                                            shareFileWithURI.setType(finalFileType);
                                            shareFileWithURI.putExtra(Intent.EXTRA_STREAM, Uri.parse(item.getPath()));


                                            // intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");


                                            shareFileWithURI.setPackage("com.whatsapp.w4b");
                                            context.startActivity(shareFileWithURI);
                                        }
                                    }).show();

                        }





                    }




                }
            });





            String path;
            if (isApi30())
            {
                path=item.getUri().getPath();
            }else {
                path=item.getPath();
            }


            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {



                        Intent in=new Intent(context, playPrivateMedia.class);
                        in.putExtra("mediaType", finalMediaType);
                        in.putExtra("uri",item.getUri().toString());
                        in.putExtra(context.getString(R.string.isFromOtherScreenPlayPrivateMedia),true);

                        context.startActivity(in);







                    }
                    catch (Exception e)
                    {
                        Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                    }

                }
            });






        }catch (Exception e)
        {
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }

    }


    private void shareFile(Intent intentShareFile,String finalFileType,whatsappStatusModel item,File fileWithinMyDir) {


        if(fileWithinMyDir.exists() && !isApi30()) {

            intentShareFile.setType(finalFileType);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(item.getPath()));



            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

            context.startActivity(Intent.createChooser(intentShareFile, "Share File"));

        }else if (isApi30())
        {
            Uri uri=item.getUri();
            if (item.getUri().toString().contains(folderNameInDownload))
            {
                uri=Uri.parse(item.getPath());
            }
            intentShareFile.setType(finalFileType);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);


            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

            context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
        else

        {
            Toast.makeText(context, "File does not exist please refresh the screen.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isApi30() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return true;

        return false;
    }

    void showSnackBar(String message,int color)
    {
        if (color==R.color.red)
        {
            playSound(context,R.raw.error_sound);
        }
        Snackbar.make(layout,message, BaseTransientBottomBar.LENGTH_SHORT)
                .setTextColor(ContextCompat.getColor(context,R.color.white))
                .setBackgroundTint(ContextCompat.getColor(context, color)).show();
    }
    private void downLoadFile(whatsappStatusModel item, viewholder holder) {
        ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Downloading..");
        pd.setCancelable(false);
        // pd.show();

        Thread thread =new Thread(new Runnable() {
            @Override
            public void run() {
                StatusSaverUtill.createFolder(context);







                if (isApi30())
                {
                    InputStream inputStream = null;
                    try {
                        inputStream = context.getContentResolver().openInputStream(item.getUri());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                    try (

                            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                            FileOutputStream out = new FileOutputStream(new File(saveFilePath+"/"+item.getName()))
                    )

                    {

                        int nRead;
                        byte[] data = new byte[16384]; // A buffer size of 16KB

                        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, nRead);
                        }

                        buffer.flush();
                        byte[] recordData = buffer.toByteArray();
                        out.write(recordData);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    pd.dismiss();
//                    try {
//
//
//
//
//                        byte[] recordData = IOUtils.toByteArray(inputStream); //https://stackoverflow.com/questions/2436385/android-getting-from-a-uri-to-an-inputstream-to-a-byte-array
//                        //https://stackoverflow.com/questions/2091454/byte-to-inputstream-or-outputstream
//                        FileOutputStream out = new FileOutputStream(saveFilePath+"/"+item.getName());
//
//
//                        out.write(recordData);
//
//                        IOUtils.closeQuietly(out);
//                        pd.dismiss();
////
//
//                    } catch (IOException e) {
//                        pd.dismiss();
//                        e.printStackTrace();
//                    }

                }else {



                    File sourceLocation= new File(item.getPath());
                    File targetLocation= new File(saveFilePath+"/"+item.getName());

                    Log.i("statusAdopter",targetLocation+"                "+item.getPath());

                    InputStream in = null;
                    OutputStream out=null;
                    try {
                        in = new FileInputStream(sourceLocation);
                        out = new FileOutputStream(targetLocation);
                    } catch (Exception e) {
                        Log.i("statusAdopter",""+e.getMessage());


                    }
                    Log.i("statusAdopter","in ="+in+" out= "+out);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len=0;
                    while (true) {
                        try {
                            if (!((len = in.read(buf)) > 0)) break;
                            out.write(buf, 0, len);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    pd.dismiss();
                    try {
                        in.close();
                        out.close();
                        pd.dismiss();

                    } catch (IOException e) {
                        e.printStackTrace();
                        pd.dismiss();
                    } //*/
                }





            }
        });
        thread.start();
        holder.download.setVisibility(View.GONE);  ///now have to show downloaded button
        holder.downloaded.setVisibility(View.VISIBLE);
        Toast.makeText(context, "Saved to "+saveFilePath, Toast.LENGTH_LONG).show();
        // Toast.makeText(context, "Saved to "+item.getPath(), Toast.LENGTH_SHORT).show();




    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class viewholder extends RecyclerView.ViewHolder {

        ImageView imageView,play,share,whatsapp,whatsappBsIcon,download,downloaded, thumbnail,delete;





        public viewholder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.images);
            play=itemView.findViewById(R.id.play);
            download=itemView.findViewById(R.id.downloadIcon);
            share=itemView.findViewById(R.id.shareIcon);
            whatsapp=itemView.findViewById(R.id.whatsappIcon);
            delete=itemView.findViewById(R.id.delete);

            downloaded=itemView.findViewById(R.id.downloaded);

            whatsappBsIcon=itemView.findViewById(R.id.whatsappBsIcon);







        }
    }


}
