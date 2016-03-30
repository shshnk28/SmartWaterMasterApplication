package com.example.shashankshekhar.servicedemo;


import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
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
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements MQTTConstants{

    Messenger messenger = null;
    boolean mBound = false;
    ProgressDialog connectingDialog;
    Messenger clientMessenger;
    String userName;

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

    public void startAndBindService (View view) {
        EditText editText = (EditText)findViewById(R.id.textView1);
        userName = editText.getText().toString();
        if (userName == null || userName.isEmpty()) {
            CommonUtils.showToast(getApplicationContext(),"Pls enter name");
            return;
        }
        if (messenger != null && mBound != false) {
            CommonUtils.printLog("service connected already ");
            CommonUtils.showToast(getApplicationContext(), "Service already connected");
            Intent publisherServiceIntent = new Intent(getApplicationContext(), PublisherService.class);
            publisherServiceIntent.putExtra("userName",userName);
            startService(publisherServiceIntent);
            return;
        }
        ComponentName componentName = new ComponentName(PACKAGE_NAME,SERVICE_NAME);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        ComponentName componentName1 = startService(intent);
        CommonUtils.printLog("returned component name: "+componentName1.toString());
        CommonUtils.printLog("now binding to service");
        Boolean bindSuccess = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    public void showDialogAndConnectToMqtt (View view) {
        if (messenger == null || mBound == false ) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(),"Service not running");
            return;
        }
        connectingDialog = ProgressDialog.show(this,"Please Wait...","Connecting to broker");
        connectingDialog.setCancelable(false);
        clientMessenger = new Messenger(new IncomingHandler(connectingDialog,this));
        connectMqtt();
        Intent publisherServiceIntent = new Intent(getApplicationContext(), PublisherService.class);
        publisherServiceIntent.putExtra("userName",userName);
        startService(publisherServiceIntent);
    }

    public void connectMqtt () {
        // TODO: 22/03/16 remove the hardcoded stuff such as 8. integrate the library
        Message message = Message.obtain(null,8);
        message.replyTo= clientMessenger;
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            CommonUtils.printLog("remote Exception,Could not send message");
        }
    }
    public void launchDebugScreen(View view) {
        Intent debugIntent = new Intent(this,DebugActivity.class);
        debugIntent.putExtra("messengerObj",messenger);
        debugIntent.putExtra("bound",mBound);
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
