package com.linksrussia.tsup.workerapp;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.room.Room;

import com.linksrussia.tsup.workerapp.db.AppDatabase;

public class App extends Application {
    public static final String DB_NAME = "measurement-series-db-v2";

    public static Context context;
    public static AppDatabase db;
    public static BluetoothDevice selectedDevice;
    public static boolean deviceConnected;

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        App.db = Room.databaseBuilder(App.context, AppDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public static boolean isDeviceConnected() {
        return null != selectedDevice && deviceConnected;
    }
}
