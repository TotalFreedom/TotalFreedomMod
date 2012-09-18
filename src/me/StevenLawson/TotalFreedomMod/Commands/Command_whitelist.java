package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Set;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import net.minecraft.server.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_whitelist extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        if (!sender.isOp())
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        // list
        if (args[0].equalsIgnoreCase("list"))
        {
            TFM_Util.playerMsg(sender, "Whitelisted players: " + TFM_Util.playerListToNames(server.getWhitelistedPlayers()));
            return true;
        }

        // count
        if (args[0].equalsIgnoreCase("count"))
        {
            int onlineWPs = 0;
            int offlineWPs = 0;
            int totalWPs = 0;

            for (OfflinePlayer p : server.getWhitelistedPlayers())
            {
                if (p.isOnline())
                {
                    onlineWPs++;
                }
                else
                {
                    offlineWPs++;
                }
                totalWPs++;
            }

            sender.sendMessage(ChatColor.GRAY + "Online whitelisted players: " + onlineWPs);
            sender.sendMessage(ChatColor.GRAY + "Offline whitelisted players: " + offlineWPs);
            sender.sendMessage(ChatColor.GRAY + "Total whitelisted players: " + totalWPs);

            return true;
        }

        // all commands past this line are superadmin-only
        if (!(senderIsConsole || TFM_Util.isUserSuperadmin(sender)))
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        // on
        if (args[0].equalsIgnoreCase("on"))
        {
            TFM_Util.adminAction(sender.getName(), "Turning the whitelist on.", true);
            server.setWhitelist(true);
            return true;
        }

        // off
        if (args[0].equalsIgnoreCase("off"))
        {
            TFM_Util.adminAction(sender.getName(), "Turning the whitelist off.", true);
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

            OfflinePlayer p;
            try
            {
                p = getPlayer(search_name);
            }
            catch (CantFindPlayerException ex)
            {
                p = server.getOfflinePlayer(search_name);
            }

            TFM_Util.adminAction(sender.getName(), "Adding " + p.getName() + " to the whitelist.", false);
            p.setWhitelisted(true);
            return true;
        }

        // remove
        if (args[0].equalsIgnoreCase("remove"))
        {
            if (args.length < 2)
            {
                return false;
            }

            String search_name = args[1].trim().toLowerCase();

            OfflinePlayer p;
            try
            {
                p = getPlayer(search_name);
            }
            catch (CantFindPlayerException ex)
            {
                p = server.getOfflinePlayer(search_name);
            }

            if (p.isWhitelisted())
            {
                TFM_Util.adminAction(sender.getName(), "Removing " + p.getName() + " from the whitelist.", false);
                p.setWhitelisted(false);
                return true;
            }
            else
            {
                TFM_Util.playerMsg(sender, "That player is not whitelisted");
                return true;
            }

        }

        // addall
        if (args[0].equalsIgnoreCase("addall"))
        {
            TFM_Util.adminAction(sender.getName(), "Adding all online players to the whitelist.", false);
            int counter = 0;
            for (Player p : server.getOnlinePlayers())
            {
                if (!p.isWhitelisted())
                {
                    p.setWhitelisted(true);
                    counter++;
                }
            }

            TFM_Util.playerMsg(sender, "Whitelisted " + counter + " players.");
            return true;
        }

        // all commands past this line are console/telnet only
        if (!senderIsConsole)
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        //purge
        if (args[0].equalsIgnoreCase("purge"))
        {
            @SuppressWarnings("rawtypes")
			Set whitelisted = MinecraftServer.getServer().getServerConfigurationManager().getWhitelisted();
            TFM_Util.adminAction(sender.getName(), "Removing all players from the whitelist.", false);
            TFM_Util.playerMsg(sender, "Removed " + whitelisted.size() + " players from the whitelist.");
            whitelisted.clear();

            return true;
        }

        // none of the commands were executed
        return false;
    }
}
