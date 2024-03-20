package com.affixstudio.whatsapptool.adopterOur;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.schedule_sms;
import com.affixstudio.whatsapptool.getData.ArrayData;
import com.affixstudio.whatsapptool.getData.getMiliDiff;
import com.affixstudio.whatsapptool.modelOur.database;
import com.affixstudio.whatsapptool.modelOur.setOnRecycleClick;
import com.androidifygeeks.library.iface.IFragmentListener;

import java.util.ArrayList;

public class Adepter extends RecyclerView.Adapter<Adepter.ViewHolder>  {


    ArrayList<String> sendThrough;
    LinearLayout editSchedule;
    Context context;
    ArrayList <String> names;
    ArrayList <String> message;
    ArrayList <String> date;
    ArrayList <String> WAnumber;
    ArrayList <Integer> isdraftInt; // is the item draft or saved
    ArrayList<Integer> id=new ArrayList<>();

    ArrayList<String> state=new ArrayList<>();
    ArrayList<String> imagesUri=new ArrayList<>();
    ArrayList<String> videoUri=new ArrayList<>();
    ArrayList<String> audioUri=new ArrayList<>();
    ArrayList<String> docUri=new ArrayList<>();
    ArrayList<String> Opdate=new ArrayList<>();
    ArrayList<String> Optime=new ArrayList<>();

    int stateInt=-1; // detect which list to show . success or pending or both


    ArrayData arrayData;
    int callingFromChat=0; // because the adepter is used in two screen. It indicating from which screen its calling

    public Adepter(Context context, ArrayData arrayData, int callingFromChat,setOnRecycleClick setOnRecycleClick) {
        this.context = context;
        this.arrayData = arrayData;
        this.callingFromChat = callingFromChat;
        this. setOnRecycleClick = setOnRecycleClick;
    }

    com.affixstudio.whatsapptool.modelOur.setOnRecycleClick setOnRecycleClick;
    int d;

    public Adepter(  ArrayList<String> names, ArrayList<String> message, ArrayList<String> date, ArrayList<String> WAnumber, ArrayList<Integer> isdraftInt,ArrayList<String> sendThrough,
                     ArrayList<Integer> id, ArrayList<String> state,
                     ArrayList<String> imagesUri, ArrayList<String> videoUri,
                     ArrayList<String> audioUri,
                     ArrayList<String> docUri,
                     com.affixstudio.whatsapptool.modelOur.setOnRecycleClick setOnRecycleClick
            ,int d,LinearLayout editSchedule, Context context,int stateInt,ArrayList<String> Opdate,
    ArrayList<String> Optime) {
        this.sendThrough = sendThrough;
        this.editSchedule = editSchedule;
        this.context = context;
        this.names = names;
        this.message = message;
        this.date = date;
        this.WAnumber = WAnumber;
        this.isdraftInt = isdraftInt;
        this.id = id;
        this.stateInt = stateInt;

        this.state = state;
        this.imagesUri = imagesUri;
        this.videoUri = videoUri;
        this.audioUri = audioUri;
        this.docUri = docUri;

        this.setOnRecycleClick = setOnRecycleClick;
        this.d = d;
        this.Opdate = Opdate;
        this.Optime = Optime;
    }




