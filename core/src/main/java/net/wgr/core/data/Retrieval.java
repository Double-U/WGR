/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.core.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.scale7.cassandra.pelops.Bytes;
import org.scale7.cassandra.pelops.Selector;

/**
 *
 * @author DoubleU
 */
public class Retrieval {

    public static Map<Bytes, List<Column>> getAllRowsFromColumnFamily(String columnFamilyName) {
        return getRowsFromColumnFamily(columnFamilyName, 100000);
    }

    public static Map<Bytes, List<Column>> getRowsFromColumnFamily(String columnFamilyName, KeyRange selector) {
        if (!ColumnFamily.exists(columnFamilyName)) {
            return null;
        }

        Selector s = DataPool.getSelector(columnFamilyName);
        Map<Bytes, List<Column>> columnsFromRows = new LinkedHashMap<>();
        try {
            // This is something that I shouldn't be doing (and note the duplication of the whole data set, expensive to say the least)
            // Filtering for tombstones
            for (Map.Entry<Bytes, List<Column>> entry : s.getColumnsFromRows(columnFamilyName, selector, Selector.newColumnsPredicate("", "", false, 1000), ConsistencyLevel.ALL).entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    columnsFromRows.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Retrieval.class.getName()).log(Level.ERROR, "Row retrieval failed", ex);
        }
        return columnsFromRows;
    }

    public static Map<Bytes, List<Column>> getRowsFromColumnFamily(String columnFamilyName, int count) {
        return getRowsFromColumnFamily(columnFamilyName, Selector.newKeyRange("", "", count));
    }

    /**
     * Get row for key
     * @param key to find
     * @param cf columnfamily
     * @return row
     */
    public static List<Column> getRowForKey(Bytes key, String cf) {
        Selector s = DataPool.getSelector(cf);
        List<Column> columnsFromRow = s.getColumnsFromRow(cf, key, false, ConsistencyLevel.ALL);
        return columnsFromRow;
    }

    public static <T> T getObjectForKey(Bytes key, String cf, Class<T> type) {
        List<Column> row = getRowForKey(key, cf);
        if (!(net.wgr.core.dao.Object.class.isAssignableFrom(type))) {
            return null;
        }
        T obj = null;
        try {
            obj = type.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(Retrieval.class.getName()).log(Level.ERROR, null, ex);
        }

        net.wgr.core.dao.Object o = (net.wgr.core.dao.Object) obj;
        o.getFromColumns(row);

        return (T) o;
    }

    /**
     * Get rows as 
     * @param <T> type
     * @param type type name
     * @param rows rows to wrap
     * @return wrapped rows
     */
    public static <T> Map<Bytes, T> getRowsAs(Class<T> type, Map<Bytes, List<Column>> rows) {
        return getRowsAs(Bytes.class, type, rows);
    }

    /**
     * Get rows as 
     * H5 for generics in Java!
     * @param <K> keyType
     * @param <T> valueType
     * @param type type name
     * @param rows rows to wrap
     * @return wrapped rows
     */
    public static <K, T> Map<K, T> getRowsAs(Class<K> keyType, Class<T> valueType, Map<Bytes, List<Column>> rows) {
        if (rows == null) {
            return null;
        }
        Map<K, T> objects = new HashMap<>();
        for (Map.Entry<Bytes, List<Column>> e : rows.entrySet()) {
            if (!(net.wgr.core.dao.Object.class.isAssignableFrom(valueType))) {
                continue;
            }
            T obj = null;
            try {
                obj = valueType.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Retrieval.class.getName()).log(Level.ERROR, "Could not instantiate", ex);
            }

            net.wgr.core.dao.Object o = (net.wgr.core.dao.Object) obj;
            o.getFromColumns(e.getValue());

            K key = null;
            switch (keyType.getName()) {
                case "java.util.UUID":
                    key = (K) e.getKey().toUuid();
                    break;
                case "java.lang.String":
                    key = (K) e.getKey().toString();
                    break;
                default:
                    key = (K) e.getKey();
                    break;
            }

            objects.put(key, obj);
        }
        return objects;
    }
}
