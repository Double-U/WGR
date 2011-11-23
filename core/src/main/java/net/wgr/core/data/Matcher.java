/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.data;

import java.util.List;
import org.apache.cassandra.thrift.Column;

/**
 * 
 * @created Aug 27, 2011
 * @author double-u
 */
public interface Matcher<T> {

    /**
     * Match the same or extending type
     * @param <T> 
     * @param object
     * @return match 
     */
    public boolean match(T object);
    
    /**
     * Get an instance of given type from the data
     * @param <T> target type, which may extend Base type
     * @param columns data
     * @return T
     */
    public T buildInstance(List<Column> columns);
}
