package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Commands.CommandPermissions.ADMIN_LEVEL;
import me.StevenLawson.TotalFreedomMod.Commands.CommandPermissions.SOURCE_TYPE_ALLOWED;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, block_host_console = true, ignore_permissions = false)
public class Command_permban extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            dumplist(sender);
        }
        else if (args[0].equalsIgnoreCase("reload"))
        {
            if (!senderIsConsole)
            {
                sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                return true;
            }
            playerMsg("Reloading permban list...", ChatColor.RED);
            TotalFreedomMod.loadPermbanConfig();
            dumplist(sender);
        }
        else
        {
            return false;
        }

        return true;
    }

    private void dumplist(CommandSender sender)
    {
        if (TotalFreedomMod.permbanned_players.isEmpty())
        {
            playerMsg(sender, "No permanently banned player names.");
        }
        else
        {
            playerMsg(sender, TotalFreedomMod.permbanned_players.size() + " permanently banned players:");
            playerMsg(sender, StringUtils.join(TotalFreedomMod.permbanned_players, ", "));
        }

        if (TotalFreedomMod.permbanned_ips.isEmpty())
        {
            playerMsg(sender, "No permanently banned IPs.");
        }
        else
        {
            playerMsg(sender, TotalFreedomMod.permbanned_ips.size() + " permanently banned IPs:");
            playerMsg(sender, StringUtils.join(TotalFreedomMod.permbanned_ips, ", "));
        }
    }
}
