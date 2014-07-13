package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;

import static me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.*;

/*
 * This class was adapted from https://github.com/NanoHttpd/nanohttpd/blob/master/webserver/src/main/java/fi/iki/elonen/SimpleWebServer.java
 */
public class Module_file extends TFM_HTTPD_Module
{
    private final File rootDir = new File(TFM_ConfigEntry.HTTPD_PUBLIC_FOLDER.getString());
    public static final Map<String, String> MIME_TYPES = new HashMap<String, String>();

    static
    {
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("xml", "text/xml");
        MIME_TYPES.put("java", "text/x-java-source, text/java");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("asc", "text/plain");
        MIME_TYPES.put("yml", "text/yaml");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("mp3", "audio/mpeg");
        MIME_TYPES.put("m3u", "audio/mpeg-url");
        MIME_TYPES.put("mp4", "video/mp4");
        MIME_TYPES.put("ogv", "video/ogg");
        MIME_TYPES.put("flv", "video/x-flv");
        MIME_TYPES.put("mov", "video/quicktime");
        MIME_TYPES.put("swf", "application/x-shockwave-flash");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("pdf", "application/pdf");
        MIME_TYPES.put("doc", "application/msword");
        MIME_TYPES.put("ogg", "application/x-ogg");
        MIME_TYPES.put("zip", "application/octet-stream");
        MIME_TYPES.put("exe", "application/octet-stream");
        MIME_TYPES.put("class", "application/octet-stream");
    }

    public Module_file(NanoHTTPD.HTTPSession session)
    {
        super(session);
    }

    private File getRootDir()
    {
        return rootDir;
    }

    private String encodeUri(String uri)
    {
        String newUri = "";
        StringTokenizer st = new StringTokenizer(uri, "/ ", true);
        while (st.hasMoreTokens())
        {
            String tok = st.nextToken();
            if (tok.equals("/"))
            {
                newUri += "/";
            }
            else if (tok.equals(" "))
            {
                newUri += "%20";
            }
            else
            {
                try
                {
                    newUri += URLEncoder.encode(tok, "UTF-8");
                }
                catch (UnsupportedEncodingException ignored)
                {
                }
            }
        }
        return newUri;
    }

