/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.web.fragments.client;

/**
 * 
 * @created Dec 11, 2011
 * @author double-u
 */
public class RenderInstruction {
    protected String when, what;
    protected String call;
    protected String placeIn;

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getPlaceIn() {
        return placeIn;
    }

    public void setPlaceIn(String placeIn) {
        this.placeIn = placeIn;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }
}
