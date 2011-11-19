/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.services.discovery;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;



/**
 *
 * @author double-u
 */
public class BasicDiscoverableService implements DiscoverableService {

    private String domain, application, name;
    private Protocol protocol;
    private int port;
    private Map<String, String> properties;

    public BasicDiscoverableService(String name, String domain, String application, Protocol protocol, int port) {
        this.name = name;
        this.domain = domain;
        this.application = application;
        this.protocol = protocol;
        this.port = port;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public String getApplication() {
        return application;
    }

    @Override
    public int getPort() {
        return port;
    }

    public String getFQSN() {
        return String.format("%2$s.%1$s.%3$s.", protocol.toString(), application, domain);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void addProperty(String name, String value) {
        properties.put(name, value);
    }

    public static BasicDiscoverableService createInLocalNetwork(String applicationName, String serviceName, int port) {
        String domain = "local";
        BasicDiscoverableService service = new BasicDiscoverableService(applicationName, domain, serviceName, Protocol.TCP, port);
        return service;
    }

    @Override
    public String getName() {
        return name;
    }
}
