/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wgr.core.data;

import java.util.List;
import java.util.Map;
import net.wgr.core.dao.StringHelper;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.scale7.cassandra.pelops.Bytes;
import org.scale7.cassandra.pelops.Selector;

/**
 *
 * @author DoubleU
 */
public class ConsoleTools {
    public static void writeColumnFamilyToConsole(String columnFamily) {
        Selector s = DataPool.getSelector(columnFamily);
        Map<Bytes, List<Column>> m = null;
        try {
            m = s.getColumnsFromRows(columnFamily, Selector.newKeyRange(Bytes.EMPTY, Bytes.EMPTY, 1000), Selector.newColumnsPredicate(Bytes.EMPTY, Bytes.EMPTY, false, 1000), ConsistencyLevel.ALL);
        } catch (Exception ex) {
            Logger.getLogger(ConsoleTools.class.getName()).log(Level.ERROR, null, ex);
        }

        for (Map.Entry<Bytes, List<Column>> e : m.entrySet()) {
            String out = "";
            out += e.getKey().toUTF8() + ":\n";
            for (Column c : e.getValue()) {
                out += "\t" + StringHelper.toUTF8(c.getName()) + " : " + StringHelper.toUTF8(c.getValue()).replace("\n", "\n\t") + "\n";
            }

            System.out.print(out);
        }
    }
}
