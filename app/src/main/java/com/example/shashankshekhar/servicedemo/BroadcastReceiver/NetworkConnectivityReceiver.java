package com.example.shashankshekhar.servicedemo.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.shashankshekhar.servicedemo.FirstService;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

/**
 * Created by shashankshekhar on 19/03/16.
 */
public class NetworkConnectivityReceiver extends BroadcastReceiver {
    static FirstService firstService;
    public static void initServiceObj (FirstService serviceObj) {
        if (serviceObj == null) {
            CommonUtils.printLog("111111service obj is null in setter.. returing");
            return;
        }
        firstService = serviceObj;
        
    }
    public void onReceive(Context context, Intent intent) {
        if (firstService == null) {
            CommonUtils.printLog("111111service obj is null in onreceive.. returing");
            return;
        }
        firstService.onNetworkChange();
    }
}
