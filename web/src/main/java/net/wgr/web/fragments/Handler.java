/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.wgr.web.fragments.client.RenderInstruction;
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
    protected SectionListener sl;
    protected Map<String, String> inlineData;
    protected Context context;
    protected boolean started = false;
    protected Action action = Action.NOTHING;
    protected String into;
    protected StringBuffer capture;
    protected LinkedList<Element> root;
    protected Element currentElement;
    protected final static String INLINE_ELEMENT = "inline", EVENT_ELEMENT = "event";
    public static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]*)}");

    public Handler() {
        this.inliners = new ArrayList<>();
        this.capture = new StringBuffer();
        this.sl = new SL();
        this.inlineData = new HashMap<>();
        this.root = new LinkedList<>();
    }

    public Handler(OutputStream os) {
        this();

        this.output = os;
        this.serializer = new HtmlSerializer(os);
    }

    protected static enum Action {

        WRITE_ON_END_ELEMENT, DEFER_INLINING, EXTRACT_INLINE_PARTS, NOTHING
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (action != Action.NOTHING) {
            capture.append(Arrays.copyOfRange(ch, start, start + length));
        } else {
            serializer.characters(ch, start, length);
        }
    }

    public void writeString(String str) throws SAXException {
        serializer.characters(str.toCharArray(), 0, str.length());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!started) {
            this.serializer.startDocument();
            started = true;
        }

        Element e = new Element(attributes, qName);
        if (currentElement == null) {
            currentElement = e;
        } else {
            currentElement.addChild(e);
        }

        root.push(e);

        if (qName.equals("fragment")) {
            return;
        }

        switch (qName) {
            case INLINE_ELEMENT:
            case "w:" + INLINE_ELEMENT:
                if (attributes.getLength() == 0 || (attributes.getValue("build") != null && attributes.getValue("build").equals("yes"))) {
                    action = Action.WRITE_ON_END_ELEMENT;
                } else if (attributes.getValue("default") != null) {
                    inlineData.putAll(Fragments.get().getPartsFromFragment(attributes.getValue("default"), output, context));
                }

                if (attributes.getValue("name") != null) {
                    Logger.getLogger(getClass()).info("Inlining " + attributes.getValue("name"));
                    context.parseSection(attributes.getValue("name"), this);
                } else if (attributes.getValue("into") != null) {
                    if (action != Action.EXTRACT_INLINE_PARTS) {
                        action = Action.DEFER_INLINING;
                    }
                    into = attributes.getValue("into");
                }
                break;
            case EVENT_ELEMENT:
                RenderInstruction ri = new RenderInstruction();
                ri.setWhat(currentElement.getName());
                ri.setWhen(attributes.getValue("event"));
                ri.setPlaceIn(attributes.getValue("target"));
                ri.setCall(attributes.getValue("call"));
                break;
            default:
                parseAttributes(attributes);
                serializer.startElement(uri, localName, qName, attributes);
                break;
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
        currentElement = root.pop();
        if (qName.equals("fragment")) {
            return;
        }

        if (qName.equals(INLINE_ELEMENT) || qName.equals("w:" + INLINE_ELEMENT)) {
            String result = build(capture.toString());
            if (action == Action.WRITE_ON_END_ELEMENT) {
                this.serializer.characters(result.toCharArray(), 0, result.length());
            } else if (action == Action.DEFER_INLINING || action == Action.EXTRACT_INLINE_PARTS) {
                this.inlineData.put(into, result);
            }
            capture.setLength(0);
            action = Action.NOTHING;
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
        if (context != null) {
            this.context.addSectionListener(sl);
        }
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

    public void doNotWriteDocumentStart() {
        started = true;
    }

    protected final class SL implements SectionListener {

        @Override
        public void section(String name, Handler handler) throws SAXException {
            if (inlineData.containsKey(name)) {
                String result = inlineData.get(name);
                Logger.getLogger(getClass()).info("Writing " + name + " : " + result);
                handler.writeString(result);
            }
        }
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Map<String, String> getFragmentParts() {
        return inlineData;
    }

    protected static class Element {

        protected Attributes attrs;
        protected String name;
        protected LinkedList<Element> children;

        public Element(Attributes attrs, String name) {
            this.attrs = attrs;
            this.name = name;
            this.children = new LinkedList<>();
        }

        public Attributes getAttributes() {
            return attrs;
        }

        public void setAttributes(Attributes attrs) {
            this.attrs = attrs;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void addChild(Element child) {
            this.children.push(child);
        }

        public List<Element> getChildren() {
            return children;
        }
    }

    public static interface InlineListener {

        public String inline(String key, Context context) throws InliningException;
    }
}
