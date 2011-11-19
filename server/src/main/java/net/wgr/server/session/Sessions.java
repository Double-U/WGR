/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.server.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 
 * @created Jul 27, 2011
 * @author double-u
 */
public class Sessions {

    protected HashMap<String, Session> sessions;
    private static Sessions instance;

    public static Sessions getInstance() {
        if (instance == null) {
            instance = new Sessions();
        }
        return instance;
    }

    protected Sessions() {
        sessions = new HashMap<>();
    }

    public Session getSession(String id) {
        return sessions.get(id);
    }

    public boolean sessionExists(String id) {
        return sessions.containsKey(id);
    }

    public void removeSession(String id) {
        sessions.remove(id);
    }

    public void addSession(String key, Session session) {
        sessions.put(key, session);
    }

    public Set<String> getCurrentSessions() {
        return sessions.keySet();
    }

    public void invalidateAll() {
        sessions.clear();
    }

    public Session getSessionByWCPConnectionId(UUID id) {
        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            if (entry.getValue().getWCPConnectionId().equals(id)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
