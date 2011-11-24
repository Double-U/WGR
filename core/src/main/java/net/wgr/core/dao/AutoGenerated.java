/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.core.dao;

// Annotation must be kept and accessible in runtime

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

/**
 * 
 * @created Aug 3, 2011
 * @author double-u
 */
public @interface AutoGenerated {
    GenerationStrategy strategy() default GenerationStrategy.RANDOM;
    
    public enum GenerationStrategy {
        RANDOM, SEQUENTIAL
    }
}