    @NonNull
    @Override
    public Adepter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Adepter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sch_msg_history_recycle,null,false));
    }



    getMiliDiff diff=new getMiliDiff();
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, @SuppressLint("RecyclerView") int p) {

        Log.i("onBindViewHolder","position "+p);



        int position=0;;





        if (callingFromChat==1)//if calling from chat history
        {
            position=arrayData.getWANumebr().size()-1-p;
            h.nameTB.setVisibility(View.GONE);
            h.isdraft.setVisibility(View.GONE);


            if (arrayData.getSendThrough().get(position).equals("com.whatsapp")) {
                h.wsLogo.setVisibility(View.VISIBLE);
                h.wsBsLogo.setVisibility(View.GONE);
            } else {
                h.wsLogo.setVisibility(View.GONE);
                h.wsBsLogo.setVisibility(View.VISIBLE);
            }
            h.messageTb.setText(arrayData.getMessage().get(position));
            h.dateTB.setText(arrayData.getDate().get(position));
            h.number.setText(arrayData.getWANumebr().get(position));

            int finalPosition = position;
            int finalPosition2 = position;
            h.schiduleRVCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    setOnRecycleClick.onItemEdit(finalPosition, names.get(finalPosition2));
                    Log.i("schiduleRVCard", "schiduleRVCard pressed");
                }
            });

        }else
        { //if calling from schedule message

            position=isdraftInt.size()-1-p;
            if (sendThrough.get(position).equals("com.whatsapp")) {
                h.wsLogo.setVisibility(View.VISIBLE);
                h.wsBsLogo.setVisibility(View.GONE);
            } else {
                h.wsLogo.setVisibility(View.GONE);
                h.wsBsLogo.setVisibility(View.VISIBLE);
            }

            if (!names.get(position).isEmpty()) {
                Log.i("onBindViewHolder", "!names.get(position).isEmpty()");
                h.nameTB.setText(names.get(position));
            } else {
                Log.i("onBindViewHolder", "!names.get(position).isEmpty() else");
                h.nameTB.setText("Not set");
            }

            if (!message.get(position).isEmpty()) {
                Log.i("onBindViewHolder", "!message.get(position).isEmpty()");
                h.messageTb.setText(message.get(position));

            }else {
                h.msgHisLay.setVisibility(View.GONE);
            }

            Log.i("messageTb", "" + message.get(position) + " " + position);


            if (!date.get(position).isEmpty()) {
                Log.i("onBindViewHolder", "!date.get(position).isEmpty()");
                Log.i("dateTb", "" + date.get(position) + date.size());
                h.dateTB.setText(date.get(position));
            } else {
                Log.i("dateTb", "isEmpty");
            }

            long mildiff=0;
            long min3=-180000;

            if (!state.get(position).equals("") && !Opdate.get(position).contains("Select_Date")
                    &&  !Optime.get(position).contains("Select_Time"))
            {
                 mildiff=diff.finalMilis(Opdate.get(position),Optime.get(position));

                Log.i("Adepter","mildiff "+mildiff+" mildiff< min3  "+(mildiff< min3));


                if (!state.get(position).equals(""))
                {
                    h.schduleHisLay.setVisibility(View.VISIBLE);
                    String[] s=Optime.get(position).split(":");
                    int i=Integer.parseInt(s[0]);
                    String scheduleTime=i+":"+s[1]+" am "+Opdate.get(position);

                    if (i>12)
                    {
                        int intPm=i-12;
                        String timePart=(intPm)+":"+s[1];

                        if (String.valueOf(intPm).length()<2 && s[1].length()<2)
                        {
                            timePart="0"+(intPm)+":0"+s[1];
                        }else if (s[1].length()<2)
                        {
                            timePart=(intPm)+":0"+s[1];
                        }else if (String.valueOf(i-12).length()<2)
                        {
                            timePart="0"+(intPm)+":"+s[1];
                        }

                        scheduleTime=timePart+" pm "+Opdate.get(position);


                    }
                    else if (i==12)
                    {
                        scheduleTime=i+":"+s[1]+" pm "+Opdate.get(position);
                    }

                    h.schtime.setText(scheduleTime);


                }
                else
                {
                    h.schduleHisLay.setVisibility(View.GONE);
                }
            }else {
                h.schduleHisLay.setVisibility(View.GONE);
            }


            if (state.get(position).isEmpty())
            {

                h.isdraft.setTextColor(ContextCompat.getColor(context, R.color.ascii_cl));
                h.isdraft.setText("Draft");
            }
            else if (state.get(position).equals("Pending") && (mildiff< min3))
            {
                Log.i("onBindViewHolder", "state.get(position).equals(failed)");
                h.isdraft.setTextColor(ContextCompat.getColor(context, R.color.red));
                h.isdraft.setText("Failed");


                String query="CREATE TABLE IF NOT EXISTS scheduleMessage(_id INTEGER PRIMARY KEY autoincrement, Name text DEFAULT 'Not set',Message text,Number text,Date text,OpDate text,OpTime text,isDraft INTEGER ,TextCountryCode text DEFAULT '91',SendThrough text DEFAULT 'com.whatsapp',State text DEFAULT '',ImageURIs text DEFAULT 'Not set'" +
                        ",VideoURIs text DEFAULT 'Not set',AudioURIs text DEFAULT 'Not set',DocURIs text DEFAULT '') ";

                database db =new database(context,context.getString(R.string.schiduleTableName),query,1);
                db.getWritableDatabase().execSQL("UPDATE "+context.getString(R.string.schiduleTableName)+" SET State='Failed' WHERE _id="+id.get(position));



            }else if (state.get(position).equals("Pending"))
            {
                Log.i("onBindViewHolder", "state.get(position)  "+position+"  = "+state.get(position));
                h.isdraft.setTextColor(ContextCompat.getColor(context, R.color.status));
                h.isdraft.setText(state.get(position));
            }else if (state.get(position).equals("Failed"))
            {
                h.isdraft.setTextColor(ContextCompat.getColor(context, R.color.red));
                h.isdraft.setText(state.get(position));
            }else if (state.get(position).equals("Success"))
            {
                h.isdraft.setTextColor(ContextCompat.getColor(context, R.color.recycle_icon_cl));
                h.isdraft.setText(state.get(position));
            }
            //           h.number.setText(WAnumber.get(position));
            if (!WAnumber.get(position).isEmpty() && names.get(position).equals("Unknown")
                    &&! WAnumber.get(position).equals("Status"))

            {

                h.number.setVisibility(View.VISIBLE);
                h.number.setText(WAnumber.get(position));


            } else
            {

                h.number.setVisibility(View.GONE);



            }

            int finalPosition1 = position;
            h.schiduleRVCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    setOnRecycleClick.onItemEdit(finalPosition1,names.get(finalPosition1));
                    Log.i("schiduleRVCard", "schiduleRVCard pressed");
                }
            });





            // media count set up

            if (imagesUri.get(position).length()!=0)
            {
                h.imageLA.setVisibility(View.VISIBLE);
                String[] s=imagesUri.get(position).split("\\*");
                h.imageTxt.setText(""+s.length);


            }else {
                h.imageLA.setVisibility(View.GONE);
            }

            if (videoUri.get(position).length()!=0)
            {
                h.videoLA.setVisibility(View.VISIBLE);


                String[] s=videoUri.get(position).split("\\*");

                h.videoTxt.setText(""+s.length);


            }else {
                h.videoLA.setVisibility(View.GONE);
            }

            if (audioUri.get(position).length()!=0)
            {
                h.musicLA.setVisibility(View.VISIBLE);
                String[] s=audioUri.get(position).split("\\*");
                h.musicTxt.setText(""+s.length);


            }else {
                h.musicLA.setVisibility(View.GONE);
            }

            if (docUri.get(position).length()!=0)
            {
                h.documentLA.setVisibility(View.VISIBLE);
                String[] s=docUri.get(position).split("\\*");
                h.documentTxt.setText(""+s.length);


            }else {
                h.documentLA.setVisibility(View.GONE);
            }




            if (stateInt==1 &&  !state.get(position).equals("Pending"))
            {
                Log.i("schiduleRVCard", "Pending visible");
                h.schiduleRVCard.setVisibility(View.GONE);

            }
            else if (stateInt==2 && !state.get(position).equals("Success") )

            {
                Log.i("schiduleRVCard", "success visible");
                h.schiduleRVCard.setVisibility(View.GONE); //TODO success and pending list not working
            }else if (stateInt==0)
            {
                if (state.get(position).isEmpty() )
                {
                    h.schiduleRVCard.setVisibility(View.GONE); // invisibling those which are draft
                }

            }










           //



        }
    }

    @Override
    public int getItemCount() {

        if (callingFromChat==1)
        {
            return arrayData.getWANumebr().size();
        }else
            return names.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTB,messageTb,dateTB,isdraft,number,schtime,musicTxt,videoTxt,imageTxt,documentTxt;
        CardView schiduleRVCard;
        ImageView wsBsLogo,wsLogo;
        LinearLayout schduleHisLay,musicLA,videoLA,imageLA,documentLA,msgHisLay;
        FrameLayout adFrame;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTB=itemView.findViewById(R.id.name);
            messageTb=itemView.findViewById(R.id.Messages);
            dateTB=itemView.findViewById(R.id.date);
            isdraft=itemView.findViewById(R.id.isdraft);
            number=itemView.findViewById(R.id.number);
            musicTxt=itemView.findViewById(R.id.musicTxt);
            videoTxt=itemView.findViewById(R.id.videoTxt);
            imageTxt=itemView.findViewById(R.id.imageTxt);
            documentTxt=itemView.findViewById(R.id.documentTxt);

            schiduleRVCard=itemView.findViewById(R.id.schiduleRVCard);
            wsBsLogo=itemView.findViewById(R.id.wsBsLogo);
            wsLogo=itemView.findViewById(R.id.wsLogo);
            schtime=itemView.findViewById(R.id.schtime);
            schduleHisLay=itemView.findViewById(R.id.schduleHisLay);
            musicLA=itemView.findViewById(R.id.musicLA);
            videoLA=itemView.findViewById(R.id.videoLA);
            imageLA=itemView.findViewById(R.id.imageLA);
            documentLA=itemView.findViewById(R.id.documentLA);
            msgHisLay=itemView.findViewById(R.id.msgHisLay);

            ViewGroup.LayoutParams params = schiduleRVCard.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;




        }
    }
}
