/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.services.utility;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wgr.server.http.HttpExchange;
import net.wgr.server.http.ServerHook;
import net.wgr.services.api.Twitter;
import org.apache.commons.lang.StringUtils;
import twitter4j.auth.RequestToken;

/**
 * 
 * @created Aug 25, 2011
 * @author double-u
 */
public class ServiceWebHelper extends HttpServlet implements ServerHook {

    private RequestToken rt;
    
    @Override
    public String getContext() {
        return "/service/*";
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] parts = StringUtils.split(req.getRequestURI(), '/');
        if (parts[1].equals("twitter")) {
            String part = (parts.length > 2 ? parts[2] : "");
            switch (part) {
                case "authorize":
                    rt = Twitter.getUnboundInstance().requestToken();
                    resp.sendRedirect(rt.getAuthenticationURL());
                    break;
                default:
                    if (!req.getParameter("oauth_token").isEmpty() && !req.getParameter("oauth_verifier").isEmpty()) {
                        Twitter.getUnboundInstance().getAccessTokenFor(rt, req.getParameter("oauth_verifier"));
                    }
                    break;
            }
        }
    }
}
