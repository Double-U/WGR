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
    protected Bytes lastKey = Bytes.NULL;
    protected ArrayList<Matcher<T>> matchers;
    protected ResultStrategy resultStrategy;
    protected MatchStrategy matchStrategy;
    protected Map<Bytes, T> results;
    protected boolean run = false;
    protected String columnFamily;
    protected long limit;

    public LazyQuery(String columnFamily, ResultStrategy strategy, MatchStrategy matchStrategy) {
        matchers = new ArrayList<>();
        results = new HashMap<>();
        this.columnFamily = columnFamily;
        this.resultStrategy = strategy;
        this.matchStrategy = matchStrategy;
    }
    
    public LazyQuery(String columnFamily, ResultStrategy strategy) {
        this(columnFamily, strategy, MatchStrategy.MATCH_ONE);
    }

    public void addMatcher(Matcher<T> m) {
        matchers.add(m);
    }

    @Override
    public void run() {
        run = true;
        long count = 0;

        while (run) {
            try {
                Map<Bytes, List<Column>> rows = null;
                if (lastKey.equals(Bytes.NULL)) {
                    rows = Retrieval.getRowsFromColumnFamily(columnFamily, pageSize);
                } else {
                    // Get new page
                    rows = Retrieval.getRowsFromColumnFamily(columnFamily, Selector.newKeyRange(lastKey, Bytes.EMPTY, pageSize));
                }
                if (rows == null || rows.isEmpty()) {
                    break;
                }

                if (rows.size() < pageSize) {
                    run = false;
                } else {
                    lastKey = (Bytes) new ArrayList<>(rows.keySet()).get(999);
                }

                mainIter:
                // Loop over every row
                for (Map.Entry<Bytes, List<Column>> entry : rows.entrySet()) {
                    // Check if any matcher gives a match
                    for (Matcher<T> m : matchers) {
                        T object = m.buildInstance(entry.getValue());
                        if (m.match(object)) {
                            results.put(entry.getKey(), object);
                            count++;

                            if (resultStrategy == ResultStrategy.FIND_ONE) {
                                // We only need to find one, break outer loop
                                // Also: using named for-loops :)
                                break mainIter;
                            } else if (resultStrategy == ResultStrategy.LIMIT && count >= limit) {
                                break mainIter;
                            } else if (matchStrategy == MatchStrategy.MATCH_ONE) {
                                // We only need one matcher to give a match
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

    public long getLimit() {
        return limit;
    }

    /**
     * Sets limit value and resultStrategy to LIMIT
     */
    public void setLimit(long limit) {
        this.resultStrategy = ResultStrategy.LIMIT;
        this.limit = limit;
    }

    public MatchStrategy getMatchStrategy() {
        return matchStrategy;
    }

    public ResultStrategy getResultStrategy() {
        return resultStrategy;
    }

    public enum ResultStrategy {

        FIND_ONE, FIND_ALL, LIMIT
    }
    
    public enum MatchStrategy {
        MATCH_ONE, MATCH_ALL
    }
}