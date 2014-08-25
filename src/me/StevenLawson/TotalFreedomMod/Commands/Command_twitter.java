package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_TwitterHandler;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manage your twitter.", usage = "/<command> <set [twitter] | info | enable | disable>")
public class Command_twitter extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!TFM_ConfigEntry.TWITTERBOT_ENABLED.getBoolean())
        {
            playerMsg("TwitterBot has been disabled in config.", ChatColor.RED);
            return true;
        }

        if (args.length < 1)
        {
            return false;
        }

        if ("set".equals(args[0]))
        {
            if (args.length != 2)
            {
                return false;
            }

            if (args[1].startsWith("@"))
            {
                playerMsg("Please do not prefix your twitter username with '@'");
                return true;
            }

            String reply = TFM_TwitterHandler.setTwitter(sender.getName(), args[1]);

            if ("ok".equals(reply))
            {
                playerMsg("Your twitter handle has been set to: " + ChatColor.AQUA + "@" + args[1] + ChatColor.GRAY + ".");
            }
            else if ("disabled".equals(reply))
            {
                playerMsg("TwitterBot has been temporarily disabled,, please wait until it get re-enabled", ChatColor.RED);
            }
            else if ("failed".equals(reply))
            {
                playerMsg("There was a problem querying the database, please let a developer know.", ChatColor.RED);
            }
            else if ("false".equals(reply))
            {
                playerMsg("There was a problem with the database, please let a developer know.", ChatColor.RED);
            }
            else if ("cannotauth".equals(reply))
            {
                playerMsg("The database password is incorrect, please let a developer know.", ChatColor.RED);
            }
            else
            {
                playerMsg("An unknown error occurred, please contact a developer", ChatColor.RED);
                playerMsg("Response code: " + reply);
            }
            return true;
        }

        if (args.length != 1)
        {
            return false;
        }

        if ("info".equals(args[0]))
        {
            String reply = TFM_TwitterHandler.getTwitter(sender.getName());
            playerMsg("-- Twitter Information --", ChatColor.BLUE);
            playerMsg("Using this feature, you can re-super yourself using twitter.");
            playerMsg("You can set your twitter handle using " + ChatColor.AQUA + "/twitter set [twittername]");
            playerMsg("Then, you can verify yourself by tweeting " + ChatColor.AQUA + "@TFUpdates #superme");
            if ("notfound".equals(reply))
            {
                playerMsg("You currently have " + ChatColor.RED + "no" + ChatColor.BLUE + " Twitter handle set.", ChatColor.BLUE);
            }
            else if ("disabled".equals(reply))
            {
                playerMsg("TwitterBot has been temporarily disabled, please wait until re-enabled", ChatColor.RED);
            }
            else if ("failed".equals(reply))
            {
                playerMsg("There was a problem querying the database, please let a developer know.", ChatColor.RED);
            }
            else if ("false".equals(reply))
            {
                playerMsg("There was a problem with the database, please let a developer know.", ChatColor.RED);
            }
            else if ("cannotauth".equals(reply))
            {
                playerMsg("The database password is incorrect, please let a developer know.", ChatColor.RED);
            }
            else
            {
                playerMsg("Your current twitter handle: " + ChatColor.AQUA + "@" + reply + ChatColor.BLUE + ".", ChatColor.BLUE);
            }
            return true;
        }

        if ("enable".equals(args[0]) || "disable".equals(args[0]))
        {
            if (!sender.getName().equalsIgnoreCase("DarthSalamon"))
            {
                sender.sendMessage(TFM_Command.MSG_NO_PERMS);
                return true;
            }

            TFM_Util.adminAction(sender.getName(), ("enable".equals(args[0]) ? "Ena" : "Disa") + "bling Twitterbot", true);
            String reply = TFM_TwitterHandler.setEnabled(args[0] + "d");
            playerMsg("Reply: " + reply);
            return true;
        }

        // Command not recognised
        return false;
    }
}
