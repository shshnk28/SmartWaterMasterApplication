package com.example.shashankshekhar.servicedemo.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.shashankshekhar.servicedemo.Constants.MQTTConstants;
import com.example.shashankshekhar.servicedemo.FileHandler.ConnOptsJsonHandler;
import com.example.shashankshekhar.servicedemo.R;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

public class AdminActivity extends AppCompatActivity implements MQTTConstants {
    private static final int PING_FREQ_MAX = 720; // minutes
    private static final int KEEP_ALIVE_INTERVAL_MAX = 720; // minutes
    private static final int CONNECTION_TIMEOUT_MAX = 120; // seconds
    private SeekBar keepAliveBar;
    private SeekBar connectionTimeOutBar;
    private SeekBar pingFreqBar;

    private EditText pingFreqTV;
    private EditText keepAliveTV;
    private TextView connectionTimeOutTV;

    private CheckBox cleanSession;
    private CheckBox enableSSL;
    private CheckBox publishConnLogs;

    private EditText brokerAddress;
    private EditText portNum;
    private EditText userName;
    private EditText pwd;

    //    private HashMap<String,String> connectionOptions;
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txt = pingFreqTV.getText().toString();
                txt = txt.trim();
                if (txt == null || txt.isEmpty()) {
                    return;
                }
                if (Integer.parseInt(txt) > PING_FREQ_MAX) {
                    CommonUtils.showToast(getApplicationContext(),"Ping freq limited to" +PING_FREQ_MAX+ "Minutes");
                    pingFreqTV.setText("100");
                    pingFreqBar.setProgress(100);
                } else {
                    pingFreqBar.setProgress(Integer.parseInt(txt));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        keepAliveTV = (EditText) findViewById(R.id.keepAliveTime);
        keepAliveTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txt = keepAliveTV.getText().toString();
                txt = txt.trim();
                if (txt == null || txt.isEmpty()) {
                    return;
                }
                if (Integer.parseInt(txt) > KEEP_ALIVE_INTERVAL_MAX) {
                    CommonUtils.showToast(getApplicationContext(),"Ping freq limited to "+ KEEP_ALIVE_INTERVAL_MAX+" Minutes");
                    keepAliveTV.setText("100");
                    keepAliveBar.setProgress(100);
                    return;
                }
                keepAliveBar.setProgress(Integer.parseInt(txt));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        connectionTimeOutTV = (TextView) findViewById(R.id.connectionTO);

        // configure chec boxes
        cleanSession = (CheckBox) findViewById(R.id.cleanSessionCheckbox);
        enableSSL = (CheckBox) findViewById(R.id.SSLCheckBox);
        publishConnLogs = (CheckBox) findViewById(R.id.ConnLogsCheckBox);

        // configure edit texts
        brokerAddress = (EditText) findViewById(R.id.brokerAddress);
        portNum = (EditText) findViewById(R.id.portNum);
        userName = (EditText) findViewById(R.id.userName);
        pwd = (EditText) findViewById(R.id.pwd);



        // read from json and initialise the fields here as well
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        init it every time the screen loads
         */
    }

    public void saveAndReconnect(View view) {
        /*
        write the UI components to json file and reconnect to broker
        implement the connection UI here with connecting dialog
         */

    }

    public void resetConnectionOptions(View view) {
        // reload the UI elements from JSON
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
            // call to reset the fields
            return;
        }
        ConnOptsJsonHandler.writeToJsonFile(BROKER_ADDRESS, brokerAdd);
        ConnOptsJsonHandler.writeToJsonFile(PORT_NUM, port);
        ConnOptsJsonHandler.writeToJsonFile(USER_NAME, username);
        ConnOptsJsonHandler.writeToJsonFile(PASSWORD, pwdtemp);

        String keepAlive = keepAliveBar.getProgress() + "";
        String connectionTO = connectionTimeOutBar.getProgress() + "";
        String pingfreq = pingFreqBar.getProgress() + "";
        if (pingFreqBar.getProgress() > keepAliveBar.getProgress()) {
            CommonUtils.showToast(getApplicationContext(), "ping freq should be less than keep alive ");
        }
        ConnOptsJsonHandler.writeToJsonFile(KEEP_ALIVE, keepAlive);
        ConnOptsJsonHandler.writeToJsonFile(CONNECTION_TIME_OUT, connectionTO);
        ConnOptsJsonHandler.writeToJsonFile(PING_FREQ, pingfreq);

        String ssl = enableSSL.isChecked() + "";
        String session = cleanSession.isChecked() + "";
        String connLogs = publishConnLogs.isChecked() + "";
        ConnOptsJsonHandler.writeToJsonFile(SSL_ENABLED, ssl);
        ConnOptsJsonHandler.writeToJsonFile(CLEAN_SESSION, session);
        ConnOptsJsonHandler.writeToJsonFile(PUBLISH_CONN_LOGS, connLogs);
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
}
