package com.rbkmoney.columbus.endpoint;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.woody.api.event.CompositeServiceEventListener;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import com.rbkmoney.woody.thrift.impl.http.event.HttpServiceEventLogListener;
import com.rbkmoney.woody.thrift.impl.http.event.ServiceEventLogListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/repo")
public class GeoIpServlet extends GenericServlet {

    private Servlet thriftServlet;

    @Autowired
    private GeoIpServiceSrv.Iface requestHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .withEventListener(
                        new CompositeServiceEventListener(
                                new ServiceEventLogListener(),
                                new HttpServiceEventLogListener()))
                .build(GeoIpServiceSrv.Iface.class, requestHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
