package com.example.kiosk_electric_charger_pole_kc_rehearsal.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.SplashActivity;

public class KioskDeviceAdminReceiver extends DeviceAdminReceiver {
    private static final String TAG = KioskDeviceAdminReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent.getAction());

        if(intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)){
            Intent startIntent = new Intent(context, SplashActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(startIntent);
        }
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, "Device admin enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Toast.makeText(context, "Device admin disabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLockTaskModeEntering(Context context, Intent intent, String pkg) {
        Log.i(TAG, "onLockTaskModeEntering: " + pkg);
    }
}