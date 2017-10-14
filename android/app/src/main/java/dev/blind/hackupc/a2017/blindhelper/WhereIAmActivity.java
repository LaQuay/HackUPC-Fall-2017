package dev.blind.hackupc.a2017.blindhelper;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import dev.blind.hackupc.a2017.blindhelper.controllers.GeocodingController;
import dev.blind.hackupc.a2017.blindhelper.controllers.LocationController;
import dev.blind.hackupc.a2017.blindhelper.controllers.VolleyController;
import dev.blind.hackupc.a2017.blindhelper.model.MyLocation;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class WhereIAmActivity extends AppCompatActivity implements LocationController.OnNewLocationCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView whereIAmLatLngTextView;
    private TextView whereIAmAddressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_i_am);

        setUpElements();

        LocationController.getInstance(this).startLocation(this);
    }

    private void setUpElements() {
        whereIAmLatLngTextView = (TextView) findViewById(R.id.where_i_am_lat_lng_text);
        whereIAmAddressTextView = (TextView) findViewById(R.id.where_i_am_address_text);
    }

    public void makeRequestGoogle(String url) {
        Log.e(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                MyLocation currentMyLocation = GeocodingController.getLocationFromGoogleJSON(jsonObject);
                Log.e(TAG, currentMyLocation.getAddress());

                whereIAmAddressTextView.setText(currentMyLocation.getAddress());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyController.getInstance(getApplicationContext()).onConnectionFailed(volleyError.toString());
            }
        });
        VolleyController.getInstance(this).addToQueue(request);
    }

    @Override
    public void onNewLocation(Location location) {
        Log.e(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
        LocationController.getInstance(this).stopLocation();

        whereIAmLatLngTextView.setText(location.getLatitude() + ", " + location.getLongitude());

        makeRequestGoogle(
                GeocodingController.getGoogleApiByLatLng(location.getLatitude(), location.getLongitude()));
    }
}
