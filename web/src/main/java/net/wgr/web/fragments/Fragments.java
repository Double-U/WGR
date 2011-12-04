/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.wgr.core.ReflectionUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @created Nov 30, 2011
 * @author double-u
 */
public class Fragments {

    protected List<Class> targets;
    protected List<InlineContentProvider> instances;
    private static Fragments instance;

    private Fragments() {
        targets = new ArrayList<>();
        instances = new ArrayList<>();
    }

    public static Fragments get() {
        if (instance == null) {
            instance = new Fragments();
        }
        return instance;
    }

    public void loadFromPackage(String packageName) throws ClassNotFoundException {
        try {
            List<Class> classes = ReflectionUtils.getClasses(packageName);
            for (Class clazz : classes) {
                if (InlineContentProvider.class.isAssignableFrom(clazz)) classes.add(clazz);
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass()).error("Failed to load package", ex);
        }
    }
    
    public void addTarget(Class clazz) {
        targets.add(clazz);
    }
    
    public void addInstance(InlineContentProvider icp) {
        instances.add(icp);
    }

    public Inliner buildInliner() {
        Inliner in = new Inliner();
        for (Class c : targets) {
            if (InlineContentProvider.class.isAssignableFrom(c)) {
                try {
                    in.addContentProvider((InlineContentProvider) c.newInstance());
                } catch (IllegalAccessException | InstantiationException ex) {
                    Logger.getLogger(getClass()).error("Failed to instantiate InlineContentProvider " + c.getCanonicalName(), ex);
                }
            }
        }
        
        for (InlineContentProvider icp : instances ) {
            in.addContentProvider(icp);
        }
        return in;
    }
}
