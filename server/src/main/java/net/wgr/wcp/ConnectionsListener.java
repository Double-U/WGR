/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp;

import net.wgr.wcp.connectivity.Connection;

/**
 * 
 * @created Jul 14, 2011
 * @author double-u
 */
public interface ConnectionsListener {
    public void connectionAdded(Connection c);
    public void connectionRemoved(Connection c);
}
