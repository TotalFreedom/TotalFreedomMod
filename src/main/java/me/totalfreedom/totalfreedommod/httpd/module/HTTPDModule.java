package me.totalfreedom.totalfreedommod.httpd.module;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import me.totalfreedom.totalfreedommod.httpd.HTTPDPageBuilder;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD.HTTPSession;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD.Method;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD.Response;
import me.totalfreedom.totalfreedommod.util.FLog;

public abstract class HTTPDModule
{

    protected final String uri;
    protected final Method method;
    protected final Map<String, String> headers;
    protected final Map<String, String> params;
    protected final Socket socket;
    protected final HTTPSession session;

    public HTTPDModule(HTTPSession session)
    {
        this.uri = session.getUri();
        this.method = session.getMethod();
        this.headers = session.getHeaders();
        this.params = session.getParms();
        this.socket = session.getSocket();
        this.session = session;
    }

    public String getBody()
    {
        return null;
    }

    public String getTitle()
    {
        return null;
    }

    public String getStyle()
    {
        return null;
    }

    public String getScript()
    {
        return null;
    }

    public Response getResponse()
    {
        return new HTTPDPageBuilder(getBody(), getTitle(), getStyle(), getScript()).getResponse();
    }

    protected final Map<String, String> getFiles()
    {
        Map<String, String> files = new HashMap<>();

        try
        {
            session.parseBody(files);
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }

        return files;
    }
}
