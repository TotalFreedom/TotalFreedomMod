package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_Superadmin;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class Module_schematic extends TFM_HTTPD_Module
{
    private static final File SCHEMATIC_FOLDER = new File("./plugins/WorldEdit/schematics/");
    private static final String REQUEST_FORM_FILE_ELEMENT_NAME = "schematicFile";
    private static final Pattern SCHEMATIC_FILENAME = Pattern.compile("^[a-zA-Z0-9]+\\.schematic$");
    private static final String[] SCHEMATIC_FILTER = new String[]
    {
        "schematic"
    };

    public Module_schematic(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files, Socket socket)
    {
        super(uri, method, headers, params, files, socket);
    }

    @Override
    public String getTitle()
    {
        return "TotalFreedomMod :: Schematic Manager";
    }

    @Override
    public String getBody()
    {
        if (!SCHEMATIC_FOLDER.exists())
        {
            return HTMLGenerationTools.paragraph("Can't find WorldEdit schematic folder.");
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
                    schematicsFormatted.add("<li><a href=\"/schematic/download?schematicName=" + filename + "\">" + filename + "</a></li>");
                }

                Collections.sort(schematicsFormatted);

                out
                        .append(HTMLGenerationTools.heading("Schematics:", 1))
                        .append("<ul>")
                        .append(StringUtils.join(schematicsFormatted, "\r\n"))
                        .append("</ul>");

                break;
            }
            case DOWNLOAD:
            {
                out.append(HTMLGenerationTools.paragraph("Not yet implemented - Download: " + params.get("schematicName")));
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
                    try
                    {
                        uploadSchematic();
                        out.append(HTMLGenerationTools.paragraph("Schematic uploaded successfully."));
                    }
                    catch (SchematicUploadException ex)
                    {
                        out.append(HTMLGenerationTools.paragraph("Error uploading schematic."));
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

    private boolean uploadSchematic() throws SchematicUploadException
    {
        final String tempFileName = files.get(REQUEST_FORM_FILE_ELEMENT_NAME);
        if (tempFileName != null)
        {
            final String origFileName = params.get(REQUEST_FORM_FILE_ELEMENT_NAME).trim();
            if (origFileName == null || origFileName.trim().isEmpty())
            {
                throw new SchematicUploadException("Can't resolve original file name.");
            }
            else
            {
                final File tempFile = new File(tempFileName);
                if (tempFile.exists())
                {
                    if (tempFile.length() <= FileUtils.ONE_KB * 64L)
                    {
                        if (SCHEMATIC_FILENAME.matcher(origFileName).find())
                        {
                            final File targetFile = new File(SCHEMATIC_FOLDER.getPath(), origFileName);
                            if (targetFile.exists())
                            {
                                throw new SchematicUploadException("Schematic exists on the server already.");
                            }
                            else
                            {
                                try
                                {
                                    FileUtils.copyFile(tempFile, targetFile);
                                }
                                catch (IOException ex)
                                {
                                    TFM_Log.severe(ex);
                                    throw new SchematicUploadException();
                                }
                            }
                        }
                        else
                        {
                            throw new SchematicUploadException("File name must be alphanumeric with a \".schematic\" extension.");
                        }
                    }
                    else
                    {
                        throw new SchematicUploadException("Schematic is too big (64kb max).");
                    }
                }
                else
                {
                    throw new SchematicUploadException();
                }
            }
        }
        else
        {
            throw new SchematicUploadException("No file transmitted to server.");
        }

        return true;
    }

    private static class SchematicUploadException extends Exception
    {
        public SchematicUploadException()
        {
        }

        public SchematicUploadException(String string)
        {
            super(string);
        }
    }

    private boolean isAuthorized(String remoteAddress)
    {
        TFM_Superadmin entry = TFM_SuperadminList.getAdminEntryByIP(remoteAddress);
        return entry != null && entry.isActivated();
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

    private String getArg(String[] args, int index)
    {
        String out = (args.length == index + 1 ? args[index] : null);
        return (out == null ? null : (out.trim().isEmpty() ? null : out.trim()));
    }
}
