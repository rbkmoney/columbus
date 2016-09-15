package com.rbkmoney.columbus;

import com.rbkmoney.columbus.dao.GeoIpDao;
import com.rbkmoney.columbus.model.Lang;
import com.rbkmoney.columbus.model.LocationInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
//@Ignore //to run test need correct .mmdb file path in config
public class MaxMindDbTest {

    @Autowired
    GeoIpDao geoIpDao;

    @Test
    public void testMoscow() {
        LocationInfo locationInfoByIp = geoIpDao.getLocationInfoByIp("94.159.54.234");
        assertEquals("Москва", locationInfoByIp.getCity().getNames().get(Lang.RU));
    }

    @Test
    public void testWrongIp() {
        LocationInfo undefinedLocation = geoIpDao.getLocationInfoByIp("null");
        assertEquals("Неизвестно", undefinedLocation.getCity().getNames().get(Lang.RU));
    }
}
