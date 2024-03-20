package com.affixstudio.whatsapptool.activity.notification;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activity.BaseActivity;
import com.affixstudio.whatsapptool.activity.main.MainActivity;
import com.affixstudio.whatsapptool.model.utils.NotificationHelper;

public class NotificationIntentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_intent_activity); //dummy layout

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.getString("package") != null) {
                String packageName = extras.getString("package");
                NotificationHelper.getInstance(getApplicationContext()).markNotificationDismissed(packageName);
                launchApp(packageName);
            } else {
                launchHomeScreen();
            }
        }
    }

    private void launchApp(String packageName) {
        Intent intent;
        PackageManager pm = getPackageManager();

        intent = pm.getLaunchIntentForPackage(packageName);

        // ToDo: Getting null intent sometimes when service restart is implemented #291 (Google Play report)
        if (intent == null) {
            // Toast.makeText("Unable to open application").show();
            return;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

        finish();
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
