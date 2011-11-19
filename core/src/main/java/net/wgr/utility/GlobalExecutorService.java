/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.utility;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 
 * @created Aug 13, 2011
 * @author double-u
 */
public class GlobalExecutorService {
    private static ScheduledExecutorService ses;

    public static ScheduledExecutorService get() {
        if (ses == null) {
            ses = new ScheduledThreadPoolExecutor(1);
        }
        return ses;
    }
}
