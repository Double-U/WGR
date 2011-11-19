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
import org.apache.cassandra.thrift.Column;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.scale7.cassandra.pelops.Bytes;
import org.scale7.cassandra.pelops.Selector;

/**
 * Performs a query against a ColumnTable. Lazily.
 * @created Aug 3, 2011
 * @author double-u
 */
public class LazyQuery implements Runnable {

    protected int pageSize = 1000;
    protected String oldKey = "";
    protected ArrayList<Matcher> matchers;
    protected Strategy strategy;
    protected Map<Bytes, List<Column>> results;
    protected boolean run = false;
    protected String columnFamily;

    public LazyQuery(String columnFamily, Strategy strategy) {
        matchers = new ArrayList<>();
        results = new HashMap<>();
        this.columnFamily = columnFamily;
        this.strategy = strategy;
    }

    public void addMatcher(Matcher m) {
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

                for (Map.Entry<Bytes, List<Column>> entry : rows.entrySet()) {
                    boolean matches = false;
                    for (Matcher m : matchers) {
                        if (m.match(entry.getValue())) {
                            matches = true;
                        }
                    }
                    if (matches) {
                        results.put(entry.getKey(), entry.getValue());
                        if (strategy == Strategy.FIND_ONE) {
                            break;
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

    public Map<Bytes, List<Column>> getResults() {
        return results;
    }

    public <T extends net.wgr.core.dao.Object> T getResultAs(Class<T> clazz) {
        try {
            if (results.isEmpty()) {
                return null;
            }
            if (results.size() > 1) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARN, "Only returning one result while multiple present");
            }
            T obj = clazz.newInstance();
            obj.getFromColumns(results.entrySet().iterator().next().getValue());
            return obj;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.ERROR, "Casting result failed");
        }
        return null;
    }

    public enum Strategy {

        FIND_ONE, FIND_ALL
    }
}
