package me.totalfreedom.totalfreedommod.command;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FSync;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Validates if a given account is premium, or cracked.", usage = "/<command> <player>", aliases = "prem")
public class Command_premium extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
            return false;

        final Player player = getPlayer(args[0]);
        final String name;

        if (player != null)
            name = player.getName();
        else
            name = args[0];

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
                    String message = "";
                    /*old code
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())))
                    //message = (!"PREMIUM".equalsIgnoreCase(in.readLine()) ? ChatColor.RED + "No" : ChatColor.DARK_GREEN + "Yes");
                    */
                    try
                    {
                        if (urlConnection.getResponseCode() == 200)
                            message = ChatColor.GREEN + "Yes";
                        else
                            message = ChatColor.RED + "No";                            
                        FSync.playerMsg(sender, "Player " + name + " is premium: " + message);
                    }
                    catch (IOException e)
                    {
                       FSync.playerMsg(sender, ChatColor.RED + "There was an error on trying to connect to the API server");
                    }
                    
                }
                catch (IOException ex)
                {
                    FLog.severe(ex);
                    msg("There was an error querying the API server.", ChatColor.RED);
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
