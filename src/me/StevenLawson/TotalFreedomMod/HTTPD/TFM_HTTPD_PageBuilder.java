package me.StevenLawson.TotalFreedomMod.HTTPD;

import me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.Response;

public class TFM_HTTPD_PageBuilder
{
    private static final String TEMPLATE
            = "<!DOCTYPE html>\r\n"
            + "<html>\r\n"
            + "<head>\r\n"
            + "<title>{$TITLE}</title>\r\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\r\n"
            + "{$STYLE}"
            + "{$SCRIPT}"
            + "</head>\r\n"
            + "<body>\r\n{$BODY}</body>\r\n"
            + "</html>\r\n";
    private static final String STYLE = "<style type=\"text/css\">{$STYLE}</style>\r\n";
    private static final String SCRIPT
            = "<script src=\"//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js\"></script>\r\n"
            + "<script src=\"//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js\"></script>\r\n"
            + "<script>\r\n{$SCRIPT}\r\n</script>\r\n";
    //
    private String body = null;
    private String title = null;
    private String style = null;
    private String script = null;

    public TFM_HTTPD_PageBuilder()
    {
    }

    public TFM_HTTPD_PageBuilder(String body, String title, String style, String script)
    {
        this.body = body;
        this.title = title;
        this.style = style;
        this.script = script;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public void setScript(String script)
    {
        this.script = script;
    }

    public Response getResponse()
    {
        return new Response(this.toString());
    }

    @Override
    public String toString()
    {
        return TEMPLATE
                .replace("{$BODY}", this.body == null ? "" : this.body)
                .replace("{$TITLE}", this.title == null ? "" : this.title)
                .replace("{$STYLE}", this.style == null ? "" : STYLE.replace("{$STYLE}", this.style))
                .replace("{$SCRIPT}", this.script == null ? "" : SCRIPT.replace("{$SCRIPT}", this.script));
    }
}
