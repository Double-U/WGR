/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.core.data;

import org.apache.cassandra.thrift.CfDef;
import org.scale7.cassandra.pelops.Cluster;
import org.scale7.cassandra.pelops.ColumnFamilyManager;
import org.scale7.cassandra.pelops.KeyspaceManager;
import org.scale7.cassandra.pelops.Mutator;
import org.scale7.cassandra.pelops.Pelops;
import org.scale7.cassandra.pelops.RowDeletor;
import org.scale7.cassandra.pelops.Selector;

/**
 *
 * @author DoubleU
 */
public class DataPool {

    private static boolean booted = false;
    private static String poolName, keyspace;
    private static Cluster cluster;

    public static void simpleBoot(String poolName, String host, String keyspace) {
        //Pelops.addPool("Main", new String[] { "localhost"},  9160, new Policy());
        DataPool.poolName = poolName;
        DataPool.keyspace = keyspace;
        cluster = new Cluster(host, 9160);
        Pelops.addPool(poolName, cluster, keyspace);
        booted = true;
    }

    public static Selector getSelector(String columnFamily) {
        if (!booted) {
            throw new RuntimeException("DataPool is not booted");
        }
        Selector s = Pelops.createSelector(poolName);
        return s;
    }

    /**
     * Timestamps must be used or tests will fail!
     *
     * http://comments.gmane.org/gmane.comp.db.cassandra.user/9327
     * Insert after Delete fails silently
    If you delete a row, and it therefore is marked as tombstone, and
    subsequently you try to insert the row again it appears to succeed,
    but if you try to request the row you don't get a result.
    
    If you try to insert a row that has been recently deleted, and has an
    active tombstone I would expect either the tombstone marker to be
    removed, or the insert to fail. It seems to currently accept the
    insert and then subsequently the row can't be found.
    
    Re: Insert after Delete fails silently
    
    Are you re-using the timestamp from the first insert on the second insert?
    
    The insert must occur after the tombstone, otherwise cassandra will assume the tombstone is the current
    version and ignore the delete.
    
    Aaron
     *
     */
    public static Mutator getMutator() {
        if (!booted) {
            throw new RuntimeException("DataPool is not booted");
        }
        return Pelops.createMutator(poolName, System.currentTimeMillis());
    }

    public static RowDeletor getRowDeletor() {
        if (!booted) {
            throw new RuntimeException("DataPool is not booted");
        }
        return Pelops.createRowDeletor(poolName, System.currentTimeMillis());
    }

    public static ColumnFamilyManager getColumnFamilymanager() {
        return new ColumnFamilyManager(cluster, keyspace);
    }

    public static KeyspaceManager getKeySpaceManager() {
        return new KeyspaceManager(cluster);
    }

    public static boolean keySpaceExists(String keySpaceName) throws Exception {
        boolean found = false;
        for (CfDef cfd : getKeySpaceManager().getKeyspaceSchema(keyspace).getCf_defs()) {
            if (cfd.getName().equals(keySpaceName)) {
                found = true;
            }
        }

        return found;
    }

    public static String getKeyspace() {
        return keyspace;
    }

    public static void stop() {
        Pelops.shutdown();
    }
}
