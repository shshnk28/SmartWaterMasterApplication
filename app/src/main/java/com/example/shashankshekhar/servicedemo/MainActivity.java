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


import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import android.os.Handler;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

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
    private class CheckForOpenPort implements Runnable {
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            if (CommonUtils.checkMainThread() == true) {
                CommonUtils.printLog("main thread .. returning");
                return;
            }
            CommonUtils.printLog("trying to connect to 1883 port on smartx");
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("smartx.cloudapp.net", 1883),120000);
                socket.close();
                // connection success
                CommonUtils.printLog("connection successful");
            } catch (java.io.IOException ex) {
                CommonUtils.printLog("could not connect - ioException");
                if (ex.getCause() != null) {
                    CommonUtils.printLog("cause: "+ex.getCause().toString());
                }
                if (ex.getMessage() != null) {
                    CommonUtils.printLog("message: " + ex.getMessage());
                }

            }
        }
    }

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
    public void checkForOpenPort (View view) {

        CheckForOpenPort cop = new CheckForOpenPort();
        Thread portThread = new Thread(cop);
        portThread.start();
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
    static final int MQTT_CONNECTED =1;
    static final int UNABLE_TO_CONNECT =2;
    static final int MQTT_ALREADY_CONNECTED =3;
    static final int NO_NETWORK_AVAILABLE =4;
    static final int MQTT_CONNECTION_IN_PROGRESS = 5;
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
            case MQTT_ALREADY_CONNECTED:
                CommonUtils.showToast(applicationContext, "Already Connected");
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
