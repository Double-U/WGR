/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wgr.server.application;

import java.io.IOException;
import net.wgr.server.http.HttpExchange;
import net.wgr.server.http.ServerHook;

/**
 *
 * @author DoubleU
 */
public interface Application extends ServerHook {
    public String getRootFolder();
    public void handle(HttpExchange he) throws IOException;
}
