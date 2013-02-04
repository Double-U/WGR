/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.access;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;
import net.wgr.core.access.entity.Rule;
import net.wgr.core.access.entity.Rule.Action;
import net.wgr.core.data.Retrieval;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * @created Jul 24, 2011
 * @author double-u
 */
public class Authorize {

    protected static final Map<Long, Rule> rules = getRules();
    private static final UUID nullUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    protected static boolean enable = true;
    
    private Authorize() {
        
    }

    private static Map<Long, Rule> getRules() {
        return Retrieval.getRowsAs(Long.class, Rule.class, Retrieval.getAllRowsFromColumnFamily("rules"));
    }

    public static void disable() {
        Logger.getLogger(Authorize.class).warn("Authorization has been disabled!");
        enable = false;
    }

    public static boolean path(String path, AuthenticationProvider.Ticket ticket) {
        if (!enable) {
            return true;
        }
        UUID target = null;
        if (ticket == null) {
            target = nullUUID;
        } else {
            target = ticket.getUser().getId();
        }

        Action result = parse(target, path);
        return result == Rule.Action.ALLOW;
    }

    public static void reload() {
        rules.clear();
        rules.putAll(getRules());
    }

    protected static Rule.Action parse(UUID target, String path) {
        if (rules == null) {
            return Action.DENY;
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        // Strip out ./ and ../
        URI uri = null;
        try {
            uri = new URI(path);
            uri = uri.normalize();
        } catch (URISyntaxException ex) {
            Logger.getLogger(Authorize.class).log(Level.WARN, "Illegal path: " + path, ex);
            return Action.DENY;
        }

        path = uri.toString();
        String[] pp = path.split("/");
        Rule.Action result = Rule.Action.DENY;
        int matchingSections = 0;

        for (Rule r : rules.values()) {
            String[] rp = r.getPath().split("/");
            boolean match = true;
            for (int i = 0; i < Math.min(pp.length, rp.length); i++) {
                if (rp[i].equals(pp[i]) && match) {
                    match = true;
                } else {
                    if (i > matchingSections && rp[i].equals("*")) {
                        match = true;
                    } else {
                        match = false;
                        break;
                    }
                }
            }
            if (match) {
                if (r.getTarget().equals(target) || r.getTarget().equals(nullUUID)) {
                    int ms = Math.min(pp.length, rp.length);
                    if (ms > matchingSections) {
                        matchingSections = ms;
                        result = r.getAction();
                    }
                }
            }
        }

        return result;
    }
}
