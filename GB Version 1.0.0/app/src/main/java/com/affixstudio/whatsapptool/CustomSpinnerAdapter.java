package com.affixstudio.whatsapptool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter {

    public CustomSpinnerAdapter(@NonNull Context context, ArrayList<CustomSpinnerItem> customList) {
        super(context, 0, customList);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent){
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout,parent,false);
        }
        CustomSpinnerItem item = (CustomSpinnerItem) getItem(position);
        ImageView spinnerIv=convertView.findViewById(R.id.ivSpinnerLayout);
        TextView spinnerTv=convertView.findViewById(R.id.tvSpinnerLayout);
        if(item != null){
            spinnerIv.setImageResource(item.getSpinnerItemImage());
            spinnerTv.setText(item.getSpinnerItemName());
        }
        return convertView;
    }
    public View getDropDownView(int position, @Nullable View convertView, @Nullable ViewGroup parent){
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.custom_sp_dropdown_layout,parent,false);
        }
        CustomSpinnerItem item = (CustomSpinnerItem) getItem(position);
        ImageView dropDownIv=convertView.findViewById(R.id.ivDropDownLayout);
        TextView dropDownTv=convertView.findViewById(R.id.tvDropDownLayout);
        if(item != null){
            dropDownIv.setImageResource(item.getSpinnerItemImage());
            dropDownTv.setText(item.getSpinnerItemName());
        }
        return convertView;
    }
}
