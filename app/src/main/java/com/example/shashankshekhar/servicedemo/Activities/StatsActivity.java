package com.example.shashankshekhar.servicedemo.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.shashankshekhar.servicedemo.FileHandler.ConnOptsJsonHandler;
import com.example.shashankshekhar.servicedemo.R;

import org.w3c.dom.Text;

public class StatsActivity extends AppCompatActivity {
    TextView jsonPath;
    TextView subscribedTopics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        jsonPath = (TextView)findViewById(R.id.jsonPath);
        jsonPath.setText(ConnOptsJsonHandler.jsonPath());
        subscribedTopics  = (TextView)findViewById(R.id.subscribedTopicsList);
    }
}
