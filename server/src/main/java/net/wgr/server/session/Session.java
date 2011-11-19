/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.server.session;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import net.wgr.core.access.AuthenticationProvider.Ticket;
import org.eclipse.jetty.server.session.AbstractSession;

/**
 * 
 * @created Jul 26, 2011
 * @author double-u
 */
public class Session extends net.wgr.core.dao.Object {

    protected Ticket ticket;
    protected AbstractSession httpSession;
    protected Map<String, Object> attr;
    protected UUID wcpClientId;
    protected Date creationTime;
    // It is the same as in the httpSession, just for storing properly
    protected String id;

    public Session(AbstractSession httpSession) {
        this.httpSession = httpSession;
        if (httpSession != null) {
            creationTime = new Date(httpSession.getCreationTime());
        }
    }

    public Session() {
        creationTime = new Date();
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Object getAttribute(String name) {
        if (httpSession != null) {
            return httpSession.getAttribute(name);
        }
        return attr.get(name);
    }

    public void setAttribute(String name, Object value) {
        if (httpSession != null) {
            httpSession.setAttribute(name, value);
        } else {
            attr.put(name, value);
        }
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public static Session fromId(UUID sessionId) {
        return null;
    }

    public AbstractSession getHttpSession() {
        return httpSession;
    }

    public void setWCPConnectionId(UUID id) {
        wcpClientId = id;
    }

    public UUID getWCPConnectionId() {
        if (wcpClientId == null) {
            wcpClientId = UUID.randomUUID();
        }
        return wcpClientId;
    }

    @Override
    public String getColumnFamily() {
        return "sessions";
    }

    @Override
    public String getKeyFieldName() {
        return "id";
    }
}
