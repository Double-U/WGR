/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.notifications;

import java.io.IOException;


import net.wgr.wcp.Command;
import net.wgr.wcp.Connection;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * @created Jul 14, 2011
 * @author double-u
 */
public class WCPConnectionClient extends Client {
    protected Connection conn;

    public WCPConnectionClient(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void notify(Notification n) {
        try {
            conn.sendCommand(new Command("", "alert", n.getText()));
        } catch (IOException ex) {
            Logger.getLogger(WCPConnectionClient.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    public Connection getConnection() {
        return conn;
    }
    
}
