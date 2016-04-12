package com.example.shashankshekhar.servicedemo.UtilityClasses;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by shashankshekhar on 09/11/15.
 * put any common utilities here
 */
public class CommonUtils {
    public static void printLog (String string) {
        Log.d("S-WATER",string);
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
    public static String deviceId (Context appContext) {
        if (appContext == null) {
            return null;
        } else {
            String id = Settings.Secure.getString(appContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            return id;
        }
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
    public static boolean isInternetConnectedSec(Context appContext) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
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
    public static boolean httpConnectionTest(String url) {
        try {
            URL url1 = new URL("http://" + url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setConnectTimeout(1000 * 10); // mTimeout is in seconds
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateString = dateFormat.format(calendar.getTime());
        return dateString;
    }
}
