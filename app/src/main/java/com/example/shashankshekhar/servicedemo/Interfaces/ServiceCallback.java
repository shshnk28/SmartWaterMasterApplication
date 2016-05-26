package com.example.shashankshekhar.servicedemo.Interfaces;

/**
 * Created by shashankshekhar on 30/03/16.
 */
public interface ServiceCallback {
    void messageReceivedFromService(int number);
    void serviceDisconnected ();
    void serviceConnected ();
}
