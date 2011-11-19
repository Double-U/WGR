/*
 * Copyright 2011 Wannes De Smet 
 * All rights reserved
 */
package net.wgr.rmi;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Remote Method Invocation. My way.
 * 03/2011
 * @author  DoubleU
 */
public final class Remote implements Serializable {

    private transient ObjectInputStream ois;
    private transient ObjectOutputStream oos;
    private transient Caller caller;
    private final transient Receiver receiver;
    private UUID session;

    public Remote() {
        caller = new Caller();
        receiver = new Receiver();
    }

    public void boot(Socket s){
        try {
            this.boot(s.getInputStream(), s.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Remote.class.getName()).log(Level.ERROR, "Receiver startup failed", ex);
        }
    }

    public void boot(InputStream input, OutputStream output) {
        try {
            BufferedInputStream bis = new BufferedInputStream(input);
            oos = new ObjectOutputStream(output);
            ois = new ObjectInputStream(bis);

            session = UUID.randomUUID();
            receiver.start();
        } catch (IOException ex) {
            Logger.getLogger(Remote.class.getName()).log(Level.ERROR, "Receiver startup failed", ex);
        }
    }

    public void halt() {
        try {
            receiver.stop();
            oos.close();
            ois.close();
        } catch (IOException ex) {
            Logger.getLogger(Remote.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public Caller getCaller() {
        return caller;
    }

    public final class Caller implements Serializable, InvocationHandler {

        public Caller() {
            super();
        }

        /**
         * Generic Proxy generator
         * AKA: Awesomeness
         * @param <T> InvocationTarget type
         * @param clazz target class
         * @return proxy class for target
         */
        public <T extends InvocationTarget> T getProxyForType(Class<T> clazz) {
            Class[] interfacesArray = new Class[]{clazz};
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfacesArray, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Command cmd = new Command(proxy, method.getDeclaringClass(), method.getName(), method.getParameterTypes(), args);
            sendCommand(cmd);
            Object obj = getResult(method.getReturnType());

            return obj;
        }

        private void sendCommand(Command cmd) {
            try {
                oos.writeObject(cmd);
                oos.flush();
            } catch (IOException ex) {
                Logger.getLogger(Caller.class.getName()).log(Level.ERROR, "Communication error ocurred", ex);
            }
        }

        public Object getResult(Class returnType) throws Throwable {
            Object result = null;
            boolean validResult = false;
            synchronized (receiver.getThread()) {
                while (!validResult) {
                    try {
                        // If we wait just long enough, we'll maybe get an answer
                        receiver.getThread().wait();
                        result = receiver.getResult();

                        // Check if everything is A-OK
                        if (!returnType.isAssignableFrom(receiver.getResult().getClass()) && !returnType.getCanonicalName().equals("void")) {
                            if (receiver.getResult() instanceof Exception) {
                                // Jus' relax. An error occurred. Let's rethrow the real exception for handling
                                throw ((InvocationTargetException) receiver.getResult()).getTargetException();
                            } else {
                                throw new Exception("Wrong return type, got " + receiver.getResult().getClass().getCanonicalName() + " instead of " + returnType.getCanonicalName());
                            }
                        } else {
                            validResult = true;
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Remote.class.getName()).log(Level.ERROR, "Interrupted while waiting for a result", ex);
                    }
                }

            }
            if (result instanceof NullResponse) {
                result = null;
            }
            if (result instanceof Throwable) {
                throw (Throwable) result;
            }

            return result;
        }
    }

    /**
     * @author   double-u
     */
    public final class Receiver implements Runnable {

        private transient ArrayList<InvocationTarget> targets;
        private boolean run;
        private transient final Thread thread;
        private transient ThreadGroup tg;
        private transient Object result;

        public Receiver() {
            this.targets = new ArrayList<>();
            thread = new Thread(this);

        }

        public void addInvocationTarget(InvocationTarget it) {
            targets.add(it);
        }

        public Object getResult() {
            return result;
        }

        @Override
        public void run() {
            thread.setName("Remote for: " + getSession().toString());
            tg = new ThreadGroup(getSession().toString());
            run = true;

            while (run) {
                try {
                    // Received something!
                    Object obj = ois.readObject();
                    if (obj == null || !(obj instanceof Command)) {
                        // Noise or return value
                        synchronized (thread) {
                            result = obj;
                            thread.notify();
                        }
                    } else {
                        // Execute Command
                        final Command c = (Command) obj;
                        Thread t = new Thread(tg, new Runnable() {

                            @Override
                            public void run() {
                                handleCommand(c);
                            }
                        });
                        t.setName("Invocator: " + c.getMethodName());
                        t.start();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Receiver.class.getName()).log(Level.ERROR, "Remote communication error", ex);
                    stop();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Receiver.class.getName()).log(Level.ERROR, "Remote client tried to call unkown class", ex);
                }
            }
        }

        public Thread getThread() {
            return thread;
        }

        public void start() {
            thread.start();
        }

        public void stop() {
            run = false;
        }

        private void sendResult(Object result) throws IOException {
            oos.writeObject(result);
            //System.out.println("Sent: " + result.getClass().getCanonicalName());
            oos.flush();
            //oos.reset();
        }

        private boolean handleCommand(Command cmd) {
            boolean handled = false;

            // Check if command is received within 2 seconds; we don't want to be late for the party
            long curTime = System.currentTimeMillis() / 2000L;
            if (curTime - cmd.getTimestamp() > 5) {
                throw new IllegalArgumentException("Command TTL exceeded");
            }

            // Check if command is complete
            if (cmd.getProxy() == null || cmd.getMethodName() == null) {
                throw new IllegalArgumentException("Proxy a/o Method are not supplied");
            }

            for (InvocationTarget t : targets) {
                // Command matches InvocationTarget
                if (cmd.getTarget().isInstance(t)) {
                    try {
                        Method m = cmd.getTarget().getMethod(cmd.getMethodName(), cmd.getParameterTypes());
                        // Magic
                        Object res = m.invoke(t, cmd.getArgs());
                        if (res == null) {
                            res = new NullResponse();
                        }
                        //System.out.println("Executed " + cmd.getMethodName() + " : " + res.toString());
                        sendResult(res);

                        handled = true;
                    } catch (Throwable ex) {
                        System.out.println("Invocation target exception on method:" + cmd.getMethodName());
                        try {
                            sendResult(ex);
                        } catch (IOException ex1) {
                            Logger.getLogger(Remote.class.getName()).log(Level.ERROR, "Sending exception failed", ex1);
                        }
                    }
                }
            }

            return handled;
        }
    }

    public UUID getSession() {
        return session;
    }
}
