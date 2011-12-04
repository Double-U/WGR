/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Arrays;
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
    protected boolean started = false, captureInput = false;
    protected StringBuffer capture;
    protected final static String INLINE_ELEMENT = "inline";
    public static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]*)}");

    public Handler() {
        this.inliners = new ArrayList<>();
        this.capture = new StringBuffer();
    }

    public Handler(OutputStream os) {
        this();

        this.output = os;
        this.serializer = new HtmlSerializer(os);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (captureInput) {
            capture.append(Arrays.copyOfRange(ch, start, start + length));
        } else {
            serializer.characters(ch, start, length);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!started) {
            this.serializer.startDocument();
            started = true;
        }

        if (localName.equals(INLINE_ELEMENT)) {
            captureInput = true;
        } else {
            System.out.println("Start E: " + qName);
            parseAttributes(attributes);
            serializer.startElement(uri, localName, qName, attributes);
        }
    }

    protected String inline(String expression) {
        String value = "";
        for (InlineListener il : inliners) {
            try {
                String str = il.inline(expression, context);
                if (str != null) {
                    value += str;
                }
            } catch (InliningException ex) {
                Logger.getLogger(getClass()).warn("Inlining failed", ex);
            }
        }
        return value;
    }

    protected String build(String data) {
        String newValue = data;
        Matcher matcher = PATTERN.matcher(data);

        int prevEnd = 0, start = 0;
        while (matcher.find()) {
            if (prevEnd == 0) {
                newValue = "";
            }

            String match = matcher.group(1);
            start = matcher.start();
            newValue += data.substring(prevEnd, start);

            Logger.getLogger(getClass()).info("Inline match: " + match);
            newValue += inline(match);
            prevEnd = matcher.end();
        }

        return newValue;
    }

    protected void parseAttributes(Attributes attr) {
        for (int i = 0; i < attr.getLength(); i++) {
            String newValue = build(attr.getValue(i));
            Logger.getLogger(getClass()).info("A: " + attr.getLocalName(i) + " = " + attr.getValue(i) + " => " + newValue);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals(INLINE_ELEMENT)) {
            captureInput = false;
            String result = build(capture.toString());
            this.serializer.characters(result.toCharArray(), 0, result.length());
            capture.setLength(0);
        } else {
            this.serializer.endElement(uri, localName, qName);
        }
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

    public OutputStream getOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
        this.serializer = new HtmlSerializer(output);
    }

    public void addInlineListener(InlineListener inl) {
        this.inliners.add(inl);
    }

    public static interface InlineListener {

        public String inline(String key, Context context) throws InliningException;
    }
}
