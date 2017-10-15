package dev.blind.hackupc.a2017.blindhelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

import dev.blind.hackupc.a2017.blindhelper.adapters.PhotoQuestionAdapter;
import dev.blind.hackupc.a2017.blindhelper.controllers.BackendController;
import dev.blind.hackupc.a2017.blindhelper.controllers.GooglePlacesController;
import dev.blind.hackupc.a2017.blindhelper.controllers.LocationController;
import dev.blind.hackupc.a2017.blindhelper.controllers.VolleyController;
import dev.blind.hackupc.a2017.blindhelper.model.MyLocation;
import dev.blind.hackupc.a2017.blindhelper.model.MyPlaces;
import dev.blind.hackupc.a2017.blindhelper.model.Question;

import static java.security.AccessController.getContext;

public class PhotoAnswerActivity extends AppCompatActivity implements BackendController.ResponseServerCallback {
    private static final String TAG = PhotoAnswerActivity.class.getSimpleName();
    private List<Question> mData;
    private RecyclerView mRecyclerView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_answer);

        setUpElements();
    }

    private void setUpElements() {
        mData = new ArrayList<>();
        makeRequestQuestions();
        //mData.add(new Question("Cuando caduca", "1"));

        mRecyclerView = (RecyclerView) findViewById(R.id.photo_answer_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new PhotoQuestionAdapter(this, mData));

        //LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //mRecyclerView.setLayoutManager(horizontalLayoutManagaer);

        mContext = this;
    }

    public void makeRequestQuestions() {
        Log.e("TT", BackendController.GET_QUESTIONS_URL);
        JsonObjectRequest request = new JsonObjectRequest(BackendController.GET_QUESTIONS_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ArrayList<Question> questions = BackendController.getQuestionsJSON(jsonObject);

                mData = questions;
                mRecyclerView.setAdapter(new PhotoQuestionAdapter(mContext, mData));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyController.getInstance(getApplicationContext()).onConnectionFailed(volleyError.toString());
            }
        });
        VolleyController.getInstance(this).addToQueue(request);
    }

    public void addAnswer(String username, String id, String text) {
        BackendController.addAnswer(username, id, text, this);
    }

    @Override
    public void onResponseServer(String petition, String message) {

    }
}