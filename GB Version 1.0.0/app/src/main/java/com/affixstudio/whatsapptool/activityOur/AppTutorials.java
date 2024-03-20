package com.affixstudio.whatsapptool.activityOur;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.affixstudio.whatsapptool.CustomSpinnerAdapter;
import com.affixstudio.whatsapptool.CustomSpinnerItem;
import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.modelOur.dialog.tutorialVideo;

import java.util.ArrayList;
import java.util.Collections;

public class AppTutorials extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner csSpinner;
    ArrayList<CustomSpinnerItem> csList;

    ArrayList<String> arrayNames=new ArrayList<>();
    String selection="All";
    String[] list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_tutorials);
        // getSupportActionBar().hide(); //hide the title bar

        list=getResources().getStringArray(R.array.list);

        Collections.addAll(arrayNames, list);

        detalisList=new int[]{R.array.Details1,R.array.Details2,R.array.Details3,R.array.Details4,R.array.Details5,
                R.array.Details6,R.array.Details7,R.array.Details8,R.array.Details9,R.array.Details10,
                R.array.Details11,R.array.Details12};


        usageList=new int[]{R.array.usefull1,R.array.usefull2,R.array.usefull3,R.array.usefull4,R.array.usefull5,
                R.array.usefull6,R.array.usefull7,R.array.usefull8,R.array.usefull9,R.array.usefull10,
                R.array.usefull11,R.array.usefull12};

        ListVideoUrl=getResources().getStringArray(R.array.urlList); //#todo change urls

        csSpinner = findViewById(R.id.pageSpinner);
        csList=getCustomList();
        int screenCode=0;
        if (getIntent().getIntExtra("screenCode",-1)==-1)
        {
            CustomSpinnerAdapter adapter=new CustomSpinnerAdapter(this,csList);
            if (csSpinner != null) {
                csSpinner.setAdapter(adapter);
                csSpinner.setOnItemSelectedListener(this);
            }
        }else
        {
            csSpinner.setVisibility(View.GONE);
            screenCode = getIntent().getIntExtra("screenCode",-1);
            selection=list[screenCode];
            showList(screenCode);
        }

        findViewById(R.id.back).setOnClickListener(view -> {
            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("setting",false) || getIntent().getIntExtra("screenCode",-1)>-1
        )
        {
            super.onBackPressed();
        }else
        {
            Intent i = new Intent(this, MainActivity.class);
            Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(i, b);
        }
    }





    private ArrayList<CustomSpinnerItem> getCustomList() {
        csList=new ArrayList<>();
        csList.add(new CustomSpinnerItem("All", R.drawable.cp_all));
        csList.add(new CustomSpinnerItem("Auto Reply", R.drawable.auto_reply_lg));
        csList.add(new CustomSpinnerItem("Ascii Faces", R.drawable.ascii_fc_logo));
        csList.add(new CustomSpinnerItem("Caption", R.drawable.sms_template_icon));
        csList.add(new CustomSpinnerItem("Direct Chat", R.drawable.direct_msg_focused));
        csList.add(new CustomSpinnerItem("Decoration Text", R.drawable.dc_text_icon));
        csList.add(new CustomSpinnerItem("Quick Messages", R.drawable.quick_sms_focused));
        csList.add(new CustomSpinnerItem("Recover Messages", R.drawable.restore_sms));
        csList.add(new CustomSpinnerItem("Status Download", R.drawable.download_lg));
        csList.add(new CustomSpinnerItem("Schedule Messages", R.drawable.schedule_icon));
        csList.add(new CustomSpinnerItem("Text Repeater", R.drawable.repeat_icon));
        csList.add(new CustomSpinnerItem("No Blue Tick", R.drawable.double_tick));
        csList.add(new CustomSpinnerItem("WhatsApp Web", R.drawable.whats_web_ic));
        return csList;
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        CustomSpinnerItem item=(CustomSpinnerItem) adapterView.getSelectedItem();
        // Toast.makeText(this,item.getSpinnerItemName(), Toast.LENGTH_SHORT).show();
        selection = item.getSpinnerItemName();
        showList(i-1);
    }

    int[] detalisList;
    int[] usageList;
    Integer[] usageListIcon={R.drawable.auto_reply_lg,R.drawable.ascii_fc_logo,R.drawable.sms_template_icon
            ,R.drawable.direct_msg_focused,R.drawable.dc_text_icon, R.drawable.quick_sms_focused,R.drawable.restore_sms,R.drawable.download_lg,
            R.drawable.schedule_icon,R.drawable.repeat_icon,R.drawable.double_tick,R.drawable.whats_web_ic};
    String[] ListVideoUrl; //#todo change urls

    private void showList(int i) {

        Adapter adapter=new Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_recycle,parent,false)) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }

                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int p) {

                int position=p;
                if (!selection.equals("All"))
                {
                    position=i;
                    holder.itemView.findViewById(R.id.pageTitle).setVisibility(View.GONE);

                }
                holder.itemView.findViewById(R.id.understood).setVisibility(View.GONE);
                holder.itemView.findViewById(R.id.closeicon).setVisibility(View.GONE);

                RecyclerView featureListRecycle= holder.itemView.findViewById(R.id.featureListRecycle);
                RecyclerView detailsListRecycle= holder.itemView.findViewById(R.id.detailsListRecycle);

                featureListRecycle.setAdapter(bottomAdepter(getResources().getStringArray(usageList[position])));
                detailsListRecycle.setAdapter(bottomAdepter(getResources().getStringArray(detalisList[position])));

//                holder.itemView.findViewById(R.id.closeicon).setVisibility(View.GONE);
//                TextView details= (TextView)  holder.itemView.findViewById(R.id.details);
//                details.setText(detalisList[position]);
//
//                TextView usage= (TextView)  holder.itemView.findViewById(R.id.usage);
//                usage.setText(usageList[position]);

                TextView title= (TextView)  holder.itemView.findViewById(R.id.pageTitle);
                title.setText(list[position]);

                ImageView icon=(ImageView) holder.itemView.findViewById(R.id.screenIcon);
                icon.setImageResource(usageListIcon[position]);





                holder.itemView.findViewById(R.id.seeVideo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tutorialVideo video=new tutorialVideo();
                        video.showVideo(arrayNames.indexOf(title.getText().toString()),AppTutorials.this,title.getText().toString());
                    }
                });
            }

            @Override
            public int getItemCount() {
                if (selection.equals("All"))
                {
                    return detalisList.length;
                }
                return 1;
            }
        };


        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);

    }

    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static RecyclerView.Adapter bottomAdepter(String[] texts)
    {
        return new RecyclerView.Adapter (){
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.des_txt_para,parent,false))
                {
                    @NonNull
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int p)
            {
               TextView mainText= h.itemView.findViewById(R.id.mainText);
               TextView index= h.itemView.findViewById(R.id.index);


                mainText.setText(texts[p]);
                index.setText((p+1)+". ");




            }

            @Override
            public int getItemCount() {
                return texts.length;
            }
        };
    }
}