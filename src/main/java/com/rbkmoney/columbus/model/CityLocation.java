package com.rbkmoney.columbus.model;

public class CityLocation {
    private int geonameId;
    private String localeCode;
    private String continentCode;
    private String continentName;
    private String countryIsoCode;
    private String countryName;
    private String subdivision_1IsoCode;
    private String subdivision_1Name;
    private String subdivision_2IsoCode;
    private String subdivision_2Name;
    private String cityName;
    private String metroCode;
    private String timeZone;

    public int getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(int geonameId) {
        this.geonameId = geonameId;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getContinentCode() {
        return continentCode;
    }

    public void setContinentCode(String continentCode) {
        this.continentCode = continentCode;
    }

    public String getContinentName() {
        return continentName;
    }

    public void setContinentName(String continentName) {
        this.continentName = continentName;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public void setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getSubdivision_1IsoCode() {
        return subdivision_1IsoCode;
    }

    public void setSubdivision_1IsoCode(String subdivision_1IsoCode) {
        this.subdivision_1IsoCode = subdivision_1IsoCode;
    }

    public String getSubdivision_1Name() {
        return subdivision_1Name;
    }

    public void setSubdivision_1Name(String subdivision_1Name) {
        this.subdivision_1Name = subdivision_1Name;
    }

    public String getSubdivision_2IsoCode() {
        return subdivision_2IsoCode;
    }

    public void setSubdivision_2IsoCode(String subdivision_2IsoCode) {
        this.subdivision_2IsoCode = subdivision_2IsoCode;
    }

    public String getSubdivision_2Name() {
        return subdivision_2Name;
    }

    public void setSubdivision_2Name(String subdivision_2Name) {
        this.subdivision_2Name = subdivision_2Name;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getMetroCode() {
        return metroCode;
    }

    public void setMetroCode(String metroCode) {
        this.metroCode = metroCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getName(){
        String[] names = {cityName, subdivision_2Name, subdivision_1Name, countryName, continentName};
        for(String name: names){
            if(name != null && !"".equals(name)){
                return name;
            }
        }
        return null;
    }
}
