/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.services.api;

import com.rosaloves.bitlyj.Bitly.Provider;
import net.wgr.settings.Settings;

/**
 * A bitly adapter for one of the most strange Java api's ever
 * @created Aug 30, 2011
 * @author double-u
 */
public class Bitly {

    private static Bitly instance;
    protected Provider bitly;

    public static Bitly getInstance() {
        if (instance == null) {
            instance = new Bitly();
        }
        return instance;
    }

    private Bitly() {
        Settings s = Settings.getInstance();
        bitly = com.rosaloves.bitlyj.Bitly.as(s.getString("Bitly.Username"), s.getString("Bitly.Key"));
    }

    public String shorten(String url) {
        // This is ridiculous
        return bitly.call(com.rosaloves.bitlyj.Bitly.shorten(url)).getShortUrl();
    }
}
