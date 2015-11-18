package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.freeze.FreezeData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Freeze players (toggles on and off).", usage = "/<command> [target | purge]")
public class Command_fr extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            boolean allFrozen = plugin.fm.isGlobalFreeze();
            plugin.fm.setGlobalFreeze(!allFrozen);

            if (!allFrozen)
            {
                FUtil.adminAction(sender.getName(), "Disabling global player freeze", true);
                playerMsg("Players are now free to move.");
                return true;
            }

            FUtil.adminAction(sender.getName(), "Disabling global player freeze", true);
            playerMsg("Players are now unfrozen.");

            for (Player player : server.getOnlinePlayers())
            {
                if (!isAdmin(player))
                {
                    playerMsg(player, "You have been frozen due to rulebreakers, you will be unfrozen soon.", ChatColor.RED);
                }
            }
            return true;
        }

        if (args[0].equals("purge"))
        {
            FUtil.adminAction(sender.getName(), "Unfreezing all players", false);
            plugin.fm.purge();
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            playerMsg(FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
            return true;
        }

        final FreezeData fd = plugin.pl.getPlayer(player).getFreezeData();
        fd.setFrozen(!fd.isFrozen());

        playerMsg(player.getName() + " has been " + (fd.isFrozen() ? "frozen" : "unfrozen") + ".");
        playerMsg(player, "You have been " + (fd.isFrozen() ? "frozen" : "unfrozen") + ".", ChatColor.AQUA);

        return true;
    }
}
