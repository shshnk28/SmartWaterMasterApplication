package com.example.shashankshekhar.servicedemo;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;

import android.view.View;


import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MQTTConstants{

    Messenger messenger = null;
    boolean mBound = false;
    ProgressDialog connectingDialog;
    Messenger clientMessanger;
    static final String PACKAGE_NAME = "com.example.shashankshekhar.servicedemo";
    static final String SERVICE_NAME = "com.example.shashankshekhar.servicedemo.FirstService";

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
    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtils.printLog("on destroy main activity called- master application");
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            received_string = intent.getStringExtra("key");
            CommonUtils.printLog("received intent = " + intent.toString());
        }

    };

    public void startAndBindService (View view) {
        ComponentName componentName = new ComponentName(PACKAGE_NAME,SERVICE_NAME);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        ComponentName componentName1 = startService(intent);
        CommonUtils.printLog("returned component name: "+componentName1.toString());
        CommonUtils.printLog("now binding to service");
        Boolean bindSuccess = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }
    public void stopService (View view) {
        if (messenger == null || mBound == false ) {
            CommonUtils.printLog("service not connected .. returning");
            return;
        }
        ComponentName componentName = new ComponentName(PACKAGE_NAME,SERVICE_NAME);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        stopService(intent);
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
            CommonUtils.showToast(getApplicationContext(), "Service not running");
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
    public void showDialogAndConnectToMqtt (View view) {
        if (messenger == null || mBound == false ) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(),"Service not running");
            return;
        }
        connectingDialog = ProgressDialog.show(this,"Please Wait...","Connecting to broker");
        connectingDialog.setCancelable(false);
        clientMessanger = new Messenger(new IncomingHandler(connectingDialog,this));
        connectMqtt();
    }

    public void connectMqtt () {
        Message message = Message.obtain(null,8);
        message.replyTo= clientMessanger;
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            CommonUtils.printLog("remote Exception,Could not send message");
        }
    }
    public void launchDebugScreen(View view) {
        Intent debugIntent = new Intent(this,ConnectionCheckActivity.class);
        startActivity(debugIntent);
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            mBound = true;
            showDialogAndConnectToMqtt(null);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            CommonUtils.printLog("service disconnected");
            mBound = false;
            messenger = null;
        }

    };
}
class IncomingHandler extends Handler {
    // TODO: 13/02/16 this list should be with the library since many other applciations will use it. now main app
    // 3s1 are using
    static final int MQTT_CONNECTED =1;
    static final int UNABLE_TO_CONNECT =2;
    static final int NO_NETWORK_AVAILABLE =4;
    static final int MQTT_CONNECTION_IN_PROGRESS = 5;
    static final int MQTT_NOT_CONNECTED = 6;
    ProgressDialog dialog;
    Context applicationContext;
    IncomingHandler(ProgressDialog dialog1, Context context) {
        this.dialog= dialog1;
        this.applicationContext = context;
    }
    @Override
    public void handleMessage (Message message) {
        dialog.dismiss();
        switch (message.what) {
            case MQTT_CONNECTED://
                CommonUtils.printLog("mqtt connected");
                CommonUtils.showToast(applicationContext, "Connected!!");
                break;
            case UNABLE_TO_CONNECT:
                CommonUtils.printLog("unable to connect");
                CommonUtils.showToast(applicationContext,"could not connect");
                break;
            case NO_NETWORK_AVAILABLE:
                CommonUtils.showToast(applicationContext,"No Network!!");
                break;
            case MQTT_CONNECTION_IN_PROGRESS:
                CommonUtils.showToast(applicationContext,"Connection in Progress");
                break;
            default:

        }
    }
}
