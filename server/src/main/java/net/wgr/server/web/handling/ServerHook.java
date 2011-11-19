/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.server.web.handling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wgr.core.access.Authorize;
import net.wgr.server.http.HttpExchange;
import net.wgr.server.session.Session;
import net.wgr.server.session.Sessions;
import net.wgr.wcp.Command;
import net.wgr.wcp.CommandHandler;
import net.wgr.wcp.Commander;
import net.wgr.wcp.HttpConnection;
import net.wgr.wcp.WebSocketConnection;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

/**
 * Does all the wet work for proper handling
 * @author double-u
 */
public class ServerHook {

    protected List<WebHook> hooks;
    protected String context;

    public class HttpHandler extends HttpServlet implements net.wgr.server.http.ServerHook {

        @Override
        public String getContext() {
            return context;
        }

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            HttpExchange he = new WebHook.RequestBundle(req, resp, req.getPathInfo().split("/"));

            String[] requestParts = he.getRequestURI().toString().split("/");
            WebHook.RequestBundle rb = new WebHook.RequestBundle(he.getRequest(), he.getResponse(), (String[]) ArrayUtils.subarray(requestParts, 2, 1000));
            rb.getBaseRequest().setHandled(false);
            if (!Authorize.path(rb.getRequestURI(), Sessions.getInstance().getSession(req.getSession().getId()).getTicket())) {
                rb.notAuthorized();
                return;
            }

            // Check if this is a wwscp command (last part equals wwscp)
            if (requestParts.length != 0  && requestParts[requestParts.length - 1].equals("wwscp")) {
                HttpConnection hc = new HttpConnection(he.getRequest(), he.getResponse());
                Commander.getInstance().addConnection(hc);
                hc.handleCommand(Command.parse(IOUtils.toString(he.getRequestBody(), "UTF-8"), hc));
            }
            // Access control
            Session session = Sessions.getInstance().getSession(he.getRequest().getSession(true).getId());

            WebHook designatedHook = null;
            ArrayList<WebHook> temp = new ArrayList<>(hooks);
            while (!rb.getBaseRequest().isHandled() && !temp.isEmpty()) {
                if (designatedHook != null) {
                    temp.remove(designatedHook);
                }
                designatedHook = electHook(he, temp);
                if (designatedHook == null) {
                    rb.getBaseRequest().setHandled(false);
                    break;
                }
                try {
                    designatedHook.handle(rb);
                } catch (Exception ex) {
                    Logger.getLogger(getClass()).log(Level.ERROR, "Uncaught exception in request handling", ex);
                    return;
                }
            }
        }
    }

    protected WebHook electHook(HttpExchange he, List<WebHook> hooks) {
        int highestMatch = 10000;
        WebHook designatedHook = null;

        for (WebHook ph : hooks) {
            if (ph.getSelector().equals("*")) {
                highestMatch = 0;
                designatedHook = ph;
            }
            if (!he.getRequestURI().toString().contains(ph.getSelector())) {
                continue;
            }
            // I hope this is a well performing function allright ...
            int ld = StringUtils.getLevenshteinDistance(he.getRequestURI().toString(), ph.getSelector());
            if (ld < highestMatch) {
                designatedHook = ph;
                highestMatch = ld;
            }
        }
        return designatedHook;
    }

    public ServerHook(String context) {
        this.hooks = new ArrayList<>();
        this.context = context;
    }

    public WebSocketHandler getWebSocketHandler() {
        return new WebSocketHandler();
    }

    public HttpHandler getHttpHandler() {
        return new HttpHandler();
    }

    public void addWebHook(WebHook hook) {
        this.hooks.add(hook);
        if (hook instanceof CommandHandler) {
            Commander.getInstance().addCommandHandler((CommandHandler) hook);
        }
    }

    public class WebSocketHandler extends WebSocketServlet {

        @Override
        public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
            // WWSCP : W WebSocket Command Protocol. Yes, it's actually just JSON.
            if (protocol != null && protocol.toUpperCase().equals("WWSCP")) {
                return new TextWebSocket(request);
            }
            return null;
        }
    }

    public final class TextWebSocket implements WebSocket.OnTextMessage {

        private HttpServletRequest request;
        private Connection conn;
        private WebSocketConnection wsc;

        @Override
        public void onOpen(Connection connection) {
            this.conn = connection;
            wsc = new WebSocketConnection(request, conn);
            Commander.getInstance().addConnection(wsc);
        }

        @Override
        public void onClose(int closeCode, String message) {
            wsc.close();
        }

        @Override
        public void onMessage(String data) {
            wsc.handleCommand(Command.parse(data, wsc));
        }

        public TextWebSocket(HttpServletRequest request) {
            this.request = request;
        }
    }
}
