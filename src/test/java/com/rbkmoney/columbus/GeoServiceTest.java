package com.rbkmoney.columbus;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.rbkmoney.columbus.dao.CityLocationsDao;
import com.rbkmoney.columbus.dao.GeoIpDao;
import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.Lang;
import com.rbkmoney.columbus.service.GeoIpServiceHandler;
import com.rbkmoney.columbus.service.GeoService;
import com.rbkmoney.columbus.util.IpAddresUtils;
import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.geo_ip.GeoIDInfo;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.geo_ip.geo_ipConstants;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

import static com.rbkmoney.columbus.service.GeoIpServiceHandler.buildUnknownGeoIdInfo;
import static com.rbkmoney.damsel.geo_ip.geo_ipConstants.GEO_ID_UNKNOWN;
import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@Import(IntegrationBaseRule.class)
//@Ignore
public class GeoServiceTest {

    @ClassRule
    public static IntegrationBaseRule rule = new IntegrationBaseRule();

    public static final Map<String, String> IP_TO_CITY = new HashMap<>();
    public static final String IP_MOSCOW = "94.159.54.234";
    public static final String IP_LONDON = "212.71.235.130";
    public static final int GEOID_KAMIZIAK = 553248;
    public static final int GEOID_MOSCOW = 524901;
    public static final int GEOID_LONDON = 2643743;

    static {
        IP_TO_CITY.put(IP_MOSCOW, "Moscow");
        IP_TO_CITY.put(IP_LONDON, "London");
    }

    @Autowired
    CityLocationsDao cityLocationsDao;

    @Autowired
    GeoIpDao geoIpDao;

    @Autowired
    GeoService service;

    GeoIpServiceHandler handler;

    @Before
    public void before() {
        handler = new GeoIpServiceHandler(service);
    }

    @Test
    public void testGetLocationException() throws TException {
        try {
            handler.getLocation("null");
            fail("InvalidRequest expected.");
        } catch (InvalidRequest ir) {
            assertEquals("null", ir.getErrors().get(0));
        }
    }

    @Test
    public void testGetLocationNullCity() throws TException {
        LocationInfo info = handler.getLocation("89.218.51.9");

        assertEquals(info.getCityGeoId(), GEO_ID_UNKNOWN);
        assertEquals(info.getCityGeoId(), GEO_ID_UNKNOWN);
    }

    @Test
    public void testWrongLangException() throws TException {
        final Integer[] ids = {GEOID_KAMIZIAK, GEOID_MOSCOW, 0};
        try {
            Map<Integer, GeoIDInfo> info = handler.getLocationInfo(Set(ids), "rublya");
        } catch (InvalidRequest e) {
            assertTrue(!e.getErrors().isEmpty());
        }
    }

    @Test
    public void testGetLocationNullValuesInMap() throws TException {
        final int unknown = 0;
        final Integer[] ids = {GEOID_KAMIZIAK, GEOID_MOSCOW, unknown};
        Map<Integer, GeoIDInfo> info = handler.getLocationInfo(Set(ids), "ru");

        assertEquals(info.size(), 3);
        assertEquals("Камызяк", info.get(GEOID_KAMIZIAK).city_name);
        assertEquals("Москва", info.get(GEOID_MOSCOW).city_name);
        assertEquals(buildUnknownGeoIdInfo(), info.get(unknown));
    }

    @Test
    public void testGetLocationNullValuesInNameMap() throws TException {
        final int unknown = 0;
        final Integer[] ids = {GEOID_KAMIZIAK, GEOID_MOSCOW, unknown};
        Map<Integer, String> info = handler.getLocationName(Set(ids), "ru");

        assertEquals(info.size(), 3);
        assertEquals("Камызяк", info.get(GEOID_KAMIZIAK));
        assertEquals("Москва", info.get(GEOID_MOSCOW));
        assertEquals("UNKNOWN", info.get(unknown));
    }

    @Test
    public void getLocationByIp() throws IOException, GeoIp2Exception {
        for (String ip : IP_TO_CITY.keySet()) {
            CityResponse cityResponse = service.getLocationByIp(IpAddresUtils.convert(ip));
            assertEquals(cityResponse.getCity().getNames().get(Lang.ENG.getValue()), IP_TO_CITY.get(ip));
        }
    }

    @Test
    public void getLocationsByIps() throws TException {
        Map<String, LocationInfo> map = handler.getLocations(IP_TO_CITY.keySet());

        assertEquals(map.size(), 2);
        assertEquals(map.get(IP_LONDON).getCityGeoId(), GEOID_LONDON);
        assertEquals(map.get(IP_MOSCOW).getCityGeoId(), GEOID_MOSCOW);
    }

    @Test
    public void getLocationByGeoIds() {
        final Integer[] ids = {GEOID_KAMIZIAK, GEOID_MOSCOW};
        List<CityLocation> list = cityLocationsDao.getByGeoIds(Set(ids), Lang.RU);

        assertEquals(2, list.size());
        assertTrue(list.get(0).getGeonameId() == ids[0] || list.get(0).getGeonameId() == ids[1]);
        assertTrue(list.get(1).getGeonameId() == ids[0] || list.get(1).getGeonameId() == ids[1]);

        assertEquals("Камызяк", getById(list, GEOID_KAMIZIAK).getCityName());
        assertEquals("Москва", getById(list, GEOID_MOSCOW).getCityName());
    }

    private static CityLocation getById(List<CityLocation> list, int id) {
        for (CityLocation cl : list) {
            if (cl.getGeonameId() == id) {
                return cl;
            }
        }

        return null;
    }

    private static Set<Integer> Set(Integer[] ids) {
        return new HashSet(Arrays.asList(ids));
    }
}
