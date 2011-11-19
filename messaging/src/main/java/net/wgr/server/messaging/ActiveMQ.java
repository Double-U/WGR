/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.server.messaging;

import net.wgr.settings.Settings;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * ActiveMQ handler
 * Only String based messaging is implemented
 * @author DoubleU
 */
public class ActiveMQ {

    private static BrokerService service;
    private static boolean started;
    private static LocalMessaging lm;
    private static boolean ssl;

    public static void startBroker() {
        if (started) {
            return;
        }
        try {
            service = new BrokerService();
            service.setDataDirectory("data/");
            service.setPersistent(false);
            service.setUseJmx(false);

            if (ssl) {
                System.setProperty("javax.net.ssl.keyStore", Settings.getInstance().getString("SSLKeystore.Path"));
                System.setProperty("javax.net.ssl.keyStorePassword", Settings.getInstance().getString("SSLKeystore.Password"));
                service.addConnector("stomp+ssl://0.0.0.0:16662");
                service.addConnector("ssl://0.0.0.0:16663");
            } else {
                service.addConnector("stomp://0.0.0.0:16662");
                service.addConnector("tcp://0.0.0.0:16663");
            }

            service.start();
            started = true;
        } catch (Exception ex) {
            Logger.getLogger(ActiveMQ.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    public static void useSSL(boolean ssl) {
        // Useless to set this when already started
        if (started) {
            return;
        }
        ActiveMQ.ssl = ssl;
    }

    public static void stopBroker() {
        if (!started) {
            return;
        }
        try {
            service.stop();
        } catch (Exception ex) {
            Logger.getLogger(ActiveMQ.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    public static LocalMessaging getLocalMessaging() {
        // You can actually return a new LocalMessaging instance, but as it is set to connect to the service which isn't started, it will fail. And we wouldn't want that, would we?
        if (!started) {
            return null;
        }
        if (lm == null) {
            lm = new LocalMessaging(service.getVmConnectorURI());
        }
        return lm;
    }
}
