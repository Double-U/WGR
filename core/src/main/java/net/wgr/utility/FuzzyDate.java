/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.utility;

import java.util.Date;
import org.joda.time.Interval;
import org.joda.time.Period;

/**
 * 
 * @created Aug 7, 2011
 * @author double-u
 */
public class FuzzyDate {

    final static int SECOND = 1;
    final static int MINUTE = 60 * SECOND;
    final static int HOUR = 60 * MINUTE;
    final static int DAY = 24 * HOUR;
    final static int MONTH = 30 * DAY;

    public static String format(Date date) {
        Date now = new Date();
        // In seconds
        double delta = (now.getTime() - date.getTime()) / 1000;
        Interval interval = new Interval(date.getTime(), now.getTime());
        Period p = interval.toPeriod();

        if (delta < 0) {
            return "not yet";
        }
        if (delta < 1 * MINUTE) {
            return p.getSeconds() == 1 ? "one second ago" : p.getSeconds() + " seconds ago";
        }
        if (delta < 2 * MINUTE) {
            return "a minute ago";
        }
        if (delta < 45 * MINUTE) {
            return p.getMinutes() + " minutes ago";
        }
        if (delta < 90 * MINUTE) {
            return "an hour ago";
        }
        if (delta < 24 * HOUR) {
            return p.getHours() + " hours ago";
        }
        if (delta < 48 * HOUR) {
            return "yesterday";
        }
        if (delta < 30 * DAY) {
            return p.getDays() + " days ago";
        }
        if (delta < 12 * MONTH) {
            int months = p.getMonths();
            return months <= 1 ? "one month ago" : months + " months ago";
        } else {
            int years = p.getYears();
            return years <= 1 ? "one year ago" : years + " years ago";
        }
    }
}
