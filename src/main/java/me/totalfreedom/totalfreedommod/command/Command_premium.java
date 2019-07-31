package me.totalfreedom.totalfreedommod.command;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Validates if a given account is premium.", usage = "/<command> <player>", aliases = "prem")
public class Command_premium extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);
        final String name;

        if (player != null)
        {
            name = player.getName();
        }
        else
        {
            name = args[0];
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final URL getUrl = new URL("https://api.ashcon.app/mojang/v2/user/" + name);
                    final HttpURLConnection urlConnection = (HttpURLConnection)getUrl.openConnection();
                    urlConnection.setRequestProperty("User-Agent", "");
                    final String message;
                    String message1;
                    try //( // Read the response
                          //BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())))
                    {
                        if (urlConnection.getResponseCode() == 200)
                        {
                            message1 = ChatColor.GREEN + "Yes";
                        }
                        else
                        {
                            message1 = ChatColor.RED + "No";
                        }
                        //message = (!"PREMIUM".equalsIgnoreCase(in.readLine()) ? ChatColor.RED + "No" : ChatColor.DARK_GREEN + "Yes");
                    }
                    catch (Exception e){
                        message1 = ChatColor.RED + "There was an error on trying to connect to the API server";
                    }


                    message = message1;
                    if (!plugin.isEnabled())
                    {
                        return;
                    }

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            msg("Player " + name + " is premium: " + message);
                        }
                    }.runTask(plugin);

                }
                catch (Exception ex)
                {
                    FLog.severe(ex);
                    msg("There was an error querying the API server.", ChatColor.RED);
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
