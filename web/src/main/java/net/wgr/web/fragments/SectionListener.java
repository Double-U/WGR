/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import org.xml.sax.SAXException;

/**
 * 
 * @created Dec 5, 2011
 * @author double-u
 */
public interface SectionListener {
    public void section(String name, Handler handler) throws SAXException;
}
