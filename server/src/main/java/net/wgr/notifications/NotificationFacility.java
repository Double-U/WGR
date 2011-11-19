/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.notifications;

import net.wgr.wcp.Scope;
import java.util.List;

/**
 * 
 * @created Jul 14, 2011
 * @author double-u
 */
public interface NotificationFacility {
    public List<Client> getClients();
    public List<Client> getClients(Scope scope);
    public void addClient(Client c);
}
