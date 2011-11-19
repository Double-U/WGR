/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.notifications;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation
 * @created Jul 14, 2011
 * @author double-u
 */
public class DefaultNotificationFacility extends AbstractNotificationFacility {
    protected ArrayList<Client> clients;

    @Override
    public List<Client> getClients() {
        return clients;
    }
    
    public DefaultNotificationFacility() {
        clients = new ArrayList<Client>();
    }
}
