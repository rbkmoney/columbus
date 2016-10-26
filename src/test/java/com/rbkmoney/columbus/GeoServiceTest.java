package com.rbkmoney.columbus;

import com.rbkmoney.columbus.dao.CityLocationsDao;
import com.rbkmoney.columbus.dao.GeoIpDao;
import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.Lang;
import com.rbkmoney.columbus.model.LocationInfo;
import com.rbkmoney.columbus.service.GeoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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

    @Test
    public void testWrongIp() {
        LocationInfo undefinedLocation = geoIpDao.getLocationInfoByIp("null");
        assertEquals("Неизвестно", undefinedLocation.getCity().getNames().get(Lang.RU));
    }

    @Test
    public void getLocationByIp(){
        for(String ip: IP_TO_CITY.keySet()){
            LocationInfo locationInfo = service.getLocationByIp(ip);
            assertEquals(locationInfo.getCity().getNames().get(Lang.ENG), IP_TO_CITY.get(ip));
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
