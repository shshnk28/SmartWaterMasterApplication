package com.example.shashankshekhar.servicedemo.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private SeekBar keepAliveBar;
    private SeekBar connectionTimeOutBar;
    private SeekBar pingFreqBar;

    private TextView pingFreqTV;
    private TextView keepAliveTV;
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

        // set text views
        pingFreqTV = (TextView) findViewById(R.id.pingFreqTV);
        pingFreqTV.setText("23");
        keepAliveTV = (TextView) findViewById(R.id.keepAliveTime);
        keepAliveTV.setText("11");
        connectionTimeOutTV = (TextView) findViewById(R.id.connectionTO);
        connectionTimeOutTV.setText("42");
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

    //    public void onCheckBoxClicked (View view) {
//        CheckBox tempCheckBox = null;
//        String key = null;
//        /*
//        String CLEAN_SESSION = "cleanSession";
//    String SSL_ENABLED = "SslEnabled";
//    String PUBLISH_CONN_LOGS = "publishConnLogs";
//         */
//        switch (view.getId()) {
//            case R.id.ConnLogsCheckBox:
//                tempCheckBox = publishConnLogs;
//                key = PUBLISH_CONN_LOGS;
//                break;
//            case R.id.cleanSessionCheckbox:
//                tempCheckBox = cleanSession;
//                key = CLEAN_SESSION;
//                break;
//            case R.id.SSLCheckBox:
//                tempCheckBox = enableSSL;
//                key =  SSL_ENABLED;
//                break;
//            default:
//                break;
//        }
//        if (tempCheckBox == null || key == null) {
//            return;
//        }
//        if (tempCheckBox.isChecked()) {
//            connectionOptions.put(key,"true");
//        } else {
//            connectionOptions.put(key, "false");
//        }
//    }
    private void saveFiledstoJsonFile() {
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
            // update the textvew here
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
            if (seekBar.equals(keepAliveBar)) {

            } else if (seekBar.equals(pingFreqBar)) {

            } else {

            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (seekBar.equals(keepAliveBar)) {

            } else if (seekBar.equals(pingFreqBar)) {

            } else {

            }
        }
    }
}
