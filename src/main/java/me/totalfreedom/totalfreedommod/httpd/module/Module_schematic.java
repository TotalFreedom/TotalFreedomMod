package me.totalfreedom.totalfreedommod.httpd.module;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.httpd.HTMLGenerationTools;
import me.totalfreedom.totalfreedommod.httpd.HTTPDPageBuilder;
import me.totalfreedom.totalfreedommod.httpd.HTTPDaemon;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD.Method;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD.Response;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.staff.StaffMember;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class Module_schematic extends HTTPDModule
{

    private static final File SCHEMATIC_FOLDER = new File("./plugins/WorldEdit/schematics/");
    private static final String REQUEST_FORM_FILE_ELEMENT_NAME = "schematicFile";
    private static final Pattern SCHEMATIC_FILENAME_LC = Pattern.compile("^[a-z0-9_'!,\\-]*\\.(schem|schematic)$");
    private static final String[] SCHEMATIC_FILTER = new String[]
            {
                    "schematic",
                    "schem"
            };
    private static final String UPLOAD_FORM = "<form method=\"post\" name=\"schematicForm\" id=\"schematicForm\" action=\"/schematic/upload/\" enctype=\"multipart/form-data\">\n"
            + "<p>Select a schematic file to upload. Filenames must be alphanumeric, between 1 and 30 characters long (inclusive), and have a .schematic extension.</p>\n"
            + "<input type=\"file\" id=\"schematicFile\" name=\"schematicFile\" />\n"
            + "<br />\n"
            + "<button type=\"submit\">Submit</button>\n"
            + "</form>";

    public Module_schematic(TotalFreedomMod plugin, NanoHTTPD.HTTPSession session)
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
        return "TotalFreedomMod :: Schematic Manager";
    }

    public String body() throws ResponseOverrideException
    {
        if (!SCHEMATIC_FOLDER.exists())
        {
            return HTMLGenerationTools.paragraph("Can't find the WorldEdit schematic folder.");
        }

        final StringBuilder out = new StringBuilder();

        final String[] args = StringUtils.split(uri, "/");
        final ModuleMode mode = ModuleMode.getMode(getArg(args, 1));

        switch (mode)
        {
            case LIST:
            {
                Collection<File> schematics = FileUtils.listFiles(SCHEMATIC_FOLDER, SCHEMATIC_FILTER, false);

                final List<String> schematicsFormatted = new ArrayList<>();
                for (File schematic : schematics)
                {
                    String filename = StringEscapeUtils.escapeHtml4(schematic.getName());

                    if (SCHEMATIC_FILENAME_LC.matcher(filename.trim().toLowerCase()).find())
                    {
                        schematicsFormatted.add("<li><a href=\"/schematic/download?schematicName=" + filename + "\">" + filename + "</a></li>");
                    }
                    else if (filename.length() > 254)
                    {
                        schematicsFormatted.add("<li>" + filename + " - (Filename too long, can't download)</li>");
                    }
                    else
                    {
                        schematicsFormatted.add("<li>" + filename + " - (Illegal filename, can't download)</li>");
                    }
                }

                Collections.sort(schematicsFormatted, new Comparator<String>()
                {
                    @Override
                    public int compare(String a, String b)
                    {
                        return a.toLowerCase().compareTo(b.toLowerCase());
                    }
                });

                out.append(HTMLGenerationTools.heading("Schematics:", 1))
                        .append("<ul>")
                        .append(StringUtils.join(schematicsFormatted, "\r\n"))
                        .append("</ul>");
                break;
            }
            case DOWNLOAD:
            {
                try
                {
                    throw new ResponseOverrideException(downloadSchematic(params.get("schematicName")));
                }
                catch (SchematicTransferException ex)
                {
                    out.append(HTMLGenerationTools.paragraph("Error downloading schematic: " + ex.getMessage()));
                }
                break;
            }
            case UPLOAD:
            {
                final String remoteAddress = socket.getInetAddress().getHostAddress();
                if (!isAuthorized(remoteAddress))
                {
                    out.append(HTMLGenerationTools.paragraph("Schematic upload access denied: Your IP, " + remoteAddress + ", is not registered to an admin on this server."));
                }
                else
                {
                    if (method == Method.POST)
                    {
                        try
                        {
                            uploadSchematic(remoteAddress);

                            out.append(HTMLGenerationTools.paragraph("Schematic uploaded successfully."));
                        }
                        catch (SchematicTransferException ex)
                        {
                            out.append(HTMLGenerationTools.paragraph("Error uploading schematic: " + ex.getMessage()));
                        }
                    }
                    else
                    {
                        out.append(UPLOAD_FORM);
                    }
                }
                break;
            }
            default:
            {
                out.append(HTMLGenerationTools.heading("Schematic Submodules", 1));
                out.append("<ul><li>");
                out.append("<a href=\"http://")
                        .append(ConfigEntry.HTTPD_HOST.getString())
                        .append(":")
                        .append(ConfigEntry.HTTPD_PORT.getInteger())
                        .append("/schematic/list")
                        .append("\">Schematic List</a></li>")
                        .append("<li><a href=\"http://")
                        .append(ConfigEntry.HTTPD_HOST.getString())
                        .append(":")
                        .append(ConfigEntry.HTTPD_PORT.getInteger())
                        .append("/schematic/upload")
                        .append("\">Upload Schematics</a></li></ul>");
                break;
            }
        }
        return out.toString();
    }

    private boolean uploadSchematic(String remoteAddress) throws SchematicTransferException
    {
        Map<String, String> files = getFiles();

        final String tempFileName = files.get(REQUEST_FORM_FILE_ELEMENT_NAME);
        if (tempFileName == null)
        {
            throw new SchematicTransferException("No file transmitted to server.");
        }

        final File tempFile = new File(tempFileName);
        if (!tempFile.exists())
        {
            throw new SchematicTransferException();
        }

        String origFileName = params.get(REQUEST_FORM_FILE_ELEMENT_NAME);
        if (origFileName == null || (origFileName = origFileName.trim()).isEmpty())
        {
            throw new SchematicTransferException("Can't resolve original file name.");
        }

        if (tempFile.length() > FileUtils.ONE_MB)
        {
            throw new SchematicTransferException("Schematic is too big (1mb max).");
        }

        if (plugin.web.getWorldEditPlugin() == null)
        {
            throw new SchematicTransferException("WorldEdit is not on the server.");
        }

        if (!SCHEMATIC_FILENAME_LC.matcher(origFileName.toLowerCase()).find())
        {
            throw new SchematicTransferException("File name must be alphanumeric, between 1 and 30 characters long (inclusive), and have a \".schematic\" extension.");
        }

        final File targetFile = new File(SCHEMATIC_FOLDER.getPath(), origFileName);
        if (targetFile.exists())
        {
            throw new SchematicTransferException("Schematic already exists on the server.");
        }

        try
        {
            FileUtils.copyFile(tempFile, targetFile);
            ClipboardFormat format = ClipboardFormats.findByFile(targetFile);
            if (format == null)
            {
                FileUtils.deleteQuietly(targetFile);
                throw new SchematicTransferException("Schematic is not a valid schematic.");
            }
            try
            {
                ClipboardReader reader = format.getReader(new FileInputStream(targetFile));
            }
            catch (IOException e)
            {
                FileUtils.deleteQuietly(targetFile);
                throw new SchematicTransferException("Schematic is not a valid schematic.");
            }

            FLog.info(remoteAddress + " uploaded schematic: " + targetFile.getName());

        }
        catch (IOException ex)
        {
            FLog.severe(ex);
            throw new SchematicTransferException();
        }

        return true;
    }

    private Response downloadSchematic(String schematicName) throws SchematicTransferException
    {
        if (schematicName == null || !SCHEMATIC_FILENAME_LC.matcher((schematicName = schematicName.trim()).toLowerCase()).find())
        {
            throw new SchematicTransferException("Invalid schematic name requested: " + schematicName);
        }

        final File targetFile = new File(SCHEMATIC_FOLDER.getPath(), schematicName);
        if (!targetFile.exists())
        {
            throw new SchematicTransferException("Schematic not found: " + schematicName);
        }

        Response response = HTTPDaemon.serveFileBasic(targetFile);

        response.addHeader("Content-Disposition", "attachment; filename=" + targetFile.getName() + ";");

        return response;
    }

    private boolean isAuthorized(String remoteAddress)
    {
        StaffMember staffMemberEntry = plugin.sl.getEntryByIp(remoteAddress);
        PlayerData data = plugin.pl.getDataByIp(remoteAddress);
        return ((staffMemberEntry != null && staffMemberEntry.isActive()) || data != null && data.isMasterBuilder());
    }

    private static class SchematicTransferException extends Exception
    {

        public SchematicTransferException()
        {
        }

        public SchematicTransferException(String string)
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
        UPLOAD("upload"),
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