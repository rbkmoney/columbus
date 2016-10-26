package com.rbkmoney.columbus;

import com.rbkmoney.columbus.dao.CityLocationsDao;
import com.rbkmoney.columbus.dao.GeoIpDao;
import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.CityResponseWrapper;
import com.rbkmoney.columbus.model.Lang;
import com.rbkmoney.columbus.service.GeoIpServiceHandler;
import com.rbkmoney.columbus.service.GeoService;
import com.rbkmoney.damsel.geo_ip.CantDetermineLocation;
import com.rbkmoney.damsel.geo_ip.GeoIDInfo;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GeoServiceTest {
    public static final Map<String, String> IP_TO_CITY = new HashMap<>();
    static {
        IP_TO_CITY.put("94.159.54.234", "Moscow");
        IP_TO_CITY.put("212.71.235.130", "London");
    }

    @Autowired
    CityLocationsDao cityLocationsDao;

    @Autowired
    GeoIpDao geoIpDao;

    @Autowired
    GeoService service;

    GeoIpServiceHandler handler;

    @Before
    public void before(){
        handler = new GeoIpServiceHandler(service);
    }

    @Test
    public void testGetLocationException(){
        try {
            handler.getLocation("null");
            fail("CantDetermineLocation should be thrown");
        }catch (CantDetermineLocation e){
            //success
        } catch (Exception e) {
            fail("CantDetermineLocation should be thrown");
        }
    }

    @Test
    public void testGetLocationNullCity() throws TException {
        LocationInfo info = handler.getLocation("89.218.51.9");

        assertEquals(info.getCityGeoId(), GeoIpServiceHandler.UNDEFINED_GEO_ID);
        assertEquals(info.getCityGeoId(), GeoIpServiceHandler.UNDEFINED_GEO_ID);
    }

    @Test
    public void testGetLocationNullValuesInMap() throws TException {
        final int kamiziak = 553248;
        final int moscow = 524901;
        final int unknown = 0;
        final Integer[] ids = {kamiziak, moscow, unknown} ;
        Map<Integer, GeoIDInfo> info = handler.getLocationInfo(Set(ids), "ru");

        assertEquals(info.size(), 3);
        assertEquals(info.get(kamiziak).city_name, "Камызяк");
        assertEquals(info.get(moscow).city_name, "Москва");
        assertEquals(info.get(unknown), null);
    }

    @Test
    public void testGetLocationNullValuesInNameMap() throws TException {
        final int kamiziak = 553248;
        final int moscow = 524901;
        final int unknown = 0;
        final Integer[] ids = {kamiziak, moscow, unknown} ;
        Map<Integer, String> info = handler.getLocationName(Set(ids), "ru");

        assertEquals(info.size(), 3);
        assertEquals(info.get(kamiziak), "Камызяк");
        assertEquals(info.get(moscow), "Москва");
        assertEquals(info.get(unknown), null);
    }

    @Test
    public void testWrongIp() {
        CityResponseWrapper undefinedLocation = geoIpDao.getLocationInfoByIp("null");
        assertEquals(null, undefinedLocation);
    }

    @Test
    public void getLocationByIp(){
        for(String ip: IP_TO_CITY.keySet()){
            CityResponseWrapper locationInfo = service.getLocationByIp(ip);
            assertEquals(locationInfo.getResponse().getCity().getNames().get(Lang.ENG.getValue()), IP_TO_CITY.get(ip));
        }
    }

    @Test
    public void getLocationByGeoIds(){
        final int kamiziak = 553248;
        final int moscow = 524901;
        final Integer[] ids = {kamiziak,moscow} ;
        List<CityLocation> list = cityLocationsDao.getByGeoIds(Set(ids), Lang.RU);

        assertEquals(2, list.size());
        assertTrue(list.get(0).getGeonameId() == ids[0] || list.get(0).getGeonameId() == ids[1]);
        assertTrue(list.get(1).getGeonameId() == ids[0] || list.get(1).getGeonameId() == ids[1]);

        assertEquals("Камызяк",getById(list, kamiziak).getCityName());
        assertEquals("Москва",getById(list, moscow).getCityName());
    }

    private static CityLocation getById(List<CityLocation> list, int id){
        for(CityLocation cl: list){
            if(cl.getGeonameId() == id){
                return cl;
            }
        }

        return null;
    }
    private static Set<Integer> Set(Integer[] ids){
        return new HashSet(Arrays.asList(ids));
    }
}
