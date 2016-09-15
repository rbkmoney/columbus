package com.rbkmoney.columbus.model;

public class LocationInfo {
    private GeoNameIdInfo city;
    private GeoNameIdInfo subdivision;
    private GeoNameIdInfo country;
    private Double longitude;
    private Double latitude;
    private String timeZone;
    private Integer confidence;
    private String rawResponse;

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponce) {
        this.rawResponse = rawResponce;
    }



    public GeoNameIdInfo getCity() {
        return city;
    }

    public void setCity(GeoNameIdInfo city) {
        this.city = city;
    }

    public GeoNameIdInfo getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(GeoNameIdInfo subdivision) {
        this.subdivision = subdivision;
    }

    public GeoNameIdInfo getCountry() {
        return country;
    }

    public void setCountry(GeoNameIdInfo country) {
        this.country = country;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }
}
