package com.example.shashankshekhar.servicedemo.Mqtt;

import android.content.Context;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.Logger.MqttLogger;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

/**
 * Created by shashankshekhar on 11/03/16.
 */
public class ConnectivityCheck implements MQTTConstants {
    private static final String BROKER_ADDRESS = "smartx.cds.iisc.ac.in";
    private static final String BROKER_ADDRESS_NO_DNS = "13.76.132.113";
    static int PORT_NUM = 1883;

    private Context appContext;
    //    String BROKER_ADDRESS_CLOUD = "tcp://smartx.cds.iisc.ac.in:1883";
//    String STATIC_BROKER_ADDRESS = "tcp://13.76.132.113:1883";

    /*
    trigger this if the mqtt connect fails it will examine different reasons for the connect.
     */
    ConnectivityCheck(Context applicationContext) {
        this.appContext = applicationContext;
    }

    public void checkNonConnectivityReason() {
        checkNonConnectivityReason(" ");
    }

    public void checkNonConnectivityReason(String logString) {
        MqttLogger.initAppContext(appContext);
        CheckNonConnectivity cnc = new CheckNonConnectivity();
        cnc.setLogString(logString);
        Thread portThread = new Thread(cnc);
        portThread.start();
    }

    private class CheckNonConnectivity implements Runnable {
        private String logString;

        public void setLogString(String logStr) {
            this.logString = logStr;
        }
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            if (CommonUtils.checkMainThread() == true) {
                CommonUtils.printLog("main thread .. returning");
                return;
            }
            logString += " Initialising Checks/";
            CommonUtils.printLog("Initialising Checks");
            boolean isNetConnected = CommonUtils.isNetworkAvailable(appContext);
            if (isNetConnected == false) {
                logString += " S1-Not connected to any network/";
                MqttLogger.writeDataToLogFile(logString);
                CommonUtils.printLog("not connected to any network");
                return;
            }
            logString += " S1-Connected to network/";
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(BROKER_ADDRESS, PORT_NUM), 10000);
                if (socket.isConnected()) {
                    // update the logger stating that socket is reachable
                    logString += " S2-Socket Connection to broker established with DNS/";
                    MqttLogger.writeDataToLogFile(logString);
                } else {
                    logString += " S2-Socket Connection to broker established with DNS but isConnected false/";
                }
                socket.close();
                return;
            } catch (java.io.IOException ex) {
                // update logger socket connection failed
                // try without the DNS now
                logString += " S2-Socket Connection to broker not established with DNS/";
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(BROKER_ADDRESS_NO_DNS, PORT_NUM), 10000);
                    if (socket.isConnected()) {
                        logString += " S3-Socket Connection established withOUT DNS/";
                        MqttLogger.writeDataToLogFile(logString);
                    } else {
                        logString += " S3-Socket Connection established withOUT DNS but isConnected false/";
                    }
                    socket.close();
                    return;
                } catch (IOException e) {
                    // update the logger failed without DNS
                    logString += " S3-Socket Connection not established withOUT DNS/";

                }

            }
            // try to ping india network
            boolean connectionTest = CommonUtils.httpConnectionTest(GOOGLE_INDIA);
            if (connectionTest == true) {
                // update the logger
                logString += " S4-Able to connect to GoogleIndia/";
                MqttLogger.writeDataToLogFile(logString);
                return;
            }
            logString += " S4-Not able to connect GoogleIndia/";
            connectionTest = CommonUtils.httpConnectionTest(GOOGLE_INDIA_NO_DNS);
            if (connectionTest == true) {
                logString += " S5-Able to connect to Google without DNS/";
                MqttLogger.writeDataToLogFile(logString);
                return;
            }
            logString += " S5-Not able to connect to Google withOut DNS/";
            logString += " Checks complete/";
            MqttLogger.writeDataToLogFile(logString);
                    /*
                    check if net is available and write it to the log
                     */
            /*
            ping something like google first if it fails

            try pinging 8.8.8.8  Google primary DNS server
            how to implement ping
            http connection
            https://stackoverflow.com/questions/1443166/android-how-to-check-if-the-server-is-available
            https://stackoverflow.com/questions/2786720/android-service-ping-url
            ping
            https://stackoverflow.com/questions/3905358/how-to-ping-external-ip-from-java-android
             */
        }
        /*
        do not do ping test it fails from behind the proxy
         */
        private boolean pingTest(String ipAddress) {
            Runtime runtime = Runtime.getRuntime();
            try {
                Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + ipAddress);
                int mExitValue = mIpAddrProcess.waitFor();
                System.out.println(" mExitValue " + mExitValue);
                if (mExitValue == 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (InterruptedException ignore) {
                ignore.printStackTrace();
                System.out.println(" Exception:" + ignore);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(" Exception:" + e);
            }
            return false;
        }

    }
}

