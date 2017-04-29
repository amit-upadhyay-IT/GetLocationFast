package com.amitupadhyay.getlocationfast;

import android.location.Location;
import android.os.Bundle;

import com.yayandroid.locationmanager.configuration.LocationConfiguration;

public class MainActivity extends LocationBaseActivity implements SamplePresenter.SampleView {

    @Override
    public LocationConfiguration getLocationConfiguration() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void setText(String text) {

    }

    @Override
    public void updateProgress(String text) {

    }

    @Override
    public void dismissProgress() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onLocationFailed(int type) {

    }
}
