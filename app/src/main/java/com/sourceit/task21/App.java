package com.sourceit.task21;

import android.app.Application;

/**
 * Created by User on 04.03.2016.
 */
public class App extends Application {
    private static App instance;

    public static App getApp() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
