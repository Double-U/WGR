/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.notifications;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @created Jul 14, 2011
 * @author double-u
 */
public class Scheduler {

    protected Timer timer;

    protected class Job extends TimerTask {

        protected Notification notification;
        protected List<Client> clients;

        @Override
        public void run() {
            for (Client c : clients) {
                c.notify(notification);
            }
        }

        public List<Client> getClients() {
            return clients;
        }

        public void setClients(List<Client> clients) {
            this.clients = clients;
        }

        public Notification getNotification() {
            return notification;
        }
        
        public Job(Notification n) {
            notification = n;
        }
    }

    public Scheduler() {
        timer = new Timer();
    }

    public void schedule(List<Client> clients, Notification notification) {
        Job job = new Job(notification);
        job.setClients(clients);
        switch (notification.getSchedule().getStrategy()) {
            case ASAP:
                job.run();
                break;
            case REPEAT:
                timer.scheduleAtFixedRate(job, 0, notification.getSchedule().getInterval());
                break;
            case DATE:
                timer.schedule(job, notification.getSchedule().getDate());
                break;
        }
    }
}
