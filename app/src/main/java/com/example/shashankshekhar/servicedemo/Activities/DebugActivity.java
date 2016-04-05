package com.example.shashankshekhar.servicedemo.Activities;

import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.R;
import com.example.shashankshekhar.servicedemo.SCServiceConnector;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class DebugActivity extends AppCompatActivity implements MQTTConstants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_check);
//        debugIntent.putExtra("messengerObj", messenger);
//        debugIntent.putExtra("bound", mBound);
        if (SCServiceConnector.messenger == null) {
            CommonUtils.printLog("messenger is null in debug screen");
        }
    }
    public void checkGoogle(View view) {
        pingTest("www.google.co.in");
    }
    public void checkWODNS(View view) {
        pingTest("8.8.8.8");
    }
    public void checkBroker(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                try {
                    Long start  = System.currentTimeMillis();
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress("smartx.cds.iisc.ac.in", 1883), 10000);
                    if (socket.isConnected()) {
                        CommonUtils.printLog("socket connection ok");
                        CommonUtils.printLog(" time taken: " + (System.currentTimeMillis() - start));
                        showToastOnUIThread("SOCCONN ok");
                    }
                    socket.close();
                    return;
                } catch (java.io.IOException ex) {
                    CommonUtils.printLog("exception in connecting to socket");
                    showToastOnUIThread("SOCCONN NOT ok");
                }
            }
        }).start();

    }
    public void checkBrokerWODNS(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                try {
                    Long start  = System.currentTimeMillis();
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress("13.76.132.113", 1883), 10000);
                    if (socket.isConnected()) {
                        CommonUtils.printLog("socket connection ok wo dns");
                        CommonUtils.printLog(" time taken: " + (System.currentTimeMillis() - start));
                        showToastOnUIThread("SOCCONN ok");
                    }
                    socket.close();
                    return;
                } catch (java.io.IOException ex) {
                    CommonUtils.printLog("exception in connecting to socket wo dns");
                    showToastOnUIThread("SOCCONN Not ok");
                }
            }
        }).start();
    }
    private void pingTest( final String ipAddress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                Runtime runtime = Runtime.getRuntime();
                Process mIpAddrProcess = null;
                try {
                    Long start  = System.currentTimeMillis();
                    mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + ipAddress);
                    int mExitValue = mIpAddrProcess.waitFor();
                    if (mExitValue == 0) {
                        CommonUtils.printLog("able to ping: "+ ipAddress);
                        CommonUtils.printLog("time taken: " + (System.currentTimeMillis() - start));
                        showToastOnUIThread("ping success");
                        mIpAddrProcess.destroy();
                        return;
                    } else {
                        CommonUtils.printLog("not able to ping1: " + ipAddress);
                    }
                } catch (InterruptedException ignore) {
                    ignore.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mIpAddrProcess != null) {
                    mIpAddrProcess.destroy();
                }
                CommonUtils.printLog("not able to ping2: " + ipAddress);
                showToastOnUIThread("ping failed");
            }
        }).start();
    }
    public void checkHttpConn (View view) {
        httpConnectionTest("www.google.co.in");
    }
    public  void checkService (View view) {
        if (SCServiceConnector.messenger == null || SCServiceConnector.mBound == false ) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(), "not running");
            return;
        }
        Message message = Message.obtain(null,CHECK_SERVICE);
        try {
            SCServiceConnector.messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            CommonUtils.printLog("remote Exception,Could not send message");
        }
    }
    public void checkConnection (View view) {
        if (SCServiceConnector.messenger == null || SCServiceConnector.mBound == false ) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(), "Service not running");
            return;
        }
        Message message = Message.obtain(null,CHECK_MQTT_CONNECTION);
        try {
            SCServiceConnector.messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            CommonUtils.printLog("remote Exception,Could not send message");
        }
    }
    private void httpConnectionTest(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                try {
                    URL url1 = new URL("http://" + url);
                    Long start1 = System.currentTimeMillis();
                    HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                    connection.setConnectTimeout(1000 * 10); // mTimeout is in seconds
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        CommonUtils.printLog("http connection success");
                        CommonUtils.printLog("time taken: " + (System.currentTimeMillis() - start1));
                        showToastOnUIThread("http success");
                        return;
                    }
                } catch (MalformedURLException e) {
                    CommonUtils.printLog("malformed url exception");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CommonUtils.printLog("can not establish http connection ");
                showToastOnUIThread("could not establish http connection");
            }
        }).start();
    }
    private void showToastOnUIThread(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                CommonUtils.showToast(getApplicationContext(), message);
            }
        });
    }
}