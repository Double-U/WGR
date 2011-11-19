/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.data;

import java.util.List;
import org.apache.cassandra.thrift.Column;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * @created Aug 27, 2011
 * @author double-u
 */
public abstract class DAObjectMatcher<T extends net.wgr.core.dao.Object> implements Matcher {
    
    protected Class<T> clazz;
    
    public DAObjectMatcher(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean match(List<Column> columns) {
        try {
            T obj = clazz.newInstance();
            obj.getFromColumns(columns);
            return match(obj);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.ERROR, "Casting failed", ex);
        }
        
        return false;
    }
    
    public abstract boolean match(T object);
    
}
