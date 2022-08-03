package com.example.maskrcnn;

import android.app.Application;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class MyModel extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initPython();
    }
    private void initPython() {
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }
}
