package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.util.FLog;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TwitterHandler
{
    private TwitterHandler()
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
            FUtil.adminAction(sender.getName(), "Removing " + targetName + " from TwitterBot", true);
        }
        else if ("disabled".equals(reply))
        {
            FUtil.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            FUtil.playerMsg(sender, "TwitterBot has been temporarily disabled, please wait until it gets re-enabled", ChatColor.RED);
        }
        else if ("failed".equals(reply))
        {
            FUtil.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            FUtil.playerMsg(sender, "There was a problem querying the database, please let a developer know.", ChatColor.RED);
        }
        else if ("false".equals(reply))
        {
            FUtil.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            FUtil.playerMsg(sender, "There was a problem with the database, please let a developer know.", ChatColor.RED);
        }
        else if ("cannotauth".equals(reply))
        {
            FUtil.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            FUtil.playerMsg(sender, "The database password is incorrect, please let a developer know.", ChatColor.RED);
        }
        else if ("notfound".equals(reply))
        {
            FUtil.playerMsg(sender, targetName + " did not have a twitter handle registered to their name.", ChatColor.GREEN);
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

        final String twitterbotURL = ConfigEntry.TWITTERBOT_URL.getString();
        final String twitterbotSecret = ConfigEntry.TWITTERBOT_SECRET.getString();

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
                FLog.severe(ex);
            }
        }

        return line;
    }
}
