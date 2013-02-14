/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.wcp.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import net.wgr.wcp.connectivity.Connection;

/**
 * 
 * @created Jul 7, 2011
 * @author double-u
 */
public class Command {

    protected String handler, name;
    protected String tag;
    protected transient JsonElement data;
    protected HttpServletRequest request;
    protected Connection conn;
    protected static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

    public static Command parse(String data, Connection c) {
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(data);
        if (je.isJsonObject()) {
            JsonObject jo = je.getAsJsonObject();
            if (!jo.has("handler") || !jo.has("name") || !jo.has("tag")) {
                throw new JsonParseException("Not a valid command object");
            }

            Command cmd = new Command(jo.get("handler").getAsString(), jo.get("name").getAsString(), jo.get("tag").getAsString(), jo.get("data") != null ? jo.get("data") : null, c);
            return cmd;
        } else {
            throw new JsonParseException("Not a valid JSON object");
        }
    }

    public Command(String handler, String name, Object data) {
        this.handler = handler;
        this.name = name;
        this.data = gson.toJsonTree(data);
        this.tag = UUID.randomUUID().toString();
    }

    public Command(String handler, String name, String tag, Object data) {
        this.handler = handler;
        this.name = name;
        this.data = gson.toJsonTree(data);
        this.tag = tag;
    }

    public Command(String handler, String name, String tag, JsonElement data, Connection c) {
        this.handler = handler;
        this.name = name;
        this.tag = tag;
        this.data = data;
        this.conn = c;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public JsonElement getData() {
        // Piggyback the system, if there is no data there still should be an empty JsonObject
        if (data == null) {
            data = new JsonObject();
        }
        return data;
    }

    public String getHandler() {
        return handler;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public Connection getConnection() {
        return conn;
    }

    /**
     * Gson serializes its own objects ><
     * @return a properly serialized Command
     */
    public String toJson() {
        String command = gson.toJson(this);
        String d = gson.toJson(data);
        // This is stupid
        String json = "{\"data\":" + d + "," + command.substring(1);
        return json;
    }
}
