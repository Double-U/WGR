/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.logger;

import java.util.List;
import org.apache.log4j.spi.LoggingEvent;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * 
 * @created Sep 11, 2011
 * @author double-u
 */
public abstract class LogPublisher implements Runnable {
    
    protected Buffer buffer;
    protected PublisherConfiguration conf;
    protected DateTime lastRun;
    
    public LogPublisher() { 
        lastRun = new DateTime();
    }
    
    public void run() {
        if (conf.isDirect()) {
            publish(buffer.getLastEntry(), null);
        } else {
            Period p = new Period(System.currentTimeMillis() - lastRun.getMillis());
            publish(buffer.requestLogs(p), p);
        }
    }
    
    public void logEntryAdded() {
        if (isDirect()) {
            run();
        }
    }
    
    public abstract void publish(List<LoggingEvent> records, Period p);

    public void setConfiguration(PublisherConfiguration conf) {
        this.conf = conf;
    }
    
    public void setBuffer(Buffer b) {
        this.buffer = b;
    }
    
    public boolean isDirect() {
        return conf.isDirect();
    }
}
