package com.affixstudio.whatsapptool.adopterOur;

import static com.affixstudio.whatsapptool.activityOur.startScreen.adsDetails;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activity.main.MainActivity;
import com.affixstudio.whatsapptool.activityOur.Call_block;
import com.affixstudio.whatsapptool.activityOur.private_chat;
import com.affixstudio.whatsapptool.modelOur.setOnRecycleClick;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder>{

    int[] images;
    Context c;

    public SliderAdapter(int[] images, Context c, com.affixstudio.whatsapptool.modelOur.setOnRecycleClick setOnRecycleClick) {
        this.images = images;
        this.c = c;
        this.setOnRecycleClick = setOnRecycleClick;
    }

    setOnRecycleClick setOnRecycleClick;


    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {


       // viewHolder.imageView.setImageResource(images[position]);
        Picasso.get().load(adsDetails.get(position).imageLink).into(viewHolder.imageView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                c.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(adsDetails.get(position).openingLink)));


//                if(position == 0){
//                    c.startActivity(new Intent(c, Call_block.class));
//                }else if(position == 1){
//                    c.startActivity(new Intent(c, private_chat.class));
//                }
//                else if(position == 2){
//                    setOnRecycleClick.onItemEdit(6,""); //sending data to mainActivity to change navigation selection
//                }
//                else if(position == 3){
//                    c.startActivity(new Intent(c, MainActivity.class));
//                }


//                c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.affixstudio.gbwhats")));
            }
        });

    }

    @Override
    public int getCount() {
        return adsDetails.size();
    }

    public class Holder extends  ViewHolder{

        ImageView imageView;

        public Holder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);


        }
    }

}