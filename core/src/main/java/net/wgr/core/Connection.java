/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core;

import java.util.UUID;

/**
 * Does not so much define the way to communicate, but more the whole process (sender - link - receiver)
 * @created Jul 22, 2011
 * @author double-u
 */
public class Connection {
    protected UUID id;
    
    // Only one connection manager allowed, otherwise things get messy
    protected ConnectionListener listener;

    public UUID getId() {
        return id;
    }
    
    public void close(ClosingArgument ca) {
        
    }
    
    public static enum ClosingArgument {
        TIMEOUT, NOT_SPECIFIED
    }
}
