package me.totalfreedom.totalfreedommod.httpd.module;

import java.io.File;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.banning.PermbanList;
import me.totalfreedom.totalfreedommod.httpd.HTTPDaemon;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD;

public class Module_permbans extends HTTPDModule
{

    public Module_permbans(TotalFreedomMod plugin, NanoHTTPD.HTTPSession session)
    {
        super(plugin, session);
    }

    @Override
    public NanoHTTPD.Response getResponse()
    {
        File permbanFile = new File(plugin.getDataFolder(), PermbanList.CONFIG_FILENAME);
        final String remoteAddress = socket.getInetAddress().getHostAddress();
        if (!isAuthorized(remoteAddress))
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
                    "You may not view the permban list. Your IP, " + remoteAddress + ", is not registered to an admin on the server.");
        }
        if (permbanFile.exists())
        {
            return HTTPDaemon.serveFileBasic(new File(plugin.getDataFolder(), PermbanList.CONFIG_FILENAME));
        }
        else
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
                    "Error 404: Not Found - The requested resource was not found on this server.");
        }
    }

    private boolean isAuthorized(String remoteAddress)
    {
        Admin entry = plugin.al.getEntryByIp(remoteAddress);
        return entry != null && entry.isActive();
    }
}
