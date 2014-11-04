package com.example.TelReceiver;

/**
 * Created by Juntao Zhen on 2014/11/2.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TelReceiver extends BroadcastReceiver {
    static boolean  isServiceStarted = false;
    final String tel = "android.intent.action.PHONE_STATE";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
            {
                Log.e("Juntao.Zhen:", "Outgoing call......");
            }
            else
            {
                Log.e("Juntao.Zhen:", "Incoming call......");
            }
            if (intent.getAction().equals(tel) && isServiceStarted == false) {
                Intent ServiceIntent = new Intent("com.tel.listner");
                context.startService(ServiceIntent);
                isServiceStarted = true;
            }
        }
    }
}
