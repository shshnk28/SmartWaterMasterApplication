package com.example.shashankshekhar.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Handler;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.DBOperations.SCDBOperations;
import com.example.shashankshekhar.servicedemo.FileHandler.ConnOptsJsonHandler;
import com.example.shashankshekhar.servicedemo.FileHandler.MqttLogger;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttConnector;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttPublisher;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttReceiver;
import com.example.shashankshekhar.servicedemo.Mqtt.MqttSubscriber;
import com.example.shashankshekhar.servicedemo.Mqtt.SCMqttClient;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;
import com.example.shashankshekhar.smartcampuslib.SmartXLibConstants;

//import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

import java.util.HashSet;
import java.util.Set;

public class FirstService extends Service implements MQTTConstants,SmartXLibConstants {

    // connection options
    static final int MQTT_CONNECTED = 1;
    static final int UNABLE_TO_CONNECT = 2;
    static final int NO_NETWORK_AVAILABLE = 4;
    static final int MQTT_CONNECTION_IN_PROGRESS = 5;
    static final int MQTT_NOT_CONNECTED = 6;
    static final int DISCONNECT_SUCCESS= 11;

    // publish status
    static final int TOPIC_PUBLISHED = 7;
    static final int ERROR_IN_PUBLISHING = 8;

    // subscription status
    static final int SUBSCRIPTION_SUCCESS = 9;
    static final int SUBSCRIPTION_ERROR = 10;

    // unsubscription status
    static final int UNSUBSCRIPTION_SUCCESS = 12;
    static final int UNSUBSCRIPTION_ERROR = 13;

