/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.access;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import net.wgr.core.Connection;

/**
 * 
 * @created Jul 23, 2011
 * @author double-u
 */
public class Enforcer {

    protected Timer timer;

    public boolean enforce(Connection c, AuthenticationProvider.Ticket ticket) {
        if (ticket.getExpirationDate() != null) {
            if (new Date().after(ticket.getExpirationDate())) {
                return false;
            }
            
            ExpirationHandler exp = new ExpirationHandler(c);
            timer.schedule(exp, ticket.getExpirationDate());
        }


        return true;
    }

    protected class ExpirationHandler extends TimerTask {

        protected Connection conn;

        public ExpirationHandler(Connection c) {
            this.conn = c;
        }

        @Override
        public void run() {
            conn.close(Connection.ClosingArgument.TIMEOUT);
        }
    }
}
