/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp;

import net.wgr.wcp.Command.Result;

/**
 * 
 * @created Jul 14, 2011
 * @author double-u
 */
public interface ConnectionActivityListener {
    public Result executeCommand(Command cmd);
    public void connectionClosed(Connection c);
}
