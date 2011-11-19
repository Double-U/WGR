/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;


import net.wgr.rmi.Command;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * When a method is being called, it is subsequently called on all listeners in the collection
 * See Remote for a full blown remoting implementation (07/2010)
 * @created Jul 3, 2011
 * @author double-u
 */
public class ElementsByProxyList<E> extends ArrayList<E> implements InvocationHandler {

    protected Class<E> clazz;

    /**
     * TODO: determine clazz from type parameter
     * @param type 
     */
    public void enable(Class<E> type) {
        clazz = type;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] os) throws Throwable {
        Command cmd = new Command(o, method.getDeclaringClass(), method.getName(), method.getParameterTypes(), os);
        Object result = executeCommand(cmd);
        return result;
    }

    protected Object executeCommand(Command cmd) {
        // Check if command is complete
        if (cmd.getProxy() == null || cmd.getMethodName() == null) {
            throw new IllegalArgumentException("Proxy a/o Method are not supplied");
        }

        ArrayList<Object> combinedResult = null;
        for (E t : this) {
            try {
                Method m = cmd.getTarget().getMethod(cmd.getMethodName(), cmd.getParameterTypes());
                // Magic
                Object res = m.invoke(t, cmd.getArgs());
                if (res instanceof List) {
                    if (combinedResult == null) {
                        combinedResult = new ArrayList<>();
                    }
                    combinedResult.addAll((List) res);
                }
            } catch (Exception ex) {
                Logger.getLogger(ElementsByProxyList.class.getName()).log(Level.ERROR, "Invocation target exception on method:" + cmd.getMethodName(), ex);
            }
        }

        return combinedResult;
    }

    public E getProxy() {
        // This is funky
        if (clazz == null) {
            try {
                throw new InstantiationException("Sorry old chap, but you still have to enable it");
            } catch (InstantiationException ex) {
                Logger.getLogger(ElementsByProxyList.class.getName()).log(Level.ERROR, "List is not enabled", ex);
            }
        }
        Class[] interfacesArray = new Class[]{clazz};
        return (E) Proxy.newProxyInstance(clazz.getClassLoader(), interfacesArray, this);
    }
}
