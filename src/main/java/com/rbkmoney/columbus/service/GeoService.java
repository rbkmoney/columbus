package com.rbkmoney.columbus.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.rbkmoney.columbus.dao.CityLocationsDao;
import com.rbkmoney.columbus.dao.GeoIpDao;
import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.Lang;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Set;

@Service
public class GeoService {

    @Autowired
    CityLocationsDao cityLocationsDao;

    @Autowired
    GeoIpDao geoIpDao;

    public CityResponse getLocationByIp(InetAddress ipAddress) throws IOException, GeoIp2Exception {
        return geoIpDao.getLocationInfoByIp(ipAddress);
    }

    public List<CityLocation> getLocationName(Set<Integer> geoIdset, String lang) throws TException {
        return cityLocationsDao.getByGeoIds(geoIdset, Lang.getByAbbreviation(lang));
    }
}
