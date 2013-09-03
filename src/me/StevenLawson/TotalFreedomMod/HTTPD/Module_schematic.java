package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.net.Socket;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class Module_schematic extends TFM_HTTPD_Module
{
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
        final StringBuilder out = new StringBuilder();

        final String[] args = StringUtils.split(uri, "/");
        ModuleMode mode = ModuleMode.getMode(getArg(args, 1));

        switch (mode)
        {
            case DOWNLOAD:
            {
                break;
            }
            case UPLOAD:
            {
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

    private static enum ModuleMode
    {
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
