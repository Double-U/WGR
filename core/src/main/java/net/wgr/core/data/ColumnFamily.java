/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.data;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.wgr.core.ReflectionUtils;
import net.wgr.core.dao.TimeUUID;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnDef;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.log4j.Logger;
import org.scale7.cassandra.pelops.Bytes;
import org.scale7.cassandra.pelops.Selector;

/**
 * 
 * @created Jul 8, 2011
 * @author double-u
 */
public class ColumnFamily {

    public static void createFromObject(net.wgr.core.dao.Object obj) throws Exception {
        CfDef cfd = new CfDef();
        cfd.setKeyspace(DataPool.getKeyspace());
        cfd.setName(obj.getColumnFamily());
        cfd.setDefault_validation_class("UTF8Type");
        for (Field f : ReflectionUtils.getAllFields(obj.getClass())) {
            String type = "";
            if (f.getType().isAssignableFrom(UUID.class)) {
                if (f.isAnnotationPresent(TimeUUID.class)) {
                    type = "TimeUUIDType";
                } else {
                    type = "LexicalUUIDType";
                }
            } else if (Map.class.isAssignableFrom(f.getType())) {
                type = "Super";
            } else if (f.getType().isAssignableFrom(String.class)) {
                type = "UTF8Type";
            } else if (f.getType() == long.class) {
                type = "LongType";
            } else {
                type = "BytesType";
            }

            if (f.getName().equals(obj.getKeyFieldName())) {
                cfd.setKey_validation_class(type);
            }

            ColumnDef def = new ColumnDef(ByteBuffer.wrap(f.getName().getBytes("UTF-8")), type);
            cfd.addToColumn_metadata(def);
        }
        DataPool.getColumnFamilymanager().addColumnFamily(cfd);
    }

    public static boolean exists(String name) {
        boolean found = false;
        try {
            List<CfDef> columnFamilies = DataPool.getKeySpaceManager().getKeyspaceSchema(DataPool.getKeyspace()).getCf_defs();
            for (CfDef cfd : columnFamilies) {
                if (cfd.getName().equals(name)) {
                    found = true;
                }
            }
            return found;
        } catch (Exception ex) {
            // This is not how it should be, but the caller should not have to care whether the connection works properly when he just wants to know is a CF exists.
            Logger.getLogger(ColumnFamily.class).debug("Error in check for ColumnFamily: " + name, ex);
            return false;
        }
    }

    public static long getKeyCount(String columnFamily) {
        Selector s = DataPool.getSelector(columnFamily);
        LinkedHashMap<Bytes, List<Column>> map = s.getColumnsFromRows(columnFamily, Selector.newKeyRange("", "", 10000), false, ConsistencyLevel.ALL);
        return map.size();
    }
}
