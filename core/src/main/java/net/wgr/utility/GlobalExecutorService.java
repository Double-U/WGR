/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.utility;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @created Aug 13, 2011
 * @author double-u
 */
public class GlobalExecutorService {
    private static ScheduledExecutorService ses;
    private static AtomicInteger counter = new AtomicInteger();

    public static ScheduledExecutorService get() {
        if (ses == null) {
            ses = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "SES-" + counter.incrementAndGet());
                    return t;
                }
            });
        }
        return ses;
    }
}