    public Response serveFile(String uri, Map<String, String> params, File homeDir)
    {
        Response res = null;

        // Make sure we won't die of an exception later
        if (!homeDir.isDirectory())
        {
            res = new Response(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "INTERNAL ERRROR: serveFile(): given homeDir is not a directory.");
        }

        if (res == null)
        {
            // Remove URL arguments
            uri = uri.trim().replace(File.separatorChar, '/');
            if (uri.indexOf('?') >= 0)
            {
                uri = uri.substring(0, uri.indexOf('?'));
            }

            // Prohibit getting out of current directory
            if (uri.startsWith("src/main") || uri.endsWith("src/main") || uri.contains("../"))
            {
                res = new Response(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Won't serve ../ for security reasons.");
            }
        }

        File f = new File(homeDir, uri);
        if (res == null && !f.exists())
        {
            res = new Response(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
        }

        // List the directory, if necessary
        if (res == null && f.isDirectory())
        {
            // Browsers get confused without '/' after the
            // directory, send a redirect.
            if (!uri.endsWith("/"))
            {
                uri += "/";
                res = new Response(Response.Status.REDIRECT, NanoHTTPD.MIME_HTML, "<html><body>Redirected: <a href=\"" + uri + "\">" + uri
                        + "</a></body></html>");
                res.addHeader("Location", uri);
            }

            if (res == null)
            {
                // First try index.html and index.htm
                if (new File(f, "index.html").exists())
                {
                    f = new File(homeDir, uri + "/index.html");
                }
                else if (new File(f, "index.htm").exists())
                {
                    f = new File(homeDir, uri + "/index.htm");
                }
                else if (f.canRead())
                {
                    // No index file, list the directory if it is readable
                    res = new Response(listDirectory(uri, f));
                }
                else
                {
                    res = new Response(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: No directory listing.");
                }
            }
        }

        try
        {
            if (res == null)
            {
                // Get MIME type from file name extension, if possible
                String mime = null;
                int dot = f.getCanonicalPath().lastIndexOf('.');
                if (dot >= 0)
                {
                    mime = MIME_TYPES.get(f.getCanonicalPath().substring(dot + 1).toLowerCase());
                }
                if (mime == null)
                {
                    mime = TFM_HTTPD_Manager.MIME_DEFAULT_BINARY;
                }

                // Calculate etag
                String etag = Integer.toHexString((f.getAbsolutePath() + f.lastModified() + "" + f.length()).hashCode());

                final long fileLen = f.length();

                long startFrom = 0;
                long endAt = -1;
                final String range = params.get("range");
                if (range != null)
                {
                    final String[] rangeParams = net.minecraft.util.org.apache.commons.lang3.StringUtils.split(range, "=");
                    if (rangeParams.length >= 2)
                    {
                        if ("bytes".equalsIgnoreCase(rangeParams[0]))
                        {
                            try
                            {
                                int minus = rangeParams[1].indexOf('-');
                                if (minus > 0)
                                {
                                    startFrom = Long.parseLong(rangeParams[1].substring(0, minus));
                                    endAt = Long.parseLong(rangeParams[1].substring(minus + 1));
                                }
                            }
                            catch (NumberFormatException ignored)
                            {
                            }
                        }
                        else if ("tail".equalsIgnoreCase(rangeParams[0]))
                        {
                            try
                            {
                                final long tailLen = Long.parseLong(rangeParams[1]);
                                if (tailLen < fileLen)
                                {
                                    startFrom = fileLen - tailLen - 2;
                                    if (startFrom < 0)
                                    {
                                        startFrom = 0;
                                    }
                                }
                            }
                            catch (NumberFormatException ignored)
                            {
                            }
                        }
                    }
                }

                // Change return code and add Content-Range header when skipping is requested
                if (range != null && startFrom >= 0)
                {
                    if (startFrom >= fileLen)
                    {
                        res = new Response(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
                        res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
                        res.addHeader("ETag", etag);
                    }
                    else
                    {
                        if (endAt < 0)
                        {
                            endAt = fileLen - 1;
                        }
                        long newLen = endAt - startFrom + 1;
                        if (newLen < 0)
                        {
                            newLen = 0;
                        }

                        final long dataLen = newLen;
                        FileInputStream fis = new FileInputStream(f)
                        {
                            @Override
                            public int available() throws IOException
                            {
                                return (int) dataLen;
                            }
                        };
                        fis.skip(startFrom);

                        res = new Response(Response.Status.PARTIAL_CONTENT, mime, fis);
                        res.addHeader("Content-Length", "" + dataLen);
                        res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                        res.addHeader("ETag", etag);
                    }
                }
                else
                {
                    res = new Response(Response.Status.OK, mime, new FileInputStream(f));
                    res.addHeader("Content-Length", "" + fileLen);
                    res.addHeader("ETag", etag);
                }
            }
        }
        catch (IOException ioe)
        {
            res = new Response(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
        }

        res.addHeader("Accept-Ranges", "bytes"); // Announce that the file server accepts partial content requestes
        return res;
    }

    private String listDirectory(String uri, File f)
    {
        String heading = "Directory " + uri;
        String msg = "<html><head><title>" + heading + "</title><style><!--\n"
                + "span.dirname { font-weight: bold; }\n"
                + "span.filesize { font-size: 75%; }\n"
                + "// -->\n"
                + "</style>"
                + "</head><body><h1>" + heading + "</h1>";

        String up = null;
        if (uri.length() > 1)
        {
            String u = uri.substring(0, uri.length() - 1);
            int slash = u.lastIndexOf('/');
            if (slash >= 0 && slash < u.length())
            {
                up = uri.substring(0, slash + 1);
            }
        }

        List<String> _files = Arrays.asList(f.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return new File(dir, name).isFile();
            }
        }));
        Collections.sort(_files);
        List<String> directories = Arrays.asList(f.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return new File(dir, name).isDirectory();
            }
        }));
        Collections.sort(directories);
        if (up != null || directories.size() + _files.size() > 0)
        {
            msg += "<ul>";
            if (up != null || directories.size() > 0)
            {
                msg += "<section class=\"directories\">";
                if (up != null)
                {
                    msg += "<li><a rel=\"directory\" href=\"" + up + "\"><span class=\"dirname\">..</span></a></b></li>";
                }
                for (int i = 0; i < directories.size(); i++)
                {
                    String dir = directories.get(i) + "/";
                    msg += "<li><a rel=\"directory\" href=\"" + encodeUri(uri + dir) + "\"><span class=\"dirname\">" + dir + "</span></a></b></li>";
                }
                msg += "</section>";
            }
            if (_files.size() > 0)
            {
                msg += "<section class=\"files\">";
                for (int i = 0; i < _files.size(); i++)
                {
                    String file = _files.get(i);

                    msg += "<li><a href=\"" + encodeUri(uri + file) + "\"><span class=\"filename\">" + file + "</span></a>";
                    File curFile = new File(f, file);
                    long len = curFile.length();
                    msg += "&nbsp;<span class=\"filesize\">(";
                    if (len < 1024)
                    {
                        msg += len + " bytes";
                    }
                    else if (len < 1024 * 1024)
                    {
                        msg += len / 1024 + "." + (len % 1024 / 10 % 100) + " KB";
                    }
                    else
                    {
                        msg += len / (1024 * 1024) + "." + len % (1024 * 1024) / 10 % 100 + " MB";
                    }
                    msg += ")</span></li>";
                }
                msg += "</section>";
            }
            msg += "</ul>";
        }
        msg += "</body></html>";
        return msg;
    }

    @Override
    public Response getResponse()
    {
        return serveFile(uri, params, getRootDir());
    }
}
