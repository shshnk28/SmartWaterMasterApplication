package com.example.shashankshekhar.servicedemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

public class MainActivity extends AppCompatActivity {
    private String received_string = null;
    Messenger messenger = null;
    boolean mBound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            received_string = intent.getStringExtra("key");
            CommonUtils.printLog("received intent = " + intent.toString());
        }

    };
    public void subscribetoTopic (View view) {
        if (mBound == false || messenger == null) {
            return;
        }

    }

    public void bindService (View view) {
        ComponentName componentName = new ComponentName("com.example.shashankshekhar.servicedemo","com.example.shashankshekhar.servicedemo.FirstService");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        Boolean bindSuccess = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }
    public void unbindService (View view) {
        if (messenger == null || mBound == false ) {
            CommonUtils.printLog("service not connected .. returning");
            return;
        }
        unbindService(serviceConnection);
        mBound = false;
        messenger = null;
    }

    public  void checkService (View view) {
        if (messenger == null || mBound == false ) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(), "not running");
            return;
        }
        Message message = Message.obtain(null,6);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            CommonUtils.printLog("remote Exception,Could not send message");
        }

    }
    public void checkConnection (View view) {
        if (messenger == null || mBound == false ) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(),"Service not running");
            return;
        }
        Message message = Message.obtain(null,7);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            CommonUtils.printLog("remote Exception,Could not send message");
        }
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            messenger = null;
        }
    };
}
