package com.example.shashankshekhar.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;
import com.google.android.gms.gcm.GcmPubSub;

import java.io.IOException;

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    public GcmListenerService() {
    }
    @Override
    public void onMessageReceived (String from,Bundle data) {
        CommonUtils.printLog("message received form gcm..:|");
        if (from.startsWith("/topics/dogs")) {
            CommonUtils.printLog("got the log");
        }
    }
//    private void subscribeTopics(String token) throws IOException {
//        GcmPubSub pubSub = GcmPubSub.getInstance(this);
//        for (String topic : TOPICS) {
//            pubSub.subscribe(token, "/topics/" + topic, null);
//        }
//    }
//    @Override
//    public void onTokenRefresh() {
//        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
//        Intent intent = new Intent(this, MyInstanceIDService.class);
//        startService(intent);
//    }

}
