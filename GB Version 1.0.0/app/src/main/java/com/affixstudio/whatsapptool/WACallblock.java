package com.affixstudio.whatsapptool;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.affixstudio.whatsapptool.modelOur.callBlockHelper;


public class WACallblock extends Fragment {


    callBlockHelper cbh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_wa_callblock, container, false);


        cbh=new callBlockHelper(v,1,getContext(),getActivity());
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