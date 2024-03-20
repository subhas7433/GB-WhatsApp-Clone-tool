package com.affixstudio.whatsapptool.modelOur.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.affixstudio.whatsapptool.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class bottomSmallDialog {

    public void showinfo(Context context,int index)
    {

        String[] titleList=context.getResources().getStringArray(R.array.short_tutorial_names);
        String[] tutorialList=context.getResources().getStringArray(R.array.short_tutorials);
        BottomSheetDialog bt=new BottomSheetDialog(context);


        View v=View.inflate(context, R.layout.bottom_auto_dialog,null);
        TextView
                pageTitle=v.findViewById(R.id.pageTitle),
                details=v.findViewById(R.id.details);

        Button understood=v.findViewById(R.id.understand);


        pageTitle.setText(titleList[index]);
        details.setText(tutorialList[index]);

        understood.setOnClickListener(view -> bt.dismiss());

        bt.setContentView(v);
        bt.show();

    }
}
