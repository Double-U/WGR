/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


// Annotation must be kept and accessible in runtime
@Retention(RetentionPolicy.RUNTIME)
/**
 * 
 * @created Dec 3, 2011
 * @author double-u
 */
public @interface Name {
    public String value() default "";
}
