package me.StevenLawson.TotalFreedomMod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.lang.exception.ExceptionUtils;

public class TFM_TwitterHandler
{
    private TFM_TwitterHandler()
    {
    }

    public String getTwitter(String player)
    {
        return request("action=gettwitter&player=" + player);
    }

    public String setTwitter(String player, String twitter)
    {
        if (twitter.startsWith("@"))
        {
            twitter = twitter.replaceAll("@", "");
        }
        return request("action=settwitter&player=" + player + "&twitter=" + twitter);
    }

    public String delTwitter(String player)
    {
        return request("action=deltwitter&player=" + player);
    }

    public String isEnabled()
    {
        return request("action=getstatus");
    }

    public String setEnabled(String status)
    {
        return request("action=setstatus&status=" + status);
    }

    private String request(String queryString)
    {
        String line = "failed";

        final String twitterbotURL = TFM_ConfigEntry.TWITTERBOT_URL.getString();
        final String twitterbotSecret = TFM_ConfigEntry.TWITTERBOT_SECRET.getString();

        if (twitterbotURL != null && twitterbotSecret != null && !twitterbotURL.isEmpty() && !twitterbotSecret.isEmpty())
        {
            try
            {
                URL getUrl = new URL(twitterbotURL + "?auth=" + twitterbotSecret + "&" + queryString);
                URLConnection urlConnection = getUrl.openConnection();
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                line = in.readLine();
                in.close();
            }
            catch (Exception ex)
            {
                TFM_Log.severe(ExceptionUtils.getFullStackTrace(ex));
            }
        }

        return line;
    }

    public static TFM_TwitterHandler getInstance()
    {
        return new TFM_TwitterHandler();
    }
}
