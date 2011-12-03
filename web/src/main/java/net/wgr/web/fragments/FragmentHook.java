/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import net.wgr.server.web.handling.WebHook;
import net.wgr.settings.Settings;
import org.apache.log4j.Logger;

/**
 * 
 * @created Dec 2, 2011
 * @author double-u
 */
public class FragmentHook extends WebHook {

    protected Parser parser;
    protected Context context;

    public FragmentHook() {
        super("*");
        parser = new Parser();
        context = new Context();
    }

    @Override
    public void handle(RequestBundle rb) throws IOException {
        URI uri = null;
        try {
            uri = new URI(rb.getRequestURI());
            uri = uri.normalize();
        } catch (URISyntaxException ex) {
            Logger.getLogger(getClass()).warn("Failed to parse request path", ex);
            rb.getBaseRequest().setHandled(false);
            return;
        }

        Settings s = Settings.getInstance();
        String fragmentPath = s.getString("WebContentPath") + '/' + s.getString("FragmentsPath") + '/' + (uri.getPath().isEmpty() || uri.getPath().equals("/") ? "index.html" : uri.getPath());
        File f = new File(fragmentPath);
        if (!fragmentPath.endsWith(".html") || !f.exists()) {
            rb.getBaseRequest().setHandled(false);
            return;
        }
        
        context.setLocale(rb.getRequest().getLocale());

        rb.sendResponseHeaders(200);
        parser.parseStreaming(new FileInputStream(fragmentPath), rb.getResponseBody(), context);
        rb.close();
    }
}
