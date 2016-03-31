package com.example.shashankshekhar.servicedemo.Interfaces;

/**
 * Created by shashankshekhar on 30/03/16.
 */
public interface ServiceCallback {
    public void messageReceivedFromService(int number);
    public void serviceConnected ();
    public void serviceDisconnected ();
}
