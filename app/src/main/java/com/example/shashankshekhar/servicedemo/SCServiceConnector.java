package com.example.shashankshekhar.servicedemo;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

import com.example.shashankshekhar.servicedemo.Interfaces.ServiceCallback;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

/**
 * Created by shashankshekhar on 30/03/16.

public class SCServiceConnector implements ServiceConnection {
    // TODO: 31/03/16 these two vars should only have getters and no setters
    public static Messenger messenger;
    public static boolean mBound;
    ServiceCallback callback;
    public SCServiceConnector (ServiceCallback callback1) {
        callback = callback1;
    }
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        messenger = new Messenger(service);
        mBound = true;
        callback.serviceConnected();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        CommonUtils.printLog("service disconnected");
        mBound = false;
        messenger = null;
        callback.serviceDisconnected();
    }
}*/
