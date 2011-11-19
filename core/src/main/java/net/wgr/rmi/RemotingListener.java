/*
 * Copyright 2011 Wannes De Smet 
 * All rights reserved
 */
package net.wgr.rmi;

/**
 * Useful for scheduling
 * @author double-u
 */
public interface RemotingListener {
    /**
     * Command was parsed, answer sent
     */
    public void invocationCompleted();
}
