package com.rbkmoney.columbus;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.apache.thrift.TException;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Ignore
public class TestApi {
    @Test
    public void test() throws URISyntaxException, TException {
        THSpawnClientBuilder clientBuilder = (THSpawnClientBuilder) new THSpawnClientBuilder().withAddress(new URI("http://localhost:8022/repo"));
        GeoIpServiceSrv.Iface client = clientBuilder.build(GeoIpServiceSrv.Iface.class);

        LocationInfo locationInfo = client.getLocation("94.159.54.234");

        Set<Integer> set = new HashSet<>();
        set.add(553248);
        set.add(524901);
        Map<Integer,String> map = client.getLocationName(set, "ru");

        System.out.println("");
    }
}