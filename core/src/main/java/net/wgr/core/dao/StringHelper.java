/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.core.dao;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author DoubleU
 */
public class StringHelper {

    public static String toUTF8(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static byte[] toBytes(String string) {
        try {
            return string.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
