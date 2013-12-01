package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, block_host_console = true)
@CommandParameters(description = "Manage permanently banned players and IPs.", usage = "/<command> <list | reload>")
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
        if (TotalFreedomMod.permbannedPlayers.isEmpty())
        {
            playerMsg(sender, "No permanently banned player names.");
        }
        else
        {
            playerMsg(sender, TotalFreedomMod.permbannedPlayers.size() + " permanently banned players:");
            playerMsg(sender, StringUtils.join(TotalFreedomMod.permbannedPlayers, ", "));
        }

        if (TotalFreedomMod.permbannedIps.isEmpty())
        {
            playerMsg(sender, "No permanently banned IPs.");
        }
        else
        {
            playerMsg(sender, TotalFreedomMod.permbannedIps.size() + " permanently banned IPs:");
            playerMsg(sender, StringUtils.join(TotalFreedomMod.permbannedIps, ", "));
        }
    }
}
