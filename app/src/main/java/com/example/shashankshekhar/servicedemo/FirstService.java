package com.example.shashankshekhar.servicedemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Handler;

import com.example.shashankshekhar.servicedemo.BroadcastReceiver.NetworkConnectivityReceiver;
import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttPublisher;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttReceiver;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttSubscriber;
import com.example.shashankshekhar.servicedemo.Mqtt.SmartCampusMqttClient;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class FirstService extends Service implements MQTTConstants {


    // connection options
    static final int MQTT_CONNECTED = 1;
    static final int UNABLE_TO_CONNECT = 2;
    static final int NO_NETWORK_AVAILABLE = 4;
    static final int MQTT_CONNECTION_IN_PROGRESS = 5;
    static final int MQTT_NOT_CONNECTED = 6;

    // publish status
    static final int TOPIC_PUBLISHED = 7;
    static final int ERROR_IN_PUBLISHING = 8;

    // subscription status
    static final int SUBSCRIPTION_SUCCESS = 9;
    static final int SUBSCRIPTION_ERROR = 10;

    private static boolean isConnecting = false; // indicates if a MQtt session is connecting currently
    final Messenger messenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1: // not being used
                    break;
                case 2: // not being used
                    break;
                case PUBLISH_MESSAGE: // publish global message to a particular topic
                    String topicName = message.getData().getString("topicName");
                    String eventName = message.getData().getString("eventName");
                    String dataString = message.getData().getString("dataString");
                    if (topicName == null || eventName == null || dataString == null) {
                        CommonUtils.printLog(" either topic, event or data is null ... returning");
                        return;
                    }
                    if (checkConnectivity(message.replyTo) == false) {
                        return;
                    }
                    // TODO: 18/01/16  don't call MQTT directly here. make it modular so that this class does not need know
                    // who handles the publishing event
                    MqttPublisher mqttPublisher = new MqttPublisher(topicName, eventName, dataString);
                    boolean didPublish = mqttPublisher.publishData();
                    if (didPublish) {
                        sendMessageToClient(message.replyTo, TOPIC_PUBLISHED);
                    } else {
                        sendMessageToClient(message.replyTo, ERROR_IN_PUBLISHING);
                    }
                    break;
                case SUBSCRIBE_TO_TOPIC: // to subscribe to a topic
                    // TODO: 13/02/16 return the subscribed topic id from here via clientMessenger
                    // set it as a string in the messenger
                    topicName = message.getData().getString("topicName");
                    if (checkConnectivity(message.replyTo) == false) {
                        return;
                    }
                    CommonUtils.printLog("subscribe call made");
                    String subscribeID = MqttSubscriber.subscribeToTopic(topicName);
                    if (subscribeID == null || subscribedTopics.contains(topicName) == true) {
                        CommonUtils.printLog("couldnot subscribe to topic : ");
                    } else {
                        subscribedTopics.add(topicName);
                    }
                    // TODO: 12/11/15 return the subscribeId to the client from here.
                    break;
                case UNSUBSCRIBE_TO_TOPIC:// unsubscribe to a topic
                    if (checkConnectivity(message.replyTo) == false) {
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
                    // check network before
                    if (isConnecting) {
                        CommonUtils.showToast(getApplicationContext(), "connection in progress");
                        return;
                    }
                    if (SmartCampusMqttClient.isClientConnected()) {
                        CommonUtils.showToast(getApplicationContext(), "client connected ");
                    } else {
                        CommonUtils.showToast(getApplicationContext(), "client not connected ");
                    }
                    break;
                case CONNECT_MQTT: // reconnect mqtt
                    if (message.replyTo == null) {
                        sendMessageToClient(message.replyTo, UNABLE_TO_CONNECT);
                        return;
                    }
                    if (isConnecting == true) {
                        sendMessageToClient(message.replyTo, MQTT_CONNECTION_IN_PROGRESS);
                        return;
                    }
                    if (SmartCampusMqttClient.isClientConnected() == true) {
                        sendMessageToClient(message.replyTo, MQTT_CONNECTED);
                        return;
                    }
                    ConnectToMqtt connectToMqtt = new ConnectToMqtt(message.replyTo);
                    Thread mqttConnector = new Thread(connectToMqtt);
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
        if (intent.getBooleanExtra("fromBroadcastReceiver",false) == true) {
            // the service start call is coming from network change broadcast receiver
            CommonUtils.printLog("service start call from BR receiver");
            onNetworkChange();
        } else {
            CommonUtils.printLog("service start call NOT from BR receiver");
        }
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
    }

    public void resubscribeToAllTopics() {
        for (String topic : subscribedTopics) {
            MqttSubscriber.subscribeToTopic(topic);
            CommonUtils.printLog("reconnected to topic: " + topic);
        }
    }

    private void sendMessageToClient(Messenger messenger, int val) {
        if (messenger == null) {
            CommonUtils.printLog("Could not send message to client app from service, replyTo is null");
            return;
        }
        Message replyMessage = Message.obtain(null, val);
        try {
            messenger.send(replyMessage);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private Boolean checkConnectivity(Messenger messenger) {
        if (CommonUtils.isNetworkAvailable(getApplicationContext()) == false) {
            sendMessageToClient(messenger, NO_NETWORK_AVAILABLE);
            return false;
        }
        if (SmartCampusMqttClient.isClientConnected() == false) {
            sendMessageToClient(messenger, MQTT_NOT_CONNECTED);
            return false;
        }
        return true;
    }

    private void intiateMqttConnection(boolean sendMessage, Messenger clientMessenger) {
        if (CommonUtils.httpConnectionTest(GOOGLE_INDIA) == false) {
            if (sendMessage == true && clientMessenger != null) {
                sendMessageToClient(clientMessenger, NO_NETWORK_AVAILABLE);
            }
            return;
        }
        isConnecting = true;
        MqttReceiver mqttReceiver = MqttReceiver.getReceiverInstance(getApplicationContext());
        Boolean connectionSuccessful = mqttReceiver.initialiseReceiver();
        if (connectionSuccessful == true) {
            if (sendMessage == true) {
                sendMessageToClient(clientMessenger, MQTT_CONNECTED);
            } else {
                resubscribeToAllTopics();
            }
        } else {
            sendMessageToClient(clientMessenger, UNABLE_TO_CONNECT);
        }
        isConnecting = false;
    }


    private class ConnectToMqtt implements Runnable {
        Messenger clientMessenger;

        ConnectToMqtt(Messenger messenger) {
            this.clientMessenger = messenger;
        }

        @Override
        public void run() {
            CommonUtils.printLog("manual connection req initiated");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            intiateMqttConnection(true, clientMessenger);
            CommonUtils.printLog("manual connection req returned");
        }
    }

    public void onNetworkChange() {
        if (isConnecting == true) {
            CommonUtils.printLog("A Connection req is already in progress.. returning from BR");
            return;
        }
        if (CommonUtils.isNetworkAvailable(getApplicationContext()) == false) {
            CommonUtils.printLog("no internetconnection detected.. returning from BR");
            return;
        }
        if (SmartCampusMqttClient.isClientConnected() == true) {
            CommonUtils.printLog("client already connected ...returning from BR");
            return;
        }
        CommonUtils.printLog("reconnection initiated via BR");
        new Thread(new Runnable() {
            @Override
            public void run() {
                intiateMqttConnection(false, null);
            }
        }).start();
    }


    /* left for referecne purpose
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
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            CommonUtils.showToast(getApplicationContext(), "Service Created");


        }

    }*/

}

