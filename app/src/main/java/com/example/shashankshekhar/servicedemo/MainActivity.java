package com.example.shashankshekhar.servicedemo;


import android.app.ProgressDialog;
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

import android.view.Menu;

import android.view.View;


import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    Messenger messenger = null;
    boolean mBound = false;
    ProgressDialog connectingDialog;
    Messenger clientMessanger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clientMessanger = new Messenger(new IncomingHandler(connectingDialog,this));

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

    public void bindService (View view) {
        ComponentName componentName = new ComponentName("com.example.shashankshekhar.servicedemo","com.example.shashankshekhar.servicedemo.FirstService");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        connectingDialog = ProgressDialog.show(this,"Please Wait...","Connecting to broker");
        connectingDialog.setCancelable(false);
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
        // TODO: 18/01/16  note that here you are not stopping the service actually, just th e connection of the app
        // is being severed. but onDestroy is called on the service
    }

    public  void checkService (View view) {
        if (messenger == null || mBound == false ) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(), "not running");
            return;
        }
        Message message = Message.obtain(null,6);
        message.replyTo = clientMessanger;
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
    public void connectMqtt (View view) {
        Message message = Message.obtain(null,8);
        message.replyTo= clientMessanger;
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
            connectMqtt(new View(getApplicationContext()));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            messenger = null;
        }
        
    };
}
class IncomingHandler extends Handler {
    static final int MQTT_CONNECTED =1;
    static final int UNABLE_TO_CONNECT =2;
    ProgressDialog dialog;
    Context applicationContext;
    IncomingHandler(ProgressDialog dialog1, Context context) {
        this.dialog= dialog1;
        this.applicationContext = context;
    }
    @Override
    public void handleMessage (Message message) {
        switch (message.what) {
            case MQTT_CONNECTED://
                CommonUtils.printLog("mqtt connected");
                // STOP THE progress dialog
                // show a toast
                dialog.dismiss();
                CommonUtils.showToast(applicationContext, "Connected!!");

                break;
            case UNABLE_TO_CONNECT:
                CommonUtils.printLog("unable to connect");
                // STOP THE progress dialog
                // show a toast. show a reason as well if possible
                dialog.dismiss();
                CommonUtils.showToast(applicationContext,"could not connect");
        }

    }
}
