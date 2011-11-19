/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Annotation must be kept and accessible in runtime
@Retention(RetentionPolicy.RUNTIME)

/**
 * Marks that this field should be not-null or empty
 * @created Jun 26, 2011
 * @author double-u
 */
public @interface Required {
    int maxLength() default -1;
    int minLength() default 0;
}
