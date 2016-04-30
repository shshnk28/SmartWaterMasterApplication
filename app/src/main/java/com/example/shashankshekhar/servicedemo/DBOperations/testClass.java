package com.example.shashankshekhar.servicedemo.DBOperations;

/**
 * Created by shashankshekhar on 29/04/16.
 */
public class testClass {
    private static testClass ourInstance = new testClass();

    public static testClass getInstance() {
        return ourInstance;
    }

    private testClass() {
    }
}
