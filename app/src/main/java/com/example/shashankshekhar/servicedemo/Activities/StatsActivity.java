package com.example.shashankshekhar.servicedemo.Activities;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.shashankshekhar.servicedemo.DBOperations.SCDBOperations;
import com.example.shashankshekhar.servicedemo.FileHandler.ConnOptsJsonHandler;
import com.example.shashankshekhar.servicedemo.R;
import com.example.shashankshekhar.servicedemo.UtilityClasses.CommonUtils;

import org.w3c.dom.Text;

public class StatsActivity extends AppCompatActivity {
    TextView jsonPath;
    TextView subscribedTopics;
    TextView publishedMessagesList;
    TextView receivedMessagesList;
    TextView subscribedTopicsList;
    TextView messagesSentTV;
    TextView messageReceivedTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        jsonPath = (TextView)findViewById(R.id.jsonPath);
        jsonPath.setText(ConnOptsJsonHandler.jsonPath());
        subscribedTopics  = (TextView)findViewById(R.id.subscribedTopicsList);
        publishedMessagesList = (TextView)findViewById(R.id.publishedMessagesList);
        receivedMessagesList = (TextView)findViewById(R.id.receivedMessagesList);
        subscribedTopicsList = (TextView)findViewById(R.id.subscribedTopicsList);
        messagesSentTV = (TextView)findViewById(R.id.MessagesSentCount);
        messageReceivedTV= (TextView)findViewById(R.id.MessagesReceivedCount);
        populateUI();
    }
    private void populateUI () {
        SCDBOperations.initDBAppContext(getApplicationContext());
        Cursor sentMessages = SCDBOperations.returnSentMessageDataFromDB();
        if (sentMessages.moveToFirst()){
            do {
                publishedMessagesList.append(sentMessages.getString(0) + " : " + sentMessages.getString(1) + "\n");
            } while (sentMessages.moveToNext());

        } else {
            publishedMessagesList.setText("Published messages list is empty");
            CommonUtils.printLog("Published messages list is empty");
        }
        Cursor receivedMessages = SCDBOperations.returnReceivedMessaegDataFromDB();
        if (receivedMessages.moveToFirst()){
            do {
                receivedMessagesList.append(receivedMessages.getString(0) + " : " +receivedMessages.getString(1)+ " :" +
                                " " + receivedMessages.getString(2) +
                        "\n");
            } while (receivedMessages.moveToNext());

        } else {
            receivedMessagesList.setText("Receieved messages list is empty");
            CommonUtils.printLog("Receieved messages list is empty");
        }
        Cursor subscribedTopics = SCDBOperations.returnSubscribedTopicsFromDB();
        if (subscribedTopics.moveToFirst()){
            do {
//                CommonUtils.printLog("cursor data:" + receivedMessages.getString(0) + " : " + receivedMessages.getString(1) + " : " +
//                        receivedMessages.getString(2));
                subscribedTopicsList.append(subscribedTopics.getString(0) + " : " +subscribedTopics.getString(1)+ "\n");
            } while (subscribedTopics.moveToNext());

        } else {
            subscribedTopicsList.setText("No subscribed topics");
            CommonUtils.printLog("No subscribed topics");
        }
        int[] messageCount = SCDBOperations.returnCurrentMessageCount();
        messagesSentTV.setText(messageCount[0]+"");
        messageReceivedTV.setText(messageCount[1]+"");
    }
}
