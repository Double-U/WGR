/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp.connectivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;
import net.wgr.core.access.AuthenticationProvider.Ticket;
import net.wgr.core.access.Authorize;
import net.wgr.server.session.Session;
import net.wgr.server.session.Sessions;
import net.wgr.wcp.ConnectionActivityListener;
import net.wgr.wcp.command.Command;
import net.wgr.wcp.command.Result;
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
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Object.class, new ArraySerializer());
        this.gson = gb.create();
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
                sendResult(new Result(Result.NOT_AUTHORIZED, cmd.getTag(), Result.ERROR));
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

    // Gson really needs some work done
    static class ArraySerializer implements JsonSerializer<Object> {

        public JsonElement serialize(final Object src, final Type typeOfSrc, final JsonSerializationContext context) {
            if (src == null) {
                return new JsonNull();
            }
            if (!src.getClass().isArray()) {
                return context.serialize(src);
            }

            JsonArray result = new JsonArray();
            for (Object el : (Object[]) src) {
                if (el == null) {
                    result.add(new JsonNull());
                } else {
                    result.add(context.serialize(el, el.getClass()));
                }
            }
            return result;
        }
    }
}
