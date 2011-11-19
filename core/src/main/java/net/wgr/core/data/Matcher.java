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
public interface Matcher {

    public boolean match(List<Column> columns);
}
