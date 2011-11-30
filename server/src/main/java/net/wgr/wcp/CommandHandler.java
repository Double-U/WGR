/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp;

import net.wgr.wcp.command.Command;

/**
 * 
 * @created Jul 7, 2011
 * @author double-u
 */
public interface CommandHandler {
    public String getName();
    public Object execute(final Command cmd);
}
