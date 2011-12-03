/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.sax.HtmlParser;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @created Nov 29, 2011
 * @author double-u
 */
public class Parser {

    public void parseStreaming(InputStream is, OutputStream os, Context context) {
        // HTML5 can violate XML correctness. It shouldn't, but hey, shit happens.
        HtmlParser hp = new HtmlParser(XmlViolationPolicy.ALLOW);
        Handler h = new Handler(os);
        h.setContext(context);
        hp.setContentHandler(h);
        try {
            //hp.setProperty("http://xml.org/sax/properties/lexical-handler", h);
            hp.parse(new InputSource(is));
            h.end();
        } catch (IOException | SAXException ex) {
            Logger.getLogger(getClass()).error("HTML parsing failed", ex);
        }
    }
}
