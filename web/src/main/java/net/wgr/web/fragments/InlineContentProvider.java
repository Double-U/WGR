/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.util.List;

/**
 * 
 * @created Dec 1, 2011
 * @author double-u
 */
public interface InlineContentProvider {
    public String getContentFor(String key, Context context, List<String> args) throws InliningException;
}
