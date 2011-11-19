/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.server.http;

import java.io.IOException;

/**
 *
 * @author double-u
 */
public abstract class Request {

    private HttpExchange he;

    public Request(HttpExchange he) {
        this.he = he;
    }
    
    public HttpExchange getExchange() {
        return he;
    }

    public abstract void routeToHandler(Class type) throws IOException;
}
