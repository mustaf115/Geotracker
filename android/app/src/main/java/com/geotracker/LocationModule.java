package com.geotracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class LocationModule extends ReactContextBaseJavaModule {
    public static ReactApplicationContext mReactContext;
    private Intent mService;

    private final LifecycleEventListener lifecycleEventListener = new LifecycleEventListener() {
        @Override
        public void onHostResume() {

        }

        @Override
        public void onHostPause() {

        }

        @Override
        public void onHostDestroy() {
            untrack();
        }
    };

    public LocationModule(ReactApplicationContext context) {
        super(context);
        mReactContext = context;
        mService = new Intent(mReactContext, TrackingService.class);
        createNotificationChannel();
    }

    @NonNull
    @Override
    public String getName() {
        return "Location";
    }



    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mReactContext.getString(R.string.channel_name);
            String description = mReactContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Tracking", name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);

            NotificationManager notificationManager = mReactContext.getSystemService((NotificationManager.class));
            notificationManager.createNotificationChannel(channel);
        }
    }

    @ReactMethod
    public void track() {
        mReactContext.startService(mService);
    }

    @ReactMethod
    public void untrack() {
        mReactContext.stopService(mService);
    }

    @ReactMethod
    public void saveToken(String token) {
        SharedPreferences sharedPref = mReactContext.getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.commit();
    }

    @ReactMethod
    public void removeToken() {
        SharedPreferences sharedPref = mReactContext.getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("token");
        editor.commit();
    }

    @ReactMethod
    public void isLogged(Promise promise) {
        try {
            SharedPreferences sharedPref = mReactContext.getSharedPreferences("token", Context.MODE_PRIVATE);
            Boolean isLogged = sharedPref.contains("token");
            promise.resolve(isLogged);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void isTracking(Promise promise) {
        try {
            promise.resolve(TrackingService.isTracking);
        } catch (Exception e) {
            promise.reject(e);
        }
    }
}