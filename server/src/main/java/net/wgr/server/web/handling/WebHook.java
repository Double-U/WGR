/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.server.web.handling;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wgr.core.access.AuthenticationProvider.Ticket;
import net.wgr.server.http.HttpExchange;
import net.wgr.server.session.Sessions;
import org.apache.commons.io.IOUtils;

/**
 * 
 * @created Jun 25, 2011
 * @author double-u
 */
public abstract class WebHook {

    private String selector;

    public WebHook(String selector) {
        this.selector = selector;
    }

    public String getSelector() {
        return selector;
    }

    public static class RequestBundle extends HttpExchange {

        protected String[] pathParts;
        protected byte[] requestBody;

        public RequestBundle(HttpServletRequest request, HttpServletResponse response, String[] pathParts) {
            super(request, response);
            this.pathParts = pathParts.clone();
        }
        
        public String[] getPathParts() {
            return pathParts;
        }

        public byte[] getRequestBodyAsByteArray() throws IOException {
            if (requestBody == null) {
                requestBody = IOUtils.toByteArray(this.getRequestBody());
                this.getRequestBody().close();
            }
            return requestBody;
        }

        public String getRequestBodyAsString() throws IOException {
            String str = new String(getRequestBodyAsByteArray());
            return str;
        }

        public void illegalRequest() throws IOException {
            // 404 Not Found
            this.sendResponseHeaders(404);
            this.close();
        }

        public void replyWithString(String reply) throws IOException {
            if (this.getRequestBody() != null) {
                byte[] replyBytes = reply.getBytes("UTF-8");
                this.getResponse().setContentType("text/html;charset=UTF-8");
                this.sendResponseHeaders(200);
                this.getBaseRequest().setHandled(true);
               
                this.getResponseBody().write(replyBytes);
                this.close();
            } else {
                throw new IllegalStateException("There is no connection available to send reply");
            }
        }

        public void setReplyHeader(String key, String value) {
            this.setResponseHeader(key, value);
        }

        public void notAuthorized() throws IOException {
            if (this.getRequestBody() != null) {
                this.sendResponseHeaders(403);
                this.getBaseRequest().setHandled(true);
                this.close();
            }
        }

        public Ticket getTicket() {
            if (this.getRequest().getSession() != null) {
                return Sessions.getInstance().getSession(this.getRequest().getSession().getId()).getTicket();
            }
            return null;
        }
    }

    public abstract void handle(RequestBundle rb) throws IOException;
}
