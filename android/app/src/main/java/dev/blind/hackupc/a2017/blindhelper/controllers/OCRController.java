package dev.blind.hackupc.a2017.blindhelper.controllers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class OCRController {
    private static final String TAG = OCRController.class.getSimpleName();
    private final String OCRLanguage;
    private Context context;

    public OCRController(Context context, String OCRLanguage) {
        this.context = context;
        this.OCRLanguage = OCRLanguage;
    }

    public void imageOCRRequest(String imageURL, final OCRResolvedCallback imageOCRResolvedCallback) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.ocr.space")
                .appendPath("parse")
                .appendPath("imageurl")
                .appendQueryParameter("apikey", "ab70658b5888957")
                .appendQueryParameter("language", OCRLanguage)
                .appendQueryParameter("url", imageURL);
        String url = builder.build().toString();

        Log.e(TAG, url);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String imageOCRArray = parseImageOCRJSON(response);
                        imageOCRResolvedCallback.onImageOCRResolved(imageOCRArray);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        VolleyController.getInstance(context).addToQueue(jsonObjectRequest);
    }

    private String parseImageOCRJSON(JSONObject imageOCRJSONObject) {
        try {
            String resultOCR = null;

            JSONArray parsedResultsArray = imageOCRJSONObject.getJSONArray("ParsedResults");
            for (int i = 0; i < parsedResultsArray.length(); ++i) {
                JSONObject parsedResultObject = parsedResultsArray.getJSONObject(i);
                resultOCR = parsedResultObject.getString("ParsedText");
            }

            return resultOCR;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface OCRResolvedCallback {
        void onImageOCRResolved(String OCRResponse);
    }
}
