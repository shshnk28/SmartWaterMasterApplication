package com.example.shashankshekhar.servicedemo.Activities;

import android.app.ProgressDialog;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.FileHandler.ConnOptsJsonHandler;
import com.example.shashankshekhar.servicedemo.IncomingHandler;
import com.example.shashankshekhar.servicedemo.Interfaces.ServiceCallback;
import com.example.shashankshekhar.servicedemo.R;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;
import com.example.shashankshekhar.smartcampuslib.ServiceAdapter;
import com.example.shashankshekhar.smartcampuslib.SmartXLibConstants;


public class AdminActivity extends AppCompatActivity implements MQTTConstants,ServiceCallback,SmartXLibConstants {
    private static final int PING_FREQ_MAX = 720; // minutes
    private static final int KEEP_ALIVE_INTERVAL_MAX = 720; // minutes
    private static final int CONNECTION_TIMEOUT_MAX = 120; // seconds
    private SeekBar keepAliveBar;
    private SeekBar connectionTimeOutBar;
    private SeekBar pingFreqBar;

    private EditText pingFreqTV;
    private EditText keepAliveTV;
    private TextView connectionTimeOutTV;

    private Switch cleanSession;
    private Switch enableSSL;
    private Switch publishConnLogs;

    private EditText brokerAddress;
    private EditText portNum;
    private EditText userName;
    private EditText pwd;
    ServiceAdapter serviceAdapter;

    ProgressDialog connectingDialog;
    Messenger clientMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        // configure seek bars
        keepAliveBar = (SeekBar) findViewById(R.id.seekKeepAlive);
        connectionTimeOutBar = (SeekBar) findViewById(R.id.seekConnTimeOut);
        pingFreqBar = (SeekBar) findViewById(R.id.pingFreq);

        keepAliveBar.setOnSeekBarChangeListener(new SeekbarListener());
        connectionTimeOutBar.setOnSeekBarChangeListener(new SeekbarListener());
        pingFreqBar.setOnSeekBarChangeListener(new SeekbarListener());

        keepAliveBar.setMax(KEEP_ALIVE_INTERVAL_MAX);
        connectionTimeOutBar.setMax(CONNECTION_TIMEOUT_MAX);
        pingFreqBar.setMax(PING_FREQ_MAX);

        // set text views
        pingFreqTV = (EditText) findViewById(R.id.pingFreqTV);
        pingFreqTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeSeekBarAndTextView(pingFreqBar,pingFreqTV,PING_FREQ_MAX);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        keepAliveTV = (EditText) findViewById(R.id.keepAliveTime);
        keepAliveTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeSeekBarAndTextView(keepAliveBar,keepAliveTV,KEEP_ALIVE_INTERVAL_MAX);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        connectionTimeOutTV = (TextView) findViewById(R.id.connectionTO);

        // configure chec boxes
        cleanSession = (Switch) findViewById(R.id.cleanSessionSwitch);
        enableSSL = (Switch) findViewById(R.id.SSLSwitch);
        publishConnLogs = (Switch) findViewById(R.id.ConnLogsSwitch);

        // configure edit texts
        brokerAddress = (EditText) findViewById(R.id.brokerAddress);
        portNum = (EditText) findViewById(R.id.portNum);
        userName = (EditText) findViewById(R.id.userName);
        pwd = (EditText) findViewById(R.id.pwd);

