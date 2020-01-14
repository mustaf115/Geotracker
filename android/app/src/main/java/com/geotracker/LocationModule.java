package com.geotracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class LocationModule extends ReactContextBaseJavaModule {
    public static ReactApplicationContext mReactContext;
    private Intent mService;

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
}