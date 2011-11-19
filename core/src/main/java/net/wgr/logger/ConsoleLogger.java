/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.logger;

import java.util.List;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * 
 * @created Sep 26, 2011
 * @author double-u
 */
public class ConsoleLogger extends LogPublisher {
    
    protected final PatternLayout pl = new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN);

    @Override
    public void publish(List<LoggingEvent> records, Period p) {
        if (p != null) {
            DateTime dt = new DateTime();
            System.out.println("Logging output for " + dt.minus(p).toString() + " - " + dt.toString());
        }
        
        for (LoggingEvent le : records) {
            System.out.println(pl.format(le));
        }
    }
    
}
