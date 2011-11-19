/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.notifications;

/**
 * 
 * @created Jul 14, 2011
 * @author double-u
 */
public class Notification {
    protected Schedule schedule;
    protected String title;
    protected String text;

    public Notification(Schedule schedule, String title, String text) {
        this.schedule = schedule;
        this.title = title;
        this.text = text;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
