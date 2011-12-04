/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.lang;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import net.wgr.web.fragments.Context;
import net.wgr.web.fragments.InlineContentProvider;
import net.wgr.web.fragments.InliningException;
import net.wgr.web.fragments.Name;

/**
 * 
 * @created Dec 3, 2011
 * @author double-u
 */
@Name("loc")
public class I18N implements InlineContentProvider {
    
    protected Map<String, Properties> locales;
    
    public I18N() {
        locales = new HashMap<>();
    }
    
    public void loadLocaleFromStream(InputStream is, Locale locale) throws IOException {
        Properties prop = new Properties();
        prop.load(is);
        locales.put(locale.getLanguage(), prop);
    }
    
    @Override
    public String getContentFor(String key, Context context, List<String> args) throws InliningException {
        
        if (locales.containsKey(context.getLocale().getLanguage())) {
            return locales.get(context.getLocale().getLanguage()).getProperty(key);
        }
        
        return key;
    }
    
}
