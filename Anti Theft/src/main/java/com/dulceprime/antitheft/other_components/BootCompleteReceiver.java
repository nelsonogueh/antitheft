package com.dulceprime.antitheft.other_components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dulceprime.antitheft.services.Background_Service;

/**
 * Created by NELSON on 11/23/2017.
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
// TODO Auto-generated method stub
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // we start the main activity when the boot is completed

            context.stopService(new Intent(context, com.dulceprime.antitheft.services.Background_Service.class));
            Intent activityIntent = new Intent(context, com.dulceprime.antitheft.services.Background_Service.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(activityIntent);
        }

    }

}