        clientMessenger = new Messenger(new IncomingHandler(getApplicationContext(), this));
        serviceAdapter = new  ServiceAdapter(getApplicationContext());
    }
    @Override
    public void onResume () {
        populateUIFromJson();
        super.onResume();
    }

    public void saveAndReconnect(View view) {
        /*
        write the UI components to json file and reconnect to broker
        implement the connection UI here with connecting dialog
         */
        connectingDialog = ProgressDialog.show(this, "Please Wait...", "Reconnecting");
        connectingDialog.setCancelable(false);
        saveFieldstoJsonFile();
        disconnectMqtt();

        /*
        use the process dialog here. issue a reconnect call as done in main activity
         */

    }
    public void messageReceivedFromService(int number) {
        String toastStr = null;
        switch (number) {
            case MQTT_CONNECTED:
                toastStr = "Connected";
                connectingDialog.dismiss();
                break;
            case UNABLE_TO_CONNECT:
                toastStr = "Not Connected";
                connectingDialog.dismiss();
                break;
            case NO_NETWORK_AVAILABLE:
                toastStr = "No network";
                connectingDialog.dismiss();
                break;
            case MQTT_CONNECTION_IN_PROGRESS:
                toastStr = "Connection in progress";
                connectingDialog.dismiss();
                break;
            case DISCONNECT_SUCCESS:
                CommonUtils.printLog("mqtt disconnected before reconnecting");
                connectMqtt();// start the reconnection
                break;
            default:
                toastStr = "switch case unknown";
        }
        if (toastStr!=null) {
            CommonUtils.showToast(getApplicationContext(),toastStr);
        }

    }
    @Override
    public void serviceConnected() {
    }

    @Override
    public void serviceDisconnected() {
        CommonUtils.printLog("service disconnecetd");
    }

    public void resetConnectionOptions(View view) {
        // reload the UI elements from JSON
        populateUIFromJson();
    }

    private void saveFieldstoJsonFile() {
        String brokerAdd = brokerAddress.getText().toString().trim();
        String port = portNum.getText().toString().trim();
        String username = userName.getText().toString().trim();
        String pwdtemp = pwd.getText().toString().trim();
        if (brokerAdd == null || brokerAdd.isEmpty() ||
                port == null || port.isEmpty() ||
                username == null || username.isEmpty() ||
                pwdtemp == null || pwdtemp.isEmpty()) {
            CommonUtils.showToast(getApplicationContext(), "Enter a valid field in text");
            resetConnectionOptions(null);
            return;
        }


        String keepAlive = keepAliveBar.getProgress() + "";
        String connectionTO = connectionTimeOutBar.getProgress() + "";
        String pingfreq = pingFreqBar.getProgress() + "";
        if (pingFreqBar.getProgress() > keepAliveBar.getProgress()) {
            CommonUtils.showToast(getApplicationContext(), "ping freq should be less than keep alive. Retry");
            resetConnectionOptions(null);
            return;
        }
        ConnOptsJsonHandler.initJsonWriter();
        ConnOptsJsonHandler.writeToJsonFile(BROKER_ADDRESS_KEY, brokerAdd);
        ConnOptsJsonHandler.writeToJsonFile(PORT_NUM_KEY, port);
        ConnOptsJsonHandler.writeToJsonFile(USER_NAME_KEY, username);
        ConnOptsJsonHandler.writeToJsonFile(PASSWORD_KEY, pwdtemp);

        ConnOptsJsonHandler.writeToJsonFile(KEEP_ALIVE_KEY, keepAlive);
        ConnOptsJsonHandler.writeToJsonFile(CONNECTION_TIME_OUT_KEY, connectionTO);
        ConnOptsJsonHandler.writeToJsonFile(PING_FREQ_KEY, pingfreq);

        String ssl = enableSSL.isChecked() + "";
        String session = cleanSession.isChecked() + "";
        String connLogs = publishConnLogs.isChecked() + "";
        ConnOptsJsonHandler.writeToJsonFile(SSL_ENABLED_KEY, ssl);
        ConnOptsJsonHandler.writeToJsonFile(CLEAN_SESSION_KEY, session);
        ConnOptsJsonHandler.writeToJsonFile(PUBLISH_CONN_LOGS_KEY, connLogs);
        ConnOptsJsonHandler.closeJsonFile();
    }

    private class SeekbarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            if (seekBar.equals(keepAliveBar)) {
                keepAliveTV.setText(progress + "");
            } else if (seekBar.equals(pingFreqBar)) {
                pingFreqTV.setText(progress + "");
            } else {
                connectionTimeOutTV.setText(progress + "");
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
    private void changeSeekBarAndTextView (SeekBar seekBar,TextView textView, int interval) {
        String txt = textView.getText().toString();
        txt = txt.trim();
        if (txt == null || txt.isEmpty()) {
            return;
        }
        if (Integer.parseInt(txt) > interval) {
            CommonUtils.showToast(getApplicationContext(), "Ping freq limited to " + interval + " Minutes");
            textView.setText("100");
            seekBar.setProgress(100);
            return;
        }
        seekBar.setProgress(Integer.parseInt(txt));
    }
    private void populateUIFromJson () {
        if (ConnOptsJsonHandler.doesConfigFileExists() == false) {
            CommonUtils.showToast(getApplicationContext(),"confing file missing.Restart app");
            return;
        }
        brokerAddress.setText(ConnOptsJsonHandler.readFromJsonFile(BROKER_ADDRESS_KEY));
        portNum.setText(ConnOptsJsonHandler.readFromJsonFile(PORT_NUM_KEY));
        userName.setText(ConnOptsJsonHandler.readFromJsonFile(USER_NAME_KEY));
        pwd.setText(ConnOptsJsonHandler.readFromJsonFile(PASSWORD_KEY));

        keepAliveTV.setText(ConnOptsJsonHandler.readFromJsonFile(KEEP_ALIVE_KEY));
        pingFreqTV.setText(ConnOptsJsonHandler.readFromJsonFile(PING_FREQ_KEY));
        connectionTimeOutTV.setText(ConnOptsJsonHandler.readFromJsonFile(CONNECTION_TIME_OUT_KEY));
        connectionTimeOutBar.setProgress(Integer.parseInt(ConnOptsJsonHandler.readFromJsonFile(CONNECTION_TIME_OUT_KEY)));

        cleanSession.setChecked(Boolean.valueOf(ConnOptsJsonHandler.readFromJsonFile(CLEAN_SESSION_KEY)));
        enableSSL.setChecked(Boolean.valueOf(ConnOptsJsonHandler.readFromJsonFile(SSL_ENABLED_KEY)));
        publishConnLogs.setChecked(Boolean.valueOf(ConnOptsJsonHandler.readFromJsonFile(PUBLISH_CONN_LOGS_KEY)));
    }
    private boolean isServiceRunning( ) {
        if (serviceAdapter.serviceConnected() == false) {
            CommonUtils.printLog("service not connected .. returning");
            CommonUtils.showToast(getApplicationContext(), "Service not running");
            return false;
        }
        return true;
    }
    private void connectMqtt() {
        if (isServiceRunning() == false) {
            return;
        }
        serviceAdapter.connectMqtt(clientMessenger);
    }
    private void disconnectMqtt () {
        if (isServiceRunning() == false) {
            if (connectingDialog.isShowing()) {
                connectingDialog.dismiss();
            }
            return;
        }
        serviceAdapter.disconnectMqtt(clientMessenger);
    }
}
