package dev.blind.hackupc.a2017.blindhelper.controllers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dev.blind.hackupc.a2017.blindhelper.model.MyPlaces;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class GooglePlacesController {
    private static final String TAG = GooglePlacesController.class.getSimpleName();
    private static final String mapsKey = "AIzaSyCScWRCp0MaVnBvfz9MMMWz30nh0FmSRLw";

    public static String createUrlWithFilters(Double lat, Double lng) {
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=500" +
                "&type=restaurant&key=" + mapsKey;
    }

    public static ArrayList<MyPlaces> getPlacesFromGoogleJSON(JSONObject jsonObject) {
        try {
            if (jsonObject.getString("status").equals("OK")) {
                JSONArray results = jsonObject.getJSONArray("results");
                ArrayList<MyPlaces> myPlaces = new ArrayList<>();
                for (int i = 0; i < results.length(); ++i) {
                    MyPlaces place = new MyPlaces();
                    JSONObject placeJSONObject = results.getJSONObject(i);

                    JSONObject placeGeometryJSONObject = placeJSONObject.getJSONObject("geometry");
                    JSONObject placeLocationJSONObject = placeGeometryJSONObject.getJSONObject("location");
                    place.setLat(placeLocationJSONObject.getDouble("lat"));
                    place.setLng(placeLocationJSONObject.getDouble("lng"));

                    place.setName(placeJSONObject.getString("name"));

                    ArrayList<String> types = new ArrayList<>();
                    JSONArray typesJSONArray = placeJSONObject.getJSONArray("types");
                    for (int j = 0; j < typesJSONArray.length(); ++j) {
                        types.add(typesJSONArray.getString(j));
                    }
                    place.setType(types);

                    place.setAddress(placeJSONObject.getString("vicinity"));

                    myPlaces.add(place);
                }
                return myPlaces;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
