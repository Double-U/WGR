/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.server.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wgr.server.application.Application;
import net.wgr.server.session.SessionManager;
import net.wgr.settings.Settings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
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
    private ArrayList<AppHandler> hooks;
    private ArrayList<Servlet> servlets;
    private boolean started = false;
    private InetSocketAddress isa;
    private SessionManager sessionManager;

    public void boot() {
        hooks = new ArrayList<>();
        servlets = new ArrayList<>();
        isa = new InetSocketAddress((Integer) Settings.getInstance().get("WebApplicationPort"));

        if (server == null) {
            server = new org.eclipse.jetty.server.Server(isa);
            // Proud to be on the Jetty!
            server.setSendServerVersion(true);
            server.setGracefulShutdown(6000);
            server.setSendDateHeader(true);
            server.setSendServerVersion(false);

            sessionManager = new SessionManager();
            sessionManager.setSessionCookie("W_SESSION");
        }


        Logger.getLogger(getClass()).info("W - Server booted");
    }

    public void start() {
        if (started) {
            return;
        }
        attachAll();
        try {
            server.start();
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.ERROR, null, ex);
        }
        started = true;
        Logger.getLogger(getClass()).info("Server started -- " + isa.getHostName() + ":" + isa.getPort());
    }

    public void stop() throws Exception {
        server.stop();
        detachAllHooks();
        started = false;
    }

    public void addHook(Application app) {
        hooks.add(new AppHandler(app));
    }

    public void addServlet(Servlet s) {
        servlets.add(s);
    }

    private void attachAll() {
        ContextHandlerCollection chc = new ContextHandlerCollection();
        
        if (servlets.size() > 0) {
            ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
            sch.setContextPath("/");
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
            
            chc.addHandler(sch);
        }
        
        for (AppHandler hh : hooks) {
            ContextHandler ch = new ContextHandler(hh.getApplication().getContext());
            ch.setHandler(hh);
            chc.addHandler(ch);
            Logger.getLogger(getClass()).info("Added hook: " + hh.getApplication().getClass().getName());
        }

        server.setHandler(chc);
    }

    private class AppHandler extends AbstractHandler {

        private Application app;

        public AppHandler(Application app) {
            this.app = app;
        }

        public Application getApplication() {
            return app;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            // Package it up
            HttpExchange he = new HttpExchange(baseRequest, request, response);
            app.handle(he);
        }
    }

    private void detachAllHooks() {
        server.setHandler(new HandlerCollection());
    }
}
