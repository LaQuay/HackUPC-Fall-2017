package dev.blind.hackupc.a2017.blindhelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import dev.blind.hackupc.a2017.blindhelper.components.SpeechTextView;
import dev.blind.hackupc.a2017.blindhelper.controllers.GeocodingController;
import dev.blind.hackupc.a2017.blindhelper.controllers.LocationController;
import dev.blind.hackupc.a2017.blindhelper.controllers.VolleyController;
import dev.blind.hackupc.a2017.blindhelper.model.MyLocation;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class WhereIAmActivity extends AppCompatActivity implements LocationController.OnNewLocationCallback, OnMapReadyCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final float DEFAULT_CAMERA_ZOOM = 18f;
    private SpeechTextView whereIAmAddressTextView;

    private MyLocation myLocation;

    private GoogleMap mMap;
    private MapView mapView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_i_am);

        setUpElements();

        LocationController.getInstance(this).startLocation(this);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);
    }

    private void setUpElements() {
        whereIAmAddressTextView = findViewById(R.id.where_i_am_address_text);

        mapView = findViewById(R.id.where_i_am_map);
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

        myLocation = new MyLocation();
        myLocation.setLat(location.getLatitude());
        myLocation.setLng(location.getLongitude());

        makeRequestGoogle(
                GeocodingController.getGoogleApiByLatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        final Runnable r = new Runnable() {
            public void run() {
                if (myLocation != null) {
                    LatLng latLng = new LatLng(myLocation.getLat(), myLocation.getLng());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_CAMERA_ZOOM));
                } else {
                    handler.postDelayed(this, 250);
                }
            }
        };
        handler.post(r);
    }
}
