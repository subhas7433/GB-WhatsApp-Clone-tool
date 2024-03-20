package com.affixstudio.whatsapptool.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.modelOur.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class auto_reply_history extends Fragment {


    RecyclerView recycleAutoHistory;
    LinearLayout noDataLayout;

    ArrayList<autoHistoryInfo> autoHistoryInfos;
    autoHistoryInfo autoHistoryInfo;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         v=inflater.inflate(R.layout.fragment_auto_reply_history, container, false);


        v.findViewById(R.id.testAutoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), com.affixstudio.whatsapptool.activity.autoReplyTest.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(i,b);
            }
        });

        //MobileAds.initialize(getContext());










        IntentFilter filter=new IntentFilter(getString(R.string.autoReplyHistoryBroadCast));
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refresh();
                Log.i("autoHistory","onReceived");
            }
        };

        getActivity().registerReceiver(receiver,filter);

        refresh();






        return v;
    }

    BroadcastReceiver receiver;
    LinearLayout recycleLayout;
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(receiver);

        }catch (Exception e)
        {

            Log.e("e",e.getMessage());
        }
    }
    void refresh()
    {

        String q="CREATE TABLE IF NOT EXISTS "+getString(R.string.autoReplyHistory)+"(_id INTEGER PRIMARY KEY autoincrement, ContactName text DEFAULT 'Not set',ReceivedMessage text DEFAULT 'Not set',Replied text DEFAULT 'Not set',isGroup text DEFAULT 'false',Time text DEFAULT 'Not set') ";
        database db=new database(getContext(),getString(R.string.autoReplyHistory),q,1);

        Cursor c = db.getinfo(1);
        autoHistoryInfos=new ArrayList<>();
        while (c.moveToNext())
        {

            autoHistoryInfo=new autoHistoryInfo(c.getString(1),
                    c.getString(2),c.getString(3),c.getString(5),c.getInt(4)
                    ,c.getInt(0));


            autoHistoryInfos.add(autoHistoryInfo);

        }

        final Context[] context = new Context[1];
        RecyclerView.Adapter adapter=new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                context[0] =parent.getContext();
                return new  RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_auto_reply_history,null)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int p) {


                FrameLayout adFrame=h.itemView.findViewById(R.id.ad_frame);

                if (getItemViewType(p)==0)
                {
                    Log.i("Adepter","ads op "+p);
//                    AdLoader adLoader=new AdLoader.Builder(getContext(),getContext().getString(R.string.nativeAdsId))
//                            .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                                @Override
//                                public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
//                                    adFrame.setVisibility(View.VISIBLE);
//                                    // Assumes you have a placeholder FrameLayout in your View layout
//                                    // (with id fl_adplaceholder) where the ad is to be placed.
//                                    FrameLayout frameLayout =
//                                            adFrame;
//                                    // Assumes that your ad layout is in a file call native_ad_layout.xml
//                                    // in the res/layout folder
//                                    NativeAdView adView = (NativeAdView) LayoutInflater.from(context[0])
//                                            .inflate(R.layout.native_ad_small_auto_history, null);
//                                    // This method sets the text, images and the native ad, etc into the ad
//                                    // view.
//                                    nativeAds nativeClass=new nativeAds();
//                                    nativeClass.populateNativeAdViewSmallChat(nativeAd, adView);
//
//                                    frameLayout.removeAllViews();
//                                    frameLayout.addView(adView);
//                                }
//                            }).build();
//                    AdRequest adRequest=new AdRequest.Builder().build();
//                    adLoader.loadAd(adRequest);
                }


                int index=autoHistoryInfos.size()-1-p;

                TextView chatName=h.itemView.findViewById(R.id.chatName),
                        reply=h.itemView.findViewById(R.id.incomingMessage),
                        incomingMessage=h.itemView.findViewById(R.id.chatMessage),
                        timeAgo=h.itemView.findViewById(R.id.timeAgo);

                CardView chatNameListCard=h.itemView.findViewById(R.id.chatNameListCard);


                chatName.setText(autoHistoryInfos.get(index).ContactName);
                incomingMessage.setText(autoHistoryInfos.get(index).ReceivedMessage);
                reply.setText(autoHistoryInfos.get(index).Replied);
                timeAgo.setText(autoHistoryInfos.get(index).Time);
                chatName.setText(autoHistoryInfos.get(index).ContactName);


                /*new entry*/
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("h:mm a d/M/yy");
                Date d1= null;
                Date d2= null;
                try {
                    d1 = sdf.parse(autoHistoryInfos.get(index).Time);
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
                    timeAgo.setText("A week ago");
                }else if (days>9 )
                {
                    timeAgo.setText(autoHistoryInfos.get(index).Time);
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
                    Log.i("chatlist","minutes>0 1");
                    long sec=minutes*60;

                    if (seconds>sec)
                    {   long secDiff=seconds-sec;
                        timeAgo.setText(""+minutes+" min "+secDiff+"sec ago");
                    }else
                    {
                        timeAgo.setText(""+minutes+" min ago");
                        Log.i("chatlist","minutes>0");
                    }
                }else
                {
                    timeAgo.setText("Few Moment ago");
                }

            }

            @Override
            public int getItemViewType(int position) {
                if (position%7==0 )
                {
                    return 0;
                }else {
                    return 1;
                }
            }

            @Override
            public int getItemCount() {
                return autoHistoryInfos.size();
            }
        };

        recycleAutoHistory=v.findViewById(R.id.recycleAutoHistory);
        noDataLayout=v.findViewById(R.id.noDataLayout);

        recycleLayout=v.findViewById(R.id.recycleLayout);

        if (autoHistoryInfos.size()>0)
        {

            Log.i("autoHistory","data found");
            noDataLayout.setVisibility(View.GONE);
            recycleAutoHistory.setAdapter(adapter);
            recycleAutoHistory.setVisibility(View.VISIBLE);
            recycleLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            noDataLayout.setVisibility(View.VISIBLE);
            recycleAutoHistory.setVisibility(View.GONE);
            recycleLayout.setVisibility(View.GONE);
        }
    }
    public class autoHistoryInfo{


        String ContactName,ReceivedMessage,Replied,Time;
        int isGroup,id;

        public autoHistoryInfo(String contactName, String receivedMessage, String replied, String time, int isGroup, int id) {
            ContactName = contactName;
            ReceivedMessage = receivedMessage;
            Replied = replied;
            Time = time;
            this.isGroup = isGroup;
            this.id = id;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}