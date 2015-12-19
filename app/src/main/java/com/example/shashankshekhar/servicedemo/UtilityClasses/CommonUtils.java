package com.example.shashankshekhar.servicedemo.UtilityClasses;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

/**
 * Created by shashankshekhar on 09/11/15.
 * put any common utilities here
 */
public class CommonUtils {
    public static void printLog (String string) {
        Log.i("S-WATER",string);
    }
    public static String randomString () {
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        return uuid;
    }
    public static void showToast (Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();

    }
    public static  boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static boolean checkMainThread () {
        if(Looper.myLooper() == Looper.getMainLooper()) {
            // Current Thread is Main Thread.
            return true;
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context appContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    // edit the permission array to check for it.
    private boolean checkPermission (Context context) {
        String permission[] = {"android.permission.INTERNET",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
//                "android.permission.READ_INTERNAL_STORAGE",
//                "android.permission.WRITE_INTERNAL_STORAGE",
                "android.permission.ACCESS_NETWORK_STATE",
                "android.permission.ACCESS_WIFI_STATE",
                "com.google.android.providers.gsf.permission.READ_GSERVICES" };
        for (String str:permission) {
            int res = context.checkCallingOrSelfPermission(str);
            if (res == PackageManager.PERMISSION_GRANTED) {
                CommonUtils.printLog("permission granted for" + str);
            } else {
                CommonUtils.printLog("permission NOT granted for" + str);
                return false;
            }

        }
        return true;
    }
}
