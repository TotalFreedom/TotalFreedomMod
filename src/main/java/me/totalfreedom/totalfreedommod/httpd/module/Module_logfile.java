package me.totalfreedom.totalfreedommod.httpd.module;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.httpd.HTMLGenerationTools;
import me.totalfreedom.totalfreedommod.httpd.HTTPDPageBuilder;
import me.totalfreedom.totalfreedommod.httpd.HTTPDaemon;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class Module_logfile extends HTTPDModule
{

    private static final File LOG_FOLDER = new File("./logs/");
    private static final String[] LOG_FILTER = new String[]
    {
        "log",
        "gz"
    };

    public Module_logfile(TotalFreedomMod plugin, NanoHTTPD.HTTPSession session)
    {
        super(plugin, session);
    }

    @Override
    public Response getResponse()
    {
        try
        {
            return new HTTPDPageBuilder(body(), title(), null, null).getResponse();
        }
        catch (ResponseOverrideException ex)
        {
            return ex.getResponse();
        }
    }

    public String title()
    {
        return "TotalFreedomMod :: Logfiles";
    }

    public String body() throws ResponseOverrideException
    {
        if (!LOG_FOLDER.exists())
        {
            return HTMLGenerationTools.paragraph("Can't find the logs folder.");
        }

        final StringBuilder out = new StringBuilder();
        final String remoteAddress = socket.getInetAddress().getHostAddress();
        final String[] args = StringUtils.split(uri, "/");
        final ModuleMode mode = ModuleMode.getMode(getArg(args, 1));

        switch (mode)
        {
            case LIST:
            {
                if (!isAuthorized(remoteAddress))
                {
                    out.append(HTMLGenerationTools.paragraph("Log files access denied: Your IP, " + remoteAddress + ", is not registered to a superadmin on this server."));
                }
                else
                {
                    Collection<File> LogFiles = FileUtils.listFiles(LOG_FOLDER, LOG_FILTER, false);

                    final List<String> LogFilesFormatted = new ArrayList<>();
                    for (File logfile : LogFiles)
                    {
                        String filename = StringEscapeUtils.escapeHtml4(logfile.getName());

                        LogFilesFormatted.add("<li><a href=\"/logfile/download?logFileName=" + filename + "\">" + filename + "</a></li>");

                    }

                    Collections.sort(LogFilesFormatted, new Comparator<String>()
                    {
                        @Override
                        public int compare(String a, String b)
                        {
                            return a.toLowerCase().compareTo(b.toLowerCase());
                        }
                    });

                    out
                            .append(HTMLGenerationTools.heading("Logfiles:", 1))
                            .append("<ul>")
                            .append(StringUtils.join(LogFilesFormatted, "\r\n"))
                            .append("</ul>");
                }
                break;
            }
            case DOWNLOAD:
            {
                if (!isAuthorized(remoteAddress))
                {
                    out.append(HTMLGenerationTools.paragraph("Log files access denied: Your IP, " + remoteAddress + ", is not registered to a superadmin on this server."));
                }
                else
                {
                    try
                    {
                        throw new ResponseOverrideException(downloadLogFile(params.get("logFileName")));
                    }
                    catch (LogFileTransferException ex)
                    {
                        out.append(HTMLGenerationTools.paragraph("Error downloading logfile: " + ex.getMessage()));
                    }
                }
                break;
            }
            default:
            {
                out.append(HTMLGenerationTools.paragraph("Invalid request mode."));
                break;
            }
        }

        return out.toString();
    }

    private Response downloadLogFile(String LogFilesName) throws LogFileTransferException
    {
        if (LogFilesName == null)
        {
            throw new LogFileTransferException("Invalid logfile requested: " + LogFilesName);
        }

        final File targetFile = new File(LOG_FOLDER.getPath(), LogFilesName);
        if (!targetFile.exists())
        {
            throw new LogFileTransferException("Logfile not found: " + LogFilesName);
        }

        Response response = HTTPDaemon.serveFileBasic(targetFile);

        response.addHeader("Content-Disposition", "attachment; filename=" + targetFile.getName() + ";");

        return response;
    }

    private boolean isAuthorized(String remoteAddress)
    {
        Admin entry = plugin.al.getEntryByIp(remoteAddress);
        return entry != null && entry.isActive();
    }

    private static class LogFileTransferException extends Exception
    {

        public LogFileTransferException()
        {
        }

        public LogFileTransferException(String string)
        {
            super(string);
        }
    }

    private static class ResponseOverrideException extends Exception
    {

        private final Response response;

        public ResponseOverrideException(Response response)
        {
            this.response = response;
        }

        public Response getResponse()
        {
            return response;
        }
    }

    private static String getArg(String[] args, int index)
    {
        String out = (args.length == index + 1 ? args[index] : null);
        return (out == null ? null : (out.trim().isEmpty() ? null : out.trim()));
    }

    private static enum ModuleMode
    {

        LIST("list"),
        DOWNLOAD("download"),
        INVALID(null);
        //
        private final String modeName;

        private ModuleMode(String modeName)
        {
            this.modeName = modeName;
        }

        @Override
        public String toString()
        {
            return this.modeName;
        }

        public static ModuleMode getMode(String needle)
        {
            for (ModuleMode mode : values())
            {
                final String haystack = mode.toString();
                if (haystack != null && haystack.equalsIgnoreCase(needle))
                {
                    return mode;
                }
            }
            return INVALID;
        }
    }

}
