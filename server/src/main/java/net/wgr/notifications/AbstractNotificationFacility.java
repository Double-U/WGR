/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.notifications;

import net.wgr.wcp.Scope;
import java.util.List;

/**
 * 
 * @created Jul 14, 2011
 * @author double-u
 */
public abstract class AbstractNotificationFacility implements NotificationFacility {

    @Override
    public List<Client> getClients(Scope scope) {
        switch (scope.getTarget()) {
            case ALL:
                return getClients();
        }
        return null;
    }

    @Override
    public void addClient(Client c) {
        c.setListener(new ClientListener());
        getClients().add(c);
    }

    public class ClientListener implements Client.Listener {

        @Override
        public void closed(Client c) {
            getClients().remove(c);
        }
    }
}
