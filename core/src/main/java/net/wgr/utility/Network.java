/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.utility;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @created Nov 5, 2011
 * @author double-u
 */
public class Network {

    public static InetAddress getHostAddressInSubnet(String address, String mask) {
        try {
            SubnetUtils utils = new SubnetUtils(address, mask);
            nics:
            for (NetworkInterface nic : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress addr : Collections.list(nic.getInetAddresses())) {
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        if (utils.getInfo().isInRange(addr.getHostAddress())) {
                            return addr;
                        }
                    }
                }
            }

        } catch (SocketException ex) {
            Logger.getLogger(Network.class).error("Failed to find corresponding interface", ex);
        }
        return null;
    }
}
