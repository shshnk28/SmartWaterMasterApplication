package com.example.shashankshekhar.servicedemo.Activities;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;

import android.view.View;


import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.R;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import com.example.shashankshekhar.smartcampuslib.IncomingHandler;
import com.example.shashankshekhar.smartcampuslib.Interfaces.ServiceCallback;
import com.example.shashankshekhar.smartcampuslib.Interfaces.ServiceStatusCallback;

import com.example.shashankshekhar.smartcampuslib.SCServiceConnector;
import com.example.shashankshekhar.smartcampuslib.ServiceAdapter;
import com.example.shashankshekhar.smartcampuslib.SmartXLibConstants;

import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements MQTTConstants, ServiceCallback,SmartXLibConstants,ServiceStatusCallback
         {
    //    ProgressDialog connectingDialog;
    Messenger clientMessenger;
    String userName;
    ProgressDialog connectingDialog;
    SCServiceConnector serviceConnector = new SCServiceConnector(this);
    ServiceAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isStoragePermissionGranted())
            serviceAdapter = new ServiceAdapter(getApplicationContext());
//        CommonUtils.printLog(" Main activity process id: " + android.os.Process.myPid());
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
        connectingDialog.dismiss();
        String toastStr;
        switch (number) {
            case MQTT_CONNECTED:
                toastStr = "Mqtt Connected";
                // SUBSCRIBE call
//                Message message = Message.obtain(null, SUBSCRIBE_TO_TOPIC);
//                Bundle bundle = new Bundle();
//                bundle.putString("topicName",TEST_TOPIC1);
//                message.setData(bundle);
//                message.replyTo = clientMessenger;
//                try {
//                    SCServiceConnector.messenger.send(message);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                    CommonUtils.printLog("remote Exception,Could not send message");
//                }
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
            case SUBSCRIPTION_ERROR:
                toastStr = "Subscription failed";
                break;
            case SUBSCRIPTION_SUCCESS:
                toastStr = "Subscription success";
                break;
            default:
                toastStr = "switch case unknown";

        }
        CommonUtils.printLog("number: " + number);
        showToastOnUIThread(toastStr);
    }

    @Override
    public void serviceConnected() {
        CommonUtils.printLog("connection callback received in main activity");
        showDialogAndConnectToMqtt(null);
    }

    @Override
    public void serviceDisconnected() {
        CommonUtils.printLog("service disconnecetd");
    }

    public void startAndBindService(View view) {
        if (serviceAdapter.serviceConnected()) {
            CommonUtils.printLog("service connected already ");
            CommonUtils.showToast(getApplicationContext(), "Service already connected");
            return;
        }

//        EditText editText = (EditText) findViewById(R.id.textView1);
//        userName = editText.getText().toString();
//        userName = userName.trim();
//        if (userName == null || userName.isEmpty()) {
//            CommonUtils.showToast(getApplicationContext(), "Pls enter name");
//            return;
//        }
        writeToSharedPreferences(USER_NAME_KEY,"Anon");
        serviceAdapter.startAndBindToService(serviceConnector);
//        ComponentName componentName1 = startService(intent);
//        Boolean bindSuccess = bindService(intent, serviceConnector, Context.BIND_AUTO_CREATE);

    }

    public void showDialogAndConnectToMqtt(View view) {
        if (serviceAdapter.serviceConnected() == false) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(), "Service not running");
            return;
        }
        connectingDialog = ProgressDialog.show(this, "Please Wait...", "Connecting to broker");
        connectingDialog.setCancelable(false);
        clientMessenger = new Messenger(new IncomingHandler(getApplicationContext(), this));
        connectMqtt();
//        Intent publisherServiceIntent = new Intent(getApplicationContext(), PublisherService.class);
//        publisherServiceIntent.putExtra("userName", userName);
//        startService(publisherServiceIntent);
    }

    public void connectMqtt() {
        CommonUtils.printLog("connection req sent in main activity");
        serviceAdapter.connectMqtt(clientMessenger);
    }

    public void launchDebugScreen(View view) {
        Intent debugIntent = new Intent(this, DebugActivity.class);
        startActivity(debugIntent);
    }
    public void launchAdminScreen(View view) {
        Intent adminIntent = new Intent(this, AdminActivity.class);
        startActivity(adminIntent);
    }
    public void launchStatsScreen(View view) {
        Intent statsIntent = new Intent(this, StatsActivity.class);
        startActivity(statsIntent);
    }
    public void launchPubSubScreen(View view) {
        Intent pubsubIntent = new Intent(this, PubSubActivity.class);
        startActivity(pubsubIntent);
    }



    private void showToastOnUIThread(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                CommonUtils.showToast(getApplicationContext(), message);
            }
        });
    }

    private void writeToSharedPreferences (String key, String val) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,val);
        editor.commit();

    }

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if(grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]== PackageManager.PERMISSION_GRANTED){
             CommonUtils.printLog("Permission: "+permissions[0]+ " was "+grantResults[0]);
             CommonUtils.printLog("Permission: "+permissions[1]+ " was "+grantResults[1]);
             //resume tasks needing this permission
             serviceAdapter = new ServiceAdapter(getApplicationContext());
         }
         else
         {
             new AlertDialog.Builder(MainActivity.this)
                     .setTitle("Error!")
                     .setMessage("Allow storage permissions and try again!")
                     .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             finish();
                         }
                     }).create().show();
         }
     }

     public  boolean isStoragePermissionGranted() {
         if (Build.VERSION.SDK_INT >= 23) {
             if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                     == PackageManager.PERMISSION_GRANTED &&
                     checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                             == PackageManager.PERMISSION_GRANTED) {
                 CommonUtils.printLog("Read and Write Storage permission is granted");
                 return true;
             } else {

                 CommonUtils.printLog("Read and Write Storage permission is revoked");
                 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                 return false;
             }
         }
         else { //permission is automatically granted on sdk<23 upon installation
             CommonUtils.printLog("Read and Write Storage permission is granted");
             return true;
         }
     }
}
