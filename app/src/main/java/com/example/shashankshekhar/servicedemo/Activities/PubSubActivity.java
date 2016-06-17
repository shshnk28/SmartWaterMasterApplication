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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.R;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;
import com.example.shashankshekhar.smartcampuslib.IncomingHandler;
import com.example.shashankshekhar.smartcampuslib.Interfaces.ServiceCallback;
import com.example.shashankshekhar.smartcampuslib.ServiceAdapter;
import com.example.shashankshekhar.smartcampuslib.SmartXLibConstants;

import java.util.List;

public class PubSubActivity extends AppCompatActivity implements ServiceCallback,MQTTConstants,SmartXLibConstants {
    TextView multiLineTV;
    EditText topicEditText;
    EditText publishEditText;
    TextView incomingMessageTV;
    String topicName;
    String currentlySubscribedTopic = null;
    ProgressDialog connectingDialog;
    Messenger clientMessenger;
    boolean continueWithSubscription;
    ServiceAdapter serviceAdapter;
//    List<String> subscribedTopics;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CommonUtils.printLog("Broadcast received in Pub-Sub Screen");
            String message = intent.getStringExtra("message");
            incomingMessageTV.append("received: " +message + "\n");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_sub);
        multiLineTV  = (TextView)findViewById(R.id.multiLineTextView);
        multiLineTV.setMovementMethod(new ScrollingMovementMethod());
        topicEditText = (EditText)findViewById(R.id.topicName);
        publishEditText = (EditText)findViewById(R.id.publishET);
        incomingMessageTV = (TextView)findViewById(R.id.multiLineTextView);
        clientMessenger = new Messenger(new IncomingHandler(getApplicationContext(), this));
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        serviceAdapter = new ServiceAdapter(getApplicationContext());
    }
    @Override
    public void onDestroy () {
        super.onDestroy();
        if (currentlySubscribedTopic == null) {
            return;
        }
        unsubscribeToTopic(currentlySubscribedTopic);
        currentlySubscribedTopic = null;
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }

    }
    @Override
    public void onResume () {
        super.onResume();
        setupBroadcastReceiver();

    }
    @Override
    public void onPause () {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }
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
            continueWithSubscription = true;
            unsubscribeToTopic(currentlySubscribedTopic);
        } else {
            subscribeToTopic();
        }
    }

    public void publishData (View view) {
        String data = publishEditText.getText().toString();
        publishData(data);
    }

    public void unsubscribeToTopic (View view) {
        if (currentlySubscribedTopic == null) {
            return;
        }
        continueWithSubscription = false;
        unsubscribeToTopic(currentlySubscribedTopic);
    }
    public void messageReceivedFromService(int number) {
        String toastStr = null;
        switch (number) {
            case SUBSCRIPTION_SUCCESS:
                toastStr = "subscribed";
                // here only register the BR
                currentlySubscribedTopic = topicName;
                setupBroadcastReceiver();
                if(connectingDialog != null && connectingDialog.isShowing())
                    {connectingDialog.dismiss();}
                break;
            case SUBSCRIPTION_ERROR:
                toastStr = "could not subscribe";
                if(connectingDialog != null && connectingDialog.isShowing())
                    {connectingDialog.dismiss();}
                break;
            case NO_NETWORK_AVAILABLE:
                toastStr = "No Network";
                if(connectingDialog != null && connectingDialog.isShowing())
                    {connectingDialog.dismiss();}
                break;
            case MQTT_NOT_CONNECTED:
                toastStr = "Mqtt Not cnnected to broker";
                if(connectingDialog != null && connectingDialog.isShowing())
                    {connectingDialog.dismiss();}
                break;
            case UNSUBSCRIPTION_SUCCESS:
                // remove the broadcast receiver here
                currentlySubscribedTopic = null;
                try {
                    unregisterReceiver(broadcastReceiver);
                } catch (Exception ex) {

                }
                if (continueWithSubscription) {
                    CommonUtils.printLog("unsub succcessful. Resubscribing now");
                    subscribeToTopic();
                } else {
                    toastStr = "unsubscribed";
                }
                break;
            case UNSUBSCRIPTION_ERROR:
                toastStr = "could not unsubscribe";
                if(connectingDialog != null && connectingDialog.isShowing()){connectingDialog.dismiss();}
                break;
            case TOPIC_PUBLISHED:
                toastStr = "published";
                break;
            case ERROR_IN_PUBLISHING:
                toastStr = "could not publish";
                break;
            default:
                toastStr = "switch case unknown";
        }
        if (toastStr!=null) {
            CommonUtils.showToast(getApplicationContext(),toastStr);
        }
    }
    private boolean isServiceRunning( ) {
        if (serviceAdapter.serviceConnected() == false) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(), "Service not running");
            return false;
        }
        return true;
    }

    private void unsubscribeToTopic(String topicName2) {
        if (isServiceRunning() == false) {
            if (connectingDialog.isShowing()) {
                if(connectingDialog != null && connectingDialog.isShowing()){connectingDialog.dismiss();}
            }
            return;
        }
        serviceAdapter.unsubscribeFromTopic(topicName2, clientMessenger);
//        Message message = Message.obtain(null, UNSUBSCRIBE_TO_TOPIC);
//        Bundle bundle = new Bundle();
//        bundle.putString("topicName",topicName2);
//        message.setData(bundle);
//        message.replyTo = clientMessenger;
//        try {
//            SCServiceConnector.messenger.send(message);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            CommonUtils.printLog("remote Exception,Could not send message");
//        }
    }
    private void subscribeToTopic () {
        if (isServiceRunning() == false) {
            if (connectingDialog.isShowing()) {
                if(connectingDialog != null && connectingDialog.isShowing()){connectingDialog.dismiss();}
            }
            return;
        }
        serviceAdapter.subscribeToTopic(topicName,clientMessenger);
//        Message message = Message.obtain(null, SUBSCRIBE_TO_TOPIC);
//        Bundle bundle = new Bundle();
//        bundle.putString("topicName",topicName);
//        message.setData(bundle);
//        message.replyTo = clientMessenger;
//        try {
//            SCServiceConnector.messenger.send(message);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            CommonUtils.printLog("remote Exception,Could not send message");
//        }
    }
    private void publishData (String data) {
        if (isServiceRunning() == false) {
            return;
        }
        String topicName = topicEditText.getText().toString();
        serviceAdapter.publishGlobal(topicName,null,data,clientMessenger);
//        Message message = Message.obtain(null, PUBLISH_MESSAGE);
//        Bundle bundle = new Bundle();
//        bundle.putString("topicName",topicName);
//        bundle.putString("dataString",data);
//        message.setData(bundle);
//        message.replyTo = clientMessenger;
//        try {
//            SCServiceConnector.messenger.send(message);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            CommonUtils.printLog("remote Exception,Could not send message");
//        }
    }
    private void setupBroadcastReceiver () {
        if (currentlySubscribedTopic == null) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(currentlySubscribedTopic);
        try {
            registerReceiver(broadcastReceiver, intentFilter);
        } catch (IllegalArgumentException ex) {
            // recevier already registered
        }
    }
}
