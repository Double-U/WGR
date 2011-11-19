/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.server.web.handling;

import java.io.IOException;
import net.wgr.wcp.CommandHandler;
import org.eclipse.jetty.server.Request;

/**
 * 
 * @created Jul 6, 2011
 * @author double-u
 */
public abstract class WebCommandHandler extends WebHook implements CommandHandler {

    public WebCommandHandler(String selector) {
        super(selector);
    }

    @Override
    public void handle(RequestBundle rb) throws IOException {
        Request.getRequest(rb.getRequest()).setHandled(false);
    }

    @Override
    public String getName() {
        return getSelector();
    }
}
