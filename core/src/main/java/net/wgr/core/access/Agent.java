/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.access;

import java.util.HashMap;
import java.util.UUID;
import net.wgr.core.Connection;
import net.wgr.core.ConnectionListener;
import net.wgr.core.access.AuthenticationProvider.Ticket;

/**
 * 
 * @created Jul 22, 2011
 * @author double-u
 */
public class Agent {
    protected HashMap<UUID, Connection> connections;
    protected Enforcer enforcer;
    
    public Agent() {
        connections = new HashMap<>();
        enforcer = new Enforcer();
    }
    
    public void addNewConnection(Connection c) {
        connections.put(c.getId(), c);
    }
    
    protected class CL implements ConnectionListener {

        @Override
        public boolean authenticate(Ticket ap) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
