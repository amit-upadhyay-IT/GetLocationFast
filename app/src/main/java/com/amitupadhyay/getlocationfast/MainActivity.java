package com.amitupadhyay.getlocationfast;

import android.app.ProgressDialog;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yayandroid.locationmanager.configuration.Configurations;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.constants.ProcessType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends LocationBaseActivity implements SamplePresenter.SampleView {

    @BindView(R.id.getLocationBtn)
    Button getLocationBtn;

    @BindView(R.id.showLocationTV)
    TextView showLocationTV;

    @OnClick(R.id.getLocationBtn)
    void onClickGetLocationBtn()
    {
        if (isNetworkAvailable())
            getLocation();
        else
            Toast.makeText(this, "Would you mind to turn on your Network?", Toast.LENGTH_SHORT).show();
    }

    private ProgressDialog progressDialog;
    private SamplePresenter samplePresenter;

    private String completeJSONresponse = "";

    @Override
    public LocationConfiguration getLocationConfiguration() {
        return Configurations.defaultConfiguration("Gimme the permission!", "Would you mind to turn GPS on?");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        samplePresenter = new SamplePresenter(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getLocationManager().isWaitingForLocation()
                && !getLocationManager().isAnyDialogShowing()) {
            displayProgress();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        dismissProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        samplePresenter.destroy();
    }

    private void displayProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            progressDialog.setMessage("Getting location...");
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }


    @Override
    public String getText() {
        return "";
    }

    @Override
    public void setText(String text) {
        // The text here has two double values separated by ",". So you can split the string
        String locations[] = text.split(",");
        Double latitude = Double.parseDouble(locations[0]);
        Double longitude = Double.parseDouble(locations[1]);
        showLocationTV.setText(text);

        new FetchLocationThread(latitude, longitude).start();
    }

    @Override
    public void updateProgress(String text) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(text);
        }
    }

    @Override
    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onProcessTypeChanged(@ProcessType int processType) {
        samplePresenter.onProcessTypeChanged(processType);
    }

    @Override
    public void onLocationChanged(Location location) {
        samplePresenter.onLocationChanged(location);
    }

    @Override
    public void onLocationFailed(int type) {
        samplePresenter.onLocationFailed(type);
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }

    class FetchLocationThread extends Thread
    {
        double latitude, longitude;
        public FetchLocationThread(double lat, double lon)
        {
            this.latitude = lat;
            this.longitude = lon;
        }

        @Override
        public void run() {

            try
            {
                URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng="+String.valueOf(latitude)+","+String.valueOf(longitude)+"&sensor=true");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); // sending req. to server

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader buffer = new BufferedReader(reader); // now we can read this response line by line.

                StringBuilder builder = new StringBuilder();
                String line = buffer.readLine();

                while (line != null)
                {
                    builder.append(line);
                    line = buffer.readLine();
                }
                // so finally the builder will contain the complete JSON data.

                completeJSONresponse = builder.toString(); // now response contains JSON documents as a String.

                handler.sendEmptyMessage(101);



            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 101)
            {
                parseJSONResponse();
            }

        }
    };

    private void parseJSONResponse()
    {
        try {

            JSONObject jsonObject = new JSONObject(completeJSONresponse);

            JSONArray jsonArray = jsonObject.getJSONArray("results");

            JSONObject firstObj = jsonArray.getJSONObject(0);

            String formatedAddress = firstObj.getString("formatted_address");

            showLocationTV.setText(formatedAddress);
            this.dismissProgress();


        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Some Parsing issue "+completeJSONresponse, Toast.LENGTH_LONG).show();
        }
    }
}
