/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @created Dec 1, 2011
 * @author double-u
 */
public class Inliner implements Handler.InlineListener {

    protected Map<String, InlineContentProvider> icps;

    public Inliner() {
        icps = new HashMap<>();
    }

    public void addContentProvider(InlineContentProvider icp) {
        String name = null;
        if (icp.getClass().isAnnotationPresent(Name.class)) {
            name = icp.getClass().getAnnotation(Name.class).value();
        } else {
            name = icp.getClass().getSimpleName().toLowerCase();
        }
        this.icps.put(name, icp);
    }

    @Override
    public String inline(String key, Context context) throws InliningException {
        String[] parts = StringUtils.split(key, ',');
        if (parts.length < 1) {
            throw new InliningException("No inline expression was found");
        }

        String[] str = StringUtils.split(parts[0], '.');
        ArrayList<String> args = new ArrayList<>();
        Collections.addAll(args, Arrays.copyOfRange(parts, 1, parts.length));

        for (Map.Entry<String, InlineContentProvider> entry : icps.entrySet()) {
            if (entry.getKey().equals(parts[0])) {
                String res = entry.getValue().getContentFor(parts[0], context, args);
                if (res != null) {
                    return res;
                }
            }
        }

        return null;
    }
}
