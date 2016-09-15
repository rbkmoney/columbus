package com.rbkmoney.columbus.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GeoNameIdInfo {
    private int geoNameId;
    private Map<Lang, String> names;
    private GeoNameType geoNameType;

    public GeoNameIdInfo(int geoNameId, Map<Lang, String> names, GeoNameType geoNameType) {
        this.geoNameId = geoNameId;
        this.names = names;
        this.geoNameType = geoNameType;
    }


    public GeoNameType getGeoNameType() {
        return geoNameType;
    }

    public void setGeoNameType(GeoNameType geoNameType) {
        this.geoNameType = geoNameType;
    }

    public int getGeoNameId() {
        return geoNameId;
    }

    public void setGeoNameId(int geoNameId) {
        this.geoNameId = geoNameId;
    }

    public Map<Lang, String> getNames() {
        return names;
    }

    public void setNames(HashMap<Lang, String> names) {
        this.names = names;
    }


    public enum GeoNameType {
        CITY,
        SUBDIVISION,
        COUNTRY,
        UNDEFINED
    }

    public static GeoNameIdInfo buildUndefined() {
        HashMap<Lang, String> langMap = new HashMap<>();
        Arrays.stream(Lang.values()).forEach(l -> langMap.put(l, "UNDEFINED"));
        langMap.put(Lang.RU,"Неизвестно");
        return new GeoNameIdInfo(0, langMap, GeoNameType.UNDEFINED);
    }

}
