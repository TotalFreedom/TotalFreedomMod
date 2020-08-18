package me.totalfreedom.totalfreedommod.httpd.module;

import java.io.File;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.httpd.HTTPDaemon;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD;
import me.totalfreedom.totalfreedommod.staff.ActivityLog;
import me.totalfreedom.totalfreedommod.staff.StaffMember;

public class Module_activitylog extends HTTPDModule
{

    public Module_activitylog(TotalFreedomMod plugin, NanoHTTPD.HTTPSession session)
    {
        super(plugin, session);
    }

    @Override
    public NanoHTTPD.Response getResponse()
    {
        final String remoteAddress = socket.getInetAddress().getHostAddress();

        if (!isAuthorized(remoteAddress))
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
                    "You may not view the activity log. Your IP, " + remoteAddress + ", is not registered to an admin on the server.");
        }
        File activityLogFile = new File(plugin.getDataFolder(), ActivityLog.FILENAME);
        if (activityLogFile.exists())
        {
            return HTTPDaemon.serveFileBasic(new File(plugin.getDataFolder(), ActivityLog.FILENAME));
        }
        else
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
                    "Error 404: Not Found - The requested resource was not found on this server.");
        }
    }

    private boolean isAuthorized(String remoteAddress)
    {
        StaffMember entry = plugin.sl.getEntryByIp(remoteAddress);
        return entry != null && entry.isActive();
    }
}
