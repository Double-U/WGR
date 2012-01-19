/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.lang;

import java.util.List;
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

    @Override
    public String getContentFor(String key, Context context, List<String> args) throws InliningException {
        
        if (net.wgr.lang.I18N.instance().hasLocale(context.getLocale())) {
            return net.wgr.lang.I18N.instance().getTextForLocale(key, context.getLocale());
        }
        
        return key;
    }
    
}
