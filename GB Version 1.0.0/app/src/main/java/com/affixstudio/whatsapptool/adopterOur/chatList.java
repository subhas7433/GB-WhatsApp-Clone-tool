package com.affixstudio.whatsapptool.adopterOur;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.induvidualChat;
import com.affixstudio.whatsapptool.ads.AppLovinNative;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class chatList extends RecyclerView.Adapter<chatList.viewholder> {


    LayoutInflater inflater;
    Context context;
    ArrayList<String> names;
    ArrayList<String> messages;
    ArrayList<String> time;
    ArrayList<Integer> unread;
    int fromRecoverMessage=0;
    Activity a;
    public chatList(Context context, ArrayList<String> names, ArrayList<String> messages, ArrayList<String> time, ArrayList<Integer> unreadM, int fromRecoverMessage, Activity a) {
        this.context = context;
        this.names = names;
        this.messages = messages;
        this.time = time;
        this.unread = unreadM;
        this.fromRecoverMessage = fromRecoverMessage;
        this.a = a;
    }

    @NonNull
    @Override
    public chatList.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        inflater=LayoutInflater.from(context);

        return new viewholder(inflater.inflate(R.layout.chat_names,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, @SuppressLint("RecyclerView") int position) {

        //        animation


        if (getItemViewType(position)==0)
        {
            holder.adFrame.setVisibility(View.VISIBLE);
            AppLovinNative lovinNative=new AppLovinNative(R.layout.native_applovin_chat_small,a );

           // lovinNative.small(holder.adFrame);





        }




        holder.chatName.setText(names.get(position));
        holder.chatMessage.setText(messages.get(position));



        Log.i("chatlist",time.get(position)+" m");

        holder.chatNameListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                Intent intent=new Intent(context, induvidualChat.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("user",names.get(position));
                if (context.toString().contains("private_chat"))
                {
                    intent.putExtra("fromPrivateChat",true);
                }else {
                    intent.putExtra("fromPrivateChat",false);
                }
                context.startActivity(intent);
                }catch (Exception e)
                {
                    Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        });

      //  setTimeAgo(holder.timeAgo,time.get(index)); //function written in this class

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy");
        Date d1= null;
        Date d2= null;
        try {
            d1 = sdf.parse(time.get(position));
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
        Log.i("chatlist",minutes+" minutes>0 1");
        if (days<9 && days>6)
        {
            holder.timeAgo.setText("A week ago");
        }else if (days>9 )
        {
            holder.timeAgo.setText(time.get(position));
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
            Log.i("chatlist","minutes>0 1");
            long sec=minutes*60;

            if (seconds>sec)
            {   long secDiff=seconds-sec;
                holder.timeAgo.setText(""+minutes+" min "+secDiff+"sec ago");
            }else
            {
                holder.timeAgo.setText(""+minutes+" min ago");
                Log.i("chatlist","minutes>0");
            }
        }else
        {
            holder.timeAgo.setText("Few Moment ago");
        }


        if (fromRecoverMessage==0 && unread.get(position)>0 ) // not from recover means from private chat
        {


            holder.unReadCount.setVisibility(View.VISIBLE);
            holder.unReadCount.setText(""+unread.get(position));

        }
        holder.chatNameListCard.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.chat));




    }

    public static void setTimeAgo(TextView timeAgo,String time ){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy");

        Date d1= null;
        Date d2= null;
        try {
            d1 = sdf.parse(time);
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

        if (days<9 && days>6)
        {
            timeAgo.setText("A week ago");
        }else if (days>9 )
        {
            timeAgo.setText(time);
        }else if (days<=6 && days>0)
        {
            timeAgo.setText(""+days+" day(s) ago");
        }else if (hours>0)
        {
            long min=hours*60;

            if (minutes>min)
            {   long MinDiff=minutes-min;
                timeAgo.setText(""+hours+" h "+MinDiff+"min ago");
            }else
            {
                timeAgo.setText(""+hours+" h ago");
            }

        }else if (minutes>0)
        {
            long sec=minutes*60;

            if (seconds>sec)
            {   long secDiff=seconds-sec;
                timeAgo.setText(""+minutes+" min "+secDiff+"sec ago");
            }else
            {
                timeAgo.setText("Few moment ago");
            }
        }

        Log.i("timeago",""+hours+" "+minutes);


    }


    @Override
    public int getItemViewType(int position) {
       // return 0; // todo make it correct
        if (position%3==0)
        {
            return 0;// show ads
        }else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder{

        TextView chatName,chatMessage,timeAgo,unReadCount;
        CardView chatNameListCard;

        FrameLayout adFrame;
        public viewholder(@NonNull View itemView) {
            super(itemView);



            try {

            adFrame=itemView.findViewById(R.id.ad_frame);
            timeAgo=itemView.findViewById(R.id.timeAgo);
            chatName=itemView.findViewById(R.id.chatName);
            chatMessage=itemView.findViewById(R.id.chatMessage);
            chatNameListCard=itemView.findViewById(R.id.chatNameListCard);
                unReadCount=itemView.findViewById(R.id.unreadCount);

        }catch (Exception e)
        {
            Toast.makeText(itemView.getContext(), ""+e, Toast.LENGTH_SHORT).show();
        }












        }
    }

}
