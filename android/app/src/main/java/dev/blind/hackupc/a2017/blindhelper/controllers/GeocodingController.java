package dev.blind.hackupc.a2017.blindhelper.controllers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import dev.blind.hackupc.a2017.blindhelper.model.MyLocation;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class GeocodingController {
    private static final String TAG = GeocodingController.class.getSimpleName();
    private static final String mapsKey = "AIzaSyCScWRCp0MaVnBvfz9MMMWz30nh0FmSRLw";

    public static String getGoogleApiByLatLng(Double lat, Double lng) {
        return "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true&key=" + mapsKey;
    }

    public static String getGoogleApiByAddress(String address) {
        if (address != null && !address.isEmpty()) {
            try {
                address = URLEncoder.encode(address, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return "https://maps.googleapis.com/maps/api/geocode/json?address="
                + address
                + "&region=es&components=country:ES|administrative_area_level_1:CT"
                + "&key=" + mapsKey;
    }

    public static MyLocation getLocationFromGoogleJSON(JSONObject json) {
        try {
            if (json.getString("status").equals("OK")) {
                JSONArray results = json.getJSONArray("results");
                for (int i = 0; i < results.length(); ++i) {
                    MyLocation novaUbicacio = new MyLocation();
                    JSONObject location = results.getJSONObject(i);

                    String strPostalCode = null;
                    JSONArray addressComponents = location.getJSONArray("address_components");
                    for (int j = 0; j < addressComponents.length(); ++j) {
                        JSONObject component = addressComponents.getJSONObject(j);
                        if (component.getJSONArray("types").getString(0).equals("postal_code")) {
                            strPostalCode = component.getString("long_name");
                        }
                    }

                    String formattedAddress = location.getString("formatted_address");

                    boolean isAValidType = false;
                    JSONArray typeOfPlace = location.getJSONArray("types");
                    for (int j = 0; j < typeOfPlace.length(); ++j) {
                        String type = typeOfPlace.get(j).toString();
                        isAValidType = !type.equals("establishment");
                    }

                    //Only show results that match with the previous type of points.
                    if (!isAValidType) {
                        novaUbicacio.setErrorCode(MyLocation.ERROR_NOT_VALID_TYPE);
                        continue;
                    }

                    JSONObject locationGeometry = location.getJSONObject("geometry").getJSONObject("location");
                    novaUbicacio.setLat(Double.parseDouble(locationGeometry.getString("lat")));
                    novaUbicacio.setLng(Double.parseDouble(locationGeometry.getString("lng")));
                    novaUbicacio.setPostalCode(strPostalCode);
                    novaUbicacio.setAddress(formattedAddress);

                    return novaUbicacio;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface GoogleGeocodingAPICallback {
        void onLocationGeocoded(String location);
    }
}