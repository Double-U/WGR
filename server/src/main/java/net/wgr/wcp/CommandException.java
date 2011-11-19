/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp;

/**
 * 
 * @created Oct 10, 2011
 * @author double-u
 */
public class CommandException {

    protected String commandName;
    protected String message;

    public CommandException(String message, String commandName) {
        this.commandName = commandName;
        this.message = message;
    }

    public CommandException(Throwable cause, String commandName) {
        cause.fillInStackTrace();
        this.message = cause.toString();
        this.commandName = commandName;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return "CommandException for " + commandName + " : " + this.getMessage();
    }
}
