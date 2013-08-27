package me.StevenLawson.TotalFreedomMod.HTTPD;

import me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.Response;

public class TFM_HTTPD_PageBuilder
{
    private static final String TEMPLATE =
            "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "<title>{$TITLE}</title>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
            + "</head>\n"
            + "<body>{$BODY}</body>\n"
            + "</html>\n";
    //
    private String body;
    private String title;

    public TFM_HTTPD_PageBuilder()
    {
    }

    public TFM_HTTPD_PageBuilder(String body, String title)
    {
        this.body = body;
        this.title = title;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Response getResponse()
    {
        return new Response(TEMPLATE.replace("{$TITLE}", title).replace("{$BODY}", body));
    }
}
