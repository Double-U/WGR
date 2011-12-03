/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;

/**
 * 
 * @created Nov 30, 2011
 * @author double-u
 */
public class FragmentTest {
    public static void main(String[] args) throws FileNotFoundException {
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.DEBUG);
        root.addAppender(new ConsoleAppender(new TTCCLayout()));
        
        Context context = new Context();
        
        File f = new File("/Users/double-u/Projects/Stewie/web/index.html");
        Parser p = new Parser();
        p.parseStreaming(new FileInputStream(f), System.out, context);
    }
}
