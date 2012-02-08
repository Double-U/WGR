/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp.connectivity;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.websocket.WebSocket;

/**
 * W WebSocket Command Protocol (WWSCP)
 * @created Jul 7, 2011
 * @author double-u
 */
public class WebSocketConnection extends ServletConnection {

    private org.eclipse.jetty.websocket.WebSocket.Connection conn;

    public WebSocketConnection(HttpServletRequest request, WebSocket.Connection conn) {
        super(request);
        this.conn = conn;
    }

    @Override
    public void sendMessage(String data) throws IOException {
        try {
            conn.sendMessage(data);
        } catch (IOException ex) {
            if (conn.isOpen()) {
                conn.disconnect();
            }
            close();
        }
    }

    public void close() {
        cal.connectionClosed(this);
    }
}
