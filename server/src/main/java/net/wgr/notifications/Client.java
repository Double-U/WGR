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
public abstract class Client {

    protected String hostname;
    protected Listener listener;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public abstract void notify(Notification n);

    public void close() {
        listener.closed(this);
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
    
    public interface Listener {
        public void closed(Client c);
    }
}
