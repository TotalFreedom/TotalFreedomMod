package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.freeze.FreezeData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Freeze/Unfreeze a specified player, or all non-staff on the server.", usage = "/<command> [target | purge]", aliases = "fr")
public class Command_freeze extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            boolean gFreeze = !plugin.fm.isGlobalFreeze();
            plugin.fm.setGlobalFreeze(gFreeze);

            if (!gFreeze)
            {
                FUtil.staffAction(sender.getName(), "Disabling global player freeze", false);
                msg("Players are now free to move.");
                return true;
            }

            FUtil.staffAction(sender.getName(), "Enabling global player freeze", false);
            for (Player player : server.getOnlinePlayers())
            {
                if (!isStaff(player))
                {
                    player.sendTitle(ChatColor.RED + "You've been globally frozen.", ChatColor.YELLOW + "Please be patient and you will be unfrozen shortly.", 20, 100, 60);
                    msg(player, "You have been globally frozen due to an OP breaking the rules, please wait and you will be unfrozen soon.", ChatColor.RED);
                }
            }
            msg("Players are now frozen.");
            return true;
        }

        if (args[0].equals("purge"))
        {
            FUtil.staffAction(sender.getName(), "Unfreezing all players", false);
            for (Player player : server.getOnlinePlayers())
            {
                if (!isStaff(player))
                {
                    player.sendTitle(ChatColor.GREEN + "You've been unfrozen.", ChatColor.YELLOW + "You may now move again.", 20, 100, 60);
                }
            }
            plugin.fm.purge();
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
            return true;
        }

        final FreezeData fd = plugin.pl.getPlayer(player).getFreezeData();
        fd.setFrozen(!fd.isFrozen());

        msg(player.getName() + " has been " + (fd.isFrozen() ? "frozen" : "unfrozen") + ".");
        msg(player, "You have been " + (fd.isFrozen() ? "frozen" : "unfrozen") + ".", ChatColor.AQUA);
        return true;
    }
}