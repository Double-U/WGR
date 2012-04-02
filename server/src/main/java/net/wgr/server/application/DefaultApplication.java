/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.server.application;

import com.github.rjeschke.txtmark.Processor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import net.wgr.core.access.Authorize;
import net.wgr.server.http.HttpExchange;
import net.wgr.server.session.Session;
import net.wgr.server.session.Sessions;
import net.wgr.settings.Settings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpFields;

/**
 *
 * @author DoubleU
 */
public class DefaultApplication implements Application {

    private String path, root;
    private boolean searchForIndex = true;
    private PagePathImplementation ppi;

    @Override
    public String getContext() {
        return path;
    }

    @Override
    public String getRootFolder() {
        return root;
    }

    public PagePathImplementation getPagePathImplementation() {
        if (ppi == null) {
            ppi = new PagePathImplementation(PagePathImplementation.Type.STANDARD_FOLLOW);
        }
        return ppi;
    }

    public void setPagePathImplementation(PagePathImplementation ppi) {
        this.ppi = ppi;
    }

    public boolean isSearchForIndex() {
        return searchForIndex;
    }

    public void setSearchForIndex(boolean searchForIndex) {
        this.searchForIndex = searchForIndex;
    }

    private String reconsiderType(String type, File f) {
        if (!type.contains("text")) {
            return type;
        }

        String fileName = f.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        switch (ext) {
            case "html":
            case "htm":
                type = "text/html";
                break;
            case "css":
                type = "text/css";
                break;
            case "js":
                type = "text/javascript";
                break;
            case "md":
                type = "text/markdown";
                break;
            default:
                type = "application/octet-stream";
                break;
        }

        return type;
    }

    private byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
            throw new Error("File is too large: " + file.getName() + ":" + length + ", max allowed: " + Integer.MAX_VALUE);
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    private DefaultApplication(String path, String rootFolder) {
        this.path = path;
        this.root = rootFolder;
        
        // End extraneous logging
        Logger.getLogger("net.sf.jmimemagic").setLevel(Level.ERROR);
    }

    public static DefaultApplication create(String path, String rootFolder) throws IOException {
        File root = new File(rootFolder);
        if (!root.exists()) {
            throw new IOException("Root folder does not exist");
        }
        DefaultApplication app = new DefaultApplication(path, rootFolder);
        return app;
    }

    protected byte[] tryToDeflate(HttpExchange he, String type, byte[] file) {
        if (he.getRequestHeader("Accept-Encoding") == null || he.getRequestHeader("User-Agent") == null) {
            return file;
        }

        // Deflate for maximum awesomeness
        // Chrome and IE have some bugs ><
        if (he.getRequestHeader("Accept-Encoding").toString().contains("deflate") && (type.contains("text") || type.contains("application"))
                && !he.getRequestHeader("User-Agent").toString().contains("Chrome")
                && !he.getRequestHeader("User-Agent").toString().contains("MSIE")) {
            Deflater deflater = new Deflater(Deflater.DEFLATED, false);

            byte[] deflated = new byte[file.length];
            deflater.setInput(file);
            deflater.finish();
            file = new byte[deflater.deflate(deflated)];
            file = deflated;

            he.setResponseHeader("Content-Encoding", "deflate");

            //DeflaterOutputStream dos = new DeflaterOutputStream(he.getResponseBody());
            //dos.write(file);

            return deflated;
        } else {
            return file;
        }
    }

    protected void setCaching(HttpExchange he, String type, File f) {
        if (Settings.getInstance().enable("Caching.Enabled")) {
            // We're cool, so cache publically by default
            he.setResponseHeader("Last-Modified", HttpFields.formatDate(f.lastModified()));

            if (type.startsWith("text/") || Settings.getInstance().enable("Caching.MandatoryRecheck")) {
                he.setResponseHeader("Cache-Control", "public, max-age=0");
            } else {
                // Content files do not need to be rechecked every time
                he.setResponseHeader("Cache-Control", "public, max-age=86400");
            }
        }
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        String requestMethod = he.getRequestMethod();

        if (requestMethod.equalsIgnoreCase("GET")) {
            try (OutputStream responseBody = he.getResponseBody()) {
                String requestURI = "";
                if (ppi != null) {
                    requestURI = ppi.getRewrittenPath(he.getRequestURI());
                } else {
                    requestURI = he.getRequestURI();
                }
                
                if (requestURI.equals("/") && searchForIndex) {
                    requestURI += "index.html";
                }
                String filePath = getRootFolder() + requestURI;

                // Strip out ../ and ./
                Path normalized = Paths.get(filePath);
                normalized = normalized.normalize();
                path = normalized.toString();

                File f = new File(filePath);
                if (!f.exists() || f.isDirectory()) {
                    Logger.getLogger(getClass()).log(Level.INFO, "404 " + filePath);
                    he.sendResponseHeaders(404);
                    he.close();
                    return;
                }

                Session session = Sessions.getInstance().getSession(he.getBaseRequest().getSession().getId());
                boolean authorized = false;
                if (session != null) {
                    authorized = Authorize.path(requestURI, session.getTicket());
                } else {
                    authorized = Authorize.path(requestURI, null);
                }

                if (!authorized) {
                    Logger.getLogger(getClass()).log(Level.INFO, "403 " + filePath);
                    he.sendResponseHeaders(403);
                    he.close();
                    return;
                }

                he.getBaseRequest().setHandled(true);

                if (Settings.getInstance().enable("Caching.Enabled") && he.getRequest().getHeader("If-Modified-Since") != null) {
                    long lms = HttpFields.parseDate(he.getRequestHeader("If-Modified-Since"));
                    if (f.lastModified() <= lms) {
                        he.sendResponseHeaders(304);
                        return;
                    }
                }

                // Try to determine MIME type in a way that does not blow up in our face
                MagicMatch match = null;
                String type = "";
                try {
                    match = Magic.getMagicMatch(f, true);
                } catch (MagicParseException | MagicMatchNotFoundException | MagicException ex) {
                    // Magic failed - but well just ignore this for the time being
                } finally {
                    if (match != null) {
                        type = match.getMimeType();
                    } else {
                        type = "text/html";
                    }
                }

                byte[] file = null;
                type = reconsiderType(type, f);

                if (type.equals("text/markdown")) {
                    file = Processor.process(f).getBytes("UTF-8");
                }

                if (file == null) {
                    file = getBytesFromFile(f);
                }

                file = tryToDeflate(he, type, file);

                he.setResponseHeader("Content-Type", type + "; charset=UTF-8");
                he.setResponseHeader("Server", "W Application Server");

                setCaching(he, type, f);

                // X-tra headers
                he.setResponseHeader("X-Pandas-FTW", "true");
                he.setResponseHeader("X-UA-Compatible", "IE=edge,chrome=1");
                he.setResponseHeader("X-XSS-Protection", "1; mode=block");
                he.sendResponseHeaders(200);

                responseBody.write(file);

                file = null;
            } catch (IOException ex) {
                Logger.getLogger(DefaultApplication.class.getName()).log(Level.INFO, "Response failed", ex);
            }
        }
    }
}
