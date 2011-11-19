/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wgr.server.messaging;

import java.util.UUID;

/**
 *
 * @author DoubleU
 */
public abstract class ProtocolHandler {
    public void handleMessage(String message) {
        handleMessageInChannel(message, UUID.randomUUID());
    }
    public abstract void handleMessageInChannel(String message, UUID client);
    public abstract String getQueueName();

    public void sendMessage(String message) {
        ProtocolAgent.sendMessage(message, this);
    }

    public void sendMessage(String message, String channel) {
        ProtocolAgent.sendMessage(message, channel, this);
    }
}
