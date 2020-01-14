package com.geotracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TrackingService extends Service {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private OkHttpClient mClient;

    @Override
    public void onCreate() {
        try {
            mClient = new OkHttpClient();

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            Notification noti = getNotification();
            startForeground(12355, noti);
            createLocationRequest();
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if(locationResult == null) {
                        return;
                    }

                    try {
                        onNewLocation(locationResult.getLastLocation());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            };
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());

        } catch (Exception e) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            return Service.START_NOT_STICKY;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void onDestroy() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
    }

    private Notification getNotification() {
        try {
            Notification noti = new NotificationCompat.Builder(this, "Tracking")
                    .setContentTitle("Title")
                    .setContentText("Text")
                    .setSmallIcon(android.R.drawable.ic_menu_compass)
                    .build();


            return noti;
        } catch (Exception e) {
            throw e;
        }
    }

    private void onNewLocation(Location location) throws IOException, JSONException {
        JSONObject json = new JSONObject();
        json.put("id", 0).put("latitude", location.getLatitude()).put("longitude", location.getLongitude());
        post("http://192.168.1.12:8080", json.toString());
    }

    private void post(String url, String json){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                try (Response response = mClient.newCall(request).execute()) {
                    //return response.body().string();
                    return;
                } catch (Exception e) {
                    return;
                }
            }
        });
        thread.start();
    }
}