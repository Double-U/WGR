/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.notifications;

import java.util.Date;

/**
 * 
 * @created Jul 14, 2011
 * @author double-u
 */
public class Schedule {
    protected Date date;
    protected long interval;
    
    public static enum Strategy {
        ASAP, TIMEOUT, REPEAT, DATE
    }
    
    protected Strategy strategy;

    public Schedule(Strategy strategy) {
        this.strategy = strategy;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }
}
