package dev.blind.hackupc.a2017.blindhelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;

import dev.blind.hackupc.a2017.blindhelper.components.SpeechRadioButton;
import dev.blind.hackupc.a2017.blindhelper.controllers.GooglePlacesController;
import dev.blind.hackupc.a2017.blindhelper.controllers.LocationController;
import dev.blind.hackupc.a2017.blindhelper.controllers.VolleyController;
import dev.blind.hackupc.a2017.blindhelper.model.MyLocation;
import dev.blind.hackupc.a2017.blindhelper.model.MyPlaces;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class AroundMeActivity extends AppCompatActivity implements LocationController.OnNewLocationCallback, OnMapReadyCallback {
    private static final String TAG = AroundMeActivity.class.getSimpleName();
    private static final float DEFAULT_CAMERA_ZOOM = 16f;
    private SpeechRadioButton restaurantsCheckBox;
    private SpeechRadioButton monumentCheckBox;
    private SpeechRadioButton pharmacyCheckBox;
    private SpeechRadioButton marketCheckBox;
    private SpeechRadioButton banksCheckBox;
    private SpeechRadioButton postOfficeCheckBox;

    private MyLocation myLocation;
    private GoogleMap mMap;
    private MapView mapView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around_me);

        setUpElements();
        setUpListeners();

        restaurantsCheckBox.setChecked(true);

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
        restaurantsCheckBox = findViewById(R.id.checkbox_restaurants);
        monumentCheckBox = findViewById(R.id.checkbox_monuments);
        pharmacyCheckBox = findViewById(R.id.checkbox_pharmacy);
        marketCheckBox = findViewById(R.id.checkbox_market);
        banksCheckBox = findViewById(R.id.checkbox_banks);
        postOfficeCheckBox = findViewById(R.id.checkbox_post_office);

        mapView = findViewById(R.id.around_me_map);
    }

    private void setUpListeners() {
        restaurantsCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCallWithFilters();
            }
        });

        monumentCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCallWithFilters();
            }
        });

        pharmacyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCallWithFilters();
            }
        });

        marketCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCallWithFilters();
            }
        });

        banksCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCallWithFilters();
            }
        });

        postOfficeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCallWithFilters();
            }
        });
    }

    public void makeRequestGooglePlaces(String url) {
        Log.e(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                removeMarkersInMap();

                ArrayList<MyPlaces> myPlaces = GooglePlacesController.getPlacesFromGoogleJSON(jsonObject);
                if (myPlaces != null && !myPlaces.isEmpty()) {
                    drawMarkersInMap(myPlaces);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyController.getInstance(getApplicationContext()).onConnectionFailed(volleyError.toString());
            }
        });
        VolleyController.getInstance(this).addToQueue(request);
    }

    private void removeMarkersInMap() {
        mMap.clear();
    }

    private void drawMarkersInMap(ArrayList<MyPlaces> myPlaces) {
        for (int i = 0; i < myPlaces.size(); ++i) {
            MyPlaces currentPlace = myPlaces.get(i);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(currentPlace.getLat(), currentPlace.getLng()))
                    .title(currentPlace.getName())
                    .icon(BitmapDescriptorFactory.fromResource(GooglePlacesController.getBestIntDrawableForType(currentPlace.getTypes()))));
            marker.setTag(i);
        }
    }

    private void createCallWithFilters() {
        String url = GooglePlacesController.createUrlWithFilters(myLocation.getLat(), myLocation.getLng(),
                restaurantsCheckBox.isChecked(), monumentCheckBox.isChecked(), pharmacyCheckBox.isChecked(),
                marketCheckBox.isChecked(), banksCheckBox.isChecked(), postOfficeCheckBox.isChecked());

        makeRequestGooglePlaces(url);
    }

    @Override
    public void onNewLocation(Location location) {
        Log.e(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
        LocationController.getInstance(this).stopLocation();

        myLocation = new MyLocation();
        myLocation.setLat(location.getLatitude());
        myLocation.setLng(location.getLongitude());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        final Runnable r = new Runnable() {
            public void run() {
                if (myLocation != null) {
                    LatLng latLng = new LatLng(myLocation.getLat(), myLocation.getLng());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_CAMERA_ZOOM));

                    createCallWithFilters();
                } else {
                    handler.postDelayed(this, 250);
                }
            }
        };
        handler.post(r);
    }
}
