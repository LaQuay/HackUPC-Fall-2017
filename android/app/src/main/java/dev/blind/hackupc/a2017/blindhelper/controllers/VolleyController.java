package dev.blind.hackupc.a2017.blindhelper.controllers;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class VolleyController {
    private static VolleyController instance;
    private RequestQueue fRequestQueue;
    private VolleyCore volley;

    private VolleyController(Context context) {
        volley = new VolleyCore(context.getApplicationContext());
        fRequestQueue = volley.getRequestQueue();
    }

    public static VolleyController getInstance(Context context) {
        if (instance == null) {
            createInstance(context);
        }
        return instance;
    }

    private synchronized static void createInstance(Context context) {
        if (instance == null) {
            instance = new VolleyController(context);
        }
    }

    public void addToQueue(Request request) {
        if (request != null) {
            request.setTag(this);
            if (fRequestQueue == null)
                fRequestQueue = volley.getRequestQueue();
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            fRequestQueue.add(request);
        }
    }

    public void onConnectionFailed(String error) {
        //Toast.makeText(ctx, error, Toast.LENGTH_SHORT).show();
    }

    public void removeAllDataInQueue() {
        if (fRequestQueue != null) {
            fRequestQueue.cancelAll(this);
        }
    }

    private class VolleyCore {
        private RequestQueue mRequestQueue;

        private VolleyCore(Context context) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        RequestQueue getRequestQueue() {
            return mRequestQueue;
        }
    }
}
