/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.server.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author DoubleU
 */
public class ProtocolAgent {

    private static ArrayList<ProtocolHandler> handlers;
    private static Session session;
    private static boolean booted;
    private static ArrayList<Destination> destinations;
    private static HashMap<UUID, Destination> clients;

    public static void addProtocolHandler(ProtocolHandler ph) {
        if (!booted) {
            return;
        }
        if (handlers == null) {
            handlers = new ArrayList<>();
        }
        handlers.add(ph);
        int protocolID = handlers.indexOf(ph);

        if (destinations == null) {
            destinations = new ArrayList<>();
        }
        if (destinations.size() <= protocolID || destinations.get(protocolID) == null) {
            try {
                Destination d = session.createQueue(ph.getQueueName());
                destinations.add(d);
                session.createConsumer(d).setMessageListener(new MessageHandler());
            } catch (JMSException ex) {
                Logger.getLogger(ProtocolAgent.class.getName()).log(Level.ERROR, null, ex);
            }
        }
    }

    public static void boot() {
        if (booted) {
            return;
        }

        // TODO set to true when implemented SSL!
        ActiveMQ.useSSL(false);
        ActiveMQ.startBroker();
        session = ActiveMQ.getLocalMessaging().createNewSession(false, Session.CLIENT_ACKNOWLEDGE);
        //RemoteMessaging rm = new RemoteMessaging("tcp://localhost:61615");
        //session = rm.createNewSession(false, Session.CLIENT_ACKNOWLEDGE);
        try {
            Destination discover = session.createQueue("discover");
            Destination discoveries = session.createQueue("discoveries");
            session.createConsumer(discover).setMessageListener(new DiscoveryMessageHandler(discoveries));
        } catch (JMSException ex) {
            Logger.getLogger(ProtocolAgent.class.getName()).log(Level.ERROR, null, ex);
        }
        booted = true;
    }

    public static void sendMessage(String message, ProtocolHandler handler) {
        sendMessage(message, "default", handler);
    }

    public static void sendMessage(String message, String channel, ProtocolHandler handler) {
        int protocolID = handlers.indexOf(handler);

        if (destinations.get(protocolID) == null) {
            throw new Error("Protocol destination is not present");
        }

        Queue q = (Queue) clients.get(UUID.fromString(channel));
        try {
            System.out.println("Replying with " + message + " to " + q.getQueueName());
        } catch (JMSException ex) {
            Logger.getLogger(ProtocolAgent.class.getName()).log(Level.ERROR, null, ex);
        }

        try {
            TextMessage tm = session.createTextMessage(message);
            //tm.setStringProperty("Channel", channel);

            MessageProducer p = session.createProducer(clients.get(UUID.fromString(channel)));
            p.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            p.send(tm);
            System.out.println("Message successfully sent to " + p.getDestination().toString());
        } catch (JMSException ex) {
            Logger.getLogger(ProtocolAgent.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    private static class MessageHandler implements MessageListener {

        private static MessageHandler instance;

        private MessageHandler() {
        }

        public static MessageHandler getInstance() {
            if (instance == null) {
                instance = new MessageHandler();
            }
            return instance;
        }

        @Override
        public void onMessage(Message message) {
            try {
                if (!(message instanceof TextMessage)) {
                    return;
                }
                
                message.acknowledge();
                TextMessage txtMessage = (TextMessage) message;

                if (!message.propertyExists("ClientID")) {
                    return;
                }
                
                System.out.println("Message received: " + txtMessage.getText() + " from " + message.getStringProperty("ClientID"));

                if (!(message.getJMSDestination() instanceof Queue)) {
                    return;
                }
                Queue d = (Queue) message.getJMSDestination();
                String protocolName;
                UUID clientID;
                protocolName = d.getQueueName();
                clientID = UUID.fromString(message.getStringProperty("ClientID"));

                boolean protocolNameIsValid = false;
                for (ProtocolHandler ph : handlers) {
                    if (ph.getQueueName().equals(protocolName)) {
                        ph.handleMessageInChannel(txtMessage.getText(), clientID);
                        protocolNameIsValid = true;
                        break;
                    }
                }

                if (!protocolNameIsValid) {
                    System.out.println("Error in message");
                }

            } catch (JMSException e) {
                System.out.println("Caught:" + e);
                e.printStackTrace();
            }
        }
    }

    private static boolean ignoreMessage(String messageID) {
        boolean ignoreMessage = true;
        /*try {
        InetAddress addr = InetAddress.getLocalHost();

        // Get IP Address
        byte[] ipAddr = addr.getAddress();

        // Get hostname
        String hostname = addr.getHostName();
        if (messageID.contains(hostname)) {
        ignoreMessage = false;
        }
        } catch (UnknownHostException e) {
        }*/
        ignoreMessage = false;
        return ignoreMessage;
    }

    private static class DiscoveryMessageHandler implements MessageListener {

        private static Destination d;

        public DiscoveryMessageHandler(Destination d) {
            DiscoveryMessageHandler.d = d;
        }

        @Override
        public void onMessage(Message message) {
            try {
                if (!(message instanceof TextMessage)) {
                    return;
                }

                if (ignoreMessage(message.getJMSMessageID())) {
                    return;
                } else {
                    message.acknowledge();
                }

                // TODO Implement some timeout detection instead of this overflow killer
                if (clients != null && clients.size() > 100) clients.clear();

                TextMessage txtMessage = (TextMessage) message;
                String msgBody = txtMessage.getText();
                MessageProducer p = session.createProducer(d);
                TextMessage reply = session.createTextMessage();

                if (msgBody.substring(0, 8).equals("DISCOVER")) {
                    String needle = msgBody.substring(9);
                    boolean found = false;
                    for (ProtocolHandler h : handlers) {
                        System.out.println("Searching for: " + needle);
                        if (h.getClass().getSimpleName().equals(needle)) {
                            UUID clientId = UUID.randomUUID();
                            if (clients == null) {
                                clients = new HashMap<UUID, Destination>();
                            }
                            Destination de = session.createQueue(h.getQueueName() + "/" + clientId.toString());
                            //session.createConsumer(d).setMessageListener(MessageHandler.getInstance());
                            clients.put(clientId, de);
                            reply.setText("DISCOVERED " + needle + " AT\n" + h.getQueueName() + "/" + clientId.toString());
                            p.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                            System.out.println("Replied: DISCOVERED " + needle + " AT\n" + h.getQueueName() + "/" + clientId.toString());
                            p.send(reply);
                            found = true;
                        }
                    }

                    if (!found) {
                        reply.setText("NO SUCH PROTOCOL");
                        System.out.println("NO SUCH PROTOCOL");
                        p.send(reply);
                    }
                } else {
                    System.out.println("Malformed discovery message");
                }
            } catch (JMSException e) {
                System.out.println("Caught:" + e);
                e.printStackTrace();
            }
        }
    }
}
