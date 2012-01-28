/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.server.web.hooks;

import java.io.IOException;
import net.wgr.server.web.handling.WebHook;

/**
 * 
 * @created Jan 27, 2012
 * @author double-u
 */
public class SinglePageHook extends WebHook {
    
    protected String[] args;
    protected String page;

    public SinglePageHook(String page, String ... args) {
        super("*");
        
        this.page = page;
        this.args = args;
    }

    @Override
    public void handle(RequestBundle rb) throws IOException {
        rb.replyWithString(String.format(page, (Object[]) args));
    }
    
}
