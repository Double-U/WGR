/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp;

import java.util.ArrayList;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.wgr.server.session.Sessions;

/**
 * Just tells the commander what to do
 * @created Aug 28, 2011
 * @author double-u
 */
public class Captain {
    public static void sendCommandToRequester(Command cmd, HttpServletRequest request) {
        ArrayList<UUID> targets = new ArrayList<>();
        HttpSession s = request.getSession();
        if (s == null) return;
        targets.add(Sessions.getInstance().getSession(s.getId()).getWCPConnectionId());
        Commander.getInstance().commandeer(cmd, new Scope(targets));
    }
}
