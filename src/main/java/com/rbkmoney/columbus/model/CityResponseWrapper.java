package com.rbkmoney.columbus.model;

import com.maxmind.geoip2.model.CityResponse;

public class CityResponseWrapper {
    private CityResponse response;
    private String jsonResponse;

    public CityResponseWrapper(CityResponse response, String jsonResponse) {
        this.response = response;
        this.jsonResponse = jsonResponse;
    }

    public CityResponse getResponse() {
        return response;
    }

    public String getJsonResponse() {
        return jsonResponse;
    }
}
