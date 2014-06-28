package me.StevenLawson.TotalFreedomMod;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TFM_TwitterHandler
{
    private TFM_TwitterHandler()
    {
        throw new AssertionError();
    }

    public static String getTwitter(String player)
    {
        return request("action=gettwitter&player=" + player);
    }

    public static String setTwitter(String player, String twitter)
    {
        if (twitter.startsWith("@"))
        {
            twitter = twitter.replaceAll("@", "");
        }
        return request("action=settwitter&player=" + player + "&twitter=" + twitter);
    }

    public static String delTwitter(String player)
    {
        return request("action=deltwitter&player=" + player);
    }

    public static void delTwitterVerbose(String targetName, CommandSender sender)
    {
        final String reply = delTwitter(targetName);
        if ("ok".equals(reply))
        {
            TFM_Util.adminAction(sender.getName(), "Removing " + targetName + " from TwitterBot", true);
        }
        else if ("disabled".equals(reply))
        {
            TFM_Util.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            TFM_Util.playerMsg(sender, "TwitterBot has been temporarily disabled, please wait until it gets re-enabled", ChatColor.RED);
        }
        else if ("failed".equals(reply))
        {
            TFM_Util.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            TFM_Util.playerMsg(sender, "There was a problem querying the database, please let a developer know.", ChatColor.RED);
        }
        else if ("false".equals(reply))
        {
            TFM_Util.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            TFM_Util.playerMsg(sender, "There was a problem with the database, please let a developer know.", ChatColor.RED);
        }
        else if ("cannotauth".equals(reply))
        {
            TFM_Util.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            TFM_Util.playerMsg(sender, "The database password is incorrect, please let a developer know.", ChatColor.RED);
        }
        else if ("notfound".equals(reply))
        {
            TFM_Util.playerMsg(sender, targetName + " did not have a twitter handle registered to their name.", ChatColor.GREEN);
        }
    }

    public static String isEnabled()
    {
        return request("action=getstatus");
    }

    public static String setEnabled(String status)
    {
        return request("action=setstatus&status=" + status);
    }

    private static String request(String queryString)
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
                TFM_Log.severe(ex);
            }
        }

        return line;
    }
}
