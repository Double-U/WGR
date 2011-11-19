/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp;

import java.util.List;
import java.util.UUID;

/**
 * 
 * @created Jul 14, 2011
 * @author double-u
 */
public class Scope {
    public static enum Target {
        ALL, BY_ID
    }
    
    protected Target target;
    protected List<UUID> ids;

    public Scope(Target target) {
        this.target = target;
    }
    
    public Scope(List<UUID> ids) {
        target = Target.BY_ID;
        this.ids = ids;
    }

    public List<UUID> getIds() {
        return ids;
    }

    public Target getTarget() {
        return target;
    }
    
    
}
