/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.lang;

import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    
    protected Map<Locale, Map<String, String>> locales;
    
    @Override
    public String getContentFor(String key, Context context, List<String> args) throws InliningException {
        if (locales.containsKey(context.getLocale())) {
            return locales.get(context.getLocale()).get(key);
        }
        
        return key;
    }
    
}
