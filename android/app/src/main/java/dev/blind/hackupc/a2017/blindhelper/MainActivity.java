package dev.blind.hackupc.a2017.blindhelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dev.blind.hackupc.a2017.blindhelper.controllers.GeocodingController;
import dev.blind.hackupc.a2017.blindhelper.controllers.LocationController;
import dev.blind.hackupc.a2017.blindhelper.controllers.VolleyController;
import dev.blind.hackupc.a2017.blindhelper.model.MyLocation;

public class MainActivity extends AppCompatActivity implements LocationController.OnNewLocationCallback {
    public static final int MULTIPLE_PERMISSIONS_CODE = 10;
    public static final int CAMERA_PERMISSION_CODE = 200;
    public static final int WRITE_SD_PERMISSION_CODE = 201;
    public static final int LOCATION_PERMISSION_CODE = 202;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!requestPermissions()) {
                LocationController.getInstance(this).startLocation(this);
            }
        }

        Fragment newFragment = new MainActivityFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(android.R.id.content, newFragment).commit();
    }

    public void makeRequestGoogle(String url) {
        Log.e(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                MyLocation currentMyLocation = GeocodingController.getLocationFromGoogleJSON(jsonObject);
                Log.e(TAG, currentMyLocation.getAddress());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyController.getInstance(getApplicationContext()).onConnectionFailed(volleyError.toString());
            }
        });
        VolleyController.getInstance(this).addToQueue(request);
    }

    public boolean requestPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS_CODE);
            }
        }
        return !listPermissionsNeeded.isEmpty();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MULTIPLE_PERMISSIONS_CODE: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // permissions list of don't granted permission
                    for (String permission : permissions) {
                        if (permission.contains("CAMERA")) {
                            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "Permisos no concedidos para CAMARA", Toast.LENGTH_SHORT).show();
                            }
                        } else if (permission.contains("WRITE")) {
                            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "Permisos no concedidos para ESCRITURA", Toast.LENGTH_SHORT).show();
                            }
                        } else if (permission.contains("LOCATION")) {
                            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "Permisos no concedidos para LOCATION", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (String permission : permissions) {
                        if (permission.contains("LOCATION")) {
                            LocationController.getInstance(this).startLocation(this);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onNewLocation(Location location) {
        Log.e(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
        LocationController.getInstance(this).stopLocation();

        makeRequestGoogle(
                GeocodingController.getGoogleApiByLatLng(location.getLatitude(), location.getLongitude()));
    }
}
