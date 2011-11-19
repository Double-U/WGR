/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.server.application;

/**
 *
 * @author DoubleU
 */
public class PagePathImplementation {

    protected Type type;
    protected String queryStringKey;

    public PagePathImplementation(Type type) {
        this.type = type;
        queryStringKey = "";
    }

    public PagePathImplementation(Type type, String queryStringKey) {
        this.type = type;
        this.queryStringKey = queryStringKey;
    }

    public enum Type {

        STANDARD_FOLLOW, REWRITE_PATH_TO_QUERYSTRING, IGNORE_FOLDER
    }

    public Type getType() {
        return type;
    }

    public String getQueryStringKey() {
        if (queryStringKey.isEmpty()) {
            return "path";
        }
        return queryStringKey;
    }

    public String getRewrittenPath(String path) {
        String hoopajoo = "";
        int questionMarkIndex = path.indexOf('?');
        if (questionMarkIndex != -1) {
            hoopajoo = path.substring(0, questionMarkIndex);
        } else {
            hoopajoo = path;
        }

        if (hoopajoo.lastIndexOf('.') != -1) {
            return path;
        }

        String rp = "";
        switch (type) {
            case STANDARD_FOLLOW:
                rp = path;
                break;
            case IGNORE_FOLDER:
                rp = "/";
                break;
            case REWRITE_PATH_TO_QUERYSTRING:
                if (path.length() > 1) {
                    String r = path.replace('/', '.');
                    rp = "/?" + getQueryStringKey() + "=" + r.substring(1);
                } else {
                    rp = path;
                }
                break;
        }
        return rp;
    }
}
