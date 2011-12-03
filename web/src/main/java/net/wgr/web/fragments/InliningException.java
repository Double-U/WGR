/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

/**
 * 
 * @created Dec 1, 2011
 * @author double-u
 */
public class InliningException extends Exception {
    
    protected String message;

    public InliningException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
