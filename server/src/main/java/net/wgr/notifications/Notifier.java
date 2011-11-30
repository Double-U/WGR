/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.notifications;

import net.wgr.wcp.connectivity.Connection;
import net.wgr.wcp.Scope;
import java.util.List;
import net.wgr.core.ElementsByProxyList;
import net.wgr.wcp.Commander;
import net.wgr.wcp.ConnectionsListener;

/**
 * 
 * @created Jul 14, 2011
 * @author double-u
 */
public class Notifier {

    private static Notifier instance;
    protected Scheduler scheduler;
    protected ElementsByProxyList<NotificationFacility> facilities;
    protected DefaultNotificationFacility dnf;

    public static Notifier getInstance() {
        if (instance == null) {
            instance = new Notifier();
        }
        return instance;
    }

    public static void boot() {
        if (instance == null) {
            instance = new Notifier();
        }
    }

    public Notifier() {
        scheduler = new Scheduler();
        Commander.getInstance().addConnectionsListener(new WCPCL());
        facilities = new ElementsByProxyList<NotificationFacility>();
        facilities.enable(NotificationFacility.class);
        // Always have at least one notification facility, otherwise there will not be any notifying going on any time soon
        dnf = new DefaultNotificationFacility();
        facilities.add(dnf);
    }

    public DefaultNotificationFacility getDefaultNotificationFacility() {
        return dnf;
    }

    public void addNotificationFacility(NotificationFacility nf) {
        facilities.add(nf);
    }

    public void notify(Scope scope, Notification n) {
        List<Client> clients = facilities.getProxy().getClients(scope);
        scheduler.schedule(clients, n);
    }

    public class WCPCL implements ConnectionsListener {

        @Override
        public void connectionAdded(Connection c) {
            getDefaultNotificationFacility().addClient(new WCPConnectionClient(c));
        }

        @Override
        public void connectionRemoved(Connection c) {
            Client found = null;
            for (Client client : getDefaultNotificationFacility().getClients()) {
                if (client instanceof WCPConnectionClient) {
                    if (((WCPConnectionClient) client).getConnection().equals(c)) {
                        found = client;
                    }
                }
            }
            if (found != null) {
                getDefaultNotificationFacility().getClients().remove(found);
            }
        }
    }
}
