/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments;

import java.util.ArrayList;
import java.util.List;
import net.wgr.web.fragments.client.RenderInstruction;

/**
 * 
 * @created Dec 11, 2011
 * @author double-u
 */
public class Fragment {
    protected List<RenderInstruction> instructions;

    public Fragment() {
        instructions = new ArrayList<>();
    }
    
    public void addRenderInstruction(RenderInstruction ri) {
        instructions.add(ri);
    }
    
    public List<RenderInstruction> getRenderInstructions() {
        return instructions;
    }
}
