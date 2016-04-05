package com.example.shashankshekhar.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;
import com.google.android.gms.gcm.GcmPubSub;

import java.io.IOException;

public class GcmMessageHandler extends com.google.android.gms.gcm.GcmListenerService {
    public GcmMessageHandler() {
    }
    @Override
    public void onMessageReceived (String from,Bundle data) {
        CommonUtils.printLog("Great Success.. message received from gcm..");
    }
//    @Override
//    public void onTokenRefresh() {
//        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
//        Intent intent = new Intent(this, MyInstanceIDService.class);
//        startService(intent);
//    }

}
