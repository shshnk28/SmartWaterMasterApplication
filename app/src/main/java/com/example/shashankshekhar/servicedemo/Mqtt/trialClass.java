package com.example.shashankshekhar.servicedemo.Mqtt;

/**
 * Created by shashankshekhar on 10/11/15.
 */
public class trialClass {
    private static trialClass ourInstance = new trialClass();

    public static trialClass getInstance() {
        return ourInstance;
    }

    private trialClass() {
    }
}
