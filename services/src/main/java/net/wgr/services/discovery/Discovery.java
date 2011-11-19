/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.services.discovery;

import java.io.IOException;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author double-u
 */
public class Discovery {

    private static Discovery instance;
    private JmDNS mDNS;

    public static Discovery getInstance() {
        if (instance == null) {
            instance = new Discovery();
        }
        return instance;
    }

    public Discovery() {
        try {
            mDNS = JmDNS.create();
        } catch (IOException ex) {
            Logger.getLogger(Discovery.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    public void announceService(BasicDiscoverableService service) {
        ServiceInfo info = getServiceInfoForDiscoverableService(service);
        try {
            mDNS.registerService(info);
        } catch (IOException ex) {
            Logger.getLogger(Discovery.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    private ServiceInfo getServiceInfoForDiscoverableService(BasicDiscoverableService service) {
        ServiceInfo info = ServiceInfo.create(service.getFQSN(), service.getName(), service.getPort(), 1, 1, service.getProperties());
        return info;
    }
}
