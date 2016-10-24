package com.rbkmoney.columbus;

import com.rbkmoney.columbus.dao.CityLocationsDao;
import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.Lang;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CityLocationsDaoTest {
    @Autowired
    CityLocationsDao cityLocationsDao;

    @Test
    public void getLocationByGeoId(){
        CityLocation byGeoId = cityLocationsDao.getByGeoId(553248, Lang.RU);
        assertEquals("Камызяк",byGeoId.getCityName());
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

    public static CityLocation getById(List<CityLocation> list, int id){
        for(CityLocation cl: list){
            if(cl.getGeonameId() == id){
                return cl;
            }
        }

        return null;
    }
    public static Set<Integer> Set(Integer[] ids){
       return new HashSet(Arrays.asList(ids));
    }

}
