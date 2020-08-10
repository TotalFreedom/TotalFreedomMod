package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Check the status of the server, including opped players, staff, etc.", usage = "/<command>", aliases = "ss")
public class Command_serverstats extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        msg("-==" + ConfigEntry.SERVER_NAME.getString() + " server stats==-", ChatColor.GOLD);
        msg("Total opped players: " + server.getOperators().size(), ChatColor.RED);
        msg("Total staff: " + plugin.al.getAllAdmins().size() + " (" + plugin.al.getActiveAdmins().size() + " active)", ChatColor.BLUE);
        int bans = plugin.im.getIndefBans().size();
        int nameBans = plugin.im.getNameBanCount();
        int uuidBans = plugin.im.getUuidBanCount();
        int ipBans = plugin.im.getIpBanCount();
        msg("Total indefinite ban entries: " + bans + " (" + nameBans + " name bans, " + uuidBans + " UUID bans, and " + ipBans + " IP bans)", ChatColor.GREEN);
        return true;
    }
}