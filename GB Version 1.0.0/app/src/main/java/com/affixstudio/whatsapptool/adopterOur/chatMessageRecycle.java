package com.affixstudio.whatsapptool.adopterOur;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.private_view_media_small;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

public class chatMessageRecycle extends RecyclerView.Adapter<chatMessageRecycle.viewholder> {


    Context context;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<Integer> isdeleted;

    int mediaType=0; // indicating on click on message text
    boolean isFromPrivateChat=true;



    public chatMessageRecycle(Context context, ArrayList<String> message, ArrayList<String> time,ArrayList<Integer> isdeleted,boolean isFromPrivateChat) {
        this.context = context;
        this.message = message;
        this.time = time;
        this.isdeleted = isdeleted;
        this.isFromPrivateChat = isFromPrivateChat;
    }

    @NonNull
    @Override
    public chatMessageRecycle.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater= LayoutInflater.from(parent.getContext());






        return new viewholder(inflater.inflate(R.layout.message_recycle_design, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        //        animation
        holder.cardMessegeRecycle.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.chat));

        try {


        int index=message.size()-position-1;


        holder.messages.setText(message.get(index));
        //Toast.makeText(context, ""+, Toast.LENGTH_SHORT).show();

        if (isdeleted.get(index)==1)//trying to identify and change deleted message colour
        {
            holder.messages.setTextColor(Color.parseColor("#990f00"));
        }




        if (isMediaMessage(message.get(index)))
        {

            holder.messages.setTextColor(context.getResources().getColor(R.color.colorSecondaryVariant));

            Intent intent=new Intent(context, private_view_media_small.class);
            intent.putExtra("isPrivateActivity",isFromPrivateChat);

            holder.cardMessegeRecycle.setOnClickListener(view -> {
                isMediaMessage(message.get(index));
                intent.putExtra("mediaType",mediaType);
                context.startActivity(intent);
            });

        }









           // String date= DateTimeUtils.formatWithPattern(time.get(index), "dd-M-yyyy hh:mm:ss");
           // setTimeAgo(holder.time,time.get(index)); // function written in chatMessageRecycle.java

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy");
            Date d1= null;
            Date d2= null;
            try {
                d1 = sdf.parse(time.get(index));
                d2=sdf.parse(sdf.format(new Date()));
            } catch (ParseException e) {
                Log.e("time",e.getMessage());
            }

            long diff=0;
            if (d2!=null){
                diff = d2.getTime() - d1.getTime();//as given
            }


            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            Log.i("chatMessageRecycle",minutes+" minutes>0 1");
            if (days<9 && days>6)
            {
                holder.timeAgo.setText("A week ago");
            }else if (days>9 )
            {
                holder.timeAgo.setText(time.get(index));
            }else if (days<=6 && days>0)
            {
                holder.timeAgo.setText(""+days+" day(s) ago");
            }else if (hours>0)
            {
                long min=hours*60;

                if (minutes>min)
                {   long MinDiff=minutes-min;
                    holder.timeAgo.setText(""+hours+" h "+MinDiff+"min ago");
                }else
                {
                    holder.timeAgo.setText(""+hours+" h ago");
                }

            }else if (minutes>0)
            {
                Log.i("chatMessageRecycle","minutes>0 1");
                long sec=minutes*60;

                if (seconds>sec)
                {   long secDiff=seconds-sec;
                    holder.timeAgo.setText(""+minutes+" min "+secDiff+"sec ago");
                }else
                {
                    holder.timeAgo.setText(""+minutes+" min ago");
                    Log.i("chatMessageRecycle","minutes>0");
                }
            }else
            {
                Log.i("chatMessageRecycle","else"+seconds);
                holder.timeAgo.setText("Few Moment ago");
            }




           /* int in=message.size()-1;
            for (int i=0;i<deletedPosition.size();i++)
            {
                if (index!=in)
                {
                    if (deletedPosition.get(i)==index+1)
                    {
                        holder.messages.setTextColor(Color.parseColor("#990f00"));
                    }
                    Toast.makeText(context, "okey", Toast.LENGTH_SHORT).show();
                }

            }*/


       /* if (message.get(index+1).equals("This message was deleted"))
        {

            isdeleted=1;
            holder.cardMessegeRecycle.setVisibility(View.GONE);



        }else
        {
            isdeleted=0;
        }*/
    }catch (Exception e)
    {
        Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
    }

    }

    private boolean isMediaMessage(String s) {

        Log.i("individual","clicked icon is "+s);
        String[] icons=context.getResources().getStringArray(R.array.privateMediaIcon);

        int i=0;
        for (String icon:icons)
        {
            if (s.contains(icon))
            {

                mediaType=i;
                if (mediaType==6)
                {
                    mediaType=1;
                }
                return true;

            }

            i++;
        }



        return false;

    }

    @Override
    public int getItemCount() {
        return message.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {

        TextView messages,timeAgo;
        ConstraintLayout cardMessegeRecycle;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            messages=itemView.findViewById(R.id.recycleMessage);
            timeAgo=itemView.findViewById(R.id.recycleTime);
            cardMessegeRecycle=itemView.findViewById(R.id.cardMessegeRecycle);
        }
    }

}
