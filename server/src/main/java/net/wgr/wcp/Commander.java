/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp;

import net.wgr.wcp.connectivity.Connection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.wgr.core.ElementsByProxyList;
import net.wgr.wcp.command.Command;
import net.wgr.wcp.command.Result;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * W Command Protocol Orchestrator
 * @created Jul 7, 2011
 * @author double-u
 */
public class Commander {

    private static Commander instance;
    protected List<Connection> connections;
    protected ElementsByProxyList<CommandHandler> handlers;
    protected ElementsByProxyList<ConnectionsListener> connectionListeners;

    private Commander() {
        this.handlers = new ElementsByProxyList<>();
        this.connections = new ArrayList<>();
        this.connectionListeners = new ElementsByProxyList<>();
        this.connectionListeners.enable(ConnectionsListener.class);
    }

    public static Commander getInstance() {
        if (instance == null) {
            instance = new Commander();
        }
        return instance;
    }

    public void addCommandHandler(CommandHandler ch) {
        this.handlers.add(ch);
    }

    public void addConnection(Connection connection) {
        connectionListeners.getProxy().connectionAdded(connection);
        connection.setConnectionActivityListener(new CAL());
        connections.add(connection);
    }

    public void addConnectionsListener(ConnectionsListener cl) {
        this.connectionListeners.add(cl);
    }

    public void removeConnection(Connection conn) {
        connectionListeners.getProxy().connectionRemoved(conn);
        connections.remove(conn);
    }

    public void commandeer(Command cmd, Scope scope) {
        String json = cmd.toJson();

        switch (scope.getTarget()) {
            case ALL:
                for (Connection c : connections) {
                    sendMessageToConnection(json, c);
                }
                break;
            case BY_ID:
                for (Connection c : connections) {
                    for (UUID id : scope.getIds()) {
                        if (c.getId().equals(id)) {
                            sendMessageToConnection(json, c);
                        }
                    }
                }
                break;
        }
    }

    protected void sendMessageToConnection(String message, Connection c) {
        try {
            c.sendMessage(message);
        } catch (IOException ex) {
            Logger.getLogger(Commander.class.getName()).log(Level.INFO, "Sending message to connection failed", ex);
        }
    }

    public class CAL implements ConnectionActivityListener {

        @Override
        public Result executeCommand(Command cmd) {
            CommandHandler cl = null;
            for (CommandHandler c : handlers) {
                if (c.getName().equals(cmd.getHandler())) {
                    cl = c;
                }
            }
            if (cl == null) {
                throw new IllegalArgumentException("Handler does not exist:" + cmd.getHandler());
            }
            Object result = cl.execute(cmd);
            if (result instanceof Result) {
                Result r = (Result) result;
                if (r.getType() == null || r.getType().isEmpty()) {
                    throw new IllegalArgumentException("Result has no type");
                }
                r.setTag(cmd.getTag());
                return r;
            }
            if (result == null) {
                // Execution failed
                return new Result(Result.EXECUTION_FAILED, cmd.getTag(), Result.ERROR);
            } else {
                return new Result(result, cmd.getTag(), Result.RESULT);
            }
        }

        @Override
        public void connectionClosed(Connection c) {
            removeConnection(c);
        }
    }
}
