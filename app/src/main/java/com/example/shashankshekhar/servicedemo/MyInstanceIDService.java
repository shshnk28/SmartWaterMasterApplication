package com.example.shashankshekhar.servicedemo;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class MyInstanceIDService extends IntentService {
private final String SENDER_ID = "162530255284";
    public MyInstanceIDService() {
        super("MyInstanceIDService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            // request token that will be used by the server to send push notifications
            CommonUtils.printLog("sending token request in intent service");
            String token = instanceID.getToken(SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
//            CommonUtils.printLog("token received: " + token + "length: " + token.length());
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            try {
                pubSub.subscribe(token,"/topics/hello",null);
            } catch (IOException e) {
                CommonUtils.printLog("subscription failed");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

}
