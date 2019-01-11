package com.rbkmoney.columbus.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.geo_ip.geo_ipConstants;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Matchers.any;

public class GeoIpServiceHandlerTest {

    private static final String IP = "192.168.100.1";
    private static final String RU = "RU";
    GeoIpServiceHandler geoIpServiceHandler;

    @Mock
    GeoService service;
    @Mock
    Country country;

    @Before
    public void init() throws IOException, GeoIp2Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(country.getIsoCode()).thenReturn(RU);
        CityResponse result = new CityResponse(new City(), new Continent(), country, new Location(), new MaxMind(),
                new Postal(), country, new RepresentedCountry(), null, null);
        Mockito.when(service.getLocationByIp(any())).thenReturn(result);
        geoIpServiceHandler = new GeoIpServiceHandler(service);
    }

    @Test
    public void getLocationIsoCode() throws TException {
        String locationIsoCode = geoIpServiceHandler.getLocationIsoCode(IP);
        Assert.assertEquals(RU, locationIsoCode);
    }

    @Test
    public void getLocationIsoCodeUnknown() throws TException, IOException, GeoIp2Exception {
        Mockito.when(service.getLocationByIp(any())).thenReturn(null);
        String locationIsoCode = geoIpServiceHandler.getLocationIsoCode(IP);
        Assert.assertEquals(geo_ipConstants.UNKNOWN, locationIsoCode);
    }

    @Test(expected = InvalidRequest.class)
    public void getLocationIsoCodeInvalidRequestTest() throws TException {
        geoIpServiceHandler.getLocationIsoCode("23e23e23e2");
    }

    @Test(expected = TException.class)
    public void getLocationIsoCodeTExceptionTest() throws TException, IOException, GeoIp2Exception {
        Mockito.when(service.getLocationByIp(any())).thenThrow(new RuntimeException());
        geoIpServiceHandler.getLocationIsoCode("23e23e23e2");
    }
}