/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.cassandra.thrift.Column;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.scale7.cassandra.pelops.Bytes;
import org.scale7.cassandra.pelops.Selector;

/**
 * Performs a query against a ColumnTable with an Object based predicate. Lazily.
 * @created Aug 3, 2011
 * @author double-u
 */
public class LazyQuery<T> implements Runnable {

    protected int pageSize = 1000;
    protected String oldKey = "";
    protected ArrayList<Matcher<T>> matchers;
    protected Strategy strategy;
    protected Map<Bytes, T> results;
    protected boolean run = false;
    protected String columnFamily;

    public LazyQuery(String columnFamily, Strategy strategy) {
        matchers = new ArrayList<>();
        results = new HashMap<>();
        this.columnFamily = columnFamily;
        this.strategy = strategy;
    }

    public void addMatcher(Matcher<T> m) {
        matchers.add(m);
    }

    @Override
    public void run() {
        run = true;

        while (run) {
            try {
                Map<Bytes, List<Column>> rows = null;
                if (oldKey.equals("")) {
                    rows = Retrieval.getRowsFromColumnFamily(columnFamily, pageSize);
                } else {
                    // We're just loading every column, even if we only needed one. Because we can. And we're lazy.
                    rows = Retrieval.getRowsFromColumnFamily(columnFamily, Selector.newKeyRingRange(oldKey, "", pageSize));
                }
                if (rows == null || rows.isEmpty()) {
                    break;
                }

                if (rows.size() < pageSize) {
                    run = false;
                }

                mainIter:
                // Loop over every row
                for (Map.Entry<Bytes, List<Column>> entry : rows.entrySet()) {
                    // Check if any matcher gives a match
                    for (Matcher<T> m : matchers) {
                        T object = m.buildInstance(entry.getValue());
                        if (m.match(object)) {
                            results.put(entry.getKey(), object);
                            if (strategy == Strategy.FIND_ONE) {
                                // We only need to find one, break outer loop
                                // Also: using named for-loops :)
                                break mainIter;
                            } else {
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.ERROR, "Lazy Query execution failed", ex);
            }
        }
    }

    public void stop() {
        run = false;
    }

    public Map<Bytes, T> getResults() {
        return results;
    }

    public Entry<Bytes, T> getResult() {
        if (results.size() < 1) {
            return null;
        }
        if (results.size() > 1) {
            Logger.getLogger(getClass()).warn("Only returning one object when multiple are available");
        }
        return results.entrySet().iterator().next();
    }

    public enum Strategy {

        FIND_ONE, FIND_ALL
    }
}
