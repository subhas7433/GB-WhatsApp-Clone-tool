package com.affixstudio.whatsapptool.activity;

import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.affixstudio.whatsapptool.model.preferences.PreferencesManager;
import com.affixstudio.whatsapptool.model.utils.ContextWrapper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        PreferencesManager prefs = PreferencesManager.getPreferencesInstance(newBase);
        ContextWrapper contextWrapper = ContextWrapper.wrap(newBase, prefs.getSelectedLocale());
        super.attachBaseContext(contextWrapper);

        //Fix language changing bug on API L to N_MR1, caused by AndroidX
        //REF: https://stackoverflow.com/a/61572489/5525931
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1)
            applyOverrideConfiguration(contextWrapper.getResources().getConfiguration());
    }
}
