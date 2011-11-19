/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core;

import net.wgr.core.access.AuthenticationProvider;

/**
 * 
 * @created Jul 22, 2011
 * @author double-u
 */
public interface ConnectionListener {
    /**
     * Connection wishes to authenticate
     * @param apt the provider ticket by which authentication will be provided
     * @return success if agent agrees
     */
    public boolean authenticate(AuthenticationProvider.Ticket apt);
}
