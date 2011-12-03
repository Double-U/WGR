/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nu.validator.htmlparser.sax.HtmlSerializer;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @created Nov 30, 2011
 * @author double-u
 */
public class Handler extends DefaultHandler {

    protected OutputStream output;
    protected HtmlSerializer serializer;
    protected List<InlineListener> inliners;
    protected Context context;
    public static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]*)}");

    public Handler(OutputStream os) {
        this.output = os;
        this.serializer = new HtmlSerializer(os);
        this.inliners = new ArrayList<>();

        try {
            this.serializer.startDocument();
        } catch (SAXException ex) {
            Logger.getLogger(getClass()).error("Failed to start parsing document", ex);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        serializer.characters(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        System.out.println("Start E: " + qName);
        parseAttributes(attributes);
        serializer.startElement(uri, localName, qName, attributes);
    }

    protected void parseAttributes(Attributes attr) {
        for (int i = 0; i < attr.getLength(); i++) {
            String value = attr.getValue(i);
            String newValue = value;
            Matcher matcher = PATTERN.matcher(value);

            int prevEnd = 0, start = 0;
            while (matcher.find()) {
                if (prevEnd == 0) {
                    newValue = "";
                }

                String match = matcher.group(1);
                start = matcher.start();
                newValue += value.substring(prevEnd, start);

                Logger.getLogger(getClass()).debug("Inline match: " + match);

                for (InlineListener il : inliners) {
                    try {
                        String str = il.inline(match, context);
                        if (str != null) {
                            newValue += str;
                        }
                    } catch (InliningException ex) {
                        Logger.getLogger(getClass()).warn("Inlining failed", ex);
                    }
                }

                prevEnd = matcher.end();
            }

            Logger.getLogger(getClass()).debug("A: " + attr.getLocalName(i) + " = " + value + " => " + newValue);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.serializer.endElement(uri, localName, qName);
    }  
    
    public void end() throws SAXException {
        this.serializer.endDocument();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static interface InlineListener {

        public String inline(String key, Context context) throws InliningException;
    }
}
