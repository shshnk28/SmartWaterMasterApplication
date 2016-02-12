package com.example.shashankshekhar.servicedemo;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Handler;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttPublisher;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttReceiver;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttSubscriber;
import com.example.shashankshekhar.servicedemo.Mqtt.SmartCampusMqttClient;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FirstService extends Service implements MQTTConstants {
    private static boolean isConnecting = false;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1: // not being used
                    String data = message.getData().getString("data");
                    // post a broadcast here.
                    break;
                case 2: // not being used
                    // update the main activity here with teh received string.
                    String data1 = message.getData().getString("data");
                    // send the data back to main activity to be displayed.
                    break;
                case PUBLISH_MESSAGE: // publish global message to a particular topic
                    String topicName = message.getData().getString("topicName");
                    String eventName = message.getData().getString("eventName");
                    String dataString = message.getData().getString("dataString");
                    if (topicName == null || eventName == null || dataString == null) {
                        CommonUtils.printLog(" either topic, event or data is null ... returning");
                        return;
                    }
                    if (CommonUtils.isNetworkAvailable(getApplicationContext()) == false) {
                        CommonUtils.showToast(getApplicationContext(), "Failed to publish, Network unavailable");
                        return;
                    }
                    // TODO: 18/01/16  don't call MQTT directly here. make it modular so that this class does not need know
                    // who handles the publishing event

                    MqttPublisher mqttPublisher = new MqttPublisher(topicName, eventName, dataString);
                    mqttPublisher.publishTopic(getApplicationContext());
                    break;
                case SUBSCRIBE_TO_TOPIC: // to subscribe to a topic
                    topicName = message.getData().getString("topicName");
                    if (CommonUtils.isNetworkAvailable(getApplicationContext()) == false) {
                        CommonUtils.showToast(getApplicationContext(), "Failed to subscribe, Network unavailable");
                        return;
                    }
                    CommonUtils.printLog("subscribe call made");
                    String subscribeID = MqttSubscriber.subscribeToTopic(topicName);
                    if (subscribeID == null) {
                        CommonUtils.printLog("couldnot subscribe to topic : ");
                    } else {
                        subscribedTopics.add(topicName);
                    }
                    // TODO: 12/11/15 return the subscribeId to the client from here.
                    break;
                case UNSUBSCRIBE_TO_TOPIC:// unsubscribe to a topic
                    if (CommonUtils.isNetworkAvailable(getApplicationContext()) == false) {
                        CommonUtils.showToast(getApplicationContext(), "Failed to unsubscribe, Network unavailable");
                        return;
                    }
                    topicName = message.getData().getString("topicName");
                    MqttSubscriber.unsubscribeToTopic(topicName);
                    subscribedTopics.remove(topicName);
                    break;
                case CHECK_SERVICE: // check if  service is running
                    boolean isRunning = CommonUtils.isMyServiceRunning(FirstService.class, getApplicationContext());
                    if (isRunning) {
                        CommonUtils.showToast(getApplicationContext(), "running");
                    } else {
                        CommonUtils.showToast(getApplicationContext(), "Not running");
                    }
                    break;
                case CHECK_MQTT_CONNECTION: // check if the mqtt client is connected
                    if (isConnecting) {
                        CommonUtils.showToast(getApplicationContext(), "connection in progress");
                    }
                    if (SmartCampusMqttClient.isClientConnected()) {
                        CommonUtils.showToast(getApplicationContext(), "client connected ");
                    } else {
                        CommonUtils.showToast(getApplicationContext(), "client not connected ");
                    }
                    break;
                case CONNECT_MQTT: // reconnect mqtt
                    if (SmartCampusMqttClient.isClientConnected()) {
                        sendMessageToClient(message.replyTo, 3);
                        return;
                    }
                    if (CommonUtils.isNetworkAvailable(getApplicationContext()) == false) {
                        sendMessageToClient(message.replyTo, 4);
                        return;
                    }
                    if (message.replyTo == null) {
                        sendMessageToClient(message.replyTo, 2);
                        return;
                    }
                    if (isConnecting == true){
                        sendMessageToClient(message.replyTo, 2);
                        return;
                    }

                    ConnectToMqtt connectToMqtt = new ConnectToMqtt(message.replyTo);
                    Thread mqttConnector = new Thread(connectToMqtt);
//               connectToMqtt.run();
                    mqttConnector.start();
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    public FirstService() {
    }

    List<String> subscribedTopics = new ArrayList<String>();

    @Override
    public IBinder onBind(Intent intent) {
        CommonUtils.printLog("on bind called");
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int id = android.os.Process.myPid();
        CommonUtils.printLog("onStart called in service");
        return Service.START_NOT_STICKY;

    }

    @Override
    public void onCreate() {
        CommonUtils.printLog("ONCreate called in service");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        CommonUtils.printLog("on unbind called");
        return false;
    }

    @Override
    public void onDestroy() {
        CommonUtils.printLog("SERVICE DESTROYED!!");
        // disconnect the mqtt in here
        MqttReceiver mqttReceiver = MqttReceiver.getReceiverInstance(getApplicationContext());
        mqttReceiver.disconnectMqtt();

        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final Messenger messenger = new Messenger(new IncomingHandler());

    public void setupBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if (broadcastReceiver == null) {
            CommonUtils.printLog("broadcast receiver is null..returning");
            return;
        }
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void resubscribeToAllTopics() {
        for (String topic : subscribedTopics) {
            MqttSubscriber.subscribeToTopic(topic);
            CommonUtils.printLog("reconnected to topic: " + topic);
        }
    }

    private void sendMessageToClient(Messenger messenger, int val) {
        Message replyMessage = Message.obtain(null, val);
        try {
            messenger.send(replyMessage);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isConnecting == true) {
                CommonUtils.printLog("A Connection req is already in progress.. returning");
                return;
            }
            final ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final android.net.NetworkInfo wifi = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final android.net.NetworkInfo mobile = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi.isConnected() || mobile.isConnected()) {
                isConnecting = true;
                CommonUtils.printLog("wifi connected");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MqttReceiver mqttReceiver = MqttReceiver.getReceiverInstance(getApplicationContext());
                        boolean isConnected = mqttReceiver.initialiseReceiver();
                        if (isConnected == true) {
                            resubscribeToAllTopics();
                        }
                        isConnecting = false;
                        CommonUtils.printLog("broadcast receiver - is main thread " + CommonUtils.checkMainThread());
                    }
                });
            } else {
                CommonUtils.printLog("no network available,Could not connect to mqtt");
            }
        }

    };

    private class ConnectToMqtt implements Runnable {
        Messenger clientMessenger;

        ConnectToMqtt(Messenger messenger) {
            this.clientMessenger = messenger;
        }

        @Override
        public void run() {
            isConnecting = true;
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            MqttReceiver mqttReceiver = MqttReceiver.getReceiverInstance(getApplicationContext());
            Boolean connectionSuccessful = mqttReceiver.initialiseReceiver();
            if (connectionSuccessful == true) {
                setupBroadcastReceiver();
//                 replyMessage = Message.obtain(null,1);
                sendMessageToClient(clientMessenger, 1);
            } else {
                sendMessageToClient(clientMessenger, 2);
            }
            isConnecting = false;
            CommonUtils.printLog("is main thread: " + Boolean.toString(CommonUtils.checkMainThread()));
        }
    }

    /*
    private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonUtils.printLog("pre-execute");
        }
        @Override
        protected Void doInBackground(Void... params) {
            if (CommonUtils.isNetworkAvailable(getApplicationContext()) == false) {
                return null;
            }
            MqttReceiver mqttReceiver = MqttReceiver.getReceiverInstance(getApplicationContext());
            mqttReceiver.initialiseReceiver();
            setupBroadcastReceiver();
            CommonUtils.printLog("is main thread: " + Boolean.toString(CommonUtils.checkMainThread()));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            CommonUtils.showToast(getApplicationContext(), "Service Created");


        }

    }*/

}

