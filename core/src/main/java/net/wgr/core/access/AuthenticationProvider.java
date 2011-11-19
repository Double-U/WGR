/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.access;

import java.util.Date;
import net.wgr.core.access.entity.User;

/**
 * 
 * @created Jul 22, 2011
 * @author double-u
 */
public abstract class AuthenticationProvider {
    
    protected Ticket createTicket(Date expirationDate, User u) {
        // Do some checks before delivering the ticket
        return new Ticket(expirationDate, u);
    }
    
    public class Ticket {
        protected Date validUntil;
        protected User user;

        private Ticket(Date validUntil, User u) {
            this.validUntil = validUntil;
            this.user = u;
        }
        
        public Date getExpirationDate() {
            return validUntil;
        }
        
        public void setExpirationDate(Date d) {
            validUntil = d;
        }

        public User getUser() {
            return user;
        }
        
        public boolean isValid() {
            // TODO 
            return false;
        }
    }
}
