package com.linksrussia.tsup.workerapp;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.linksrussia.tsup.workerapp.db.AppDatabase;
import com.linksrussia.tsup.workerapp.dto.ApplicationScope;

import lombok.Getter;

public class App extends Application {
    public static final String DB_NAME = "measurement-series-db-v2";

    private static Context context;
    private static AppDatabase db;
    private static final ApplicationScope applicationScope = new ApplicationScope();

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        App.db = Room.databaseBuilder(App.context, AppDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static AppDatabase getAppDatabase() {
        return App.db;
    }

    public static ApplicationScope getApplicationScope() {
        return applicationScope;
    }
}
