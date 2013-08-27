package me.StevenLawson.TotalFreedomMod.HTTPD;

import me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.Response;

public class TFM_HTTPD_PageBuilder
{
    private static final String TEMPLATE =
            "<!DOCTYPE html>\r\n"
            + "<html>\r\n"
            + "<head>\r\n"
            + "<title>{$TITLE}</title>\r\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\r\n"
            + "<style type=\"text/css\">{$STYLE}</style>\r\n"
            + "</head>\r\n"
            + "<body>{$BODY}</body>\r\n"
            + "</html>\r\n";
    //
    private String body = "";
    private String title = "";
    private String style = "";

    public TFM_HTTPD_PageBuilder()
    {
    }

    public TFM_HTTPD_PageBuilder(String body, String title, String style)
    {
        this.body = body;
        this.title = title;
        this.style = style;
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

    public Response getResponse()
    {
        return new Response(this.toString());
    }

    @Override
    public String toString()
    {
        return TEMPLATE.replace("{$BODY}", body).replace("{$TITLE}", title).replace("{$STYLE}", style);
    }
}
