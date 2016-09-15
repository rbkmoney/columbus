package com.rbkmoney.columbus;

import com.rbkmoney.columbus.dao.CityLocationsDao;
import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.Lang;
import com.rbkmoney.columbus.service.GeoService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Ignore //need location names db with data (for example "city_locations_ru")
public class GeoServiceTest {

    @Autowired
    CityLocationsDao cityLocationsDao;

    @Autowired
    GeoService geoEnrichmentService;

    @Test
    public void getLocationByGeoId(){
        CityLocation byGeoId = cityLocationsDao.getByGeoId(553248, Lang.RU);
        assertEquals("Камызяк",byGeoId.getCityName());
    }

    @Test
    public void getGeoDataBYIdTest(){
        //todo: write correct test
        geoEnrichmentService.enrich();
    }




}
