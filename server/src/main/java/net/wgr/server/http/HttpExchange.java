/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.server.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;

/**
 * Mimicking the one truly good part of the Sun HttpServer: everything necessary consolidated into HttpExchange
 * @created Jun 26, 2011
 * @author double-u
 */
public class HttpExchange {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Request baseRequest;

    public HttpExchange(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpExchange(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getRequestMethod() {
        return request.getMethod();
    }

    public String getRequestHeader(String key) {
        return request.getHeader(key);
    }

    public void setResponseHeader(String key, String value) {
        response.setHeader(key, value);
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public void sendResponseHeaders(int responseCode) {
        response.setStatus(responseCode);
        //request.
    }

    public InputStream getRequestBody() throws IOException {
        return request.getInputStream();
    }

    public OutputStream getResponseBody() throws IOException {
        return response.getOutputStream();
    }

    public void close() throws IOException {
        try {
            response.getOutputStream().close();
        } catch (EOFException ex) {
            // Denotes the stream is already closed -- so just be happy and move on
        }
    }

    public void setContentType(String type) {
        response.setContentType(type);
    }

    public Request getBaseRequest() {
        return Request.getRequest(request);
    }
}
