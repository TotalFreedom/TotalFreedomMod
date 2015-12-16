package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.util.DepreciationAggregator;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Manage the whitelist.", usage = "/<command> <on | off | list | count | add <player> | remove <player> | addall | purge>")
public class Command_whitelist extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        // list
        if (args[0].equalsIgnoreCase("list"))
        {
            playerMsg("Whitelisted players: " + FUtil.playerListToNames(server.getWhitelistedPlayers()));
            return true;
        }

        // count
        if (args[0].equalsIgnoreCase("count"))
        {
            int onlineWPs = 0;
            int offlineWPs = 0;
            int totalWPs = 0;

            for (OfflinePlayer player : server.getWhitelistedPlayers())
            {
                if (player.isOnline())
                {
                    onlineWPs++;
                }
                else
                {
                    offlineWPs++;
                }
                totalWPs++;
            }

            playerMsg("Online whitelisted players: " + onlineWPs);
            playerMsg("Offline whitelisted players: " + offlineWPs);
            playerMsg("Total whitelisted players: " + totalWPs);

            return true;
        }

        // all commands past this line are superadmin-only
        if (!(senderIsConsole || plugin.al.isAdmin(sender)))
        {
            sender.sendMessage(FreedomCommand.MSG_NO_PERMS);
            return true;
        }

        // on
        if (args[0].equalsIgnoreCase("on"))
        {
            FUtil.adminAction(sender.getName(), "Turning the whitelist on.", true);
            server.setWhitelist(true);
            return true;
        }

        // off
        if (args[0].equalsIgnoreCase("off"))
        {
            FUtil.adminAction(sender.getName(), "Turning the whitelist off.", true);
            server.setWhitelist(false);
            return true;
        }

        // add
        if (args[0].equalsIgnoreCase("add"))
        {
            if (args.length < 2)
            {
                return false;
            }

            String search_name = args[1].trim().toLowerCase();

            OfflinePlayer player = getPlayer(search_name);

            if (player == null)
            {
                player = DepreciationAggregator.getOfflinePlayer(server, search_name);
            }

            FUtil.adminAction(sender.getName(), "Adding " + player.getName() + " to the whitelist.", false);
            player.setWhitelisted(true);
            return true;
        }

        // remove
        if ("remove".equals(args[0]))
        {
            if (args.length < 2)
            {
                return false;
            }

            String search_name = args[1].trim().toLowerCase();

            OfflinePlayer player = getPlayer(search_name);

            if (player == null)
            {
                player = DepreciationAggregator.getOfflinePlayer(server, search_name);
            }

            if (player.isWhitelisted())
            {
                FUtil.adminAction(sender.getName(), "Removing " + player.getName() + " from the whitelist.", false);
                player.setWhitelisted(false);
                return true;
            }
            else
            {
                playerMsg("That player is not whitelisted");
                return true;
            }

        }

        // addall
        if (args[0].equalsIgnoreCase("addall"))
        {
            FUtil.adminAction(sender.getName(), "Adding all online players to the whitelist.", false);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!player.isWhitelisted())
                {
                    player.setWhitelisted(true);
                    counter++;
                }
            }

            playerMsg("Whitelisted " + counter + " players.");
            return true;
        }

        // all commands past this line are console/telnet only
        if (!senderIsConsole)
        {
            sender.sendMessage(FreedomCommand.MSG_NO_PERMS);
            return true;
        }

        //purge
        if (args[0].equalsIgnoreCase("purge"))
        {
            FUtil.adminAction(sender.getName(), "Removing all players from the whitelist.", false);
            playerMsg("Removed " + plugin.si.purgeWhitelist() + " players from the whitelist.");

            return true;
        }

        // none of the commands were executed
        return false;
    }
}
