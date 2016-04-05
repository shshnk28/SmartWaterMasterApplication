package com.example.shashankshekhar.servicedemo;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

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

    public MyInstanceIDService() {
        super("MyInstanceIDService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = "someSenderId";
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token,"/topics/dogs",null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

}
