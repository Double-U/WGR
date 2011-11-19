/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.server.session;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.session.AbstractSession;
import org.eclipse.jetty.server.session.AbstractSessionManager;

/**
 * 
 * @created Jul 27, 2011
 * @author double-u
 */
public class SessionManager extends AbstractSessionManager {

    @Override
    public Map getSessionMap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    protected void invalidateSessions() throws Exception {
        Sessions.getInstance().invalidateAll();
    }

    @Override
    protected LocalSession newSession(HttpServletRequest request) {
        LocalSession ls = new SessionManager.LocalSession(this, request);
        return ls;
    }

    @Override
    protected boolean removeSession(String idInCluster) {
        Sessions.getInstance().removeSession(idInCluster);
        return true;
    }

    @Override
    protected void addSession(AbstractSession as) {
        // There are six 'Session' occurences in the following statement. Can you spot them all?
        Sessions.getInstance().addSession(as.getId(), new net.wgr.server.session.Session(as));
    }

    @Override
    public AbstractSession getSession(String string) {
        return (Sessions.getInstance().sessionExists(string)) ? Sessions.getInstance().getSession(string).getHttpSession() : null;
    }
    
    protected class LocalSession extends AbstractSession {

        public LocalSession(AbstractSessionManager abstractSessionManager, long created, long accessed, String clusterId) {
            super(abstractSessionManager, created, accessed, clusterId);
        }

        public LocalSession(AbstractSessionManager abstractSessionManager, javax.servlet.http.HttpServletRequest request) {
            super(abstractSessionManager, request);
        }

    }
}
