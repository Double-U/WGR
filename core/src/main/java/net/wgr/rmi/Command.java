/*
 * Copyright 2011 Wannes De Smet 
 * All rights reserved
 */
package net.wgr.rmi;

import java.io.Serializable;

/**
 * Remote Invocation data
 * @author  DoubleU
 */
public class Command implements Serializable {

    private static final long serialVersionUID = -6041113159658760890L;
    /**
	 * @uml.property  name="timestamp"
	 */
    private long timestamp;
    /**
	 * @uml.property  name="args"
	 */
    private Object[] args;
    /**
	 * @uml.property  name="proxy"
	 */
    private Object proxy;
    /**
	 * @uml.property  name="methodName"
	 */
    private String methodName;
    /**
	 * @uml.property  name="parameterTypes"
	 */
    private Class[] parameterTypes;
    /**
	 * @uml.property  name="target"
	 */
    private Class target;

    public Command(Object proxy, Class target, String methodName, Class[] parameterTypes, Object[] args) {
        this.args = args.clone();
        this.proxy = proxy;
        this.target = target;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes.clone();
        this.timestamp = System.currentTimeMillis() / 1000L;
    }

    /**
	 * @return
	 * @uml.property  name="args"
	 */
    public Object[] getArgs() {
        return args;
    }

    /**
	 * @return
	 * @uml.property  name="proxy"
	 */
    public Object getProxy() {
        return proxy;
    }

    /**
	 * @return
	 * @uml.property  name="methodName"
	 */
    public String getMethodName() {
        return methodName;
    }

    /**
	 * @return
	 * @uml.property  name="parameterTypes"
	 */
    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    /**
	 * @return
	 * @uml.property  name="timestamp"
	 */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
	 * @return
	 * @uml.property  name="target"
	 */
    public Class getTarget() {
        return target;
    }
}
