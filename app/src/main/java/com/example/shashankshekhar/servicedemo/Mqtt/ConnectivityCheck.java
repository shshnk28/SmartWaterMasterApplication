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
                logString += " S1-Net Disconnected/";
                MqttLogger.writeDataToLogFile(logString);
                CommonUtils.printLog("internet is disconnected");
                return;
            }
            logString += " S1-Net Connected/";
            /*
            return if net is not available
             */
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(BROKER_ADDRESS, PORT_NUM), 10000);
                if (socket.isConnected()) {
                    // update the logger stating that socket is reachable
                    logString += " S2-Socket Connection to broker established with DNS/";
                    MqttLogger.writeDataToLogFile(logString);
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
                    }
                    socket.close();
                    return;
                } catch (IOException e) {
                    // update the logger failed without DNS
                    logString += " S3-Socket Connection not established withOUT DNS/";

                }

            }
            // try to ping india network
            boolean pingResult = pingTest("www.google.co.in");
            if (pingResult == true) {
                // update the logger
                logString += " S4-Able to Ping GoogleIndia/";
                MqttLogger.writeDataToLogFile(logString);
                return;
            } else {
                logString += " S4-Not able to ping GoogleIndia/";
                pingResult = pingTest("8.8.8.8");
                if (pingResult == true) {
                    logString += " S5-Able to ping Google without DNS/";
                    MqttLogger.writeDataToLogFile(logString);
                    return;
                } else {
                    logString += " S5-Not able to ping Google withOut DNS/";
                    isNetConnected = CommonUtils.isInternetConnectedSec(appContext);
                    if (isNetConnected == true) {
                        logString += " S6-Internet Connected/";
                    } else {
                        logString += " S6-Internet DISconnected/";
                    }
                    logString += " Checks complete/";
                    MqttLogger.writeDataToLogFile(logString);
                    /*
                    check if net is available and write it to the log
                     */
                }

            }
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

        private boolean urlPingTest(String url) {
            try {
                URL url1 = new URL("http://" + url);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestProperty("User-Agent", "Android Application:");
                connection.setRequestProperty("Connection", "close");
                connection.setConnectTimeout(1000 * 10); // mTimeout is in seconds
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    return true;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}

