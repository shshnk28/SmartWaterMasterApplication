package com.example.shashankshekhar.servicedemo.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.FirstService;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

/**
 * Created by shashankshekhar on 19/03/16.
 */
public class NetworkConnectivityReceiver extends BroadcastReceiver implements MQTTConstants {
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, FirstService.class);
        serviceIntent.putExtra("fromBroadcastReceiver", true);
        context.startService(serviceIntent);
    }
}
