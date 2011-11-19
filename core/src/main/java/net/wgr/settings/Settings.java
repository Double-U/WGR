/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.settings;

import java.util.HashMap;
import net.wgr.data.XMLTree;

/**
 *
 * @author DoubleU
 */
public class Settings extends XMLTree {

    private static Settings instance;
    private HashMap<String, String> deleted;

    public Settings(String fileName) {
        super(fileName);
        this.deleted = new HashMap<>();
    }

    public String getString(String key) {
        if (deleted.containsKey(key)) {
            throw new IllegalArgumentException("Used deleted key");
        }
        return super.getString(key);
    }

    public boolean enable(String key) {
        if (deleted.containsKey(key)) {
            throw new IllegalArgumentException("Used deleted key");
        }
        try {
            return (boolean) super.get(key);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void invalidate(String key) {
        deleted.put(key, getString(key));
        super.invalidate(key);
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings("settings.xml");
        }
        return instance;
    }
    
    public static void loadFromFile(String fileName) {
        instance = new Settings(fileName);
    }

    public boolean settingExists(String settingName) {
        return keyExists(settingName);
    }
}
