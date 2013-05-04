/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.server.http;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import javax.servlet.Servlet;
import net.wgr.server.session.SessionManager;
import net.wgr.settings.Settings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.WebSocketServlet;

/**
 *
 * @author DoubleU
 */
public class Server {

    private org.eclipse.jetty.server.Server server;
    private ArrayList<Servlet> servlets;
    private boolean started = false;
    private InetSocketAddress isa;
    private SessionManager sessionManager;

    public void boot() {
        servlets = new ArrayList<>();
        isa = new InetSocketAddress((Integer) Settings.getInstance().get("WebApplicationPort"));

        if (server == null) {
            server = new org.eclipse.jetty.server.Server(isa);
            server.setSendServerVersion(true);
            server.setGracefulShutdown(6000);
            server.setSendDateHeader(true);
            server.setSendServerVersion(false);

            sessionManager = new SessionManager();
            //sessionManager.setSessionCookie("W_SESSION");
        }

        Logger.getLogger(getClass()).info("Server booted");
    }

    public void start() {
        if (started) {
            return;
        }
        attachAll();
        try {
            server.start();
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.ERROR, "Failed to start server", ex);
        }
        started = true;
        Logger.getLogger(getClass()).info("Server started -- " + isa.getHostName() + ":" + isa.getPort());
    }

    public void stop() throws Exception {
        server.stop();
        detachAllHooks();
        started = false;
    }
    
    public void addServlet(Servlet s) {
        servlets.add(s);
    }

    private void attachAll() {
        ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
        sch.setContextPath("/");
        server.setHandler(sch);
        SessionHandler sessionHandler = new SessionHandler(sessionManager);
        sch.setSessionHandler(sessionHandler);

        for (Servlet s : servlets) {
            if (s instanceof ServerHook) {
                sch.addServlet(new ServletHolder(s), ((ServerHook) s).getContext());
            } else if (s instanceof WebSocketServlet) {
                sch.addServlet(new ServletHolder(s), "/wwscp/*");
            }
            Logger.getLogger(getClass()).info("Added servlet: " + s.getClass().getName());
        }
    }

    private void detachAllHooks() {
        server.setHandler(new HandlerCollection());
    }
}
