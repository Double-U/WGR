/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.UUID;
import net.wgr.core.access.AuthenticationProvider.Ticket;
import net.wgr.core.access.Authorize;
import net.wgr.server.session.Session;
import net.wgr.server.session.Sessions;
import net.wgr.wcp.Command.Result;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * @created Jul 7, 2011
 * @author double-u
 */
public abstract class Connection {
    // Only one connection activity listener allowed atm

    protected ConnectionActivityListener cal;
    // Making sure we at least have something to target
    protected UUID id;
    protected Gson gson;

    public abstract void sendMessage(String data) throws IOException;

    public void sendResult(Result result) throws IOException {
        String json = gson.toJson(result);
        sendMessage(json);
    }

    public Connection() {
        this.gson = new Gson();
    }

    public UUID getId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        return id;
    }

    public void handleCommand(Command cmd) {
        try {
            Session s = Sessions.getInstance().getSessionByWCPConnectionId(cmd.getConnection().getId());
            Ticket t = null;
            
            if (s != null) {
                t = s.getTicket() != null ? s.getTicket() : null;
            }
            if (Authorize.path("wcp:" + cmd.getHandler() + "/" + cmd.getName(), t)) {
                sendResult(cal.executeCommand(cmd));
            } else {
                sendResult(new Result(Result.NOT_AUTHORIZED, cmd.getTag()));
            }
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.WARN, "Command handling failed", ex);
        }
    }

    public ConnectionActivityListener getConnectionActivityListener() {
        return cal;
    }

    public void setConnectionActivityListener(ConnectionActivityListener cal) {
        this.cal = cal;
    }

    public void sendCommand(Command command) throws IOException {
        sendMessage(gson.toJson(command));
    }

    public void coupleToSession(String sessionId) {
        Sessions.getInstance().getSession(sessionId).setWCPConnectionId(this.id);
    }
}
