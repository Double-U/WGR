/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.logger;

import java.util.concurrent.TimeUnit;
import net.wgr.utility.GlobalExecutorService;

/**
 * 
 * @created Sep 26, 2011
 * @author double-u
 */
public class PublisherConfiguration {
    protected boolean direct;
    protected int delta;
    protected TimeUnit timeUnit;
    
    public PublisherConfiguration() {
        this.direct = true;
    }
    
    public PublisherConfiguration(int delta, TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        this.delta = delta;
    }
    
    public void configure(LogPublisher publisher) {
        if (timeUnit != null && delta > 0) {
            GlobalExecutorService.get().scheduleAtFixedRate(publisher, delta, delta, timeUnit);
        }
        publisher.setConfiguration(this);
    }
    
    public boolean isDirect() {
        return direct;
    }
    
    public int getDelta() {
        return delta;
    }
    
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
