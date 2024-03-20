package com.affixstudio.whatsapptool;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.affixstudio.whatsapptool.modelOur.callBlockHelper;
import com.affixstudio.whatsapptool.serviceOur.NotificationService;


public class WBCallBlock extends Fragment {

    callBlockHelper cbh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_wb_call_block, container, false);


         cbh=new callBlockHelper(v,2,getContext(),getActivity());
        cbh.setView(getChildFragmentManager());
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        cbh.onResume();


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        cbh.OnDestory();
    }
}