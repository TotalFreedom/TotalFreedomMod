package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.Method;
import me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.Response;
import me.StevenLawson.TotalFreedomMod.TFM_Admin;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class Module_schematic extends TFM_HTTPD_Module
{
    private static final File SCHEMATIC_FOLDER = new File("./plugins/WorldEdit/schematics/");
    private static final String REQUEST_FORM_FILE_ELEMENT_NAME = "schematicFile";
    private static final Pattern SCHEMATIC_FILENAME_LC = Pattern.compile("^[a-z0-9_'!,\\-]{1,30}\\.schematic$");
    private static final String[] SCHEMATIC_FILTER = new String[]
    {
        "schematic"
    };
    private static final String UPLOAD_FORM = "<form method=\"post\" name=\"schematicForm\" id=\"schematicForm\" action=\"/schematic/upload/\" enctype=\"multipart/form-data\">\n"
            + "<p>Select a schematic file to upload. Filenames must be alphanumeric, between 1 and 30 characters long (inclusive), and have a .schematic extension.</p>\n"
            + "<input type=\"file\" id=\"schematicFile\" name=\"schematicFile\" />\n"
            + "<br />\n"
            + "<button type=\"submit\">Submit</button>\n"
            + "</form>";

    public Module_schematic(NanoHTTPD.HTTPSession session)
    {
        super(session);
    }

    @Override
    public Response getResponse()
    {
        try
        {
            return new TFM_HTTPD_PageBuilder(body(), title(), null, null).getResponse();
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

                final List<String> schematicsFormatted = new ArrayList<String>();
                for (File schematic : schematics)
                {
                    String filename = StringEscapeUtils.escapeHtml4(schematic.getName());

                    if (SCHEMATIC_FILENAME_LC.matcher(filename.trim().toLowerCase()).find())
                    {
                        schematicsFormatted.add("<li><a href=\"/schematic/download?schematicName=" + filename + "\">" + filename + "</a></li>");
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

                out
                        .append(HTMLGenerationTools.heading("Schematics:", 1))
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
                    out.append(HTMLGenerationTools.paragraph("Schematic upload access denied: Your IP, " + remoteAddress + ", is not registered to a superadmin on this server."));
                }
                else
                {
                    if (method == Method.POST)
                    {
                        try
                        {
                            uploadSchematic();
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
                out.append(HTMLGenerationTools.paragraph("Invalid request mode."));
                break;
            }
        }

        return out.toString();
    }

    private boolean uploadSchematic() throws SchematicTransferException
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

        if (tempFile.length() > FileUtils.ONE_KB * 64L)
        {
            throw new SchematicTransferException("Schematic is too big (64kb max).");
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
        }
        catch (IOException ex)
        {
            TFM_Log.severe(ex);
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

        Response response = TFM_HTTPD_Manager.serveFileBasic(targetFile);

        response.addHeader("Content-Disposition", "attachment; filename=" + targetFile.getName() + ";");

        return response;
    }

    private boolean isAuthorized(String remoteAddress)
    {
        TFM_Admin entry = TFM_AdminList.getEntryByIp(remoteAddress);
        return entry != null && entry.isActivated();
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
