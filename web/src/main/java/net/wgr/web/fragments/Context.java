/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.xml.sax.SAXException;

/**
 *
 * @created
 * Dec
 * 3,
 * 2011
 *
 * @author
 * double-u
 */
public class Context {

    protected Locale locale;
    protected List<SectionListener> listeners;

    public Context() {
        listeners = new ArrayList<>();
    }

    public void addSectionListener(SectionListener sl) {
        if (!listeners.contains(sl)) {
            listeners.add(sl);
        }
    }

    public void parseSection(String sectionName, Handler h) throws SAXException {
        for (SectionListener sl : listeners) {
            sl.section(sectionName, h);
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
