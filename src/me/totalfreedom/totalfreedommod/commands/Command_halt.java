package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.admin.AdminList;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Halts a player", usage = "/<command> <<partialname> | all | purge | list>")
public class Command_halt extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("all"))
        {
            FUtil.adminAction(sender.getName(), "Halting all non-superadmins.", true);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!plugin.al.isAdmin(player))
                {
                    plugin.pl.getPlayer(player).setHalted(true);
                    counter++;
                }
            }
            playerMsg("Halted " + counter + " players.");
            return true;
        }

        if (args[0].equalsIgnoreCase("purge"))
        {
            FUtil.adminAction(sender.getName(), "Unhalting all players.", true);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                FPlayer playerdata = plugin.pl.getPlayer(player);
                if (plugin.pl.getPlayer(player).isHalted())
                {
                    playerdata.setHalted(false);
                    counter++;
                }
            }
            playerMsg("Unhalted " + counter + " players.");
            return true;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            FPlayer info;
            int count = 0;
            for (Player hp : server.getOnlinePlayers())
            {
                info = plugin.pl.getPlayer(hp);
                if (info.isHalted())
                {
                    if (count == 0)
                    {
                        playerMsg("Halted players:");
                    }
                    playerMsg("- " + hp.getName());
                    count++;
                }
            }
            if (count == 0)
            {
                playerMsg("There are currently no halted players.");
            }
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        FPlayer playerdata = plugin.pl.getPlayer(player);
        if (!playerdata.isHalted())
        {
            FUtil.adminAction(sender.getName(), "Halting " + player.getName(), true);
            playerdata.setHalted(true);
            return true;
        }
        else
        {
            FUtil.adminAction(sender.getName(), "Unhalting " + player.getName(), true);
            playerdata.setHalted(false);
            return true;
        }
    }
}
