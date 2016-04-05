package com.example.shashankshekhar.servicedemo.Activities;


import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;

import android.view.View;


import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.GcmMessageHandler;
import com.example.shashankshekhar.servicedemo.IncomingHandler;
import com.example.shashankshekhar.servicedemo.Interfaces.ServiceCallback;
import com.example.shashankshekhar.servicedemo.MyInstanceIDService;
import com.example.shashankshekhar.servicedemo.PublisherService;
import com.example.shashankshekhar.servicedemo.R;
import com.example.shashankshekhar.servicedemo.SCServiceConnector;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements MQTTConstants, ServiceCallback {

    //    ProgressDialog connectingDialog;
    Messenger clientMessenger;
    String userName;
    ProgressDialog connectingDialog;
    SCServiceConnector serviceConnector = new SCServiceConnector(this);

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
    }

    // service callbacks
    @Override
    public void messageReceivedFromService(int number) {
        CommonUtils.printLog("messageReceivedFromService  in mainActivity" + number);
//        int MQTT_CONNECTED =1;
//        int UNABLE_TO_CONNECT =2;
//        int NO_NETWORK_AVAILABLE =4;
//        int MQTT_CONNECTION_IN_PROGRESS = 5;
//        int MQTT_NOT_CONNECTED = 6;
        connectingDialog.dismiss();
        String toastStr;
        switch (number) {
            case MQTT_CONNECTED:
                toastStr = "Mqtt Connected";
                break;
            case UNABLE_TO_CONNECT:
                toastStr = "Not Connected";
                break;
            case NO_NETWORK_AVAILABLE:
                toastStr = "No network";
                break;
            case MQTT_CONNECTION_IN_PROGRESS:
                toastStr = "Connection in progress";
                break;
            case MQTT_NOT_CONNECTED:
                toastStr = "Mqtt Not Connected";
                break;
            default:
                toastStr = "switch case unknown";

        }
        showToastOnUIThread(toastStr);
    }

    @Override
    public void serviceConnected() {
        CommonUtils.printLog("service connected callback received in main");
        showDialogAndConnectToMqtt(null);
    }

    @Override
    public void serviceDisconnected() {
        CommonUtils.printLog("service disconnecetd");
    }

    public void startAndBindService(View view) {
        if (SCServiceConnector.messenger != null && SCServiceConnector.mBound != false) {
            CommonUtils.printLog("service connected already ");
            CommonUtils.showToast(getApplicationContext(), "Service already connected");
            return;
        }

        EditText editText = (EditText) findViewById(R.id.textView1);
        userName = editText.getText().toString();
        userName = userName.trim();
        if (userName == null || userName.isEmpty()) {
            CommonUtils.showToast(getApplicationContext(), "Pls enter name");
            return;
        }

        ComponentName componentName = new ComponentName(PACKAGE_NAME, SERVICE_NAME);
        Intent intent = new Intent();
        intent.putExtra("username", userName);
        intent.setComponent(componentName);
        ComponentName componentName1 = startService(intent);
        Boolean bindSuccess = bindService(intent, serviceConnector, Context.BIND_AUTO_CREATE);

    }

    public void showDialogAndConnectToMqtt(View view) {
        if (SCServiceConnector.messenger == null || SCServiceConnector.mBound == false) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(), "Service not running");
            return;
        }
        connectingDialog = ProgressDialog.show(this, "Please Wait...", "Connecting to broker");
        connectingDialog.setCancelable(false);
        clientMessenger = new Messenger(new IncomingHandler(getApplicationContext(), this));
        connectMqtt();
        Intent publisherServiceIntent = new Intent(getApplicationContext(), PublisherService.class);
        publisherServiceIntent.putExtra("userName", userName);
        startService(publisherServiceIntent);
    }

    public void connectMqtt() {
        // TODO: 22/03/16 remove the hardcoded stuff such as 8. integrate the library
        Message message = Message.obtain(null, 8);
        message.replyTo = clientMessenger;
        try {
            SCServiceConnector.messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            CommonUtils.printLog("remote Exception,Could not send message");
        }
    }
    public void connectToGcm (View view) {
        // start the gcm thingy
         Intent mServiceIntent = new Intent(getApplicationContext(), MyInstanceIDService.class);
        startService(mServiceIntent);

    }

    public void launchDebugScreen(View view) {
        Intent debugIntent = new Intent(this, DebugActivity.class);
        startActivity(debugIntent);
    }

    private void showToastOnUIThread(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                CommonUtils.showToast(getApplicationContext(), message);
            }
        });
    }
}
