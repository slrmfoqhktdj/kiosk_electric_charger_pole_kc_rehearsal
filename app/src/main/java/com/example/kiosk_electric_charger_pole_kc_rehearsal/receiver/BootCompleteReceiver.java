package com.example.kiosk_electric_charger_pole_kc_rehearsal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.SplashActivity;

public class BootCompleteReceiver extends BroadcastReceiver {
    static final String TAG = "SIMPLE_KIOSK";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive: " + action);
        if(Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Intent i = new Intent(context, SplashActivity.class);

            context.startActivity(i);
        }
    }

}