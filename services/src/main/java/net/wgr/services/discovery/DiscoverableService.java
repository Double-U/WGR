/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wgr.services.discovery;

enum Protocol {
    TCP ("_tcp"), UDP ("_udp");
    
    private final String protocolValue;
    
    Protocol(String protocolValue) {
        this.protocolValue = protocolValue;
    }
    
    @Override
    public String toString() {
        return protocolValue;
    }
}

/**
 * Bare essentials for propagating a service
 * @author double-u
 */
public interface DiscoverableService  {
    public String getDomain();
    public Protocol getProtocol();
    public String getApplication();
    public int getPort();
    public String getName();
}
