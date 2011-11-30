/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp.connectivity;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @created Jul 14, 2011
 * @author double-u
 */
public abstract class ServletConnection extends Connection {

    protected HttpServletRequest request;

    public ServletConnection(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }
}
