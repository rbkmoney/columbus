package com.rbkmoney.columbus;

import com.rbkmoney.columbus.dao.CityLocationsDao;
import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.Lang;
import com.rbkmoney.columbus.service.GeoService;
import com.rbkmoney.damsel.state_processing.AutomatonSrv;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Test
    public void startManyThreads(){
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        for(int i=0; i<1000; i++ ){
            executorService.submit(new MyRannable());
        }
    };



    class MyRannable implements Runnable{

        @Override
        public void run() {
            System.out.println("Thread started:" + Thread.currentThread().getName() );
            try {
                Thread.currentThread().sleep(1000000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread stopped:" + Thread.currentThread().getName() );

        }
    }


}
