/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp.connectivity;

import net.wgr.wcp.connectivity.ServletConnection;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wgr.wcp.command.Command;

/**
 * 
 * @created Jul 7, 2011
 * @author double-u
 */
public class HttpConnection extends ServletConnection {
    
    private HttpServletResponse response;

    public HttpConnection(HttpServletRequest request, HttpServletResponse response) {
        super(request);
        this.response = response;
    }

    @Override
    public void handleCommand(Command cmd) {
        super.handleCommand(cmd);
        cal.connectionClosed(this);
    }
    
    @Override
    public void sendMessage(String data) throws IOException {
        response.getOutputStream().write(data.getBytes("UTF-8"));
        response.getOutputStream().flush();
    }
}
