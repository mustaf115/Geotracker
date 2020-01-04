package com.geotracker;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationModule extends ReactContextBaseJavaModule {
    public static ReactApplicationContext reactContext;
    public static FusedLocationProviderClient fusedLocationClient;
    public static PendingIntent service;
    private LocationRequest locationRequest;

    public LocationModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);

        this.reactContext = reactContext;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(reactContext);
        this.locationRequest = createLocationRequest();

        createNotificationChannel();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @NonNull
    @Override
    public String getName() {
        return "Location";
    }

    private void sendEvent(
                            ReactContext reactContext,
                            String eventName,
                            @Nullable WritableMap params
                            ) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = reactContext.getString(R.string.channel_name);
            String description = reactContext.getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel("Geotracker", name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            NotificationManager notificationManager = reactContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private NotificationCompat.Builder createNotificationBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(LocationModule.reactContext, "Geotracker")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentTitle("Tracking")
                .setContentText("Geotracker is sharing your location")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Geotracker is sharing your location"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder;
    }

    @ReactMethod
    public void watchLocation() {
        try {
            Intent intent = new Intent(reactContext, TrackingService.class);
            PendingIntent service = PendingIntent.getService(reactContext, 0, intent, 0);
            this.service = service;
            fusedLocationClient.requestLocationUpdates(locationRequest, service);
        } catch (Exception e) {
            throw e;
        }
    }

    @ReactMethod
    public void stopLocation() {
        fusedLocationClient.removeLocationUpdates(service);
    }

    @ReactMethod
    public void getLocation() {
        try {
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null) {
                            WritableMap params = Arguments.createMap();
                            params.putDouble("latitude", location.getLatitude());
                            params.putDouble("longitude", location.getLongitude());
                            sendEvent(reactContext, "EventLocation", params);

                        }
                    }
                });
        } catch(Exception e) {
            throw e;
        }
    }
}
