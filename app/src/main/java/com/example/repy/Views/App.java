package com.example.repy.Views;

import android.app.Application;

import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.StorageManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        StorageManager.getInstance(this);
        DataManager.getInstance();
    }
}
