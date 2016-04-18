package com.example.shashankshekhar.servicedemo.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Switch;

import com.example.shashankshekhar.servicedemo.R;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import java.util.HashMap;

public class AdminActivity extends AppCompatActivity {
    private SeekBar keepAliveBar;
    private SeekBar connectionTimeOutBar;
    private CheckBox cleanSession;
    private CheckBox enableSSL;
    private CheckBox publishConnLogs;
    private HashMap<String,String> connectionOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        // configure seek bars
        keepAliveBar = (SeekBar)findViewById(R.id.seekKeepAlive);
        connectionTimeOutBar = (SeekBar)findViewById(R.id.seekConnTimeOut);
        keepAliveBar.setOnSeekBarChangeListener(new SeekbarListener());
        connectionTimeOutBar.setOnSeekBarChangeListener(new SeekbarListener());

        // configure chec boxes
        cleanSession = (CheckBox)findViewById(R.id.cleanSessionCheckbox);
        enableSSL = (CheckBox)findViewById(R.id.SSLCheckBox);
        publishConnLogs = (CheckBox)findViewById(R.id.ConnLogsCheckBox);

        // read from json and initialise the fields here as well
    }
    @Override
    protected void onResume () {
        super.onResume();
        /*
        init it every time the screen loads
         */
        connectionOptions = null;
    }

    public void saveAndReconnect (View view) {
        /*
        write the UI components to json file and reconnect to broker
        implement the connection UI here with connecting dialog
         */
    }
    public void onCheckBoxClicked (View view) {
        switch (view.getId()) {
            case R.id.ConnLogsCheckBox:
                // write to json
                publishConnLogs.isChecked();
                break;
            case R.id.cleanSessionCheckbox:
                break;
            case R.id.SSLCheckBox:
                break;
            default:
                break;
        }
    }
    private class SeekbarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar.equals(keepAliveBar)) {
                CommonUtils.printLog("progress changed keepalive bar: " + progress);
            } else {

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (seekBar.equals(keepAliveBar)) {

            } else {
                CommonUtils.printLog("start tracking conn bar");
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (seekBar.equals(keepAliveBar)) {

            } else {
                CommonUtils.printLog("stop tracking conn bar");
            }
        }
    }
}
