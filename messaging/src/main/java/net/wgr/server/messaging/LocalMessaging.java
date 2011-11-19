/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.server.messaging;

import java.net.URI;


import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author DoubleU
 */
public class LocalMessaging {

    private Connection conn;
    private boolean started;

    public LocalMessaging(URI lmURI) {
        try {
            ActiveMQConnectionFactory amcf = new ActiveMQConnectionFactory(lmURI);
            conn = amcf.createConnection();
            conn.setExceptionListener(new EL());
            conn.start();

            started = true;
        } catch (JMSException ex) {
            Logger.getLogger(LocalMessaging.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    public Session createNewSession(boolean transacted, int ack_mode) {
        if (!started) {
            return null;
        }
        try {
            return conn.createSession(transacted, ack_mode);
        } catch (JMSException ex) {
            Logger.getLogger(LocalMessaging.class.getName()).log(Level.ERROR, null, ex);
        }
        return null;
    }

    public void stop() {
        if (!started) {
            return;
        }
        try {
            conn.close();
        } catch (JMSException ex) {
            Logger.getLogger(LocalMessaging.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    private class EL implements ExceptionListener {

        @Override
        public void onException(JMSException exception) {
            System.out.println("JMSException ocurred : " + exception.getMessage());
        }
    }
}
