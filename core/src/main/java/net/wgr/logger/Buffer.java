/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import net.wgr.utility.GlobalExecutorService;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * 
 * @created Sep 11, 2011
 * @author double-u
 */
public class Buffer extends AppenderSkeleton implements Runnable {

    protected Level baseLevel;
    protected ArrayList<LoggingEvent> records;
    protected ArrayList<LogPublisher> publishers;
    protected final int retentionTreshold = 2;

    public Buffer(Level baseLevel) {
        this.records = new ArrayList<>();
        this.publishers = new ArrayList<>();
        this.baseLevel = baseLevel;
    }

    public void boot() {
        Logger.getRootLogger().addAppender(this);
        GlobalExecutorService.get().scheduleAtFixedRate(this, 1, 1, TimeUnit.DAYS);
    }

    public void addPublisher(LogPublisher lp, PublisherConfiguration pc) {
        lp.setBuffer(this);
        pc.configure(lp);
        this.publishers.add(lp);
    }

    /**
     * Request events for given period
     * @param p period
     * @return log events
     */
    public ArrayList<LoggingEvent> requestLogs(Period p) {
        ArrayList<LoggingEvent> result = new ArrayList<>();
        ListIterator<LoggingEvent> li = records.listIterator(records.size());
        // Working with long's instead of DateTime objects for better performance
        long treshold = new DateTime().minus(p).getMillis();
        while (li.hasPrevious()) {
            LoggingEvent le = li.previous();
            if (le.getTimeStamp() > treshold) {
                result.add(le);
            } else {
                break;
            }
        }
        return result;
    }

    @Override
    public void close() {
        run();
    }

    @Override
    public void run() {
        // Cleanup
        long treshold = new DateTime().minus(retentionTreshold).getMillis();
        ListIterator<LoggingEvent> li = records.listIterator();
        while (li.hasNext()) {
            if (li.next().getTimeStamp() < treshold) {
                li.remove();
            }
        }
        Logger.getLogger(getClass()).log(Level.INFO, "Cleaned up log entries exceeding treshold");
    }

    @Override
    protected void append(LoggingEvent event) {
        if (event.getLevel().isGreaterOrEqual(baseLevel)) {
            // We're interested
            this.records.add(event);
            for (LogPublisher p : publishers) {
                p.logEntryAdded();
            }
        }
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    public List<LoggingEvent> getLastEntry() {
        ArrayList<LoggingEvent> list = new ArrayList<>();
        list.add(records.get(records.size() - 1));
        return list;
    }

    public void addPublisher(ConsoleLogger consoleLogger) {
        addPublisher(consoleLogger, new PublisherConfiguration());
    }
}
