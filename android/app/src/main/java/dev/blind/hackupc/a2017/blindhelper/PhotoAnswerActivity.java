package dev.blind.hackupc.a2017.blindhelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;

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

import dev.blind.hackupc.a2017.blindhelper.controllers.GooglePlacesController;
import dev.blind.hackupc.a2017.blindhelper.controllers.LocationController;
import dev.blind.hackupc.a2017.blindhelper.controllers.VolleyController;
import dev.blind.hackupc.a2017.blindhelper.model.MyLocation;
import dev.blind.hackupc.a2017.blindhelper.model.MyPlaces;
public class PhotoAnswerActivity extends AppCompatActivity {
    private static final String TAG = PhotoAnswerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_answer);

        setUpElements();
    }

    private void setUpElements() {
    }
}
