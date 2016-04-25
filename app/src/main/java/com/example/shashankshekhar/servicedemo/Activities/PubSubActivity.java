package com.example.shashankshekhar.servicedemo.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.IncomingHandler;
import com.example.shashankshekhar.servicedemo.Interfaces.ServiceCallback;
import com.example.shashankshekhar.servicedemo.R;
import com.example.shashankshekhar.servicedemo.SCServiceConnector;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import java.util.List;

public class PubSubActivity extends AppCompatActivity implements ServiceCallback,MQTTConstants {
    TextView multiLineTV;
    EditText topicEditText;
    String topicName;
    String currentlySubscribedTopic = null;
    ProgressDialog connectingDialog;
    Messenger clientMessenger;
//    List<String> subscribedTopics;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_sub);
        multiLineTV  = (TextView)findViewById(R.id.multiLineTextView);
        multiLineTV.setMovementMethod(new ScrollingMovementMethod());
        topicEditText = (EditText)findViewById(R.id.topicName);
        clientMessenger = new Messenger(new IncomingHandler(getApplicationContext(), this));
    }
    @Override
    public void onDestroy () {
        super.onDestroy();
        unsubscribeToTopic(currentlySubscribedTopic);
        currentlySubscribedTopic = null;
        unregisterReceiver(broadcastReceiver);
    }
    public void subscribeToTopic(View view) {
        connectingDialog = ProgressDialog.show(this, "Please Wait...", "Subscribing to Topic");
        connectingDialog.setCancelable(false);
        topicName = topicEditText.getText().toString();
        if (currentlySubscribedTopic != null) {
            /*
        1. usnsubscribe the previously subscribed topic if any
        2. unregister the broadcast receiver
         */
            unsubscribeToTopic(currentlySubscribedTopic);
        } else {
            subscribeToTopic();
        }
    }

    public void messageReceivedFromService(int number) {
        String toastStr = null;
        switch (number) {
            case SUBSCRIPTION_SUCCESS:
                toastStr = "subscribed";
                // here only register the BR
                currentlySubscribedTopic = topicName;
                setupBroadcastReceiver();
                connectingDialog.dismiss();
                break;
            case SUBSCRIPTION_ERROR:
                toastStr = "could not subscribe";
                connectingDialog.dismiss();
                break;
            case NO_NETWORK_AVAILABLE:
                toastStr = "No Network";
                connectingDialog.dismiss();
                break;
            case MQTT_NOT_CONNECTED:
                toastStr = "Mqtt Not cnnected to broker";
                connectingDialog.dismiss();
                break;
            case UNSUBSCRIPTION_SUCCESS:
                // remove the broadcast receiver here
                currentlySubscribedTopic = null;
                unregisterReceiver(broadcastReceiver);
                subscribeToTopic();
                break;
            case UNSUBSCRIPTION_ERROR:
                toastStr = "could not unsubscribe";
                connectingDialog.dismiss();
                break;
            default:
                toastStr = "switch case unknown";
        }
        if (toastStr!=null) {
            CommonUtils.showToast(getApplicationContext(),toastStr);
        }
    }
    private boolean isServiceRunning( ) {
        if (SCServiceConnector.messenger == null || SCServiceConnector.mBound == false) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(), "Service not running");
            return false;
        }
        return true;
    }

    private void unsubscribeToTopic(String topicName2) {
        if (isServiceRunning() == false) {
            if (connectingDialog.isShowing()) {
                connectingDialog.dismiss();
            }
            return;
        }
        Message message = Message.obtain(null, UNSUBSCRIBE_TO_TOPIC);
        Bundle bundle = new Bundle();
        bundle.putString("topicName",topicName2);
        message.setData(bundle);
        message.replyTo = clientMessenger;
        try {
            SCServiceConnector.messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            CommonUtils.printLog("remote Exception,Could not send message");
        }
    }
    private void subscribeToTopic () {
        if (isServiceRunning() == false) {
            if (connectingDialog.isShowing()) {
                connectingDialog.dismiss();
            }
            return;
        }
        Message message = Message.obtain(null, SUBSCRIBE_TO_TOPIC);
        Bundle bundle = new Bundle();
        bundle.putString("topicName",topicName);
        message.setData(bundle);
        message.replyTo = clientMessenger;
        try {
            SCServiceConnector.messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            CommonUtils.printLog("remote Exception,Could not send message");
        }
    }
    public void setupBroadcastReceiver () {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(currentlySubscribedTopic);
        try {
            registerReceiver(broadcastReceiver, intentFilter);
        } catch (IllegalArgumentException ex) {
            // recevier already registered
        }
    }
    @Override
    public void serviceConnected() {
    }

    @Override
    public void serviceDisconnected() {
        CommonUtils.printLog("service disconnecetd");
    }
}
