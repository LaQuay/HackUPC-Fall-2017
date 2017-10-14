package dev.blind.hackupc.a2017.blindhelper.model;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class MyLocation {
    public static final String ERROR_NOT_VALID_TYPE = "ERROR_NOT_VALID_TYPE";
    public static final String ERROR_PARTIAL_MATCH = "ERROR_PARTIAL_MATCH";
    private Double lat;
    private Double lng;
    private String address;
    private String postalCode;
    private String errorCode;

    public MyLocation() {
    }

    public MyLocation(Double lat, Double lng, String postalCode) {
        this.lat = lat;
        this.lng = lng;
        this.postalCode = postalCode;
        this.errorCode = null;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean hasError() {
        return errorCode != null &&
                (errorCode.equals(ERROR_NOT_VALID_TYPE) || errorCode.equals(ERROR_PARTIAL_MATCH));
    }
}
