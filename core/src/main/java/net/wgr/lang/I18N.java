/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.lang;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * 
 * @created Dec 3, 2011
 * @author double-u
 */
public class I18N {

    protected Map<String, Properties> locales;
    private static I18N instance;

    private I18N() {
        locales = new HashMap<>();
    }

    public static I18N get() {
        if (instance == null) {
            instance = new I18N();
        }
        return instance;
    }

    public void loadLocaleFromStream(InputStream is, Locale locale) throws IOException {
        Properties prop = new Properties();
        prop.load(is);
        locales.put(locale.getLanguage(), prop);
    }

    protected void loadLocale(Locale locale) {
        if (!locales.containsKey(locale.getLanguage())) {
            // Try to load
            InputStream is = getClass().getResourceAsStream("/lang_" + locale.getLanguage());
            if (is == null) {
                Logger.getLogger(getClass()).error("Failed to find localization file for language " + locale.getLanguage());
            } else {
                try {
                    loadLocaleFromStream(is, locale);
                } catch (IOException ex) {
                    Logger.getLogger(getClass()).error("Failed to load localization file for language " + locale.getLanguage(), ex);
                }
            }
        }
    }

    public String getStringForLocale(String key, Locale locale) {
        if (!locales.containsKey(locale.getLanguage()) || !locales.get(locale.getLanguage()).containsKey(key)) {
            return null;
        } else {
            return locales.get(locale.getLanguage()).getProperty(key);
        }
    }
}
