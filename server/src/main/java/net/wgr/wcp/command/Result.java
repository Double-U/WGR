/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp.command;

/**
 * 
 * @created Nov 30, 2011
 * @author double-u
 */
public class Result {

    protected Object result;
    protected String tag, type;
    public static final String EXECUTION_FAILED = "command execution failed";
    public static final String NOT_AUTHORIZED = "I see what you did there";
    public static final String ERROR = "ERROR";
    public static final String RESULT = "RESULT";

    public Result(Object result, String tag, String type) {
        this.result = result;
        this.tag = tag;
        this.type = type;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    } 
}
