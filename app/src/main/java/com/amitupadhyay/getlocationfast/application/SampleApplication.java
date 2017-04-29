package com.amitupadhyay.getlocationfast.application;

import android.app.Application;

import com.yayandroid.locationmanager.LocationManager;

/**
 * Created by aupadhyay on 4/29/17.
 */

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager.enableLog(true);
    }
}
