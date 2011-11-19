/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.logger;

import java.util.List;
import org.apache.log4j.spi.LoggingEvent;
import org.joda.time.Period;

/**
 * 
 * @created Sep 26, 2011
 * @author double-u
 */
public class LaunchPlatformPublisher extends LogPublisher {

    @Override
    public void publish(List<LoggingEvent> records, Period p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
