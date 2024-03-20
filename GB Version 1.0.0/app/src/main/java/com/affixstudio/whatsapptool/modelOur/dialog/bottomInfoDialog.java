package com.affixstudio.whatsapptool.modelOur.dialog;

import static com.affixstudio.whatsapptool.activityOur.AppTutorials.bottomAdepter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class bottomInfoDialog {


   public void showinfo(Context context,int p,String tegName)
    {

        SharedPreferences sp=context.getSharedPreferences("affix",Context.MODE_PRIVATE);
        SharedPreferences.Editor se=sp.edit();

        boolean isFirstTime=sp.getBoolean(tegName,true);



        String[] list=context.getResources().getStringArray(R.array.list);
        int[] usageList=new int[]{R.array.usefull1,R.array.usefull2,R.array.usefull3,R.array.usefull4,R.array.usefull5,
                R.array.usefull6,R.array.usefull7,R.array.usefull8,R.array.usefull9,R.array.usefull10,
                R.array.usefull11,R.array.usefull12,R.array.usefull13};
        int[] detalisList=new int[]{R.array.Details1,R.array.Details2,R.array.Details3,R.array.Details4,R.array.Details5,
                R.array.Details6,R.array.Details7,R.array.Details8,R.array.Details9,R.array.Details10,
                R.array.Details11,R.array.Details12,R.array.Details13};
        Integer[] usageListIcon={R.drawable.auto_reply_lg,R.drawable.ascii_fc_logo,R.drawable.sms_template_icon
                ,R.drawable.direct_msg_focused,R.drawable.dc_text_icon, R.drawable.quick_sms_focused,R.drawable.restore_sms,R.drawable.download_lg,
                R.drawable.schedule_icon,R.drawable.repeat_icon,R.drawable.double_tick,R.drawable.whats_web_ic,R.drawable.call_blocked_focused};


        BottomSheetDialog bt=new BottomSheetDialog(context);

        if (isFirstTime)
        {

            se.putBoolean(tegName,false).apply();
        }


        View v=View.inflate(context, R.layout.bottom_recycle,null);
        TextView
                pageTitle=v.findViewById(R.id.pageTitle),
                understood=v.findViewById(R.id.understood);

        Button seeVideo=v.findViewById(R.id.seeVideo);
        ImageView screenIcon=v.findViewById(R.id.screenIcon);

        RecyclerView featureListRecycle= v.findViewById(R.id.featureListRecycle);
        RecyclerView detailsListRecycle= v.findViewById(R.id.detailsListRecycle);

        featureListRecycle.setAdapter(bottomAdepter(context.getResources().getStringArray(usageList[p])));
        detailsListRecycle.setAdapter(bottomAdepter(context.getResources().getStringArray(detalisList[p])));


        pageTitle.setText(list[p]);
        
        understood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.dismiss();
            }
        });

        seeVideo.setOnClickListener(view -> {
            tutorialVideo video=new tutorialVideo();
            video.showVideo(p,context,list[p]);
        });

        screenIcon.setImageResource(usageListIcon[p]);



        bt.setContentView(v);
        bt.show();

    }


}
