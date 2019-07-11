package me.totalfreedom.totalfreedommod.httpd.module;

import java.io.File;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.ActivityLog;
import me.totalfreedom.totalfreedommod.httpd.HTTPDaemon;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD;

public class Module_activitylog extends HTTPDModule
{

    public Module_activitylog(TotalFreedomMod plugin, NanoHTTPD.HTTPSession session)
    {
        super(plugin, session);
    }

    @Override
    public NanoHTTPD.Response getResponse()
    {
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
}