    final Messenger messenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(final Message message) {
            switch (message.what) {
                case 1: // not being used
                    break;
                case 2: // not being used
                    break;
                case PUBLISH_MESSAGE: // publish global message to a particular topic
                    String topicName = message.getData().getString("topicName");
                    String eventName = message.getData().getString("eventName");
                    String dataString = message.getData().getString("dataString");
                    if (message.replyTo == null) {
                        CommonUtils.printLog("reply messenger is null.. returning");
                        return;
                    }
                    if (checkConnectivity(message.replyTo) == false) {
                        return;
                    }
                    if (topicName == null) {
                        CommonUtils.printLog("topic,null ... returning");
                        sendMessageToClient(message.replyTo, ERROR_IN_PUBLISHING);
                        return;
                    }
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
                    if (message.replyTo == null) {
                        CommonUtils.printLog("no reply messenger .. returning");
                        return;
                    }
                    final String topicName2 = message.getData().getString("topicName");
                    final Messenger subReplyToMessenger = message.replyTo;
                    if (checkConnectivity(message.replyTo) == false) {
                        return;
                    }
                    Runnable success = new Runnable() {
                        @Override
                        public void run() {
                            subscribedTopics.add(topicName2);
                            sendMessageToClient(subReplyToMessenger, SUBSCRIPTION_SUCCESS);
                        }
                    };
                    Runnable failure= new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.printLog("couldnot subscribe to topic : ");
                            sendMessageToClient(subReplyToMessenger, SUBSCRIPTION_ERROR);

                        }
                    };
                    CommonUtils.printLog("trying to subscribe");
                    String subscribeID = MqttSubscriber.subscribeToTopic(getApplicationContext(),topicName2,success,
                            failure);
                    // TODO: 12/11/15 return the subscribeId to the client from here.
                    break;
                case UNSUBSCRIBE_TO_TOPIC:// unsubscribe to a topic
                    if (message.replyTo == null) {
                        CommonUtils.printLog("reply messenger is null.. returning");
                        return;
                    }
                    if (checkConnectivity(message.replyTo) == false) {
                        return;
                    }
                    final Messenger unsubReplyToMessenger = message.replyTo;
                    topicName = message.getData().getString("topicName");
                    if (topicName == null){
                        sendMessageToClient(unsubReplyToMessenger, UNSUBSCRIPTION_ERROR);
                    }
                    Runnable successSubscribe = new Runnable() {
                        @Override
                        public void run() {
                                sendMessageToClient(unsubReplyToMessenger, UNSUBSCRIPTION_SUCCESS);
                        }
                    };
                    Runnable failureSubscribe= new Runnable() {
                        @Override
                        public void run() {
                                sendMessageToClient(unsubReplyToMessenger, UNSUBSCRIPTION_ERROR);

                        }
                    };
                    MqttSubscriber.unsubscribeToTopic(getApplicationContext(),topicName,successSubscribe,
                            failureSubscribe);
                    subscribedTopics.remove(topicName);
                    break;
//                case CHECK_SERVICE: // check if  service is running
//                    boolean isRunning = CommonUtils.isMyServiceRunning(FirstService.class, getApplicationContext());
//                    if (isRunning) {
//                        CommonUtils.showToast(getApplicationContext(), "running");
//                    } else {
//                        CommonUtils.showToast(getApplicationContext(), "Not running");
//                    }
//                    break;
                case CHECK_MQTT_CONNECTION: // check if the mqtt client is connected
                    if (message.replyTo == null) {
                        CommonUtils.printLog("reply messenger is null.. returning");
                        return;
                    }
                    if (checkConnectivity(message.replyTo) == false) {
                        return;
                    }
                    int status;
                    if (MqttConnector.isConnecting) {
                        status= MQTT_CONNECTION_IN_PROGRESS;
                    }
                    else if (SCMqttClient.isMqttConnected()) {
                        status = MQTT_CONNECTED;
                    } else {
                        status = MQTT_NOT_CONNECTED;
                    }
                    CommonUtils.printLog("sending status back: " + status);
                    sendMessageToClient(message.replyTo, status);
                    break;
                case CONNECT_MQTT: // reconnect mqtt
                    CommonUtils.printLog("connect to mqtt req received");
                    if (message.replyTo == null) {
                        CommonUtils.printLog("reply messenger is null.. returning");
                        return;
                    }
                    if (MqttConnector.isConnecting == true) {
                        sendMessageToClient(message.replyTo, MQTT_CONNECTION_IN_PROGRESS);
                        return;
                    }
                    if (SCMqttClient.isMqttConnected() == true) {
                        sendMessageToClient(message.replyTo, MQTT_CONNECTED);
                        return;
                    }
                    if (CommonUtils.isNetworkAvailable(getApplicationContext()) == false) {
                        sendMessageToClient(messenger, NO_NETWORK_AVAILABLE);
                        return;
                    }
                    ConnectToMqtt connectToMqtt = new ConnectToMqtt(message.replyTo);
                    Thread mqttConnector = new Thread(connectToMqtt);
                    mqttConnector.start();
                    break;
                case DISCONNECT_MQTT: // disconnect mqtt
                    if (message.replyTo == null) {
                        sendMessageToClient(message.replyTo, UNABLE_TO_CONNECT);
                        return;
                    }
                    MqttConnector.disconnectMqtt();
                    sendMessageToClient(message.replyTo,DISCONNECT_SUCCESS);
                    break;
                default:
                    CommonUtils.printLog("unknown message received from client");
                    super.handleMessage(message);
            }
        }
    }

    public FirstService() {
    }

    Set<String> subscribedTopics = new HashSet<>();


    @Override
    public IBinder onBind(Intent intent) {
        CommonUtils.printLog("on bind called");
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MqttLogger.initAppContext(getApplicationContext());
        CommonUtils.printLog("onStart called in service");
        if (intent != null && intent.getBooleanExtra("fromBroadcastReceiver",false) == true) {
            // the service start call is coming from network change broadcast receiver
            CommonUtils.printLog("service start call from BR receiver");
            MqttLogger.writeDataToLogFile("reconnection initiated from BR");
            MqttLogger.writeDataToTempLogFile("reconnection initiated from BR");
            onNetworkChange();
        } else if (intent != null && intent.getBooleanExtra("fromReconnecter",false) == true) {
            CommonUtils.printLog("service start call from reconnector");
            MqttLogger.writeDataToLogFile("reconnection initiated from reconnector thread");
            MqttLogger.writeDataToTempLogFile("reconnection initiated from reconnector thread");
            initiateMqttOnNewThread();
        }
        else {
//            intent.putExtra("username", userName);
            CommonUtils.printLog("manual start service call");
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        CommonUtils.printLog("ONCreate called in service");
        CommonUtils.setClientId(getApplicationContext());
        /*
        write the connection options to json
         */
        ConnOptsJsonHandler.writeDefaultConnectionSettings();

        // for testing
//        SCDBOperations.initDBAppContext(getApplicationContext());
//        SCDBOperations.writeToDB();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        CommonUtils.printLog("on unbind called");
        return false;
    }

    @Override
    public void onDestroy() {
        MqttConnector.cancelAlarm();
        CommonUtils.printLog("SERVICE DESTROYED!!");
        MqttConnector.disconnectMqtt();
        MqttLogger.initAppContext(getApplicationContext());
        MqttLogger.writeDataToLogFile("Mqtt Service DESTROYED!!");

    }

//    public void resubscribeToAllTopics() {
//        for (String topic : subscribedTopics) {
//            MqttSubscriber.subscribeToTopic(topic);
//        }
//    }

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
        if (SCMqttClient.isMqttConnected() == false) {
            sendMessageToClient(messenger, MQTT_NOT_CONNECTED);
            return false;
        }
        return true;
    }

    private void intiateMqttConnection( final boolean sendMessage, final Messenger clientMessenger) {
//        if (CommonUtils.httpConnectionTest(GOOGLE_INDIA) == false) {
//            MqttLogger.initAppContext(getApplicationContext());
//            MqttLogger.writeDataToLogFile("Not able to ping Google India");
//            if (sendMessage == true && clientMessenger != null) {
//                sendMessageToClient(clientMessenger, NO_NETWORK_AVAILABLE);
//            }
//            return;
//        }
        Runnable success = new Runnable() {
            @Override
            public void run() {
                new MqttReceiver(getApplicationContext());
                if (sendMessage == true) {
                    sendMessageToClient(clientMessenger, MQTT_CONNECTED);
                }
//                else {
//                    resubscribeToAllTopics();
//                }
            }
        };
        Runnable failure= new Runnable() {
            @Override
            public void run() {
                if (sendMessage == true) {
                    sendMessageToClient(clientMessenger, UNABLE_TO_CONNECT);
                }
            }
        };
        MqttConnector.connectToMqttClient(success, failure, getApplicationContext());
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
        }
    }

    public void onNetworkChange() {
        MqttLogger.initAppContext(getApplicationContext());
        if (MqttConnector.isConnecting == true) {
            MqttLogger.writeDataToLogFile("isConnecting true. Returning from BR call");
            CommonUtils.printLog("A Connection req is already in progress.. returning from BR");
            return;
        }
        if (CommonUtils.isNetworkAvailable(getApplicationContext()) == false) {
            MqttLogger.writeDataToLogFile("No network available. Returning from BR call");
            CommonUtils.printLog("no internetconnection detected.. returning from BR");
            return;
        }
        if (SCMqttClient.isMqttConnected() == true) {
            MqttLogger.writeDataToLogFile("Mqtt already Connected. Returning from BR call");
            CommonUtils.printLog("client already connected ...returning from BR");
            return;
        }
        CommonUtils.printLog("reconnection initiated via BR");
        initiateMqttOnNewThread();
    }
    public void initiateMqttOnNewThread () {
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

